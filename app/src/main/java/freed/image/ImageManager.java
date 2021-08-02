package freed.image;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import freed.utils.Log;

public class ImageManager {
    
    private final String TAG = ImageManager.class.getSimpleName();
    private final ImageSaveManager imageSaveManager;
    private final ImageLoadManager imageLoadManager;

    private final int KEEP_ALIVE_TIME = 500;

    public ImageManager()
    {
        imageSaveManager = new ImageSaveManager();
        imageLoadManager = new ImageLoadManager();
    }

    public void putImageSaveTask(ImageTask task)
    {
        imageSaveManager.imageSaveExecutor.execute(task);
    }

    public void cancelImageSaveTasks()
    {
        synchronized (imageSaveManager) {
            imageSaveManager.cancel();
        }
    }

    public void putImageLoadTask(ImageTask runnable)
    {
        imageLoadManager.imageLoadExecutor.execute(runnable);
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

    private class ImageSaveManager {

        private final BlockingQueue<Runnable> imagesToSaveQueue;
        private final ThreadPoolExecutor imageSaveExecutor;

        private ImageSaveManager() {

            int coresize = Runtime.getRuntime().availableProcessors()/2;
            Log.d(TAG,"Cores Avail: "+Runtime.getRuntime().availableProcessors() + " CoreSize" + coresize);
            if (coresize>4)
                coresize = 4;
            if (coresize == 0)
                coresize = 1;
            imagesToSaveQueue = new ArrayBlockingQueue<>(coresize);

            imageSaveExecutor = new ThreadPoolExecutor(
                    coresize,       // Initial pool size
                    coresize,       // Max pool size
                    KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    imagesToSaveQueue);
            //handel case that queue is full, and wait till its free
            imageSaveExecutor.setRejectedExecutionHandler((r, executor) -> {
                Log.d(TAG, "imageSave Queue full");
                try {
                    executor.getQueue().put(r);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        public void cancel()
        {
            ImageSaveTask tasks [] = new ImageSaveTask[imagesToSaveQueue.size()];
            imagesToSaveQueue.toArray(tasks);
            for (ImageSaveTask task : tasks)
            {
                Thread thread = task.getThread();
                if (thread != null)
                    thread.interrupt();
            }
            imagesToSaveQueue.clear();
        }
    }

    private class ImageLoadManager
    {
        private final BlockingQueue<Runnable> imagesToLoadQueue;
        private final ThreadPoolExecutor imageLoadExecutor;
        private ImageLoadManager()
        {
            int coreSize = Runtime.getRuntime().availableProcessors();
            imagesToLoadQueue = new LinkedBlockingDeque<>();
            imageLoadExecutor = new ThreadPoolExecutor(
                    coreSize,       // Initial pool size
                    coreSize,       // Max pool size
                    KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    imagesToLoadQueue);
        }

        public void cancel()
        {
            ImageTask tasks [] = new ImageTask[imagesToLoadQueue.size()];
            imagesToLoadQueue.toArray(tasks);
            for (ImageTask task : tasks)
            {
                Thread thread = task.getThread();
                if (thread != null)
                    thread.interrupt();
            }
            imagesToLoadQueue.clear();
        }

        public void removeTaskFromQueue(ImageTask task)
        {
            imagesToLoadQueue.remove(task);
            try {
                if (task != null && task.getThread() != null && !task.getThread().isInterrupted())
                    task.getThread().interrupt();
            }
            catch (NullPointerException ex)
            {
                Log.WriteEx(ex);
            }

        }
    }

}
