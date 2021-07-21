package freed.cam.histogram;

import com.google.android.renderscript.Toolkit;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import freed.utils.Log;

public class HistogramProcessor {

    private static final String TAG = HistogramProcessor.class.getSimpleName();
    private HistogramController histogramController;

    private final int KEEP_ALIVE_TIME = 500;
    private final BlockingQueue<Runnable> histogramProcessingQueue;
    private final ThreadPoolExecutor histogramProcessingExecutor;

    public HistogramProcessor(HistogramController histogramController)
    {
        this.histogramController = histogramController;
        int coresize = 1;
        histogramProcessingQueue = new ArrayBlockingQueue<>(3);

        histogramProcessingExecutor = new ThreadPoolExecutor(
                coresize,       // Initial pool size
                coresize,       // Max pool size
                KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS,
                histogramProcessingQueue);
        //handel case that queue is full, and wait till its free
        histogramProcessingExecutor.setRejectedExecutionHandler((r, executor) -> {
            Log.d(TAG, "imageSave Queue full");
            try {
                executor.getQueue().take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void add(final byte[] bytes,int width, int height)
    {
        histogramProcessingExecutor.execute(new HistogramCreatorRunner(bytes,width,height));
    }


    class HistogramCreatorRunner implements Runnable {
        private byte[] bytes;
        private int width;
        private int height;
        HistogramCreatorRunner(byte[] bytes, int width, int height)
        {
            this.bytes = bytes;
            this.width = width;
            this.height = height;
        }

        @Override
        public void run() {
            int[] histo = Toolkit.INSTANCE.histogram(bytes,4, width,height);
            histogramController.onHistogramChanged(histo);
        }
    }
}
