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
}
