package server;

import spark.*;
import com.google.gson.Gson;


public class Server {

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
        return new Object();
    }

    private Object clearApp(Request req, Response res)
    {
        /*
        service.deleteAllPets();
        res.status(204);
        return "";
         */


        // right now, it's set up to return 200 no matter what...
        res.status(200);
        res.body("");

        return "";
    }
}
