package freed.cam.apis.camera2.modules.capture;

import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.image.EmptyTask;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ContinouseYuvCapture extends StillImageCapture {

    public ContinouseYuvCapture(Size size, int format, boolean setToPreview, ActivityInterface activityInterface, ModuleInterface moduleInterface, String file_ending,int max_images) {
        super(size, format, setToPreview, activityInterface, moduleInterface, file_ending,max_images);
    }


    @Override
    protected void createTask() {
        task = new EmptyTask();
        if (image !=  null)
            image.close();
    }
}
