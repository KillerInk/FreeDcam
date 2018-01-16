package freed.cam.apis.camera2.parameters.modes;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;

/**
 * Created by KillerInk on 15.01.2018.
 */

public class HistogramParameterApi2 extends FocusPeakModeApi2 {

    private String state = "off";

    public HistogramParameterApi2(CameraWrapperInterface cameraUiWrapper) {
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
