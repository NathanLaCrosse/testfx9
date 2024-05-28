// this class sets up the visual and functional elements of a chess game that can be added
// to any JavaFX program

import java.util.HashMap;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VisualChessGame {
    private VBox gameBox; // entire game is inside this vbox

    private GameController gc;
    private VisualGameControllerThread gct;
    private HashMap<Pos, BoardTile> tileMap;

    public VisualChessGame() {
        gc = null;
        gct = null;

        tileMap = new HashMap<>();

        gameBox = new VBox();
        for(int i = 0; i < 8; i++) {
            HBox rank = new HBox();
            for(int k = 0; k < 8; k++) {
                // for alternating black / white color
                boolean tileColor = (i + k) % 2 == 0;

                Pos p = new Pos(i, k);
                BoardTile tile = new BoardTile(this, tileColor, p);
                tileMap.put(p, tile);

                rank.getChildren().add(tile.getImageContainer());
            }
            gameBox.getChildren().add(rank);
        }
    }

    public VBox getGameBox() {
        return gameBox;
    }
    // protected as a chess bot or other party doesn't need access to the game controller :)
    protected GameController getGameController() {
        return gc;
    }

    // starts a chess game and finishes once it's done
    public void startNewGame(Entity player1, Entity player2) {
        gc = new GameController(player1, player2);
        gct = new VisualGameControllerThread(this);

        // if any entities are players, set up their threads
        if(player1 instanceof Player) {
            ((Player)player1).setThread(gct);
        }
        if(player2 instanceof Player) {
            ((Player)player2).setThread(gct);
        }

        // begin the game
        gct.start();
        updateSprites();
        
        // at the end of the game, get rid of game controller
        // gc = null;
        // gct = null;
    }

    // change the graphics of each tile if pieces have been moved    
    public void updateSprites() {
        for(BoardTile bt : tileMap.values()) {
            bt.determinePieceSprite();
            bt.disableMovementDot();
        }
    }

    public void displayMovementDotsForPiece(ChessPiece piece) {
        for(Move m : piece.getMoves().values()) {
            tileMap.get(m.destination).enableMovementDot(piece);
        }
    }
}   

// this class manages the 2 image views needed to show tiles and the pieces on them
class BoardTile {
    private VisualChessGame chessGUI;

    private VBox imageContainer;
    private ImageView tileDraw;
    private ImageView pieceDraw; // on top of tile
    private ImageView moveDot; // visual to show that a piece could move here

    private ChessPiece selectedKing; // selected piece could move to this tile

    private Pos positionOnBoard;

    public BoardTile(VisualChessGame chessGUI, boolean tileColor, Pos positionOnBoard) {
        imageContainer = new VBox();
        imageContainer.setSpacing(-64); // negative spacing so the piece is on top of the tile (64 pixels for tile sprite)

        tileDraw = new ImageView();
        pieceDraw = new ImageView();
        moveDot = new ImageView();

        imageContainer.getChildren().addAll(tileDraw, pieceDraw, moveDot);

        if(tileColor) {
            tileDraw.setImage(new Image(this.getClass().getResourceAsStream("Sprites/LightSquare.png")));
        }else {
            tileDraw.setImage(new Image(this.getClass().getResourceAsStream("Sprites/DarkSquare.png")));
        }

        this.positionOnBoard = positionOnBoard;
        this.chessGUI = chessGUI;
        selectedKing = null;

        determinePieceSprite();

        // adds mouse functionality to the board
        imageContainer.setOnMousePressed(new EventHandler<Event>() {

            @Override
            public void handle(Event arg0) {
                if(chessGUI.getGameController() == null) return;

                GameController gc = chessGUI.getGameController();
                ChessPiece piece = gc.getGameBoard().retrievePiece(positionOnBoard);

                if(gc.currentPlayer() instanceof Player) {
                    Player p = (Player)gc.currentPlayer();
                    
                    if(piece == null || piece.getSide() != p.getSide() // select a move
                            || /*Exception for castles*/(selectedKing != null && Math.abs(selectedKing.currentPos.second() - positionOnBoard.second()) == 2)) {
                        p.selectIfCanPlay(positionOnBoard);
                        chessGUI.updateSprites();
                    }else if(piece != null && piece.getSide() == p.getSide()) { // select a piece 
                        p.setPiece(piece);
                        chessGUI.updateSprites();
                        chessGUI.displayMovementDotsForPiece(piece);
                    }
                }
            }
            
        });
    }

    // displays a movement dot to show a certain piece could move here
    // input is the piece that is enabling the dots
    public void enableMovementDot(ChessPiece piece) {
        moveDot.setImage(new Image(this.getClass().getResourceAsStream("Sprites/SelectionDot.png")));
        if(piece instanceof King) selectedKing = piece;
    }
    // stops displaying the movement dot
    public void disableMovementDot() {
        moveDot.setImage(new Image(this.getClass().getResourceAsStream("Sprites/Blank.png")));
        selectedKing = null;
    }

    public void determinePieceSprite() {
        GameController gc = chessGUI.getGameController();
        if(gc == null) {
            pieceDraw.setImage(new Image(this.getClass().getResourceAsStream("Sprites/Blank.png")));
            return;
        }

        ChessPiece pieceOnTile = gc.getGameBoard().retrievePiece(positionOnBoard);

        String nameOfSprite = "Blank";
        if(pieceOnTile != null) {
            nameOfSprite = (pieceOnTile.getSide() ? "Light" : "Dark") + pieceOnTile.getName(); // assumes the piece name lines up with the file names (rook should be "Rook")
        }

        Image img = new Image(this.getClass().getResourceAsStream("Sprites/"+nameOfSprite+".png"));

        pieceDraw.setImage(img);
    }

    public VBox getImageContainer() {
        return imageContainer;
    }
    
}
