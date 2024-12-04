import chess.*;
import ui.Repl;

public class Main {
    public static void main(String[] args) {
        try {
            var port = 8080;
            Repl repl = new Repl("http://localhost:" + port);
            System.out.println("Client run with port : " + port);
            repl.run();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}