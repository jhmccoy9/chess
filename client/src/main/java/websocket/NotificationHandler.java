package websocket;

import webSocketMessages.serverMessages.*;
import webSocketMessages.serverMessages.Error;

public class NotificationHandler {
    void notify(Notification notification)
    {
        System.out.println((notification.toString()));
    }

    void error(Error error)
    {
        System.out.println(error.getErrorMessage());
    }

    void loadGame(LoadGame loadGame)
    {
        // TODO: you may need to fix this
        System.out.println(loadGame.toString());
    }
}
