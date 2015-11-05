package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.hardware.camera2.params.RggbChannelVector;
import android.os.Build;
import android.util.Log;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

/**
 * Created by Ingo on 01.05.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualWbCtApi2  extends  AbstractManualParameter implements AbstractModeParameter.I_ModeParameterEvent
{
    int current = 5000;
    public ColorSpaceTransform colorSpaceTransform;
    public RggbChannelVector rggbChannelVector;
    private RggbChannelVector wbChannelVector;
    boolean isSupported = false;
    BaseCameraHolderApi2 cameraHolder;
    boolean canSet = false;

    final String TAG = ManualWbCtApi2.class.getSimpleName();

    public ManualWbCtApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
        super(camParametersHandler);
        this.cameraHolder = cameraHolder;
    }

    @Override
    public int GetMaxValue() {
        return 100;
    }

    @Override
    public int GetMinValue() {
        return 15;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int GetValue()
    {
        return current/100;
    }

    @Override
    public String GetStringValue() {
        return (current /100) +"K";
    }

    @Override
    public String[] getStringValues() {
        return null;
    }


    //rgb(255,108, 0)   1500k
    //rgb 255,255,255   6000k
    //rgb(181,205, 255) 15000k
    @Override
    public void SetValue(int valueToSet)
    {
        valueToSet = valueToSet*100;
        current =valueToSet;
        //code is based on http://www.tannerhelland.com/4435/convert-temperature-rgb-algorithm-code/
        double r,g,b;
        double tmpcol = 0;
        double colortemp = valueToSet / 100;
        //red

        if( colortemp <= 66 )
        {
            r = 255;
            g = colortemp;
            g = 99.4708025861 * Math.log(g) - 161.1195681661;
            if( colortemp <= 19)
            {
                b = 0;
            }
            else
            {
                b = colortemp-10;
                b = 138.5177312231 * Math.log(b) - 305.0447927307;
            }
        }
        else
        {
            r = colortemp - 60;
            r = 329.698727446 * Math.pow(r, -0.1332047592);
            g = colortemp - 60;
            g = 288.1221695283 * Math.pow(g, -0.0755148492 );
            b = 255;
        }

        float rf,gf,bf = 0;

        rf = (float)getRGBToDouble(checkminmax((int)r));
        gf = (float)getRGBToDouble(checkminmax((int)g))/2;
        bf = (float)getRGBToDouble(checkminmax((int)b));

        Log.d(TAG, "r:" +r +" g:"+g +" b:"+b);
        Log.d(TAG, "ColorTemp=" + colortemp + " WBCT = r:" +rf +" g:"+gf +" b:"+bf);
        wbChannelVector =  new RggbChannelVector(rf,gf,gf,bf);
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, wbChannelVector);
        try {
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }

    }

    private int checkminmax(int val)
    {
        if (val>255)
            return 255;
        else if(val < 0)
            return 0;
        else return val;
    }

    private double getRGBToDouble(int color)
    {
        double t = color;
        t = t * 3 *2;
        t = t / (255);
        t = t / 3;
        t += 1;

        return t;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public boolean IsSupported() {
        isSupported = camParametersHandler.ColorCorrectionMode.GetValue() != null && camParametersHandler.ColorCorrectionMode.GetValue().equals("TRANSFORM_MATRIX") && camParametersHandler.WhiteBalanceMode.GetValue().equals("OFF");
        return isSupported;
    }

    @Override
    public void onValueChanged(String val)
    {
        if (camParametersHandler.ColorCorrectionMode.GetValue() != null && camParametersHandler.ColorCorrectionMode.GetValue().equals("TRANSFORM_MATRIX") && camParametersHandler.WhiteBalanceMode.GetValue().equals("OFF"))
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

    private int getCctFromRGB(int R, int G, int B)
    {
        double n=((0.23881)*R+(0.25499)*G+(-0.58291)*B)/((0.11109)*R+(-0.85406)*G+(0.52289)*B);
        int CCT=(int)(449*Math.pow(n,3)+3525*Math.pow(n,2)+Math.pow(n,6823.3)+5520.33);
        return CCT;
    }
}
