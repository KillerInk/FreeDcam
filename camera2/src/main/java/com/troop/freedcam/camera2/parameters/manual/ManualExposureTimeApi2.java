package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

    public static String ShutterValues = "Auto,1/90000,1/75000,1/50000,1/45000,1/30000,1/20000,1/12000,1/10000"+
            ",1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250,1/1000"+
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65"+
            ",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2,0.8"+
            ",1.0,1.2,1.4,1.5,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0"+
            ",15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0,23.0,24.0,25.0,26.0,27.0,28.0,29.0"+
            ",30.0,31.0,32.0,33.0,35.0,36.0,37.0,38.0,39.0,40.0,41.0,42.0,43.0,44,45.0,46.0"+
            ",47.0,48.0,49.0,50.0,51.0,52.0,53.0,54.0,55.0,56.0,57.0,58.0,59.0,60.0,120.0,240.0";

    String shutterValues[];
    String usedShutterValues[];

    public ManualExposureTimeApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
        super(camParametersHandler);
        this.cameraHolder = cameraHolder;
        this.camParametersHandler = camParametersHandler;
        shutterValues = ShutterValues.split(",");
        try {
            findMinMaxValue();
        }
        catch (NullPointerException ex)
        {
            this.isSupported = false;
        }

    }

    int current = 0;
    int min = 0;
    int max =0;

    private void findMinMaxValue()
    {
        int millimax = (cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper()).intValue() / 1000;
        int millimin = (cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower()).intValue() / 1000;
        boolean foundmin = false, foundmax = false;
        ArrayList<String> tmp = new ArrayList<>();
        for (int i = 1; i< shutterValues.length; i++ )
        {
            String s = shutterValues[i];

            float a;
            if (s.contains("/")) {
                String split[] = s.split("/");
                a =(Float.parseFloat(split[0]) / Float.parseFloat(split[1])*1000000f);
            }
            else
                a = (Float.parseFloat(s)*1000000f);

            if (a>= millimin && a <= millimax)
                tmp.add(s);
            if (a >= millimin && !foundmin)
            {
                foundmin = true;
                min = i;
            }
            if (a > millimax && !foundmax)
            {
                foundmax = true;
                max = i-1;
            }
            if (foundmax && foundmin)
                usedShutterValues = tmp.toArray(new String[tmp.size()]);
        }
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



    public String getSECONDSasString (long time)
    {
        double mili = time /1000000  ;
        double sec =  mili / 1000;
        return sec +"";
    }

    public int getMilliSECONDS (long time)
    {
        double mili = time /1000000  ;

        return (int)mili;
    }

    @Override
    public String[] getStringValues() {
        return usedShutterValues;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void SetValue(int valueToSet)
    {
        String s = usedShutterValues[valueToSet];
        current = valueToSet;
        float a;
        if (s.contains("/")) {
            String split[] = s.split("/");
            a =(Float.parseFloat(split[0]) / Float.parseFloat(split[1])*1000000f);
        }
        else
            a = (Float.parseFloat(s)*1000000f);
        cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, (long)(a * 1000f));
        try {
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
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
