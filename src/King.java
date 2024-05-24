// class to provide special implementation of the king's castling move
public class King extends ChessPiece {

    // the king requires the board as it needs to search for its rooks
    public King(Board referenceBoard, Pos startingPos, boolean side) {
        super(referenceBoard, startingPos,"King", side, 0, ChessPiece.DEFAULT_KING_MOVES);
    }
    
    @Override
    public void generateMoves() {
        super.generateMoves(); // generate default moves

        // special case: castling
        if(hasMoved()) return;

        Pos[] possiblePosi = new Pos[]{new Pos(currentPos.first(),0), new Pos(currentPos.first(), referenceBoard.getDimX() - 1)};

        for(int i = 0; i < possiblePosi.length; i++) {
            ChessPiece rookPartner = referenceBoard.retrievePiece(possiblePosi[i]);
            if(rookPartner == null || rookPartner.side != side || !rookPartner.getName().equals("Rook")) continue; // skip over this possible castle

            // TODO: I am unsure whether the clear line of sight works properly yet - untested
            if(!rookPartner.hasMoved() && hasClearLineOfSight(currentPos.second(), i == 0 ? -1 : 1, possiblePosi[i].second(), currentPos.first(), referenceBoard.getIdentifierMap())) {
                Pos destKing = currentPos.returnModified(0, 2 * (i == 0 ? -1 : 1));
                Pos destRook = currentPos.returnModified(0, i == 0 ? -1 : 1);

                if(!referenceBoard.inBounds(destKing)) continue; // board too small for specific castle

                moves.put(destKing, new Castle(referenceBoard, destKing, currentPos, referenceBoard.getIdentifierAtPos(currentPos), destRook, possiblePosi[i], referenceBoard.getIdentifierAtPos(possiblePosi[i])));
            }
        }
    }

    // checks for a clear line of sight between the king and the rook
    public boolean hasClearLineOfSight(int index, int direction, int stopOnDex, int row, String[][] idMap) {
        if(index + direction == stopOnDex) return true;

        return idMap[row][index + direction].equals("") && hasClearLineOfSight(index + (2 * direction), direction, stopOnDex, row, idMap);
    }
}
