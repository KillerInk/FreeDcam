package freed.cam.apis.basecamera.parameters.modes;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;

/**
 * Created by KillerInk on 23.01.2018.
 */

public class ClippingMode extends HistogramParameter {

    private String state = "off";

    public ClippingMode(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        state = valueToSet;
        if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_)))
        {
            cameraUiWrapper.getFocusPeakProcessor().setClippingEnable(true);
            fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.on_));
        }
        else {
            cameraUiWrapper.getFocusPeakProcessor().setClippingEnable(false);
            fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
        }

    }

    @Override
    public String GetStringValue() {
        return state;
    }
}
