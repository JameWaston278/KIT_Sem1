package logic.combination;

import model.Unit;
import utils.MathUtils;

/**
 * A class representing the prime combination rule. This rule allows two units
 * to combine if the greatest common divisor (GCD) of their attack stats and the
 * GCD of their defense stats have a GCD equal to 100, and either both units'
 * attack stats divided by 100 are prime numbers, or both units' defense stats
 * divided by 100 are prime numbers. If these conditions are met, the resulting
 * combined unit will have an attack stat equal to the sum of the two units'
 * attack stats, and a defense stat equal to the sum of the two units' defense
 * stats.
 * 
 * @author udqch
 */
public class PrimeRule implements CombineRule {

    @Override
    public CombineStats check(Unit a, Unit b) {
        int gcdAtk = MathUtils.gcd(a.getAtk(), b.getAtk());
        int gcdDef = MathUtils.gcd(a.getDef(), b.getDef());
        int g3T = MathUtils.gcd(gcdAtk, gcdDef);
        if (g3T == 100
                && ((MathUtils.isPrime(a.getAtk() / 100) && MathUtils.isPrime(b.getAtk() / 100))
                        || (MathUtils.isPrime(a.getDef() / 100) && MathUtils.isPrime(b.getDef() / 100)))) {
            int newAtk = a.getAtk() + b.getAtk();
            int newDef = a.getDef() + b.getDef();
            return new CombineStats(newAtk, newDef);
        }
        return null;
    }
}
