package freed.utils;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

public class BackgroundHandlerThread
{
    private final String TAG = BackgroundHandlerThread.class.getSimpleName();
    private HandlerThread mBackgroundThread;
    protected Handler mBackgroundHandler;
    private final String name;

    public BackgroundHandlerThread(String name)
    {
        this.name = name;
    }

    public void create()
    {
        Log.d(TAG,"startBackgroundThread" + name);
        mBackgroundThread = new HandlerThread(name);
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    public void destroy()
    {
        Log.d(TAG,"stopBackgroundThread" + name);
        if(mBackgroundThread == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBackgroundThread.quitSafely();
        }
        else
            mBackgroundThread.quit();

        mBackgroundThread = null;
        mBackgroundHandler = null;
    }

    public HandlerThread getThread()
    {
        return mBackgroundThread;
    }

    public void execute(Runnable runnable)
    {
        mBackgroundHandler.post(runnable);
    }
    public void executeDelayed(Runnable runnable, long delay)
    {
        mBackgroundHandler.postDelayed(runnable, delay);
    }
}