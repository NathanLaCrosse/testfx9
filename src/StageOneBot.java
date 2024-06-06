// designed to be a simple bot yet stronger than randBot

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

public class StageOneBot extends Entity {
    private Move moveToPlay;

    public StageOneBot(boolean side) {
        super(side);
    }

    @Override
    public Move selectMoveToPlay(LinkedList<Move> validMoves, Board gameBoard) {
        search(gameBoard, side, 6); // keep on even for now

        return moveToPlay;
    }

    @Override
    public void reset() {

    }

    private double search(Board gameBoard, boolean sideToPlay, int depth) {return search(gameBoard, sideToPlay, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);}
    private double search(Board gameBoard, boolean sideToPlay, int depth, double alpha, double beta, int distFromRoot)  {
        if(depth <= 0) return gradeBoardState(gameBoard, sideToPlay);

        // set up the board and obtain valid moves
        Collection<Move> validMoves = createOrderedMoves(gameBoard, sideToPlay);

        //if(gameBoard.inCheck(!sideToPlay)) {return 1000;}

        // check for end states
        int endCon = gameBoard.getEndCondition(sideToPlay);
        if(endCon == 2) return Integer.MIN_VALUE; // checkmate
        if(endCon != -1) return -100; // some sort of draw

        double bestMoveVal = Integer.MIN_VALUE;
        double eval;

        for(Move m : validMoves) {
            m.move();
            eval = -search(gameBoard, !sideToPlay, depth-1, -beta, -alpha, distFromRoot + 1);
            if(distFromRoot == 0) {
                eval += 2 * gradeMove(gameBoard, m, sideToPlay);
            }
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
    
    // gives a score on how good a certain board state is for the given side
    private double gradeBoardState(Board gameBoard, boolean side) {
        double score = 0;

        int materialAdvantage = gameBoard.materialOnSide(side) - gameBoard.materialOnSide(!side);
        score += 100 * materialAdvantage;

        if(gameBoard.inCheck(!side) ) {
            score += 250;
        }

        // give extra points for good-looking moves
        // Collection<Move> availableMoves = createOrderedMoves(gameBoard, side);
        // for(Move m : availableMoves) {
        //     if(m.getCapturedPiece() != null) score += 0.05;
        // }

        // bonus for least amount of king moves - to encourage checkmate
        int opponentKingMovesAttacked = 0;
        for(Move m : gameBoard.getKingOnSide(!side).getMoves().values()) {
            if(gameBoard.squareIsAttacked(m.destination, side)) opponentKingMovesAttacked++;
        }
        score += 40 * opponentKingMovesAttacked;

        return score;
    }

    // for move ordering
    // priority for unmoved pieces
    // moves help protect
    // remove moves into unprotected spaces
    private Collection<Move> createOrderedMoves(Board gameBoard, boolean side) {
        LinkedList<Move> validMoves = gameBoard.generateSanitizedMovesForSide(side);
        TreeMap<Double, Move> orderedMoves = new TreeMap<>();
        
        for(Move m : validMoves) {
            orderedMoves.put(gradeMove(gameBoard, m, side), m);
        }

        return orderedMoves.values();
    }

    private double gradeMove(Board gameBoard, Move m, boolean side) {
        double score = 0;

        ChessPiece moving = m.getMovingPiece();
        ChessPiece capture = m.getCapturedPiece();
        if(capture != null) {
            // create urgency to move out of a square, so we are less likely to leave pieces hanging
            if(gameBoard.squareIsAttacked(m.originalPosition, !side)) {
                score += (capture.material - m.getMovingPiece().material) * 6;
            }else {
                // this will sometimes make moves like a queen capturing a free pawn unappealing
                score += (capture.material - m.getMovingPiece().material) * 6;
            }
        }

        score += gameBoard.squareIsAttacked(m.originalPosition, !side) ? moving.material : 0;
        score -= gameBoard.squareIsAttacked(m.destination, !side) ? moving.material : 0;

        //encourage pawn moves early game as well as getting pieces
        if(m.getMovingPiece() instanceof Pawn && 5 - gameBoard.getMovesMade() >= 1) {
            score += 10;
        }

        // also encourage moving a piece if it hasn't moved already
        if(!m.getMovingPiece().hasMoved()) {
            score += 2;
        }

        return score;
    }
}
