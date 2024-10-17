package service;

import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import spark.Request;

import java.util.Objects;
import java.util.UUID;

public class MyService {
    UserData existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
    AuthData auth = new AuthData("existingAuth", "temp");
    private static MyService instance;
    public MyService() throws DataAccessException {}

    public static synchronized MyService getInstance() throws DataAccessException {
        if (instance == null){
            return new MyService();
        } else {
            return instance;
        }
    }

    public void clear(){
        System.out.println("Clear executed");
    }

    public String register(UserData userData){
        AuthData auth;
        if(Objects.equals(userData.username(), existingUser.username())){
            return null;
        }else{
            auth = new AuthData(UUID.randomUUID().toString(), userData.username());
        }
        return auth.authToken();
    }

    public String login(UserData userData){
        AuthData auth;
        if(!Objects.equals(userData.password(), existingUser.password())){
            return null;
        }else{
            auth = new AuthData(UUID.randomUUID().toString(), userData.username());
        }

        return auth.authToken();
    }

    public void logout(String authToken) throws Exception {
        //Delete auth token
        if(Objects.equals(authToken, auth.authToken())){
            auth = null;
        }else{
            throw new Exception("Error");
        }
    }
}
