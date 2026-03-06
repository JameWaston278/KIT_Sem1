package model;

import java.util.List;
import java.util.Random;

import exceptions.ErrorMessage;
import exceptions.GameLogicException;

/**
 * The Game class represents the overall state and logic of the game. It manages
 * the player and enemy teams, the game board, turn switching, and game over
 * conditions. It also initializes the game state based on provided parameters.
 * 
 * @author udqch
 */
public class Game {
    private static final Position PLAYER_KING_INITIAL_POSITION = new Position(3, 0);
    private static final Position ENEMY_KING_INITIAL_POSITION = new Position(3, 6);

    private final Team player;
    private final Team enemy;
    private final Board board;
    private final Random random;

    private Team currentTurn;
    private String selectedPosition;
    private boolean isGameOver;

    /**
     * Constructor for the Game class, which initializes the game state with the
     * provided parameters. It sets up the player and enemy teams, initializes the
     * game board, and places the Kings on the board.
     * 
     * @param seed       The seed for random number generation, used for shuffling
     *                   decks and other random events in the game.
     * @param playerName The name of the player's team. If null, a default name
     *                   "Player" will be used.
     * @param enemyName  The name of the enemy's team. If null, a default name
     *                   "Enemy" will be used.
     * @param playerDeck The list of Unit objects representing the player's deck.
     * @param enemyDeck  The list of Unit objects representing the enemy's deck.
     * @throws GameLogicException If there is an error during game initialization,
     *                            such as invalid deck configurations or issues
     *                            placing units on the board.
     */
    public Game(long seed, String playerName, String enemyName, List<Unit> playerDeck, List<Unit> enemyDeck)
            throws GameLogicException {
        this.random = new Random(seed);
        this.board = new Board();

        String playerTeamName = (playerName != null) ? playerName : "Player";
        String enemyTeamName = (enemyName != null) ? enemyName : "Enemy";
        this.player = new Team(playerTeamName, playerDeck);
        this.enemy = new Team(enemyTeamName, enemyDeck);

        this.selectedPosition = null;
        this.isGameOver = false;

        setupInitialGameState();
    }

    private void setupInitialGameState() throws GameLogicException {
        // Both teams draw their initial hand
        this.player.drawInitialHand();
        this.enemy.drawInitialHand();

        // Place the Kings on the board
        King playerKing = new King(player);
        King enemyKing = new King(enemy);
        board.placeUnitAt(playerKing, PLAYER_KING_INITIAL_POSITION);
        board.placeUnitAt(enemyKing, ENEMY_KING_INITIAL_POSITION);

        this.currentTurn = player; // Player starts first
    }

    // --- GAME LOGIC METHODS ---

    /**
     * Switches the turn to the other team.
     * 
     * @throws GameLogicException If there is an error during turn switching.
     */
    public void switchTurn() throws GameLogicException {
        this.selectedPosition = null; // Clear selected position at the end of each turn
        this.currentTurn = (this.currentTurn == player) ? enemy : player;
        this.currentTurn.drawCard(); // Draw a card at the start of the turn
        this.currentTurn.resetAllMovedStatus(); // Reset moved status of all units at the start of the turn
        checkGameOver(); // Check if the game has ended after switching turns
    }

    /**
     * Checks if the game is over based on the defeat of either team.
     * 
     * @throws GameLogicException If there is an error during the game over check.
     */
    public void checkGameOver() throws GameLogicException {
        if (player.isDefeated() || player.getLp() <= 0) {
            this.isGameOver = true;
            throw new GameLogicException(ErrorMessage.GAME_OVER.format(enemy.getName()));
        } else if (enemy.isDefeated() || enemy.getLp() <= 0) {
            this.isGameOver = true;
            throw new GameLogicException(ErrorMessage.GAME_OVER.format(player.getName()));
        }
    }

    // --- GETTERS & SETTERS ---

    /**
     * Returns the game board.
     * 
     * @return The game board.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Returns the team of the current player.
     * 
     * @return The current player's team.
     */
    public Team getPlayer() {
        return this.player;
    }

    /**
     * Returns the team of the enemy player.
     * 
     * @return The enemy player's team.
     */
    public Team getEnemy() {
        return this.enemy;
    }
}
