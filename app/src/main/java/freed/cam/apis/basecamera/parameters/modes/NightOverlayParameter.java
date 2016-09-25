package freed.cam.apis.basecamera.parameters.modes;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.utils.AppSettingsManager;

/**
 * Created by Ingo on 24.09.2016.
 */
public class NightOverlayParameter extends AbstractModeParameter  {
    private CameraWrapperInterface cameraWrapperInterface;
    public NightOverlayParameter(CameraWrapperInterface cameraWrapperInterface)
    {
        this.cameraWrapperInterface = cameraWrapperInterface;
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public boolean IsVisible() {
        return true;
    }
    @Override
    public String[] GetValues() {
        return new String[] { KEYS.OFF, KEYS.ON };
    }

    @Override
    public String GetValue() {
        boolean enable = cameraWrapperInterface.GetAppSettingsManager().getBoolean(AppSettingsManager.SETTINGS_NIGHTOVERLAY,false);
        if (enable)
            return KEYS.ON;
        else
            return KEYS.OFF;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        if (valueToSet.equals(KEYS.ON))
            cameraWrapperInterface.GetAppSettingsManager().setBoolean(AppSettingsManager.SETTINGS_NIGHTOVERLAY,true);
        else
            cameraWrapperInterface.GetAppSettingsManager().setBoolean(AppSettingsManager.SETTINGS_NIGHTOVERLAY,false);
        cameraWrapperInterface.getActivityInterface().SetNightOverlay();

    }
}
