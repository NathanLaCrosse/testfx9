// defines the Board class which is comprised of a bunch of BoardNodes
// contains various methods to modify entire board

// NOTES FOR WHEN WORKING ON A CHESS BOT
// The board should be able to make a copy of itself for the bot to use
// In this copy, the bot will make a move to check how good it is
// To prevent many copies being made, the bot should also be able to undo a move

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Board {
    private static final int A_VAL = (int)'a';

    private int dimX;
    private int dimY;
    
    private BoardNode bottomLeft;
    private HashMap<String, BoardNode> board; // designed to have quick access to each node

    private LinkedList<ChessPiece> allPieces; // needed for searching for a given piece
    private LinkedList<ChessPiece> whitePieces;
    private LinkedList<ChessPiece> blackPieces;

    private ChessPiece whiteKing = null;
    private ChessPiece blackKing = null;

    public Board(int dimX, int dimY) {
        board = new HashMap<>();

        buildBoard(dimX, dimY);
        
        allPieces = new LinkedList<>();
        whitePieces = new LinkedList<>();
        blackPieces = new LinkedList<>();

        this.dimX = dimX;
        this.dimY = dimY;
    }
    public Board() {
        this(8,8);
    }

    public BoardNode getBottomLeftNode() {
        return bottomLeft;
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

    // sets up the board with a certain dimension (has to be rectangular) and links all nodes together
    public void buildBoard(int dimX, int dimY) {
        bottomLeft = new BoardNode("a1");
        board.put(bottomLeft.getName(), bottomLeft);

        buildFirstRank(bottomLeft, 'a', 2, dimX);

        buildRemainingRanks(bottomLeft, dimX, dimY, 1);
    }
    // builds first rank assuming the first node in the rank has already been created
    public void buildFirstRank(BoardNode start, char rank, int fileNum, int maxFile) {
        if(maxFile - fileNum < 0) return;

        BoardNode next = new BoardNode("" + rank + fileNum);
        start.east = next;
        next.west = start;
        board.put(next.getName(), next);

        buildFirstRank(start.east, rank, fileNum + 1, maxFile);
    }
    // builds the rest of the board on top of the first rank
    public void buildRemainingRanks(BoardNode start, int dimX, int dimY, int currentRank) {
        if(dimY - currentRank - 1 < 0) return;

        char rankName = (char)(A_VAL + currentRank);

        BoardNode firstInRank =  new BoardNode(rankName + "1");
        start.north = firstInRank;
        firstInRank.south = start;
        board.put(firstInRank.getName(), firstInRank);

        buildAndConnectRank(firstInRank, start, rankName, 2, dimX);

        buildRemainingRanks(firstInRank, dimX, dimY, currentRank + 1);
    }
    // builds an individual rank and properly connects it with the rank below
    public void buildAndConnectRank(BoardNode startAbove, BoardNode startBelow, char rank, int fileNum, int maxFile) {
        if(maxFile - fileNum < 0) return;

        BoardNode next = new BoardNode("" + rank + fileNum);
        startAbove.east = next;
        next.west = startAbove;
        next.south = startBelow.east;
        startBelow.east.north = next;
        board.put(next.getName(), next);

        buildAndConnectRank(next, startBelow.east, rank, fileNum + 1, maxFile);
    }

    // finds the tile with the given name - use hash map
    public BoardNode findNode(String name) {
        return board.get(name);
    }
    // finds a piece if it exists on a tile with the given name, if it exists
    // due to the volatility of a piece's position, we can't store them in an ordered collection
    public ChessPiece findPiece(String name) {
        for(ChessPiece piece : allPieces) {
            if(piece.currentSquare.equals(name)) return piece;
        }
        return null;
    }

    public void addPieceToBoard(ChessPiece piece, String spotName) {
        addPieceToBoard(piece, findNode(spotName));
    }
    public void addPieceToBoard(ChessPiece piece, BoardNode spot) {
        piece.setStartingLocation(spot);
        allPieces.add(piece);
        
        if(piece.getSide()) {
            whitePieces.add(piece);
            if(piece.getName().equals("King")) whiteKing = piece;
        }else {
            blackPieces.add(piece);
            if(piece.getName().equals("King")) blackKing = piece;
        }
    }

    // recursive method to clear all attacks forom the board nodes
    private void clearAttacks(BoardNode start) {
        if(start == null) return;
        start.clearAttacks();

        // traverse the board recursively
        if(start.south == null) {
            clearAttacks(start.north);
            clearAttacks(start.east);
        }else {
            clearAttacks(start.north);
        }
    }

    // generates moves and makes sure they do not cause the player to lose
    public LinkedList<Move> generateSanitizedMovesForSide(boolean side) {
        LinkedList<ChessPiece> enemyPieces = side ? blackPieces : whitePieces;
        LinkedList<ChessPiece> myPieces = side ? whitePieces : blackPieces;
        ChessPiece myKing = side ? whiteKing : blackKing;

        LinkedList<Move> sanitizedList = new LinkedList<>();

        // we need to check each possible move and see whether or not it will land us in check, which would cause a player to walk into checkmate
        for(ChessPiece myPiece : myPieces) {
            myPiece.generateMoves(this);
            LinkedList<Move> possibleMoves = myPiece.getMoves();

            for(Move m : possibleMoves) {
                boolean checkPresent = false;
                Iterator<ChessPiece> itr = enemyPieces.iterator();

                // make move to test this new board state
                clearAttacks(bottomLeft);
                m.move(this);

                // check all enemy moves to see if they cause a check
                while(itr.hasNext() && !checkPresent) {
                    itr.next().generateMoves(this);
                    checkPresent = findNode(myKing.currentSquare).isAttacked(side);
                }

                // undo the move once we're done with it
                m.undoMove(this);

                // properly handle the move if it is a castle
                if(m instanceof Castle && !checkPresent) {
                    clearAttacks(bottomLeft);
                    itr = enemyPieces.iterator();

                    while(itr.hasNext()) {
                        itr.next().generateMoves(this);
                    }

                    checkPresent = findNode(((Castle)m).getRookDest()).isAttacked(side) || findNode(m.originalPosition).isAttacked(side); // negated because if it is valid it will be false
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
        ArrayList<String> ranks = new ArrayList<>();

        BoardNode currentRank = bottomLeft;
        while(currentRank != null) {
            ranks.add(getStringFromRank(currentRank));
            currentRank = currentRank.north;
        }

        String str = "";

        for(int i = ranks.size() - 1; i >= 0; i--) {
            str += ranks.get(i) + "\n";
        }

        return str;
    }
    // recursively add all strings on a given rank
    private String getStringFromRank(BoardNode current) {
        if(current == null) {return "";}
        return current + " " + getStringFromRank(current.east);
    }
}
