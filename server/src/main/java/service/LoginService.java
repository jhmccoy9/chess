package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class LoginService {

    private final DataAccess dataAccess;

    public LoginService(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
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
        else if (preexisting_user.username().equals(user.username()) &&
                 preexisting_user.password().equals(user.password()))
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
