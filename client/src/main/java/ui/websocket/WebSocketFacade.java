package ui.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import websocket.messages.ServerMessage.*;
import websocket.commands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws IOException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()){
                        case LOAD_GAME -> loadGame(message);
                        case ERROR -> error(message);
                        case NOTIFICATION -> notification(message);
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    public void loadGame(String serverMessage){
        LoadGame loadGame = new Gson().fromJson(serverMessage, LoadGame.class);
        notificationHandler.loadGame(loadGame);
    }

    public void error(String serverMessage){
        ErrorMessage error = new Gson().fromJson(serverMessage, ErrorMessage.class);
        notificationHandler.error(error);
    }

    public void notification(String serverMessage){
        Notification notification = new Gson().fromJson(serverMessage, Notification.class);
        notificationHandler.notify(notification);
    }

    public void joinPlayer(String authToken, int gameID, ChessGame.TeamColor teamColor) throws IOException {
        var command = new JoinPlayer(authToken, gameID, teamColor);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void joinObserver(String authToken, int gameID) throws IOException {
        var command = new JoinObserver(authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        var command = new MakeMove(authToken, gameID, move);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void resignGame(String authToken, int gameID) throws IOException {
        var command = new Resign(authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void leaveGame(String authToken, int gameID) throws IOException {
        var command = new Leave(authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
