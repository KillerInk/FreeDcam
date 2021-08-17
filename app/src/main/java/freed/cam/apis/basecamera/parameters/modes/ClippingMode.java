package freed.cam.apis.basecamera.parameters.modes;

import android.text.TextUtils;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

/**
 * Created by KillerInk on 23.01.2018.
 */

public class ClippingMode extends FocusPeakMode {

    public ClippingMode(CameraWrapperInterface cameraUiWrapper, SettingKeys.Key settingMode) {
        super(cameraUiWrapper, settingMode);
    }

    @Override
    public void setStringValue(String valueToSet, boolean setToCamera)
    {
        currentString = valueToSet;
        settingMode.set(valueToSet);
        if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_)))
        {
            previewController.setClipping(true);
            fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.on_));
        }
        else {
            previewController.setClipping(false);
            fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
        }

    }
}
