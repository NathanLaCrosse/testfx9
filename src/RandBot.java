import java.util.LinkedList;

// randbot just picks a random move to play
public class RandBot extends Entity {

    public RandBot() {
        super();
    }

    @Override
    public Move selectMoveToPlay(LinkedList<Move> validMoves, LinkedList<ChessPiece> pieces) {
        int randDex = (int)(Math.random() * validMoves.size());

        return validMoves.get(randDex);
    }
    
}
