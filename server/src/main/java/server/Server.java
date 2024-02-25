package server;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.MemoryDataAccess;
import spark.*;
import com.google.gson.Gson;
import service.ClearService;
import model.*;

import javax.xml.crypto.Data;


public class Server {
    private final ClearService clear_service;

    public Server()
    {

        DataAccess dataAccess = new MemoryDataAccess();
        clear_service = new ClearService(dataAccess);
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
        /*
        var pet = new Gson().fromJson(req.body(), Pet.class);
        pet = service.addPet(pet);
        webSocketHandler.makeNoise(pet.name(), pet.sound());
        return new Gson().toJson(pet);
         */
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
