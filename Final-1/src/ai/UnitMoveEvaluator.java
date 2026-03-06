package ai;

import java.util.ArrayList;
import java.util.List;

import exceptions.GameLogicException;
import logic.combination.UnitCombiner;
import model.Board;
import model.Position;
import model.Team;
import model.Unit;

/**
 * The UnitMoveEvaluator class evaluates the potential moves for a unit on the
 * board.
 * It calculates scores for moving in each direction, blocking, and staying in
 * place, based on the current state of the board and the positions of enemy
 * units.
 * 
 * @author udqch
 */
public class UnitMoveEvaluator {

    private static final int[][] POSSIBLE_DIRECTIONS = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
    private static final ActionType[] DIRECTION_TYPES = {
        ActionType.MOVE_UP, ActionType.MOVE_RIGHT,
        ActionType.MOVE_DOWN, ActionType.MOVE_LEFT };

    private final Board board;
    private final Team fellow;
    private final Team enemy;

    /**
     * Constructor for the UnitMoveEvaluator class.
     *
     * @param board  The game board to evaluate moves on.
     * @param fellow The team of the unit being evaluated.
     * @param enemy  The opposing team.
     */
    public UnitMoveEvaluator(Board board, Team fellow, Team enemy) {
        this.board = board;
        this.fellow = fellow;
        this.enemy = enemy;
    }

    // --- EVALUATION METHODS ---

    /**
     * Evaluates all active units of the fellow team and returns a list of
     * EvaluatedUnit objects containing the scores for each unit's potential moves.
     *
     * @return A list of EvaluatedUnit objects with scores for each unit's moves.
     * @throws GameLogicException If there is an error accessing the board state.
     */
    public List<EvaluatedUnit> evaluateAllUnits() throws GameLogicException {
        List<EvaluatedUnit> allEvaluated = new ArrayList<>();

        List<Unit> activeUnits = fellow.getActiveUnits();
        for (Unit unit : activeUnits) {
            if (unit.isKing() || unit.hasMoved()) {
                continue; // Skip evaluating the King for movement
            }
            allEvaluated.add(evaluateSingleUnit(unit));
        }

        return allEvaluated;
    }

    /**
     * Evaluates the potential moves for a single unit and returns an EvaluatedUnit
     * object containing the scores for each move.
     *
     * @param unit The unit to evaluate.
     * @return An EvaluatedUnit object with scores for each potential move.
     * @throws GameLogicException If there is an error accessing the board state.
     */
    public EvaluatedUnit evaluateSingleUnit(Unit unit) throws GameLogicException {
        List<ScoredActions<ActionType>> actions = new ArrayList<>();
        int totalScore = 0;
        Position currentPos = unit.getPosition();

        for (int i = 0; i < 4; i++) {
            Position newPos = new Position(
                    currentPos.col() + POSSIBLE_DIRECTIONS[i][0], currentPos.row() + POSSIBLE_DIRECTIONS[i][1]);

            // Check if the new position is within the bounds of the board
            if (!board.isValid(newPos)) {
                actions.add(new ScoredActions<>(DIRECTION_TYPES[i], 0)); // Invalid move
                continue; // Skip invalid moves
            }

            int score = calculateMoveScore(unit, newPos);
            actions.add(new ScoredActions<>(DIRECTION_TYPES[i], score));
            totalScore += score;
        }

        // Add score for blocking
        int blockScore = calculateBlockScore(unit, currentPos);
        actions.add(new ScoredActions<>(ActionType.BLOCK, blockScore));
        totalScore += blockScore;

        // Add score for staying in place
        int enPlaceScore = calculateEnplaceScore(unit, currentPos);
        actions.add(new ScoredActions<>(ActionType.EN_PLACE, enPlaceScore));
        totalScore += enPlaceScore;

        return new EvaluatedUnit(unit, totalScore, actions);
    }

    // --- SCORING METHODS ---

