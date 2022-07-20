package freed.cam.apis.camera1.parameters.manual.shutter;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.SettingKeys;
import freed.utils.Log;

/**
 * Created by troop on 29.01.2017.
 */

public class ExposureTime_MicroSec extends AbstractParameter {

    private final String TAG = ExposureTime_MicroSec.class.getSimpleName();
    private final Camera.Parameters parameters;

    public ExposureTime_MicroSec(CameraWrapperInterface cameraUiWrapper, Camera.Parameters parameters) {
        super(cameraUiWrapper,SettingKeys.M_EXPOSURE_TIME);
        stringvalues = settingsManager.get(SettingKeys.M_EXPOSURE_TIME).getValues();
       setViewState(ViewState.Visible);
        this.parameters = parameters;
    }

    @Override
    public void setValue(int valueToset, boolean setToCamera)
    {
        super.setValue(valueToset,setToCamera);
        currentInt = valueToset;
        String shutterstring = stringvalues[currentInt];
        if(!shutterstring.equals(FreedApplication.getStringFromRessources(R.string.auto_)))
        {
            if (shutterstring.contains("/")) {
                String[] split = shutterstring.split("/");
                Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
                shutterstring = "" + a;
            }
            Log.d(TAG, "StringUtils.FormatShutterStringToDouble:" + shutterstring);

            float b =  Float.parseFloat(shutterstring);
            float c = b * 1000000;
            shutterstring = Math.round(c)+"";
            parameters.set(settingsManager.get(SettingKeys.M_EXPOSURE_TIME).getCamera1ParameterKEY(), shutterstring);

        }
        else
        {
            parameters.set(settingsManager.get(SettingKeys.M_EXPOSURE_TIME).getCamera1ParameterKEY(), "0");
            Log.d(TAG, "set exposure time to auto");
        }
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
    }
}
