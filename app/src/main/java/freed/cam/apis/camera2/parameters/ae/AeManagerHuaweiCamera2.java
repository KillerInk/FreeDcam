package freed.cam.apis.camera2.parameters.ae;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Rational;

import com.huawei.camera2ex.CaptureRequestEx;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.ae.AeStates;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;

/**
 * Created by KillerInk on 29.12.2017.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AeManagerHuaweiCamera2 extends AeManagerCamera2 {

    private boolean expotimeIsActive = false;
    private boolean isoIsActive = false;

    public AeManagerHuaweiCamera2(CameraWrapperInterface cameraWrapperInterface) {
        super(cameraWrapperInterface);
        manualExposureTime.fireIsReadOnlyChanged(true);
    }

    @Override
    public boolean isExposureTimeWriteable() {
        return activeAeState == AeStates.shutter_priority || activeAeState == AeStates.manual || activeAeState == AeStates.auto;
    }

    @Override
    public void setExposureTime(int valueToSet, boolean setToCamera) {
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_ENABLED,setToCamera);
        if (valueToSet > 0) {
            String shutter = manualExposureTime.getStringValues()[valueToSet];
            Rational rational;
            if (shutter.contains("/"))
            {
                String[]split = shutter.split("/");
                rational = new Rational(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
            }
            else if (shutter.contains("."))
            {
                double sh = Double.parseDouble(shutter);
                double sh10 = sh*10;
                rational = new Rational((int)sh10,10);
            }
            else
            {
                rational = new Rational(Integer.parseInt(shutter),1);
            }
            int val = (int) AbstractManualShutter.getMilliSecondStringFromShutterString(manualExposureTime.getStringValues()[valueToSet]);
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_SENSOR_EXPOSURE_TIME, val,setToCamera);
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_PROF_EXPOSURE_TIME, rational,setToCamera);
            manualExposureTime.fireIntValueChanged(valueToSet);
            expotimeIsActive = true;

        }
        else
        {
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_SENSOR_EXPOSURE_TIME, 0,setToCamera);
            expotimeIsActive = false;
        }
        applyAeMode();
    }

    private void applyAeMode()
    {
        if (expotimeIsActive && isoIsActive)
            setAeMode(AeStates.manual);
        else if (!expotimeIsActive && isoIsActive)
            setAeMode(AeStates.iso_priority);
        else if (expotimeIsActive && !isoIsActive)
            setAeMode(AeStates.shutter_priority);
        else if (!expotimeIsActive && !isoIsActive)
            setAeMode(AeStates.auto);
    }

    @Override
    public void setIso(int valueToSet, boolean setToCamera) {
        if (cameraUiWrapper.captureSessionHandler.GetActiveCameraCaptureSession() == null)
            return;
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_ENABLED,setToCamera);
        if (valueToSet == 0)
        {
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_SENSOR_ISO_VALUE, 0,setToCamera);
            isoIsActive = false;
        }
        else
        {
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_SENSOR_ISO_VALUE, Integer.parseInt(manualIso.getStringValues()[valueToSet]),setToCamera);
            isoIsActive =true;
        }
        applyAeMode();
    }

    @Override
    public void setExposureCompensation(int valueToSet, boolean setToCamera) {
        float t = Float.parseFloat(exposureCompensation.getStringValues()[valueToSet].replace(",","."));
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_EXPOSURE_COMP_VALUE, t,setToCamera);
    }

    @Override
    public void setAeMode(AeStates aeState) {
        if (activeAeState == aeState)
            return;
        activeAeState = aeState;
        switch (aeState)
        {
            case manual:
                exposureCompensation.fireIsSupportedChanged(false);
                break;
            default:
                exposureCompensation.fireIsSupportedChanged(true);
        }
    }
}
