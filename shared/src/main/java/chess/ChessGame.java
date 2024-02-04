package chess;

import java.util.Collection;
import java.util.HashSet;
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

    public ChessGame()
    {
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
    public Collection<ChessMove> validMoves(ChessPosition startPosition)
    {
        // step 1: if there is no piece there, return null
        if (this.getBoard().getPiece(startPosition) == null)
            return null;

        // step 2: get a list of all possible moves the piece could make: regardless of it would put
        // the king in check
        Collection<ChessMove> possible_moves =
                this.getBoard().getPiece(startPosition).pieceMoves(this.getBoard(), startPosition);

        // step 3: filter out anything that would leave the king in check. Do this by making each move
        // on a temporary board and then adding the move to a new collection if it's valid
        // deep copy
        ChessBoard old_board = new ChessBoard(this.getBoard());
        Collection<ChessMove> valid_moves = new HashSet<>();
        for (ChessMove move : possible_moves)
        {
            // make the move. if it's invalid, keep going
            boolean bad_move = false;
            try
            {
                this.makeMove(move);
            }
            catch (InvalidMoveException e1)
            {
                bad_move = true;
            }

            // if it is valid, reset the board to its normal state, add the move
            // to the valid moves set, and keep going. you also need to invert the color
            // of the team playing since it just made a successful move
            if (!bad_move)
            {
                ChessMove good_move = new ChessMove(move);
                valid_moves.add(good_move);
                ChessBoard reset_board = new ChessBoard(old_board);
                this.setBoard(reset_board);
                this.whoseTurn = (this.whoseTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
            }
        }

        return valid_moves;
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
        TeamColor current_color = board.getPiece(start).getTeamColor();

        // get the piece
        ChessPiece piece = this.board.getPiece(start);

        // get the piece that's about to be overwritten, in case you have to undo the move
        ChessPiece overwritten_piece = this.board.getPiece(end);

        // make sure that this move is in the set of valid moves for that piece
        Collection<ChessMove> possible_moves = piece.pieceMoves(this.board, start);
        if (!(possible_moves.contains(move)))
        {
            throw new InvalidMoveException("Not a possible move for this piece");
        }

        // make sure that the move being made is for the right color
        if (this.whoseTurn != current_color)
        {
            throw new InvalidMoveException("Not this color's turn!");
        }

        // if it's getting promoted, make a new piece
        ChessPiece new_piece;
        if (move.getPromotionPiece() != null)
            new_piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        else
            new_piece = piece;

        // make a new temporary board based off of the current board
        ChessBoard new_board = new ChessBoard(this.getBoard());
        ChessBoard old_board = this.getBoard();
        this.setBoard(new_board);

        // put the piece in its new spot
        board.addPiece(end, new_piece);

        ChessPiece dead_piece = null;

        // make it null where it used to be
        board.addPiece(start, dead_piece);
        this.whoseTurn = (this.whoseTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        // make sure this doesn't result in a check. if it does, undo the move and throw
        // the exception
        if (isInCheck(current_color))
        {
            // change it back to the original team's turn
            this.whoseTurn = (this.whoseTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
            setBoard(old_board);

            throw new InvalidMoveException("Puts king in check");
        }

        return;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor)
    {
        ChessPosition king_position = null;
        // get the position of teamColor's king
        for (int i = 1; i <= 8; i++)
        {
            for (int j = 1; j <= 8; j++)
            {
                ChessPosition temp_position = new ChessPosition(i, j);
                ChessPiece temp_piece = board.getPiece(temp_position);
                if (temp_piece != null) {
                    // if it's a king of the correct color
                    if (temp_piece.getPieceType() == ChessPiece.PieceType.KING &&
                            temp_piece.getTeamColor() == teamColor) {
                        king_position = new ChessPosition(i, j);
                        // end the loop
                        i = j = 9;
                    }
                }
            }
        }
        if (king_position == null)
        {
            return false; // can't be in check if you don't have a king ;)
        }


        TeamColor enemyColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        // go through all the pieces of the enemy color
        for (int i = 1; i <= 8; i++)
        {
            for (int j = 1; j <= 8; j++)
            {
                ChessPosition temp_position = new ChessPosition(i, j);
                ChessPiece temp_piece = board.getPiece(temp_position);
                // make sure it's a valid piece
                if (temp_piece != null)
                {
                    // make sure it's the right color
                    if (temp_piece.getTeamColor() == enemyColor)
                    {
                        // calculate every possible move for the enemy piece
                        Collection<ChessMove> possible_moves = temp_piece.pieceMoves(board, temp_position);

                        // if any of those moves' end position is the position of teamColor's king, it is in check
                        for (ChessMove move : possible_moves)
                        {
                            if (move.getEndPosition().equals(king_position))
                            {
                                return true;
                            }
                        }

                    }
                }

            }

        }
        return false;
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
    public void setBoard(ChessBoard board) {this.board = board;}

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {return this.board;}
}
