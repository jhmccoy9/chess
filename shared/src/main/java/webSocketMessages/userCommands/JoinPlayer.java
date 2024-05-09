package websocket.commands;

import chess.ChessGame;

public class JoinPlayer extends UserGameCommand {

    private int gameID;
    private ChessGame.TeamColor color;

    public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor color) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.CONNECT;
        this.color = color;
    }

    public int getGameID() {
        return this.gameID;
    }

    public ChessGame.TeamColor getColor() { return this.color; }
}
