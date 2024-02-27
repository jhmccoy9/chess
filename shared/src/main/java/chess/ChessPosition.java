package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

// IMPORTANT NOTE: from a user standpoint, indices start at 1 but
// under the hood they start at 0
public class ChessPosition {

    // class variables
    private int row;
    private int col;


    public ChessPosition(int row, int col)
    {
        this.row = row;
        this.col = col;
    }

    // copy constructor
    public ChessPosition(ChessPosition old)
    {
        this.row = old.getRow();
        this.col = old.getColumn();
    }


    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow()
    {
        return this.row;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn()
    {
        return this.col;
        //throw new RuntimeException("Not implemented");
    }

    @Override
    public String toString() { return "Row: " + row + " Column: " + col;}

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public boolean equals(Object position)
    {
        if (this == position)
            return true;
        else if (position == null || getClass() != position.getClass())
            return false;
        else if (this.col == ((ChessPosition) position).getColumn() && this.row == ((ChessPosition) position).getRow()) {
            return true;
        }
        return false;
    }
}
