// defines the Board class which is comprised of a bunch of BoardNodes
// contains various methods to modify entire board

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Board {
    private static final int A_VAL = (int)'a';

    // not intended to be altered by a chess bot
    protected int fiftyMoveCounter = 0;

    private int dimX;
    private int dimY;
    private int movesMade;

    private String[][] identifierMap; // contains a string representing a piece
    private HashMap<String, ChessPiece> pieceMap;

    private LinkedList<ChessPiece> whitePieces;
    private LinkedList<ChessPiece> blackPieces;

    // don't need to worry about these after setting up due to aliases in the king's Pos
    private ChessPiece whiteKing = null;
    private ChessPiece blackKing = null;

    public Board(int dimX, int dimY) {
        pieceMap = new HashMap<>();
        
        whitePieces = new LinkedList<>();
        blackPieces = new LinkedList<>();

        this.dimX = dimX;
        this.dimY = dimY;
        movesMade = 0;

        initializeBoard();
    }
    public Board() {
        this(8,8);
    }

    // initialize board based on dimX, dimY and a preloaded board
    // TODO: create "preloaded boards" - probably with a String
    private void initializeBoard() {
        identifierMap = new String[8][8];

        for(int i = 0; i < identifierMap.length; i++) {
            for(int k = 0; k < identifierMap[0].length; k++) { // assumes a rectangular region
                identifierMap[i][k] = "";
            }
        }

        // set up board state
        for(int i = 0; i < 2; i++) {
            // variables which change depending on side
            boolean side = i == 0;
            String sideStr = side ? "White" : "Black";

            // add pawns
            int pawnRow = side ? 6 : 1;
            for(int k = 0; k < 8; k++) {
                Pos position = new Pos(pawnRow, k);
                String identifier = sideStr + "Pawn" + k;

                Pawn pawn = new Pawn(this, position, side, identifier);

                if(side) {
                    whitePieces.add(pawn);
                }else {
                    blackPieces.add(pawn);
                }

                identifierMap[position.first()][position.second()] = identifier;
                pieceMap.put(identifier, pawn);
            }

            int mainRow = side ? dimY - 1 : 0; // row king is on

            LinkedList<ChessPiece> mainRowPieces = new LinkedList<>();

            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 0), "Rook", side, 5, ChessPiece.ROOK_MOVES, ""));
            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 7), "Rook", side, 5, ChessPiece.ROOK_MOVES, ""));

            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 1), "Knight", side, 3, ChessPiece.KNIGHT_MOVES, ""));
            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 6), "Knight", side, 3, ChessPiece.KNIGHT_MOVES, ""));

            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 2), "Bishop", side, 3, ChessPiece.BISHOP_MOVES, ""));
            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 5), "Bishop", side, 3, ChessPiece.BISHOP_MOVES, ""));

            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 3), "Queen", side, 9, ChessPiece.QUEEN_MOVES, ""));
            ChessPiece king = new King(this, new Pos(mainRow, 4), side, "");
            mainRowPieces.add(king);

            if(side) {
                whiteKing = king;
            }else {
                blackKing = king;
            }

            int increment = 0;
            for(ChessPiece piece : mainRowPieces) {
                String identifier = sideStr + piece.getName() + increment;

                identifierMap[piece.currentPos.first()][piece.currentPos.second()] = identifier;
                pieceMap.put(identifier, piece);
                piece.setIdentifier(identifier);

                if(side) {
                    whitePieces.add(piece);
                }else {
                    blackPieces.add(piece);
                }

                increment++;
            }

        }
        
    }

    public LinkedList<ChessPiece> getPiecesOnSide(boolean side) {
        if(side) {
            return whitePieces;
        }else {
            return blackPieces;
        }
    }
    public ChessPiece getKingOnSide(boolean side) {
        if(side) {
            return whiteKing;
        }else {
            return blackKing;
        }
    }

    public int getDimX() {
        return dimX;
    }
    public int getDimY() {
        return dimY;
    }
    public String[][] getIdentifierMap() {
        return identifierMap;
    }

    protected void incrementMovesMade() {
        movesMade++;
    }
    protected void decrementMovesMade() {
        movesMade--;
    }
    public int getMovesMade() {
        return movesMade;
    }

    public boolean inBounds(Pos p) {
        if(p == null) return false;

        return p.first() < dimY && p.first() > -1 && p.second() < dimX && p.second() > -1;
    }
    public void setIdentifierAtPos(Pos p, String newVal) {
        identifierMap[p.first()][p.second()] = newVal;
    }
    public String getIdentifierAtPos(Pos p) {
        return identifierMap[p.first()][p.second()];
    }

    public Pos createPosThroughString(String str) {
        int firstDex = (int)str.charAt(0) - A_VAL;
        int secondDex = Integer.parseInt(str.substring(1)) - 1;
        return new Pos(firstDex, secondDex);
    }
    public String createStringThroughPos(Pos pos) {
        char c = (char)(A_VAL + (dimY - 1) - pos.first());
        return "" + c + (pos.second() + 1);
    }

    // finds a piece given its identifier from identifierMap
    public ChessPiece retrievePiece(String identifier) {
        return pieceMap.get(identifier);
    }
    public ChessPiece retrievePiece(Pos pos) {
        return pieceMap.get(identifierMap[pos.first()][pos.second()]);
    }

    // returns true if the position is in the promotion rank for a pawn
    public boolean inPromotionRank(boolean side, int rank) {
        if(side) {
            return rank == 0;
        }else {
            return rank == dimY - 1;
        }
    }

    /* Code for end states of the board */
    public boolean inCheck(boolean side) {
        // if we are in check, then the opponent's pieces should have a previously generated move which attacks our king
        LinkedList<ChessPiece> enemyPieces = side ? blackPieces : whitePieces;
        ChessPiece myKing = side ? whiteKing : blackKing;

        for(ChessPiece piece : enemyPieces) {
            if(piece.attacksSquare(myKing.currentPos)) return true;
        }
        return false;
    }
    // returns true if there is insufficient material on the board - no checkmates possible
    // TODO: Test insufficient material cases
    public boolean insufficientMaterial() {
        LinkedList<ChessPiece> whitePiecesInBounds = new LinkedList<>();
        LinkedList<ChessPiece> blackPiecesInBounds = new LinkedList<>();

        for(ChessPiece piece : whitePieces) {
            if(inBounds(piece.currentPos) && !(piece instanceof King)) {
                whitePiecesInBounds.add(piece);
                if(whitePiecesInBounds.size() > 1) return false; // enough pieces on board
            }
        }
        for(ChessPiece piece : blackPieces) {
            if(inBounds(piece.currentPos) && !(piece instanceof King)) {
                blackPiecesInBounds.add(piece);
                if(blackPiecesInBounds.size() > 1) return false; // enough pieces on board
            }
        }

        if(whitePiecesInBounds.size() == 0 && blackPiecesInBounds.size() == 0) return true; // only two kings

        // past this point, at least one side has only 1 piece left (excluding kings)

        if(whitePiecesInBounds.size() == 1 && blackPiecesInBounds.size() == 1) {
            ChessPiece possibleBishop1 = whitePiecesInBounds.getFirst();
            ChessPiece possibleBishop2 = blackPiecesInBounds.getFirst();

            // exit if any remaining piece isn't a bishop
            if(!possibleBishop1.getName().contains("Bishop") || !possibleBishop2.getName().contains("Bishop")) return false;

            // if both bishops are on the same colored square, there is insufficient material
            Pos pos1 = possibleBishop1.currentPos;
            Pos pos2 = possibleBishop2.currentPos;

            return pos1.first() + pos1.second() % 2 == pos2.first() + pos2.second() % 2;
        }

        // now there is only one piece to look at - if it is a knight or bishop there is insufficient material
        ChessPiece onlyPieceOnBoard = whitePiecesInBounds.size() == 1 ? whitePiecesInBounds.getFirst() : blackPiecesInBounds.getFirst();

        return onlyPieceOnBoard.getName().contains("Knight") || onlyPieceOnBoard.getName().contains("Bishop");
    }
    // the logic behind this method is implemented in the Move and related derived classes
    // TODO: make sure fifty move rule is properly implemented
    public boolean fiftyMoveRuleValid() {
        return fiftyMoveCounter == 50;
    }
    // returns -1 if the board isnt in a end state. If it is in an ending state, then expect values from 0-3
    // this method should be called after a call to valid moves for its side
    // 0 - insufficient material
    // 1 - fifty move rule
    // 2 - checkmate
    // 3 - stalemate
    public int getEndCondition(boolean side) {
        if(insufficientMaterial()) return 0;
        if(fiftyMoveRuleValid()) return 1;

        // check for valid moves
        for(ChessPiece piece : getPiecesOnSide(side)) {
            if(piece.getMoves().size() > 0) return -1;
        }

        // no moves past this point
        if(inCheck(side)) return 2; // checkmate
        else return 3; // stalemate
    }

    // note: kings are not counted in material checking
    public int materialOnSide(boolean side) {
        LinkedList<ChessPiece> pieces = getPiecesOnSide(side);

        int total = 0;
        for(ChessPiece piece : pieces) {
            if(!inBounds(piece.currentPos) || piece instanceof King) continue; // skip over if out of bounds or king

            total += piece.getMaterial();
        }
        return total;
    }

    // returns true if the enemy can attack a given position
    // assumes that the enemy pieces have the latest moves generated on them
    public boolean squareIsAttacked(Pos p, boolean side) {
        LinkedList<ChessPiece> enemyPieces = side ? blackPieces : whitePieces;

        for(ChessPiece piece : enemyPieces) {
            if(piece.attacksSquare(p)) {
                return true;
            }
        }

        return false;
    }

    // generates moves and makes sure they do not cause the player to lose
    public LinkedList<Move> generateSanitizedMovesForSide(boolean side) {
        LinkedList<ChessPiece> enemyPieces = side ? blackPieces : whitePieces;
        LinkedList<ChessPiece> myPieces = side ? whitePieces : blackPieces;
        ChessPiece myKing = side ? whiteKing : blackKing;

        LinkedList<Move> sanitizedList = new LinkedList<>();

        LinkedList<Move> destroyList = new LinkedList<>();

        // we need to check each possible move and see whether or not it will land us in check, which would cause a player to walk into checkmate
        for(ChessPiece myPiece : myPieces) {
            myPiece.generateMoves();
            HashMap<Pos, Move> possibleMoves = myPiece.getMoves();
            
            for(Move m : possibleMoves.values()) {
                boolean checkPresent = false;
                Iterator<ChessPiece> itr = enemyPieces.iterator();         

                // make move to test this new board state
                m.move();

                // check all enemy moves to see if they cause a check (a move can go to our king's square)
                while(itr.hasNext() && !checkPresent) {
                    ChessPiece enemy = itr.next();
                    enemy.generateMoves();
                    checkPresent = enemy.attacksSquare(myKing.currentPos);
                }

                // undo the move once we're done with it
                m.undoMove();

                // properly handle the move if it is a castle
                if(m instanceof Castle && !checkPresent) {
                    itr = enemyPieces.iterator();

                    Castle c = (Castle)m;

                    while(itr.hasNext() && !checkPresent) {
                        ChessPiece enemy = itr.next();
                        //enemy.generateMoves();
                        checkPresent = enemy.attacksSquare(c.getRookDest()) || enemy.attacksSquare(myKing.currentPos);
                    }
                }
                
                // only add this move to our sanitized list if it doesn't lead into a check
                if(!checkPresent) {
                    sanitizedList.add(m);
                }else {
                    destroyList.add(m);
                }
            }
        }

        // get rid of invalid moves that are stored on a given piece
        while(destroyList.size() > 0) {
            Move destroyMove = destroyList.getFirst();
            ChessPiece piece = retrievePiece(destroyMove.movingIdentifier);

            piece.removeMove(destroyMove.destination);
            destroyList.removeFirst();
        }

        return sanitizedList;
    }

    @Override
    public String toString() {
        String str = "";
        for(int i = 0; i < dimY; i++) {
            for(int k = 0; k < dimX; k++) {
                str += "" + (char)(A_VAL + dimY - 1 - i) + (k + 1) + " ";
            }
            str += "\n";
        }
        return str;
    }

    protected String generateBoardString() {
        String str = "";
        for(int i = 0; i < identifierMap.length; i++) {
            for(int k = 0; k < identifierMap[0].length; k++) {
                if(identifierMap[i][k].equals("")) {
                    str += "BLANK";
                }else {
                    str += identifierMap[i][k];
                }
            }
        }
        return str;
    }
}
