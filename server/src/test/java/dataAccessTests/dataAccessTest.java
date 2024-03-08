package dataAccessTests;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MySqlDataAccess;
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
}

