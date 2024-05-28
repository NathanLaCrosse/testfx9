// class that represents a given chess piece

import java.util.HashMap;

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
    protected Pos startingPos;
    protected Pos currentPos;

    private String name;

    protected boolean side;
    protected int material;

    // a move instruction is comprised of the modifier segment and a set of directions
    // ex: L| is a modifier that makes the move loop 
    // ex: L|NE repeats the move north, east until reaching end of board
    private String[] moveInstructions;

    protected HashMap<Pos, Move> moves; // moves will be used infrequently, so using a linked list for fast addition + removal

    public ChessPiece(Board referenceBoard, Pos startingPos, String name, boolean side, int material, String[] moveInstructions) {
        this.referenceBoard = referenceBoard;

        this.name = name;
        this.side = side;
        this.material = material;
        this.moveInstructions = moveInstructions;

        moves = new HashMap<>();

        this.startingPos = startingPos;
        this.currentPos = new Pos(startingPos);
    }
    public ChessPiece(Board referenceBoard, Pos startingPos, boolean side, int material, String[] moveInstructions) {
        this(referenceBoard, startingPos, "Placeholder", side, material, moveInstructions);
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
    public HashMap<Pos, Move> getMoves() {
        return moves;
    }
    public boolean hasMoved() {
        return !startingPos.equals(currentPos);
    }

    public boolean attacksSquare(Pos pos) {
        return moves.get(pos) != null;
    }

    public void removeMove(Pos p) {
        moves.remove(p);
    }

    // stores all valid moves in the moves linkedlist
    // this method returns the node the piece is currently on to be used in any derived classes
    public void generateMoves() {
        moves = new HashMap<>();
        if(currentPos == null) return;

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
                sequence = Integer.parseInt(modifier.substring(sequenceDex + 1, sequenceDex + 2));
            }

            // a starting sequence repeats only when it hasnt moved (there will still be one move if it has moved) ex: SI3|NE
            boolean startingSequence = modifier.indexOf("SI") != -1;

            if(!startingSequence && startingMove && !hasMoved()) continue; // skip over this starting move if we've already moved (excludes a starting sequence)

            // find a possible move but stop if not looping/come into contact with another piece
            Pos destCoords = findDestCoords(directions, currentPos);
            while(referenceBoard.inBounds(destCoords) && (!nonLoopingFlag || loop || (sequenceCounter < sequence && (!startingSequence || !hasMoved())))) {
                ChessPiece destPiece = referenceBoard.retrievePiece(destCoords);

                if(destPiece == null) {
                    if(onlyCapture) break;

                    moves.put(destCoords, new Move(referenceBoard, destCoords, currentPos, referenceBoard.getIdentifierAtPos(currentPos), referenceBoard.getIdentifierAtPos(destCoords)));
                }else if(destPiece.side != side && (canCapture || onlyCapture)) {
                    moves.put(destCoords, new Move(referenceBoard, destCoords, currentPos, referenceBoard.getIdentifierAtPos(currentPos), referenceBoard.getIdentifierAtPos(destCoords)));

                    break;
                }else {
                    break;
                }

                nonLoopingFlag = true;
                if(loop || sequenceCounter < sequence) {
                    destCoords = findDestCoords(directions, destCoords);
                }

                sequenceCounter++;
            }
        }
    }
    // recursively traverse the board using the instruction as a guide
    private Pos findDestCoords(String instruction, Pos pos) {
        Pos dest = new Pos(pos);

        // traverse the identifier map using the instructions
        for(int i = 0; i < instruction.length(); i++) {
            char c = instruction.charAt(i);

            switch (c) {
                case 'N':
                    if(side) {
                        dest.moveNorth();
                    }else {
                        dest.moveSouth();
                    }
                    break;
                case 'S':
                    if(side) {
                        dest.moveSouth();
                    }else {
                        dest.moveNorth();
                    }
                    break;
                case 'E':
                    if(side) {
                        dest.moveEast();
                    }else {
                        dest.moveWest();
                    }
                    break;
                default: // 'W' case (default = else)
                    if(side) {
                        dest.moveWest();
                    }else {
                        dest.moveEast();
                    }
                    break;
            }
        }

        return dest;
    }

    @Override
    public String toString() {
        return (side ? "White " : "Black ") + name + " At " + referenceBoard.createStringThroughPos(currentPos);
    }
}
