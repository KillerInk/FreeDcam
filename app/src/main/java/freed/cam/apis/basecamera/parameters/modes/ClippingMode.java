package freed.cam.apis.basecamera.parameters.modes;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingKeys;

/**
 * Created by KillerInk on 23.01.2018.
 */

public class ClippingMode extends FocusPeakMode {


    public ClippingMode(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.CLIPPING);
    }

    @Override
    public void setStringValue(String valueToSet, boolean setToCamera)
    {
        currentString = valueToSet;
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
