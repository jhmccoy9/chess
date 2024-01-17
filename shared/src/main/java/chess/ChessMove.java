package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    ChessPosition startPosition;
    ChessPosition endPosition;
    ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.endPosition = endPosition;
        this.startPosition = startPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition()
    {
        return this.startPosition;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition()
    {
        return this.endPosition;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece()
    {
        return this.promotionPiece;
        //throw new RuntimeException("Not implemented");
    }

    @Override
    public String toString() {
        if (this.getPromotionPiece() == null)
            return "Start: " + this.getStartPosition().toString() + " End: " + this.getEndPosition().toString() + " Promotion Piece: " + "null\n";
        else
            return "Start: " + this.getStartPosition().toString() + " End: " + this.getEndPosition().toString() + " Promotion Piece: " + this.getPromotionPiece().toString() + "\n";

    }



    /*@Override
    public boolean equals(Object move)
    {
        if (this == move)
            return true;
        else if (move == null || getClass() != move.getClass())
            return false;
        else if (this.getEndPosition().equals(((ChessMove) move).getEndPosition()) &&
                 this.getStartPosition().equals(((ChessMove) move).getStartPosition()) &&
                 this.getPromotionPiece().equals(((ChessMove) move).getPromotionPiece()))
        {
            return true;
        }
        else
            return false;
    }

     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) && Objects.equals(endPosition, chessMove.endPosition) && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode()
    {
        if (promotionPiece != null)
            return Objects.hash(startPosition.hashCode(), endPosition.hashCode(), promotionPiece.hashCode());
        else
            return Objects.hash(startPosition.hashCode(), endPosition.hashCode());
    }
}
