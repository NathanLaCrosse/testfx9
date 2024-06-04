import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// TODO: implement choosing which piece you want to promote to
// TODO: Draw after repeating the same board position 3 times (do we really need this for a chess bot program?!?)
// TODO: make sure the board state isn't altered by a entity

public class App extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    // ran at the start of the program 
    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();

        VisualChessGame gui = new VisualChessGame();
        root.getChildren().add(gui.getGameBox());
        gui.startNewGame(new RandBot(true), new StageOneBot(false));

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}