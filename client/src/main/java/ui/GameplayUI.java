package ui;


import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import server.ServerFacade;

import java.util.*;

public class GameplayUI
{
    private final ServerFacade server;
    private final AuthData authData;

    private int gameID;



    public GameplayUI(ServerFacade server, AuthData authData, int serverGameID)
    {
        this.server = server;
        this.authData = authData;
        this.gameID = serverGameID;
    }


    public void run()
    {
        System.out.println("Gameplay UI Under Construction");

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

        ChessBoard board = game.getBoard();
        Utilities.printChessBoard(board);
        return;
    }
}
