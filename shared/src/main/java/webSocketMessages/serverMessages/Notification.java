package webSocketMessages.serverMessages;

import com.google.gson.Gson;

public class Notification extends ServerMessage{

    private String message;

    public Notification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString()
    {
        return this.getMessage();
    }

    public String toJSON()
    {
        return new Gson().toJson(this);
    }
}
