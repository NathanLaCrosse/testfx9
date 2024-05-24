// this thread allows for the game controller to be working at the same time
// as the GUI
public class VisualGameControllerThread extends Thread {
    private GameController gc;
    private App app;
    
    public VisualGameControllerThread(GameController gc) {
        this.gc = gc;
    }

    public void setApp(App app) {
        this.app = app;
    }

    // use game controller to run game until it ends
    @Override
    public void run() {

        gc.nextTurn();
        app.updateSprites();

    }
}
