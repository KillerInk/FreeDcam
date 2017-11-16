package freed.utils;


import android.os.AsyncTask;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import freed.jni.RawToDng;

public class ImageSaveManager {

    private static ImageSaveManager imageSaveManager = new ImageSaveManager();

    public static ImageSaveManager getInstance()
    {
        return imageSaveManager;
    }
    private LinkedBlockingQueue<SaveTask> imageToSave;
    private boolean cancleThread = false;
    private final String TAG = ImageSaveManager.class.getSimpleName();
    private List<ImageSaveThread> imageSaveManagerList;
    private final int MAXTHREADS = 4;

    private ImageSaveManager()
    {
        imageSaveManagerList = new ArrayList<>();
        imageToSave = new LinkedBlockingQueue<>(MAXTHREADS);
        cancleThread = false;
        for (int i = 0; i < MAXTHREADS; i++)
        {
            ImageSaveThread queueObserver = new ImageSaveThread();
            imageSaveManagerList.add(queueObserver);
            queueObserver.setName("QueueObserver" + i);
            queueObserver.start();
        }
    }

    public void put(SaveTask runnable)
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
        for (int i = 0; i< MAXTHREADS; i++)
        {
            if (!imageSaveManagerList.get(i).isInterrupted())
                imageSaveManagerList.get(i).interrupt();
            imageSaveManagerList.remove(i);
        }
        imageToSave.clear();
    }

    private class ImageSaveThread extends Thread
    {
        private SaveTask runnable;

        @Override
        public void run() {
            Log.d(TAG,"run");

                while (!cancleThread)
                {
                    try {
                        Log.d(TAG,"Wait for work");
                        runnable = imageToSave.take();

                        if (runnable != null) {
                            Log.d(TAG, "Excute Runnable");
                            runnable.save();
                        }
                        else
                            Log.d(TAG,"Runnable is null");
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
}
