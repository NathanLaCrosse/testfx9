// defines the Board class which is comprised of a bunch of BoardNodes
// contains various methods to modify entire board

// TODO: add win/loss conditions to the game

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

        // set up board state
        for(int i = 0; i < 2; i++) {
            // variables which change depending on side
            boolean side = i == 0;
            String sideStr = side ? "White" : "Black";

            // add pawns
            int pawnRow = side ? 6 : 1;
            for(int k = 0; k < 8; k++) {
                Pos position = new Pos(pawnRow, k);
                Pawn pawn = new Pawn(this, position, side);

                if(side) {
                    whitePieces.add(pawn);
                }else {
                    blackPieces.add(pawn);
                }

                String identifier = sideStr + "Pawn" + k;
                identifierMap[position.first()][position.second()] = identifier;
                pieceMap.put(identifier, pawn);
            }

            int mainRow = side ? 7 : 0; // row king is on

            LinkedList<ChessPiece> mainRowPieces = new LinkedList<>();

            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 0), "Rook", side, 5, ChessPiece.ROOK_MOVES));
            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 7), "Rook", side, 5, ChessPiece.ROOK_MOVES));

            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 1), "Knight", side, 3, ChessPiece.KNIGHT_MOVES));
            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 6), "Knight", side, 3, ChessPiece.KNIGHT_MOVES));

            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 2), "Bishop", side, 3, ChessPiece.BISHOP_MOVES));
            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 5), "Bishop", side, 3, ChessPiece.BISHOP_MOVES));

            mainRowPieces.add(new ChessPiece(this, new Pos(mainRow, 3), "Queen", side, 9, ChessPiece.QUEEN_MOVES));
            ChessPiece king = new King(this, new Pos(mainRow, 4), side);
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
                        enemy.generateMoves();
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

        // get rid of moves that are not valid - they cause checks
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
}
