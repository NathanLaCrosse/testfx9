import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) throws Exception {

        GameController gc = new GameController(new RandBot(), new RandBot());

        System.out.println("Before moves: ");
        System.out.println(gc);

        gc.nextTurn();
        gc.nextTurn();

        System.out.println("After Moves: ");
        System.out.println(gc);

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
