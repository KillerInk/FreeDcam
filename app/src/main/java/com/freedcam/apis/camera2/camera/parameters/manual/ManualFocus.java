package com.freedcam.apis.camera2.camera.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera2.camera.CameraHolderApi2;
import com.freedcam.apis.camera2.camera.parameters.ParameterHandlerApi2;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

/**
 * Created by troop on 28.04.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualFocus extends AbstractManualParameter
{
    private final String TAG = ManualFocus.class.getSimpleName();
    private CameraHolderApi2 cameraHolder;
    public ManualFocus(ParameterHandlerApi2 camParametersHandler, CameraHolderApi2 cameraHolder)
    {
        super(camParametersHandler);
        this.cameraHolder =cameraHolder;
        try {
            int max = (int)(cameraHolder.characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)*10);
            stringvalues = createStringArray(0, max,1);
            currentInt = -1;
        }
        catch (NullPointerException ex)
        {
            Logger.exception(ex);
        }

    }

    @Override
    public int GetValue() {
        return (int)(cameraHolder.get(CaptureRequest.LENS_FOCUS_DISTANCE)* 10);
    }

    @Override
    public String GetStringValue()
    {
        if (currentInt == -1)
            return "Auto";
        else {
            if (isSupported)
                return StringUtils.TrimmFloatString4Places(cameraHolder.get(CaptureRequest.LENS_FOCUS_DISTANCE) + "");
        }
        return "";
    }


    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        if(valueToSet == 0)
        {
            camParametersHandler.FocusMode.SetValue("auto", true);
        }
        else
        {
            if (!camParametersHandler.FocusMode.GetValue().equals("off"))
                camParametersHandler.FocusMode.SetValue("off",true);
            cameraHolder.SetParameterRepeating(CaptureRequest.LENS_FOCUS_DISTANCE, (float) valueToSet / 10);
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
            Logger.d(TAG, "LensFocusDistance" + cameraHolder.get(CaptureRequest.LENS_FOCUS_DISTANCE));
        }
        catch (NullPointerException ex){Logger.exception(ex);}
        try {
            Logger.d(TAG, "LensMinFocusDistance" + cameraHolder.characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE));
        }
        catch (NullPointerException ex){Logger.exception(ex);}


        if (cameraHolder.get(CaptureRequest.LENS_FOCUS_DISTANCE) == null
                || cameraHolder.characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE) == 0)
            isSupported = false;
        return  isSupported;
    }

    @Override
    public boolean IsVisible() {
        return isSupported;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }



}
