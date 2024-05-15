// combines data detailing a specific move - also handles making moves on a given board
public class Move {
    protected String destination;
    protected String originalPosition; // in regards to moving piece
    protected ChessPiece movingPiece;
    protected ChessPiece capturedPiece;

    public Move(String destination, String originalPosition, ChessPiece movingPiece, ChessPiece capturedPiece) {
        this.destination = destination;
        this.originalPosition = originalPosition;
        this.movingPiece = movingPiece;
        this.capturedPiece = capturedPiece;
    }

    // executes the move
    public void move(Board board) {
        movingPiece.currentSquare = destination;
        if(capturedPiece != null) capturedPiece.currentSquare = "?-1";
    }

    // undos the move - assumes this in done in the correct order (no other moves made)
    public void undoMove(Board board) {
        movingPiece.currentSquare = originalPosition;
        if(capturedPiece != null) capturedPiece.currentSquare = destination;
    }

    @Override 
    public String toString() {
        return "Moving" + (movingPiece.side ? " White " : " Black ") + movingPiece.getName() + " From " + originalPosition + " To " + destination;
    }
}
