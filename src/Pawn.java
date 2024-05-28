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

        if(currentPos == null || !name.contains("Pawn")) return; // return if we are out of bounds or a different piece (due to promotion)

        // code for pawn promotion is found in the ChessPiece move generation code

        // special case: en passant
        // can capture pawn and move forward one if it used its starting move last turn
        Pos[] possibleTargets = new Pos[]{currentPos.returnModified(0, -1), currentPos.returnModified(0, 1)};

        for(int i = 0; i < possibleTargets.length; i++) {
            if(!referenceBoard.inBounds(possibleTargets[i])) continue; // skip over nodes outside of map

            ChessPiece possiblePiece = referenceBoard.retrievePiece(possibleTargets[i]);
            if(possiblePiece != null && possiblePiece.getSide() != side && possiblePiece instanceof Pawn && ((Pawn)possiblePiece).madeStartingMoveLastTurn) {
                Pos dest = null; 
                if(side) {
                    dest = possibleTargets[i].returnModified(-1, 0);
                }else {
                    dest = possibleTargets[i].returnModified(1, 0);
                }

                moves.put(dest, new EnPassant(referenceBoard, dest, currentPos, referenceBoard.getIdentifierAtPos(currentPos), referenceBoard.getIdentifierAtPos(possibleTargets[i]), possibleTargets[i]));
            }
        }
    }

    // this method enables the en passant flag if this piece has just moved 2 spaces
    public void checkIfUsedStartingMove(Move m) {
        if(Math.abs(m.destination.first() - m.originalPosition.first()) == 2) {
            madeStartingMoveLastTurn = true;
        }else {
            madeStartingMoveLastTurn = false;
        }
    }
    public void disableEnPasantFlag() {
        madeStartingMoveLastTurn = false;
    }
}
