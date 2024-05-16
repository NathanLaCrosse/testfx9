// combines data detailing a specific move - also handles making moves on a given board
public class Move {
    protected Pos destination;
    protected Pos originalPosition; // in regards to moving piece
    protected String movingIdentifier;
    protected String capturedIdentifier;

    protected Board referenceBoard;
    private ChessPiece storedCapture;

    public Move(Board referenceBoard, Pos destination, Pos originalPosition, String movingIdentifier, String capturedIdentifier) {
        this.referenceBoard = referenceBoard;
        this.destination = new Pos(destination);
        this.originalPosition = new Pos(originalPosition);
        this.movingIdentifier = movingIdentifier;
        this.capturedIdentifier = capturedIdentifier;
    }

    // executes the move
    public void move() {
        referenceBoard.retrievePiece(originalPosition).currentPos = destination;
        storedCapture = referenceBoard.retrievePiece(destination);
        if(storedCapture != null) storedCapture.currentPos = null;

        referenceBoard.setIdentifierAtPos(originalPosition, null);
        referenceBoard.setIdentifierAtPos(destination, movingIdentifier);
    }

    // undos the move - assumes this in done in the correct order (no other moves made)
    public void undoMove() {
        referenceBoard.retrievePiece(destination).currentPos = originalPosition;
        if(storedCapture != null) storedCapture.currentPos = destination;

        referenceBoard.setIdentifierAtPos(originalPosition, movingIdentifier);
        referenceBoard.setIdentifierAtPos(destination, capturedIdentifier);
    }

    @Override 
    public String toString() {
        return "Moving From " + referenceBoard.createStringThroughPos(originalPosition) + " To " + referenceBoard.createStringThroughPos(destination);
    }
}
