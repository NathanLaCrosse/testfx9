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
        

        // runs until checkmate
        while(!chessGUI.getGameController().nextTurn()) {
            chessGUI.updateSprites();
        }

        chessGUI.startNewGame(new RandBot(true), new StageOneBot(false));

        complete = true;
    }
}
