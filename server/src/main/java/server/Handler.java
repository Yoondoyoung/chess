package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataAccess.DataAccessException;
import model.UserData;
import model.result.FailureResult;
import model.result.LoginResult;
import service.MyService;
import spark.Request;
import spark.Response;

import javax.xml.crypto.Data;

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

    public Object login(Request req, Response res) {
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
            return new Gson().toJson(response_500);
        }

        try {
            service.logout(authToken);
        } catch (Exception e){
            res.status(401);
            FailureResult response_401 = new FailureResult("Error: unauthorized");
            return new Gson().toJson(response_401);
        }

        res.status(200);
        return new Gson().toJson(new Result());
}
