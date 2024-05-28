// this thread allows for the game controller to be working at the same time
// as the GUI
public class VisualGameControllerThread extends Thread {
    private VisualChessGame chessGUI;
    private boolean complete = false;
    
    public VisualGameControllerThread(VisualChessGame chessGUI) {
        this.chessGUI = chessGUI;
    }

    public boolean finished() {
        return complete;
    }

    // use game controller to run game until it ends
    @Override
    public void run() {

        // wait a little time in case the move is instantly found (some other stuff needs to get set up first)
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // runs until checkmate
        while(!chessGUI.getGameController().nextTurn()) {
            chessGUI.updateSprites();
        }

        complete = true;
    }
}
