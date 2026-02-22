package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exceptions.ErrorMessage;
import exceptions.GameLogicException;
import utils.Constants;

public class Team {
    private String name;
    private int lp;
    private List<Unit> deck;
    private List<Unit> hand;
    private List<Unit> activeUnits;
    private int boardCount;
    private boolean isDeafeted;

    public Team(String name, List<Unit> deck) {
        this.name = name;
        this.lp = Constants.INIT_LP;
        this.deck = new ArrayList<>(deck);
        this.hand = new ArrayList<>();
        this.activeUnits = new ArrayList<>();
        this.boardCount = 0;
        this.isDeafeted = false;
    }

    // --- LOGIC METHODS ---

    public void drawInitialHand() {
        for (int i = 0; i < Constants.INIT_CARDS_IN_HAND; i++) {
            this.drawCard();
        }
    }

    public void drawCard() {
        if (this.deck.isEmpty()) {
            // No more cards to draw, player loses
            this.isDeafeted = true;
            return;
        }
        Unit drawnCard = this.deck.remove(0);
        this.hand.add(drawnCard);
    }

    public void discardCard(Unit discardedCard) throws GameLogicException {
        if (!this.hand.contains(discardedCard)) {
            throw new GameLogicException(ErrorMessage.CARD_NOT_IN_HAND.format(discardedCard.getName()));
        }
        this.hand.remove(discardedCard);
    }

    public void takeDamage(int damage) {
        this.lp -= damage;
        if (this.lp <= 0) {
            this.lp = 0;
            this.isDeafeted = true;
        }
    }

    // --- LOGIC METHODS FOR ON-BOARD UNITS ---

    public void addActiveUnit(Unit unit) {
        if (!activeUnits.contains(unit)) {
            this.activeUnits.add(unit);
        }
    }

    public void removeActiveUnit(Unit unit) {
        this.activeUnits.remove(unit);
    }

    public void resetAllMovedStatus() {
        for (Unit units : activeUnits) {
            units.setHasMoved(false);
        }
    }

    // --- GETTERS & SETTERS ---

    public String getName() {
        return name;
    }

    public int getLp() {
        return lp;
    }

    public int getDeckSize() {
        return deck.size();
    }

    public List<Unit> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public int getBoardCount() {
        return boardCount;
    }

    public void increaseBoardCount() {
        this.boardCount++;
    }

    public void decreaseBoardCount() {
        if (this.boardCount > 0) {
            this.boardCount--;
        }
    }

    public boolean isDefeated() {
        return isDeafeted;
    }
}
