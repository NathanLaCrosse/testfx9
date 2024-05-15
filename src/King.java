// class to provide special implementation of the king's castling move
public class King extends ChessPiece {

    // the king requires the board as it needs to search for its rooks
    public King(Board referenceBoard, boolean side) {
        super(referenceBoard,"King", side, 0, ChessPiece.DEFAULT_KING_MOVES);
    }
    
    @Override
    public BoardNode generateMoves(Board b) {
        BoardNode currentNode = super.generateMoves(b); // generate default moves
        if(currentNode == null) return null; // in case this piece is off grid (captured)

        // special case: castling
        // TODO: when checking for checks there will need to be an elimation of castling if any part of it is in check or if the king itself is in check
        if(hasMoved()) return null;

        String rank = currentNode.getName().substring(0,1);
        BoardNode[] possibleNodes = new BoardNode[]{referenceBoard.findNode(rank + "1"),referenceBoard.findNode(rank + referenceBoard.getDimX())};

        for(int i = 0; i < possibleNodes.length; i++) {
            ChessPiece rookPartner = b.findPiece(possibleNodes[i].getName());
            if(rookPartner == null || rookPartner.side != side || !rookPartner.getName().equals("Rook")) continue; // skip over this possible castle

            if(!rookPartner.hasMoved()) {
                BoardNode destKing = null;
                BoardNode destRook = null;

                if(i == 0) {
                    if(currentNode != null) destRook = currentNode.west;
                    if(destRook!= null) destKing = destRook.west;
                }else {
                    if(currentNode != null) destRook = currentNode.east;
                    if(destRook!= null) destKing = destRook.east;
                }

                if(destKing == null || destRook == null) continue; // board too small for specific castle

                moves.add(new Castle(destKing.getName(), currentSquare, this, rookPartner, possibleNodes[i].getName(), destRook.getName()));
            }
        }

        return null;
    }
}
