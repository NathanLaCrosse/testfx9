// this thread allows for the game controller to be working at the same time
// as the GUI

import javafx.application.Platform;

public class VisualGameControllerThread extends Thread {
    private VisualChessGame chessGUI;
    
    public VisualGameControllerThread(VisualChessGame chessGUI) {
        this.chessGUI = chessGUI;
    }

    // use game controller to run game until it ends
    @Override
    public void run() {
        Platform.runLater(() ->{
            boolean end = chessGUI.getGameController().nextTurn();
            chessGUI.updateSprites();

            if(!end) {
                VisualGameControllerThread vgct = new VisualGameControllerThread(chessGUI);
                vgct.start();
            }
        });
    }
}
