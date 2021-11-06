package freed.cam.apis.camera2.parameters.ae;

import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.qcom.CaptureRequestQcom;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ae.AeStates;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;
import freed.cam.apis.camera2.Camera2;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AeManagerCamera2Qcom extends AeManagerCamera2 {

    private boolean expotimeIsActive;
    private boolean isoIsActive;

    public AeManagerCamera2Qcom(Camera2 cameraWrapperInterface) {
        super(cameraWrapperInterface);
    }

    @Override
    public boolean isExposureTimeWriteable() {
        return true;
    }

    @Override
    public boolean isIsoWriteable() {
        return true;
    }

    @Override
    public void setAeMode(AeStates aeState) {
        if (activeAeState == aeState)
            return;
        activeAeState = aeState;
        switch (aeState)
        {
            case manual:
                exposureCompensation.setViewState(AbstractParameter.ViewState.Disabled);
                cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.org_codeaurora_qcamera3_iso_exp_priority_select_priority,0,false);
                break;
            case iso_priority:
                cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.org_codeaurora_qcamera3_iso_exp_priority_select_priority, 0,false);
                cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.org_codeaurora_qcamera3_iso_exp_priority_use_iso_exp_priority, 8L,false);
                break;
            case shutter_priority:
                cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.org_codeaurora_qcamera3_iso_exp_priority_select_priority, 1,false);
                break;
            default:
                cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.org_codeaurora_qcamera3_iso_exp_priority_select_priority, 0,false);
                cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.org_codeaurora_qcamera3_iso_exp_priority_use_iso_exp_priority, 0L,false);
                exposureCompensation.setViewState(AbstractParameter.ViewState.Enabled);
        }
    }

    @Override
    public void setExposureCompensation(int valueToSet, boolean setToCamera) {
        super.setExposureCompensation(valueToSet,setToCamera);
       /* if (valueToSet > exposureCompensation.getStringValues().length)
            valueToSet = exposureCompensation.getStringValues().length/2;
        float t = Float.parseFloat(exposureCompensation.getStringValues()[valueToSet].replace(",","."));
        cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.org_codeaurora_qcamera3_iso_exp_priority_use_gain_value, t,setToCamera);*/
    }

    @Override
    public void setExposureTime(int valueToSet, boolean setToCamera) {
        if (valueToSet > 0) {
            long val = AbstractManualShutter.getMilliSecondStringFromShutterString(manualExposureTime.getStringValues()[valueToSet])* 1000;
            cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.org_codeaurora_qcamera3_iso_exp_priority_use_iso_exp_priority, val,setToCamera);

            manualExposureTime.fireIntValueChanged(valueToSet);
            expotimeIsActive = true;

        }
        else
        {
            cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.org_codeaurora_qcamera3_iso_exp_priority_use_iso_exp_priority, 0L,setToCamera);
            expotimeIsActive = false;
        }
        applyAeMode();
    }

    @Override
    public void setIso(int valueToSet, boolean setToCamera) {
        if (valueToSet == 0)
        {
            cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.org_codeaurora_qcamera3_iso_exp_priority_use_iso_value, 0,setToCamera);
            isoIsActive = false;
        }
        else
        {
            cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequestQcom.org_codeaurora_qcamera3_iso_exp_priority_use_iso_value, Integer.parseInt(manualIso.getStringValues()[valueToSet]),setToCamera);
            isoIsActive =true;
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
}
