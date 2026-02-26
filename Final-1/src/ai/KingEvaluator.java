package ai;

import java.util.ArrayList;
import java.util.List;

import exceptions.GameLogicException;
import model.Board;
import model.Team;
import model.Unit;

/**
 * The KingEvaluator class provides a method to evaluate potential moves for the
 * King unit on the board. It calculates a score for each possible move based on
 * the number of fellow and enemy units around the new position, as well as the
 * distance moved and whether there is a fellow unit on the new position.
 * 
 * @author udqch
 */
public class KingEvaluator {

    // Possible moves include moving up, down, left, right, or staying in place
    private static final int[][] POSSIBLE_DIRECTIONS = { { 1, 0 }, { 0, 1 }, { -1, 0 }, { 0, -1 }, { 0, 0 } };

    private final Board board;
    private final Team fellow;
    private final Team enemy;

    /**
     * Constructor for the KingEvaluator class, which initializes the board and the
     * teams for evaluation.
     * 
     * @param board  The game board to evaluate moves on.
     * @param fellow The team that owns the King being evaluated.
     * @param enemy  The opposing team.
     */
    public KingEvaluator(Board board, Team fellow, Team enemy) {
        this.board = board;
        this.fellow = fellow;
        this.enemy = enemy;
    }

    /**
     * Evaluates potential moves for the King unit at the given position and returns
     * a list of scored moves.
     * 
     * @param kingPos The current position of the King unit (e.g., "D1").
     * @return A list of ScoredActions, where each action is a potential move and
     *         its associated score.
     * @throws GameLogicException If there is an error in evaluating the moves
     *                            (e.g.,
     *                            invalid position).
     */
    public List<ScoredActions<String>> scoreMove(String kingPos) throws GameLogicException {
        Unit king = board.getUnitAt(kingPos);
        List<String> possibleMoves = board.getPossibleMoves(kingPos, POSSIBLE_DIRECTIONS);
        List<ScoredActions<String>> scoredMoves = new ArrayList<>();
        for (String move : possibleMoves) {
            if (board.isOwnedBy(move, enemy)) {
                continue; // Skip moves that would move onto an enemy unit
            }

            int fellows = board.countUnitsAround(move, true, fellow, king);
            int enemies = board.countUnitsAround(move, true, enemy, king);
            int distance = (move.equals(kingPos)) ? 0 : 1; // Staying in place has distance 0, moving has distance 1
            int fellowPresent = (board.isOwnedBy(move, fellow)) ? 1 : 0; // 1 if there's a fellow unit on the move
                                                                         // position, 0 otherwise
            int score = fellows - 2 * enemies - distance + 3 * fellowPresent;
            scoredMoves.add(new ScoredActions<>(move, score));
        }
        return scoredMoves;
    }

}
