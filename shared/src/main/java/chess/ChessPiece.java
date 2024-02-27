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
        Collection<ChessMove> possibleMoves = new HashSet<ChessMove>(){};
        Collection<ChessMove> temp;

        // go through each of the piece types
        switch (this.type)
        {
            case KING:
                temp = kingMoves(board, myPosition);
                possibleMoves.addAll(temp);
                break;
            case KNIGHT:
                temp = knightMoves(board, myPosition);
                possibleMoves.addAll(temp);
                break;
            case PAWN:
                temp = pawnMoves(board, myPosition);
                possibleMoves.addAll(temp);
                break;
            case BISHOP:
                temp = bishopMoves(board, myPosition);
                possibleMoves.addAll(temp);
                break;
            case ROOK:
                temp = rookMoves(board, myPosition);
                possibleMoves.addAll(temp);
                break;
            case QUEEN:
                temp = rookMoves(board, myPosition);
                temp.addAll(bishopMoves(board, myPosition));
                possibleMoves.addAll(temp);
                break;
            default:
                break;

        }

        return possibleMoves;
    }

    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new HashSet<ChessMove>() {
        };
        // black goes backwards and white goes forwards
        int direction = (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startingRow = (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // first, go through all the straight moves
        int nextRow = row + 2 * direction;
        // if nothing is there or one spot ahead of it, it can go there
        ChessPosition tempPosition = new ChessPosition(nextRow, col);
        ChessPosition directlyAhead = new ChessPosition(row + direction, col);
        if (board.getPiece(directlyAhead) == null) {
            // if it's not at the end, it gets a promotion
            if ((directlyAhead.getRow() == 8 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ||
                    (directlyAhead.getRow() == 1 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK)) {
                ChessMove tempMove = new ChessMove(myPosition, directlyAhead, PieceType.QUEEN);
                possibleMoves.add(tempMove);
                tempMove = new ChessMove(myPosition, directlyAhead, PieceType.BISHOP);
                possibleMoves.add(tempMove);
                tempMove = new ChessMove(myPosition, directlyAhead, PieceType.ROOK);
                possibleMoves.add(tempMove);
                tempMove = new ChessMove(myPosition, directlyAhead, PieceType.KNIGHT);
                possibleMoves.add(tempMove);
            }
            // otherwise it gets nothing
            else {
                ChessMove tempMove = new ChessMove(myPosition, directlyAhead, null);
                possibleMoves.add(tempMove);
            }

            if (row == startingRow && board.getPiece(tempPosition) == null) {
                ChessMove tempMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(tempMove);
            }
        }

        // next, look at the capture options
        // to the left
        int newRow = row + direction;
        int newCol = col - 1;
        // boundary check
        if (newCol >= 1 && newCol <= 8 && newRow >= 1 && newRow <= 8) {
            // make sure it's not null and an enemy
            tempPosition = new ChessPosition(newRow, newCol);
            if (board.getPiece(tempPosition) != null &&
                    board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                // if you'd be at the end, promote, otherwise like normal
                if ((tempPosition.getRow() == 8 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ||
                        (tempPosition.getRow() == 1 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK)) {
                    ChessMove tempMove = new ChessMove(myPosition, tempPosition, PieceType.QUEEN);
                    possibleMoves.add(tempMove);
                    tempMove = new ChessMove(myPosition, tempPosition, PieceType.BISHOP);
                    possibleMoves.add(tempMove);
                    tempMove = new ChessMove(myPosition, tempPosition, PieceType.ROOK);
                    possibleMoves.add(tempMove);
                    tempMove = new ChessMove(myPosition, tempPosition, PieceType.KNIGHT);
                    possibleMoves.add(tempMove);
                } else {
                    ChessMove tempMove = new ChessMove(myPosition, tempPosition, null);
                    possibleMoves.add(tempMove);
                }
            }
        }

        // to the right
        newCol = col + 1;
        // boundary check
        if (newCol >= 1 && newCol <= 8 && newRow >= 1 && newRow <= 8) {
            // make sure it's not null and an enemy
            tempPosition = new ChessPosition(newRow, newCol);
            if (board.getPiece(tempPosition) != null &&
                    board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                // if you'd be at the end, promote, otherwise like normal
                if ((tempPosition.getRow() == 8 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.WHITE) ||
                        (tempPosition.getRow() == 1 && board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK)) {
                    ChessMove tempMove = new ChessMove(myPosition, tempPosition, PieceType.QUEEN);
                    possibleMoves.add(tempMove);
                    tempMove = new ChessMove(myPosition, tempPosition, PieceType.BISHOP);
                    possibleMoves.add(tempMove);
                    tempMove = new ChessMove(myPosition, tempPosition, PieceType.ROOK);
                    possibleMoves.add(tempMove);
                    tempMove = new ChessMove(myPosition, tempPosition, PieceType.KNIGHT);
                    possibleMoves.add(tempMove);
                } else {
                    ChessMove tempMove = new ChessMove(myPosition, tempPosition, null);
                    possibleMoves.add(tempMove);
                }
            }
        }
        return possibleMoves;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> possibleMoves = new HashSet<ChessMove>(){};
        ChessMove tempMove;
        ChessPosition tempPosition;

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
            tempPosition = new ChessPosition(y+directions[i][0], x+directions[i][1]);
            if (x+directions[i][1] >= 1 && x+directions[i][1] <= 8 && y+directions[i][0] >= 1 && y+directions[i][0] <= 8)
            {
                // make the new move

                // make sure that the space is either null or a different color
                if (board.getPiece(tempPosition) == null)
                {
                    tempMove = new ChessMove(myPosition, tempPosition, null);
                    possibleMoves.add(tempMove);
                }
                else if (board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor())
                {
                    tempMove = new ChessMove(myPosition, tempPosition, null);
                    possibleMoves.add(tempMove);
                }

            }
        }




        return possibleMoves;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> possibleMoves = new HashSet<ChessMove>(){};
        ChessMove tempMove;
        ChessPosition tempPosition;
        List<Integer> directions = new ArrayList<>();
        directions.add(-1);
        directions.add(0);
        directions.add(1);
        int x = myPosition.getColumn();
        int y = myPosition.getRow();

        // go through every possible direction combo for the king
        for(int directionX : directions)
        {
            for(int directionY : directions)
            {
                // king can't stay still
                if (!(directionX == 0 && directionY == 0))
                {
                    int newx = directionX + x;
                    int newy = directionY + y;
                    // boundary check
                    if (newx >= 1 && newx <= 8 && newy >= 1 && newy <= 8)
                    {
                        // make the new move
                        tempPosition = new ChessPosition(newy, newx);

                        // make sure that the space is either null or a different color
                        if (board.getPiece(tempPosition) == null)
                        {
                            tempMove = new ChessMove(myPosition, tempPosition, null);
                            possibleMoves.add(tempMove);
                        }
                        else if (board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor())
                        {
                            tempMove = new ChessMove(myPosition, tempPosition, null);
                            possibleMoves.add(tempMove);
                        }

                    }

                }

            }
        }




        return possibleMoves;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> possibleMoves = new HashSet<ChessMove>(){};
        // go in each of the possible four directions until an obstacle is hit or you hit the edge
        //move up
        boolean keepGoing = true;
        for (int row = myPosition.getRow() + 1; (row <= 8) && keepGoing; row++)
        {
            ChessPosition tempPosition = new ChessPosition(row, myPosition.getColumn());
            if (board.getPiece(tempPosition) == null)
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
            }
            else if(board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
                keepGoing = false;
            }
            else
            {
                keepGoing = false;
            }

        }

        // go down
        keepGoing = true;
        for (int row = myPosition.getRow() - 1; (row >= 1) && keepGoing; row--)
        {
            ChessPosition tempPosition = new ChessPosition(row, myPosition.getColumn());
            if (board.getPiece(tempPosition) == null)
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
            }
            else if(board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
                keepGoing = false;
            }
            else
            {
                keepGoing = false;
            }

        }

        // go left
        keepGoing = true;
        for (int col = myPosition.getColumn() - 1; (col >= 1) && keepGoing; col--)
        {
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow(), col);
            if (board.getPiece(tempPosition) == null)
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
            }
            else if(board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
                keepGoing = false;
            }
            else
            {
                keepGoing = false;
            }

        }

        // go right
        keepGoing = true;
        for (int col = myPosition.getColumn() + 1; (col <= 8) && keepGoing; col++)
        {
            ChessPosition tempPosition = new ChessPosition(myPosition.getRow(), col);
            if (board.getPiece(tempPosition) == null)
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
            }
            else if(board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
                keepGoing = false;
            }
            else
            {
                keepGoing = false;
            }

        }


        return possibleMoves;

    }

    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition)
    {
        Collection<ChessMove> possibleMoves = new HashSet<ChessMove>(){};

        // Bishop can move diagonally until it hits another player
        // or a boundary
        // four combinations: slope of 1 and -1 going forward and backward in time

        // going up and to the right
        int y = myPosition.getRow() + 1;
        int x = myPosition.getColumn() + 1;
        boolean keepGoing = true;
        while (keepGoing && x <= 8 && y <= 8) {
            ChessPosition tempPosition = new ChessPosition(y, x);
            if (board.getPiece(tempPosition) == null)
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
            }
            else if(board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
                keepGoing = false;
            }
            else
            {
                keepGoing = false;
            }
            x++;y++;
        }

        // going up and to the left
        y = myPosition.getRow() + 1;
        x = myPosition.getColumn() - 1;
        keepGoing = true;
        while (keepGoing && x >= 1 && y <= 8)
        {
            ChessPosition tempPosition = new ChessPosition(y, x);
            if (board.getPiece(tempPosition) == null)
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
            }
            else if(board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
                keepGoing = false;
            }
            else
            {
                keepGoing = false;
            }

            x--;y++;
        }

        // going down and to the left
        y = myPosition.getRow() - 1;
        x = myPosition.getColumn() - 1;
        keepGoing = true;
        while (keepGoing && x >= 1 && y >= 1)
        {
            ChessPosition tempPosition = new ChessPosition(y, x);
            if (board.getPiece(tempPosition) == null)
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
            }
            else if(board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
                keepGoing = false;
            }
            else
            {
                keepGoing = false;
            }
            x--;y--;
        }

        // going down and to the right
        y = myPosition.getRow() - 1;
        x = myPosition.getColumn() + 1;
        keepGoing = true;
        while (keepGoing && x <= 8 && y >= 1)
        {
            ChessPosition tempPosition = new ChessPosition(y, x);
            if (board.getPiece(tempPosition) == null)
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
            }
            else if(board.getPiece(tempPosition).getTeamColor() != board.getPiece(myPosition).getTeamColor())
            {
                ChessMove newMove = new ChessMove(myPosition, tempPosition, null);
                possibleMoves.add(newMove);
                keepGoing = false;
            }
            else
            {
                keepGoing = false;
            }

            x++;y--;
        }

        return possibleMoves;

    }
}
