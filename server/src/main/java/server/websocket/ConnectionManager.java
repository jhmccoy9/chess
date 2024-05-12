package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, List<Connection>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String visitorName, Session session, Connection.Role role) throws IOException {
        // create a temporary Integer since you need it for the hash map
        Integer gameIDInteger = Integer.valueOf(gameID);
        // get the old list of connections on that specific game, and add the new connection
        List<Connection> tempList = connections.get(gameIDInteger);
        if (tempList == null)
        {
            tempList = new ArrayList<>();
        }
        // TODO: make sure that role isn't already filled

        // make the new connection, based on the provided information
        var connection = new Connection(visitorName, session, role);

        tempList.add(connection);
        connections.put(Integer.valueOf(gameIDInteger), tempList);

        // send the game to the new person
        LoadGame loadGame = new LoadGame(new ChessGame());
        if (connection.session.isOpen())
            connection.send(loadGame.toJSON());
    }

    public void removePlayer(int gameID, String visitorName)
    {
        Integer gameIDInteger = Integer.valueOf(gameID);
        List<Connection> tempList = connections.get(gameIDInteger);
        for (Connection connection : tempList)
        {
            if (connection.visitorName.equals(visitorName))
            {
                tempList.remove(connection);
            }
        }
        connections.put(gameIDInteger, tempList);
    }

    public void broadcast(int gameID, ServerMessage serverMessage, Collection<String> exclusions) throws IOException {
        // get all the connections on a certain game
        List<Connection> broadcastList = connections.get(Integer.valueOf(gameID));
        if (broadcastList == null)
            return;

        // go through all the connections
        for (Connection recipient : broadcastList)
        {
            // if the session is still active, send the notification
            if (recipient.session.isOpen())
            {
                if (!(exclusions.contains(recipient.visitorName)))
                {
                    // must be sent as a json object, not a string
                    recipient.send(serverMessage.toJSON());
                }
            }
            // otherwise, take them off the list and update it in the connections manager
            else
            {
                broadcastList.remove(recipient);
                connections.put(Integer.valueOf(gameID), broadcastList);
            }
        }
    }

}

