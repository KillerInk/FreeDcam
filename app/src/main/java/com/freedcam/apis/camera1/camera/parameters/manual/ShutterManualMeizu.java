package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.utils.Logger;

/**
 * Created by GeorgeKiarie on 6/3/2016.
 */
public class ShutterManualMeizu extends BaseManualParameter
{
    private I_CameraHolder baseCameraHolder;
    private final String TAG = ShutterManualMeizu.class.getSimpleName();

    private final String MX4Shutter ="Auto,1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250,1/1000"+
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65"+
            ",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5";

    /**
     * @param parameters
     * @param camParametersHandler
     */
    public ShutterManualMeizu(Camera.Parameters parameters, I_CameraHolder baseCameraHolder, CamParametersHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);
        this.baseCameraHolder = baseCameraHolder;

            stringvalues = MX4Shutter.split(",");

        this.isSupported = true;
    }

    @Override
    public boolean IsSupported() {
        return super.IsSupported();
    }

    @Override
    public boolean IsVisible() {
        return  IsSupported();
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        String shutterstring = stringvalues[currentInt];
        if (shutterstring.contains("/")) {
            String split[] = shutterstring.split("/");
            Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
            shutterstring = "" + a*1000000;
        }
        if(!stringvalues[currentInt].equals("Auto"))
        {
            try {
                shutterstring = setExposureTimeToParameter(shutterstring);
            }
            catch (Exception ex)
            {
                Logger.d("Freedcam", "Shutter Set FAil");
            }
        }
        else
        {
            shutterstring = setExposureTimeToParameter("0");
        }
        Logger.e(TAG, shutterstring);
    }



    private String setExposureTimeToParameter(final String shutterstring)
    {
        try {

            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {

                    parameters.set("shutter-value", shutterstring);
                    camParametersHandler.SetParametersToCamera(parameters);

                    if(Double.parseDouble(shutterstring) <= 0.5 && Double.parseDouble(shutterstring) >= 0.0005 ){
                        baseCameraHolder.StopPreview();
                        baseCameraHolder.StartPreview();
                    }
                }
            };
            handler.post(r);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
        return shutterstring;
    }
}