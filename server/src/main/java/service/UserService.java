package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData newUser) throws DataAccessException
    {
        String username = newUser.username();
        String password = newUser.password();
        String email = newUser.email();

        // make sure the username, password, and email are all valid
        if (username == null || password == null || email == null)
        {
            throw new DataAccessException("Error: bad request");
        }
        else if (username.isEmpty() || password.isEmpty() || email.isEmpty())
        {
            throw new DataAccessException("Error: bad request");
        }

        // hash the password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);

        UserData user = dataAccess.getUser(username);
        if (user == null)
        {
            // good news: make the new user
            dataAccess.createUser(username, hashedPassword, email);

            // make that authtoken
            AuthData authData = dataAccess.createAuth(username);
            return authData;
        }
        else
        {
            // username already taken :(
            throw new DataAccessException("Error: already taken");
        }
    }

    public AuthData login(UserData user) throws DataAccessException
    {
        String username = user.username();
        String password = user.password();

        // make sure the username, password, and email are all valid
        if (username == null || password == null)
        {
            throw new DataAccessException("Error: bad request");
        }
        else if (username.isEmpty() || password.isEmpty())
        {
            throw new DataAccessException("Error: bad request");
        }


        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UserData preexistingUser = dataAccess.getUser(username);
        // bad news: user doesn't exist
        if (preexistingUser == null)
        {
            throw new DataAccessException("Error: unauthorized");
        }
        // ideal: they're the same user with the same hashed password
        else if (encoder.matches(password, preexistingUser.password()))
        {
            // make them an authtoken and return the auth data
            AuthData authData = dataAccess.createAuth(username);
            return authData;
        }
        // bad login info
        else
        {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    public void logout(String authToken) throws DataAccessException
    {
        // see if the authtoken is valid
        if (dataAccess.sessionExists(authToken))
        {
            dataAccess.deleteSession(authToken);
        }
        else
        {
            // session doesn't exist...
            throw new DataAccessException("Error: unauthorized");
        }
    }
}
