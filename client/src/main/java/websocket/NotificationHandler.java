package websocket;

import webSocketMessages.serverMessages.*;

public class NotificationHandler {
    void notify(Notification notification)
    {
        System.out.println((notification.toString()));
    }
}
