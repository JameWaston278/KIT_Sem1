package logic.combination;

import model.Unit;

/**
 * An interface representing a rule for combining two units. Implementations of
 * this interface will define specific rules for how units can be combined and
 * what the resulting stats should be.
 * 
 * @author udqch
 */
public interface CombineRule {

    /**
     * Checks if the two units can be combined according to this rule.
     * 
     * @param a The first unit.
     * @param b The second unit.
     * @return A CombineStats object containing the result of the check.
     */
    CombineStats check(Unit a, Unit b);
}
