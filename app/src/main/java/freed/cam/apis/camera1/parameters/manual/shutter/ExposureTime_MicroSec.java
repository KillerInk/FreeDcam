package freed.cam.apis.camera1.parameters.manual.shutter;

import android.hardware.Camera;
import freed.utils.Log;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter;
import freed.cam.apis.camera1.parameters.ParametersHandler;

/**
 * Created by troop on 29.01.2017.
 */

public class ExposureTime_MicroSec extends AbstractManualParameter {
    private final String TAG = ExposureTime_MicroSec.class.getSimpleName();
    private Camera.Parameters parameters;
    public ExposureTime_MicroSec(CameraWrapperInterface cameraUiWrapper, Camera.Parameters parameters) {
        super(cameraUiWrapper);
        stringvalues = cameraUiWrapper.GetAppSettingsManager().manualExposureTime.getValues();
        isSupported = true;
        isVisible = true;
        this.parameters = parameters;
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public void SetValue(int valueToset)
    {
        currentInt = valueToset;
        String shutterstring = stringvalues[currentInt];
        if(!shutterstring.equals(cameraUiWrapper.getResString(R.string.auto_)))
        {
            if (stringvalues[currentInt].contains("/")) {
                String[] split = stringvalues[currentInt].split("/");
                Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
                shutterstring = "" + a;
            }
            shutterstring = Double.parseDouble(shutterstring) * 1000 +"";
            Log.d(TAG, "set exposure time to " + shutterstring);
            parameters.set(cameraUiWrapper.GetAppSettingsManager().manualExposureTime.getKEY(), shutterstring);
        }
        else
        {
            parameters.set(cameraUiWrapper.GetAppSettingsManager().manualExposureTime.getKEY(), "0");
            Log.d(TAG, "set exposure time to auto");
        }
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
    }
}
