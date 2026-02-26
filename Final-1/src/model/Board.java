package model;

import java.util.ArrayList;
import java.util.List;

import exceptions.ErrorMessage;
import exceptions.GameLogicException;
import utils.DisplayFormat;

/**
 * The Board class represents the game board, which is a 7x7 grid where units
 * can be placed and moved.
 * It provides methods to parse coordinates, get and place units, and check if
 * positions are occupied.
 * 
 * @author udqch
 */
public class Board {
    private final Unit[][] board;

    /**
     * Constructor for the Board class, which initializes an empty 7x7 grid.
     */
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

    /**
     * Convert board coordinates (x, y) back into a position string (e.g., "A1",
     * "B2").
     * 
     * @param x The column index (0-6).
     * @param y The row index (0-6).
     * @return A string representing the position on the board.
     * @throws GameLogicException If the coordinates are out of bounds.
     */
    public String coordinatesToString(int x, int y) throws GameLogicException {
        if (x < 0 || x >= board.length || y < 0 || y >= board[0].length) {
            String coordString = DisplayFormat.COORDINATES.format(x, y);
            throw new GameLogicException(ErrorMessage.OUT_OF_BOUNDS.format(coordString));
        }
        char columnChar = (char) ('A' + x);
        char rowChar = (char) ('1' + y);
        return "" + columnChar + rowChar;
    }

    /**
     * Get a list of possible moves from a given position based on the provided
     * directions. Each direction is represented as an array of two integers (dx,
     * dy) indicating the change in x and y coordinates.
     * 
     * @param posString  The current position in string format (e.g., "A1", "B2").
     * @param directions An array of possible move directions, where each direction
     *                   is an array of two integers representing the change in x
     *                   and y coordinates.
     * @return A list of valid move positions in string format that can be reached
     *         from the current position based on the provided directions.
     * @throws GameLogicException If the input position is invalid or if any of the
     *                            calculated move positions are out of bounds.
     */
    public List<String> getPossibleMoves(String posString, int[][] directions) throws GameLogicException {
        List<String> possibleMoves = new ArrayList<>();
        int[] indices = parseCoordinates(posString);
        int x = indices[0];
        int y = indices[1];
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length) {
                possibleMoves.add(coordinatesToString(newX, newY));
            }
        }
        return possibleMoves;
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
    public void placeUnitAt(Unit unit, String posString) throws GameLogicException {
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
    public void removeUnitAt(String posString) throws GameLogicException {
        int[] indices = parseCoordinates(posString);
        int x = indices[0];
        int y = indices[1];
        this.board[x][y] = null;
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
        placeUnitAt(unit, toPos);
        removeUnitAt(fromPos);
    }

    // --- CHECKS ---

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
     * Check if the specified position on the board is occupied by a unit belonging
     * to the given team.
     * 
     * @param posString The position in string format (e.g., "A1", "B2").
     * @param team      The team to check against.
     * @return True if the position is occupied by a unit of the specified team,
     *         false otherwise.
     * @throws GameLogicException If the coordinates are invalid.
     */
    public boolean isOwnedBy(String posString, Team team) throws GameLogicException {
        Unit unit = getUnitAt(posString);
        return unit != null && unit.getOwner() == team;
    }

    // --- COUNTERS ---

    /**
     * Counts the number of units around a given position, optionally including
     * diagonal directions.
     * 
     * @param posString       The position to check around (e.g., "A1").
     * @param eightDirections If true, counts units in all 8 surrounding positions;
     *                        if false, counts only orthogonal positions.
     * @param targetTeam      The team of the units to count.
     * @param excludedUnit    A unit to exclude from the count (e.g., the unit being
     *                        moved), or null to include all units.
     * @return The count of units around the specified position that match the
     *         criteria.
     * @throws GameLogicException If the coordinates are invalid.
     */
    public int countUnitsAround(String posString, boolean eightDirections, Team targetTeam, Unit excludedUnit)
            throws GameLogicException {
        int count = 0;
        int[] indices = parseCoordinates(posString);
        int x = indices[0];
        int y = indices[1];

        // Define the directions to check based on the eightDirections flag
        int[][] directions = eightDirections
                ? new int[][] { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } }
                : new int[][] { { -1, 0 }, { 0, -1 }, { 0, 1 }, { 1, 0 } };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (newX >= 0 && newX < board.length && newY >= 0 && newY < board[0].length) {
                Unit adjacentUnit = board[newX][newY];
                if (adjacentUnit != null && adjacentUnit.getOwner() == targetTeam
                        && adjacentUnit != excludedUnit) {
                    count++;
                }
            }
        }
        return count;
    }
}