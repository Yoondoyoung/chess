import chess.*;
import server.Server;
import service.MyService;

public class Main {
    public static void main(String[] args) {
        System.out.println("Server Run");
        var server = new Server();
        server.run(8080);
    }
}