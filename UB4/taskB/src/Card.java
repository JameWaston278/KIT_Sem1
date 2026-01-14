
/**
 * Represents a single playing card with a Rank and a Suit.
 * Immutable class.
 *
 * @author udqch
 * @version 1.0
 */
public final class Card implements Comparable<Card> {

    private final Suit suit;
    private final Rank rank;

    /**
     * Creates a new card.
     * 
     * @param rank The rank of the card (e.g., SEVEN, KING).
     * @param suit The suit of the card (e.g., HEARTS).
     */
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Checks if this card can be legally played on top of another card.
     * Rule: Same Rank OR Same Suit.
     *
     * @param preCard The card currently on top of the discard pile.
     * @return true if the move is valid.
     */
    public boolean canStackOn(Card preCard) {
        return this.rank == preCard.rank || this.suit == preCard.suit;
    }

    /**
     * Gets the suit of the card.
     * 
     * @return the suit of the card.
     */
    public Suit getSuit() {
        return this.suit;
    }

    /**
     * Gets the rank of the card.
     * 
     * @return the rank of the card.
     */
    public Rank getRank() {
        return this.rank;
    }

    /**
     * Returns the string representation (e.g., "7H").
     * 
     * @return rank representation + suit reprsentation.
     */
    @Override
    public String toString() {
        return rank.toString() + suit.toString();
    }

    /**
     * Compares two cards based on their string representation.
     * 
     * @return compare values.
     */
    @Override
    public int compareTo(Card other) {
        return this.toString().compareTo(other.toString());
    }
}