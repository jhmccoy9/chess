package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import server.ServerFacade;
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


    public GameplayUI(ServerFacade server, AuthData authData, int serverGameID, String serverURL)
    {
        this.server = server;
        this.authData = authData;
        this.gameID = serverGameID;
        this.serverUrl = serverURL;
        this.notificationHandler = new NotificationHandler(); // The gameplay UI itself will probably be the notification handler
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
                break;
            }
        }

        if (game == null)
        {
            System.out.println("Error: no game found");
            return;
        }

        ChessBoard board = game.getBoard();
        this.redrawBoard(board);

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
                    // MUST FIX STUFF BELOW
//                    try
//                    {
//                        server.logoutUser(authData.authToken());
//                    }
//                    catch (ResponseException e)
//                    {
//                        System.out.println("Error logging out. Returning to prelogin");
//                    }
                    System.out.println("You have left the game");
                    break;

                case "redraw":
                    this.redrawBoard(board);
                    break;

                case "move":
//                    this.listGames();
                    break;

                case "resign":
//                    this.joinGame();
                    break;

                case "highlight":
                    this.highlightMoves(game, board);
                    break;

                default:
                    break;
                }



            }
        return;
    }
    private void redrawBoard(ChessBoard board) { Utilities.printChessBoard(board); }

    private void highlightMoves(ChessGame game, ChessBoard board)
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
        System.out.println("Enter the position of the piece whose moves you want to know");
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
        ChessPosition position = new ChessPosition(Integer.parseInt(row), intCol);

        // get the possible moves for that place
        Collection<ChessMove> possibleMoves = game.validMoves(position);
        Collection<ChessPosition> possiblePositions = new HashSet<>();
        if (possibleMoves != null)
            for (ChessMove move : possibleMoves) { possiblePositions.add(move.getEndPosition()); }

        // redraw the board, but with possible moves highlighted
        Utilities.printChessBoard(board, possiblePositions);
        return;
    }
}