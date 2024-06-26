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
    private final Collection<ChessMove> moves;
    private ChessGame.TeamColor whoseTurn;

    private boolean isOver;

    public ChessGame()
    {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.moves = new Stack<>();
        this.whoseTurn = TeamColor.WHITE;
        this.isOver = false;
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
        TeamColor oldColor = this.whoseTurn;

        // step 1: if there is no piece there, return null
        if (this.getBoard().getPiece(startPosition) == null)
            return null;

        // step 2: get a list of all possible moves the piece could make: regardless of it would put
        // the king in check
        Collection<ChessMove> possibleMoves =
                this.getBoard().getPiece(startPosition).pieceMoves(this.getBoard(), startPosition);

        // step 3: filter out anything that would leave the king in check. Do this by making each move
        // on a temporary board and then adding the move to a new collection if it's valid
        // deep copy
        ChessBoard oldBoard = new ChessBoard(this.getBoard());
        Collection<ChessMove> validMoves = new HashSet<>();
        for (ChessMove move : possibleMoves)
        {
            // make the move. if it's invalid, keep going
            boolean badMove = false;
            try
            {
                this.makeMove(move);
            }
            catch (InvalidMoveException e1)
            {
                // if it's not that color's turn, change the color and try again
                if (e1.toString().equals("chess.InvalidMoveException: Not this color's turn!"))
                {
                    try
                    {
                        this.whoseTurn = (this.whoseTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
                        this.makeMove(move);
                    }
                    catch (InvalidMoveException e2)
                    {
                        // if it's still a bad move, mark it
                        badMove  = true;
                    }
                    finally
                    {
                        // either way, make sure you don't mess up the color
                        this.whoseTurn = oldColor;
                    }
                }
                else // if the error is because of something else, there is a legitimate error
                {
                    this.whoseTurn = oldColor;
                    badMove = true;
                }
            }

            // if it is valid, reset the board to its normal state, add the move
            // to the valid moves set, and keep going. you also need to invert the color
            // of the team playing since it just made a successful move
            if (!badMove)
            {
                ChessMove goodMove = new ChessMove(move);
                validMoves.add(goodMove);
                ChessBoard resetBoard = new ChessBoard(oldBoard);
                this.setBoard(resetBoard);
                this.whoseTurn = (this.whoseTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
            }
        }

        return validMoves;
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
        TeamColor currentColor = board.getPiece(start).getTeamColor();

        // get the piece
        ChessPiece piece = this.board.getPiece(start);

        // get the piece that's about to be overwritten, in case you have to undo the move
        ChessPiece overwrittenPiece = this.board.getPiece(end);

        // make sure that this move is in the set of valid moves for that piece
        Collection<ChessMove> possibleMoves = piece.pieceMoves(this.board, start);
        if (!(possibleMoves.contains(move)))
        {
            throw new InvalidMoveException("Not a possible move for this piece");
        }

        // make sure that the move being made is for the right color
        if (this.whoseTurn != currentColor)
        {
            throw new InvalidMoveException("Not this color's turn!");
        }

        // if it's getting promoted, make a new piece
        ChessPiece newPiece;
        if (move.getPromotionPiece() != null)
            newPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        else
            newPiece = piece;

        // make a new temporary board based off of the current board
        ChessBoard newBoard = new ChessBoard(this.getBoard());
        ChessBoard oldBoard = this.getBoard();
        this.setBoard(newBoard);

        // put the piece in its new spot
        board.addPiece(end, newPiece);

        ChessPiece deadPiece = null;

        // make it null where it used to be
        board.addPiece(start, deadPiece);
        this.whoseTurn = (this.whoseTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        // make sure this doesn't result in a check. if it does, undo the move and throw
        // the exception
        if (isInCheck(currentColor))
        {
            // change it back to the original team's turn
            this.whoseTurn = (this.whoseTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
            setBoard(oldBoard);

            throw new InvalidMoveException("Puts king in check");
        }

        // see if the opposing team is now in checkmate or stalemate. If so, toggle the isOver boolean
        if (isInCheckmate(this.whoseTurn) || isInStalemate(this.whoseTurn))
            this.isOver = true;

    }

    public boolean isOver() { return isOver; }

    public void forceGameOver() { this.isOver = true; }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor)
    {
        ChessPosition kingPosition = null;
        // get the position of teamColor's king
        for (int i = 1; i <= 8; i++)
        {
            for (int j = 1; j <= 8; j++)
            {
                ChessPosition tempPosition = new ChessPosition(i, j);
                ChessPiece tempPiece = board.getPiece(tempPosition);
                if (tempPiece != null) {
                    // if it's a king of the correct color
                    if (tempPiece.getPieceType() == ChessPiece.PieceType.KING &&
                            tempPiece.getTeamColor() == teamColor) {
                        kingPosition = new ChessPosition(i, j);
                        // end the loop
                        i = j = 9;
                    }
                }
            }
        }
        if (kingPosition == null)
        {
            return false; // can't be in check if you don't have a king ;)
        }


        TeamColor enemyColor = teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
        // go through all the pieces of the enemy color
        for (int i = 1; i <= 8; i++)
        {
            for (int j = 1; j <= 8; j++)
            {
                ChessPosition tempPosition = new ChessPosition(i, j);
                ChessPiece tempPiece = board.getPiece(tempPosition);
                // make sure it's a valid piece
                if (tempPiece != null)
                {
                    // make sure it's the right color
                    if (tempPiece.getTeamColor() == enemyColor)
                    {
                        // calculate every possible move for the enemy piece
                        Collection<ChessMove> possibleMoves = tempPiece.pieceMoves(board, tempPosition);

                        // if any of those moves' end position is the position of teamColor's king, it is in check
                        for (ChessMove move : possibleMoves)
                        {
                            if (move.getEndPosition().equals(kingPosition))
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
    public boolean isInCheckmate(TeamColor teamColor)
    {
        // if it's in check and in stalemate, it's in checkmate
        return (this.isInStalemate(teamColor) && this.isInCheck(teamColor));
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // find the king of that color
        ChessPiece king = null;
        ChessPosition kingPosition = null;
        for (int i = 1; i <= 8; i++)
        {
            for (int j = 1; j <= 8; j++)
            {
                ChessPosition tempPosition = new ChessPosition(i, j);
                ChessPiece tempPiece = this.getBoard().getPiece(tempPosition);
                if (tempPiece != null &&
                    tempPiece.getPieceType() == ChessPiece.PieceType.KING &&
                    tempPiece.getTeamColor() == teamColor)
                {
                    king = tempPiece;
                    kingPosition = tempPosition;
                    // break
                    i = j = 9;
                }

            }

        }

        // can't be in stalemate if you don't have a king ;)
        if (king == null)
            return false;

        // get all the valid moves
        Collection<ChessMove> validMoves = this.validMoves(kingPosition);

        // if there are no valid moves, it's  in stalemate
        return validMoves.isEmpty();
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
