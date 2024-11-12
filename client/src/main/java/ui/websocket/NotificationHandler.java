package ui.websocket;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;

public interface NotificationHandler {
    void loadGame(LoadGame game);
    void error(ErrorMessage error);
    void notify(Notification notification);
}
