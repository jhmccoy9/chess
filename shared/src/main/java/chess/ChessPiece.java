package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private PieceType type;
    private ChessGame.TeamColor color;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
    }


    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }



    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition)
    {
        Set<ChessMove> possible_moves = new HashSet<ChessMove>() {};

        // go through each of the piece types
        switch (this.type)
        {
            // TODO: implement all the piece types
            case KING:
                // King can move to any adjacent square that doesn't have
                // a player already there
                break;
            case BISHOP:
                // Bishop can move diagonally until it hits another player
                // or a boundary
                // four combinations: slope of 1 and -1 going forward and backward in time
                // y_2 - y_1 = m(x_2 - x_1)
                // start_y - end_y = start_x - (go through all possible x's)

                //TODO: must implement different slopes and directions

                // going up and to the right
                int y = myPosition.getRow() + 1;
                int x = myPosition.getColumn() + 1;
                while (x <= 8 && y <= 8)
                {
                    ChessPosition temp_position = new ChessPosition(y, x);
                    if(board.getPiece(temp_position) == null)
                    {
                        ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                        possible_moves.add(new_move);
                    }
                    x++;y++;
                }

                // going up and to the left
                y = myPosition.getRow() + 1;
                x = myPosition.getColumn() - 1;
                while (x >= 1 && y <= 8)
                {
                    ChessPosition temp_position = new ChessPosition(y, x);
                    if(board.getPiece(temp_position) == null)
                    {
                        ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                        possible_moves.add(new_move);
                    }
                    x--;y++;
                }

                break;
        }

//        if (possible_moves.isEmpty())
//        {
//            throw new RuntimeException("no valid moves rn");
//        }
        return possible_moves;
        //throw new RuntimeException("Not implemented");
    }
}
