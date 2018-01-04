package freed.cam.apis.camera2.parameters.ae;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.apis.basecamera.parameters.ae.AeManager;
import freed.cam.apis.basecamera.parameters.ae.AeStates;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.camera2.parameters.modes.BaseModeApi2;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by KillerInk on 29.12.2017.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AeManagerCamera2 extends AeManager {

    public final static long MAX_PREVIEW_EXPOSURETIME = 100000000;

    protected Camera2Fragment cameraUiWrapper;
    private AeModeApi2 aeModeApi2;
    public AeManagerCamera2(CameraWrapperInterface cameraWrapperInterface) {
        super(cameraWrapperInterface);
        this.cameraUiWrapper = (Camera2Fragment)cameraWrapperInterface;
        aeModeApi2 = new AeModeApi2(cameraWrapperInterface);
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
            if (val > MAX_PREVIEW_EXPOSURETIME && !SettingsManager.getInstance().GetCurrentModule().equals(cameraUiWrapper.getResString(R.string.module_video))) {
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
        cameraWrapperInterface.getParameterHandler().get(SettingKeys.FlashMode).SetValue(cameraWrapperInterface.getParameterHandler().get(SettingKeys.FlashMode).GetStringValue(),true);
        //show flashmode ui item
        cameraWrapperInterface.getParameterHandler().get(SettingKeys.FlashMode).fireIsSupportedChanged(true);
        //set exposure ui item to enable
        exposureCompensation.fireIsSupportedChanged(true);
        exposureCompensation.fireIsReadOnlyChanged(true);
        manualIso.fireIsReadOnlyChanged(true);
        manualExposureTime.fireIsReadOnlyChanged(false);
    }


    private void setToManual()
    {
        //hide manualexposuretime ui item
        exposureCompensation.fireIsSupportedChanged(false);
        //turn flash off when ae is off. else on some devices it applys only manual stuff only for a few frames
        //apply it direct to the preview that old value can get loaded from FocusModeParameter when Ae gets set back to auto
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF,true);
        //hide flash ui item its not supported in manual mode
        cameraUiWrapper.getParameterHandler().get(SettingKeys.FlashMode).fireIsSupportedChanged(false);
        //enable manualiso item in ui
        manualIso.fireIsReadOnlyChanged(true);
        //enable manual exposuretime in ui
        manualExposureTime.fireIsReadOnlyChanged(true);
        manualExposureTime.fireStringValueChanged(manualExposureTime.GetStringValue());
    }

    public class AeModeApi2 extends BaseModeApi2
    {
        public AeModeApi2(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper, SettingKeys.ExposureMode,CaptureRequest.CONTROL_AE_MODE);
        }

        @Override
        public void setValue(String valueToSet, boolean setToCamera)
        {
            super.setValue(valueToSet,setToCamera);
            if (valueToSet.equals(cameraUiWrapper.getContext().getString(R.string.off))) {
                setAeMode(AeStates.manual);
            }
            else {
                setAeMode(AeStates.auto);
            }
            fireStringValueChanged(valueToSet);
        }
    }
}
