package ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import exceptions.GameLogicException;
import model.Board;
import model.Game;
import model.Position;
import model.Team;
import model.Unit;
import utils.GameConstants;
import utils.RandomUtils;

/**
 * The Controller class is responsible for managing the AI player's turn in the
 * game. It evaluates potential moves for the King, placements of units, and
 * actions for active units, and executes the best choices based on the current
 * state of the board and the positions of enemy units.
 * 
 * @author udqch
 */
public class Controller {
    private final Game game;
    private final Board board;
    private final Team aiTeam;
    private final Team enemyTeam;
    private final Random random;

    /**
     * Constructor for the Controller class.
     *
     * @param game   The game instance to control.
     * @param aiTeam The team that the AI player belongs to.
     * @param random A Random instance for tie-breaking decisions.
     */
    public Controller(Game game, Team aiTeam, Random random) {
        this.game = game;
        this.board = game.getBoard();
        this.aiTeam = aiTeam;
        this.enemyTeam = (aiTeam.equals(game.getPlayer()) ? game.getEnemy() : game.getPlayer());
        this.random = random;
    }

    /**
     * Executes the AI player's turn by evaluating and performing the best moves for
     * the King, placements, and active units.
     *
     * @return A list of log messages describing the actions taken during the turn.
     * @throws GameLogicException If there is an error during move execution.
     */
    public List<String> playTurn() throws GameLogicException {
        List<String> logs = new ArrayList<>();

        logs.addAll(moveKingPhase());
        logs.addAll(placeUnitPhase());
        logs.addAll(moveUnitPhase());
        logs.addAll(endTurnPhase());

        return logs;
    }

    /**
     * Executes the AI player's move for the King.
     *
     * @throws GameLogicException If there is an error during move execution.
     */
    private List<String> moveKingPhase() throws GameLogicException {
        Position kingPos = board.getKingPosition(aiTeam);
        KingEvaluator kingEvaluator = new KingEvaluator(board, aiTeam, enemyTeam);
        List<ScoredActions<Position>> scoredMoves = kingEvaluator.scoreMove(kingPos);

        // Find the maximum score among the scored moves
        List<Position> bestMoves = findBestOptions(scoredMoves, ScoredActions::score, ScoredActions::action);
        // Randomly select one of the best moves
        Position chosenMove = weightedRandom(bestMoves);

        // Move the King to the chosen position
        return game.executeMove(aiTeam, kingPos, chosenMove);
    }

    /**
     * Executes the AI player's placement of a unit from the hand onto the board.
     *
     * @throws GameLogicException If there is an error during move execution.
     */
    private List<String> placeUnitPhase() throws GameLogicException {
        List<String> logs = new ArrayList<>();
        Position kingPos = board.getKingPosition(aiTeam);
        FieldsEvaluator fieldsEvaluator = new FieldsEvaluator(board, aiTeam, enemyTeam);
        List<ScoredActions<Position>> scoredFields = fieldsEvaluator.scorePlacement(kingPos);

        if (scoredFields.isEmpty()) {
            return logs; // No valid placements available
        }

        // Find the maximum score among the scored placements
        List<Position> bestFields = findBestOptions(scoredFields, ScoredActions::score, ScoredActions::action);
        // Randomly select one of the best fields
        Position chosenField = weightedRandom(bestFields);

        // Randomly select an unit from the player's hand to place on the chosen field
        Unit chosenUnit = chooseUnitToPlace(aiTeam.getHand());

        // Place the chosen unit on the chosen field
        if (chosenUnit != null) {
            int oneBasedIndex = aiTeam.getHand().indexOf(chosenUnit) + 1; // Convert to 1-based index for logging
            logs.addAll(game.executePlace(aiTeam, List.of(oneBasedIndex), chosenField));
        }
        return logs;
    }

    /**
     * Evaluates and executes the AI player's actions for all active units on the
     * board.
     *
     * @throws GameLogicException If there is an error during move execution.
     */
    private List<String> moveUnitPhase() throws GameLogicException {
        List<String> logs = new ArrayList<>();
        UnitMoveEvaluator unitMoveEvaluator = new UnitMoveEvaluator(board, aiTeam, enemyTeam);

        while (true) {
            List<EvaluatedUnit> allUnits = unitMoveEvaluator.evaluateAllUnits();
            if (allUnits.isEmpty()) {
                break; // No active units to move
            }

            // Find the maximum score among all evaluated units
            List<EvaluatedUnit> bestUnits = findBestOptions(allUnits, EvaluatedUnit::totalScore, u -> u);
            // Randomly select one of the best units
            EvaluatedUnit bestUnit = weightedRandom(bestUnits);
            // Find the best action for the chosen unit
            ActionType bestAction = findBestAction(bestUnit.actions());

            Position fromPos = bestUnit.unit().getPosition();
            if (bestAction == ActionType.BLOCK) {
                logs.addAll(game.executeBlock(aiTeam, fromPos));
            } else {
                Position toPos = bestAction.getTargetPosition(fromPos);
                logs.addAll(game.executeMove(aiTeam, fromPos, toPos));
            }
        }
        return logs;
    }

