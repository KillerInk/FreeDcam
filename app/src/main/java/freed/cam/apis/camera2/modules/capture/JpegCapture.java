package freed.cam.apis.camera2.modules.capture;

import android.graphics.ImageFormat;
import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.cam.apis.camera2.modules.helper.CaptureType;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JpegCapture extends ByteImageCapture {

    public JpegCapture(Size size, boolean setToPreview, ActivityInterface activityInterface, ModuleInterface moduleInterface, String file_ending) {
        super(size, ImageFormat.JPEG, setToPreview, activityInterface, moduleInterface, file_ending);
    }

    @Override
    public boolean onCaptureCompleted(Image image, CaptureResult result) {
        if (captureType == CaptureType.Jpeg || captureType == CaptureType.JpegDng10 || captureType == CaptureType.JpegDng16)
            super.onCaptureCompleted(image, result);
        return false;
    }
}
