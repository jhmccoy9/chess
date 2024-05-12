package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import dataAccess.MySqlDataAccess;
import server.websocket.WebSocketHandler;
import spark.*;
import com.google.gson.Gson;
import service.*;
import model.*;

import java.util.Collection;
import java.util.Map;


public class Server {
    private ClearService clearService;
    private UserService userService;
    private GameService gameService;
    private WebSocketHandler webSocketHandler;

    public Server()
    {
        DataAccess dataAccess;
        try
        {
            dataAccess = new MySqlDataAccess();
            clearService = new ClearService(dataAccess);
            userService = new UserService(dataAccess);
            gameService = new GameService(dataAccess);
        }
        catch (DataAccessException e)
        {
            dataAccess = new MemoryDataAccess();
            clearService = new ClearService(dataAccess);
            userService = new UserService(dataAccess);
            gameService = new GameService(dataAccess);
        }
        // make the websocket handler, and give it access to all the data
        webSocketHandler = new WebSocketHandler(dataAccess);


    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.webSocket("/connect", webSocketHandler);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.delete("/db", this::clearApp);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.post("/game", this::createGame);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::joinGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object joinGame(Request req, Response res)
    {
        // pull out all the data
        String authToken = req.headers("authorization");
        String color = new Gson().fromJson(req.body(), JoinGameData.class).playerColor();

        int gameID = (new Gson().fromJson(req.body(), JoinGameData.class)).gameID();

        // see if you can run it validly
        try
        {
            gameService.joinGame(authToken, color, gameID);
            res.status(200);
            return "{}";
        }
        catch (DataAccessException e)
        {
            ErrorData error = new ErrorData(e.getMessage());
            String toReturn = new Gson().toJson(error);

            switch (error.message()) {
                case "Error: unauthorized" -> res.status(401);
                case "Error: bad request" -> res.status(400);
                case "Error: already taken" -> res.status(403);
                default -> res.status(500);
            }
            System.out.println(error.message());
            return toReturn;
        }
    }

    private Object listGames(Request req, Response res)
    {
        String authToken = req.headers("authorization");
        try  // return a list of games if successful
        {
            Collection<GameData> games = gameService.listGames(authToken);
            return new Gson().toJson(Map.of("games", games.toArray()));
        }
        catch (DataAccessException e)
        {
            ErrorData error = new ErrorData(e.getMessage());
            String toReturn = new Gson().toJson(error);

            if (error.message().equals("Error: unauthorized"))
                res.status(401);
            else
                res.status(500);
            System.out.println(error.message());
            return toReturn;
        }


    }

    private Object createGame(Request req, Response res)
    {
        String authToken = req.headers("authorization");
        String gameName = (new Gson().fromJson(req.body(), GameData.class)).gameName();

        try
        {
            String gameData = new Gson().toJson(gameService.createGame(authToken, gameName));
            res.status(200);
            return gameData;
        }
        catch(DataAccessException e)
        {
            ErrorData error = new ErrorData(e.getMessage());
            String toReturn = new Gson().toJson(error);

            if (error.message().equals("Error: unauthorized"))
                res.status(401);
            else if (error.message().equals("Error: bad request"))
                res.status(400);
            else
                res.status(500);
            System.out.println(error.message());
            return toReturn;
        }
    }
    private Object logoutUser(Request req, Response res)
    {
        String authToken = req.headers("authorization");
        try
        {
            userService.logout(authToken);
            res.status(200);
            return "{}";
        }
        catch (DataAccessException e)
        {
            ErrorData error = new ErrorData(e.getMessage());
            String toReturn = new Gson().toJson(error);
            if (error.message().equals("Error: unauthorized"))
            {
                res.status(401);
            }
            else
                res.status(500);
            System.out.println(error.message());
            return  toReturn;
        }
    }
    private Object loginUser(Request req, Response res)
    {
        var user = new Gson().fromJson(req.body(), UserData.class);
        try
        {
            AuthData data = userService.login(user);
            res.status(200);
            return new Gson().toJson(data);
        }
        catch (DataAccessException e)
        {
            ErrorData error = new ErrorData(e.getMessage());
            String toReturn = new Gson().toJson(error);
            if (error.message().equals("Error: unauthorized"))
            {
                res.status(401);
            }
            else
            {
                res.status(500);
                System.out.println(error.message());
            }


            return toReturn;
        }
    }

    private Object registerUser(Request req, Response res)
    {
        // deserialize the json object
        var newUser = new Gson().fromJson(req.body(), UserData.class);
        try
        {
            AuthData data = userService.register(newUser);
            res.status(200);
            return new Gson().toJson(data);
        }
        catch(DataAccessException e)
        {
            ErrorData error = new ErrorData(e.toString());
            String toReturn = new Gson().toJson(error);
            // see what kind it is, and return the right message based off of that
            if (e.getMessage().equals("Error: already taken"))
            {
                res.status(403);
                return toReturn;
            } else if (e.getMessage().equals("Error: bad request")) {
                res.status(400);
                return toReturn;
            }
            else {
                res.status(500);
                System.out.println(error.message());
                return toReturn;
            }
        }
    }

    private Object clearApp(Request req, Response res)
    {
        try {
            clearService.clear();
        }
        catch (DataAccessException e)
        {
            // if there's an error
            res.status(500);
            System.out.println("Error: bad clear attempt");

            return new Gson().toJson(new model.ErrorData("Error: bad clear attempt"));
        }

        // normal return case
        res.status(200);
        res.body("");
        return "{}";
    }
}
