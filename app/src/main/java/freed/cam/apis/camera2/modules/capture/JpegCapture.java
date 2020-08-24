package freed.cam.apis.camera2.modules.capture;

import android.graphics.ImageFormat;
import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.cam.apis.camera2.modules.helper.CaptureType;
import freed.image.EmptyTask;
import freed.image.ImageTask;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JpegCapture extends ByteImageCapture {

    private final String TAG = JpegCapture.class.getSimpleName();

    public JpegCapture(Size size, boolean setToPreview, ActivityInterface activityInterface, ModuleInterface moduleInterface, String file_ending, int max_images) {
        super(size, ImageFormat.JPEG, setToPreview, activityInterface, moduleInterface, file_ending,max_images);
    }

    @Override
    protected void createTask() {
        if (captureType == CaptureType.Jpeg || captureType == CaptureType.JpegDng10 || captureType == CaptureType.JpegDng16)
            super.createTask();
        else
            task = new EmptyTask();
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        if (captureType == CaptureType.Jpeg || captureType == CaptureType.JpegDng10 || captureType == CaptureType.JpegDng16) {
            Log.d(TAG, "onImageAvailable forward to super");
            super.onImageAvailable(reader);
        }
        else {
            Log.d(TAG, "onImageAvailable close image not in jpeg capture mode");
            image = reader.acquireLatestImage();
            if (image != null)
                image.close();
            image = null;
            synchronized (this)
            {
                notifyAll();
            }
        }
    }
}
