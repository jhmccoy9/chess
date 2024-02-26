package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.*;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData new_user) throws DataAccessException
    {
        String username = new_user.username();
        String password = new_user.password();
        String email = new_user.email();

        // make sure the username, password, and email are all valid
        if (username == null || password == null || email == null)
        {
            throw new DataAccessException("Error: bad request");
        }
        else if (username.isEmpty() || password.isEmpty() || email.isEmpty())
        {
            throw new DataAccessException("Error: bad request");
        }

        UserData user = dataAccess.getUser(username);
        if (user == null)
        {
            // good news: make the new user
            dataAccess.createUser(username, password, email);

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

        UserData preexisting_user = dataAccess.getUser(username);
        // bad news: user doesn't exist
        if (preexisting_user == null)
        {
            throw new DataAccessException("Error: unauthorized");
        }
        // ideal: they're the same user
        else if (preexisting_user.password().equals(user.password()))
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
}
