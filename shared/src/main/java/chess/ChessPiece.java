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

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
    }

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
            // TODO: implement all the piece types
            case KING:
                // King can move to any adjacent square that doesn't have
                // a player already there
                temp = kingMoves(board, myPosition);
                possible_moves.addAll(temp);
                break;
            case KNIGHT:
                temp = knightMoves(board, myPosition);
                possible_moves.addAll(temp);
                break;
            case PAWN:
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
                // TODO: throw an error ou qqch
                break;

        }

        return possible_moves;
    }

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
