package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import exceptions.GameLogicException;
import logic.combination.UnitCombiner;
import message.ErrorMessage;
import message.EventLog;
import utils.GameConstants;

/**
 * The PlaceUnitHelper class provides a static method to handle the logic of
 * placing a unit on the board during the game. It checks for valid placement
 * conditions, manages the combination of units if necessary, and updates the
 * game state accordingly. This helper class is designed to encapsulate the
 * specific logic related to placing units, making the code more modular and
 * easier to maintain.
 * 
 * @author udqch
 */
final class PlaceUnitHelper {
    private PlaceUnitHelper() {
        // Private constructor to prevent instantiation of this utility class
    }

    /**
     * Handles the logic for placing a unit on the board. It checks if the player
     * has already placed a unit this turn, validates the target position, checks
     * for valid hand indices, and manages the combination of units if the target
     * position is occupied by a friendly unit. It also updates the game state and
     * logs relevant events.
     *
     * @param game        The current game state.
     * @param team        The team placing the unit.
     * @param handIndices The list of indices from the player's hand representing
     *                    the units to be placed.
     * @param targetPos   The target position on the board where the unit(s) should
     *                    be placed.
     * @return A list of event log messages generated during this action.
     * @throws GameLogicException If any validation fails during the placement
     *                            process,
     *                            such as invalid placement conditions or hand
     *                            indices.
     */
    static List<String> placeUnit(Game game, Team team, List<Integer> handIndices, Position targetPos)
            throws GameLogicException {
        game.getBoard().isValid(targetPos); // Validate the target position first
        List<Unit> unitsToPlace = validateInput(game, team, handIndices, targetPos);

        List<String> logs = new ArrayList<>();

        for (Unit unit : unitsToPlace) {
            team.discardCard(unit);
        }

        for (Unit unit : unitsToPlace) {
            logs.add(EventLog.PLACES.format(team.getName(), unit.getName(), targetPos.toString()));

            if (team.getActiveUnits().size() >= GameConstants.MAX_ACTIVE_UNITS) {
                logs.add(EventLog.ELIMINATED.format(unit.getName()));
                continue;
            }

            Unit targetUnit = game.getBoard().getUnitAt(targetPos);
            if (targetUnit == null) {
                // If the target position is empty, simply place the unit there
                game.getBoard().placeUnitAt(unit, targetPos);
                team.addActiveUnit(unit);
                unit.setHasMoved(false);
            } else {
                // If the target position is occupied by a friendly unit, combine them
                combineUnits(game, unit, targetUnit, logs);
            }
        }

        game.setHasPlaceUnitInTurn(true);
        game.checkWinCondition(logs);
        return logs;
    }

    private static List<Unit> validateInput(Game game, Team team, List<Integer> handIndices, Position targetPos)
            throws GameLogicException {
        if (game.hasPlaceUnitInTurn()) {
            throw new GameLogicException(ErrorMessage.ALREADY_PLACED_UNIT.format());
        }

        Position kingPos = team.getKing().getPosition();
        int rowDiff = Math.abs(kingPos.row() - targetPos.row());
        int colDiff = Math.abs(kingPos.col() - targetPos.col());
        if (rowDiff > 1 || colDiff > 1) {
            throw new GameLogicException(ErrorMessage.INVALID_PLACEMENT.format(targetPos.toString()));
        }

        Unit existingUnit = game.getBoard().getUnitAt(targetPos);
        if (existingUnit != null && !existingUnit.getOwner().equals(team)) {
            throw new GameLogicException(ErrorMessage.INVALID_PLACEMENT.format(targetPos.toString()));
        }

        List<Unit> hand = team.getHand();
        Set<Integer> uniqueIndices = new HashSet<>();
        List<Unit> unitsToPlace = new ArrayList<>();

        for (int idx : handIndices) {
            if (idx < 1 || idx > hand.size()) {
                throw new GameLogicException(ErrorMessage.INVALID_CARD_INDEX.format(idx));
            }
            if (!uniqueIndices.add(idx)) {
                throw new GameLogicException(ErrorMessage.DUPLICATE_CARD_INDEX.format(idx));
            }
            unitsToPlace.add(hand.get(idx - 1));
        }
        return unitsToPlace;
    }

    private static void combineUnits(Game game, Unit newUnit, Unit targetUnit, List<String> logs) {
        Team team = newUnit.getOwner();
        Position pos = targetUnit.getPosition();
        logs.add(EventLog.JOIN_FORCES.format(newUnit.getName(), targetUnit.getName(), pos.toString()));

        Unit combinedUnit = UnitCombiner.combine(newUnit, targetUnit);
        game.eliminateUnit(targetUnit);
        if (combinedUnit != null) {
            // If the combination is successful, replace the target unit with the combined
            // unit
            game.getBoard().placeUnitAt(combinedUnit, pos);
            team.addActiveUnit(combinedUnit);
            combinedUnit.setHasMoved(false); // The combined unit can still move this turn
            logs.add(EventLog.COMBINE_SUCCESS.format());
        } else {
            // If the combination fails, eliminate the target unit and replace with the new
            // unit
            game.getBoard().placeUnitAt(newUnit, pos);
            team.addActiveUnit(newUnit);
            newUnit.setHasMoved(true);
            logs.add(EventLog.COMBINE_FAIL.format(targetUnit.getName()));
        }
    }
}
