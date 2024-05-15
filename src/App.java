import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) throws Exception {
        Board b = new Board();
        System.out.println(b);

        ChessPiece whiteKing = new King(b, true);
        ChessPiece blackKing = new King(b, false);

        //ChessPiece allyPawn = new Pawn(b, true);
        ChessPiece allyRook = new ChessPiece(b, "Rook", true, 5, ChessPiece.ROOK_MOVES);
        ChessPiece enemyRook =  new ChessPiece(b, "Rook", false, 5, ChessPiece.ROOK_MOVES);

        b.addPieceToBoard(whiteKing, "a4");
        b.addPieceToBoard(blackKing, "h1");
        //b.addPieceToBoard(allyPawn, "b3");
        b.addPieceToBoard(enemyRook, "c5");
        b.addPieceToBoard(allyRook, "a8");

        System.out.println(b.generateSanitizedMovesForSide(true));

        //King k = new King(b, true);
        //ChessPiece enemy = new ChessPiece(b, "Rook", false, 5, ChessPiece.ROOK_MOVES);

        //b.addPieceToBoard(k, "a5");
        //b.addPieceToBoard(enemy, "d5");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}
