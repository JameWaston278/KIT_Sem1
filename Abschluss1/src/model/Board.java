package model;

import exceptions.ErrorMessage;
import exceptions.GameLogicException;

/**
 * The Board class represents the game board, which is a 7x7 grid where units
 * can be placed and moved.
 * It provides methods to parse coordinates, get and place units, and check if
 * positions are occupied.
 */
public class Board {
    private final Unit[][] board;

    public Board() {
        this.board = new Unit[7][7];
    }

    // --- PARSE COORDINATES ---

    /**
     * Parse a position string (e.g., "A1", "B2") into board coordinates (x, y).
     * 
     * @param posString The position string to parse.
     * @return An array of two integers representing the column index (0-6) and row
     *         index (0-6).
     * @throws GameLogicException If the input string is invalid or out of bounds.
     */
    public int[] parseCoordinates(String posString) throws GameLogicException {
        if (posString == null || posString.length() != 2) {
            throw new GameLogicException(ErrorMessage.INVALID_COORDINATES.format(posString));
        }

        char columnChar = Character.toUpperCase(posString.charAt(0));
        char rowChar = posString.charAt(1);
        if (columnChar < 'A' || columnChar > 'G' || rowChar < '1' || rowChar > '7') {
            throw new GameLogicException(ErrorMessage.OUT_OF_BOUNDS.format(posString));
        }

        int colIndex = columnChar - 'A';
        int rowIndex = Character.getNumericValue(rowChar) - 1;
        return new int[] { colIndex, rowIndex };
    }

    // --- BOARD OPERATIONS ---

    /**
     * Get the unit at the specified position on the board.
     * 
     * @param posString The position in string format (e.g., "A1", "B2").
     * @return The unit at the specified position, or null if the position is empty.
     * @throws GameLogicException If the coordinates are invalid.
     */
    public Unit getUnitAt(String posString) throws GameLogicException {
        int[] indices = parseCoordinates(posString);
        int x = indices[0];
        int y = indices[1];
        return this.board[x][y];
    }

    /**
     * Place a unit at the specified position on the board.
     * 
     * @param unit      The unit to place on the board.
     * @param posString The position in string format (e.g., "A1", "B2").
     * @throws GameLogicException If the coordinates are invalid.
     */
    public void placeUnit(Unit unit, String posString) throws GameLogicException {
        int[] indices = parseCoordinates(posString);
        int x = indices[0];
        int y = indices[1];
        this.board[x][y] = unit;
    }

    /**
     * Remove the unit at the specified position on the board.
     * 
     * @param posString The position in string format (e.g., "A1", "B2").
     * @throws GameLogicException If the coordinates are invalid.
     */
    public void removeUnit(String posString) throws GameLogicException {
        int[] indices = parseCoordinates(posString);
        int x = indices[0];
        int y = indices[1];
        this.board[x][y] = null;
    }

    /**
     * Check if the specified position on the board is occupied by a unit.
     * 
     * @param posString The position in string format (e.g., "A1", "B2").
     * @return True if the position is occupied, false otherwise.
     * @throws GameLogicException If the coordinates are invalid.
     */
    public boolean isOccupied(String posString) throws GameLogicException {
        return getUnitAt(posString) != null;
    }

    /**
     * Move a unit from one position to another on the board.
     * 
     * @param fromPos The starting position of the unit (e.g., "A1").
     * @param toPos   The target position for the unit (e.g., "B2").
     * @throws GameLogicException If there is no unit at the starting position or if
     *                            the coordinates are invalid.
     */
    public void moveUnit(String fromPos, String toPos) throws GameLogicException {
        Unit unit = getUnitAt(fromPos);
        if (unit == null) {
            throw new GameLogicException(ErrorMessage.NO_UNIT_AT_POSITION.format(fromPos));
        }
        placeUnit(unit, toPos);
        removeUnit(fromPos);
    }
}