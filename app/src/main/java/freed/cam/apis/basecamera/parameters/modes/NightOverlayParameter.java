package freed.cam.apis.basecamera.parameters.modes;

import com.troop.freedcam.R;

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
        return new String[] { cameraWrapperInterface.getResString(R.string.off_), cameraWrapperInterface.getResString(R.string.on_) };
    }

    @Override
    public String GetValue() {
        boolean enable = cameraWrapperInterface.getAppSettingsManager().getBoolean(AppSettingsManager.SETTINGS_NIGHTOVERLAY,false);
        if (enable)
            return cameraWrapperInterface.getAppSettingsManager().getResString(R.string.on_);
        else
            return cameraWrapperInterface.getAppSettingsManager().getResString(R.string.off_);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        if (valueToSet.equals(cameraWrapperInterface.getResString(R.string.on_)))
            cameraWrapperInterface.getAppSettingsManager().setBoolean(AppSettingsManager.SETTINGS_NIGHTOVERLAY,true);
        else
            cameraWrapperInterface.getAppSettingsManager().setBoolean(AppSettingsManager.SETTINGS_NIGHTOVERLAY,false);
        cameraWrapperInterface.getActivityInterface().SetNightOverlay();

    }
}
