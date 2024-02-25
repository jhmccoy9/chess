package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import spark.*;
import com.google.gson.Gson;
import service.*;
import model.*;

import javax.xml.crypto.Data;


public class Server {
    private final ClearService clear_service;
    private final RegistrationService registration_service;

    public Server()
    {

        DataAccess dataAccess = new MemoryDataAccess();
        clear_service = new ClearService(dataAccess);
        registration_service = new RegistrationService(dataAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.delete("/db", this::clearApp);


        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
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
            // see what kind it is, and return the right message based off of that

        }
        res.status(500);
        return "{}";
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
