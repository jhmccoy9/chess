package dataAccessTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
import model.AuthData;
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
    }

    @Test
    void createValidUserTest()
    {
        // just try putting in a user like normal
        String username = "user";
        String password = "passe";
        String email = "courriel";
        assertDoesNotThrow(() -> dataAccess.createUser(username, password, email));
    }

    @Test
    void createInvalidUserTest()
    {
        // just try putting in a user with a bad email
        String username = "user";
        String password = "passe";
        String email = null;
        assertThrows(Exception.class, () -> dataAccess.createUser(username, password, email));
    }

    @Test
    void createValidAuthTest()
    {
        // make sure it's making valid authtokens
        String username = "some guy i guess";
        AuthData authData = dataAccess.createAuth(username);
        assertNotNull(authData.authToken());
    }

    @Test
    void createInvalidAuthTest()
    {
        // make sure it crashes if you put in no username
        String username = null;
        assertThrows(Exception.class, () -> dataAccess.createAuth(username));
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
    }

    @Test
    void invalidSessionExistsTest()
    {
        // throw a random authtoken in there and make sure it doesn't work
        assertFalse(dataAccess.sessionExists("ceci n'est pas un symbole d'autentification"));
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
    }

    @Test
    void validGameCreationTest()
    {

    }

}

