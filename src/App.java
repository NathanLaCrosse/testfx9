import java.util.HashMap;
import java.util.LinkedList;
import java.lang.Thread;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// TODO: Build GUI for program

public class App extends Application {
    public static Application a;
    private static GameController gc;
    private static VisualGameControllerThread gct;
    private static HashMap<Pos, BoardTile> tileMap;

    public static void main(String[] args) throws Exception {
        Player p1 = new Player(true);

        gc = new GameController(p1, new RandBot());

        gct = new VisualGameControllerThread(gc);
        p1.setThread(gct);
        gct.start();

        launch(args);
    }

    // ran at the start of the program 
    @Override
    public void start(Stage primaryStage) throws Exception {
        a = this;
        gct.setApp(this);

        StackPane root = new StackPane();

        root.getChildren().add(buildChessWindow());
        updateSprites();

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    // returns the entire chess window - stored in vbox
    public VBox buildChessWindow() {
        tileMap = new HashMap<>();

        VBox ranks = new VBox();
        for(int i = 0; i < 8; i++) {
            HBox rank = new HBox();
            for(int k = 0; k < 8; k++) {
                // for alternating black / white color
                boolean tileColor = (i + k) % 2 == 0;

                Pos p = new Pos(i, k);
                BoardTile tile = new BoardTile(this, tileColor, p, gc);
                tileMap.put(p, tile);

                rank.getChildren().add(tile.getImageContainer());
            }
            ranks.getChildren().add(rank);
        }
        return ranks;
    }

    public void updateSprites() {
        for(BoardTile bt : tileMap.values()) {
            bt.determinePieceSprite();
            bt.disableMovementDot();
        }
    }

    public void displayMovementDotsForPiece(ChessPiece piece) {
        for(Move m : piece.getMoves().values()) {
            tileMap.get(m.destination).enableMovementDot();
        }
    }
}

// this class manages the 2 image views needed to show tiles and the pieces on them
class BoardTile {
    private VBox imageContainer;
    private ImageView tileDraw;
    private ImageView pieceDraw; // on top of tile
    private ImageView moveDot; // visual to show that a piece could move here

    private Pos positionOnBoard;
    private GameController gc;

    public BoardTile(App app, boolean tileColor, Pos positionOnBoard, GameController gc) {
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
        this.gc = gc;

        determinePieceSprite();

        // adds mouse functionality to the board
        imageContainer.setOnMousePressed(new EventHandler<Event>() {

            @Override
            public void handle(Event arg0) {
                ChessPiece piece = gc.getGameBoard().retrievePiece(positionOnBoard);

                if(gc.currentPlayer() instanceof Player) {
                    Player p = (Player)gc.currentPlayer();
                    
                    if(piece != null && piece.getSide() == p.getSide()) { // select a piece
                        p.setPiece(piece);
                        app.updateSprites();
                        app.displayMovementDotsForPiece(piece);
                    }else if(piece == null || piece.getSide() != p.getSide()) { // select a move
                        p.selectIfCanPlay(positionOnBoard);
                        app.updateSprites();
                    }
                }
            }
            
        });
    }

    // displays a movement dot to show a certain piece could move here
    public void enableMovementDot() {
        moveDot.setImage(new Image(this.getClass().getResourceAsStream("Sprites/SelectionDot.png")));
    }
    // stops displaying the movement dot
    public void disableMovementDot() {
        moveDot.setImage(new Image(this.getClass().getResourceAsStream("Sprites/Blank.png")));
    }

    public void determinePieceSprite() {
        ChessPiece pieceOnTile = gc.getGameBoard().retrievePiece(positionOnBoard);

        String nameOfSprite = "Blank";
        if(pieceOnTile != null) {
            nameOfSprite = (pieceOnTile.getSide() ? "Light" : "Dark") + pieceOnTile.getName(); // assumes the piece name lines up with the file names (rook should be "Rook")
        }

        Image img = new Image(App.a.getClass().getResourceAsStream("Sprites/"+nameOfSprite+".png"));

        pieceDraw.setImage(img);
    }

    public VBox getImageContainer() {
        return imageContainer;
    }
    
}

// this class acts as a mediator between the board tiles and the game controller
// allows the end user to play the game
class Player extends Entity {
    private ChessPiece selectedPiece = null;
    private Move selectedMove = null;
    private boolean side;

    private Thread myThread = null;

    public Player(boolean side) {
        this.side = side;
    }

    public boolean getSide() {
        return side;
    }

    @Override
    public Move selectMoveToPlay(LinkedList<Move> validMoves, LinkedList<ChessPiece> pieces) {

        // wait until move has been selected (sleep thread we are on)
        while(selectedMove == null) {
            try {
                myThread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.out.println(selectedMove);

        return selectedMove; // dummy value for now
    }

    // selects a given move to play if the selected move is a valid move
    // of the previously selected piece
    public void selectIfCanPlay(Pos pos) {
        if(selectedPiece == null) return;

        if(selectedPiece.attacksSquare(pos)) {
            selectedMove = selectedPiece.getMoves().get(pos);
        }
    }

    public void setPiece(ChessPiece piece) {
        this.selectedPiece = piece;
    }

    // sets the thread so we can pause it while waiting
    public void setThread(Thread t) {
        this.myThread = t;
    }
    
}