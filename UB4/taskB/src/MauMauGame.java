import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents the core logic and state of a Mau-Mau card game.
 * This class manages the card piles (draw and discard), players, turn
 * management, and enforces the game rules.
 *
 * @author udqch
 * @version 1.0
 */
public class MauMauGame {

    private final int numberOfPlayer;
    private int currentPlayerIndex;
    private final List<Player> players;
    private final List<Card> drawPile; // The pile to draw cards from (Aufnahmestapel)
    private final List<Card> discardPile; // The pile to discard cards to (Ablagestapel)
    private boolean isRunning;
    private boolean endRound;

    /**
     * Constructs a new MauMauGame instance.
     * Initializes the lists for players and card piles and sets the default
     * configuration.
     */
    public MauMauGame() {
        this.numberOfPlayer = 4;
        this.currentPlayerIndex = 0;
        this.players = new ArrayList<>();
        this.drawPile = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        this.isRunning = true;
        this.endRound = false;
    }

    // GAME CONTROL METHODS (Start, Quit)

    /**
     * Starts a new game with a specific random seed.
     * This method resets the game state, recreates the deck, shuffles it using the
     * provided seed, deals 5 cards to each player, and places the first card on the
     * discard pile.
     *
     * @param seed The seed for the random number generator
     * @throws ErrorException If the game execution has been stopped (quit) and
     *                        cannot be restarted.
     */
    public void start(int seed) throws ErrorException {
        if (!this.isRunning) {
            throw new ErrorException(GameMessage.GAME_ENDED.format());
        }

        resetGameState();

        // 1. Shuffle the deck
        Collections.shuffle(this.drawPile, new Random(seed));

        // 2. Deal 5 cards to each player
        for (int id = 0; id < numberOfPlayer; id++) {
            List<Card> initialHand = new ArrayList<>();
            for (int k = 0; k < 5; k++) {
                initialHand.add(drawPile.get(0));
                drawPile.remove(0);
            }
            // Player ID is 1-based (1, 2, 3, 4)
            this.players.add(new Player(id + 1, initialHand));
        }

        // 3. Place the first card onto the discard pile
        discardPile.add(drawPile.get(0));
        drawPile.remove(0);

        System.out.println("Player " + (this.currentPlayerIndex + 1) + " takes the turn.");
    }

    /**
     * Stops the game execution loop.
     * Once called, the game cannot be restarted via the start command.
     */
    public void quit() {
        this.isRunning = false;
    }

    // PLAYER ACTION METHODS (Discard, Pick)

    /**
     * Handles the action of a player discarding a card.
     * Validates the turn, card ownership, and game rules before executing the move.
     *
     * @param playerId The ID of the player making the move.
     * @param cardName The string representation of the card (e.g., "7H").
     * @throws ErrorException If:
     *                        - It is not the player's turn.
     *                        - The player does not hold the specified card.
     *                        - The move violates Mau-Mau rules.
     *                        - The game ends (Player wins).
     */
    public void discard(int playerId, String cardName) throws ErrorException {
        checkTurn(playerId);

        Player player = getPlayerById(playerId);
        Card cardToPlay = player.findCard(cardName);

        // Check if player actually holds the card
        if (cardToPlay == null) {
            throw new ErrorException(GameMessage.CARD_NOT_FOUND.format(playerId, cardName));
        }

        // Check game rules (Suit/Rank match)
        if (!isValidMove(cardToPlay)) {
            throw new ErrorException(GameMessage.INVALID_MOVE.format(cardName, getTopCard().toString()));
        }

        // Execute the move
        player.removeCard(cardToPlay);
        this.discardPile.add(cardToPlay);

        // Check winning condition
        if (player.getDeck().isEmpty()) {
            this.endRound = true;
            throw new ErrorException(GameMessage.GAME_WON.format(this.currentPlayerIndex + 1), false);
        }

        nextTurn();
    }

