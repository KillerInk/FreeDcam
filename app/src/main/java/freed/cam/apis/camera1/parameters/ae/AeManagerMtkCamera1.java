package freed.cam.apis.camera1.parameters.ae;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.ae.AeManager;
import freed.cam.apis.basecamera.parameters.ae.AeStates;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.Settings;
import freed.settings.SettingsManager;

/**
 * Created by KillerInk on 29.12.2017.
 */

public class AeManagerMtkCamera1 extends AeManager
{
    private Camera.Parameters parameters;

    public AeManagerMtkCamera1(CameraWrapperInterface cameraWrapperInterface, Camera.Parameters parameters) {
        super(cameraWrapperInterface);
        this.parameters =parameters;
    }

    @Override
    public void setExposureTime(int valueToSet, boolean setToCamera) {
        if (valueToSet == 0)
        {
            parameters.set("m-ss", "0");
        }
        else
        {
            String shutterstring = manualExposureTime.getStringValues()[valueToSet];
            if (shutterstring.contains("/")) {
                String[] split = shutterstring.split("/");
                Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
                shutterstring = "" + a;
            }
            parameters.set("m-ss", FLOATtoThirty(shutterstring));
        }
        ((ParametersHandler)cameraWrapperInterface.getParameterHandler()).SetParametersToCamera(parameters);
    }

    private String FLOATtoThirty(String a)
    {
        Float b =  Float.parseFloat(a);
        float c = b * 1000;
        return String.valueOf(c);
    }

    @Override
    public void setIso(int valueToSet, boolean setToCamera) {
        if (valueToSet == 0)
        {
            parameters.set("m-sr-g", "0");
            setAeMode(AeStates.auto);
        }
        else
        {
            //cap-isp-g= 1024 == iso100? cause cap-sr-g=7808 / 1024 *100 = 762,5 same with 256 = 3050
            parameters.set("m-sr-g", String.valueOf(Integer.valueOf(manualIso.getStringValues()[valueToSet])/100 *256));
            setAeMode(AeStates.manual);
        }
        ((ParametersHandler)cameraWrapperInterface.getParameterHandler()).SetParametersToCamera(parameters);
    }

    @Override
    public void setExposureCompensation(int valueToSet, boolean setToCamera) {

    }

    @Override
    public void setAeMode(AeStates aeState) {

        if (aeState == activeAeState)
            return;
        activeAeState = aeState;
        if (aeState == AeStates.auto)
            setToAuto();
        else if (aeState == AeStates.manual)
            setToManual();
    }

    private void setToAuto()
    {
        String t = cameraWrapperInterface.getParameterHandler().get(Settings.IsoMode).GetStringValue();
        if (!t.equals(cameraWrapperInterface.getResString(R.string.iso100_)))
            cameraWrapperInterface.getParameterHandler().get(Settings.IsoMode).SetValue(cameraWrapperInterface.getResString(R.string.iso100_), true);
        else
            cameraWrapperInterface.getParameterHandler().get(Settings.IsoMode).SetValue(cameraWrapperInterface.getResString(R.string.auto_), true);
        cameraWrapperInterface.getParameterHandler().get(Settings.IsoMode).SetValue(t, true);
        //back in auto mode
        //set exposure ui item to enable
        /*exposureCompensation.fireIsSupportedChanged(true);
        exposureCompensation.fireIsReadOnlyChanged(true);*/
        manualIso.fireIsReadOnlyChanged(true);
        manualExposureTime.fireIsReadOnlyChanged(false);
    }


    private void setToManual()
    {
        //hide manualexposuretime ui item
        /*exposureCompensation.fireIsSupportedChanged(false);*/
        //turn flash off when ae is off. else on some devices it applys only manual stuff only for a few frames
        manualExposureTime.setValue(manualExposureTime.GetValue(),true);
        //enable manualiso item in ui
        manualIso.fireIsReadOnlyChanged(true);
        //enable manual exposuretime in ui
        manualExposureTime.setValue(manualExposureTime.GetValue(),true);
        manualExposureTime.fireIsReadOnlyChanged(true);
        manualExposureTime.fireStringValueChanged(manualExposureTime.GetStringValue());
    }
}
