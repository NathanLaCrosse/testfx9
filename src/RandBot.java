import java.util.LinkedList;

// randbot just picks a random move to play
public class RandBot extends Entity {

    public RandBot(boolean side) {
        super(side);
    }

    @Override
    public Move selectMoveToPlay(LinkedList<Move> validMoves, Board gameBoard) {
        int randDex = (int)(Math.random() * validMoves.size());

        return validMoves.get(randDex);
    }

    // does nothing - doesn't need to reset anything
    @Override
    public void reset() {
        
    }
    
}
