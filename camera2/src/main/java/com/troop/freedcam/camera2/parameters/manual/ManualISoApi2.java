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


    public ManualISoApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
        super(camParametersHandler, cameraHolder);
        currentInt = 0;
        ArrayList<String> ar = new ArrayList<>();
        try {
            for (int i = 0; i <= cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper(); i += 50) {
                if (i == 0)
                    ar.add("auto");
                else
                    ar.add(i + "");
            }
            this.stringvalues = new String[ar.size()];
            ar.toArray(stringvalues);
        }
        catch (NullPointerException ex)
        {
            this.isSupported = false;
        }
    }

    @Override
    public boolean IsVisible() {
        return true;
    }

    @Override
    public boolean IsSupported() {
        return cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE) != null;
    }


    @Override
    public void SetValue(int valueToSet)
    {
        //workaround when value was -1 to avoid outofarray ex
        if (valueToSet == -1)
            valueToSet = 0;
        //////////////////////
        currentInt = valueToSet;
        if (valueToSet == 0)
        {
            camParametersHandler.ExposureMode.SetValue("on",true);
        }
        else {
            if (!camParametersHandler.ExposureMode.GetValue().equals("off") && !firststart)
                camParametersHandler.ExposureMode.SetValue("off",true);
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, Integer.parseInt(stringvalues[valueToSet]));
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
