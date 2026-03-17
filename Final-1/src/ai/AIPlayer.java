package ai;

import exceptions.GameLogicException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import message.EventLog;
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
public class AIPlayer {
    /**
     * A functional interface for scoring items.
     * 
     * @param <T> The type of the item to score.
     */
    @FunctionalInterface
    interface Scorer<T> {
        /**
         * Returns the score for the given item.
         *
         * @param item The item to score.
         * @return The score for the item.
         */
        int getScore(T item);
    }

    /**
     * A functional interface for extracting values from items.
     *
     * @param <T> The type of the item to extract from.
     * @param <R> The type of the value to extract.
     */
    @FunctionalInterface
    interface Extractor<T, R> {
        /**
         * Extracts a value from the given item.
         *
         * @param item The item to extract from.
         * @return The extracted value.
         */
        R extract(T item);
    }

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
     * @param random A Random instance initialized with a specific seed for
     *               reproducibility of the AI's decisions.
     */
    public AIPlayer(Game game, Team aiTeam, Random random) {
        this.game = game;
        this.board = game.getBoard();
        this.aiTeam = aiTeam;
        this.enemyTeam = (aiTeam.equals(game.getTeam1()) ? game.getTeam2() : game.getTeam1());
        this.random = random;
    }

    /**
     * Executes the AI player's turn by evaluating and performing the best moves for
     * the King, placements, and active units.
     *
     * @param aiStepCallback A callback interface to receive updates after each step
     *                       of
     *                       the AI's turn.
     * @throws GameLogicException If there is an error during move execution.
     */
    public void playTurn(AIStepListener aiStepCallback) throws GameLogicException {

        moveKingPhase(aiStepCallback);
        if (game.isGameOver()) {
            return;
        }

        placeUnitPhase(aiStepCallback);
        if (game.isGameOver()) {
            return;
        }

        moveUnitPhase(aiStepCallback);
        if (game.isGameOver()) {
            return;
        }

        endTurnPhase(aiStepCallback);
    }

    /**
     * Executes the AI player's move for the King.
     *
     * @throws GameLogicException If there is an error during move execution.
     */
    private void moveKingPhase(AIStepListener listener) throws GameLogicException {
        Position kingPos = aiTeam.getKing().getPosition();
        KingEvaluator kingEvaluator = new KingEvaluator(board, aiTeam, enemyTeam);
        List<ScoredActions<Position>> scoredMoves = kingEvaluator.scoreMove(kingPos);

        // Find the maximum score among the scored moves
        List<Position> bestMoves = findBestOptions(scoredMoves, item -> item.score(), item -> item.action());
        // Randomly select one of the best moves
        Position chosenMove = weightedRandom(bestMoves);

        // Move the King to the chosen position
        List<String> logs = game.executeMove(aiTeam, kingPos, chosenMove);
        listener.onStepExecuted(logs, chosenMove);
    }

    /**
     * Executes the AI player's placement of a unit from the hand onto the board.
     *
     * @throws GameLogicException If there is an error during move execution.
     */
    private void placeUnitPhase(AIStepListener listener) throws GameLogicException {
        Position kingPos = aiTeam.getKing().getPosition();
        FieldsEvaluator fieldsEvaluator = new FieldsEvaluator(board, aiTeam, enemyTeam);
        List<ScoredActions<Position>> scoredFields = fieldsEvaluator.scorePlacement(kingPos);

        if (scoredFields.isEmpty()) {
            return; // No valid placements available
        }

        // Find the maximum score among the scored placements
        List<Position> bestFields = findBestOptions(scoredFields, item -> item.score(), item -> item.action());
        Position chosenField = weightedRandom(bestFields);

        // Randomly select an unit from the player's hand to place on the chosen field
        int chosenUnitIndex = chooseUnitToPlace(aiTeam.getHand());

        // Place the chosen unit on the chosen field
        if (chosenUnitIndex != -1) {
            int oneBasedIndex = chosenUnitIndex + 1; // Convert to 1-based index
            List<String> logs = game.executePlace(aiTeam, List.of(oneBasedIndex), chosenField);

            boolean isPlacementFailed = false;
            for (String log : logs) {
                if (log.contains(EventLog.COMBINE_FAIL.getTemplate())) {
                    isPlacementFailed = true;
                    break;
                }
            }
            if (isPlacementFailed) {
                listener.onStepExecuted(logs, kingPos);
            } else {
                listener.onStepExecuted(logs, chosenField);
            }
        }
    }

