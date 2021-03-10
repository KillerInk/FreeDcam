package freed.cam.apis.featuredetector;

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
