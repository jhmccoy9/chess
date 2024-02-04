package chess;

import java.sql.Array;
import java.util.*;


/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private PieceType type;
    private ChessGame.TeamColor color;
    private boolean moved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
        this.moved = false;
    }

    public ChessPiece(ChessPiece old)
    {
        this.type = old.getPieceType();
        this.color = old.getTeamColor();
        this.moved = old.hasMoved();
    }

    // getters and setters for moved
    public boolean hasMoved() {return this.moved;}
    public void setMoved() {this.moved = true;}

    @Override
    public String toString() {
        // lowercase is black, uppercase is white
        if (this.color == ChessGame.TeamColor.BLACK)
        {
            if (this.type == PieceType.PAWN)
            {
                return "p";
            } else if (this.type == PieceType.ROOK) {
                return "r";
            } else if (this.type == PieceType.KNIGHT) {
                return "n";
            } else if (this.type == PieceType.BISHOP) {
                return "b";
            } else if (this.type == PieceType.KING) {
                return "k";
            } else if (this.type == PieceType.QUEEN) {
                return "q";
            }
        }
        else if (this.color == ChessGame.TeamColor.WHITE)
        {
            if (this.type == PieceType.PAWN)
            {
                return "P";
            } else if (this.type == PieceType.ROOK) {
                return "R";
            } else if (this.type == PieceType.KNIGHT) {
                return "N";
            } else if (this.type == PieceType.BISHOP) {
                return "B";
            } else if (this.type == PieceType.KING) {
                return "K";
            } else if (this.type == PieceType.QUEEN) {
                return "Q";
            }
        }
        return "x";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.color);
    }

    @Override
    public boolean equals(Object piece) {
        if (this == piece)
            return true;
        else if (piece == null || getClass() != piece.getClass())
            return false;
        else if (this.color == ((ChessPiece) piece).color && this.type == ((ChessPiece) piece).type)
        {
            return true;
        }
        else
        {
            return false;
        }
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
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
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
        Collection<ChessMove> possible_moves = new HashSet<ChessMove>(){};
        Collection<ChessMove> temp;

        // go through each of the piece types
        switch (this.type)
        {
            case KING:
                temp = kingMoves(board, myPosition);
                possible_moves.addAll(temp);
                break;
            case KNIGHT:
                temp = knightMoves(board, myPosition);
                possible_moves.addAll(temp);
                break;
            case PAWN:
                temp = pawnMoves(board, myPosition);
                possible_moves.addAll(temp);
                break;
            case BISHOP:
                temp = bishopMoves(board, myPosition);
                possible_moves.addAll(temp);
                break;
            case ROOK:
                temp = rookMoves(board, myPosition);
                possible_moves.addAll(temp);
                break;
            case QUEEN:
                temp = rookMoves(board, myPosition);
                temp.addAll(bishopMoves(board, myPosition));
                possible_moves.addAll(temp);
                break;
            default:
                break;

        }

        return possible_moves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possible_moves = new HashSet<ChessMove>() {
        };
        // black goes backwards and white goes forwards
        int direction = (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int starting_row = (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // first, go through all the straight moves
        int next_row = row + 2 * direction;
        // if nothing is there or one spot ahead of it, it can go there
        ChessPosition temp_position = new ChessPosition(next_row, col);
        ChessPosition directly_ahead = new ChessPosition(row + direction, col);
        if (board.getPiece(directly_ahead) == null) {
            // if it's not at the end, it gets a promotion
            if ((directly_ahead.getRow() == 8 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ||
                    (directly_ahead.getRow() == 1 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK)) {
                ChessMove temp_move = new ChessMove(myPosition, directly_ahead, PieceType.QUEEN);
                possible_moves.add(temp_move);
                temp_move = new ChessMove(myPosition, directly_ahead, PieceType.BISHOP);
                possible_moves.add(temp_move);
                temp_move = new ChessMove(myPosition, directly_ahead, PieceType.ROOK);
                possible_moves.add(temp_move);
                temp_move = new ChessMove(myPosition, directly_ahead, PieceType.KNIGHT);
                possible_moves.add(temp_move);
            }
            // otherwise it gets nothing
            else {
                ChessMove temp_move = new ChessMove(myPosition, directly_ahead, null);
                possible_moves.add(temp_move);
            }

            if (row == starting_row && board.getPiece(temp_position) == null) {
                ChessMove temp_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(temp_move);
            }
        }

        // next, look at the capture options
        // to the left
        int new_row = row + direction;
        int new_col = col - 1;
        // boundary check
        if (new_col >= 1 && new_col <= 8 && new_row >= 1 && new_row <= 8) {
            // make sure it's not null and an enemy
            temp_position = new ChessPosition(new_row, new_col);
            if (board.getPiece(temp_position) != null &&
                    board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                // if you'd be at the end, promote, otherwise like normal
                if ((temp_position.getRow() == 8 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ||
                        (temp_position.getRow() == 1 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK)) {
                    ChessMove temp_move = new ChessMove(myPosition, temp_position, PieceType.QUEEN);
                    possible_moves.add(temp_move);
                    temp_move = new ChessMove(myPosition, temp_position, PieceType.BISHOP);
                    possible_moves.add(temp_move);
                    temp_move = new ChessMove(myPosition, temp_position, PieceType.ROOK);
                    possible_moves.add(temp_move);
                    temp_move = new ChessMove(myPosition, temp_position, PieceType.KNIGHT);
                    possible_moves.add(temp_move);
                } else {
                    ChessMove temp_move = new ChessMove(myPosition, temp_position, null);
                    possible_moves.add(temp_move);
                }
            }
        }

        // to the right
        new_col = col + 1;
        // boundary check
        if (new_col >= 1 && new_col <= 8 && new_row >= 1 && new_row <= 8) {
            // make sure it's not null and an enemy
            temp_position = new ChessPosition(new_row, new_col);
            if (board.getPiece(temp_position) != null &&
                    board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                // if you'd be at the end, promote, otherwise like normal
                if ((temp_position.getRow() == 8 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ||
                        (temp_position.getRow() == 1 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK)) {
                    ChessMove temp_move = new ChessMove(myPosition, temp_position, PieceType.QUEEN);
                    possible_moves.add(temp_move);
                    temp_move = new ChessMove(myPosition, temp_position, PieceType.BISHOP);
                    possible_moves.add(temp_move);
                    temp_move = new ChessMove(myPosition, temp_position, PieceType.ROOK);
                    possible_moves.add(temp_move);
                    temp_move = new ChessMove(myPosition, temp_position, PieceType.KNIGHT);
                    possible_moves.add(temp_move);
                } else {
                    ChessMove temp_move = new ChessMove(myPosition, temp_position, null);
                    possible_moves.add(temp_move);
                }
            }
        }
        return possible_moves;
    }


    /*
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> possible_moves = new HashSet<ChessMove>(){};
        ChessMove temp_move;
        ChessPosition temp_position;
        int x = myPosition.getColumn();
        int y = myPosition.getRow();

        // white starts from bottom and moves up, black starts from top and moves down
        int direction = board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;
        // see if spot immediately in front of it is free
        temp_position = new ChessPosition(y + direction, x);
        // make sure it's empty and a valid spot
        if (y + direction >= 1 && y + direction <= 8)
        {
            if (board.getPiece(temp_position) == null) {
                // if it's the end of the board, add in the promotion pieces, otherwise null
                if ((y + direction == 8 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ||
                    (y + direction == 1 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK))
                {
                    temp_move = new ChessMove(myPosition, temp_position, PieceType.QUEEN);
                    possible_moves.add(temp_move);
                    temp_move = new ChessMove(myPosition, temp_position, PieceType.KNIGHT);
                    possible_moves.add(temp_move);
                    temp_move = new ChessMove(myPosition, temp_position, PieceType.ROOK);
                    possible_moves.add(temp_move);
                    temp_move = new ChessMove(myPosition, temp_position, PieceType.BISHOP);
                    possible_moves.add(temp_move);
                }
                else
                {
                    temp_move = new ChessMove(myPosition, temp_position, null);
                    possible_moves.add(temp_move);
                }
            }
        }

        // also, see if it can move 2 spots in the first position
        if (y == 2 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE ||
            y == 7 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK)
        {
            // check the old temp position too. it can't move two up if there's still one in front of it
            if (board.getPiece(temp_position) == null)
            {
                temp_position = new ChessPosition(y + direction * 2, x);
                if (board.getPiece(temp_position) == null) {
                    temp_move = new ChessMove(myPosition, temp_position, null);
                    possible_moves.add(temp_move);
                }
            }
        }

        // look for possible captures
        // need to check both sides
        temp_position = new ChessPosition(y + direction, x + 1);
        // check for valid spot
        if (y+direction >= 1 && y+direction <= 8 && x + 1 <= 8 && x + 1 >= 1)
        {
            // check to make sure it's an enemy
            if (board.getPiece(temp_position) != null)
            {
                if (board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor())
                {
                    temp_move = new ChessMove(myPosition, temp_position, null);
                    possible_moves.add(temp_move);
                }
            }
        }
        temp_position = new ChessPosition(y + direction, x - 1);
        // check for valid spot
        if (y+direction >= 1 && y+direction <= 8 && x - 1 <= 8 && x - 1 >= 1) {
            // check to make sure it's an enemy
            if (board.getPiece(temp_position) != null) {
                if (board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                    // make sure it's empty and a valid spot
                    if (y + direction >= 1 && y + direction <= 8) {
                        // if it's the end of the board, add in the promotion pieces, otherwise null
                        if ((y + direction == 8 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ||
                                (y + direction == 1 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK)) {
                            temp_move = new ChessMove(myPosition, temp_position, PieceType.QUEEN);
                            possible_moves.add(temp_move);
                            temp_move = new ChessMove(myPosition, temp_position, PieceType.KNIGHT);
                            possible_moves.add(temp_move);
                            temp_move = new ChessMove(myPosition, temp_position, PieceType.ROOK);
                            possible_moves.add(temp_move);
                            temp_move = new ChessMove(myPosition, temp_position, PieceType.BISHOP);
                            possible_moves.add(temp_move);
                        } else {
                            temp_move = new ChessMove(myPosition, temp_position, null);
                            possible_moves.add(temp_move);
                        }
                    }
                }
            }
        }
        return possible_moves;
    }*/


    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> possible_moves = new HashSet<ChessMove>(){};
        ChessMove temp_move;
        ChessPosition temp_position;

        // a knight has at most 8 possible moves
        int[][] directions = {
                {2,1},
                {2,-1},
                {1,2},
                {1,-2},
                {-2,-1},
                {-1,-2},
                {-1,2},
                {-2,1}
        };
        int x = myPosition.getColumn();
        int y = myPosition.getRow();
        // go through every move combo and determine its suitability
        for (int i = 0; i < directions.length; i++)
        {
            temp_position = new ChessPosition(y+directions[i][0], x+directions[i][1]);
            if (x+directions[i][1] >= 1 && x+directions[i][1] <= 8 && y+directions[i][0] >= 1 && y+directions[i][0] <= 8)
            {
                // make the new move

                // make sure that the space is either null or a different color
                if (board.getPiece(temp_position) == null)
                {
                    temp_move = new ChessMove(myPosition, temp_position, null);
                    possible_moves.add(temp_move);
                }
                else if (board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor())
                {
                    temp_move = new ChessMove(myPosition, temp_position, null);
                    possible_moves.add(temp_move);
                }

            }
        }




        return possible_moves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> possible_moves = new HashSet<ChessMove>(){};
        ChessMove temp_move;
        ChessPosition temp_position;
        List<Integer> directions = new ArrayList<>();
        directions.add(-1);
        directions.add(0);
        directions.add(1);
        int x = myPosition.getColumn();
        int y = myPosition.getRow();

        // go through every possible direction combo for the king
        for(int direction_x : directions)
        {
            for(int direction_y : directions)
            {
                // king can't stay still
                if (!(direction_x == 0 && direction_y == 0))
                {
                    int newx = direction_x + x;
                    int newy = direction_y + y;
                    // boundary check
                    if (newx >= 1 && newx <= 8 && newy >= 1 && newy <= 8)
                    {
                        // make the new move
                        temp_position = new ChessPosition(newy, newx);

                        // make sure that the space is either null or a different color
                        if (board.getPiece(temp_position) == null)
                        {
                            temp_move = new ChessMove(myPosition, temp_position, null);
                            possible_moves.add(temp_move);
                        }
                        else if (board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor())
                        {
                            temp_move = new ChessMove(myPosition, temp_position, null);
                            possible_moves.add(temp_move);
                        }

                    }

                }

            }
        }




        return possible_moves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> possible_moves = new HashSet<ChessMove>(){};
        // go in each of the possible four directions until an obstacle is hit or you hit the edge
        //move up
        boolean keep_going = true;
        for (int row = myPosition.getRow() + 1; (row <= 8) && keep_going; row++)
        {
            ChessPosition temp_position = new ChessPosition(row, myPosition.getColumn());
            if (board.getPiece(temp_position) == null)
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
            }
            else if(board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
                keep_going = false;
            }
            else
            {
                keep_going = false;
            }

        }

        // go down
        keep_going = true;
        for (int row = myPosition.getRow() - 1; (row >= 1) && keep_going; row--)
        {
            ChessPosition temp_position = new ChessPosition(row, myPosition.getColumn());
            if (board.getPiece(temp_position) == null)
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
            }
            else if(board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
                keep_going = false;
            }
            else
            {
                keep_going = false;
            }

        }

        // go left
        keep_going = true;
        for (int col = myPosition.getColumn() - 1; (col >= 1) && keep_going; col--)
        {
            ChessPosition temp_position = new ChessPosition(myPosition.getRow(), col);
            if (board.getPiece(temp_position) == null)
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
            }
            else if(board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
                keep_going = false;
            }
            else
            {
                keep_going = false;
            }

        }

        // go right
        keep_going = true;
        for (int col = myPosition.getColumn() + 1; (col <= 8) && keep_going; col++)
        {
            ChessPosition temp_position = new ChessPosition(myPosition.getRow(), col);
            if (board.getPiece(temp_position) == null)
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
            }
            else if(board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
                keep_going = false;
            }
            else
            {
                keep_going = false;
            }

        }


        return possible_moves;

    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> possible_moves = new HashSet<ChessMove>(){};

        // Bishop can move diagonally until it hits another player
        // or a boundary
        // four combinations: slope of 1 and -1 going forward and backward in time

        // going up and to the right
        int y = myPosition.getRow() + 1;
        int x = myPosition.getColumn() + 1;
        boolean keep_going = true;
        while (keep_going && x <= 8 && y <= 8) {
            ChessPosition temp_position = new ChessPosition(y, x);
            if (board.getPiece(temp_position) == null)
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
            }
            else if(board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
                keep_going = false;
            }
            else
            {
                keep_going = false;
            }
            x++;y++;
        }

        // going up and to the left
        y = myPosition.getRow() + 1;
        x = myPosition.getColumn() - 1;
        keep_going = true;
        while (keep_going && x >= 1 && y <= 8)
        {
            ChessPosition temp_position = new ChessPosition(y, x);
            if (board.getPiece(temp_position) == null)
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
            }
            else if(board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
                keep_going = false;
            }
            else
            {
                keep_going = false;
            }

            x--;y++;
        }

        // going down and to the left
        y = myPosition.getRow() - 1;
        x = myPosition.getColumn() - 1;
        keep_going = true;
        while (keep_going && x >= 1 && y >= 1)
        {
            ChessPosition temp_position = new ChessPosition(y, x);
            if (board.getPiece(temp_position) == null)
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
            }
            else if(board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
                keep_going = false;
            }
            else
            {
                keep_going = false;
            }
            x--;y--;
        }

        // going down and to the right
        y = myPosition.getRow() - 1;
        x = myPosition.getColumn() + 1;
        keep_going = true;
        while (keep_going && x <= 8 && y >= 1)
        {
            ChessPosition temp_position = new ChessPosition(y, x);
            if (board.getPiece(temp_position) == null)
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
            }
            else if(board.getPiece(temp_position).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove new_move = new ChessMove(myPosition, temp_position, null);
                possible_moves.add(new_move);
                keep_going = false;
            }
            else
            {
                keep_going = false;
            }

            x++;y--;
        }

        return possible_moves;

    }
}
