package ui;


import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import server.ServerFacade;

import java.util.*;

public class PostloginUI
{
    private final ServerFacade server;
    private final AuthData authData;

    // the first integer is the gameid from the user perspective, second is the gameid from the db perspective
    private Map<Integer, Integer> gameIDs = null;

    public PostloginUI(ServerFacade server, AuthData authData)
    {
        this.server = server; this.authData = authData;
    }


    public void run()
    {
        System.out.println("You are logged in. Type 'help' to get started");

        StringBuilder currentState;

        Set<String> validOptions = new HashSet<String>();
        validOptions.add("help");
        validOptions.add("join");
        validOptions.add("observe");
        validOptions.add("list");
        validOptions.add("create");
        validOptions.add("logout");

        boolean logout = false;

        while (!logout)
        {
            String nextState = Utilities.getInput(validOptions);

            // transitions
            switch (nextState)
            {
                case "help":
                    System.out.println("list - list games");
                    System.out.println("create - make a new game");
                    System.out.println("join - join a preexisting game");
                    System.out.println("observe - watch a game");
                    System.out.println("logout - leave");
                    System.out.println("help - show this screen");
                    break;

                case "logout":
                    logout = true;
                    try
                    {
                        server.logoutUser(authData.authToken());
                    }
                    catch (ResponseException e)
                    {
                        System.out.println("Error logging out. Returning to prelogin");
                    }
                    System.out.println("You are now logged out");
                    break;

                case "create":
                    this.createGame();
                    break;

                case "list":
                    this.listGames();
                    break;

                case "join":
                    break;

                case "observe":
                    break;

                default:
                    break;
            }
        }

    }

    private void createGame()
    {
        System.out.println("Enter the name of your new game");
        String gameName = Utilities.getInput(null);
        GameData gameData;
        try
        {
            gameData = server.createGame(gameName, this.authData.authToken());
        }
        catch (ResponseException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Error creating game. Returning to home.");
            return;
        }

        System.out.printf("Game created. Name: %s\n", gameData.gameName());
    }

    private void listGames()
    {
        Collection<GameData> games;
        try
        {
            games = server.listGames(this.authData.authToken());
        }
        catch (ResponseException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Error listing games. Returning to home.");
            return;
        }

        Map<Integer, Integer> tempIDs = new HashMap<>();

        System.out.println("Available games:");
        int i = 1;
        for (GameData game : games)
        {
            String whiteUsername = game.whiteUsername() == null ? "(None)" : game.whiteUsername();
            String blackUsername = game.blackUsername() == null ? "(None)" : game.blackUsername();
            System.out.printf("%d Name: %s, White: %s, Black: %s\n", i, game.gameName(), whiteUsername, blackUsername);

            // update the IDs as you go. first id is the id for the client, second id is the id for the server
            tempIDs.put(i, game.gameID());

            i++;
        }

        // update the global variable once you're done
        this.gameIDs = tempIDs;
    }

}
