package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayer extends UserGameCommand {

    private int gameID;
    private ChessGame.TeamColor playerColor;

    public JoinPlayer(String authToken, int gameID, ChessGame.TeamColor color) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.JOIN_PLAYER;
        this.playerColor = color;
    }

    public int getGameID() {
        return this.gameID;
    }

    public ChessGame.TeamColor getColor() { return this.playerColor; }
}
