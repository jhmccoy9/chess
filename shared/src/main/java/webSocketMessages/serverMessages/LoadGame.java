package webSocketMessages.serverMessages;

import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGame extends ServerMessage{

    private ChessGame game;
    public LoadGame(ChessGame game)
    {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }

    public String toJSON()
    {
        return new Gson().toJson(this);
    }
}
