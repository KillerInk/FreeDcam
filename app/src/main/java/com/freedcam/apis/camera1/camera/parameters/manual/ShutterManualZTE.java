package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 25.11.2015.
 */
public class ShutterManualZTE extends BaseManualParameter
{
    private I_CameraHolder baseCameraHolder;
    private final String TAG = ShutterManualZTE.class.getSimpleName();

    final String Z5SShutterValues = "Auto,1/90000,1/75000,1/50000,1/45000,1/30000,1/20000,1/12000,1/10000"+
            ",1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250,1/1000"+
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65"+
            ",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2"+
            ",1.0,1.2,1.4,1.5,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0"+
            ",15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0,23.0,24.0,25.0,26.0,27.0,28.0,29.0"+
            ",30.0,31.0";

    private final String Z7ShutterValues = "Auto,1/90000,1/75000,1/50000,1/45000,1/30000,1/20000,1/12000,1/10000"+
            ",1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250,1/1000"+
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65"+
            ",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2"+
            ",1.0,1.2,1.4,1.5,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0"+
            ",15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0,23.0,24.0,25.0,26.0,27.0,28.0,29.0"+
            ",30.0,31.0,32.0,33.0,35.0,36.0,37.0,38.0,39.0,40.0,41.0,42.0,43.0,44,45.0,46.0"+
            ",47.0,48.0,49.0,50.0,51.0,52.0,53.0,54.0,55.0,56.0,57.0,58.0,59.0,60.0,120.0,240.0";

    /**
     * @param parameters
     * @param camParametersHandler
     */
    public ShutterManualZTE(Camera.Parameters parameters, I_CameraHolder baseCameraHolder, CamParametersHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);
        this.baseCameraHolder = baseCameraHolder;
        stringvalues = Z7ShutterValues.split(",");

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
            shutterstring = "" + a;
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
            setShutterToAuto();
        }
        Logger.e(TAG, shutterstring);
    }

    private void setShutterToAuto()
    {
        try
        {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {
                    camParametersHandler.SetZTESlowShutter();
                    baseCameraHolder.StopPreview();
                    baseCameraHolder.StartPreview();
                }
            };
            handler.postDelayed(r, 1);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }

    }

    private String setExposureTimeToParameter(final String shutterstring)
    {
        try {

            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {

                    parameters.set("slow_shutter", shutterstring);
                    parameters.set("slow_shutter_addition", "1");
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
