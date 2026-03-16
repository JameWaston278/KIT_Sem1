package ai;

import java.util.List;

import exceptions.GameLogicException;
import model.Game;
import model.Position;
import model.Team;

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
    MOVE_UP(0, 1) {
        @Override
        public List<String> execute(Game game, Team team, Position pos) throws GameLogicException {
            return game.executeMove(team, pos, getTargetPosition(pos));
        }
    },
    /** Move right. */
    MOVE_RIGHT(1, 0) {
        @Override
        public List<String> execute(Game game, Team team, Position pos) throws GameLogicException {
            return game.executeMove(team, pos, getTargetPosition(pos));
        }
    },
    /** Move down. */
    MOVE_DOWN(0, -1) {
        @Override
        public List<String> execute(Game game, Team team, Position pos) throws GameLogicException {
            return game.executeMove(team, pos, getTargetPosition(pos));
        }
    },
    /** Move left. */
    MOVE_LEFT(-1, 0) {
        @Override
        public List<String> execute(Game game, Team team, Position pos) throws GameLogicException {
            return game.executeMove(team, pos, getTargetPosition(pos));
        }
    },
    /** Block. */
    BLOCK(0, 0) {
        @Override
        public List<String> execute(Game game, Team team, Position pos) throws GameLogicException {
            return game.executeBlock(team, pos);
        }
    },
    /** Stay in place (en place). */
    EN_PLACE(0, 0) {
        @Override
        public List<String> execute(Game game, Team team, Position pos) throws GameLogicException {
            return game.executeMove(team, pos, pos);
        }
    };

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
        int newCol = currentPosition.col() + colDelta;
        int newRow = currentPosition.row() + rowDelta;
        return new Position(newCol, newRow);
    }

    /**
     * Executes the action on the given game state for the specified team and
     * position.
     *
     * @param game The current game state.
     * @param team The team performing the action.
     * @param pos  The position of the unit performing the action.
     * @return A list of strings representing the results of the action execution.
     * @throws GameLogicException If the action cannot be executed due to game logic
     *                            rules.
     */
    public abstract List<String> execute(Game game, Team team, Position pos) throws GameLogicException;
}
