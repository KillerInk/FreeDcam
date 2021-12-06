package freed.cam.apis.camera2.parameters.ae;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.Arrays;

import freed.FreedApplication;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ae.AeManager;
import freed.cam.apis.basecamera.parameters.ae.AeStates;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.parameters.modes.BaseModeApi2;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by KillerInk on 29.12.2017.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AeManagerCamera2 extends AeManager<Camera2> {

    public final static long MAX_PREVIEW_EXPOSURETIME = 100000000;

    private AeModeApi2 aeModeApi2;
    private SettingsManager settingsManager;
    public AeManagerCamera2(Camera2 cameraWrapperInterface) {
        super(cameraWrapperInterface);
        aeModeApi2 = new AeModeApi2(cameraWrapperInterface);
        settingsManager = FreedApplication.settingsManager();
    }

    public AbstractParameter getAeMode()
    {
        return aeModeApi2;
    }

    @Override
    public void setExposureTime(int valueToSet, boolean setToCamera) {
        if (valueToSet > 0) {
            long val = AbstractManualShutter.getMilliSecondStringFromShutterString(manualExposureTime.getStringValues()[valueToSet]) * 1000;
            Log.d(manualExposureTime.TAG, "ExposureTimeToSet:" + val);
            cameraWrapperInterface.captureSessionHandler.SetCaptureParameter(CaptureRequest.SENSOR_EXPOSURE_TIME,val);
            if (val > MAX_PREVIEW_EXPOSURETIME && !settingsManager.GetCurrentModule().equals(FreedApplication.getStringFromRessources(R.string.module_video))) {
                Log.d(manualExposureTime.TAG, "ExposureTime Exceed 100000000 for preview, set it to 100000000");
                val = MAX_PREVIEW_EXPOSURETIME;
            }

            cameraWrapperInterface.captureSessionHandler.SetPreviewParameterRepeating(CaptureRequest.SENSOR_EXPOSURE_TIME, val,setToCamera);
            manualExposureTime.fireIntValueChanged(valueToSet);
        }
    }

    @Override
    public void setIso(int valueToSet, boolean setToCamera) {
        if (valueToSet == 0)
        {
            cameraWrapperInterface.getParameterHandler().get(SettingKeys.ExposureMode).setStringValue(FreedApplication.getContext().getString(R.string.on),setToCamera);
            setAeMode(AeStates.auto);
        }
        else
        {
            if (activeAeState != AeStates.manual){
                setAeMode(AeStates.manual);
                cameraWrapperInterface.getParameterHandler().get(SettingKeys.ExposureMode).setStringValue(FreedApplication.getContext().getString(R.string.off),setToCamera);
            }
            cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequest.SENSOR_SENSITIVITY, Integer.parseInt(manualIso.getStringValues()[valueToSet]),setToCamera);
            manualIso.fireIntValueChanged(valueToSet);
        }
    }

    @Override
    public void setExposureCompensation(int valueToSet, boolean setToCamera) {
        cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, valueToSet,setToCamera);
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
        if (settingsManager.get(SettingKeys.FlashMode).isSupported()) {
            cameraWrapperInterface.getParameterHandler().get(SettingKeys.FlashMode).setStringValue(cameraWrapperInterface.getParameterHandler().get(SettingKeys.FlashMode).getStringValue(), true);
            //show flashmode ui item
            cameraWrapperInterface.getParameterHandler().get(SettingKeys.FlashMode).setViewState(AbstractParameter.ViewState.Visible);
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
        if(cameraWrapperInterface.getParameterHandler().get(SettingKeys.FlashMode) != null) {
            cameraWrapperInterface.captureSessionHandler.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF,true);
            cameraWrapperInterface.getParameterHandler().get(SettingKeys.FlashMode).setViewState(AbstractParameter.ViewState.Hidden);
        }
        //enable manualiso item in ui
        manualIso.setViewState(AbstractParameter.ViewState.Visible);
        //enable manual exposuretime in ui
        manualExposureTime.setViewState(AbstractParameter.ViewState.Enabled);
        int val = Integer.parseInt(settingsManager.get(SettingKeys.M_ExposureTime).get());
        manualExposureTime.setValue(val,true);
        //manualExposureTime.fireStringValueChanged(manualExposureTime.getStringValue());
    }

    public class AeModeApi2 extends BaseModeApi2
    {
        private final String TAG = AeModeApi2.class.getSimpleName();
        public AeModeApi2(Camera2 cameraUiWrapper) {
            super(cameraUiWrapper, SettingKeys.ExposureMode,CaptureRequest.CONTROL_AE_MODE);
            Log.d(TAG, "values: " + Arrays.toString(getStringValues()) + " value:" + getStringValue());
        }

        @Override
        public void setValue(String valueToSet, boolean setToCamera)
        {
            super.setValue(valueToSet,setToCamera);
            if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.off))) {
                setAeMode(AeStates.manual);
            }
            else {
                setAeMode(AeStates.auto);
            }
            fireStringValueChanged(valueToSet);
        }
    }
}
