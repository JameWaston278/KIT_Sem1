package logic.combination;

import model.Unit;

/**
 * A class representing the symbiosis combination rule. This rule allows two
 * units to combine if one unit's attack is greater than the other's attack, and
 * the stronger unit's attack is equal to the weaker unit's defense, and the
 * stronger unit's defense is equal to the weaker unit's attack. If these
 * conditions are met, the resulting combined unit will have the stronger unit's
 * attack and the weaker unit's defense.
 * 
 * @author udqch
 */
public class SymbiosisRule implements CombineRule {

    @Override
    public CombineStats check(Unit a, Unit b) {
        Unit stronger = (a.getAtk() > b.getAtk()) ? a : b;
        Unit weaker = (stronger == a) ? b : a;
        if (stronger.getAtk() > weaker.getAtk()
                && stronger.getAtk() == weaker.getDef()
                && stronger.getDef() == weaker.getAtk()) {
            return new CombineStats(stronger.getAtk(), weaker.getDef());
        }
        return null;
    }

}
