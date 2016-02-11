package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.util.Log;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 28.04.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualFocus extends ManualExposureTimeApi2 implements AbstractModeParameter.I_ModeParameterEvent
{

    int current = -1;
    public ManualFocus(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder)
    {
        super(camParametersHandler, cameraHolder);
    }

    @Override
    public int GetMaxValue() {
        return (int)(cameraHolder.characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)*10);
    }

    @Override
    public int GetMinValue() {
        return -1;
    }

    @Override
    public int GetValue() {
        return (int)(cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_FOCUS_DISTANCE)* 10);
    }

    @Override
    public String GetStringValue()
    {
        if (current == -1)
            return "Auto";
        else {
            if (isSupported)
                return StringUtils.TrimmFloatString(cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_FOCUS_DISTANCE) + "");
        }
        return "";
    }



    @Override
    public String[] getStringValues() {
        return null;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        current = valueToSet;
        if(valueToSet == 0)
        {
            camParametersHandler.FocusMode.SetValue("auto", true);
        }
        else
        {
            if (!camParametersHandler.FocusMode.GetValue().equals("off"))
                camParametersHandler.FocusMode.SetValue("off",true);
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, (float) valueToSet / 10);

            try {
                cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                        null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }


    @Override
    public boolean IsSupported()
    {
        int af[] = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        isSupported = false;
        for (int i : af)
        {
            if (i == CameraCharacteristics.CONTROL_AF_MODE_OFF)
                isSupported = true;
        }
        try {
            Log.d(TAG, "LensFocusDistance" + cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_FOCUS_DISTANCE));
        }
        catch (NullPointerException ex){};
        try {
            Log.d(TAG, "LensMinFocusDistance" + cameraHolder.characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE));
        }
        catch (NullPointerException ex){};


        if (cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_FOCUS_DISTANCE) == null
                || cameraHolder.characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE) == 0)
            isSupported = false;
        return  isSupported;
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }

    //implementation I_ModeParameterEvent

    @Override
    public boolean IsSetSupported() {
        return true;
    }
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
