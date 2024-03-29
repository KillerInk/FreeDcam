package freed.cam.apis.camera2.modules.capture;

import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import freed.image.ImageTask;
import freed.utils.BackgroundHandlerThread;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class AbstractImageCapture implements ImageCaptureInterface {

    private final String TAG = AbstractImageCapture.class.getSimpleName();
    protected final int max_images;
    private final ImageReader imageReader;
    private final BackgroundHandlerThread backgroundHandlerThread;
    private boolean setToPreview = false;
    protected Image image;
    protected CaptureResult result;
    protected ImageTask task;


    public AbstractImageCapture(Size size, int format, boolean setToPreview, int max_images)
    {
        backgroundHandlerThread = new BackgroundHandlerThread("AbstractImageCapture");
        backgroundHandlerThread.create();
        this.setToPreview = setToPreview;
        this.max_images = max_images;
        imageReader = ImageReader.newInstance(size.getWidth(),size.getHeight(),format,max_images);
        imageReader.setOnImageAvailableListener(this,backgroundHandlerThread.getBackgroundHandler());
    }

    public void resetTask()
    {
        task = null;
    }

    @Override
    public ImageTask getSaveTask() {
        synchronized (this) {
            if (task == null) {
                Log.d(TAG, "Task is null wait");
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return task;
    }

    @Override
    public Surface getSurface()
    {
        return imageReader.getSurface();
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Log.d(TAG,"onImageAvailable");
        synchronized (this)
        {
            image = reader.acquireLatestImage();
            createTask();
            this.notifyAll();
            Log.d(TAG, "Add new img to queue");
        }
    }

    protected  abstract void createTask();

    @Override
    public void setCaptureResult(CaptureResult  captureResult)
    {
        synchronized (this)
        {
            Log.d(TAG,"setCaptureResult");
            result = captureResult;
            createTask();
            this.notifyAll();
        }
    }

    @Override
    public boolean setToPreview() {
        return setToPreview;
    }

    @Override
    public void release()
    {
        synchronized (this)
        {
            this.notifyAll();
        }
        Log.d(TAG,"release");
        if (imageReader != null)
            imageReader.close();
        if (image != null)
            image.close();
        backgroundHandlerThread.destroy();

    }


}
