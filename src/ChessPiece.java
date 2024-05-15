// class that represents a given chess piece

import java.util.LinkedList;

public class ChessPiece {
    public static final String[] KNIGHT_MOVES = new String[]{"C|NNW","C|NNE","C|EEN","C|EES","C|SSW","C|SSE","C|WWN","C|WWS"};
    public static final String[] ROOK_MOVES = new String[]{"CL|N","CL|S","CL|E","CL|W"};
    public static final String[] BISHOP_MOVES = new String[]{"CL|NW","CL|NE","CL|SE","CL|SW"};
    public static final String[] QUEEN_MOVES = new String[]{"CL|N","CL|S","CL|E","CL|W","CL|NW","CL|NE","CL|SE","CL|SW"};

    public static final String[] DEFAULT_PAWN_MOVES = new String[]{"SI2|N","O|NW","O|NE"};
    public static final String[] DEFAULT_KING_MOVES = new String[]{"C|N","C|NE","C|E","C|SE","C|S","C|SW","C|W","C|NW"};

    protected Board referenceBoard;

    // these two variables hold the "name" of a given square which can be used to search the board for
    // its current location
    protected String startingSquare;
    protected String currentSquare;

    private String name;

    protected boolean side;
    protected int material;

    // a move instruction is comprised of the modifier segment and a set of directions
    // ex: L| is a modifier that makes the move loop 
    // ex: L|NE repeats the move north, east until reaching end of board
    private String[] moveInstructions;

    protected LinkedList<Move> moves; // moves will be used infrequently, so using a linked list for fast addition + removal

    public ChessPiece(Board referenceBoard, String name, boolean side, int material, String[] moveInstructions) {
        this.referenceBoard = referenceBoard;

        this.name = name;
        this.side = side;
        this.material = material;
        this.moveInstructions = moveInstructions;

        moves = new LinkedList<>();

        startingSquare = "?-1";
        currentSquare = "?-1";
    }

    public String getName() {
        return name;
    }
    public boolean getSide() {
        return side;
    }
    public int getMaterial() {
        return material;
    }
    public LinkedList<Move> getMoves() {
        return moves;
    }
    public boolean hasMoved() {
        return !startingSquare.equals(currentSquare);
    }

    public void setStartingLocation(BoardNode node) {
        currentSquare = node.getName();
        startingSquare = node.getName();
    }

    public boolean captured() {
        return referenceBoard.findNode(currentSquare) == null;
    }

    // stores all valid moves in the moves linkedlist
    // this method returns the node the piece is currently on to be used in any derived classes
    public BoardNode generateMoves(Board b) {
        BoardNode currentNode = referenceBoard.findNode(currentSquare);
        if(currentNode == null) return null; // in case this piece is off grid (captured)

        moves = new LinkedList<>();

        for(int i = 0; i < moveInstructions.length; i++) {
            boolean nonLoopingFlag = false;

            String modifier = moveInstructions[i].substring(0, moveInstructions[i].indexOf("|")); // informs us of modifiers needed
            String directions = moveInstructions[i].substring(moveInstructions[i].indexOf("|") + 1); // path of move

            // check for modifier statements
            // each modifier has its own unique character 
            boolean canCapture = modifier.indexOf("C") != -1;
            boolean onlyCapture = modifier.indexOf("O") != -1;
            boolean loop = modifier.indexOf("L") != -1;
            boolean startingMove = modifier.indexOf("S") != -1;

            // example sequence modifier: I3|N 
            // goes north 3 times
            int sequence = 0;
            int sequenceCounter = 0;
            int sequenceDex = modifier.indexOf("I");
            if(sequenceDex != -1) {
                sequence = Integer.parseInt(modifier.substring(modifier.indexOf("I") + 1, modifier.indexOf("I") + 2));
            }

            // a starting sequences either repeats ex: SI3|NE
            boolean startingSequence = modifier.indexOf("SI") != -1;

            if(!startingSequence && startingMove && !hasMoved()) continue; // skip over this starting move if we've already moved (excludes a starting sequence)

            BoardNode moveDest = findDest(directions, currentNode);
            while(moveDest != null && (!nonLoopingFlag || loop || (sequenceCounter < sequence && (!startingSequence || !hasMoved())))) {
                ChessPiece destPiece = b.findPiece(moveDest.getName());

                if(destPiece == null) {
                    if(onlyCapture) break;

                    moves.add(new Move(moveDest.getName(), currentSquare, this, destPiece));
                    moveDest.attack(side);
                }else if(destPiece.side != side && (canCapture || onlyCapture)) {
                    moves.add(new Move(moveDest.getName(), currentSquare, this, destPiece));
                    moveDest.attack(side);

                    break;
                }else {
                    break;
                }

                nonLoopingFlag = true;
                if(loop || sequenceCounter < sequence) {
                    moveDest = findDest(directions, moveDest);
                }

                sequenceCounter++;
            }
        }

        return currentNode;
    }
    // recursively traverse the board using the instruction as a guide
    private BoardNode findDest(String instruction, BoardNode visit) {
        if(visit == null || instruction.length() < 1) return visit;

        switch(instruction.charAt(0)) {
            case 'N':
                if(side) {
                    return findDest(instruction.substring(1), visit.north);
                }else {
                    return findDest(instruction.substring(1), visit.south);
                }
            case 'S':
                if(side) {
                    return findDest(instruction.substring(1), visit.south);
                }else {
                    return findDest(instruction.substring(1), visit.north);
                }
            case 'E':
                if(side) {
                    return findDest(instruction.substring(1), visit.east);
                }else {
                    return findDest(instruction.substring(1), visit.west);
                }
            case 'W':
                if(side) {
                    return findDest(instruction.substring(1), visit.west);
                }else {
                    return findDest(instruction.substring(1), visit.east);
                }
        }

        return null; // this line should never be reached (always one of 4 cases)
    }
}
