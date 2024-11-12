package ui.websocket;

public interface NotificationHandler {
    void loadGame(LoadGame game);
    void error(ErrorMessage error);
    void notify(Notification notification);
}
