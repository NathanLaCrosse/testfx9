// class that stores data regarding a pawn promotion
public class PawnPromotion extends Move {
    // right now it defaults to queen
    private String newName = "Queen";
    private String[] newMoves = ChessPiece.QUEEN_MOVES;
    private ChessPiece storedCapture;

    public PawnPromotion(Board referenceBoard, Pos destination, Pos originalPosition, String movingIdentifier, String capturedIdentifier) {
        super(referenceBoard, destination, originalPosition, movingIdentifier, capturedIdentifier);
    }
    
    @Override
    public void move() {
        ChessPiece movingPiece = referenceBoard.retrievePiece(movingIdentifier);
        movingPiece.currentPos = destination;
        movingPiece.name = newName;
        movingPiece.moveInstructions = newMoves;

        storedCapture = referenceBoard.retrievePiece(destination);
        if(storedCapture != null) {
            storedCapture.currentPos = null;
            referenceBoard.fiftyMoveCounter = 0;
        }else {
            referenceBoard.fiftyMoveCounter++;
        }

        referenceBoard.setIdentifierAtPos(originalPosition, "");
        referenceBoard.setIdentifierAtPos(destination, movingIdentifier);

        referenceBoard.incrementMovesMade();
    }

    @Override 
    public void undoMove() {
        ChessPiece movingPiece = referenceBoard.retrievePiece(destination);
        movingPiece.currentPos = originalPosition;
        movingPiece.name = "Pawn";
        movingPiece.moveInstructions = ChessPiece.DEFAULT_PAWN_MOVES;

        if(storedCapture != null) {
            storedCapture.currentPos = destination;
            referenceBoard.fiftyMoveCounter = previousFiftyCounter;
        }else {
            referenceBoard.fiftyMoveCounter--;
        }

        referenceBoard.setIdentifierAtPos(originalPosition, movingIdentifier);
        referenceBoard.setIdentifierAtPos(destination, capturedIdentifier);

        referenceBoard.decrementMovesMade();
    }
}
