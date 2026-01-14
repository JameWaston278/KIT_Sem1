import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents the core logic and state of a Mau-Mau card game.
 * Manages the deck, players, turn cycles, and enforces game rules.
 *
 * @author udqch
 * @version 1.0
 */
public class MauMauGame {

    private final int numberOfPlayers;
    private int currentPlayerIndex;
    private final List<Player> players;
    private final List<Card> drawPile;
    private final List<Card> discardPile;
    private boolean isRunning;
    private boolean endMatch;

    /**
     * Constructs a new MauMauGame instance with default configuration.
     * Initializes empty lists for players and card piles.
     */
    public MauMauGame() {
        this.numberOfPlayers = 4;
        this.currentPlayerIndex = 0;
        this.players = new ArrayList<>();
        this.drawPile = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        this.isRunning = true;
        this.endMatch = false;
    }

    // =========================================================================
    // GAME LIFECYCLE METHODS
    // =========================================================================

    /**
     * Starts or restarts the game with a specific random seed.
     * Resets state, shuffles the deck, deals cards, and prepares the discard pile.
     *
     * @param seed The seed for deterministic shuffling.
     * @throws GameException If the game has been permanently terminated (quit).
     */
    public void start(int seed) throws GameException {
        if (!this.isRunning) {
            throw new GameException(GameMessage.GAME_ENDED.format());
        }

        resetGameState();

        // 1. Shuffle deck
        Collections.shuffle(this.drawPile, new Random(seed));

        // 2. Deal 5 cards to each player
        for (int id = 0; id < numberOfPlayers; id++) {
            List<Card> initialHand = new ArrayList<>();
            for (int k = 0; k < 5; k++) {
                initialHand.add(drawPile.get(0));
                drawPile.remove(0);
            }
            this.players.add(new Player(id + 1, initialHand));
        }

        // 3. Setup discard pile
        discardPile.add(drawPile.get(0));
        drawPile.remove(0);

        System.out.println("Player " + (this.currentPlayerIndex + 1) + " takes the turn.");
    }

    /**
     * Terminates the game execution loop permanently.
     * After calling this, the game cannot be restarted.
     */
    public void quit() {
        this.isRunning = false;
    }

    // =========================================================================
    // PLAYER ACTIONS
    // =========================================================================

    /**
     * Executes a discard move for a specific player.
     * Checks validation, moves the card, and checks for a win condition.
     *
     * @param playerId The ID of the player (1-4).
     * @param cardName The string representation of the card to discard (e.g.,
     *                 "7H").
     * @throws GameException If the move is invalid, the card is missing, or the
     *                       player wins.
     */
    public void discard(int playerId, String cardName) throws GameException {
        checkTurn(playerId);

        Player player = getPlayerById(playerId);
        Card cardToPlay = player.findCard(cardName);

        if (cardToPlay == null) {
            throw new GameException(GameMessage.CARD_NOT_FOUND.format(playerId, cardName));
        }

        if (!isValidMove(cardToPlay)) {
            throw new GameException(GameMessage.INVALID_MOVE.format(cardName, getTopCard().toString()));
        }

        player.removeCard(cardToPlay);
        this.discardPile.add(cardToPlay);

        // Check Win Condition
        if (player.getDeck().isEmpty()) {
            this.endMatch = true;
            throw new GameException(GameMessage.GAME_WON.format(this.currentPlayerIndex + 1), false);
        }

        nextTurn();
    }

    /**
     * Executes a pick (draw) move for a specific player.
     * Draws a card from the pile and checks for a draw condition.
     *
     * @param playerId The ID of the player (1-4).
     * @throws GameException If it's not the player's turn or the draw pile is
     *                       empty (Draw).
     */
    public void pick(int playerId) throws GameException {
        checkTurn(playerId);

        Player player = getPlayerById(playerId);

        Card drawCard = drawPile.get(0);
        player.addCard(drawCard);
        drawPile.remove(0);

        // Check Draw Condition (Empty Pile)
        if (drawPile.isEmpty()) {
            this.endMatch = true;
            throw new GameException(GameMessage.GAME_DRAW.format(), false);
        }

        nextTurn();
    }

    // =========================================================================
    // VIEW / INFO METHODS
    // =========================================================================

    /**
     * Generates the current game status (Top card and Draw pile size).
     *
     * @throws GameException Containing the formatted game status string.
     */
    public void showGame() throws GameException {
        if (discardPile.isEmpty()) {
            return;
        }
        throw new GameException(GameMessage.SHOW_GAME.format(getTopCard().toString(), drawPile.size()), false);
    }

    /**
     * Generates the string representation of a player's hand.
     *
     * @param id The ID of the player (1-4).
     * @throws GameException Containing the formatted player hand string.
     */
    public void show(int id) throws GameException {
        Player player = getPlayerById(id);
        throw new GameException(GameMessage.SHOW_PLAYER_DECK.format(player.getDeckString()), false);
    }

    // =========================================================================
    // GETTERS & HELPERS
    // =========================================================================

    /**
     * Checks if the game loop is currently active.
     * 
     * @return true if the game is running, false otherwise.
     */
    public boolean isRunning() {
        return this.isRunning;
    }

    /**
     * Checks if the current round has ended (Win or Draw).
     * 
     * @return true if the round is over.
     */
    public boolean endMatch() {
        return this.endMatch;
    }

    /**
     * Resets the internal game state (players, piles, turn) for a new match.
     */
    private void resetGameState() {
        this.players.clear();
        this.discardPile.clear();
        this.drawPile.clear();
        this.currentPlayerIndex = 0;
        this.endMatch = false;

        for (Suit s : Suit.values()) {
            for (Rank r : Rank.values()) {
                this.drawPile.add(new Card(r, s));
            }
        }
    }

    /**
     * Retrieves a player object by their 1-based ID.
     *
     * @param id The player ID (1-4).
     * @return The Player object.
     * @throws GameException If the ID is invalid.
     */
    private Player getPlayerById(int id) throws GameException {
        if (id < 1 || id > numberOfPlayers) {
            throw new GameException(GameMessage.INVALID_PLAYER_COUNT.format(numberOfPlayers));
        }
        return players.get(id - 1);
    }

    /**
     * Gets the top card of the discard pile without removing it.
     * 
     * @return The top Card, or null if empty.
     */
    private Card getTopCard() {
        if (discardPile.isEmpty())
            return null;
        return discardPile.get(discardPile.size() - 1);
    }

    /**
     * Validates if a card can be played on the current discard pile.
     * 
     * @param playerCard The card to validate.
     * @return true if the move is legal according to Mau-Mau rules.
     */
    private boolean isValidMove(Card playerCard) {
        Card top = getTopCard();
        return top != null && playerCard.canStackOn(top);
    }

    /**
     * Verifies that the given player ID matches the current turn.
     * 
     * @param playerId The player ID to check.
     * @throws GameException If it is not the player's turn.
     */
    private void checkTurn(int playerId) throws GameException {
        if ((playerId - 1) != currentPlayerIndex) {
            throw new GameException(GameMessage.WRONG_TURN.format(playerId));
        }
    }

    /**
     * Advances the turn to the next player.
     */
    private void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % numberOfPlayers;
    }
}