    /**
     * Handles the action of a player picking a card from the draw pile.
     * The player draws one card. If the draw pile becomes empty, the game ends in a
     * draw.
     *
     * @param playerId The ID of the player picking the card.
     * @throws ErrorException If:
     *                        - It is not the player's turn.
     *                        - The game ends (Draw condition).
     */
    public void pick(int playerId) throws ErrorException {
        checkTurn(playerId);

        Player player = getPlayerById(playerId);

        // Move top card from draw pile to player's deck
        Card drawCard = drawPile.get(0);
        player.addCard(drawCard);
        drawPile.remove(0);

        // Check draw condition (Game ends in a draw if the last card is picked)
        if (drawPile.isEmpty()) {
            this.endRound = true;
            throw new ErrorException(GameMessage.GAME_DRAW.format(), false);
        }

        nextTurn();
    }

    // VIEW / INFO METHODS (Show)

    /**
     * Prints the current state of the game piles.
     * Output format: [Top Card Identifier] / [Draw Pile Size]
     */
    public void showGame() throws ErrorException {
        if (discardPile.isEmpty()) {
            return;
        }
        throw new ErrorException(GameMessage.SHOW_GAME.format(getTopCard().toString(), drawPile.size()), false);
    }

    /**
     * Prints the sorted hand (deck) of a specific player.
     *
     * @param id The ID of the player.
     * @throws ErrorException If the player ID is invalid.
     */
    public void show(int id) throws ErrorException {
        Player player = getPlayerById(id);
        throw new ErrorException(GameMessage.SHOW.format(player.getDeckString()), false);
    }

    // PRIVATE HELPER METHODS

    /**
     * Resets the internal game state for a new game.
     * Clears all piles and players, resets the turn counter, and creates a fresh
     * deck of 32 cards.
     */
    private void resetGameState() {
        this.players.clear();
        this.discardPile.clear();
        this.drawPile.clear();
        this.currentPlayerIndex = 0;
        this.endRound = false;

        // Create the 32-card deck (Deutsches Blatt)
        for (Suit s : Suit.values()) {
            for (Rank r : Rank.values()) {
                this.drawPile.add(new Card(r, s));
            }
        }
    }

    /**
     * Helper method to find a player object by their ID.
     *
     * @param id The player ID.
     * @return The Player object.
     * @throws ErrorException If the ID is out of the valid range (1-4).
     */
    private Player getPlayerById(int id) throws ErrorException {
        if (id < 1 || id > numberOfPlayer) {
            throw new ErrorException(GameMessage.INVALID_PLAYER_COUNT.format(numberOfPlayer));
        }
        return players.get(id - 1);
    }

    /**
     * Retrieves the top card of the discard pile without removing it.
     *
     * @return The top Card, or null if the pile is empty.
     */
    private Card getTopCard() {
        if (discardPile.isEmpty()) {
            return null;
        }
        return discardPile.get(discardPile.size() - 1);
    }

    /**
     * Checks if a card can be played on the current top card of the discard pile.
     *
     * @param playerCard The card to check.
     * @return true if the move is valid according to Mau-Mau rules, false
     *         otherwise.
     */
    private boolean isValidMove(Card playerCard) {
        Card top = getTopCard();
        if (top == null) {
            return false;
        }
        return playerCard.canStackOn(top);
    }

    /**
     * Verifies if it is currently the specified player's turn.
     *
     * @param playerId The ID of the player to check.
     * @throws ErrorException If it is not the player's turn.
     */
    private void checkTurn(int playerId) throws ErrorException {
        if ((playerId - 1) != currentPlayerIndex) {
            throw new ErrorException(GameMessage.WRONG_TURN.format(playerId));
        }
    }

    /**
     * Advances the game to the next player's turn in a round-robin fashion.
     */
    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % numberOfPlayer;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public boolean endRound() {
        return this.endRound;
    }
}