package model;

import java.util.ArrayList;
import java.util.List;

import exceptions.GameLogicException;
import logic.Duel;
import logic.DuelResult;
import logic.combination.UnitCombiner;
import message.ErrorMessage;
import message.EventLog;

/**
 * Helper class to execute a move action in the game, handling all the logic
 * related to moving units, resolving duels, combining units, and updating the
 * game state accordingly.
 * 
 * @author udqch
 */
final class MoveHelper {

    private MoveHelper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Executes a move action for a unit from one position to another, handling all
     * the game logic involved in the process.
     *
     * @param game    The current game state.
     * @param team    The team performing the move.
     * @param fromPos The starting position of the unit.
     * @param toPos   The target position for the move.
     * @return A list of log messages describing the events that occurred during the
     *         move.
     * @throws GameLogicException If any game rule is violated during the move.
     */
    static List<String> executeMove(Game game, Team team, Position fromPos, Position toPos) throws GameLogicException {
        List<String> logs = new ArrayList<>();
        Board board = game.getBoard();
        Unit unit = board.getUnitAt(fromPos);

        if (unit == null || !unit.getOwner().equals(team)) {
            throw new GameLogicException(ErrorMessage.INVALID_UNIT.format());
        }

        if (unit.hasMoved()) {
            throw new GameLogicException(ErrorMessage.UNIT_ALREADY_MOVED.format(unit.getName()));
        }

        if (unit.isKing()) {
            moveKing(game, fromPos, toPos, logs);
            unit.setHasMoved(true);
            return logs;
        }

        if (unit.isBlocking()) {
            unit.setBlocking(false);
            logs.add(EventLog.NO_LONGER_BLOCKS.format(unit.getName()));
        }

        Unit targetUnit = board.getUnitAt(toPos);
        if (targetUnit == null || fromPos.equals(toPos)) {
            // Situation 1: Target position is empty or the unit stays in place
            board.moveUnit(fromPos, toPos);
            logs.add(EventLog.MOVES_TO.format(unit.getName(), toPos.toString()));
        } else if (targetUnit.getOwner().equals(team)) {
            // If the target position has a friendly unit, combine them.
            combineUnits(game, unit, targetUnit, logs);
        } else {
            // If the target position has an enemy unit, resolve a duel.
            handleDuel(game, unit, targetUnit, logs);
        }

        unit.setHasMoved(true);
        game.checkWinCondition(logs); // Check for win condition after the move
        return logs;
    }

    private static void moveKing(Game game, Position fromPos, Position toPos, List<String> logs)
            throws GameLogicException {
        Board board = game.getBoard();
        Unit king = board.getUnitAt(fromPos);
        if (fromPos.equals(toPos)) { // King chooses to stay in place
            logs.add(EventLog.MOVES_TO.format(king.getName(), toPos.toString()));
            return;
        }

        Unit targetUnit = board.getUnitAt(toPos);
        Team kingTeam = king.getOwner();
        if (targetUnit != null) {
            if (!targetUnit.getOwner().equals(kingTeam)) {
                throw new GameLogicException(ErrorMessage.KING_CANNOT_DUEL.format());
            } else {
                // If the target is a friendly unit, eliminate it and move the King.
                game.eliminateUnit(targetUnit);
                logs.add(EventLog.ELIMINATED.format(targetUnit.getName()));
            }
        }
        board.moveUnit(fromPos, toPos);
        logs.add(EventLog.MOVES_TO.format(king.getName(), toPos.toString()));
    }

    private static void combineUnits(Game game, Unit unitA, Unit unitB, List<String> logs) throws GameLogicException {
        Position posA = unitA.getPosition();
        Position posB = unitB.getPosition();
        logs.add(EventLog.MOVES_TO.format(unitA.getName(), posB.toString()));
        logs.add(EventLog.JOIN_FORCES.format(unitA.getName(), unitB.getName(), posB.toString()));

        Board board = game.getBoard();
        Unit combinedUnit = UnitCombiner.combine(unitA, unitB);
        if (combinedUnit == null) {
            // Combination failed, eliminate unitB and move unitA to unitB's position.
            game.eliminateUnit(unitB);
            board.moveUnit(posA, posB);
            logs.add(EventLog.COMBINE_FAIL.format(unitB.getName()));
        } else {
            // Combination succeeded, eliminate both units and place the combined unit at
            // unitB's position.
            game.eliminateUnit(unitA);
            game.eliminateUnit(unitB);

            board.placeUnitAt(combinedUnit, posB);
            combinedUnit.setHasMoved(false); // The combined unit can move again this turn
            combinedUnit.getOwner().addActiveUnit(combinedUnit);

            logs.add(EventLog.COMBINE_SUCCESS.format());
        }
    }

    private static void handleDuel(Game game, Unit attacker, Unit defender, List<String> logs)
            throws GameLogicException {
        Position fromPos = attacker.getPosition();
        Position toPos = defender.getPosition();
        String defName = defender.isHidden() ? "???" : defender.getName();
        if (defender.isKing() || defender.isHidden()) {
            logs.add(EventLog.ATTACK.format(
                    attacker.getName(), attacker.getAtk(), attacker.getDef(), defName, toPos.toString()));
        } else {
            logs.add(EventLog.ATTACK_WITH_DEF_STATS.format(
                    attacker.getName(), attacker.getAtk(), attacker.getDef(), defender.getName(), defender.getAtk(),
                    defender.getDef(), toPos.toString()));
        }

        if (attacker.isHidden()) {
            attacker.faceUp();
            logs.add(
                    EventLog.FLIP.format(attacker.getName(), attacker.getAtk(), attacker.getDef(), fromPos.toString()));
        }
        if (defender.isHidden()) {
            defender.faceUp();
            logs.add(EventLog.FLIP.format(defender.getName(), defender.getAtk(), defender.getDef(), toPos.toString()));
        }

        Team atkTeam = attacker.getOwner();
        Team defTeam = defender.getOwner();
        DuelResult result = Duel.resolveDuel(attacker, defender);

        // Eliminate unit based on the duel result
        if (result.defenderDies()) {
            logs.add(EventLog.ELIMINATED.format(defender.getName()));
            game.eliminateUnit(defender);
        }
        if (result.attackerDies()) {
            logs.add(EventLog.ELIMINATED.format(attacker.getName()));
            game.eliminateUnit(attacker);
        }

        // Apply damage to both teams based on the duel result
        if (result.attackerDamageTaken() > 0) {
            atkTeam.decreaseLp(result.attackerDamageTaken());
            logs.add(EventLog.DAMAGE.format(atkTeam.getName(), result.attackerDamageTaken()));
        }
        if (result.defenderDamageTaken() > 0) {
            defTeam.decreaseLp(result.defenderDamageTaken());
            logs.add(EventLog.DAMAGE.format(defTeam.getName(), result.defenderDamageTaken()));
        }

        // Move the attacker to the defender's position
        if (result.defenderDies() && !result.attackerDies() && !defender.isKing()) {
            game.getBoard().moveUnit(fromPos, toPos);
            logs.add(EventLog.MOVES_TO.format(attacker.getName(), toPos.toString()));
        }
    }
}