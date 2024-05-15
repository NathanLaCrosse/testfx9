// stores extra data than the move class to allow for properly undoing an en passant
public class EnPassant extends Move {
    private String capturedPawnPosition;
    
    public EnPassant(String destination, String originalPosition, ChessPiece movingPiece, ChessPiece capturedPiece, String capturedPawnPosition) {
        super(destination, originalPosition, movingPiece, capturedPiece);
        
        this.capturedPawnPosition = capturedPawnPosition;
    }

    @Override
    public void undoMove(Board b) {
        movingPiece.currentSquare = originalPosition;
        capturedPiece.currentSquare = capturedPawnPosition; // captured piece will never be null in an en passant move
    }
}
