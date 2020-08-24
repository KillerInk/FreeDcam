package freed.cam.apis.basecamera.parameters.modes;

import com.troop.freedcam.R;

import freed.ActivityInterface;
import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by Ingo on 24.09.2016.
 */
public class NightOverlayParameter extends AbstractParameter {

    private final String TAG = NightOverlayParameter.class.getSimpleName();
    private ActivityInterface activityInterface;

    public NightOverlayParameter(CameraWrapperInterface cameraWrapperInterface)
    {
        super(SettingKeys.NightOverlay);
        this.activityInterface = cameraWrapperInterface.getActivityInterface();
        setViewState(ViewState.Visible);
    }

    @Override
    public String[] getStringValues() {
        return new String[] { FreedApplication.getStringFromRessources(R.string.off_), FreedApplication.getStringFromRessources(R.string.on_) };
    }

    @Override
    public String GetStringValue() {
        boolean enable = SettingsManager.getGlobal(SettingKeys.NightOverlay).get();
        if (enable)
            return FreedApplication.getStringFromRessources(R.string.on_);
        else
            return FreedApplication.getStringFromRessources(R.string.off_);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        SettingsManager.getGlobal(SettingKeys.NightOverlay).set(valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_)));
        Log.d(TAG, "Nightoverlay :" +SettingsManager.getGlobal(SettingKeys.NightOverlay).get());
        activityInterface.SetNightOverlay();
        fireStringValueChanged(valueToSet);

    }
}
