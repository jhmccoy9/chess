package ui;


import exception.ResponseException;
import model.AuthData;
import model.UserData;
import server.ServerFacade;
import ui.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PreloginUI
{
    private final ServerFacade server;

    private enum State
    {
        QUIT,
        WELCOME,
        REGISTER,
        LOGIN,
        HELP
    }


    public PreloginUI(ServerFacade server)
    {
        this.server = server;
    }


    public void run()
    {
        System.out.println("Welcome to Prelogin. Type 'help' to get started");

        Set<String> validOptions = new HashSet<String>();
        validOptions.add("help");
        validOptions.add("welcome");
        validOptions.add("register");
        validOptions.add("login");
        validOptions.add("quit");

        boolean endGame = false;

        while (!endGame)
        {
            String nextState = Utilities.getInput(validOptions);

            switch (nextState)
            {
                case "help":
                    System.out.println("register - make acct");
                    System.out.println("login - login and play if you already have an account");
                    System.out.println("quit - leave");
                    System.out.println("help - show this screen");
                    break;

                case "quit":
                    endGame = true;
                    break;

                case "register":
                    this.register();
                    break;

                case "login":
                    this.login();
                    break;

                default:
                    break;
            }
        }

    }

    private void register()
    {
        // get all the user's data
        System.out.println("Enter your username");
        String username = Utilities.getInput(null);
        System.out.println("Enter your password");
        String password = Utilities.getInput(null);
        System.out.println("Enter your email address");
        String email = Utilities.getInput(null);
        UserData userData = new UserData(username, password, email);
        AuthData authData;
        try
        {
            authData = server.registerUser(userData);
        }
        catch (ResponseException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Error registering user. Returning to home.");
            return;
        }

        System.out.println("Excellent. Logging you in...");
        PostloginUI postloginUI = new PostloginUI(server, authData);
        postloginUI.run();


    }


    private void login()
    {
        // get all the user's data
        System.out.println("Enter your username");
        String username = Utilities.getInput(null);
        System.out.println("Enter your password");
        String password = Utilities.getInput(null);
        UserData userData = new UserData(username, password, "");
        AuthData authData;
        try
        {
            authData = server.loginUser(userData);
        }
        catch (ResponseException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Error logging in user. Returning to home.");
            return;
        }

        System.out.println("Excellent. Logging you in...");
        PostloginUI postloginUI = new PostloginUI(server, authData);
        postloginUI.run();

    }

}
