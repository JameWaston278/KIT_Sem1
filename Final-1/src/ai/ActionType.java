package ai;

import model.Position;

/**
 * The ActionType enum defines the different types of actions that can be taken
 * in the game.
 * These actions include moving in four directions (up, right, down, left),
 * blockage, and stay in place.
 * 
 * @author udqch
 */
public enum ActionType {
    /** Move up. */
    MOVE_UP(0, 1),
    /** Move right. */
    MOVE_RIGHT(1, 0),
    /** Move down. */
    MOVE_DOWN(0, -1),
    /** Move left. */
    MOVE_LEFT(-1, 0),
    /** Blockage. */
    BLOCK(0, 0),
    /** Stay in place (en_place). */
    EN_PLACE(0, 0);

    private final int colDelta;
    private final int rowDelta;

    ActionType(int colDelta, int rowDelta) {
        this.colDelta = colDelta;
        this.rowDelta = rowDelta;
    }

    /**
     * Calculates the target position based on the current position and the action's
     * direction.
     *
     * @param currentPosition The current position of the unit.
     * @return The target position after applying the action's direction.
     */
    public Position getTargetPosition(Position currentPosition) {
        int newRow = currentPosition.row() + rowDelta;
        int newCol = currentPosition.col() + colDelta;
        return new Position(newRow, newCol);
    }
}
