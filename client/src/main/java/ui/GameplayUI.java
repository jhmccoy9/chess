package ui;

import chess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import server.ServerFacade;
import webSocketMessages.userCommands.MakeMove;
import webSocketMessages.userCommands.Resign;
import websocket.*;

import java.util.*;

public class GameplayUI
{
    private final ServerFacade server;
    private final AuthData authData;
    private int gameID;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    private String username;


    public GameplayUI(ServerFacade server, AuthData authData, int serverGameID, String serverURL, String username) throws ResponseException
    {
        this.server = server;
        this.authData = authData;
        this.gameID = serverGameID;
        this.serverUrl = serverURL;
        this.username = username;
        this.notificationHandler = new NotificationHandler(); // The gameplay UI itself will probably be the notification handler
        this.ws = new WebSocketFacade(this.serverUrl, this.notificationHandler);
    }


    public void run()
    {
        System.out.println("You have entered a game. Type 'help' to get started");

        // get the game
        Collection<GameData> gameDataCollection = null;
        try
        {
            gameDataCollection = server.listGames(this.authData.authToken());
        }
        catch (ResponseException e)
        {
            System.out.println("Error joining game");
            return;
        }

        ChessGame game = null;
        for (GameData possibleGame : gameDataCollection)
        {
            if (possibleGame.gameID() == this.gameID)
            {
                game = possibleGame.game();

                // get the right color
                ChessGame.TeamColor color = null;
                if (possibleGame.whiteUsername() != null &&
                    possibleGame.whiteUsername().equals(this.username))
                    color = ChessGame.TeamColor.WHITE;
                else if (possibleGame.blackUsername() != null &&
                         possibleGame.blackUsername().equals(this.username))
                    color = ChessGame.TeamColor.BLACK;
                else
                {
//                    System.out.println("Error: invalid color problem");
//                    return;
                }

                try
                {
                    if (color != null)
                        ws.enterGame(this.authData.authToken(), this.gameID, color);
                    else
                        ws.observeGame(authData.authToken(), gameID);
                }
                catch (ResponseException e)
                {
                    System.out.println("Error: unable to join game via websocket");
                    System.out.printf("Status code: %d", e.statusCode());
                    return;
                }

                break;
            }
        }

        if (game == null)
        {
            System.out.println("Error: no game found");
            return;
        }

        //ChessBoard board = game.getBoard();
        this.redrawBoard(this.getGame().getBoard());

        Set<String> validOptions = new HashSet<>();
        validOptions.add("help");
        validOptions.add("redraw");
        validOptions.add("leave");
        validOptions.add("move");
        validOptions.add("resign");
        validOptions.add("highlight");

        boolean leave = false;

        while (!leave)
        {
            String nextState = Utilities.getInput(validOptions);

            // transitions
            switch (nextState)
            {
                case "help":
                    System.out.println("redraw - redraw the board");
                    System.out.println("highlight - show all possible moves");
                    System.out.println("resign - forfeit the game");
                    System.out.println("move - make a move");
                    System.out.println("leave - leave the game");
                    System.out.println("help - show this screen");
                    break;

                case "leave":
                    leave = true;
                    try
                    {
                        ws.leaveGame(this.authData.authToken(), this.gameID);
                        System.out.println("You have left the game");
                    }
                    catch (ResponseException e)
                    {
                        System.out.printf("Unable to leave game due to exception %s\n", e.getMessage());
                    }

                    break;

                case "redraw":
                    this.redrawBoard(this.getGame().getBoard());
                    break;

                case "move":
                    try
                    {
                        this.makeMove();
                    }
                    catch (ResponseException e)
                    {
                        System.out.printf("Unable to make move due to exception %s\n", e.getMessage());
                    }
                    break;

                case "resign":
                    // go back to the main menu if they don't want to actually resign
                    System.out.println("Are you sure? y/n");
                    Collection<String> options = new HashSet<>();
                    options.add("y");
                    options.add("n");
                    String response = Utilities.getInput(options);
                    if (response.equals("n"))
                        break;

                    Resign resign = new Resign(authData.authToken(), this.gameID);
                    try
                    {
                        ws.resign(resign);
                        System.out.println("You gave up successfully :(");
                    }
                    catch (ResponseException e)
                    {
                        System.out.println("Unable to resign");
                        System.out.println(e.getMessage());
                    }
                    break;

                case "highlight":
                    this.highlightMoves(game, this.getGame().getBoard(), this.getPosition());
                    break;

                default:
                    break;
                }



            }
    }

