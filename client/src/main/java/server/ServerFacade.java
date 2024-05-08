package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.JoinGameData;
import model.UserData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
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

    public Object joinGame(int GameID, String color, String authToken) throws ResponseException
    {
        var path = "/game";
        JoinGameData request = new JoinGameData(color, GameID);
        return this.makeRequestAuthToken("PUT", path, request, Object.class, authToken);
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException
    {
        record listGamesResponse(GameData[] game) {};

        var path = "/game";
        var response = this.makeRequestListGames("GET", path, null, authToken);

        Collection<GameData> toReturn = new ArrayList<GameData>(Arrays.asList(response));
        return toReturn;
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






    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(!method.equals("GET"));

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

    private GameData[] makeRequestListGames(String method, String path, Object request, String authToken)
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
            return readBody(http);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static GameData[] readBody(HttpURLConnection http) throws IOException {
        GameData[] response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String temp = bufferedReader.readLine();
                String temp1 = temp.substring(9, temp.length() - 1);
                response = new Gson().fromJson(temp1, GameData[].class);
            }
        }
        return response;
    }

}