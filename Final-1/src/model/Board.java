package model;

import exceptions.GameLogicException;
import message.ErrorMessage;
import utils.GameConstants;

/**
 * The Board class represents the game board, which is a 7x7 grid where units
 * can be placed and moved.
 * It provides methods to parse coordinates, get and place units, and check if
 * positions are occupied.
 * 
 * @author udqch
 */
public class Board {
    // Direction vectors for counting units around a position
    private static final int[][] EIGHT_DIRECTIONS = {
            { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };
    // Direction vectors for counting units in orthogonal directions only
    private static final int[][] FOUR_DIRECTIONS = { { -1, 0 }, { 0, -1 }, { 0, 1 }, { 1, 0 } };

    private final Unit[][] board;

    /**
     * Constructor for the Board class, which initializes an empty 7x7 grid.
     */
    public Board() {
        this.board = new Unit[GameConstants.BOARD_ROWS][GameConstants.BOARD_COLS];
    }

    // --- BOARD OPERATIONS ---

    /**
     * Get the unit at the specified position on the board.
     * 
     * @param pos The position in string format (e.g., "A1", "B2").
     * @return The unit at the specified position, or null if the position is empty.
     */
    public Unit getUnitAt(Position pos) {
        return this.board[pos.col()][pos.row()];
    }

    /**
     * Place a unit at the specified position on the board.
     * 
     * @param unit The unit to place on the board.
     * @param pos  The position in string format (e.g., "A1", "B2").
     */
    public void placeUnitAt(Unit unit, Position pos) {
        this.board[pos.col()][pos.row()] = unit;
        unit.setPosition(pos); // Update the unit's position
    }

    /**
     * Remove the unit at the specified position on the board.
     * 
     * @param pos The position in string format (e.g., "A1", "B2").
     */
    public void removeUnitAt(Position pos) {
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
        if (fromPos.equals(toPos)) {
            return; // No movement needed if the positions are the same
        }
        placeUnitAt(unit, toPos);
        removeUnitAt(fromPos);
    }

    // --- CHECKERS ---

    /**
     * Check if the specified position on the board is occupied by a unit.
     * 
     * @param pos The position in string format (e.g., "A1", "B2").
     * @return True if the position is occupied, false otherwise.
     */
    public boolean isOccupied(Position pos) {
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
     */
    public boolean isOwnedBy(Position pos, Team team) {
        Unit unit = getUnitAt(pos);
        return unit != null && unit.getOwner() == team;
    }

    /**
     * Check if the specified coordinates are within the board limits quietly (no
     * exceptions).
     * 
     * @param col The column index to check.
     * @param row The row index to check.
     * @return True if the coordinates are within bounds, false otherwise.
     */
    public boolean isWithinBounds(int col, int row) {
        return col >= 0 && col < GameConstants.BOARD_COLS
                && row >= 0 && row < GameConstants.BOARD_ROWS;
    }

    /**
     * Check if the specified position is within the bounds of the board.
     * 
     * @param pos The position to check.
     * @return True if the position is valid, false otherwise.
     * @throws GameLogicException If the coordinates are invalid.
     */
    public boolean isValid(Position pos) throws GameLogicException {
        if (!isWithinBounds(pos.col(), pos.row())) {
            throw new GameLogicException(ErrorMessage.OUT_OF_BOUNDS.format(pos));
        }
        return true;
    }

    /**
     * Check if a move from one position to another is valid according to the game
     * rules (e.g., within movement range).
     * 
     * @param fromPos The starting position of the move.
     *                param toPos The target position of the move.
     * @param toPos   The target position of the move.
     * @return True if the move is valid, false otherwise.
     * @throws GameLogicException If the move is invalid (e.g., exceeds movement
     *                            range).
     */
    public boolean isValidMove(Position fromPos, Position toPos) throws GameLogicException {
        if (isValid(fromPos) && isValid(toPos)) {
            int colDiff = Math.abs(fromPos.col() - toPos.col());
            int rowDiff = Math.abs(fromPos.row() - toPos.row());
            if (Math.max(colDiff, rowDiff) > 1) {
                throw new GameLogicException(ErrorMessage.INVALID_MOVE.format(fromPos.toString(), toPos.toString()));
            }
        }
        return true;
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
     */
    public int countUnitsAround(Position pos, boolean eightDirections, Team targetTeam, Unit excludedUnit) {
        int count = 0;

        // Define the directions to check based on the eightDirections flag
        int[][] directions = eightDirections ? EIGHT_DIRECTIONS : FOUR_DIRECTIONS;

        for (int[] dir : directions) {
            int newCol = pos.col() + dir[0];
            int newRow = pos.row() + dir[1];
            if (newCol >= 0 && newCol < GameConstants.BOARD_COLS && newRow >= 0 && newRow < GameConstants.BOARD_ROWS) {
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