    private void makeMove() throws ResponseException {
        System.out.println("Enter the location of the piece you wish to move");
        ChessPosition start = this.getPosition();
        System.out.println("Enter the location of the place you'd like this piece to move");
        ChessPosition end = this.getPosition();
        ChessPiece.PieceType promotionPiece = null;

        ChessPiece piece = this.getGame().getBoard().getPiece(start);
        if (piece == null)
        {
            System.out.println("Invalid piece selection");
            return;
        }

        // see if it's a pawn and, if it is, see if you want to promote it
        if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN))
        {
            // see if it's at the very edge
            if (end.getRow() == 1 || end.getRow() == 8)
            {
                // if it is, ask for the promotion piece type
                HashSet<String> possiblePieces = new HashSet<>();
                possiblePieces.add("king");
                possiblePieces.add("queen");
                possiblePieces.add("knight");
                possiblePieces.add("bishop");
                possiblePieces.add("rook");

                System.out.println("Enter the type of piece you want to promote this pawn to");
                String stringPromotionPiece = Utilities.getInput(possiblePieces);
                switch (stringPromotionPiece)
                {
                    case "king":
                        promotionPiece = ChessPiece.PieceType.KING;
                        break;
                    case "queen":
                        promotionPiece = ChessPiece.PieceType.QUEEN;
                    case "knight":
                        promotionPiece = ChessPiece.PieceType.KNIGHT;
                    case "bishop":
                        promotionPiece = ChessPiece.PieceType.ROOK;
                    case "rook":
                        promotionPiece = ChessPiece.PieceType.ROOK;
                    case null, default:
                        // you would be stupid not to pick queen imho
                        promotionPiece = ChessPiece.PieceType.QUEEN;
                }
            }
        }
        ChessMove move = new ChessMove(start, end, promotionPiece);
        MakeMove makeMove = new MakeMove(this.authData.authToken(), this.gameID, move);
        ws.makeMove(makeMove);
    }

    private void redrawBoard(ChessBoard board) { Utilities.printChessBoard(board); }

    private void highlightMoves(ChessGame game, ChessBoard board, ChessPosition position)
    {

        // get the possible moves for that place
        Collection<ChessMove> possibleMoves = game.validMoves(position);
        Collection<ChessPosition> possiblePositions = new HashSet<>();
        if (possibleMoves != null)
            for (ChessMove move : possibleMoves) { possiblePositions.add(move.getEndPosition()); }

        // redraw the board, but with possible moves highlighted
        Utilities.printChessBoard(board, possiblePositions);
    }

    ChessPosition getPosition()
    {
        // possible options
        Collection<String> possibleRows = new ArrayList<>();
        possibleRows.add("1");
        possibleRows.add("2");
        possibleRows.add("3");
        possibleRows.add("4");
        possibleRows.add("5");
        possibleRows.add("6");
        possibleRows.add("7");
        possibleRows.add("8");

        Collection<String> possibleCols = new ArrayList<>();
        possibleCols.add("a");
        possibleCols.add("b");
        possibleCols.add("c");
        possibleCols.add("d");
        possibleCols.add("e");
        possibleCols.add("f");
        possibleCols.add("g");
        possibleCols.add("h");

        // ask for the piece whose moves it wants to know
        System.out.print("Row: ");
        String row = Utilities.getInput(possibleRows);
        System.out.print("Column: ");
        String col = Utilities.getInput(possibleCols);

        // convert the column letter to an integer
        int intCol = 1;
        switch (col)
        {
            case "a":
                intCol = 1;
                break;
            case "b":
                intCol = 2;
                break;
            case "c":
                intCol = 3;
                break;
            case "d":
                intCol = 4;
                break;
            case "e":
                intCol = 5;
                break;
            case "f":
                intCol = 6;
                break;
            case "g":
                intCol = 7;
                break;
            case "h":
                intCol = 8;
                break;
            default:
                System.out.println("Problem with the columns");
                break;
        }

        // convert those coordinates into a position
        return new ChessPosition(Integer.parseInt(row), intCol);
    }

    ChessGame getGame() {
        // get the game
        Collection<GameData> gameDataCollection = null;
        try {
            gameDataCollection = server.listGames(this.authData.authToken());
        } catch (ResponseException e) {
            System.out.println("Error joining game");
            return null;
        }

        for (GameData possibleGame : gameDataCollection) {
            if (possibleGame.gameID() == this.gameID) {
                return possibleGame.game();

            }
        }
        return null;
    }


}