
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    private final int id;
    private final List<Card> deck;

    public Player(int id, List<Card> deck) {
        this.id = id;
        this.deck = deck;
    }

    public int getId() {
        return this.id;
    }

    public List<Card> getDeck() {
        return this.deck;
    }

    public String getDeckString() {
        return deck.stream().sorted().map(Card::toString).collect(Collectors.joining(","));
    }

    public void addCard(Card card) {
        this.deck.add(card);
    }

    public void removeCard(Card card) {
        this.deck.remove(card);
    }

    public boolean isEmptyDeck() {
        return deck.isEmpty();
    }

    public boolean hasCard(Card card) {
        return deck.contains(card);
    }

    public Card findCard(String cardName) {
        for (Card c : deck) {
            if (c.toString().equals(cardName)) {
                return c;
            }
        }
        return null;
    }
}
