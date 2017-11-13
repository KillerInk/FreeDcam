package freed.utils;


import android.os.AsyncTask;

import java.io.InterruptedIOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ImageSaveManager {

    private static ImageSaveManager imageSaveManager = new ImageSaveManager();

    public static ImageSaveManager getInstance()
    {
        return imageSaveManager;
    }
    private LinkedBlockingQueue<Runnable> imageToSave;
    private boolean cancleThread = false;
    private ImageSaveThread queueObserver1;
    private ImageSaveThread queueObserver2;
    private final String TAG = ImageSaveManager.class.getSimpleName();

    private ImageSaveManager()
    {
        imageToSave = new LinkedBlockingQueue<>(2);
        cancleThread = false;
        queueObserver1 = new ImageSaveThread();
        queueObserver1.setName("QueueObserver1");
        queueObserver1.start();
        /*queueObserver2 = new ImageSaveThread();
        queueObserver2.setName("QueueObserver2");
        queueObserver2.start();*/
    }

    public void put(Runnable runnable)
    {
        Log.d(TAG,"Put Runnable");
        try {
            imageToSave.put(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void cancel()
    {
        Log.d(TAG,"cancel");
        cancleThread = true;
        if (!queueObserver1.isInterrupted())
        {
            queueObserver1.interrupt();
        }
        if (queueObserver2 != null && !queueObserver2.isInterrupted())
        {
            queueObserver2.interrupt();
        }
        imageToSave.clear();
    }

    private class ImageSaveThread extends Thread
    {
        private Runnable runnable;
        @Override
        public void run() {
            Log.d(TAG,"run");
            try {
                while (!cancleThread)
                {
                    Log.d(TAG,"Wait for work");
                    runnable = imageToSave.take();
                    if (runnable != null) {
                        Log.d(TAG, "Excute Runnable");
                        runnable.run();
                    }
                    else
                        Log.d(TAG,"Runnable is null");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }
        }
    }
}
