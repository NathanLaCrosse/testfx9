// this class defines the BoardNode, which make up the chessboard
// each BoardNode contains some important memory and the other nodes in the cardinal directions
public class BoardNode implements Comparable<BoardNode> {
    
    // data stored in mode - if it is covered by either side, what piece is on it
    private String name;
    private boolean whiteAttacks = false;
    private boolean blackAttacks = false;

    // node "pointers" in each cardinal direction
    public BoardNode north = null;
    public BoardNode east = null;
    public BoardNode south = null;
    public BoardNode west = null;

    // constructor to set name
    public BoardNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    // sets the attack flag for the following side
    public void attack(boolean mySide) {
        if(mySide) {
            whiteAttacks = true;
        }else {
            blackAttacks = true;
        }
    }

    // returns true if opponent attacks square
    public boolean isAttacked(boolean mySide) {
        if(mySide) {
            return blackAttacks;
        }else {
            return whiteAttacks;
        }
    }

    // clears both if white or black attacks piece
    public void clearAttacks() {
        whiteAttacks = false;
        blackAttacks = false;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof BoardNode)) return false;

        BoardNode b = (BoardNode)o;
        return name.equals(b.getName());
    }
    @Override
    public int compareTo(BoardNode o) {
        return name.compareTo(o.getName());
    }
}
