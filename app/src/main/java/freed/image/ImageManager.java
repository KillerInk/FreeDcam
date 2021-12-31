package freed.image;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import freed.utils.Log;

public class ImageManager {
    
    private final String TAG = ImageManager.class.getSimpleName();
    private final ThreadPoolQueue imageSaveManager;
    private final ThreadPoolQueue imageLoadManager;

    public ImageManager()
    {
        imageSaveManager = new ThreadPoolQueue();
        imageSaveManager.create(6,10);
        imageSaveManager.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                Log.d(TAG, "imageSave Queue full");
                try {
                    executor.getQueue().put(r);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        imageLoadManager = new ThreadPoolQueue();
        imageLoadManager.create();
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler)
    {
        imageSaveManager.setRejectedExecutionHandler(rejectedExecutionHandler);
    }

    public RejectedExecutionHandler getRejectedExecutionHandler()
    {
        return imageSaveManager.getRejectedExecutionHandler();
    }

    public int getImageSaveManagerRemainingCapacity()
    {
        return imageSaveManager.remainingCapacity();
    }

    public void putImageSaveTask(ImageTask task)
    {
        imageSaveManager.addTask(task);
    }

    public void cancelImageSaveTasks()
    {
        synchronized (imageSaveManager) {
            imageSaveManager.cancel();
        }
    }

    public void putImageLoadTask(ImageTask runnable)
    {
        imageLoadManager.addTask(runnable);
    }

    public void cancelImageLoadTasks()
    {
        synchronized (imageLoadManager) {
            imageLoadManager.cancel();
        }
    }

    public void removeImageLoadTask(ImageTask task)
    {
        imageLoadManager.removeTaskFromQueue(task);
    }


}
