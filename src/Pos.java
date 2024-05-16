// bundles two numbers together to be used as indices in a 2d array

import java.util.Objects;

public class Pos {
    // first is intrepreted as the first index (row) and second is the second index (col)
    private int first, second;

    public Pos(int first, int second) {
        this.first = first;
        this.second = second;
    }
    public Pos(Pos p) {
        this(p.first, p.second);
    }

    public int first() {
        return first;
    }
    public int second() {
        return second;
    }

    public void moveNorth() {
        first--;
    }
    public void moveSouth() {
        first++;
    }
    public void moveEast() {
        second++;
    }
    public void moveWest() {
        second--;
    }

    public Pos returnModified(int changeFirst, int changeSecond) {
        return new Pos(first + changeFirst, second + changeSecond);
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Pos)) return false;
        
        Pos p = (Pos)o;
        return p.first == first && p.second == second;
    } 
    // necessary for Pos to work as a key in a hash table
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
