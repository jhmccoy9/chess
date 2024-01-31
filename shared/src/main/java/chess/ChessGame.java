package chess;

import java.util.Collection;
import java.util.Stack;
import java.io.*;
/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private Collection<ChessMove> moves;
    private ChessGame.TeamColor whoseTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.moves = new Stack<>();
        this.whoseTurn = TeamColor.WHITE;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() { return this.whoseTurn; }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) { this.whoseTurn = team; }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // for now, don't do anything about verifying that this wouldn't put you in check
        //TODO: check for check
        return board.getPiece(startPosition).pieceMoves(this.board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // get the start position and end position
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        // get the piece
        ChessPiece piece = board.getPiece(start);

        // if it's getting promoted, make a new piece
        ChessPiece new_piece;
        if (move.getPromotionPiece() != null)
            new_piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        else
            new_piece = piece;

        // put the piece in its new spot
        board.addPiece(end, new_piece);

        ChessPiece dead_piece = null;

        // make it null where it used to be
        board.addPiece(start, dead_piece);
        return;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
