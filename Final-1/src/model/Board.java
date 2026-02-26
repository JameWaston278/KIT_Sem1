package model;

import exceptions.ErrorMessage;
import exceptions.GameLogicException;

/**
 * The Board class represents the game board, which is a 7x7 grid where units
 * can be placed and moved.
 * It provides methods to parse coordinates, get and place units, and check if
 * positions are occupied.
 * 
 * @author udqch
 */
public class Board {
    public static final int BOARD_SIZE = 7;
    private static final int[][] EIGHT_DIRECTIONS = {
            { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };
    private static final int[][] FOUR_DIRECTIONS = { { -1, 0 }, { 0, -1 }, { 0, 1 }, { 1, 0 } };

    private final Unit[][] board;

    /**
     * Constructor for the Board class, which initializes an empty 7x7 grid.
     */
    public Board() {
        this.board = new Unit[BOARD_SIZE][BOARD_SIZE];
    }

    // --- BOARD OPERATIONS ---

    /**
     * Get the unit at the specified position on the board.
     * 
     * @param pos The position in string format (e.g., "A1", "B2").
     * @return The unit at the specified position, or null if the position is empty.
     * @throws GameLogicException If the coordinates are invalid.
     */
    public Unit getUnitAt(Position pos) throws GameLogicException {
        return this.board[pos.col()][pos.row()];
    }

    /**
     * Place a unit at the specified position on the board.
     * 
     * @param unit The unit to place on the board.
     * @param pos  The position in string format (e.g., "A1", "B2").
     * @throws GameLogicException If the coordinates are invalid.
     */
    public void placeUnitAt(Unit unit, Position pos) throws GameLogicException {
        this.board[pos.col()][pos.row()] = unit;
    }

    /**
     * Remove the unit at the specified position on the board.
     * 
     * @param pos The position in string format (e.g., "A1", "B2").
     * @throws GameLogicException If the coordinates are invalid.
     */
    public void removeUnitAt(Position pos) throws GameLogicException {
        this.board[pos.col()][pos.row()] = null;
    }

    /**
     * Move a unit from one position to another on the board.
     * 
     * @param fromPos The starting position of the unit (e.g., "A1").
     * @param toPos   The target position for the unit (e.g., "B2").
     * @throws GameLogicException If there is no unit at the starting position or if
     *                            the coordinates are invalid.
     */
    public void moveUnit(Position fromPos, Position toPos) throws GameLogicException {
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
     * @param pos The position in string format (e.g., "A1", "B2").
     * @return True if the position is occupied, false otherwise.
     * @throws GameLogicException If the coordinates are invalid.
     */
    public boolean isOccupied(Position pos) throws GameLogicException {
        return getUnitAt(pos) != null;
    }

    /**
     * Check if the specified position on the board is occupied by a unit belonging
     * to the given team.
     * 
     * @param pos  The position in string format (e.g., "A1", "B2").
     * @param team The team to check against.
     * @return True if the position is occupied by a unit of the specified team,
     *         false otherwise.
     * @throws GameLogicException If the coordinates are invalid.
     */
    public boolean isOwnedBy(Position pos, Team team) throws GameLogicException {
        Unit unit = getUnitAt(pos);
        return unit != null && unit.getOwner() == team;
    }

    // --- COUNTERS ---

    /**
     * Counts the number of units around a given position, optionally including
     * diagonal directions.
     * 
     * @param pos             The position to check around (e.g., "A1").
     * @param eightDirections If true, counts units in all 8 surrounding positions;
     *                        if false, counts only orthogonal positions.
     * @param targetTeam      The team of the units to count.
     * @param excludedUnit    A unit to exclude from the count (e.g., the unit being
     *                        moved), or null to include all units.
     * @return The count of units around the specified position that match the
     *         criteria.
     * @throws GameLogicException If the coordinates are invalid.
     */
    public int countUnitsAround(Position pos, boolean eightDirections, Team targetTeam, Unit excludedUnit)
            throws GameLogicException {
        int count = 0;

        // Define the directions to check based on the eightDirections flag
        int[][] directions = eightDirections ? EIGHT_DIRECTIONS : FOUR_DIRECTIONS;

        for (int[] dir : directions) {
            int newCol = pos.col() + dir[0];
            int newRow = pos.row() + dir[1];
            if (newCol >= 0 && newCol < BOARD_SIZE && newRow >= 0 && newRow < BOARD_SIZE) {
                Unit adjacentUnit = board[newCol][newRow];
                if (adjacentUnit != null && adjacentUnit.getOwner() == targetTeam
                        && adjacentUnit != excludedUnit) {
                    count++;
                }
            }
        }
        return count;
    }
}