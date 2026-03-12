package cli;

import java.util.List;

import exceptions.GameLogicException;
import exceptions.InvalidCommandException;
import model.Game;
import model.Team;
import model.Unit;

/**
 * CommandYield parses the yield command, determining if a card needs to be
 * discarded, and calls the game model to end the turn.
 * 
 * @author udqch
 */
final class CommandYield {

    private CommandYield() {
        // Private constructor to prevent instantiation
    }

    /**
     * Executes the yield command.
     * 
     * @param parts The command parts, where parts[0] is "yield" and optionally
     *              parts[1] is the index of the card to discard.
     * @param game  The game instance to operate on.
     * @return A list of log messages resulting from the command execution.
     * @throws GameLogicException      If the game logic rules are violated (e.g.,
     *                                 trying to yield when not allowed).
     * @throws InvalidCommandException If the command format is invalid (e.g., too
     *                                 many arguments, non-integer index, or index
     *                                 out of range).
     */
    static List<String> execute(String[] parts, Game game) throws GameLogicException, InvalidCommandException {
        // Validate command format: "yield" or "yield <index>"
        if (parts.length > 2) {
            throw new InvalidCommandException();
        }

        Team currentTeam = game.getCurrentTurn();
        Unit unitToDiscard = null;

        // If the player provided an index, validate it and determine which card to
        // discard
        if (parts.length == 2) {
            try {
                int idx = Integer.parseInt(parts[1]);
                List<Unit> hand = currentTeam.getHand();

                if (idx < 1 || idx > hand.size()) {
                    throw new InvalidCommandException();
                }

                // Convert to 0-based index to retrieve Unit from List
                unitToDiscard = hand.get(idx - 1);

            } catch (NumberFormatException e) {
                // If the second part is not a valid integer, it's an invalid command
                throw new InvalidCommandException();
            }
        }

        return game.endTurn(currentTeam, unitToDiscard);
    }
}