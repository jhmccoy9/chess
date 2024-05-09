package webSocketMessages.serverMessages;

import chess.ChessGame;

public class Error extends ServerMessage{

    private String errorMessage;
    public Error(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
        this.serverMessageType = ServerMessageType.ERROR;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
