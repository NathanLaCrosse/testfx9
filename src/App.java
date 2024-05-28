import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// TODO: I FORGOT ABOUT PAWN PROMOTIONS LMAO

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
        gui.startNewGame(new RandBot(), new Player(false));

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }
}