    /**
     * Evaluates and executes the AI player's actions for all active units on the
     * board.
     *
     * @throws GameLogicException If there is an error during move execution.
     */
    private void moveUnitPhase(AIStepListener listener) throws GameLogicException {
        UnitMoveEvaluator unitMoveEvaluator = new UnitMoveEvaluator(board, aiTeam, enemyTeam);

        while (true) {
            List<EvaluatedUnit> allUnits = unitMoveEvaluator.evaluateAllUnits();
            if (allUnits.isEmpty() || game.isGameOver()) {
                break; // No active units to move
            }

            // Find the maximum score among all evaluated units
            List<EvaluatedUnit> bestUnits = findBestOptions(allUnits, u -> u.totalScore(), u -> u);
            // Randomly select one of the best units
            EvaluatedUnit bestUnit = weightedRandom(bestUnits);
            // Find the best action for the chosen unit
            ActionType bestAction = findBestAction(bestUnit.actions());

            Position fromPos = bestUnit.unit().getPosition();
            if (fromPos == null) {
                continue; // Skip units that are not on the board
            }

            List<String> logs = new ArrayList<>();
            Position targetPos;
            if (bestAction == ActionType.BLOCK) {
                logs.addAll(game.executeBlock(aiTeam, fromPos));
                targetPos = fromPos;
            } else {
                targetPos = bestAction.getTargetPosition(fromPos);
                logs.addAll(game.executeMove(aiTeam, fromPos, targetPos));
            }

            Position displayPos = (game.isGameOver() && game.getCurrentTurn().equals(aiTeam)) ? fromPos : targetPos;
            listener.onStepExecuted(logs, displayPos);
            if (game.isGameOver()) {
                break;
            }

        }
    }

    /**
     * Executes the AI player's end-of-turn phase, including discarding cards if the
     * hand is full.
     *
     * @throws GameLogicException If there is an error during move execution.
     */
    private void endTurnPhase(AIStepListener listener) throws GameLogicException {
        List<Unit> hand = aiTeam.getHand();
        Unit discardedUnit = null;
        if (hand.size() >= GameConstants.MAX_HAND_SIZE) {
            // If the player's hand is full, randomly discard one card
            List<Integer> discardWeights = new ArrayList<>();
            for (Unit unit : hand) {
                discardWeights.add(Math.max(unit.getAtk() + unit.getDef(), 0));
            }

            // Randomly select a card to discard based on the weights
            int discardIndex = RandomUtils.inverseWeightedRandom(discardWeights, random);
            discardedUnit = hand.get(discardIndex);
        }
        List<String> logs = game.endTurn(aiTeam, discardedUnit);
        listener.onStepExecuted(logs, null);
    }

    // --- HELPER METHODS ---

    /**
     * Chooses a unit from the player's hand to place on the board based on a
     * weighted random selection, where the weights are determined by the attack
     * value of the units.
     *
     * @param hand The list of units in the player's hand.
     * @return The index of the chosen unit to place, or null if the hand is empty.
     */
    private int chooseUnitToPlace(List<Unit> hand) {
        if (hand.isEmpty()) {
            return -1; // No units to place
        }

        List<Integer> weights = new ArrayList<>();
        for (Unit unit : hand) {
            weights.add(unit.getAtk());
        }

        return RandomUtils.weightedRandom(weights, random);
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
            return candidates.get(0); // Only one candidate, return it directly
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
    private <T, R> List<R> findBestOptions(List<T> options, Scorer<T> scorer, Extractor<T, R> extractor) {
        List<R> bestValues = new ArrayList<>();
        int maxScore = Integer.MIN_VALUE;
        for (T option : options) {
            int score = scorer.getScore(option);
            if (score > maxScore) {
                maxScore = score;
                bestValues.clear();
                bestValues.add(extractor.extract(option));
            } else if (score == maxScore) {
                bestValues.add(extractor.extract(option));
            }
        }
        return bestValues;
    }

    // --- GETTERS ---

    /**
     * Returns the AI player's team.
     * 
     * @return The AI player's team.
     */
    public Team getAiTeam() {
        return aiTeam;
    }
}
