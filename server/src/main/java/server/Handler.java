package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import model.GameData;
import model.JoinGameRequset;
import model.UserData;
import model.result.*;
import service.MyService;
import spark.Request;
import spark.Response;

import java.util.Objects;

public class Handler {
    private static Handler instance;
    private final MyService service = new MyService();
    private final Gson gson = new Gson();

    public Handler() throws DataAccessException {
    }

    public static synchronized Handler getInstance() throws DataAccessException {
        if (instance == null) {
            instance = new Handler();
        }
        return instance;
    }

    public Object clear(Request req, Response res){
        try {
            service.clear();
        } catch (Exception e){
            FailureResult response500 = new FailureResult("Error: description");
            return new Gson().toJson(response500);
        }
        res.status(200);
        return gson.toJson(null);
    }

    public Object register(Request req, Response res){
        String result;
        UserData userData;
        try{
            userData = gson.fromJson(req.body(), UserData.class);
            result = service.register(userData);

        } catch (Exception e){
            FailureResult response500 = new FailureResult("Error: description");
            return gson.toJson(response500);
        }
        if(result==null){
            res.status(403);
            return gson.toJson(new FailureResult("Error: unauthorized"));
        }else if(userData.username() == null || userData.password() == null || userData.email() == null){
            res.status(400);
            return gson.toJson(new FailureResult("Error: Bad request"));
        }
        res.status(200);
        LoginResult registerResult = new LoginResult(userData.username(), result);
        return gson.toJson(registerResult);
    }

    public Object login(Request req, Response res) throws DataAccessException {
        UserData userData;
        String auth;
        // Parse request body to UserData and handle potential JSON errors
        try {
            userData = gson.fromJson(req.body(), UserData.class);
            if (userData == null || userData.username() == null || userData.password() == null) {
                res.status(400); // Bad request
                return gson.toJson(new FailureResult("Error: missing or invalid fields"));
            }
        } catch (JsonSyntaxException e) {
            res.status(400); // Bad request for malformed JSON
            return gson.toJson(new FailureResult("Error: invalid JSON"));
        }
        auth = service.login(userData);
        if (auth == null) {
            res.status(401); // Unauthorized if login fails
            return gson.toJson(new FailureResult("Error: unauthorized"));
        }

        // Successful login
        res.status(200);
        LoginResult loginResult = new LoginResult(userData.username(), auth);
        return gson.toJson(loginResult);
    }

    public Object logout(Request req, Response res) {
        String authToken;
        try {
            authToken = req.headers("authorization");
        } catch(JsonSyntaxException e) {
            FailureResult response500 = new FailureResult("Error: description");
            return gson.toJson(response500);
        }

        try {
            service.logout(authToken);
        } catch (Exception e){
            res.status(401);
            FailureResult response401 = new FailureResult("Error: unauthorized");
            return gson.toJson(response401);
        }

        res.status(200);
        return gson.toJson(new LogoutResult());
    }

    public Object createGame(Request req, Response res) throws Exception {
        GameData gameData;
        String authToken;

        int gameID;
        try {
            authToken = req.headers("authorization");
            gameData = gson.fromJson(req.body(), GameData.class);
        } catch(JsonSyntaxException e) {
            FailureResult response500 = new FailureResult("Error: description");
            return gson.toJson(response500);
        }


        if(!service.isValidAuth(authToken)) {
            res.status(401); // Unauthorized if login fails
            return gson.toJson(new FailureResult("Error: unauthorized"));
        }

        try{
            gameID = (service.createGame(gameData.gameName()));
        } catch (Exception e) {
            res.status(400);
            FailureResult response400 = new FailureResult("Error: missing or invalid fields");
            return gson.toJson(response400);
        }

        res.status(200);
        CreateGameResult createGameResult = new CreateGameResult(gameID);
        return gson.toJson(createGameResult);
    }

    public Object joinGame(Request req, Response res) throws Exception {
        String authToken;
        JoinGameRequset joinGameRequset;


        try {
            authToken = req.headers("authorization");
            joinGameRequset = new Gson().fromJson(req.body(), JoinGameRequset.class);
        } catch (JsonSyntaxException e) {
            FailureResult response500 = new FailureResult("Error: description");
            return gson.toJson(response500);
        }
        if (!service.isValidAuth(authToken)) {
            res.status(401); // Unauthorized if login fails
            return gson.toJson(new FailureResult("Error: unauthorized"));
        }
        if ((!Objects.equals(joinGameRequset.playerColor(), "WHITE") && !Objects.equals(joinGameRequset.playerColor(), "BLACK") && Objects.equals(joinGameRequset.playerColor(), null)) || Objects.equals(joinGameRequset.gameID(), 0)){
            res.status(400);
            FailureResult response400 = new FailureResult("Error: bad request");
            return new Gson().toJson(response400);
        }
        try {
            service.joinGame(joinGameRequset, authToken);
        } catch (DataAccessException e){
            res.status(403);
            FailureResult response403 = new FailureResult("Error: already taken");
            return new Gson().toJson(response403);
        }

        res.status(200);
        return gson.toJson(new LogoutResult());
    }

    public Object listGames(Request req, Response res) throws Exception {
        String authToken;
        GameListResult gameListResult;
        try {
            authToken = req.headers("authorization");
        } catch(JsonSyntaxException e) {
            FailureResult response500 = new FailureResult("Error: description");
            return gson.toJson(response500);
        }
        try{
            gameListResult = new GameListResult(service.listGames(authToken));
        }catch (DataAccessException e) {
            res.status(401); // Unauthorized if login fails
            return gson.toJson(new FailureResult("Error: unauthorized"));
        }

        res.status(200);
        return gson.toJson(gameListResult);
    }
}
