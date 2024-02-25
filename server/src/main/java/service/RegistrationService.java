package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.*;

public class RegistrationService {

    private final DataAccess dataAccess;

    public RegistrationService(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData new_user) throws DataAccessException
    {
        String username = new_user.username();
        String password = new_user.password();
        String email = new_user.email();

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
}
