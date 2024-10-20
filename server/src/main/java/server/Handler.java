package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import model.JoinGameRequset;
import model.UserData;
import model.result.CreateGameResult;
import model.result.FailureResult;
import model.result.LoginResult;
import model.result.LogoutResult;
import service.MyService;
import spark.Request;
import spark.Response;

import java.util.List;
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
            FailureResult response_500 = new FailureResult("Error: description");
            return new Gson().toJson(response_500);
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
            FailureResult response_500 = new FailureResult("Error: description");
            return gson.toJson(response_500);
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
        String authToken = null;
        try {
            authToken = req.headers("authorization");
        } catch(JsonSyntaxException e) {
            FailureResult response_500 = new FailureResult("Error: description");
            return gson.toJson(response_500);
        }

        try {
            service.logout(authToken);
        } catch (Exception e){
            res.status(401);
            FailureResult response_401 = new FailureResult("Error: unauthorized");
            return gson.toJson(response_401);
        }

        res.status(200);
        return gson.toJson(new LogoutResult());
    }

    public Object createGame(Request req, Response res) throws Exception {
        GameData gameData = null;
        String authToken = null;

        int gameID = 0;
        try {
            authToken = req.headers("authorization");
            gameData = gson.fromJson(req.body(), GameData.class);
        } catch(JsonSyntaxException e) {
            FailureResult response_500 = new FailureResult("Error: description");
            return gson.toJson(response_500);
        }


        if(!service.isValidAuth(authToken)) {
            res.status(401); // Unauthorized if login fails
            return gson.toJson(new FailureResult("Error: unauthorized"));
        }

        try{
            gameID = (service.createGame(gameData.gameName()));
        } catch (Exception e) {
            res.status(400);
            FailureResult response_400 = new FailureResult("Error: missing or invalid fields");
            return gson.toJson(response_400);
        }

        res.status(200);
        CreateGameResult createGameResult = new CreateGameResult(gameID);
        System.out.println(createGameResult);
        return gson.toJson(createGameResult);
    }

    public Object joinGame(Request req, Response res) throws Exception {
        String authToken = null;
        JoinGameRequset joinGameRequset = null;
        try {
            authToken = req.headers("authorization");
            joinGameRequset = new Gson().fromJson(req.body(), JoinGameRequset.class);
        } catch (JsonSyntaxException e) {
            FailureResult response_500 = new FailureResult("Error: description");
            return gson.toJson(response_500);
        }
        if (!service.isValidAuth(authToken)) {
            res.status(401); // Unauthorized if login fails
            return gson.toJson(new FailureResult("Error: unauthorized"));
        }
        try {
            service.joinGame(joinGameRequset, authToken);
        } catch (DataAccessException e){
            res.status(400);
            FailureResult response_403 = new FailureResult("Error: already taken");
            return new Gson().toJson(response_403);
        }

        res.status(200);
        return gson.toJson(new LogoutResult());
    }

    public Object listGames(Request req, Response res) throws Exception {
        String authToken = null;

        try {
            authToken = req.headers("authorization");
        } catch(JsonSyntaxException e) {
            FailureResult response_500 = new FailureResult("Error: description");
            return gson.toJson(response_500);
        }
        try{
            List<GameData> gameDataList = service.listGames(authToken);
        }catch (DataAccessException e) {
            FailureResult response_500 = new FailureResult("Error: description");
            return gson.toJson(response_500);
        }

        res.status(200);

        return gson.toJson(new LogoutResult());
    }
}
