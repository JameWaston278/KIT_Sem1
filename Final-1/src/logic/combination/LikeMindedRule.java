package logic.combination;

import model.Unit;
import utils.MathUtils;

/**
 * A class representing the like-minded combination rule. This rule allows two
 * units to combine if the greatest common divisor (GCD) of their attack stats
 * and the GCD of their defense stats have a GCD greater than 100. If these
 * conditions are met, the resulting combined unit will have an attack stat
 * equal to the sum of the two units' attack stats minus the GCD of the GCDs,
 * and a defense stat equal to the sum of the two units' defense stats minus the
 * GCD of the GCDs.
 * 
 * @author udqch
 */
public class LikeMindedRule implements CombineRule {

    @Override
    public CombineStats check(Unit a, Unit b) {
        int gcdAtk = MathUtils.gcd(a.getAtk(), b.getAtk());
        int gcdDef = MathUtils.gcd(a.getDef(), b.getDef());
        int g3T = MathUtils.gcd(gcdAtk, gcdDef);
        if (g3T > 100) {
            int newAtk = a.getAtk() + b.getAtk() - g3T;
            int newDef = a.getDef() + b.getDef() - g3T;
            return new CombineStats(newAtk, newDef);
        }
        return null;
    }

}
