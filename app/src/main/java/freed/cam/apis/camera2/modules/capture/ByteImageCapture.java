package freed.cam.apis.camera2.modules.capture;

import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.nio.ByteBuffer;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.image.ImageManager;
import freed.image.ImageSaveTask;
import freed.image.ImageTask;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ByteImageCapture extends StillImageCapture {

    private final String TAG = ByteImageCapture.class.getSimpleName();
    public ByteImageCapture(Size size, int format, boolean setToPreview, ActivityInterface activityInterface, ModuleInterface moduleInterface, String file_ending) {
        super(size, format, setToPreview,activityInterface,moduleInterface,file_ending);
    }

    @Override
    public boolean onCaptureCompleted(Image image, CaptureResult result) {
        File file = new File(getFilepath()+file_ending);
        ImageTask task = process_jpeg(image, file);
        if (task != null) {
            ImageManager.putImageSaveTask(task);
            Log.d(TAG, "Put task to Queue");
        }
        return false;
    }


    private ImageTask process_jpeg(Image image, File file) {

        Log.d(TAG, "Create JPEG");
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        ImageSaveTask task = new ImageSaveTask(activityInterface,moduleInterface);
        task.setBytesTosave(bytes, ImageSaveTask.JPEG);
        task.setFilePath(file,externalSD);
        buffer.clear();
        image.close();
        buffer = null;
        image = null;
        return task;
    }
}
