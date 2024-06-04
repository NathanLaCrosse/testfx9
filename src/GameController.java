// class to control a game of chess

import java.util.LinkedList;

public class GameController {
    private Board gameBoard;

    private boolean turn;

    private Entity player1;
    private Entity player2;

    private int endCondition; // differentiates a win from a draw
    
    public GameController(Entity player1, Entity player2) {
        gameBoard = new Board(8, 8);
        turn = true;

        this.player1 = player1;
        this.player2 = player2;
    }

    public Board getGameBoard() {
        return gameBoard;
    }
    public int getEndCondition() {
        return endCondition;
    }

    // TODO: Make sure board state is unchanged by either entity
    // returns true if the current entity is in checkmate
    public boolean nextTurn() {
        String oldBoardState = gameBoard.generateBoardString();

        LinkedList<Move> validMoves = gameBoard.generateSanitizedMovesForSide(turn);

        // check for possible ends to the game
        int endCondition = gameBoard.getEndCondition(turn);
        if(endCondition == 2) {
            System.out.println((turn ? "White" : "Black") + " is checkmated!");
            return true;
        }else if(endCondition != -1) {
            System.out.println("Draw.");
            return true;
        }

        // continue the game
        Move moveToPlay = currentPlayer().selectMoveToPlay(validMoves, gameBoard);

        // check if the board was somehow modified by a bot
        if(!oldBoardState.equals(gameBoard.generateBoardString())) {
            System.out.println("Illegal modification present!");
            return true;
        }

        moveToPlay.move();
        currentPlayer().reset();

        turn = !turn;
        return false;
    }

    public Entity currentPlayer() {
        if(turn) {
            return player1;
        }else {
            return player2;
        }
    }

    public String toString() {
        String str = gameBoard.getPiecesOnSide(true).toString();
        str += gameBoard.getPiecesOnSide(false);
        return str;
    }

}
