// this class acts as a mediator between the board tiles and the game controller
// allows the end user to play the game

import java.util.LinkedList;

public class Player extends Entity {
    private ChessPiece selectedPiece = null;
    private Move selectedMove = null;
    private boolean side;

    private Thread myThread = null;

    public Player(boolean side) {
        this.side = side;
    }

    public boolean getSide() {
        return side;
    }

    @Override
    public Move selectMoveToPlay(LinkedList<Move> validMoves, LinkedList<ChessPiece> pieces) {

        // wait until move has been selected (sleep thread we are on)
        while(selectedMove == null) {
            try {
                myThread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return selectedMove; // dummy value for now
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

    // sets the thread so we can pause it while waiting
    public void setThread(Thread t) {
        this.myThread = t;
    }

    @Override
    public void reset() {
        this.selectedMove = null;
        this.selectedPiece = null;
    }
    
}
