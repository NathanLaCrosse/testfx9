// the entity class defines what a "player" will be able to do in the game
// examples of entities are human players and bots

import java.util.LinkedList;

public abstract class Entity {
    protected boolean side;
    
    public Entity(boolean side) {
        this.side = side;
    }

    // this method will be defined by derived class to give a unique way to select a "best" move
    public abstract Move selectMoveToPlay(LinkedList<Move> validMoves, Board gameBoard);

    // override this method if any variables need resetting after a move is played
    public abstract void reset();

}
