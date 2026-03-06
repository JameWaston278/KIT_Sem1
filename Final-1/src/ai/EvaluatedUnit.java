package ai;

import java.util.List;

import model.Unit;

/**
 * The EvaluatedUnit record encapsulates a unit along with its total score and a
 * list of scored actions.
 * This structure is used to represent the evaluation of a unit's potential
 * actions in the game.
 * 
 * @param unit       The unit being evaluated.
 * @param totalScore The total score calculated for the unit based on its
 *                   potential actions.
 * @param actions    A list of scored actions, where each action is associated
 *                   with a score indicating its effectiveness.
 * 
 * @author udqch
 */
public record EvaluatedUnit(
        Unit unit,
        int totalScore,
        // List of scored actions for the unit (e.g., move, block, stay in place) and
        // their associated scores.
        List<ScoredActions<ActionType>> actions) {
}
