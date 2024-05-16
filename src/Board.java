// defines the Board class which is comprised of a bunch of BoardNodes
// contains various methods to modify entire board

// NOTES FOR WHEN WORKING ON A CHESS BOT
// The board should be able to make a copy of itself for the bot to use
// In this copy, the bot will make a move to check how good it is
// To prevent many copies being made, the bot should also be able to undo a move

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Board {
    private static final int A_VAL = (int)'a';

    private int dimX;
    private int dimY;

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

        // TODO: get rid of this testing code and properly set up the board
        // first character is important: W = white, B = black
        identifierMap[7][3] = "WKing";
        //identifierMap[7][7] = "WRook";
        identifierMap[0][0] = "BKing";
        identifierMap[7][4] = "BRook";
        identifierMap[4][7] = "BBishop";

        ChessPiece whiteKing = new King(this, new Pos(7,3), true);
        //ChessPiece whiteRook = new ChessPiece(this, new Pos(7,7), "Rook", true, 5, ChessPiece.ROOK_MOVES);
        ChessPiece blackKing = new King(this, new Pos(0, 0), false);
        ChessPiece blackRook = new ChessPiece(this, new Pos(7,4), "Rook", false, 5, ChessPiece.ROOK_MOVES);
        ChessPiece blackBishop = new ChessPiece(this, new Pos(4,7), "Bishop", false, 3, ChessPiece.BISHOP_MOVES);

        pieceMap.put("WKing", whiteKing);
        //pieceMap.put("WRook", whiteRook);
        pieceMap.put("BKing", blackKing);
        pieceMap.put("BRook", blackRook);
        pieceMap.put("BBishop", blackBishop);

        this.whiteKing = whiteKing;
        this.blackKing = blackKing;

        whitePieces.add(whiteKing);
        //whitePieces.add(whiteRook);
        blackPieces.add(blackKing);
        blackPieces.add(blackRook);
        blackPieces.add(blackBishop);
        
    }

    public LinkedList<ChessPiece> getPiecesOnSide(boolean side) {
        if(side) {
            return whitePieces;
        }else {
            return blackPieces;
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

    public boolean inBounds(Pos p) {
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
    ChessPiece retrievePiece(String identifier) {
        return pieceMap.get(identifier);
    }
    ChessPiece retrievePiece(Pos pos) {
        return pieceMap.get(identifierMap[pos.first()][pos.second()]);
    }

    // generates moves and makes sure they do not cause the player to lose
    public LinkedList<Move> generateSanitizedMovesForSide(boolean side) {
        LinkedList<ChessPiece> enemyPieces = side ? blackPieces : whitePieces;
        LinkedList<ChessPiece> myPieces = side ? whitePieces : blackPieces;
        ChessPiece myKing = side ? whiteKing : blackKing;

        LinkedList<Move> sanitizedList = new LinkedList<>();

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
                        enemy.generateMoves();
                        checkPresent = enemy.attacksSquare(c.getRookDest()) || enemy.attacksSquare(myKing.currentPos);
                    }
                }

                // only add this move to our sanitized list if it doesn't lead into a check
                if(!checkPresent) {
                    sanitizedList.add(m);
                }
            }
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
}
