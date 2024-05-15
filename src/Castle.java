// stores extra data than the move class to allow for moving/unmoving a castle
public class Castle extends Move {
    private ChessPiece rook;
    private String rookOriginalPosition;
    private String rookDest;

    public Castle(String destination, String originalPosition, ChessPiece movingPiece, ChessPiece rook, String rookOriginalPosition, String rookDest) {
        super(destination, originalPosition, movingPiece, null);

        this.rook = rook;
        this.rookOriginalPosition = rookOriginalPosition;
        this.rookDest = rookDest;
    }
    
    @Override
    public void move(Board b) {
        movingPiece.currentSquare = destination;
        rook.currentSquare = rookDest;
    }

    @Override
    public void undoMove(Board b) {
        movingPiece.currentSquare = destination;
        rook.currentSquare = rookOriginalPosition;
    }

    public String getRookDest() {
        return rookDest;
    }
}
