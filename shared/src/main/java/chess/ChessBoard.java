package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    // 2D array of pieces: the actual chessboard itself
    private ChessPiece[][] board;

    public ChessBoard() {
        // initialize the chessboard. it is always 8x8
        // but it is indexed starting at 1
        board = new ChessPiece[8][8];
    }

    // copy constructor
    public ChessBoard(ChessBoard old)
    {
        board = new ChessPiece[8][8];
        for (int i = 1; i <= 8; i++)
        {
            for (int j = 1; j <= 8; j++)
            {
                ChessPosition temp_position = new ChessPosition(i, j);
                if (old.getPiece(temp_position) != null)
                {
                    ChessPiece temp_piece = new ChessPiece(old.getPiece(temp_position));
                    this.addPiece(temp_position, temp_piece);
                }

            }

        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece)
    {
        // pull out the x and y coordinates
        int row = position.getRow();
        int col = position.getColumn();

        // put the piece there
        this.board[row - 1][col - 1] = piece;
        return;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position)
    {
        return this.board[position.getRow() - 1][position.getColumn() - 1];
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard()
    {
        // should look like this:
        /*
        |r|n|b|q|k|b|n|r|
        |p|p|p|p|p|p|p|p|
        | | | | | | | | |
        | | | | | | | | |
        | | | | | | | | |
        | | | | | | | | |
        |P|P|P|P|P|P|P|P|
        |R|N|B|Q|K|B|N|R|
        capital letters are white and lowercase letters are black
         */

        // black pieces first
        // black rooks
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        ChessPosition position = new ChessPosition(8, 1);
        this.addPiece(position, piece);
        position = new ChessPosition(8,8);
        this.addPiece(position, piece);
        // black knights
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        position = new ChessPosition(8, 2);
        this.addPiece(position, piece);
        position = new ChessPosition(8,7);
        this.addPiece(position, piece);
        // black bishops
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        position = new ChessPosition(8, 3);
        this.addPiece(position, piece);
        position = new ChessPosition(8, 6);
        this.addPiece(position, piece);
        // black queen
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        position = new ChessPosition(8, 4);
        this.addPiece(position, piece);
        // black king
        piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        position = new ChessPosition(8,5);
        this.addPiece(position, piece);
        //black pawns
        for (int col = 1; col <= 8; col++)
        {
            piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            position = new ChessPosition(7, col);
            this.addPiece(position, piece);
        }

        // white pieces. copy and paste of the black pieces
        // white rooks
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        position = new ChessPosition(1, 1);
        this.addPiece(position, piece);
        position = new ChessPosition(1,8);
        this.addPiece(position, piece);
        // white knights
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        position = new ChessPosition(1, 2);
        this.addPiece(position, piece);
        position = new ChessPosition(1,7);
        this.addPiece(position, piece);
        // white bishops
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        position = new ChessPosition(1, 3);
        this.addPiece(position, piece);
        position = new ChessPosition(1, 6);
        this.addPiece(position, piece);
        // white queen
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        position = new ChessPosition(1, 4);
        this.addPiece(position, piece);
        // white king
        piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        position = new ChessPosition(1,5);
        this.addPiece(position, piece);
        //white pawns
        for (int col = 1; col <= 8; col++)
        {
            piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            position = new ChessPosition(2, col);
            this.addPiece(position, piece);
        }
        String temp = this.toString();


        return;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        boolean to_return = true;
        for (int i = 0; i < 8; i++)
        {
            if(!(Arrays.equals(board[i], that.board[i])))
            {
                to_return = false;
            }
        }
        return to_return;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }

    @Override
    public String toString() {
        // note: this returns the board upside down
        StringBuilder builder = new StringBuilder();
        ChessPosition position;
        for (int i = 1; i <= 8; i++)
        {
            for (int j = 1; j <= 8; j++)
            {
                position = new ChessPosition(i,j);
                if (this.getPiece(position) != null)
                {
                    builder.append(this.getPiece(position).toString());
                }
                else
                    builder.append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
