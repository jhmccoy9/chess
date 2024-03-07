package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import com.google.gson.Gson;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.HashSet;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

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
            PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS  games (
            `gameID` int NOT NULL AUTO_INCREMENT,
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

    private int executeUpdate(String statement, Object... params) throws DataAccessException
    {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof ChessGame p) ps.setString(i + 1, new Gson().toJson(p));
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }

    }
    public void clear() throws DataAccessException {
        try {
            var statement = "DELETE from authData;";
            executeUpdate(statement);
            statement = "DELETE from users;";
            executeUpdate(statement);
            statement = "DELETE from games;";
            executeUpdate(statement);
        }
        catch (DataAccessException e)
        {
            throw e;
        }
    }

    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement))
            {
                ps.setString(1, username);
                try (var rs = ps.executeQuery())
                {
                    if (rs.next())
                    {
                        var password = rs.getString("password");
                        var email = rs.getString("email");
                        return new UserData(username, password, email);
                    }
                }
            }

        }
        catch (Exception e)
        {
            System.out.println("you need to figure out what to do with this");
            throw new RuntimeException();
        }
        return null;
    }

    public void createUser(String username, String password, String email) {
        var statement = "INSERT INTO users (username, password, email) VALUES (?,?,?)";
        try {
            executeUpdate(statement, username, password, email);
        }
        catch (Exception e)
        {
            System.out.println("you need to figure out what to do with this");
            throw new RuntimeException();

        }
    }

    public AuthData createAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        String statement = "INSERT INTO authdata (username, authToken) VALUES (?,?)";
        try
        {
            executeUpdate(statement, username, authToken);
        }
        catch (Exception e)
        {
            System.out.println("you need to figure out what to do with this");
            throw new RuntimeException();
        }
        return new AuthData(authToken, username);
    }

    public boolean sessionExists(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken FROM authData WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement))
            {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery())
                {
                    return rs.next();
                }
            }

        }
        catch (Exception e)
        {
            System.out.println("you need to figure out what to do with this");
            throw new RuntimeException();
        }
        //return false;
    }

    public void deleteSession(String authToken) {
        String statement = "DELETE FROM authdata WHERE authToken=?";
        try
        {
            executeUpdate(statement, authToken);
        }
        catch (Exception e)
        {
            System.out.println("you need to figure out what to do with this");
            throw new RuntimeException();
        }
        return;
    }

    public GameData createGame(String gameName) {
        var statement = "INSERT INTO games (gameName, game) VALUES (?,?)";
        ChessGame newGame = new ChessGame();
        var gameJson = new Gson().toJson(newGame);
        try
        {
            var id = executeUpdate(statement, gameName, gameJson);
            return new GameData(id, null, null, gameName, newGame);
        } catch (DataAccessException e) {System.out.println("you need to figure out what to do with this");

        }
        return null;
    }

    public boolean gameExists(String gameName) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameName FROM games WHERE gameName=?";
            try (var ps = conn.prepareStatement(statement))
            {
                ps.setString(1, gameName);
                try (var rs = ps.executeQuery())
                {
                    return rs.next();
                }
            }

        }
        catch (Exception e)
        {
            System.out.println("you need to figure out what to do with this");
            throw new RuntimeException();

        }
//        return false;
    }

    public boolean gameExists(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement))
            {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery())
                {
                    return rs.next();
                }
            }

        }
        catch (Exception e)
        {
            System.out.println("you need to figure out what to do with this");
            throw new RuntimeException();

        }
//        return false;
    }

    public Collection<GameData> listGames() {
        Collection<GameData> toReturn = new HashSet<>();
        try (var conn = DatabaseManager.getConnection())
        {
            var statement = "SELECT * FROM games";
            try (var ps = conn.prepareStatement(statement))
            {
                try (var rs = ps.executeQuery())
                {
                    while (rs.next())
                    {
                        var gameJson = rs.getString("game");
                        var gameName = rs.getString("gameName");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var game = new Gson().fromJson(gameJson, ChessGame.class);
                        var gameId = rs.getInt("gameID");
                        toReturn.add(new GameData(gameId, whiteUsername, blackUsername, gameName, game));
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("you need to figure out what to do with this");
            throw new RuntimeException();
        }
        return toReturn;
    }

    public GameData getGame(int gameId) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement))
            {
                ps.setInt(1, gameId);
                try (var rs = ps.executeQuery())
                {
                    if (rs.next())
                    {
                        var gameJson = rs.getString("game");
                        var gameName = rs.getString("gameName");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var game = new Gson().fromJson(gameJson, ChessGame.class);
                        return new GameData(gameId, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }

        }
        catch (Exception e)
        {
            System.out.println("you need to figure out what to do with this");
            throw new RuntimeException();
        }
        return null;
    }

    public void addPlayerToGame(int gameId, String authToken, boolean isWhite) {
        String username = this.getUsername(authToken);
        String whiteStatement = """
                        UPDATE games
                        SET whiteUsername =?
                        WHERE gameID =?;""";
        String blackStatement = """
                        UPDATE games
                        SET blackUsername =?
                        WHERE gameID =?;""";
        String statement = isWhite ? whiteStatement : blackStatement;
        try
        {
            executeUpdate(statement, username, gameId);
        } catch (DataAccessException e)
        {
            System.out.println("you need to figure out what to do with this");
            throw new RuntimeException();
        }
    }

    public String getUsername(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM authData WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement))
            {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery())
                {
                    if (rs.next())
                    {
                        return rs.getString("username");
                    }
                }
            }

        }
        catch (Exception e)
        {
            System.out.println("you need to figure out what to do with this");
            throw new RuntimeException();
        }
        return null;
    }
}
