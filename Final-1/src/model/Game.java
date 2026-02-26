package model;

import java.util.List;
import java.util.Random;

import exceptions.ErrorMessage;
import exceptions.GameLogicException;

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

    public void switchTurn() throws GameLogicException {
        this.selectedPosition = null; // Clear selected position at the end of each turn
        this.currentTurn = (this.currentTurn == player) ? enemy : player;
        this.currentTurn.drawCard(); // Draw a card at the start of the turn
        this.currentTurn.resetAllMovedStatus(); // Reset moved status of all units at the start of the turn
        checkGameOver(); // Check if the game has ended after switching turns
    }

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

    public Board getBoard() {
        return this.board;
    }
}
