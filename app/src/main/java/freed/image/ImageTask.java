package freed.image;

/**
 * Created by KillerInk on 13.11.2017.
 */

public abstract class ImageTask implements Runnable {
    public abstract boolean process();
    private Thread currentThread;
    public Thread getThread()
    {
        return currentThread;
    }

    @Override
    public void run() {
        currentThread = Thread.currentThread();
        if (currentThread.interrupted()) {
            return;
        }
        process();
    }
}
