package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String visitorName;
    public Session session;
    public Role role;



    public Connection(String visitorName, Session session, Role role) {
        this.visitorName = visitorName;
        this.session = session;
        this.role = role;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    public enum Role
    {
        PLAYER_WHITE,
        PLAYER_BLACK,
        OBSERVER
    }
}