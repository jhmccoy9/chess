package dataAccessTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class dataAccessTest {
    static final DataAccess dataAccess;
    static {
        try {
            dataAccess = new MySqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void clear()
    {
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void clearTest()
    {
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getValidUserTest()
    {   String username = "user";
        String password = "passe";
        String email = "courriel";
        // add a user to the database
        dataAccess.createUser(username, password, email);
        UserData user = dataAccess.getUser(username);
        assertEquals(user, new UserData(username, password, email));
    }

    @Test
    void getInvalidUserTest()
    {
        String username = "user";
        String password = "passe";
        String email = "courriel";
        // add a user to the database
        dataAccess.createUser(username, password, email);
        UserData user = dataAccess.getUser(username);
        assertNotEquals(user, new UserData(username, "bad password", email));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createValidUserTest()
    {
        // just try putting in a user like normal
        String username = "user";
        String password = "passe";
        String email = "courriel";
        assertDoesNotThrow(() -> dataAccess.createUser(username, password, email));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createInvalidUserTest()
    {
        // just try putting in a user with a bad email
        String username = "user";
        String password = "passe";
        String email = null;
        dataAccess.createUser(username, password, email);
        assertNull(dataAccess.getUser(username));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createValidAuthTest()
    {
        // make sure it's making valid authtokens
        String username = "some guy i guess";
        AuthData authData = dataAccess.createAuth(username);
        assertNotNull(authData.authToken());
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createInvalidAuthTest()
    {
        // make sure it crashes if you put in no username
        String username = null;
        assertThrows(Exception.class, () -> dataAccess.createAuth(username));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void validSessionExistsTest()
    {
        // make a user and a session
        String username = "user";
        String password = "passe";
        String email = "courriel";
        // make sure it exists after you run the code
        dataAccess.createUser(username, password, email);
        AuthData authData = dataAccess.createAuth(username);
        assertTrue(dataAccess.sessionExists(authData.authToken()));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void invalidSessionExistsTest()
    {
        // throw a random authtoken in there and make sure it doesn't work
        assertFalse(dataAccess.sessionExists("ceci n'est pas un symbole d'autentification"));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteValidSessionTest()
    {
        // make a valid session
        String username = "user";
        String password = "passe";
        String email = "courriel";
        // make sure it exists after you run the code
        dataAccess.createUser(username, password, email);
        AuthData authData = dataAccess.createAuth(username);

        // make sure it deletes just fine
        assertDoesNotThrow(() -> dataAccess.deleteSession(authData.authToken()));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void deleteInvalidSessionTest()
    {
        // make a valid session
        String username = "user";
        String password = "passe";
        String email = "courriel";
        // make sure it exists after you run the code
        dataAccess.createUser(username, password, email);
        AuthData authData = dataAccess.createAuth(username);

        // if you try to delete someone that doesn't exist, the existing person should still be there
        dataAccess.deleteSession("definitely super sus");
        assertTrue(dataAccess.sessionExists(authData.authToken()));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void validGameCreationTest()
    {
        // make a game
        String gameName = "march madness but it's chess for nerds";
        GameData gameData = dataAccess.createGame(gameName);

        // make sure it still exists
        assertTrue(dataAccess.gameExists(gameName));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void invalidGameCreationTest()
    {
        // make a game with a null name
        String gameName = null;
        assertNull(dataAccess.createGame(gameName));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void validGameExistsStringTest()
    {
        dataAccess.createGame("game");
        assertTrue(dataAccess.gameExists("game"));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void invalidGameDoesntExistsStringTest()
    {
        assertFalse(dataAccess.gameExists("game"));
    }

    @Test
    void validGameExistsIntTest()
    {
        GameData data = dataAccess.createGame("game");
        assertTrue(dataAccess.gameExists(data.gameID()));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void invalidGameDoesntExistsIntTest()
    {
        assertFalse(dataAccess.gameExists(237024352));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getGoodGameTest()
    {
        GameData data = dataAccess.createGame("game");
        assertEquals(data.gameID(), dataAccess.getGame(data.gameID()).gameID());
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getInvalidGameTest()
    {
        assertNull(dataAccess.getGame(237024352));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addInvalidPlayerTest()
    {
        // make sure it crashes if you put in no username
        assertThrows(Exception.class, () -> dataAccess.addPlayerToGame(0, null, true));
        try
        {
            dataAccess.clear();
        }
        catch (DataAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

}

