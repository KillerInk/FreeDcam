package freed.image;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ImageManager {

    private static ImageManager imageManager = new ImageManager();

    public static ImageManager getInstance()
    {
        return imageManager;
    }

    private final String TAG = ImageManager.class.getSimpleName();
    private final ImageSaveManager imageSaveManager;
    private final ImageLoadManager imageLoadManager;

    private final int KEEP_ALIVE_TIME = 500;

    private ImageManager()
    {
        imageSaveManager = new ImageSaveManager();
        imageLoadManager = new ImageLoadManager();
    }

    public static void putImageSaveTask(ImageTask task)
    {
        imageManager.imageSaveManager.imageSaveExecutor.execute(task);
    }

    public static void cancelImageSaveTasks()
    {
        synchronized (imageManager) {
            imageManager.imageSaveManager.cancel();
        }
    }

    public static void putImageLoadTask(ImageTask runnable)
    {
        imageManager.imageLoadManager.imageLoadExecutor.execute(runnable);
    }

    public static void cancelImageLoadTasks()
    {
        synchronized (imageManager) {
            imageManager.imageLoadManager.cancel();
        }
    }

    public static void removeImageLoadTask(ImageTask task)
    {
        imageManager.imageLoadManager.removeTaskFromQueue(task);
    }

    private class ImageSaveManager {

        private final BlockingQueue<Runnable> imagesToSaveQueue;
        private final ThreadPoolExecutor imageSaveExecutor;

        private ImageSaveManager() {
            int coresize = Runtime.getRuntime().availableProcessors()/2;
            if (coresize>4)
                coresize = 4;
            if (coresize == 0)
                coresize = 1;
            imagesToSaveQueue = new ArrayBlockingQueue<Runnable>(coresize);

            imageSaveExecutor = new ThreadPoolExecutor(
                    coresize,       // Initial pool size
                    coresize,       // Max pool size
                    KEEP_ALIVE_TIME,
                    TimeUnit.MILLISECONDS,
                    imagesToSaveQueue);
            //handel case that queue is full, and wait till its free
            imageSaveExecutor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    try {
                        executor.getQueue().put(r);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
            if (task.getThread() != null && !task.getThread().isInterrupted())
                task.getThread().interrupt();
            imagesToLoadQueue.remove(task);
        }
    }

}
