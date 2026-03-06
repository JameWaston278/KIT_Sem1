package ai;

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
    MOVE_UP,
    /** Move right. */
    MOVE_RIGHT,
    /** Move down. */
    MOVE_DOWN,
    /** Move left. */
    MOVE_LEFT,
    /** Blockage. */
    BLOCK,
    /** Stay in place (en_place). */
    EN_PLACE;
}
