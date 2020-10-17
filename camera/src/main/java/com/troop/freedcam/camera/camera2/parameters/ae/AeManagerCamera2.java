package com.troop.freedcam.camera.camera2.parameters.ae;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.Arrays;

import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.camera.basecamera.parameters.ParameterInterface;
import com.troop.freedcam.camera.basecamera.parameters.ae.AeManager;
import com.troop.freedcam.camera.basecamera.parameters.ae.AeStates;
import com.troop.freedcam.camera.basecamera.parameters.manual.AbstractManualShutter;
import com.troop.freedcam.camera.camera2.Camera2Fragment;
import com.troop.freedcam.camera.camera2.parameters.modes.BaseModeApi2;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.Log;

/**
 * Created by KillerInk on 29.12.2017.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AeManagerCamera2 extends AeManager {

    public final static long MAX_PREVIEW_EXPOSURETIME = 100000000;

    protected Camera2Fragment cameraUiWrapper;
    private AeModeApi2 aeModeApi2;
    public AeManagerCamera2(CameraControllerInterface cameraControllerInterface) {
        super(cameraControllerInterface);
        this.cameraUiWrapper = (Camera2Fragment) cameraControllerInterface;
        aeModeApi2 = new AeModeApi2(cameraControllerInterface);
    }

    public ParameterInterface getAeMode()
    {
        return aeModeApi2;
    }

    @Override
    public void setExposureTime(int valueToSet, boolean setToCamera) {
        if (valueToSet > 0) {
            long val = AbstractManualShutter.getMilliSecondStringFromShutterString(manualExposureTime.getStringValues()[valueToSet]) * 1000;
            Log.d(manualExposureTime.TAG, "ExposureTimeToSet:" + val);
            cameraUiWrapper.captureSessionHandler.SetCaptureParameter(CaptureRequest.SENSOR_EXPOSURE_TIME,val);
            if (val > MAX_PREVIEW_EXPOSURETIME && !SettingsManager.getInstance().GetCurrentModule().equals(ContextApplication.getStringFromRessources(R.string.module_video))) {
                Log.d(manualExposureTime.TAG, "ExposureTime Exceed 100000000 for preview, set it to 100000000");
                val = MAX_PREVIEW_EXPOSURETIME;
            }

            cameraUiWrapper.captureSessionHandler.SetPreviewParameterRepeating(CaptureRequest.SENSOR_EXPOSURE_TIME, val,setToCamera);
            manualExposureTime.fireIntValueChanged(valueToSet);
        }
    }

    @Override
    public void setIso(int valueToSet, boolean setToCamera) {
        if (valueToSet == 0)
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.ExposureMode).SetValue(cameraUiWrapper.getContext().getString(R.string.on),setToCamera);
            setAeMode(AeStates.auto);
        }
        else
        {
            if (activeAeState != AeStates.manual){
                setAeMode(AeStates.manual);
                cameraUiWrapper.getParameterHandler().get(SettingKeys.ExposureMode).SetValue(cameraUiWrapper.getContext().getString(R.string.off),setToCamera);
            }
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.SENSOR_SENSITIVITY, Integer.parseInt(manualIso.getStringValues()[valueToSet]),setToCamera);
            manualIso.fireIntValueChanged(valueToSet);
        }
    }

    @Override
    public void setExposureCompensation(int valueToSet, boolean setToCamera) {
        int t = valueToSet - exposureCompensation.getStringValues().length / 2;
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, t,setToCamera);
        exposureCompensation.fireStringValueChanged(exposureCompensation.getStringValues()[valueToSet]);
    }

    @Override
    public void setAeMode(AeStates aeState) {
        activeAeState = aeState;
        switch (aeState)
        {
            case auto:
                setToAuto();
                break;
            case manual:
                setToManual();
                break;
        }
    }

    private void setToAuto()
    {
        //back in auto mode
        //set flash back to its old state
        if (SettingsManager.get(SettingKeys.FlashMode).isSupported()) {
            cameraControllerInterface.getParameterHandler().get(SettingKeys.FlashMode).SetValue(cameraControllerInterface.getParameterHandler().get(SettingKeys.FlashMode).GetStringValue(), true);
            //show flashmode ui item
            cameraControllerInterface.getParameterHandler().get(SettingKeys.FlashMode).setViewState(AbstractParameter.ViewState.Visible);
        }
        //show ev ui item
        exposureCompensation.setViewState(AbstractParameter.ViewState.Visible);

        manualIso.setViewState(AbstractParameter.ViewState.Visible);
        manualExposureTime.setViewState(AbstractParameter.ViewState.Disabled);
    }


    private void setToManual()
    {
        //hide ev ui item
        exposureCompensation.setViewState(AbstractParameter.ViewState.Hidden);
        //turn flash off when ae is off. else on some devices it applys only manual stuff only for a few frames
        //apply it direct to the preview that old value can get loaded from FocusModeParameter when Ae gets set back to auto

        //hide flash ui item its not supported in manual mode
        if(cameraUiWrapper.getParameterHandler().get(SettingKeys.FlashMode) != null) {
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF,true);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.FlashMode).setViewState(AbstractParameter.ViewState.Hidden);
        }
        //enable manualiso item in ui
        manualIso.setViewState(AbstractParameter.ViewState.Visible);
        //enable manual exposuretime in ui
        manualExposureTime.setViewState(AbstractParameter.ViewState.Enabled);
        manualExposureTime.fireStringValueChanged(manualExposureTime.GetStringValue());
    }

    public class AeModeApi2 extends BaseModeApi2
    {
        private final String TAG = AeModeApi2.class.getSimpleName();
        public AeModeApi2(CameraControllerInterface cameraUiWrapper) {
            super(cameraUiWrapper, SettingKeys.ExposureMode,CaptureRequest.CONTROL_AE_MODE);
            Log.d(TAG, "values: " + Arrays.toString(getStringValues()) + " value:" + GetStringValue());
        }

        @Override
        public void setValue(String valueToSet, boolean setToCamera)
        {
            super.setValue(valueToSet,setToCamera);
            if (valueToSet.equals(ContextApplication.getStringFromRessources(R.string.off))) {
                setAeMode(AeStates.manual);
            }
            else {
                setAeMode(AeStates.auto);
            }
            fireStringValueChanged(valueToSet);
        }
    }
}
