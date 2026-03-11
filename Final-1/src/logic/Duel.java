package logic;

import exceptions.GameLogicException;
import model.Unit;

/**
 * The Duel class encapsulates the logic of a duel between an attacking unit and
 * a defending unit on the board.
 * It handles the resolution of the duel, including revealing hidden units,
 * calculating damage, and moving units if the attacker wins.
 * 
 * @author udqch
 */
public final class Duel {

    private Duel() {
        // Private constructor to prevent instantiation
    }

    /**
     * Resolves the duel by comparing the attacker's stats against the
     * defender's stats, taking into account special conditions such as blocking
     * or if the defender is a King.
     * 
     * @param attacker The Unit that is initiating the attack.
     * @param defender The Unit that is being attacked.
     * 
     * @return A DuelResult object containing the outcome of the duel, including
     *         damage dealt and elimination status.
     * 
     * @throws GameLogicException If there is an error during the duel resolution
     *                            (e.g., invalid positions, unit not found).
     */
    public static DuelResult resolveDuel(Unit attacker, Unit defender) {
        // Resolve the duel by comparing ATK and DEF values.
        if (defender.isKing()) {
            return attackKing(attacker);
        } else if (defender.isBlocking()) {
            return attackBlockage(attacker, defender);
        } else {
            return attackStandard(attacker, defender);
        }
    }

    private static DuelResult attackKing(Unit attacker) {
        // Attacker wins, but the King is not eliminated.
        return new DuelResult(0, attacker.getAtk(), false, false);
    }

    private static DuelResult attackBlockage(Unit attacker, Unit defender) {
        if (attacker.getAtk() > defender.getDef()) {
            // Attacker wins and defender is eliminated.
            return new DuelResult(0, 0, false, true);
        } else if (attacker.getAtk() < defender.getDef()) {
            int damage = defender.getDef() - attacker.getAtk();
            // Defender wins, attacker does not move.
            return new DuelResult(damage, 0, false, false);
        } else {
            // No one wins if ATK and DEF are equal.
            return new DuelResult(0, 0, false, false);
        }
    }

    private static DuelResult attackStandard(Unit attacker, Unit defender) {
        // Calculate the difference in ATK values to determine damage.
        int diff = Math.abs(attacker.getAtk() - defender.getAtk());

        if (attacker.getAtk() > defender.getAtk()) {
            // Attacker wins, defender is eliminated.
            return new DuelResult(0, diff, false, true);
        } else if (attacker.getAtk() < defender.getAtk()) {
            // Defender wins, attacker is eliminated.
            return new DuelResult(diff, 0, true, false);
        } else {
            // ATK values are equal, both units are eliminated.
            return new DuelResult(0, 0, true, true);
        }
    }
}