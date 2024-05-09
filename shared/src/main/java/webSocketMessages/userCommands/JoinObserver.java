package websocket.commands;

public class JoinObserver extends UserGameCommand {

    private int gameID;

    public JoinObserver(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.CONNECT;
    }

    public int getGameID() {
        return this.gameID;
    }
}
