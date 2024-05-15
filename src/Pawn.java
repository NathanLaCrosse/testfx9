// this class provides some unique functionality to the pawn piece
// this is required as the en passant move requires some extra functionality
public class Pawn extends ChessPiece {
    // TODO: Add a check later on to change this flag
    private boolean madeStartingMoveLastTurn = false;

    public Pawn(Board referenceBoard, boolean side) {
        super(referenceBoard, "Pawn", side, 1, DEFAULT_PAWN_MOVES);
    }
    
    @Override
    public BoardNode generateMoves(Board b) {
        BoardNode currentNode = super.generateMoves(b); // find default moves
        if(currentNode == null) return null; // in case this piece is off grid (captured)

        // special case: en passant
        // can capture pawn and move forward one if it used its starting move last turn
        BoardNode[] possibleTargets = new BoardNode[]{currentNode.west, currentNode.east};

        for(int i = 0; i < possibleTargets.length; i++) {
            if(possibleTargets[i] == null) continue; // skip over nodes outside of map

            ChessPiece possiblePiece = b.findPiece(possibleTargets[i].getName());
            if(possiblePiece != null && possiblePiece.getSide() != side && possiblePiece instanceof Pawn && ((Pawn)possiblePiece).madeStartingMoveLastTurn) {
                BoardNode dest = null; 
                if(side) {
                    dest = possibleTargets[i].north;
                }else {
                    dest = possibleTargets[i].south;
                }

                moves.add(new EnPassant(dest.getName(), currentSquare, this, possiblePiece, possibleTargets[i].getName()));
            }
        }

        return null;
    }
}
