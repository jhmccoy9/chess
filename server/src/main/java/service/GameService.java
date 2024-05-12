package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;
import java.util.Collection;

public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException
    {
        // the game name cannot be null
        if (gameName == null)
            throw new DataAccessException("Error: bad request");
        // make sure the auth token is valid
        if (dataAccess.sessionExists(authToken))
        {
            // make a game with the name it doesn't exist
            if (!dataAccess.gameExists(gameName))
                return dataAccess.createGame(gameName);
            else
                throw new DataAccessException("Error: bad request");
        }
        else
        {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException
    {
        // make sure the auth token is valid
        if (dataAccess.sessionExists(authToken))
            return dataAccess.listGames();
        else
            throw new DataAccessException("Error: unauthorized");
    }

    public void joinGame(String authToken, String color, int gameId) throws DataAccessException
    {
        // make sure the authtoken is valid and the game exists
        if (!dataAccess.sessionExists(authToken))
        {
            throw new DataAccessException("Error: unauthorized");
        }
        // make sure the game id exists
        else if (!dataAccess.gameExists(gameId))
        {
            throw new DataAccessException("Error: bad request");
        }
        // if the game and auth token are valid, make sure that color is available
        {
            // if the color is blank, they just want to be an observer. So it's fine or whatever
            if (color == null) {
            }
            // otherwise, add that player as playing the correct color
            else if (color.equals("WHITE"))
            {
                boolean isWhite = true;
                // make sure that nobody is already playing as that color
                GameData game = dataAccess.getGame(gameId);
                if (game.whiteUsername() != null)
                    throw new DataAccessException("Error: already taken");
                // assuming the coast is clear, add that player to the game
                dataAccess.addPlayerToGame(gameId, authToken, isWhite);
            }
            else if (color.equals("BLACK"))
            {
                boolean isWhite = false;
                // make sure that nobody is already playing as that color
                GameData game = dataAccess.getGame(gameId);
                if (game.blackUsername() != null)
                    throw new DataAccessException("Error: already taken");
                // assuming the coast is clear, add that player to the game
                dataAccess.addPlayerToGame(gameId, authToken, isWhite);
            }
            // bad color
            else
            {
                throw new DataAccessException("Error: bad request");
            }
        }
    }
}
