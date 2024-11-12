package ui.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Message types for server messages
enum ServerMessageType {
    LOAD_GAME,
    ERROR,
    NOTIFICATION
}

// Base class for server messages
class ServerMessage {
    protected ServerMessageType serverMessageType;

    public ServerMessageType getServerMessageType() {
        return serverMessageType;
    }
}

class ErrorMessage extends ServerMessage {
    private String message;

    public ErrorMessage(String message) {
        this.serverMessageType = ServerMessageType.ERROR;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

class LoadGame extends ServerMessage {
    private int gameID;
    private ChessGame game;

    public LoadGame(int gameID, ChessGame game) {
        this.serverMessageType = ServerMessageType.LOAD_GAME;
        this.gameID = gameID;
        this.game = game;
    }

    public int getGameID() { return gameID; }
    public ChessGame getGame() { return game; }
}

class Notification extends ServerMessage {
    private String message;

    public Notification(String message) {
        this.serverMessageType = ServerMessageType.NOTIFICATION;
        this.message = message;
    }

    public String getMessage() { return message; }
}

// Command classes for client messages
abstract class UserGameCommand {
    protected String authToken;
    protected int gameID;
    protected String commandType;

    public UserGameCommand(String authToken, int gameID, String commandType) {
        this.authToken = authToken;
        this.gameID = gameID;
        this.commandType = commandType;
    }
}

class JoinPlayer extends UserGameCommand {
    private ChessGame.TeamColor teamColor;

    public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor teamColor) {
        super(authToken, gameID, "JOIN_PLAYER");
        this.teamColor = teamColor;
    }
}

class JoinObserver extends UserGameCommand {
    public JoinObserver(String authToken, int gameID) {
        super(authToken, gameID, "JOIN_OBSERVER");
    }
}

class MakeMove extends UserGameCommand {
    private ChessMove move;

    public MakeMove(String authToken, int gameID, ChessMove move) {
        super(authToken, gameID, "MAKE_MOVE");
        this.move = move;
    }
}

class Leave extends UserGameCommand {
    public Leave(String authToken, int gameID) {
        super(authToken, gameID, "LEAVE");
    }
}

class Resign extends UserGameCommand {
    public Resign(String authToken, int gameID) {
        super(authToken, gameID, "RESIGN");
    }
}


// Main SocketFacade class
import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;

public class SocketFacade {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private NotificationHandler notificationHandler;
    private final ExecutorService executorService;
    private volatile boolean isRunning;

    public SocketFacade(String host, int port, NotificationHandler notificationHandler) throws IOException {
        this.socket = new Socket(host, port);
        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.notificationHandler = notificationHandler;
        this.executorService = Executors.newSingleThreadExecutor();
        this.isRunning = true;

        startMessageListener();
    }

    private void startMessageListener() {
        executorService.submit(() -> {
            try {
                while (isRunning && !socket.isClosed()) {
                    String message = reader.readLine();
                    if (message != null) {
                        handleMessage(message);
                    }
                }
            } catch (IOException e) {
                notificationHandler.error(new ErrorMessage("Connection error: " + e.getMessage()));
            }
        });
    }

    private void handleMessage(String message) {
        try {
            ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
            switch (serverMessage.getServerMessageType()) {
                case LOAD_GAME -> loadGame(message);
                case ERROR -> error(message);
                case NOTIFICATION -> notification(message);
            }
        } catch (Exception e) {
            notificationHandler.error(new ErrorMessage("Failed to parse message: " + e.getMessage()));
        }
    }

    public void loadGame(String serverMessage) {
        LoadGame loadGame = new Gson().fromJson(serverMessage, LoadGame.class);
        notificationHandler.loadGame(loadGame);
    }

    public void error(String serverMessage) {
        ErrorMessage error = new Gson().fromJson(serverMessage, ErrorMessage.class);
        notificationHandler.error(error);
    }

    public void notification(String serverMessage) {
        Notification notification = new Gson().fromJson(serverMessage, Notification.class);
        notificationHandler.notify(notification);
    }

    private void sendCommand(Object command) throws IOException {
        if (socket.isClosed()) {
            throw new IOException("Socket is closed");
        }
        writer.println(new Gson().toJson(command));
    }

    public void joinPlayer(String authToken, int gameID, ChessGame.TeamColor teamColor) throws IOException {
        var command = new JoinPlayer(authToken, gameID, teamColor);
        sendCommand(command);
    }

    public void joinObserver(String authToken, int gameID) throws IOException {
        var command = new JoinObserver(authToken, gameID);
        sendCommand(command);
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        var command = new MakeMove(authToken, gameID, move);
        sendCommand(command);
    }

    public void resignGame(String authToken, int gameID) throws IOException {
        var command = new Resign(authToken, gameID);
        sendCommand(command);
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        var command = new Leave(authToken, gameID);
        sendCommand(command);
    }

    public void close() {
        isRunning = false;
        executorService.shutdown();
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            // Log or handle cleanup errors
        }
    }
}