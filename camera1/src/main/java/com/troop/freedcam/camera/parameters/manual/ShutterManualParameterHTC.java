package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class ShutterManualParameterHTC extends BaseManualParameter
{
    private static String TAG = "freedcam.ShutterManualParameterHTC";
    Double Cur;
    final String HTCShutterValues = "Auto,1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250,1/1000,1/800,1/640,1/500,1/400,1/320,1/250,1/200,1/125,1/100,1/80,1/60,1/50,1/40,1/30,1/25,1/20,1/15,1/13,1/10,1/8,1/6,1/5,1/4,0.3,0.4,0.5,0.6,0.8,1,1.3,1.6,2,2.5,3.2,4";

    public ShutterManualParameterHTC(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
        {
            this.isSupported = true;
            stringvalues = HTCShutterValues.split(",");
        }
    }

    @Override
    public boolean IsSupported() {
        return super.IsSupported();
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }


    @Override
    protected void setvalue(int valueToSet)
    {
        currentInt = valueToSet;
        String shutterstring = stringvalues[currentInt];
        if (shutterstring.contains("/")) {
            String split[] = shutterstring.split("/");
            Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
            shutterstring = "" + a;
            Cur = a;
        }
        if(!stringvalues[currentInt].equals("Auto"))
        {
            try {
                shutterstring = setExposureTimeToParameter(shutterstring);
            }
            catch (Exception ex)
            {
                Log.d("Freedcam","Shutter Set FAil");
            }
        }
        else
        {
            setShutterToAuto();
        }
        Log.e(TAG, shutterstring);
    }

    private void setShutterToAuto() {
        parameters.put("shutter", "-1");
        camParametersHandler.SetParametersToCamera();
    }

    private String setExposureTimeToParameter(String shutterstring)
    {
        shutterstring = String.format("%01.6f", Float.parseFloat(shutterstring));
        parameters.put("shutter", shutterstring);
        camParametersHandler.SetParametersToCamera();
        return shutterstring;
    }
}