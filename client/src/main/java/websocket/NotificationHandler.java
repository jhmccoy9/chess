package websocket;

import ui.Utilities;
import webSocketMessages.serverMessages.*;
import webSocketMessages.serverMessages.Error;

public class NotificationHandler {
    void notify(Notification notification)
    {
        System.out.println((notification.getMessage() + "\n>>> "));
    }

    void error(Error error)
    {
        System.out.println(error.getErrorMessage() + "\n>>> ");
    }

    void loadGame(LoadGame loadGame)
    {
        System.out.println("The chessboard has been updated");
        Utilities.printChessBoard(loadGame.getGame().getBoard());
        System.out.printf("\n>>> ");
    }
}
