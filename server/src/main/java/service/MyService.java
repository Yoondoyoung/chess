package service;

import dataAccess.DataAccessException;
import server.Handler;

public class MyService {

    private static MyService instance;
    public MyService() throws DataAccessException {}

    public static synchronized MyService getInstance() throws DataAccessException {
        if (instance == null){
            return new MyService();
        } else {
            return instance;
        }
    }

    public static void clear(){
        System.out.println("Clear executed");
    }
}
