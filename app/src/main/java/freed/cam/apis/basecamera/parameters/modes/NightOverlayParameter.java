package freed.cam.apis.basecamera.parameters.modes;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.AppSettingsManager;

/**
 * Created by Ingo on 24.09.2016.
 */
public class NightOverlayParameter extends AbstractParameter {
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
    public String[] getStringValues() {
        return new String[] { cameraWrapperInterface.getResString(R.string.off_), cameraWrapperInterface.getResString(R.string.on_) };
    }

    @Override
    public String GetStringValue() {
        boolean enable = AppSettingsManager.getInstance().getBoolean(AppSettingsManager.SETTINGS_NIGHTOVERLAY,false);
        if (enable)
            return AppSettingsManager.getInstance().getResString(R.string.on_);
        else
            return AppSettingsManager.getInstance().getResString(R.string.off_);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        if (valueToSet.equals(cameraWrapperInterface.getResString(R.string.on_)))
            AppSettingsManager.getInstance().setBoolean(AppSettingsManager.SETTINGS_NIGHTOVERLAY,true);
        else
            AppSettingsManager.getInstance().setBoolean(AppSettingsManager.SETTINGS_NIGHTOVERLAY,false);
        cameraWrapperInterface.getActivityInterface().SetNightOverlay();

    }
}
