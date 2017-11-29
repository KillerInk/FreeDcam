package freed.cam.apis.camera1.parameters.manual.shutter;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 29.01.2017.
 */

public class ExposureTime_MicroSec extends AbstractParameter {
    private final String TAG = ExposureTime_MicroSec.class.getSimpleName();
    private Camera.Parameters parameters;
    public ExposureTime_MicroSec(CameraWrapperInterface cameraUiWrapper, Camera.Parameters parameters) {
        super(cameraUiWrapper);
        stringvalues = AppSettingsManager.getInstance().manualExposureTime.getValues();
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
    public void setValue(int valueToset)
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
            parameters.set(AppSettingsManager.getInstance().manualExposureTime.getKEY(), shutterstring);
        }
        else
        {
            parameters.set(AppSettingsManager.getInstance().manualExposureTime.getKEY(), "0");
            Log.d(TAG, "set exposure time to auto");
        }
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
    }
}
