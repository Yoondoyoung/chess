package server;

import service.MyService;
import spark.*;
import websocket.WebsocketHandler;

public class Server {


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/connect", WebsocketHandler.class);
        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> (Handler.getInstance().clear(req, res)) );
        Spark.post("/user", (req, res) -> (Handler.getInstance().register(req, res)));
        Spark.post("/session", (req, res) -> (Handler.getInstance().login(req, res)));
        Spark.delete("session", (req, res) -> (Handler.getInstance().logout(req, res)));
        Spark.post("/game", (req, res) -> (Handler.getInstance().createGame(req, res)));
        Spark.put("/game", (req, res) -> (Handler.getInstance().joinGame(req, res)));
        Spark.get("/game", (req, res) -> (Handler.getInstance().listGames(req, res)));

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
