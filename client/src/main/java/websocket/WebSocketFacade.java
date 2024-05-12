package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.*;
import webSocketMessages.serverMessages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;


    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                // TODO: you could tweak this to make it accept all kinds of messages--json files, even
                // in reality, this should probably just be used for notifications. I bet there's a better
                // way to handle actual messages/data being passed. Actually, I'm not sure I still agree with that

                @Override
                public void onMessage(String jsonMessage) {
                    System.out.println(jsonMessage);
                    ServerMessage notification = new Gson().fromJson(jsonMessage, ServerMessage.class);
                    switch (notification.getServerMessageType())
                    {
                        case NOTIFICATION -> notificationHandler.notify(new Gson().fromJson(jsonMessage, Notification.class));
                        case ERROR -> notificationHandler.error(new Gson().fromJson(jsonMessage, Error.class));
                        case LOAD_GAME -> notificationHandler.loadGame(new Gson().fromJson(jsonMessage, LoadGame.class));
                        case null, default -> System.out.println("Error: unknown message type from server");
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void enterGame(String authToken, int gameID, ChessGame.TeamColor color) throws ResponseException {
        try {
            var action = new JoinPlayer(authToken, gameID, color);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leaveGame(String authToken, int gameID) throws ResponseException {
        try {
            var action = new Leave(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            this.session.close();
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

}

