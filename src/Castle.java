// stores extra data than the move class to allow for moving/unmoving a castle
public class Castle extends Move {
    private String rookIdentifier;
    private Pos rookOriginalPosition;
    private Pos rookDest;

    public Castle(Board referenceBoard, Pos destination, Pos originalPosition, String movingIdentifier, Pos rookDest, Pos rookOriginalPosition, String rookIdentifier) {
        super(referenceBoard, destination, originalPosition, movingIdentifier, "");

        this.rookDest = new Pos(rookDest);
        this.rookOriginalPosition = new Pos(rookOriginalPosition);
        this.rookIdentifier = rookIdentifier;
    }
    
    @Override
    public void move() {
        referenceBoard.retrievePiece(originalPosition).currentPos = destination;
        referenceBoard.retrievePiece(rookOriginalPosition).currentPos = rookDest;

        referenceBoard.setIdentifierAtPos(originalPosition, "");
        referenceBoard.setIdentifierAtPos(rookOriginalPosition, "");
        referenceBoard.setIdentifierAtPos(destination, movingIdentifier);
        referenceBoard.setIdentifierAtPos(rookDest, rookIdentifier);

        referenceBoard.fiftyMoveCounter++;

        referenceBoard.incrementMovesMade();
    }

    @Override
    public void undoMove() {
        referenceBoard.retrievePiece(destination).currentPos = originalPosition;
        referenceBoard.retrievePiece(rookDest).currentPos = rookOriginalPosition;

        referenceBoard.setIdentifierAtPos(destination, "");
        referenceBoard.setIdentifierAtPos(rookDest, "");
        referenceBoard.setIdentifierAtPos(originalPosition, movingIdentifier);
        referenceBoard.setIdentifierAtPos(rookOriginalPosition, rookIdentifier);

        referenceBoard.fiftyMoveCounter--;

        referenceBoard.decrementMovesMade();
    }

    public Pos getRookDest() {
        return rookDest;
    }
}
