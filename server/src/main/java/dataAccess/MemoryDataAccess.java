package dataAccess;
import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashSet;
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
        this.users.add(new UserData(username, password, email));
    }

    public AuthData createAuth(String username) {
        // make a new auth token, add it to the list and return it.
        AuthData toAdd = new AuthData(UUID.randomUUID().toString(), username);
        this.authData.add(toAdd);
        return toAdd;
    }

    public boolean sessionExists(String authToken)
    {
        // see if a session exists with the given auth token
        for (AuthData data : this.authData)
        {
            if (data.authToken().equals(authToken))
            {
                return true;
            }
        }
        return false;
    }

    public void deleteSession(String authToken)
    {
        AuthData session = null;
        // find the session in question
        for (AuthData data : this.authData)
        {
            if (data.authToken().equals(authToken))
            {
                session = data;
            }
        }
        if (session != null)
            this.authData.remove(session);
    }

    public GameData createGame(String gameName, ChessGame game)
    {
        // get the id. For us, it will be one more than the max of the game ids
        int gameId = 1;
        for (GameData i : this.games)
        {
            if (i.gameID() >= gameId)
                gameId = i.gameID() + 1;
        }
        // just autofill it with blank player names as null for the time being...
        if (game == null)
            game = new ChessGame();
        GameData newGame = new GameData(gameId, null, null, gameName, game);
        this.games.add(newGame);
        return newGame;
    }

    public boolean gameExists(String gameName)
    {
        // go through all the games. if you find one by that name, return true;
        for (GameData game : this.games)
        {
            if (game.gameName().equals(gameName))
            {
                return true;
            }
        }
        return false;
    }

    public boolean gameExists(int gameID)
    {
        // go through all the games. if you find one by that name, return true;
        for (GameData game : this.games)
        {
            if (game.gameID() == gameID)
            {
                return true;
            }
        }
        return false;
    }

    public Collection<GameData> listGames()
    {
        return this.games;
    }

    public GameData getGame(int gameId)
    {
        for (GameData game : this.listGames())
        {
            if (game.gameID() == gameId)
                return game;
        }
        return null;
    }

    public void addPlayerToGame(int gameId, String authToken, boolean isWhite)
    {
        // get the data you need to start the process
        GameData game = this.getGame(gameId);
        String username = this.getUsername(authToken);

        // make the new game
        GameData newGame;
        if (isWhite)
            newGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
        else
            newGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());

        // pop the old game out and add the new one in
        this.games.remove(game);
        this.games.add(newGame);
    }

    public String getUsername(String authToken)
    {
        // iterate over all the sessions
        for (AuthData session : this.authData)
        {
            // if you find one that has that username, return it
            if (session.authToken().equals(authToken))
                return session.username();

        }
        // if nothing shows up, return null
        return null;
    }
}
