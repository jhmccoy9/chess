package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.ClearService;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserver;
import webSocketMessages.userCommands.JoinPlayer;
import webSocketMessages.userCommands.MakeMove;
import webSocketMessages.userCommands.UserGameCommand;
//import webSocketMessages.*;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;


@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final DataAccess dataAccess;
    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;

    public WebSocketHandler(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
        this.clearService = new ClearService(dataAccess);
        this.userService = new UserService(dataAccess);
        this.gameService = new GameService(dataAccess);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        String username;
        switch (command.getCommandType())
        {
            case JOIN_PLAYER:
                JoinPlayer joinCommand = new Gson().fromJson(message, JoinPlayer.class);
                username = this.dataAccess.getUsername(joinCommand.getAuthToken());
                // make sure they're accessing a game that actually exists
                if (dataAccess.getGame(joinCommand.getGameID()) == null)
                {
                    Error error = new Error("This game doesn't exist you silly goose");
                    session.getRemote().sendString(new Gson().toJson(error));
                }
                // if they're trying to access an uninitialized player spot, send an error
                else if ((joinCommand.getColor().equals(ChessGame.TeamColor.WHITE) &&
                        dataAccess.getGame((joinCommand.getGameID())).whiteUsername() == null) ||
                (joinCommand.getColor().equals(ChessGame.TeamColor.BLACK) &&
                        dataAccess.getGame((joinCommand.getGameID())).blackUsername() == null))
                {
                    Error error = new Error("Nice try. Maybe go use the http endpoint you punk.");
                    session.getRemote().sendString(new Gson().toJson(error));
                }
                // if they're trying to log into a position that's already theirs, let them join
                else if ((joinCommand.getColor().equals(ChessGame.TeamColor.WHITE) &&
                    dataAccess.getGame((joinCommand.getGameID())).whiteUsername().equals(username)) ||
                    (joinCommand.getColor().equals(ChessGame.TeamColor.BLACK) &&
                    dataAccess.getGame((joinCommand.getGameID())).blackUsername().equals(username))
                )
                    playerJoin(joinCommand.getGameID(), username, session, joinCommand.getColor());
                // otherwise, flag an error
                else
                {
                    Error error = new Error("You are not allowed to join this game");
                    session.getRemote().sendString(new Gson().toJson(error));
                }
                break;
            case JOIN_OBSERVER:
                JoinObserver observeCommand = new Gson().fromJson(message, JoinObserver.class);
                username = this.dataAccess.getUsername(observeCommand.getAuthToken());
                // make sure they're accessing a game that actually exists
                if (dataAccess.getGame(observeCommand.getGameID()) == null)
                {
                    Error error = new Error("This game doesn't exist you silly goose");
                    session.getRemote().sendString(new Gson().toJson(error));
                }
                // if they're trying to observe and the authtoken is valid
                else if (dataAccess.sessionExists(observeCommand.getAuthToken()))
                    observerJoin(observeCommand.getGameID(), username, session);
                else
                {
                    Error error = new Error("You are not allowed to join this game");
                    session.getRemote().sendString(new Gson().toJson(error));
                }
                break;

            case MAKE_MOVE:
                MakeMove makeMove = new Gson().fromJson(message, MakeMove.class);
                move(session, makeMove.getGameID(), makeMove.getMove(), makeMove.getAuthToken());
                break;

            default:
                System.out.println("Error: unknown action");
        }
    }

    private void move(Session session, int gameID, ChessMove move, String authToken) throws IOException {
        // get the game
        ChessGame game = dataAccess.getGame(gameID).game();
        String username = this.dataAccess.getUsername(authToken);

        // if it's game over, send back an error message
        if (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInCheckmate(ChessGame.TeamColor.WHITE) ||
            game.isInStalemate(ChessGame.TeamColor.BLACK) || game.isInCheckmate(ChessGame.TeamColor.BLACK))
        {
            webSocketMessages.serverMessages.Error error = new Error("Game is over. You can't move");
            String temp = new Gson().toJson(error);
            if (session.isOpen())
                session.getRemote().sendString(new Gson().toJson(error));
            return;
        }

        // make sure it's that the move is coming from the person who has the right to move it
        ChessGame.TeamColor pieceColor = game.getBoard().getPiece(move.getStartPosition()).getTeamColor();
        String ownerOfPiece;
        if (pieceColor.equals(ChessGame.TeamColor.WHITE))
            ownerOfPiece = dataAccess.getGame(gameID).whiteUsername();
        else
            ownerOfPiece = dataAccess.getGame(gameID).blackUsername();
        if (!ownerOfPiece.equals(username))
        {
            Error error = new Error("You aren't allowed to move that piece");
            session.getRemote().sendString(new Gson().toJson(error));
            return;
        }

        // make the move
        try
        {
            game.makeMove(move);
            dataAccess.createGame(dataAccess.getGame(gameID).gameName(), game);
            ServerMessage toSend = new Notification(String.format("%s moved their piece at %s to %s",
                                                    username,
                                                    move.getStartPosition().toString(),
                                                    move.getEndPosition().toString()));
            ArrayList<String> exclusions = new ArrayList<>();
            exclusions.add(username);
            connections.broadcast(gameID, toSend, exclusions);
            toSend = new LoadGame(game);
            connections.broadcast(gameID, toSend, new ArrayList<>());
        }
        // if it was a bad move, send an error
        catch (InvalidMoveException e)
        {
            // get the connection and send an error message
            Error error = new Error(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void observerJoin(int gameID, String username, Session session) throws IOException {
        Connection.Role role = Connection.Role.OBSERVER;

        // send out the join message to everyone
        var message = String.format("%s is observing the game", username);
        var notification = new Notification(message);
        connections.broadcast(gameID, notification, new ArrayList<>());

        // add the connection
        connections.add(gameID, username, session, role);
    }

    private void playerJoin(int gameID, String visitorName, Session session, ChessGame.TeamColor color) throws IOException {
        // covert the color to a role
        Connection.Role role = color.equals(ChessGame.TeamColor.WHITE) ? Connection.Role.PLAYER_WHITE : Connection.Role.PLAYER_BLACK;

        // send out the join message to everyone
        String colorString = color.equals(ChessGame.TeamColor.WHITE) ? "white" : "black";
        var message = String.format("%s has joined the game as the %s player", visitorName, colorString);
        var notification = new Notification(message);
        connections.broadcast(gameID, notification, new ArrayList<>());

        // add the connection
        connections.add(gameID, visitorName, session, role);
    }

    private void loadGame()
    {

    }

//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }

//    public void makeNoise(String petName, String sound) throws DataAccessException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new DataAccessException(ex.getMessage());
//        }
//    }
}