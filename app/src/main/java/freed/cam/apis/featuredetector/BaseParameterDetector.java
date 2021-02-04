package freed.cam.apis.featuredetector;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.utils.Log;

public abstract class BaseParameterDetector<T> {
    public void checkIfSupported(T cameraCharacteristics)
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

    protected abstract void findAndFillSettings(T cameraCharacteristics);


}
