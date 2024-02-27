package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        AuthData authData;
        try
        {
            authData = userService.register(user);
            assertEquals(dataAccess.getUser(username), user);
            assertTrue(dataAccess.sessionExists(authData.authToken()));
        }
        catch (DataAccessException e)
        {
            return;
        }

        // make the game
        GameService gameService = new GameService(dataAccess);
        GameData gameData;
        try
        {
            gameData = gameService.createGame(authData.authToken(), "name game");
        }
        catch (DataAccessException e)
        {
            return;
        }
        // make sure it returns the right game
        assertEquals(dataAccess.getGame(gameData.gameID()), gameData);

        // try making a new game of the same name
        assertThrows(DataAccessException.class, () -> {
            gameService.createGame(authData.authToken(), "name game");
        });
        // make a new game with a bad auth token
        assertThrows(DataAccessException.class, () -> {
            gameService.createGame("", "new game");
        });
    }

    @Test
    void listGames() {
    }

    @Test
    void joinGame() {
    }
}