package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

/**
 * Created by troop on 28.04.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualISoApi2 extends ManualExposureTimeApi2 implements AbstractModeParameter.I_ModeParameterEvent
{

    public ManualISoApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
        super(camParametersHandler, cameraHolder);
    }


    @Override
    public boolean IsSupported() {
        return cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE) != null;
    }

    @Override
    public int GetMaxValue() {
        return (cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper()).intValue()/50;
    }

    @Override
    public int GetMinValue()
    {
        return -1;
        //return (cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getLower()).intValue()/50;
    }

    @Override
    public int GetValue()
    {
        if (IsSupported())
            return cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.SENSOR_SENSITIVITY).intValue()/50;
        return 0;
    }

    @Override
    public String GetStringValue()
    {
        if (GetValue() == -1)
            return "Auto";
        else
            return ""+ GetValue()*50;
    }

    @Override
    public String[] getStringValues() {
        return null;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        current = valueToSet;
        if (valueToSet == -1)
        {
            camParametersHandler.ExposureMode.SetValue("on",true);
        }
        else {
            if (!camParametersHandler.ExposureMode.GetValue().equals("off") && !firststart)
                camParametersHandler.ExposureMode.SetValue("off",true);
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, valueToSet * 50);
            try {
                cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                        null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
        firststart = false;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    //implementation I_ModeParameterEvent


    @Override
    public void onValueChanged(String val)
    {
        /*if (val.equals("off"))
        {
            canSet = true;
            BackgroundIsSetSupportedChanged(true);
        }
        else {
            canSet = false;
            BackgroundIsSetSupportedChanged(false);
        }*/
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
