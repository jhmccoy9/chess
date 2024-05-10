package ui;


import exception.ResponseException;
import model.AuthData;
import model.GameData;
import server.ServerFacade;

import java.util.*;

public class PostloginUI
{
    private final ServerFacade server;
    private final AuthData authData;
    private GameplayUI gameplayUI;
    // the first integer is the gameid from the user perspective, second is the gameid from the db perspective
    private Map<String, Integer> gameIDs = null;
    private final String serverURL;

    public PostloginUI(ServerFacade server, AuthData authData, String serverURL)
    {
        this.server = server;
        this.authData = authData;
        this.serverURL = serverURL;
    }


    public void run()
    {
        System.out.println("You are logged in. Type 'help' to get started");

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
                    this.joinGame();
                    break;

                case "observe":
                    this.observeGame();
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

        Map<String, Integer> tempIDs = new HashMap<>();

        System.out.println("Available games:");
        int i = 1;
        for (GameData game : games)
        {
            String whiteUsername = game.whiteUsername() == null ? "(None)" : game.whiteUsername();
            String blackUsername = game.blackUsername() == null ? "(None)" : game.blackUsername();
            System.out.printf("%d Name: %s, White: %s, Black: %s\n", i, game.gameName(), whiteUsername, blackUsername);

            // update the IDs as you go. first id is the id for the client, second id is the id for the server
            tempIDs.put(String.valueOf(i), game.gameID());

            i++;
        }

        // update the global variable once you're done
        this.gameIDs = tempIDs;
    }

    private void joinGame()
    {
        if (this.gameIDs == null)
        {
            System.out.println("Error: list the games first");
            return;
        }

        System.out.println("Enter the number of the game you want to join.");
        String clientGameID = Utilities.getInput(this.gameIDs.keySet());
        int serverGameID = this.gameIDs.get(clientGameID);

        System.out.println("Enter the color you want to be (w/b).");
        Set<String> colorOptions = new HashSet<>();
        colorOptions.add("b");
        colorOptions.add("w");
        String colorCode = Utilities.getInput(colorOptions);
        String color = colorCode.equals("w") ? "WHITE" : "BLACK";

        try
        {
            server.joinGame(serverGameID, color, this.authData.authToken());
            gameplayUI = new GameplayUI(this.server, this.authData, serverGameID, this.serverURL);
            gameplayUI.run();
        }
        catch (ResponseException e)
        {
            System.out.println("Error joining the game");
        }
        return;

    }

    private void observeGame()
    {
        if (this.gameIDs == null)
        {
            System.out.println("Error: list the games first");
            return;
        }

        System.out.println("Enter the number of the game you want to join.");
        String clientGameID = Utilities.getInput(this.gameIDs.keySet());
        int serverGameID = this.gameIDs.get(clientGameID);


        try
        {
            server.joinGame(serverGameID, null, this.authData.authToken());
            gameplayUI = new GameplayUI(this.server, this.authData, serverGameID, this.serverURL);
            gameplayUI.run();
        }
        catch (ResponseException e)
        {
            System.out.println("Error observing the game");
        }
        return;

    }

}
