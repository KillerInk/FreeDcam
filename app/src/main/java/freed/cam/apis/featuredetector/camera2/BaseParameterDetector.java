package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public abstract class BaseParameterDetector {
    public void checkIfSupported(CameraCharacteristics cameraCharacteristics)
    {
        try
        {
            findAndFillSettings(cameraCharacteristics);
        }
        catch (IllegalArgumentException | NullPointerException | ClassCastException e)
        {
            Log.WriteEx(e);
        }
    }

    protected abstract void findAndFillSettings(CameraCharacteristics cameraCharacteristics);

}
