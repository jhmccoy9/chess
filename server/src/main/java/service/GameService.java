package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess)
    {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String authToken, String gameName) throws DataAccessException
    {
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
}
