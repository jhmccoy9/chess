package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public AuthData registerUser(UserData user) throws ResponseException
    {
        var path = "/user";
        return this.makeRequest("POST", path, user, AuthData.class);
    }

    public AuthData loginUser(UserData user) throws ResponseException
    {
        var path = "/session";
        return this.makeRequest("POST", path, user, AuthData.class);
    }

    public String clear() throws ResponseException
    {
        var path = "/db";
        return this.makeRequest("DELETE", path, null, null);
    }

    public void joinGame() {}

    public Collection<ChessGame> listGames(String authToken) throws ResponseException
    {
        var path = "/game";
        var response = this.makeRequestAuthToken("GET", path, null, Object.class, authToken);
        if (response.getClass().equals(LinkedTreeMap.class)) {
            LinkedTreeMap<String, ChessGame> map = (LinkedTreeMap<String, ChessGame>) (response);
            Collection<ChessGame> list = (Collection<ChessGame>) map.get("games");
            Collection<ChessGame> toReturn = new ArrayList<>();
            return toReturn;
        }
        throw new ResponseException(500, "Invalid List");
    }

    public GameData createGame(String gameName, String authToken) throws ResponseException {
        var path = "/game";
        GameData gameData = new GameData(0, "", "", gameName, new ChessGame());
        return this.makeRequestAuthToken("POST", path, gameData, GameData.class, authToken);
    }

    public void logoutUser(String authToken) throws ResponseException
    {
        var path = "/session";
        this.makeRequestAuthToken("DELETE", path, null, null, authToken);
    }




//    public Pet[] listPets() throws ResponseException {
//        var path = "/pet";
//        record listPetResponse(Pet[] pet) {
//        }
//        var response = this.makeRequest("GET", path, null, listPetResponse.class);
//        return response.pet();
//    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private <T> T makeRequestAuthToken(String method, String path, Object request, Class<T> responseClass, String authToken)
            throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.addRequestProperty("authorization", authToken);
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
