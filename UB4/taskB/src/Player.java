import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a player in the game.
 * Manages the player's ID and their hand of cards.
 *
 * @author udqch
 * @version 1.0
 */
public class Player {

    private final int id;
    private final List<Card> deck;

    /**
     * Constructs a new player.
     * 
     * @param id   The unique ID of the player.
     * @param deck The initial hand of cards.
     */
    public Player(int id, List<Card> deck) {
        this.id = id;
        this.deck = deck;
    }

    /**
     * Adds a card to the player's hand.
     * 
     * @param card The card to add.
     */
    public void addCard(Card card) {
        this.deck.add(card);
    }

    /**
     * Removes a card from the player's hand.
     * 
     * @param card The card to remove.
     */
    public void removeCard(Card card) {
        this.deck.remove(card);
    }

    /**
     * Finds a card in the player's hand by its string name (e.g., "7H").
     * 
     * @param cardName The name of the card.
     * @return The Card object if found, null otherwise.
     */
    public Card findCard(String cardName) {
        for (Card c : deck) {
            if (c.toString().equals(cardName)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Checks if the player has a specific card.
     * 
     * @param card The card to check.
     * @return true if present.
     */
    public boolean hasCard(Card card) {
        return deck.contains(card);
    }

    /**
     * Returns a comma-separated, sorted string representation of the hand.
     * 
     * @return Formatted deck string.
     */
    public String getDeckString() {
        return deck.stream()
                .sorted()
                .map(Card::toString)
                .collect(Collectors.joining(","));
    }

    /**
     * Gets the the id of the player.
     * 
     * @return the id of the player.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets player's deck.
     * 
     * @return list of all cards of the player.
     */
    public List<Card> getDeck() {
        return this.deck;
    }
}