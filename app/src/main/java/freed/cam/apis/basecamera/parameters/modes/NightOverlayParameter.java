package freed.cam.apis.basecamera.parameters.modes;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

/**
 * Created by Ingo on 24.09.2016.
 */
public class NightOverlayParameter extends AbstractParameter {
    private CameraWrapperInterface cameraWrapperInterface;
    public NightOverlayParameter(CameraWrapperInterface cameraWrapperInterface)
    {
        super(SettingKeys.NightOverlay);
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
        boolean enable = SettingsManager.get(SettingKeys.NightOverlay).get();
        if (enable)
            return SettingsManager.getInstance().getResString(R.string.on_);
        else
            return SettingsManager.getInstance().getResString(R.string.off_);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        if (valueToSet.equals(cameraWrapperInterface.getResString(R.string.on_)))
            SettingsManager.get(SettingKeys.NightOverlay).set(true);
        else
            SettingsManager.get(SettingKeys.NightOverlay).set(false);
        cameraWrapperInterface.getActivityInterface().SetNightOverlay();

    }
}
