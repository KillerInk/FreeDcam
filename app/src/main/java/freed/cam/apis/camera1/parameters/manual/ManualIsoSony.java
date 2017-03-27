package freed.cam.apis.camera1.parameters.manual;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter;
import freed.cam.apis.camera1.parameters.ParametersHandler;

/**
 * Created by troop on 18.03.2017.
 */

public class ManualIsoSony extends AbstractManualParameter
{
    private final Camera.Parameters parameters;

    public ManualIsoSony(CameraWrapperInterface cameraUiWrapper, Camera.Parameters parameters) {
        super(cameraUiWrapper);
        this.parameters = parameters;
        stringvalues = cameraUiWrapper.GetAppSettingsManager().manualIso.getValues();
        isSupported = true;
        isVisible = isSupported;
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
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        if (currentInt == 0)
        {
            if (cameraUiWrapper.GetParameterHandler().ManualShutter.GetValue() == 0)
                parameters.set("sony-ae-mode", "auto");
            else if (cameraUiWrapper.GetParameterHandler().ManualShutter.GetValue() >0)
                parameters.set("sony-ae-mode", "shutter-prio");
        }
        else {
            if (cameraUiWrapper.GetParameterHandler().ManualShutter.GetValue() == 0 && !parameters.get("sony-ae-mode").equals("iso-prio"))
                parameters.set("sony-ae-mode", "iso-prio");
            else if (cameraUiWrapper.GetParameterHandler().ManualShutter.GetValue() >0 && !parameters.get("sony-ae-mode").equals("manual"))
                parameters.set("sony-ae-mode", "manual");
            parameters.set(cameraUiWrapper.GetAppSettingsManager().manualIso.getKEY(), stringvalues[currentInt]);
        }
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
    }
}
