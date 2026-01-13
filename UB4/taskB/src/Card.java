import java.util.Objects;

public final class Card implements Comparable<Card> {
    private final Suit suit;
    private final Rank rank;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Suit getSuit() {
        return this.suit;
    }

    public Rank getRank() {
        return this.rank;
    }

    @Override
    public String toString() {
        // Rank="7", Suit="H" -> "7H"
        return rank.toString() + suit.toString();
    }

    @Override
    public int compareTo(Card other) {
        String thisCard = this.toString();
        String otherCard = other.toString();

        return thisCard.compareTo(otherCard);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Card card = (Card) obj;
        return rank == card.rank && suit == card.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }

    public boolean canDiscardOn(Card preCard) {
        return this.rank == preCard.rank || this.suit == preCard.suit;
    }
}
