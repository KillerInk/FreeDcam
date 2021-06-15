package freed.cam.apis.featuredetector;

import freed.FreedApplication;
import freed.settings.SettingsManager;
import freed.utils.Log;

public abstract class BaseParameterDetector<T> {

    protected SettingsManager settingsManager;

    public BaseParameterDetector()
    {
        settingsManager = FreedApplication.settingsManager();
    }

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
