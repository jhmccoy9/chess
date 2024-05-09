package websocket;

import webSocketMessages.serverMessages.*;

public interface NotificationHandler {
    void notify(Notification notification);
}