    /**
     * Calculates the score for moving a unit to a new position based on the
     * current board state.
     *
     * @param unit   The unit to evaluate.
     * @param newPos The new position to evaluate.
     * @return The score for the move.
     * @throws GameLogicException If there is an error accessing the board state.
     */
    private int calculateMoveScore(Unit unit, Position newPos) throws GameLogicException {
        Unit targetUnit = board.getUnitAt(newPos);

        // Situtation 1: new position is empty
        if (targetUnit == null) {
            Position enemyKingPos = board.getKingPosition(enemy);
            int steps = newPos.distanceTo(enemyKingPos);
            int enemies = board.countUnitsAround(newPos, false, enemy, null);
            return 10 * steps - enemies;
        }

        // Situation 2: new position is occupied by an allied unit
        if (targetUnit.getOwner() == fellow) {
            Unit combinedUnit = UnitCombiner.combine(unit, targetUnit);
            if (combinedUnit != null) {
                // If a combination is possible, calculate the score based on the combined
                // unit's stats
                return combinedUnit.getAtk() + combinedUnit.getDef() - unit.getAtk() - unit.getDef();
            } else {
                // If no combination is possible, penalize for blocking an ally
                return -targetUnit.getAtk() - targetUnit.getDef();
            }
        }

        // Situation 3: new position is occupied by an enemy unit
        if (targetUnit.getOwner() == enemy) {
            if (targetUnit.isKing()) {
                return unit.getAtk();
            } else if (targetUnit.isHidden()) {
                return unit.getAtk() - 500;
            } else if (targetUnit.isBlocking()) {
                return unit.getAtk() - targetUnit.getDef();
            } else {
                return 2 * (unit.getAtk() - targetUnit.getAtk());
            }
        }

        return 0; // Default case (should not reach here)
    }

    /**
     * Calculates the score for blocking based on the unit's defense and the attack
     * value of nearby enemy units.
     *
     * @param unit       The unit to evaluate.
     * @param currentPos The current position of the unit.
     * @return The score for blocking.
     * @throws GameLogicException If there is an error accessing the board state.
     */
    private int calculateBlockScore(Unit unit, Position currentPos) throws GameLogicException {
        int atkStar = getAtkStar(currentPos, enemy);
        return Math.max(1, (unit.getDef() - atkStar) / 100);
    }

    /**
     * Calculates the score for staying in place based on the unit's defense and the
     * attack
     * value of nearby enemy units.
     *
     * @param unit       The unit to evaluate.
     * @param currentPos The current position of the unit.
     * @return The score for staying in place.
     * @throws GameLogicException If there is an error accessing the board state.
     */
    private int calculateEnplaceScore(Unit unit, Position currentPos) throws GameLogicException {
        int atkStar = getAtkStar(currentPos, enemy);
        return Math.max(0, (unit.getAtk() - atkStar) / 100);
    }

    // --- HELPER METHODS ---

    /**
     * Gets the maximum attack value of enemy units in line of sight from the
     * specified position by Raycasting Algorithm.
     *
     * @param pos   The position to check.
     * @param enemy The enemy team.
     * @return The maximum attack value of enemy units.
     */
    private int getAtkStar(Position pos, Team enemy) {
        int maxAtk = 0;

        for (int[] dir : POSSIBLE_DIRECTIONS) {
            int currentCol = pos.col() + dir[0];
            int currentRow = pos.row() + dir[1];

            while (currentCol >= 0 && currentCol < Board.BOARD_SIZE
                    && currentRow >= 0 && currentRow < Board.BOARD_SIZE) {
                Position checkPos = new Position(currentCol, currentRow);
                try {
                    Unit hitUnit = board.getUnitAt(checkPos);
                    if (hitUnit != null) {
                        if (hitUnit.getOwner().equals(enemy) && !hitUnit.isKing()) {
                            maxAtk = Math.max(maxAtk, hitUnit.getAtk());
                        }
                        break; // Stop checking in this direction after hitting any unit
                    }
                } catch (GameLogicException e) {
                    break; // Out of bounds, stop checking in this direction
                }
                currentCol += dir[0];
                currentRow += dir[1];
            }
        }
        return maxAtk;
    }
}