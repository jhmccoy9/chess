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


}