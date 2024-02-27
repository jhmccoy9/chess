package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import org.eclipse.jetty.server.Authentication;
import spark.*;
import com.google.gson.Gson;
import service.*;
import model.*;

import javax.swing.*;
import javax.xml.crypto.Data;


public class Server {
    private final ClearService clear_service;
    private final UserService user_service;
    private final GameService game_service;

    public Server()
    {
        DataAccess dataAccess = new MemoryDataAccess();
        clear_service = new ClearService(dataAccess);
        user_service = new UserService(dataAccess);
        game_service = new GameService(dataAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.delete("/db", this::clearApp);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.post("/game", this::createGame);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


    private Object createGame(Request req, Response res)
    {
        String auth_token = req.headers("authorization");
        String game_name = (new Gson().fromJson(req.body(), GameData.class)).gameName();

        try
        {
            String game_data = new Gson().toJson(game_service.createGame(auth_token, game_name));
            res.status(200);
            return game_data;
        }
        catch(DataAccessException e)
        {
            ErrorData error = new ErrorData(e.getMessage());
            String to_return = new Gson().toJson(error);

            if (error.message().equals("Error: unauthorized"))
                res.status(401);
            else if (error.message().equals("Error: bad request"))
                res.status(400);
            else
                res.status(500);
            return to_return;
        }
    }
    private Object logoutUser(Request req, Response res)
    {
        String auth_token = req.headers("authorization");
        try
        {
            user_service.logout(auth_token);
            res.status(200);
            return "{}";
        }
        catch (DataAccessException e)
        {
            ErrorData error = new ErrorData(e.getMessage());
            String to_return = new Gson().toJson(error);
            if (error.message().equals("Error: unauthorized"))
            {
                res.status(401);
            }
            else
                res.status(500);

            return  to_return;
        }
    }
    private Object loginUser(Request req, Response res)
    {
        var user = new Gson().fromJson(req.body(), UserData.class);
        try
        {
            AuthData data = user_service.login(user);
            res.status(200);
            return new Gson().toJson(data);
        }
        catch (DataAccessException e)
        {
            ErrorData error = new ErrorData(e.getMessage());
            String to_return = new Gson().toJson(error);
            if (error.message().equals("Error: unauthorized"))
            {
                res.status(401);
            }
            else
            {
                res.status(500);
            }


            return to_return;
        }
    }

    private Object registerUser(Request req, Response res)
    {
        // deserialize the json object
        var new_user = new Gson().fromJson(req.body(), UserData.class);

        try
        {
            AuthData data = user_service.register(new_user);
            res.status(200);
            return new Gson().toJson(data);
        }
        catch(DataAccessException e)
        {
            ErrorData error = new ErrorData(e.toString());
            String to_return = new Gson().toJson(error);


            // see what kind it is, and return the right message based off of that
            if (e.getMessage().equals("Error: already taken"))
            {
                res.status(403);
                return to_return;
            } else if (e.getMessage().equals("Error: bad request")) {
                res.status(400);
                return to_return;
            }
            else {
                res.status(500);
                return to_return;
            }
        }
    }

    private Object clearApp(Request req, Response res)
    {
        try {
            clear_service.clear();
        }
        catch (DataAccessException e)
        {
            // if there's an error
            res.status(500);
            return new Gson().toJson(new model.ErrorData("Error: bad clear attempt"));
        }

        // normal return case
        res.status(200);
        res.body("");
        return "{}";
    }
}
