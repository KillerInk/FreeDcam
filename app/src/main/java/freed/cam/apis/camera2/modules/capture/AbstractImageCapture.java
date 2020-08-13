package freed.cam.apis.camera2.modules.capture;

import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class AbstractImageCapture implements ImageCaptureInterface {

    private final String TAG = AbstractImageCapture.class.getSimpleName();
    private final int MAX_IMAGES = 5;
    private final ImageReader imageReader;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler;
    private boolean setToPreview = false;
    protected final BlockingQueue<Image> imageBlockingQueue = new ArrayBlockingQueue<>(MAX_IMAGES-1);
    protected final BlockingQueue<CaptureResult> captureResultBlockingQueue = new ArrayBlockingQueue<>(MAX_IMAGES-1);
    private final List<Image> imagespolled = new ArrayList<>();


    public AbstractImageCapture(Size size, int format, boolean setToPreview)
    {
        startBackgroundThread();
        this.setToPreview = setToPreview;
        imageReader = ImageReader.newInstance(size.getWidth(),size.getHeight(),format,MAX_IMAGES);
        imageReader.setOnImageAvailableListener(this,mBackgroundHandler);
    }

    @Override
    public CaptureResult pollCaptureResult() {
        return captureResultBlockingQueue.poll();
    }

    @Override
    public CaptureResult peekCaptureResult() {
        return captureResultBlockingQueue.peek();
    }

    @Override
    public Image pollImage() {
        Image img = imageBlockingQueue.poll();
        imagespolled.add(img);
        return img;
    }

    @Override
    public void releaseImage(Image img) {
        img.close();
        imagespolled.remove(img);
    }

    @Override
    public Image peekImage() {
        return imageBlockingQueue.peek();
    }

    @Override
    public Surface getSurface()
    {
        return imageReader.getSurface();
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        //Log.d(TAG, "onImageAvailable imageblockingqueue:" + (imageBlockingQueue.size() - imageBlockingQueue.remainingCapacity()) + "/"+ imageBlockingQueue.size() + " image polled size: " + imagespolled.size());
        while (imageBlockingQueue.remainingCapacity() == imagespolled.size()+1) {
            Log.d(TAG, "Queue is full, drop oldest");
            Objects.requireNonNull(imageBlockingQueue.poll()).close();
        }
        try {
            Log.d(TAG, "Add new img to queue");
            imageBlockingQueue.put(imageReader.acquireLatestImage());
            synchronized (this)
            {
                this.notifyAll();
            }
        } catch (InterruptedException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean setCaptureResult(CaptureResult  captureResult)
    {
        Log.d(TAG,"setCaptureResult");
        if (captureResultBlockingQueue.remainingCapacity() == 1) {
            captureResultBlockingQueue.poll();
        }
        try {
            captureResultBlockingQueue.put(captureResult);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean setToPreview() {
        return setToPreview;
    }

    @Override
    public void release()
    {
        Log.d(TAG,"release");
        if (imageReader != null)
            imageReader.close();
        Image img;
        while ((img = imageBlockingQueue.poll()) != null)
            img.close();
        captureResultBlockingQueue.clear();
        for (Image i : imagespolled)
            if (i != null)
                i.close();
        imagespolled.clear();
        stopBackgroundThread();
    }

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (mBackgroundThread == null)
            return;
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
