package dataAccess;

import model.AuthData;
import model.UserData;

public interface DataAccess
{

    void clear() throws DataAccessException;

    UserData getUser(String username);

    void createUser(String username, String password, String email);

    AuthData createAuth(String username);

    boolean sessionExists(String auth_token);

    void deleteSession(String auth_token);
}
