// combines data detailing a specific move - also handles making moves on a given board

import java.util.Objects;

public class Move {
    protected int previousFiftyCounter;

    protected Pos destination;
    protected Pos originalPosition; // in regards to moving piece
    protected String movingIdentifier;
    protected String capturedIdentifier;

    protected Board referenceBoard;
    private ChessPiece storedCapture;

    public Move(Board referenceBoard, Pos destination, Pos originalPosition, String movingIdentifier, String capturedIdentifier) {
        this.previousFiftyCounter = referenceBoard.fiftyMoveCounter;
        this.referenceBoard = referenceBoard;
        this.destination = new Pos(destination);
        this.originalPosition = new Pos(originalPosition);
        this.movingIdentifier = movingIdentifier;
        this.capturedIdentifier = capturedIdentifier;

        if(movingIdentifier.equals("") || referenceBoard.retrievePiece(originalPosition) == null) {
            try {
                throw new Exception("NO MOVING IDENTIFIER");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // executes the move
    public void move() {
        System.out.println(this);
        System.out.println(movingIdentifier);
        // en passant checks (enable/disable flag)
        ChessPiece movingPiece = referenceBoard.retrievePiece(movingIdentifier);
        if(movingPiece instanceof Pawn) {
            Pawn p = (Pawn)movingPiece;
            p.checkIfUsedStartingMove(this);
        }

        movingPiece.currentPos = destination;
        storedCapture = referenceBoard.retrievePiece(destination);

        if(storedCapture != null) {
            storedCapture.currentPos = null;
            referenceBoard.fiftyMoveCounter = 0;
        }else {
            referenceBoard.fiftyMoveCounter++;
        }

        referenceBoard.setIdentifierAtPos(originalPosition, "");
        referenceBoard.setIdentifierAtPos(destination, movingIdentifier);
    }

    // undos the move - assumes this in done in the correct order (no other moves made)
    public void undoMove() {
        // disable any enabled en passant flag
        System.out.println(this);
        System.out.println(movingIdentifier);
        ChessPiece movingPiece = referenceBoard.retrievePiece(movingIdentifier);
        if(movingPiece instanceof Pawn) {
            Pawn p = (Pawn)movingPiece;
            p.disableEnPasantFlag();
        }

        movingPiece.currentPos = originalPosition;
        if(storedCapture != null) {
            storedCapture.currentPos = destination;
            referenceBoard.fiftyMoveCounter = previousFiftyCounter;
        }else {
            referenceBoard.fiftyMoveCounter--;
        }

        referenceBoard.setIdentifierAtPos(originalPosition, movingIdentifier);
        referenceBoard.setIdentifierAtPos(destination, capturedIdentifier);
    }

    @Override 
    public String toString() {
        return "Moving From " + referenceBoard.createStringThroughPos(originalPosition) + " To " + referenceBoard.createStringThroughPos(destination);
    }
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Move)) return false;
        Move m = (Move)o;
        return m.originalPosition.equals(originalPosition) && m.destination.equals(destination);
    }
    @Override 
    public int hashCode() {
        return Objects.hash(originalPosition, destination);
    }
}
