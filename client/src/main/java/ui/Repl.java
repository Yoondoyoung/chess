package ui;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final Client client;

    public Repl(String serverUrl) throws IOException {
        client = new Client(serverUrl, this);
    }

    public void run() {
        System.out.print(SET_BG_COLOR_BLACK);
        System.out.println("Chess :D");
        System.out.println(SET_TEXT_COLOR_WHITE + client.inputUI());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if ((client.state != State.INGAME) || (result.equals("Board redrawn."))) {
                client.printPrompt();
            }
            int line = client.parseInput(scanner);

            try {
                result = client.eval(line);
                if (!Objects.equals(result, "")) {
                    System.out.println(SET_TEXT_COLOR_GREEN + SET_BG_COLOR_BLACK + result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.println(SET_TEXT_COLOR_BLUE + SET_BG_COLOR_BLACK + msg);
            }
            if (!result.equals("quit")) {
                client.setUIColor();
                System.out.println(client.inputUI());
            }
        }
    }
}
