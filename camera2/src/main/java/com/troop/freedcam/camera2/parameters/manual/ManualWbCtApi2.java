package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.hardware.camera2.params.RggbChannelVector;
import android.os.Build;
import android.util.Log;
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
    int current = 5000;
    public ColorSpaceTransform colorSpaceTransform;
    public RggbChannelVector rggbChannelVector;
    boolean isSupported = false;

    final String TAG = ManualWbCtApi2.class.getSimpleName();

    public ManualWbCtApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
        super(camParametersHandler, cameraHolder);

    }

    @Override
    public int GetMaxValue() {
        return 16000;
    }

    @Override
    public int GetMinValue() {
        return 1500;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int GetValue()
    {


        return current;
    }

    @Override
    public String GetStringValue() {
        return (current) +"K";
    }



    @Override
    public void SetValue(int valueToSet)
    {
        current =valueToSet;
        //code is based on http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
        int r,g,b;
        double tmpcol = 0;
        double colortemp = valueToSet / 100;
        //red
        if(colortemp <= 66)
            r = 255;
        else
        {
            tmpcol = colortemp -60;
            tmpcol = Math.pow(tmpcol, -0.1332047592);
            r = (int)(329.698727446 * tmpcol);
            if (r < 0)
                r = 0;
            if (r > 255)
                r = 255;
        }
        Log.d(TAG,"tmpcol red:" + tmpcol+ " colortemp:" + colortemp);

        //green
        if (colortemp <= 66)
        {
            tmpcol = 99.4708025861 * Math.log(colortemp) - 161.1195681661;
            g = (int)tmpcol;
        }
        else
        {
            tmpcol = colortemp - 60;
            tmpcol = Math.pow(tmpcol,-0.0755148492);
            tmpcol = 288.1221695283 * tmpcol;
            g = (int)tmpcol;
        }
        Log.d(TAG,"tmpcol green:" + tmpcol+ " colortemp:" + colortemp);
        if (g < 0)
            g = 0;
        if (g > 255)
            g = 255;
        //blue
        if (colortemp >= 66)
            b = 255;
        else if (colortemp <= 19)
            b = 0;
        else
        {
            tmpcol = colortemp -10;
            tmpcol = 138.5177312231 * Math.log(tmpcol) - 305.0447927307;
            b = (int)tmpcol;

            if (b <0 )
                b = 0;
            if (b > 255)
                b = 255;
        }
        Log.d(TAG,"tmpcol blue:" + tmpcol+ " colortemp:" + colortemp);


        float rf,gf,bf = 0;
        float mu = 255f /1000f;
        rf = (float)r /100f * mu;
        gf = (float)g /100f * mu /2;
        bf = (float)b/100f *mu;

        Log.d(TAG, "ColorTemp=" + colortemp + " WBCT = r:" +rf +" g:"+gf +" b:"+bf);
        rggbChannelVector =  new RggbChannelVector(rf,gf,gf,bf);
        /*float i = (float)valueToSet/100;

            rggbChannelVector =  new RggbChannelVector(
                    1.0f + i,
                    1.0f  ,
                    1.0f ,
                    1.0f +i);*/
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, rggbChannelVector);
        try {
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public void onValueChanged(String val)
    {
        if (val.equals("TRANSFORM_MATRIX"))
        {
            canSet = true;
            BackgroundIsSetSupportedChanged(true);
            isSupported = true;
            BackgroundIsSupportedChanged(true);
        }
        else {
            canSet = false;
            BackgroundIsSetSupportedChanged(false);
            isSupported = false;
            BackgroundIsSupportedChanged(false);
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
}
