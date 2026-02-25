package logic.combination;

import java.util.List;

import model.Unit;
import model.UnitTemplate;
import utils.StringConstants;

/**
 * A class responsible for combining two units according to specific combination
 * rules. The combination process checks each rule in a defined order and
 * applies the first valid rule it finds. If no rules are applicable, the
 * combination fails and returns null.
 * 
 * @author udqch
 */
public class UnitCombiner {
    private final List<CombineRule> rules = List.of(
            new SymbiosisRule(),
            new LikeMindedRule(),
            new PrimeRule());

    /**
     * Combines two units according to the defined combination rules. If the units
     * cannot be combined, this method returns null.
     *
     * @param a the first unit to combine
     * @param b the second unit to combine
     * @return a new Unit resulting from the combination of a and b, or null if
     *         they cannot be combined
     */
    public static Unit combine(Unit a, Unit b) {
        if (a.getName().equals(b.getName())) {
            return null; // Cannot combine units with the same name
        }

        CombineStats finalStats = null;
        for (CombineRule rule : new UnitCombiner().rules) {
            finalStats = rule.check(a, b);
            if (finalStats != null) {
                break; // Stop checking further rules if a valid combination is found
            }
        }
        if (finalStats == null) {
            return null; // No valid combination found
        }

        String newQualifier = b.getQualifier() + StringConstants.SPACE + a.getQualifier();
        String newRole = b.getRole();
        UnitTemplate newTemplate = new UnitTemplate(newQualifier, newRole, finalStats.newAtk(), finalStats.newDef());
        Unit combinedUnit = new Unit(newTemplate, a.getOwner());
        combinedUnit.setHidden(a.isHidden() || b.isHidden()); // The combined unit is hidden if either of the original
                                                              // units is hidden
        combinedUnit.setHasMoved(false);
        return combinedUnit;
    }
}
