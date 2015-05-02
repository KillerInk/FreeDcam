package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.hardware.camera2.params.RggbChannelVector;
import android.os.Build;
import android.util.Rational;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Ingo on 01.05.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualWbCtApi2  extends ManualExposureTimeApi2
{
    int current = 50;
    public ColorSpaceTransform colorSpaceTransform;
    public RggbChannelVector rggbChannelVector;

    public ManualWbCtApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
        super(camParametersHandler, cameraHolder);
    }

    @Override
    public int GetMaxValue() {
        return 200;
    }

    @Override
    public int GetMinValue() {
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int GetValue()
    {


        return current;
    }

    @Override
    public String GetStringValue() {
        return "";
    }


    @Override
    public void SetValue(int valueToSet)
    {
        float i = (float)valueToSet/100;

            rggbChannelVector =  new RggbChannelVector(
                    1.0f + i,
                    1.0f  ,
                    1.0f ,
                    1.0f +i);
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, rggbChannelVector);
            cameraHolder.setIntKeyToCam(CaptureRequest.CONTROL_AWB_MODE,CaptureRequest.CONTROL_AWB_MODE_OFF);

        /*int[] rationalArray = new int[18];
        if (colorSpaceTransform != null)
        {
            int ar = 0;
            for (int row = 0; row < 3; row++)
            {
                for (int col = 0; col < 3;col ++)
                {
                    Rational rational = colorSpaceTransform.getElement(col, row);
                    int a = (int)(rational.getNumerator() * i);
                    int b = (int)(rational.getDenominator() * i);
                    rationalArray[ar++]= a;
                    rationalArray[ar++]= b;
                }
            }
            ColorSpaceTransform cst = new ColorSpaceTransform(rationalArray);
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.COLOR_CORRECTION_TRANSFORM,cst);
        }*/

    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void onValueChanged(String val) {

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
}
