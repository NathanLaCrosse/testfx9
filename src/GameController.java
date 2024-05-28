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

    // returns true if the current entity is in checkmate
    public boolean nextTurn() {
        LinkedList<Move> validMoves = gameBoard.generateSanitizedMovesForSide(turn);

        // check for possible ends to the game
        if(validMoves.size() == 0) { 
            if(gameBoard.inCheck(turn)) { // we are in checkmate
                endCondition = 0;
                System.out.println("Checkmate!");
            }else { // otherwise it is a stalemate
                endCondition = 1; 
                System.out.println("Stalemate!");
            }

            return true;
        }else if(gameBoard.insufficientMaterial()) {
            endCondition = 2;
            System.out.println("Insufficient Material!");

            return true;
        }else if(gameBoard.fiftyMoveRuleValid()) {
            endCondition = 3;
            System.out.println("Fifty Move Rule Violated!");
            
            return true;
        }

        // continue the game
        Move moveToPlay = currentPlayer().selectMoveToPlay(validMoves, gameBoard.getPiecesOnSide(turn));
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
