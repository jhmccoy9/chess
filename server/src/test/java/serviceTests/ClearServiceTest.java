package serviceTests;

import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class ClearServiceTest {
    static final MemoryDataAccess dataAccess = new MemoryDataAccess();
    static final GameService service = new GameService(dataAccess);
    @Test
    void clearEverythingTest() throws DataAccessException {
        // start by making a whole bunch of stuff in the database
        // make a new user
        String username = "test_username1";
        String password = "password1231";
        String email = "noreply1@test.com";
        UserService userService = new UserService(dataAccess);
        UserData user = new UserData(username, password, email);
        AuthData authData;
        try
        {
            authData = userService.register(user);
            assertEquals(dataAccess.getUser(username).username(), user.username());
            assertEquals(dataAccess.getUser(username).email(), user.email());
            assertTrue(dataAccess.sessionExists(authData.authToken()));
            // make the game
            GameService gameService = new GameService(dataAccess);
            GameData gameData;
            gameData = gameService.createGame(authData.authToken(), "name game2.0");
            gameData = gameService.createGame(authData.authToken(), "game 2");
            gameData = gameService.createGame(authData.authToken(), "vive le quebec");
            assertEquals(3, gameService.listGames(authData.authToken()).size());
        }
        catch (DataAccessException e)
        {
            // if it gets here, you have a problem
            throw e;
        }

        // make sure the size of everything is zero after you clear it
        dataAccess.clear();
        assertEquals(0, dataAccess.listGames().size());
        assertNull(dataAccess.getUser("test_username1"));
        assertFalse(dataAccess.sessionExists(authData.authToken()));
    }
}