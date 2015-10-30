package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.util.Log;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 06.03.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualExposureTimeApi2 extends AbstractManualParameter implements AbstractModeParameter.I_ModeParameterEvent
{
    ParameterHandlerApi2 camParametersHandler;
    BaseCameraHolderApi2 cameraHolder;
    boolean canSet = false;
    private boolean isSupported = false;
    String usedShutterValues[];
    final String TAG = ManualExposureTimeApi2.class.getSimpleName();

    public ManualExposureTimeApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
        super(camParametersHandler);
        this.cameraHolder = cameraHolder;
        this.camParametersHandler = camParametersHandler;
        try {
            findMinMaxValue();
        }
        catch (NullPointerException ex)
        {
            this.isSupported = false;
        }
    }

    int current = 0;

    private void findMinMaxValue()
    {
        int millimax = 0;
        Log.d(TAG, "max exposuretime:" + cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper());
        Log.d(TAG, "min exposuretime:" + cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower());
        //866 975 130 = 0,8sec
        if (DeviceUtils.isG4())
            millimax = 30000000;
        else if (DeviceUtils.isSamsung_S6_edge_plus())
            millimax = 10000000;
        else
            millimax = (cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper()).intValue() / 1000;
        int millimin = (cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower()).intValue() / 1000;
        usedShutterValues = StringUtils.getSupportedShutterValues(millimin, millimax);
    }

    @Override
    public int GetMaxValue()
    {
        return usedShutterValues.length-1;
    }

    @Override
    public int GetMinValue()
    {
        return 0;
    }

    @Override
    public int GetValue()
    {

        return current;
    }

    @Override
    public String GetStringValue()
    {

        return usedShutterValues[current];

    }


    @Override
    public String[] getStringValues() {
        return usedShutterValues;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void SetValue(int valueToSet)
    {
        current = valueToSet;
        long val = (long)(StringUtils.getMilliSecondStringFromShutterString(usedShutterValues[valueToSet]) * 1000f);
        if (val > 500000000)
            val = 500000000;
        cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, val);
        try {
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        catch (NullPointerException ex){ex.printStackTrace();}
    }

    @Override
    public boolean IsSupported()
    {
        this.isSupported = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE) != null;
        return isSupported;
    }

    @Override
    public boolean IsSetSupported() {
        return canSet;
    }

    //implementation I_ModeParameterEvent


    @Override
    public void onValueChanged(String val)
    {
        if (val.equals("off"))
        {
            canSet = true;
            BackgroundIsSetSupportedChanged(true);
        }
        else {
            canSet = false;
            BackgroundIsSetSupportedChanged(false);
        }
    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    //implementation I_ModeParameterEvent END

}
