package serviceTests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;


class UserServiceTest {
    static final MemoryDataAccess dataAccess = new MemoryDataAccess();
    static final UserService service = new UserService(dataAccess);

    @Test
    void registerSuccess() {
        // register a user
        try
        {
            service.register(new UserData("bob", "password", "emailaddress@gmail.com"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        // make sure that user exists
        assertNotNull(dataAccess.getUser("bob"));
        dataAccess.clear();
    }

    @Test
    void loginSuccess() {
        // register a user
        try
        {
            service.register(new UserData("bob", "password", "emailaddress@gmail.com"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // try logging in that user
        assertDoesNotThrow(() -> {
            service.login(new UserData("bob", "password", "emailaddress@gmail.com"));
        });
        dataAccess.clear();
    }

    @Test
    void logoutSuccess() {
        // register a user
        AuthData authData;
        try
        {
            service.register(new UserData("bob", "password", "emailaddress@gmail.com"));
            authData = service.login(new UserData("bob", "password", "emailaddress@gmail.com"));
            assertDoesNotThrow(() -> {service.logout(authData.authToken());});
            dataAccess.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void registerFail() {
        // register a user
        try
        {
            service.register(new UserData("bob", "password", "emailaddress@gmail.com"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        // make sure that you can't add that user again
        assertThrows(DataAccessException.class, () -> {
            service.register(new UserData("bob", "password", "emailaddress@gmail.com"));
        });
        dataAccess.clear();
    }

    @Test
    void loginFail() {
        // register a user
        try
        {
            service.register(new UserData("bob", "password", "emailaddress@gmail.com"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        // try logging in that user
        assertThrows(DataAccessException.class, () -> {
            service.login(new UserData("bob", "wrong password", "emailaddress@gmail.com"));
        });
        dataAccess.clear();
    }

    @Test
    void logoutFail() {
        // register a user
        AuthData authData;
        try
        {
            service.register(new UserData("bob", "password", "emailaddress@gmail.com"));
            authData = service.login(new UserData("bob", "password", "emailaddress@gmail.com"));
            assertThrows(DataAccessException.class, () -> {service.logout("not a real authtoken");});
            dataAccess.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}