package websocket.messages;

public class Notification extends ServerMessage{

    private String notification;
    public Notification(ServerMessageType type, String notification) {
        super(type);
        this.notification = notification;
        this.serverMessageType = ServerMessageType.NOTIFICATION;
    }

    public String getNotification() {
        return notification;
    }
}
