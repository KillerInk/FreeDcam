package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_Shutter_Changed;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 25.11.2015.
 */
public class ShutterManualZTE extends BaseManualParameter
{
    I_CameraHolder baseCameraHolder;
    I_CameraChangedListner i_cameraChangedListner;
    String[] shutterValues;
    int current =0;
    final String TAG = ShutterManualZTE.class.getSimpleName();

    private I_Shutter_Changed i_shutter_changed;

    public void setTheListener(I_Shutter_Changed i_shutter_changedx) {
        i_shutter_changed = i_shutter_changedx;

    }

    /**
     * @param parameters
     * @param value
     * @param maxValue
     * @param MinValue
     * @param camParametersHandler
     */
    public ShutterManualZTE(HashMap<String, String> parameters, String value, String maxValue, String MinValue, I_CameraHolder baseCameraHolder, I_CameraChangedListner i_cameraChangedListner, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        this.baseCameraHolder = baseCameraHolder;
        this.i_cameraChangedListner = i_cameraChangedListner;
        shutterValues = ShutterManualParameter.Z5SShutterValues.split(",");
    }

    @Override
    public boolean IsSupported() {
        return super.IsSupported();
    }

    @Override
    public int GetMaxValue()
    {
        return shutterValues.length-1;
    }

    @Override
    public int GetMinValue() {
        if (DeviceUtils.isSonyADV())
            return Integer.parseInt(parameters.get("sony-min-shutter-speed"));
        else if (shutterValues != null)
            return 0;
        else if(parameters.containsKey("min-exposure-time") && (!DeviceUtils.isMoto_MSM8982_8994() || !DeviceUtils.isAlcatel_Idol3()))
            return Integer.parseInt(parameters.get("min-exposure-time"));
        else
            return 0;
    }

    @Override
    public int GetValue() {
        return current;
    }

    @Override
    protected void setvalue(int valueToSet)
    {
        current = valueToSet;
        String shutterstring = shutterValues[current];
        if (shutterstring.contains("/")) {
            String split[] = shutterstring.split("/");
            Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
            shutterstring = "" + a;


        }
        if(!shutterValues[current].equals("Auto"))
        {
            try {
                shutterstring = setExposureTimeToParameter(shutterstring);
            }
            catch (Exception ex)
            {
                Log.d("Freedcam", "Shutter Set FAil");
            }
        }
        else
        {
            setShutterToAuto();
        }
        Log.e(TAG, shutterstring);
    }

    private void setShutterToAuto() {

        parameters.put("slow_shutter", "-1");
        parameters.put("slow_shutter_addition", "0");
        baseCameraHolder.SetCameraParameters(parameters);
    }

    private String setExposureTimeToParameter(String shutterstring) {

        parameters.put("slow_shutter", shutterstring);
        parameters.put("slow_shutter_addition", "1");
        if (i_shutter_changed != null) {
            i_shutter_changed.PreviewWasRestarted();
        }
        baseCameraHolder.StopPreview();
        baseCameraHolder.StartPreview();
        i_cameraChangedListner.onPreviewOpen("restart");
        baseCameraHolder.SetCameraParameters(parameters);
        return shutterstring;
    }
}
