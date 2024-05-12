package dataAccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess
{

    void clear() throws DataAccessException;

    UserData getUser(String username);

    void createUser(String username, String password, String email);

    AuthData createAuth(String username);

    boolean sessionExists(String authToken);

    void deleteSession(String authToken);

    GameData createGame(String gameName, ChessGame game);

    boolean gameExists(String gameName);

    boolean gameExists(int gameID);

    Collection<GameData> listGames();

    GameData getGame(int gameId);

    void addPlayerToGame(int gameId, String authToken, boolean isWhite);

    String getUsername(String authToken);
}
