package freed.cam.apis.camera2.modules.capture;

import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleInterface;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ContinouseYuvCapture extends StillImageCapture {

    public ContinouseYuvCapture(Size size, int format, boolean setToPreview, ActivityInterface activityInterface, ModuleInterface moduleInterface, String file_ending) {
        super(size, format, setToPreview, activityInterface, moduleInterface, file_ending);
    }

    @Override
    public boolean onCaptureCompleted(Image image, CaptureResult result) {
        return false;
    }
}
