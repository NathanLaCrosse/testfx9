// class to control a game of chess

import java.util.LinkedList;

public class GameController {
    private Board gameBoard;

    private boolean turn;

    private Entity player1;
    private Entity player2;
    
    public GameController(Entity player1, Entity player2) {
        gameBoard = new Board(8, 8);
        turn = true;

        this.player1 = player1;
        this.player2 = player2;
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    // returns true if the current entity is in checkmate
    public boolean nextTurn() {
        LinkedList<Move> validMoves = gameBoard.generateSanitizedMovesForSide(turn);
        if(validMoves.size() == 0) return true;

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
