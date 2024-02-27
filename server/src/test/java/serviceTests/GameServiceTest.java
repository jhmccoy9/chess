package serviceTests;

import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    static final MemoryDataAccess dataAccess = new MemoryDataAccess();
    static final GameService service = new GameService(dataAccess);

    @Test
    void createGame()
    {
        // make a new user
        String username = "test_username";
        String password = "password123";
        String email = "noreply@test.com";
        UserService userService = new UserService(dataAccess);
        UserData user = new UserData(username, password, email);
        AuthData authData = userService.register(user);
        assertEquals(dataAccess.getUser(username), user);
        assertTrue(dataAccess.sessionExists(authData.authToken()));
    }

    @Test
    void listGames() {
    }

    @Test
    void joinGame() {
    }
}