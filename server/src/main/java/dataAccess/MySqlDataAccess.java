package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import com.google.gson.Gson;

import javax.xml.crypto.Data;
import java.sql.*;

public class MySqlDataAccess implements DataAccess{

    private final String[] setupStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                `username` varchar(256) NOT NULL,
                `password` varchar(256) NOT NULL,
                `email` varchar(256) NOT NULL,
                PRIMARY KEY (`username`),
                INDEX(email)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """,

            """
            CREATE TABLE IF NOT EXISTS  authData (
            `username` varchar(256) NOT NULL,
            `authToken` varchar(256) NOT NULL,
            PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS  games (
            `gameID` int NOT NULL,
            `whiteUsername` varchar(256) DEFAULT NULL,
            `blackUsername` varchar(256) DEFAULT NULL,
            `gameName` varchar(256) NOT NULL,
            `game` TEXT NOT NULL,
            PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    public MySqlDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection())
        {
            for (var statement : setupStatements)
            {
                try (var preparedStatement = conn.prepareStatement(statement))
                {
                    preparedStatement.executeUpdate();
                }
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException("Unable to configure database");
            //throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
    public void clear() throws DataAccessException {

    }

    public UserData getUser(String username) {
        return null;
    }

    public void createUser(String username, String password, String email) {

    }

    public AuthData createAuth(String username) {
        return null;
    }

    public boolean sessionExists(String authToken) {
        return false;
    }

    public void deleteSession(String authToken) {

    }

    public GameData createGame(String gameName) {
        return null;
    }

    public boolean gameExists(String gameName) {
        return false;
    }

    public boolean gameExists(int gameID) {
        return false;
    }

    public Collection<GameData> listGames() {
        return null;
    }

    public GameData getGame(int gameId) {
        return null;
    }

    public void addPlayerToGame(int gameId, String authToken, boolean isWhite) {

    }

    public String getUsername(String authToken) {
        return null;
    }
}
