// designed to be a simple bot yet stronger than randBot

import java.util.LinkedList;

public class StageOneBot extends Entity {
    private Board gameBoard;
    private Move moveToPlay;

    public StageOneBot(boolean side) {
        super(side);
    }

    @Override
    public Move selectMoveToPlay(LinkedList<Move> validMoves, Board gameBoard) {
        this.gameBoard = gameBoard;

        search(gameBoard, side, 3);

        return moveToPlay;
    }

    @Override
    public void reset() {

    }

    // code slightly different as the gameBoard has already generated valid moves
    // private Move initialSearch(LinkedList<Move> validMoves, int depth) {
    //     if(depth < 1) throw new UnsupportedOperationException("depth too low!");

    //     Move bestMove = null;
    //     int bestMoveVal = Integer.MIN_VALUE;
    //     int eval;

    //     for(Move m : validMoves) {
    //         m.move();
    //         eval = -search(gameBoard, !side, depth-1);
    //         m.undoMove();

    //         if(eval > bestMoveVal) {
    //             bestMoveVal = eval;
    //             bestMove = m;
    //             alpha = Math.max(alpha, eval);
    //             if(alpha >= beta) {break;}
    //         }
    //     }

    //     return bestMove;
    // }

    private int search(Board gameBoard, boolean sideToPlay, int depth) {return search(gameBoard, sideToPlay, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);}
    private int search(Board gameBoard, boolean sideToPlay, int depth, int alpha, int beta, int distFromRoot)  {
        if(depth <= 0) return gradeBoardState(sideToPlay);

        // set up the board and obtain valid moves
        LinkedList<Move> validMoves = gameBoard.generateSanitizedMovesForSide(sideToPlay);

        // check for end states
        int endCon = gameBoard.getEndCondition(sideToPlay);
        if(endCon == 2) return Integer.MIN_VALUE; // checkmate
        if(endCon != -1) return -10; // some sort of draw

        int bestMoveVal = Integer.MIN_VALUE;
        int eval;

        for(Move m : validMoves) {
            m.move();
            eval = -search(gameBoard, !sideToPlay, depth-1, -beta, -alpha, distFromRoot + 1);
            m.undoMove();

            if(eval > bestMoveVal) {
                bestMoveVal = eval;
                if(distFromRoot == 0) moveToPlay = m;
                alpha = Math.max(alpha, eval);
                if(alpha >= beta) {break;}
            }
        }

        return bestMoveVal;
    }
    
    private int gradeBoardState(boolean side) {
        return gameBoard.materialOnSide(side) - gameBoard.materialOnSide(!side);
    }

    // for move ordering
    // priority for unmoved pieces
    // moves help protect
    // remove moves into unprotected spaces
}
