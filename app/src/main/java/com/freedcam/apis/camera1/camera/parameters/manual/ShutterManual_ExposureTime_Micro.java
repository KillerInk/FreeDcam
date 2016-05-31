package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualShutter;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterManual_ExposureTime_Micro extends AbstractManualShutter
{
    private final String TAG = ShutterManual_ExposureTime_Micro.class.getSimpleName();

    protected Camera.Parameters  parameters;
    /*
     * The name of the current value to get like brightness
     */
    protected String value;

    /**
     * The name of the current value to get like brightness-max
     */
    protected String max_value;
    /**
     * The name of the current value to get like brightness-min
     */
    protected String  min_value;

    protected CamParametersHandler camParametersHandler;

    /**
     * @param parameters
     * @param camParametersHandler
     */
    public ShutterManual_ExposureTime_Micro(Camera.Parameters parameters, CamParametersHandler camParametersHandler, String[] shuttervalues, String value, String maxval , String minval ) {
        super(camParametersHandler);
        this.camParametersHandler = camParametersHandler;
        this.parameters = parameters;
        this.value = value;
        this.max_value = maxval;
        this.min_value = minval;
        try {
            if (shuttervalues == null)
            {
                Logger.d(TAG, "minexpo = "+parameters.get(min_value) + " maxexpo = " + parameters.get(max_value));
                int min,max;
                if (!parameters.get(min_value).contains("."))
                {
                    Logger.d(TAG, "Micro does not contain . load int");
                    min = Integer.parseInt(parameters.get(min_value));
                    max = Integer.parseInt(parameters.get(max_value));
                    Logger.d(TAG, "min converterd = "+min + " max converterd = " + max);
                }
                else
                {
                    Logger.d(TAG, "Micro contain .  *1000");
                    double tmpMin = Double.parseDouble(parameters.get(min_value))*1000;
                    double tmpMax = Double.parseDouble(parameters.get(max_value))*1000;
                    min = (int)tmpMin;
                    max = (int)tmpMax;
                    Logger.d(TAG, "min converterd = "+min + " max converterd = " + max);

                }
                stringvalues = getSupportedShutterValues(min, max, true);

            }
            else
                stringvalues = shuttervalues;
            parameters.set(value, "0");
            this.isSupported = true;

        } catch (NumberFormatException ex) {
            Logger.exception(ex);
            isSupported = false;
        }
        Logger.d(TAG, "isSupported:" +isSupported);
    }

    @Override
    public boolean IsSetSupported() {
        return isSupported;
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public void SetValue(int valueToset)
    {
        currentInt = valueToset;
        if(!stringvalues[currentInt].equals("Auto"))
        {
            String shutterstring = FormatShutterStringToDouble(stringvalues[currentInt]);
            Logger.d(TAG, "StringUtils.FormatShutterStringToDouble:" + shutterstring);
            shutterstring = getMicroSecFromMilliseconds(shutterstring);
            Logger.d(TAG, " StringUtils.getMicroSecFromMilliseconds"+ shutterstring);
            parameters.set(value, shutterstring);
        }
        else
        {
            parameters.set(value, "0");
            Logger.d(TAG, "set exposure time to auto");
        }
        camParametersHandler.SetParametersToCamera(parameters);
    }
}
