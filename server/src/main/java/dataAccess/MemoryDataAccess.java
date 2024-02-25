package dataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
}
