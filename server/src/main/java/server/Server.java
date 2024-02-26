package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import spark.*;
import com.google.gson.Gson;
import service.*;
import model.*;

import javax.swing.*;
import javax.xml.crypto.Data;


public class Server {
    private final ClearService clear_service;
    private final RegistrationService registration_service;
    private final LoginService login_service;

    public Server()
    {
        DataAccess dataAccess = new MemoryDataAccess();
        clear_service = new ClearService(dataAccess);
        registration_service = new RegistrationService(dataAccess);
        login_service = new LoginService(dataAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.delete("/db", this::clearApp);
        Spark.post("/session", this::loginUser);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object loginUser(Request req, Response res)
    {
        var user = new Gson().fromJson(req.body(), UserData.class);
        try
        {
            AuthData data = login_service.login(user);
            res.status(200);
            return new Gson().toJson(data);
        }
        // TODO: implement this!!!
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
            AuthData data = registration_service.register(new_user);
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
