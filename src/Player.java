// this class acts as a mediator between the board tiles and the game controller
// allows the end user to play the game

// note: most of the piece & tile selection code is actually in 

import java.util.LinkedList;

public class Player extends Entity {
    private ChessPiece selectedPiece = null;
    private Move selectedMove = null;

    public Player(boolean side) {
        super(side);
    }

    public boolean getSide() {
        return side;
    }

    @Override
    public Move selectMoveToPlay(LinkedList<Move> validMoves, Board gameBoard) {

        // wait until move has been selected (sleep thread we are on)
        while(selectedMove == null) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return selectedMove; 
    }

    // selects a given move to play if the selected move is a valid move
    // of the previously selected piece
    public void selectIfCanPlay(Pos pos) {
        if(selectedPiece == null) return;

        if(selectedPiece.attacksSquare(pos)) {
            selectedMove = selectedPiece.getMoves().get(pos);
        }
    }

    public void setPiece(ChessPiece piece) {
        this.selectedPiece = piece;
    }
    public ChessPiece getSelectedPiece() {
        return selectedPiece;
    }

    @Override
    public void reset() {
        this.selectedMove = null;
        this.selectedPiece = null;
    }
    
}
