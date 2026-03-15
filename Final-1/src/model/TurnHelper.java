package model;

import java.util.ArrayList;
import java.util.List;

import exceptions.GameLogicException;
import message.ErrorMessage;
import message.EventLog;
import utils.GameConstants;

/**
 * The TurnHelper class provides methods for managing the start and end
 * of turns in the game. It handles resetting unit statuses, drawing cards,
 * checking win conditions, and switching turns between teams.
 * 
 * @author udqch
 */
final class TurnHelper {
    private TurnHelper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Starts the turn for the specified team. This method resets the moved status
     * of all units on the team, allows the team to draw a card, and checks for
     * win conditions at the start of the turn.
     * 
     * @param game The game instance to operate on.
     * @param team The team whose turn is starting.
     * @return A list of event log messages generated during progress.
     */
    static List<String> startTurn(Game game, Team team) {
        List<String> logs = new ArrayList<>();
        team.resetAllMovedStatus();
        game.setHasPlaceUnitInTurn(false);

        if (team.isDeckEmpty()) {
            // If the team has no cards left to draw, they lose immediately.
            Team winner = (team == game.getTeam1()) ? game.getTeam2() : game.getTeam1();
            game.setWinner(winner);
            game.setGameOver(true);
            logs.add(EventLog.DECK_EMPTY.format(team.getName()));
            logs.add(EventLog.WINS.format(winner.getName()));
            return logs;
        }

        team.drawCard();
        game.checkWinCondition(logs);
        return logs;
    }

    /**
     * Ends the current turn for the specified team. This method checks if the
     * team has a full hand and requires discarding a card, then switches the turn
     * to the other team and starts their turn.
     * 
     * @param game          The game instance to operate on.
     * @param team          The team whose turn is ending.
     * @param unitToDiscard The unit to discard from the team's hand if their hand
     *                      is full. If the hand is not full, this should be
     * @return A list of event log messages generated during progress.
     * @throws GameLogicException If there is an error during end turn processing,
     *                            such as trying to discard a card when the hand
     *                            is not full, or not discarding a card when the
     *                            hand is full.
     */
    static List<String> endTurn(Game game, Team team, Unit unitToDiscard) throws GameLogicException {
        List<String> logs = new ArrayList<>();

        Team currentTeam = game.getCurrentTurn();
        if (team != currentTeam) {
            throw new GameLogicException(ErrorMessage.WRONG_TURN.format(team.getName()));
        }

        int currentHandSize = team.getHand().size();
        if (currentHandSize == GameConstants.MAX_HAND_SIZE) {
            if (unitToDiscard == null) {
                throw new GameLogicException(ErrorMessage.HAND_FULL.format(team.getName()));
            }
            team.discardCard(unitToDiscard);
            logs.add(EventLog.DISCARDED.format(
                    team.getName(), unitToDiscard.getName(), unitToDiscard.getAtk(), unitToDiscard.getDef()));
        } else {
            if (unitToDiscard != null) {
                throw new GameLogicException(ErrorMessage.HAND_NOT_FULL.format(team.getName()));
            }
        }

        Team nextTeam = (currentTeam == game.getTeam1()) ? game.getTeam2() : game.getTeam1();
        game.setCurrentTurn(nextTeam);
        logs.add(EventLog.SWITCH_TURNS.format(nextTeam.getName()));
        logs.addAll(startTurn(game, nextTeam));

        return logs;
    }
}
