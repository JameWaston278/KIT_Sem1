package model;

import java.util.ArrayList;
import java.util.List;

import exceptions.GameLogicException;
import message.ErrorMessage;
import utils.GameConstants;

/**
 * The Position class represents a specific location on the game board, defined
 * by a column and row index.
 * It provides methods for parsing coordinates from strings, converting back to
 * string format, and finding neighboring positions.
 * 
 * @param col The column index of the position (0-based).
 * @param row The row index of the position (0-based).
 * 
 * @author udqch
 */
public record Position(int col, int row) {
    /** The first column label. */
    public static final char FIRST_COL = 'A';
    /** The last column label. */
    public static final char LAST_COL = 'G';
    /** The first row label. */
    public static final char FIRST_ROW = '1';
    /** The last row label. */
    public static final char LAST_ROW = '7';

    // --- PARSE COORDINATES ---

    /**
     * Parses a position from a string representation (e.g., "D1" -> Position(3, 0))
     * and returns a Position object.
     * 
     * @param posString The string representation of the position.
     * @return A Position object corresponding to the input string.
     * @throws GameLogicException If the input string is invalid or out of bounds.
     */
    public static Position fromString(String posString) throws GameLogicException {
        if (posString == null || posString.length() != 2) {
            throw new GameLogicException(ErrorMessage.INVALID_COORDINATES.format(posString));
        }

        char columnChar = Character.toUpperCase(posString.charAt(0));
        char rowChar = posString.charAt(1);
        if (columnChar < FIRST_COL || columnChar > LAST_COL || rowChar < FIRST_ROW || rowChar > LAST_ROW) {
            throw new GameLogicException(ErrorMessage.OUT_OF_BOUNDS.format(posString));
        }

        int colIndex = columnChar - FIRST_COL;
        int rowIndex = Character.getNumericValue(rowChar) - 1;
        return new Position(colIndex, rowIndex);
    }

    @Override
    public String toString() {
        char colChar = (char) (FIRST_COL + col);
        char rowChar = (char) (FIRST_ROW + row);
        return "" + colChar + rowChar;
    }

    // --- NEIGHBORS ---

    /**
     * Returns a list of neighboring positions based on the given directions.
     * 
     * @param directions An array of direction vectors.
     * @return A list of neighboring positions.
     */
    public List<Position> getNeighbors(int[][] directions) {
        List<Position> neighbors = new ArrayList<>();
        for (int[] dir : directions) {
            int newCol = this.col + dir[0];
            int newRow = this.row + dir[1];
            if (newCol >= 0 && newCol < GameConstants.BOARD_COLS && newRow >= 0 && newRow < GameConstants.BOARD_ROWS) {
                neighbors.add(new Position(newCol, newRow));
            }
        }
        return neighbors;
    }

    // --- DISTANCE ---

    /**
     * Calculates the Manhattan distance from this position to another position.
     * 
     * @param other The other position to calculate the distance to.
     * @return The Manhattan distance between this position and the other position.
     */
    public int distanceTo(Position other) {
        return Math.abs(this.col - other.col) + Math.abs(this.row - other.row);
    }
}