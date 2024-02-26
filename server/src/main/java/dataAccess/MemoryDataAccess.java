package dataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess
{
    // you need a list of users
    HashSet<UserData> users;
    HashSet<AuthData> authData;
    HashSet<GameData> games;

    public MemoryDataAccess()
    {
        this.games = new HashSet<>();
        this.users = new HashSet<>();
        this.authData = new HashSet<>();
    }


    public void clear()
    {
        this.users.clear();
        this.authData.clear();
        this.games.clear();
        return;
    }

    public UserData getUser(String username) {
        // iterate over all the users
        for (UserData user : this.users)
        {
            // if you find one that has that username, return it
            if (user.username().equals(username))
                return user;

        }

        // if nothing shows up, return null
        return null;
    }

    public void createUser(String username, String password, String email)
    {
        UserData to_add = new UserData(username, password, email);
        this.users.add(to_add);
        return;
    }

    public AuthData createAuth(String username) {
        // make a new auth token, add it to the list and return it.
        AuthData to_add = new AuthData(UUID.randomUUID().toString(), username);
        this.authData.add(to_add);
        return to_add;
    }

    public boolean sessionExists(String auth_token)
    {
        boolean to_return = false;
        // see if a session exists with the given auth token
        for (AuthData data : this.authData)
        {
            if (data.authToken().equals(auth_token))
            {
                to_return = true;
            }
        }
        return to_return;
    }

    public void deleteSession(String auth_token)
    {
        AuthData session = null;
        // find the session in question
        for (AuthData data : this.authData)
        {
            if (data.authToken().equals(auth_token))
            {
                session = data;
            }
        }
        if (session != null)
            this.authData.remove(session);
        return;
    }
}
