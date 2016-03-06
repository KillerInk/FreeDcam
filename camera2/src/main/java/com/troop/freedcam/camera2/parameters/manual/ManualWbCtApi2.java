package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.hardware.camera2.params.RggbChannelVector;
import android.os.Build;
import android.util.Log;

import com.troop.androiddng.Matrixes;
import com.troop.filelogger.Logger;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

import java.util.HashMap;

/**
 * Created by Ingo on 01.05.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualWbCtApi2  extends  AbstractManualParameter implements AbstractModeParameter.I_ModeParameterEvent
{
    public ColorSpaceTransform colorSpaceTransform;
    public RggbChannelVector rggbChannelVector;
    private RggbChannelVector wbChannelVector;
    boolean isSupported = false;
    BaseCameraHolderApi2 cameraHolder;
    boolean canSet = false;
    private HashMap<String, int[]> cctLookup;

    final String TAG = ManualWbCtApi2.class.getSimpleName();

    public ManualWbCtApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
        super(camParametersHandler);
        this.cameraHolder = cameraHolder;
        stringvalues = createStringArray(1500,10000,100);
        cctLookup = Matrixes.RGB_CCT_LIST;
        currentInt = 0;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int GetValue()
    {
        return currentInt;
    }

    @Override
    public String GetStringValue()
    {
        if (stringvalues != null)
            return stringvalues[currentInt];
        return 0+"";
    }


    //rgb(255,108, 0)   1500k
    //rgb 255,255,255   6000k
    //rgb(181,205, 255) 15000k
    @Override
    public void SetValue(int valueToSet)
    {
        if (valueToSet == 0)
            return;
        currentInt =valueToSet;
        valueToSet = Integer.parseInt(stringvalues[valueToSet]);
        int[] rgb = cctLookup.get(valueToSet+"");
        if (rgb == null)
        {
            Logger.d(TAG, "get cct from lookup failed:" + valueToSet);
            return;
        }
        float rf,gf,bf = 0;

        rf = (float)getRGBToDouble(rgb[0]);
        gf = (float)getRGBToDouble(rgb[1])/2;//we have two green channels
        bf = (float)getRGBToDouble(rgb[2]);
        rf = rf/gf;
        bf = bf/gf;
        gf = 1;

        Logger.d(TAG, "r:" +rgb[0] +" g:"+rgb[1] +" b:"+rgb[2]);
        Logger.d(TAG, "ColorTemp=" + valueToSet + " WBCT = r:" +rf +" g:"+gf +" b:"+bf);
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
    public boolean IsVisible() {
        return isSupported;
    }

    @Override
    public boolean IsSupported() {
        isSupported = camParametersHandler.WhiteBalanceMode.GetValue().equals("OFF");
        return isSupported;
    }

    @Override
    public void onValueChanged(String val)
    {
        if (camParametersHandler.WhiteBalanceMode.GetValue().equals("OFF"))
        {
            canSet = true;
            BackgroundIsSetSupportedChanged(true);
            SetValue(currentInt);
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

    @Override
    public void onVisibilityChanged(boolean visible) {

    }

    private int getCctFromRGB(int R, int G, int B)
    {
        double n=((0.23881)*R+(0.25499)*G+(-0.58291)*B)/((0.11109)*R+(-0.85406)*G+(0.52289)*B);
        int CCT=(int)(449*Math.pow(n,3)+3525*Math.pow(n,2)+Math.pow(n,6823.3)+5520.33);
        return CCT;
    }
}
