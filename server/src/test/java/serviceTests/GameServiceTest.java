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

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    static final MemoryDataAccess dataAccess = new MemoryDataAccess();
    static final GameService service = new GameService(dataAccess);


    @Test
    void createGameSuccess()
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
            assertEquals(dataAccess.getUser(username).username(), user.username());
            assertEquals(dataAccess.getUser(username).email(), user.email());
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
        dataAccess.clear();
    }

    @Test
    void createGameFail()
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
            assertEquals(dataAccess.getUser(username).username(), user.username());
            assertEquals(dataAccess.getUser(username).email(), user.email());
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

        // try making a new game of the same name
        assertThrows(DataAccessException.class, () -> {
            gameService.createGame(authData.authToken(), "name game");
        });
        // make a new game with a bad auth token
        assertThrows(DataAccessException.class, () -> {
            gameService.createGame("", "new game");
        });
        dataAccess.clear();
    }

    @Test
    void listGamesNormal() throws DataAccessException {
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
            dataAccess.clear();
            return;
        }
        catch (DataAccessException e)
        {
            // if it gets here, you have a problem
            throw e;
        }
    }

    @Test
    void listGamesBadAuth()
    {
        // make a new user
        String username = "test_username";
        String password = "password123";
        String email = "noreply@test.com";
        UserService userService = new UserService(dataAccess);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        UserData user = new UserData(username, password, email);
        AuthData authData;
        GameService gameService;
        try
        {
            authData = userService.register(user);
            assertEquals(dataAccess.getUser(username).username(), user.username());
            assertEquals(dataAccess.getUser(username).email(), user.email());

            assertTrue(dataAccess.sessionExists(authData.authToken()));
            // make the game
            gameService = new GameService(dataAccess);
            GameData gameData;
            gameData = gameService.createGame(authData.authToken(), "name game");
            gameData = gameService.createGame(authData.authToken(), "game 2");
            assertThrows(DataAccessException.class, () -> {
                gameService.listGames("not a real token");
            });
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            // if it gets here, you have a problem
            assertEquals(1,0);
        }
    }

    @Test
    void joinGameSuccess()
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
            assertEquals(dataAccess.getUser(username).username(), user.username());
            assertEquals(dataAccess.getUser(username).email(), user.email());
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
            gameService.joinGame(authData.authToken(), "WHITE", gameData.gameID());
        }
        catch (DataAccessException e)
        {
            return;
        }

        // make sure it has the right player in there
        assertEquals(user.username(), dataAccess.getGame(gameData.gameID()).whiteUsername());
        dataAccess.clear();
    }


    @Test
    void joinGameBadColor()
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
            assertEquals(dataAccess.getUser(username).username(), user.username());
            assertEquals(dataAccess.getUser(username).email(), user.email());
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

            // make sure it throws if you do a bad color
            assertThrows(DataAccessException.class, () -> {
                gameService.joinGame(authData.authToken(), "NOT A COLOR HERE", gameData.gameID());            });
        }
        catch (DataAccessException e)
        {
            return;
        }
        dataAccess.clear();
    }
}