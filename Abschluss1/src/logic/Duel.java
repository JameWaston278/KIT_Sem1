package logic;

import model.Game;
import model.Unit;
import utils.Constants;

public class Duel {
    public static void resolveDuel(Game game, Unit attacker, Unit defender, String attackerPos, String defenderPos) {
        // Display notification of the attack.
        String defName = (defender.isHidden()) ? Constants.HIDDEN_VALUE : defender.getName();
        String atkStats = String.format(Constants.STATS_DISPLAY, attacker.getAtk(), attacker.getDef());
        String defStats = (defender.isKing()) ? Constants.EMPTY
                : String.format(Constants.STATS_DISPLAY, defender.getAtk(), defender.getDef());
        System.out.println(
                String.format(Constants.ATTACK_EVENT, attacker.getName(), atkStats, defName, defStats, defenderPos));

        // Reveal both units if they are hidden.
        if (attacker.isHidden()) {
            attacker.faceUp();
            System.out.println(String.format(Constants.FLIP_TEMPLATE, attacker.getName(), atkStats, defenderPos));
        }
        if (defender.isHidden()) {
            defender.faceUp();
            System.out.println(String.format(Constants.FLIP_TEMPLATE, defName, defStats, attackerPos));
        }

        // Resolve the duel by comparing ATK and DEF values.
    }
}
