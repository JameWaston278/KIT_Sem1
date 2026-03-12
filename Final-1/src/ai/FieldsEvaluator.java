package ai;

import java.util.ArrayList;
import java.util.List;

import exceptions.GameLogicException;
import model.Board;
import model.Position;
import model.Team;

/**
 * The FieldsEvaluator class provides a method to evaluate potential
 * placements for the King unit on the board. It calculates a score for each
 * possible placement based on the distance to the enemy King, the number of
 * fellow and enemy units around the new position.
 * 
 * @author udqch
 */
public class FieldsEvaluator {
    // Possible directions for neighboring positions (including diagonals)
    private static final int[][] POSSIBLE_DIRECTIONS = {
            { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 } };

    private final Board board;
    private final Team fellow;
    private final Team enemy;

    /**
     * Constructor for the FieldsEvaluator class, which initializes the board and
     * the teams for evaluation.
     * 
     * @param board  The game board to evaluate placements on.
     * @param fellow The team that owns the King being evaluated.
     * @param enemy  The opposing team.
     */
    public FieldsEvaluator(Board board, Team fellow, Team enemy) {
        this.board = board;
        this.fellow = fellow;
        this.enemy = enemy;
    }

    /**
     * Evaluates potential placements for a new unit around the King and returns a
     * list of scored placements.
     * 
     * @param kingPos The current position of the King unit (e.g., "D1").
     * @return A list of ScoredActions, where each action is a potential placement
     *         and its associated score.
     * @throws GameLogicException If there is an error in evaluating the placements
     *                            (e.g., invalid position).
     */
    public List<ScoredActions<Position>> scorePlacement(Position kingPos) throws GameLogicException {
        Position enemyKingPos = board.getKingPosition(enemy);
        List<Position> possibleMoves = kingPos.getNeighbors(POSSIBLE_DIRECTIONS);
        List<ScoredActions<Position>> scoredFields = new ArrayList<>();
        for (Position move : possibleMoves) {
            if (board.isOccupied(move)) {
                continue; // Skip occupied positions
            }
            int steps = move.distanceTo(enemyKingPos);
            int fellows = board.countUnitsAround(move, false, fellow, null);
            int enemies = board.countUnitsAround(move, false, enemy, null);
            int score = -steps + 2 * enemies - fellows;
            scoredFields.add(new ScoredActions<>(move, score));
        }
        return scoredFields;
    }
}
