package freed.cam.apis.basecamera.parameters.modes;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingKeys;

/**
 * Created by KillerInk on 15.01.2018.
 */

public class HistogramParameter extends FocusPeakMode {

    private final String state = "off";

    public HistogramParameter(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.HISTOGRAM);
        settingMode = SettingKeys.HISTOGRAM;
    }
    @Override
    public void setStringValue(String valueToSet, boolean setToCamera)
    {
        currentString = valueToSet;
        boolean toset = valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_));
        previewController.setHistogram(toset);
        settingsManager.get(settingMode).set(toset);
        fireStringValueChanged(valueToSet);

    }

    @Override
    public void set(boolean bool) {
        previewController.setHistogram(bool);
        settingsManager.get(settingMode).set(bool);
        fireStringValueChanged(getStringValue());
    }
}
