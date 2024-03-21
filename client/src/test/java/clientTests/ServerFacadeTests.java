package clientTests;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static String serverURL;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverURL = "http://localhost:" + port;
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    // make a user and make sure the username returns right
    @Test
    public void registerValidUserTest()
    {
        ServerFacade serverFacade = new ServerFacade(serverURL);
        UserData user = new UserData("username", "password", "email");
        AuthData authData;
        try
        {
            serverFacade.clear();
            authData = serverFacade.registerUser(user);
            Assertions.assertEquals(authData.username(), user.username());
        }
        catch (Exception e)
        {
        Assertions.assertTrue(false);
        }
    }

    // make a user, and then watch it fail when you try to run that user again
    @Test
    public void registerInvalidDuplicateUserTest()
    {
        ServerFacade serverFacade = new ServerFacade(serverURL);
        UserData user = new UserData("username", "password", "email");
        AuthData authData;
        try
        {
            serverFacade.clear();
            authData = serverFacade.registerUser(user);
        }
        catch (Exception e)
        {
            Assertions.assertTrue(false);
        }
        Assertions.assertThrows(Exception.class, () -> serverFacade.registerUser(user));
    }

    // make sure clearing throws no exceptions
    @Test
    public void clearTest()
    {
        ServerFacade serverFacade = new ServerFacade(serverURL);
        Assertions.assertDoesNotThrow(serverFacade::clear);
    }

    @Test
    public void loginValidUserTest()
    {
        ServerFacade serverFacade = new ServerFacade(serverURL);
        UserData user = new UserData("username", "password", "email");
        AuthData authData;
        try
        {
            serverFacade.clear();
            serverFacade.registerUser(user);

            // after you've made the user, make sure you can log them in
            authData = serverFacade.loginUser(user);
            Assertions.assertEquals(authData.username(), user.username());
        }
        catch (Exception e)
        {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void loginBadPasswordTest()
    {
        ServerFacade serverFacade = new ServerFacade(serverURL);
        UserData user = new UserData("username", "password", "email");
        AuthData authData;
        try
        {
            serverFacade.clear();
            authData = serverFacade.registerUser(user);
        }
        catch (Exception e)
        {
            Assertions.assertTrue(false);
        }
        Assertions.assertThrows(Exception.class,
                () -> serverFacade.loginUser(new UserData("username", "wrong pasword", "email")));
    }


    @Test
    public void logoutValidUserTest()
    {
        ServerFacade serverFacade = new ServerFacade(serverURL);
        UserData user = new UserData("username", "password", "email");
        AuthData authData;
        try
        {
            serverFacade.clear();
            authData = serverFacade.registerUser(user);
            Assertions.assertDoesNotThrow(() -> serverFacade.logoutUser(authData.authToken()));
        }
        catch (Exception e)
        {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void logoutInvalidUserTest()
    {
        ServerFacade serverFacade = new ServerFacade(serverURL);
        UserData user = new UserData("username", "password", "email");
        AuthData authData;
        try
        {
            serverFacade.clear();
            Assertions.assertThrows(Exception.class, () -> serverFacade.logoutUser("this is not a real authtoken"));
        }
        catch (Exception e)
        {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void createValidGameTest()
    {
        ServerFacade serverFacade = new ServerFacade(serverURL);
        UserData user = new UserData("username", "password", "email");
        String gameName = "jouer aux echecs";
        AuthData authData;
        try
        {
            serverFacade.clear();
            authData = serverFacade.registerUser(user);
            Assertions.assertDoesNotThrow(() -> serverFacade.createGame(gameName, authData.authToken()));
        }
        catch (Exception e)
        {
            Assertions.assertTrue(false);
        }
    }

    @Test
    public void createInvalidGameTest()
    {
        ServerFacade serverFacade = new ServerFacade(serverURL);
        UserData user = new UserData("username", "password", "email");

        // puts in null as the name. That's surely bound to break something...
        String gameName = null;
        AuthData authData;
        try
        {
            serverFacade.clear();
            authData = serverFacade.registerUser(user);
            Assertions.assertThrows(Exception.class, () -> serverFacade.createGame(gameName, authData.authToken()));
        }
        catch (Exception e)
        {
            Assertions.assertTrue(false);
        }
    }


}