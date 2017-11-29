package freed.cam.apis.camera1.parameters.manual;

import android.hardware.Camera;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.AppSettingsManager;

/**
 * Created by troop on 18.03.2017.
 */

public class ManualIsoSony extends AbstractParameter
{
    private final Camera.Parameters parameters;

    public ManualIsoSony(CameraWrapperInterface cameraUiWrapper, Camera.Parameters parameters) {
        super(cameraUiWrapper);
        this.parameters = parameters;
        stringvalues = AppSettingsManager.getInstance().manualIso.getValues();
        isSupported = true;
        isVisible = isSupported;
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }

    @Override
    public boolean IsSetSupported() {
        return isNotReadOnly;
    }

    @Override
    public void setValue(int valueToSet)
    {
        currentInt = valueToSet;
        if (currentInt == 0)
        {
            if (cameraUiWrapper.getParameterHandler().ManualShutter.GetValue() == 0)
                parameters.set("sony-ae-mode", "auto");
            else if (cameraUiWrapper.getParameterHandler().ManualShutter.GetValue() >0)
                parameters.set("sony-ae-mode", "shutter-prio");
        }
        else {
            if (cameraUiWrapper.getParameterHandler().ManualShutter.GetValue() == 0 && !parameters.get("sony-ae-mode").equals("iso-prio"))
                parameters.set("sony-ae-mode", "iso-prio");
            else if (cameraUiWrapper.getParameterHandler().ManualShutter.GetValue() >0 && !parameters.get("sony-ae-mode").equals("manual"))
                parameters.set("sony-ae-mode", "manual");
            parameters.set(AppSettingsManager.getInstance().manualIso.getKEY(), stringvalues[currentInt]);
        }
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
    }

}
