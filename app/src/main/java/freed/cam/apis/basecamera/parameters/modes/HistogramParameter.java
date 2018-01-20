package freed.cam.apis.basecamera.parameters.modes;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;

/**
 * Created by KillerInk on 15.01.2018.
 */

public class HistogramParameter extends FocusPeakMode {

    private String state = "off";

    public HistogramParameter(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }
    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        state = valueToSet;
        if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_)))
        {
            cameraUiWrapper.getFocusPeakProcessor().setHistogramEnable(true);
            fireStringValueChanged(cameraUiWrapper.getResString(R.string.on_));
        }
        else {
            cameraUiWrapper.getFocusPeakProcessor().setHistogramEnable(false);
            fireStringValueChanged(cameraUiWrapper.getResString(R.string.off_));
        }

    }

    @Override
    public String GetStringValue() {
        return state;
    }
}
