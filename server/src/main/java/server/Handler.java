package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.result.FailureResult;
import service.MyService;
import spark.Request;
import spark.Response;

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
            MyService.clear();
        } catch (Exception e){
            FailureResult response_500 = new FailureResult("Error: description");
            return new Gson().toJson(response_500);
        }
        res.status(200);
        return new Gson().toJson(null);
    }
}
