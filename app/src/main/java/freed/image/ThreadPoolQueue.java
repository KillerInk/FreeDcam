package freed.image;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import freed.utils.Log;

public class ThreadPoolQueue {
    private final int KEEP_ALIVE_TIME = 500;
    private BlockingQueue<Runnable> executerQueue;
    private ThreadPoolExecutor executer;


    public void create(int coreSize, int queueSize)
    {
        if (queueSize > 0)
            executerQueue = new LinkedBlockingDeque<>(queueSize);
        else
            executerQueue = new LinkedBlockingDeque<>();
        executer = new ThreadPoolExecutor(
                coreSize,       // Initial pool size
                coreSize,       // Max pool size
                KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                executerQueue);
    }

    public void create()
    {
        int coreSize = Runtime.getRuntime().availableProcessors();
        create(coreSize,0);
    }

    public void create(RejectedExecutionHandler rejectedExecutionHandler)
    {
        create();
        executer.setRejectedExecutionHandler(rejectedExecutionHandler);
    }

    public void create(int coreSize,RejectedExecutionHandler rejectedExecutionHandler)
    {
        create(coreSize,coreSize);
        executer.setRejectedExecutionHandler(rejectedExecutionHandler);
    }

    public void setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler)
    {
        executer.setRejectedExecutionHandler(rejectedExecutionHandler);
    }

    public RejectedExecutionHandler getRejectedExecutionHandler()
    {
        return executer.getRejectedExecutionHandler();
    }

    public void cancel()
    {
        ImageTask tasks [] = new ImageTask[executerQueue.size()];
        executerQueue.toArray(tasks);
        for (ImageTask task : tasks)
        {
            Thread thread = task.getThread();
            if (thread != null)
                thread.interrupt();
        }
        executerQueue.clear();
    }

    public void removeTaskFromQueue(ImageTask task)
    {
        executerQueue.remove(task);
        try {
            if (task != null && task.getThread() != null && !task.getThread().isInterrupted())
                task.getThread().interrupt();
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    public void addTask(ImageTask task)
    {
        executer.execute(task);
    }

    public int remainingCapacity() {
        return executerQueue.remainingCapacity();
    }
}
