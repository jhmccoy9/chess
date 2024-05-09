package websocket.messages;

import chess.ChessGame;

public class LoadGame extends ServerMessage{

    private ChessGame game;
    public LoadGame(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
        this.serverMessageType = ServerMessageType.LOAD_GAME;
    }

    public ChessGame getGame() {
        return game;
    }
}
