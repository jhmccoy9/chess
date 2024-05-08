package ui;

import chess.*;

import java.util.Collection;
import java.util.Scanner;

/*
 * This class contains common helper functions to be used frequently in the other UI classes
 */
public class Utilities
{
    public static String getInput(Collection<String> validEntries)
    {
        System.out.printf(EscapeSequences.SET_BG_COLOR_WHITE);
        System.out.printf(">>> ");
        Scanner scanner = new Scanner(System.in);
        StringBuilder entry = new StringBuilder(scanner.nextLine());

        // if you don't pass a collection of valid entries, it just takes that isn't null
        if (validEntries != null)
            while (!validEntries.contains(entry.toString()))
            {
                System.out.println("Invalid Entry. Options:");
                for (String option : validEntries)
                    System.out.println(option);
                System.out.printf(">>> ");
                entry = new StringBuilder(scanner.nextLine());
            }
        while (entry.toString().isEmpty())
        {
            System.out.println("Invalid Entry. Try again.");
            System.out.printf(">>> ");
            entry = new StringBuilder(scanner.nextLine());
        }
        return entry.toString();
    }

    public static void printChessBoard(ChessBoard board)
    {
        System.out.printf(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.printf(EscapeSequences.SET_BG_COLOR_WHITE);
        System.out.printf("    h  g  f  e  d  c  b  a    \n");
        boolean colorAlternator = true;
        // iterate over the entire board, with certain modifications
        for (int row = 1; row < 9; row++)
        {
            for (int col = 0; col < 10; col++)
            {
                if (col == 0 || col == 9)
                {
                    System.out.printf(EscapeSequences.SET_TEXT_COLOR_BLACK);
                    System.out.printf(EscapeSequences.SET_BG_COLOR_WHITE);
                    System.out.printf(" %d ", row);
                }
                else
                {
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col ));
                    if (piece == null)
                    {
                        String backgroundColorCode = colorAlternator ?
                        EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                        colorAlternator = !colorAlternator;
                        System.out.printf(backgroundColorCode);
                        System.out.printf("   ");
                    }
                    else {
                        String textColorCode = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                                EscapeSequences.SET_TEXT_COLOR_WHITE : EscapeSequences.SET_TEXT_COLOR_BLACK;
                        String backgroundColorCode = colorAlternator ?
                                EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                        colorAlternator = !colorAlternator;
                        System.out.printf(textColorCode);
                        System.out.printf(backgroundColorCode);
                        System.out.printf(" %s ", piece.toString());
                    }
                }
            }
            colorAlternator = !colorAlternator;
            System.out.printf("\n");
        }
        System.out.printf(EscapeSequences.SET_BG_COLOR_WHITE);
        System.out.printf(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.printf("    h  g  f  e  d  c  b  a    \n");

        System.out.println(" ");
        System.out.printf(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.printf(EscapeSequences.SET_BG_COLOR_WHITE);
        System.out.printf("    a  b  c  d  e  f  g  h    \n");
        colorAlternator = true;
        for (int row = 8; row > 0; row--)
        {
            for (int col = 9; col >= 0; col--)
            {
                if (col == 0 || col == 9)
                {
                    System.out.printf(EscapeSequences.SET_TEXT_COLOR_BLACK);
                    System.out.printf(EscapeSequences.SET_BG_COLOR_WHITE);
                    System.out.printf(" %d ", row);
                }
                else
                {
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col ));
                    if (piece == null)
                    {
                        String backgroundColorCode = colorAlternator ?
                                EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                        colorAlternator = !colorAlternator;
                        System.out.printf(backgroundColorCode);
                        System.out.printf("   ");
                    }
                    else {
                        String textColorCode = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                                EscapeSequences.SET_TEXT_COLOR_WHITE : EscapeSequences.SET_TEXT_COLOR_BLACK;
                        String backgroundColorCode = colorAlternator ?
                                EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                        colorAlternator = !colorAlternator;
                        System.out.printf(textColorCode);
                        System.out.printf(backgroundColorCode);
                        System.out.printf(" %s ", piece.toString());
                    }
                }
            }
            colorAlternator = !colorAlternator;
            System.out.printf("\n");
        }
        System.out.printf(EscapeSequences.SET_BG_COLOR_WHITE);
        System.out.printf(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.printf("    a  b  c  d  e  f  g  h    \n");
        System.out.printf(EscapeSequences.SET_BG_COLOR_WHITE);
        return;
    }

    public static void printChessBoard(ChessBoard board, Collection<ChessPosition> possiblePositions)
    {
        System.out.printf(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.printf(EscapeSequences.SET_BG_COLOR_WHITE);
        System.out.printf("    a  b  c  d  e  f  g  h    \n");
        boolean colorAlternator = true;
        for (int row = 1; row <= 8; row++)
        {
            for (int col = 0; col <= 9; col++)
            {
                if (col == 0 || col == 9)
                {
                    System.out.printf(EscapeSequences.SET_TEXT_COLOR_BLACK);
                    System.out.printf(EscapeSequences.SET_BG_COLOR_WHITE);
                    System.out.printf(" %d ", row);
                }
                else
                {
                    ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                    if (piece == null)
                    {
                        String backgroundColorCode = colorAlternator ?
                                EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                        colorAlternator = !colorAlternator;
                        System.out.printf(backgroundColorCode);

                        // if it's a possible move, highlight it, and then undo the color change
                        ChessPosition tempPosition = new ChessPosition(row, col);
                        if (possiblePositions.contains(tempPosition))
                        {
                            //System.out.print(tempPosition.toString());
                            System.out.printf(EscapeSequences.SET_BG_COLOR_MAGENTA);
                        }
                        System.out.printf("   ");
                        System.out.printf(backgroundColorCode);

                    }
                    else {
                        String textColorCode = piece.getTeamColor() == ChessGame.TeamColor.WHITE ?
                                EscapeSequences.SET_TEXT_COLOR_WHITE : EscapeSequences.SET_TEXT_COLOR_BLACK;
                        String backgroundColorCode = colorAlternator ?
                                EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                        colorAlternator = !colorAlternator;
                        System.out.printf(textColorCode);
                        System.out.printf(backgroundColorCode);

                        // if it's a possible move, highlight it, and then undo the color change
                        ChessPosition tempPosition = new ChessPosition(row, col);
                        if (possiblePositions.contains(tempPosition))
                        {
                            System.out.printf(EscapeSequences.SET_BG_COLOR_MAGENTA);
                        }

                        System.out.printf(" %s ", piece.toString());
                        System.out.printf(backgroundColorCode);

                    }
                }
            }
            colorAlternator = !colorAlternator;
            System.out.printf("\n");
        }
        System.out.printf(EscapeSequences.SET_BG_COLOR_WHITE);
        System.out.printf(EscapeSequences.SET_TEXT_COLOR_BLACK);
        System.out.printf("    a  b  c  d  e  f  g  h    \n");
        System.out.printf(EscapeSequences.SET_BG_COLOR_WHITE);
        return;
    }



}