    /**
     * Executes the AI player's end-of-turn phase, including discarding cards if the
     * hand is full.
     *
     * @throws GameLogicException If there is an error during move execution.
     */
    private List<String> endTurnPhase() throws GameLogicException {
        List<Unit> hand = aiTeam.getHand();
        Unit discardedUnit = null;
        if (hand.size() == GameConstants.MAX_HAND_SIZE) {
            // If the player's hand is full, randomly discard one card
            List<Integer> discardWeights = new ArrayList<>();
            for (Unit unit : hand) {
                discardWeights.add(unit.getAtk() + unit.getDef());
            }

            // Randomly select a card to discard based on the weights
            int discardIndex = RandomUtils.inverseWeightedRandom(discardWeights, random);
            discardedUnit = hand.get(discardIndex);
        }
        return game.endTurn(aiTeam, discardedUnit);
    }

    // --- HELPER METHODS ---

    /**
     * Chooses a unit from the player's hand to place on the board based on a
     * weighted random selection, where the weights are determined by the attack
     * value of the units.
     *
     * @param hand The list of units in the player's hand.
     * @return The chosen unit to place, or null if the hand is empty.
     */
    private Unit chooseUnitToPlace(List<Unit> hand) {
        if (hand.isEmpty()) {
            return null; // No units to place
        }

        List<Integer> weights = new ArrayList<>();
        for (Unit unit : hand) {
            weights.add(unit.getAtk());
        }

        int chosenIndex = RandomUtils.weightedRandom(weights, random);
        return hand.get(chosenIndex);
    }

    /**
     * Finds the best action to perform for a given unit based on the evaluated
     * scores of its potential moves.
     *
     * @param actions A list of ScoredActions objects containing scores for each
     *                potential move of the unit.
     * @return The ActionType with the highest score, or BLOCK if no positive
     *         actions are available.
     * @throws GameLogicException If there is an error accessing the board state.
     */
    private ActionType findBestAction(List<ScoredActions<ActionType>> actions) throws GameLogicException {
        boolean hasPositiveAction = false;
        // Find the maximum score among the potential actions
        List<Integer> actionWeights = new ArrayList<>();
        for (ScoredActions<ActionType> action : actions) {
            actionWeights.add(Math.max(action.score(), 0));
            if (action.score() > 0) {
                hasPositiveAction = true;
            }
        }

        // If no positive actions are available, default to BLOCK
        ActionType chosenAction;
        if (!hasPositiveAction) {
            chosenAction = ActionType.BLOCK;
        } else {
            int chosenActionIndex = RandomUtils.weightedRandom(actionWeights, random);
            chosenAction = actions.get(chosenActionIndex).action();
        }
        return chosenAction;
    }

    /**
     * Selects a random element from a list based on weighted probabilities.
     *
     * @param candidates The list of elements to choose from.
     * @return The chosen element.
     */
    private <T> T weightedRandom(List<T> candidates) {
        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        List<Integer> weights = new ArrayList<>(Collections.nCopies(candidates.size(), 1));
        int winnerIndex = RandomUtils.weightedRandom(weights, random);
        return candidates.get(winnerIndex);
    }

    /**
     * Finds the best options from a list of scored items based on their scores.
     *
     * @param <T>      The type of the items being evaluated (e.g.,
     *                 ScoredActions, EvaluatedUnit).
     * @param <R>      The type of the values to return for the best options (e.g.,
     *                 Position, EvaluatedUnit).
     * @param options  The list of items to evaluate.
     * @param getScore A function to extract the score from each item.
     * @param getValue A function to extract the value to return for each item.
     * @return A list of values corresponding to the items with the highest score.
     */
    private <T, R> List<R> findBestOptions(List<T> options, ToIntFunction<T> getScore, Function<T, R> getValue) {
        int maxScore = Integer.MIN_VALUE;
        List<R> bestValues = new ArrayList<>();
        for (T option : options) {
            int score = getScore.applyAsInt(option);
            if (score > maxScore) {
                maxScore = score;
                bestValues.clear();
                bestValues.add(getValue.apply(option));
            } else if (score == maxScore) {
                bestValues.add(getValue.apply(option));
            }
        }
        return bestValues;
    }
}
