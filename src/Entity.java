// the entity class defines what a "player" will be able to do in the game
// examples of entities are human players and bots

import java.util.LinkedList;

public abstract class Entity {
    
    public Entity() {

    }

    // this method will be defined by derived class to give a unique way to select a "best" move
    public abstract Move selectMoveToPlay(LinkedList<Move> validMoves, LinkedList<ChessPiece> pieces);

}
