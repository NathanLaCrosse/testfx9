// stores extra data than the move class to allow for properly undoing an en passant
public class EnPassant extends Move {
    private Pos capturedPawnPosition;

    private ChessPiece storedCapture;
    
    public EnPassant(Board referenceBoard, Pos destination, Pos originalPosition, String movingIdentifier, String capturedIndentifier, Pos capturedPawnPosition) {
        super(referenceBoard, destination, originalPosition, movingIdentifier, capturedIndentifier);
        
        this.capturedPawnPosition = new Pos(capturedPawnPosition);
    }

    @Override
    public void move() {
        referenceBoard.retrievePiece(originalPosition).currentPos = destination;
        storedCapture = referenceBoard.retrievePiece(destination);
        storedCapture.currentPos = null;

        referenceBoard.setIdentifierAtPos(originalPosition, "");
        referenceBoard.setIdentifierAtPos(capturedPawnPosition, "");
        referenceBoard.setIdentifierAtPos(destination, movingIdentifier);
    }

    @Override
    public void undoMove() {
        referenceBoard.retrievePiece(destination).currentPos = destination;

        referenceBoard.setIdentifierAtPos(originalPosition, movingIdentifier);
        referenceBoard.setIdentifierAtPos(capturedPawnPosition, capturedIdentifier);
        referenceBoard.setIdentifierAtPos(destination, "");
    }
}
