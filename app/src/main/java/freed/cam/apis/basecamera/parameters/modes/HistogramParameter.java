package freed.cam.apis.basecamera.parameters.modes;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingKeys;

/**
 * Created by KillerInk on 15.01.2018.
 */

public class HistogramParameter extends FocusPeakMode {

    private String state = "off";

    public HistogramParameter(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.HISTOGRAM);
    }
    @Override
    public void setStringValue(String valueToSet, boolean setToCamera)
    {
        state = valueToSet;
        if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_)))
        {
            cameraUiWrapper.getPreview().setHistogram(true);
            fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.on_));
        }
        else {
            cameraUiWrapper.getPreview().setHistogram(false);
            fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
        }

    }

    @Override
    public String getStringValue() {
        return state;
    }
}
