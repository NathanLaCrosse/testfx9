// this class provides some unique functionality to the pawn piece
// this is required as the en passant move requires some extra functionality
public class Pawn extends ChessPiece {
    private boolean madeStartingMoveLastTurn = false;

    public Pawn(Board referenceBoard, Pos startingPos, boolean side) {
        super(referenceBoard, startingPos, "Pawn", side, 1, DEFAULT_PAWN_MOVES);
    }
    
    @Override
    public void generateMoves() {
        super.generateMoves(); // find default moves

        // special case: en passant
        // can capture pawn and move forward one if it used its starting move last turn
        Pos[] possibleTargets = new Pos[]{currentPos.returnModified(-1, 0), currentPos.returnModified(1, 0)};

        for(int i = 0; i < possibleTargets.length; i++) {
            if(!referenceBoard.inBounds(possibleTargets[i])) continue; // skip over nodes outside of map

            ChessPiece possiblePiece = referenceBoard.retrievePiece(possibleTargets[i]);
            if(possiblePiece != null && possiblePiece.getSide() != side && possiblePiece instanceof Pawn && ((Pawn)possiblePiece).madeStartingMoveLastTurn) {
                Pos dest = null; 
                if(side) {
                    dest = currentPos.returnModified(0, -1);
                }else {
                    dest = currentPos.returnModified(0, 1);
                }

                moves.put(dest, new EnPassant(referenceBoard, dest, currentPos, referenceBoard.getIdentifierAtPos(currentPos), referenceBoard.getIdentifierAtPos(possibleTargets[i]), possibleTargets[i]));
            }
        }
    }

    // this method enables the en passant flag if this piece has just moved
    public void checkIfUsedStartingMove() {
        if((int)Math.abs(currentPos.first() - startingPos.first()) == 2) {
            madeStartingMoveLastTurn = true;
        }else {
            madeStartingMoveLastTurn = false;
        }
    }
    public void disableEnPasantFlag() {
        madeStartingMoveLastTurn = false;
    }
}
