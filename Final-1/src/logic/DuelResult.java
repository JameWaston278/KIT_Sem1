package logic;

/**
 * The DuelResult record encapsulates the outcome of a duel between two units,
 * including the damage taken by each unit and whether each unit dies as a
 * result of the duel.
 * 
 * @param attackerDamageTaken The amount of damage taken by the attacking unit.
 * @param defenderDamageTaken The amount of damage taken by the defending unit.
 * @param attackerDies        A boolean indicating whether the attacking unit
 *                            dies.
 * @param defenderDies        A boolean indicating whether the defending unit
 *                            dies.
 * @author udqch
 */
public record DuelResult(
        int attackerDamageTaken,
        int defenderDamageTaken,
        boolean attackerDies,
        boolean defenderDies) {
}
