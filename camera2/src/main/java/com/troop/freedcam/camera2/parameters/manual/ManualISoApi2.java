package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

import java.util.ArrayList;

/**
 * Created by troop on 28.04.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualISoApi2 extends ManualExposureTimeApi2 implements AbstractModeParameter.I_ModeParameterEvent
{

    String[] isovals;

    public ManualISoApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
        super(camParametersHandler, cameraHolder);
        current = 0;
        ArrayList<String> ar = new ArrayList<>();
        for (int i = 0; i<= cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper(); i+=50)
        {
            if (i == 0)
                ar.add("auto");
            else
                ar.add(i+"");
        }
        isovals = new String[ar.size()];
        ar.toArray(isovals);
    }


    @Override
    public boolean IsSupported() {
        return cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE) != null;
    }

    @Override
    public int GetMaxValue() {
        return isovals.length-1;
    }

    @Override
    public int GetMinValue()
    {
        return 0;
        //return (cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getLower()).intValue()/50;
    }

    @Override
    public int GetValue()
    {
        return current;
    }

    @Override
    public String GetStringValue()
    {
        return isovals[current];
    }

    @Override
    public String[] getStringValues() {
        return isovals;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        current = valueToSet;
        if (valueToSet == 0)
        {
            camParametersHandler.ExposureMode.SetValue("on",true);
        }
        else {
            if (!camParametersHandler.ExposureMode.GetValue().equals("off") && !firststart)
                camParametersHandler.ExposureMode.SetValue("off",true);
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, Integer.parseInt(isovals[valueToSet]));
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
