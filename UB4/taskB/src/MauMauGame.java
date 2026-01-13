
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MauMauGame {
    private final int numberOfPlayer;
    private boolean isRunning;
    private int currentPlayerIndex;
    private List<Card> deck;

    private List<Player> players = new ArrayList<>();
    private List<Card> discardDeck = new ArrayList<>();

    public MauMauGame() {
        this.numberOfPlayer = 4;
        this.isRunning = false;
        this.currentPlayerIndex = 0;

        this.deck = new ArrayList<>();
        for (Suit s : Suit.values()) {
            for (Rank r : Rank.values()) {
                this.deck.add(new Card(r, s));
            }
        }
    }

    public void start(int seed) {
        Collections.shuffle(this.deck, new Random(seed));

        for (int id = 0; id < numberOfPlayer; id++) {
            List<Card> playerDeck = deck.subList(0, 5);
            System.out.println(playerDeck);
            this.players.add(new Player(id, playerDeck));
            deck.removeAll(playerDeck);
        }

        discardDeck.add(deck.get(0));
        deck.remove(0);
    }

    public void showGame() {
        System.out.println(discardDeck.get(0).toString() + " / " + deck.size());
    }

}
