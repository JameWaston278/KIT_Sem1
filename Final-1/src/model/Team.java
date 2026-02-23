package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exceptions.ErrorMessage;
import exceptions.GameLogicException;

/**
 * The Team class represents a player in the game, holding their name, life
 * points (LP), deck of units, hand of units, active units on the board, and
 * other relevant information. It provides methods for drawing cards, taking
 * damage, and managing active units.
 * 
 * @author udqch
 */
public class Team {
    // --- TEAM CONSTANTS ---

    /**
     * Initial life points (LP) for each team at the start of the game.
     */
    public static final int INITIAL_LIFE_POINT = 8000;
    /**
     * Initial number of cards drawn into the hand at the start of the game.
     */
    public static final int INITIAL_CARDS_IN_HAND = 4;

    private final String name;
    private int lp;
    private final List<Unit> deck;
    private final List<Unit> hand;
    private final List<Unit> activeUnits;
    private int boardCount;
    private boolean isDefeated;

    /**
     * Constructor for the Team class, which initializes a team with a given name
     * and deck of units. The team starts with a predefined amount of life points
     * and an empty hand and active unit list.
     * 
     * @param name The name of the team/player.
     * @param deck The initial deck of units for the team.
     */
    public Team(String name, List<Unit> deck) {
        this.name = name;
        this.lp = INITIAL_LIFE_POINT;
        this.deck = new ArrayList<>(deck);
        this.hand = new ArrayList<>();
        this.activeUnits = new ArrayList<>();
        this.boardCount = 0;
        this.isDefeated = false;
    }

    // --- LOGIC METHODS ---

    /**
     * Draws the initial hand of cards for the team at the start of the game.
     * The number of cards drawn is determined by a constant defined in
     * Constants.INIT_CARDS_IN_HAND.
     */
    public void drawInitialHand() {
        for (int i = 0; i < INITIAL_CARDS_IN_HAND; i++) {
            this.drawCard();
        }
    }

    /**
     * Draws a card from the team's deck and adds it to their hand. If the deck is
     * empty, the team is marked as defeated.
     */
    public void drawCard() {
        if (this.deck.isEmpty()) {
            // No more cards to draw, player loses
            this.isDefeated = true;
            return;
        }
        Unit drawnCard = this.deck.remove(0);
        this.hand.add(drawnCard);
    }

    /**
     * Discards a specified card from the team's hand. If the card is not in the
     * hand, a GameLogicException is thrown.
     * 
     * @param discardedCard The card to be discarded from the hand.
     * @throws GameLogicException if the specified card is not found in the hand.
     */
    public void discardCard(Unit discardedCard) throws GameLogicException {
        if (!this.hand.contains(discardedCard)) {
            throw new GameLogicException(ErrorMessage.CARD_NOT_IN_HAND.format(discardedCard.getName()));
        }
        this.hand.remove(discardedCard);
    }

    /**
     * Reduces the team's life points (LP) by a specified amount of damage. If the
     * LP drops to zero or below, the team is marked as defeated.
     * 
     * @param damage The amount of damage to be taken by the team.
     */
    public void takeDamage(int damage) {
        this.lp -= damage;
        if (this.lp <= 0) {
            this.lp = 0;
            this.isDefeated = true;
        }
    }

    // --- LOGIC METHODS FOR ON-BOARD UNITS ---

    /**
     * Adds a unit to the team's list of active units on the board. This method
     * checks if the unit is already active to prevent duplicates.
     * 
     * @param unit The unit to be added to the active units list.
     */
    public void addActiveUnit(Unit unit) {
        if (!activeUnits.contains(unit)) {
            this.activeUnits.add(unit);
        }
    }

    /**
     * Removes a unit from the team's list of active units on the board. This
     * method checks if the unit is currently active before attempting to remove it.
     * 
     * @param unit The unit to be removed from the active units list.
     */
    public void removeActiveUnit(Unit unit) {
        this.activeUnits.remove(unit);
    }

    /**
     * Resets the "hasMoved" status of all active units on the board at the end of
     * a turn, allowing them to move again in the next turn.
     */
    public void resetAllMovedStatus() {
        for (Unit units : activeUnits) {
            units.setHasMoved(false);
        }
    }

    // --- GETTERS & SETTERS ---

    /**
     * Gets the name of the team/player.
     * 
     * @return The name of the team.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the current life points (LP) of the team.
     * 
     * @return The current LP of the team.
     */
    public int getLp() {
        return lp;
    }

    /**
     * Gets the size of the team's deck (number of cards remaining).
     * 
     * @return The number of cards left in the team's deck.
     */
    public int getDeckSize() {
        return deck.size();
    }

    /**
     * Gets an unmodifiable view of the team's hand of cards.
     * 
     * @return An unmodifiable list of units currently in the team's hand.
     */
    public List<Unit> getHand() {
        return Collections.unmodifiableList(hand);
    }

    /**
     * Gets an unmodifiable view of the team's active units on the board.
     * 
     * @return An unmodifiable list of units currently active on the board for the
     *         team.
     */
    public int getBoardCount() {
        return boardCount;
    }

    /**
     * Increases the count of active units on the board for the team by one.
     */
    public void increaseBoardCount() {
        this.boardCount++;
    }

    /**
     * Decreases the count of active units on the board for the team by one,
     * ensuring that the count does not go below zero.
     */
    public void decreaseBoardCount() {
        if (this.boardCount > 0) {
            this.boardCount--;
        }
    }

    /**
     * Checks if the team has been defeated, which occurs when their LP drops to
     * zero or below, or if they have no more cards to draw.
     * 
     * @return True if the team is defeated, false otherwise.
     */
    public boolean isDefeated() {
        return isDefeated;
    }
}
