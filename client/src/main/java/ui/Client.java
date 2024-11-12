package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;
import model.*;
import model.result.GameResult;
import model.result.GameListResult;
import model.result.UserResult;
import model.GameNameResponse;
import ui.websocket.NotificationHandler;

import static java.lang.Character.getNumericValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Client {

    private final ServerFacade facade;
    private final Scanner scanner;
    private String username = null;
    private String authToken = null;
    public State state = State.SIGNEDOUT;
    private final Board board = new Board();
    private ChessGame.TeamColor teamColor;
    private int gameID;

    public Client(String serverUrl, NotificationHandler notificationHandler) throws IOException {
        scanner = new Scanner(System.in);
        facade = new ServerFacade(serverUrl, notificationHandler);
    }

    public void setUIColor(){
        System.out.print(SET_TEXT_COLOR_WHITE + SET_BG_COLOR_BLACK);
    }

    void printPrompt() {
        setUIColor();
        System.out.print(">>> " + SET_BG_COLOR_GREEN);
    }

    public String inputUI() {
        if (this.state == State.SIGNEDOUT) {
            return """
                    Enter a number:\s
                    1. Register\s
                    2. Login\s
                    3. Quit\s
                    4. Help""";
        } else if (this.state == State.SIGNEDIN) {
            return """
                Enter a number:\s
                1. Create Game\s
                2. Join Game\s
                3. Join as Observer\s
                4. List Games\s
                5. Logout\s
                6. Help""";
        } else if (this.state == State.INGAME){
            return """
                Enter a number: \s
                1. Make Move\s
                2. Highlight Legal Moves\s
                3. Redraw Chess Board\s
                4. Leave\s
                5. Resign\s
                6. Help""";
        } else if (this.state == State.OBSERVING){
            return """
                Enter a number: \s
                1. Highlight Legal Moves\s
                2. Redraw Chess Board\s
                3. Leave\s
                4. Help""";
        }
        return "quit";
    }

    public int parseInput(Scanner scanner){
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception e){
            System.out.println(SET_TEXT_COLOR_BLUE + SET_BG_COLOR_BLACK + "Please enter a number.");
            return parseInput(scanner);
        }
    }

    public String eval(int userInput) throws IOException {
        if (this.state == State.SIGNEDOUT){
            switch (userInput) {
                case 1 -> {return registerUI();}
                case 2 -> {return loginUI();}
                case 3 -> {
                    return "quit";
                }
                default -> {return """
                Enter just the number of the option you want to pick.\s
                1. Register will register you as a user.\s
                2. Login will log you in.\s
                3. Quits\s
                Anything else, you will get a help menu.""";}
            }
        } else if (this.state == State.SIGNEDIN){
            switch (userInput) {
                case 1 -> {return createGameUI();}
                case 2 -> {return joinGameUI();}
                case 3 -> {return joinObserverUI();}
                case 4 -> {return listGamesUI();}
                case 5 -> {return logoutUI();}
                default -> {return """
                Enter just the number of the option you want to pick.\s
                1. Creates a new game with the name given.\s
                2. Joins a game of the number given in the color specified.\s
                3. Joins a game of the number given as a non-player.\s
                4. Lists the games in the system, numbered by ID.\s
                5. Logs the user out.\s
                Anything else, you will get a help menu.""";}
            }
        } else if (this.state == State.INGAME){
            switch (userInput) {
                case 1 -> {return makeMoveUI();}
                case 2 -> {return highlightGameUI();}
                case 3 -> {return drawGameUI();}
                case 4 -> {return leaveGameUI();}
                case 5 -> {return resignGameUI();}
                default -> {return """
                Enter just the number of the option you want to pick.\s
                1. Enter a start location and an end location to make a move.\s
                2. Enter a location to see what moves the piece can make.\s
                3. Redraws the board.\s
                4. Leaves the game without resigning.\s
                5. Forfeits the game.\s
                Anything else, you will get a help menu.""";}
            }
        } else if (this.state == State.OBSERVING) {
            switch (userInput) {
                case 1 -> {return highlightGameUI();}
                case 2 -> {return drawGameUI();}
                case 3 -> {return leaveGameUI();}
                default -> {return """
                Enter just the number of the option you want to pick.\s
                1. Enter a start location and an end location to make a move.\s
                2. Enter a location to see what moves the piece can make.\s
                3. Redraws the board.\s
                4. Leaves the game without resigning.\s
                5. Forfeits the game.\s
                Anything else, you will get a help menu.""";}
            }
        }
        return "quit";
    }

    private String registerUI() throws IOException {
        setUIColor();
        System.out.println("Please give a username:");
        printPrompt();
        String username = scanner.nextLine();
        setUIColor();
        System.out.println("Please give a password:");
        printPrompt();
        String password = scanner.nextLine();
        setUIColor();
        System.out.println("Please give an email:");
        printPrompt();
        String email = scanner.nextLine();

        facade.registerUser(new UserData(username, password, email));
        return login(new LoginData(username, password));
    }

    private String loginUI() throws IOException {
        setUIColor();
        System.out.println("Please give a username:");
        printPrompt();
        String username = scanner.nextLine();
        setUIColor();
        System.out.println("Please give a password:");
        printPrompt();
        String password = scanner.nextLine();

        return login(new LoginData(username, password));
    }

    private String login(LoginData loginData) throws IOException {
        UserResult user = new UserResult("", "");
        user = facade.loginUser(loginData);

        this.authToken = user.authToken();
        this.username = user.username();
        this.state = State.SIGNEDIN;

        return "Logged in as: " + user.username();
    }

    private String logoutUI() throws IOException {
        facade.logoutUser(this.authToken);
        this.state = State.SIGNEDOUT;
        this.authToken = null;
        this.teamColor = null;
        return this.username + " has been logged out.";
    }

    private String createGameUI() throws IOException {
        assertSignedIn();
        setUIColor();
        System.out.println("Please give a game name:");
        printPrompt();
        String gameName = scanner.nextLine();

        int id = facade.createGame(this.authToken, new GameNameResponse(gameName));

        return "Game created with id " + id;
    }

    private String joinGameUI() throws IOException {
        assertSignedIn();

        setUIColor();
        System.out.println("Please give a game number:");
        printPrompt();
        int gameID;
        try {
            gameID = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            setUIColor();
            System.out.println(SET_TEXT_COLOR_BLUE + SET_BG_COLOR_BLACK + "The game ID has to be a number.");
            return joinGameUI();
        }

        ChessGame.TeamColor playerColor = null;
        String colorInput = "";
        while (playerColor == null) {
            setUIColor();
            System.out.println("Please give a team color (WHITE/BLACK):");
            printPrompt();
            colorInput = scanner.nextLine();

            switch (colorInput.toUpperCase()){
                case "WHITE" -> {playerColor = ChessGame.TeamColor.WHITE;}
                case "BLACK" -> {playerColor = ChessGame.TeamColor.BLACK;}
                default -> {System.out.println(SET_TEXT_COLOR_WHITE + "It seems there was a typo with the player color.");}
            }
        }

        facade.joinGame(this.authToken, new JoinGameData(colorInput, gameID));
        this.state = State.INGAME;
        this.teamColor = playerColor;
        this.gameID = gameID;
        return "Joined game as player.";
    }

    private String joinObserverUI() throws IOException {
        assertSignedIn();

        setUIColor();
        System.out.println("Please give a game number:");
        printPrompt();
        int gameID;
        try {
            gameID = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            setUIColor();
            System.out.println(SET_TEXT_COLOR_BLUE + SET_BG_COLOR_BLACK + "The game ID has to be a number.");
            return joinObserverUI();
        }

        facade.joinGame(this.authToken, new JoinGameData(null, gameID));

        this.state = State.OBSERVING;
        this.teamColor = null;
        this.gameID = gameID;
        return "Joined game as observer.";
    }

    private String listGamesUI() throws IOException {
        assertSignedIn();

        GameListResult gameResult = facade.listGames(this.authToken);
        Collection<GameResult> games = gameResult.games();

        StringBuilder result = new StringBuilder();

        int i = 1;
        result.append("Games: \n");
        for (GameResult game : games) {
            if (i < games.size()) {
                result.append(String.format("%d. Name = %s, ID = %d, White Player = %s, Black Player = %s\n", i, game.gameName(), game.gameID(), game.whiteUsername(), game.blackUsername()));
            } else {
                result.append(String.format("%d. Name = %s, ID = %d, White Player = %s, Black Player = %s", i, game.gameName(), game.gameID(), game.whiteUsername(), game.blackUsername()));
            }
            i++;
        }

        return result.toString();
    }

    private void assertSignedIn() throws IOException {
        if (this.state == State.SIGNEDOUT) {
            throw new IOException(SET_TEXT_COLOR_RED + "You must sign in");
        }
    }

    private String makeMoveUI(){
        setUIColor();
        System.out.println("Please enter the start location (ex: a1): ");
        printPrompt();
        String start = scanner.nextLine();
        setUIColor();
        System.out.println("Please enter the end location (ex: b2): ");
        printPrompt();
        String end = scanner.nextLine();

        ChessPiece.PieceType promoType = ChessPiece.PieceType.KING;
        while (promoType == ChessPiece.PieceType.KING) {
            setUIColor();
            System.out.println("Please enter a promotion type. If no promotion is available, please enter NONE.\n" +
                    " (NONE/ROOK/BISHOP/KNIGHT/QUEEN)");
            printPrompt();
            String typeInput = scanner.nextLine();

            switch (typeInput.toUpperCase()) {
                case "ROOK" -> {
                    promoType = ChessPiece.PieceType.ROOK;
                }
                case "BISHOP" -> {
                    promoType = ChessPiece.PieceType.BISHOP;
                }
                case "KNIGHT" -> {
                    promoType = ChessPiece.PieceType.KNIGHT;
                }
                case "QUEEN" -> {
                    promoType = ChessPiece.PieceType.QUEEN;
                }
                case "NONE" -> {
                    promoType = null;
                }
                default -> {
                    setUIColor();
                    System.out.println("It seems there was a typo with the promotion type.");
                }
            }
        }
        int startRow = getNumericValue(start.charAt(1));
        int startCol = (getNumericValue(start.charAt(0))-9);
        int endRow = getNumericValue(end.charAt(1));
        int endCol = (getNumericValue(end.charAt(0))-9);

        ChessPosition startPos = new ChessPosition(startRow, startCol);
        ChessPosition endPos = new ChessPosition(endRow, endCol);
        ChessMove move = new ChessMove(startPos, endPos, promoType);

        try {
            facade.makeMove(authToken, gameID, move);
        } catch (Exception ignored) {
            return "Move failed.";
        }

        return "Moved " + start + " to " + end;
    }

    private String highlightGameUI(){
        setUIColor();
        System.out.println("Please enter the piece location (ex: a1): ");
        printPrompt();
        String locationInput = scanner.nextLine();

        int row = getNumericValue(locationInput.charAt(1));
        int col = (getNumericValue(locationInput.charAt(0))-9);

        ChessPosition position = new ChessPosition(row, col);
        Collection<ChessMove> moves = board.getGame().validMoves(position);

        if (teamColor == ChessGame.TeamColor.BLACK) {
            board.drawBlackPlayer(moves);
        } else {
            board.drawWhitePlayer(moves);
        }

        return "Board redrawn.";
    }

    public String drawGameUI(){
        Collection<ChessMove> moves = new ArrayList<>();
        if (teamColor == ChessGame.TeamColor.BLACK) {
            board.drawBlackPlayer(moves);
        } else {
            board.drawWhitePlayer(moves);
        }

        return "Board redrawn.";
    }

    private String leaveGameUI() throws IOException {
        this.state = State.SIGNEDIN;
        facade.leaveGame(authToken, gameID);

        return "Left Game";
    }

    private String resignGameUI() throws IOException {
        this.state = State.SIGNEDIN;
        facade.resignGame(authToken, gameID);

        return "Resigned Game.";
    }

    public void setBoard(ChessGame game){
        board.setGame(game);
    }
}