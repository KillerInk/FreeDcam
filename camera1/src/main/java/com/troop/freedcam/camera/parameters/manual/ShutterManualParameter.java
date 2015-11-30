package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_Shutter_Changed;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class ShutterManualParameter extends BaseManualParameter
{
    /*M8 Stuff
    //M_SHUTTER_SPEED_MARKER=1/8000,1/1000,1/125,1/15,0.5,4 ???
    //return cameraController.getStringCameraParameter("shutter-threshold");
    */
    private static String TAG = "freedcam.ShutterManualParameter";
    Double Cur;
    public static String HTCShutterValues = "Auto,1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250,1/1000,1/800,1/640,1/500,1/400,1/320,1/250,1/200,1/125,1/100,1/80,1/60,1/50,1/40,1/30,1/25,1/20,1/15,1/13,1/10,1/8,1/6,1/5,1/4,0.3,0.4,0.5,0.6,0.8,1,1.3,1.6,2,2.5,3.2,4";

    public static String Z5SShutterValues = "Auto,1/90000,1/75000,1/50000,1/45000,1/30000,1/20000,1/12000,1/10000"+
            ",1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250,1/1000"+
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65"+
            ",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2"+
            ",1.0,1.2,1.4,1.5,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0"+
            ",15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0,23.0,24.0,25.0,26.0,27.0,28.0,29.0"+
            ",30.0,31.0,32.0,33.0,35.0,36.0,37.0,38.0,39.0,40.0,41.0,42.0,43.0,44,45.0,46.0"+
            ",47.0,48.0,49.0,50.0,51.0,52.0,53.0,54.0,55.0,56.0,57.0,58.0,59.0,60.0,120.0,240.0";
    public static String LGG4Values = "Auto,1/6000,1/4000,1/2000,1/1000,1/500,1/250,1/125,1/60,1/30,1/15,1/8,1/4,1/2,2,4,8,15,30";

    public static String xIMX214_IMX230 = "Auto,1/6000,1/4000,1/2000,1/1000,1/500,1/250,1/125,1/60,1/30,1/15,1/8,1/4,1/2,1/1.9,1/1.8,1/1.7,1/1.6,1/1.5,1/1.4,1";

    public static String IMX214_IMX230 = "Auto,1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250,1/1000,1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65\"+\n" +
            "            \",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2,1/1.9,1/1.8,1/1.7,1/1.6";
    public static String Mi3WValues = "Auto,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250,1/1000"+
                   ",1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65"+
                   ",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2,0.8"+
                   ",1.0,1.2,1.4,1.5,2.0";


    String shutterValues[];
    int current = 0;
    I_CameraHolder baseCameraHolder;
    I_CameraChangedListner i_cameraChangedListner;

    public ShutterManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, I_CameraHolder baseCameraHolder,I_CameraChangedListner i_cameraChangedListner, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = baseCameraHolder;
        this.i_cameraChangedListner = i_cameraChangedListner;
        if (DeviceUtils.isSonyADV())
        {
            try {
                if (!parameters.get("sony-max-shutter-speed").equals(""))
                    this.isSupported = true;
            }
            catch (NullPointerException ex)
            {
                isSupported = false;
            }
        }
        else if (DeviceUtils.isAlcatel_Idol3() || DeviceUtils.isMoto_MSM8982_8994() )
        {
            this.isSupported = true;
            shutterValues = IMX214_IMX230.split(",");
        }
        else if (DeviceUtils.isXiaomiMI3W() )
        {
            this.isSupported = true;
            shutterValues = Mi3WValues.split(",");
        }
        else if (parameters.containsKey("exposure-time") || DeviceUtils.isRedmiNote() ) {
            try {

                int min = Integer.parseInt(parameters.get("min-exposure-time"));
                int max = Integer.parseInt(parameters.get("max-exposure-time"));
                shutterValues = StringUtils.getSupportedShutterValues(min, max);
                this.isSupported = true;

            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                isSupported = false;
            }
        }

        this.setTheListener(i_shutter_changed);


    }

    private I_Shutter_Changed i_shutter_changed;

    public void setTheListener(I_Shutter_Changed i_shutter_changedx) {
        i_shutter_changed = i_shutter_changedx;

    }

    @Override
    public boolean IsSupported() {
        return super.IsSupported();
    }

    @Override
    public int GetMaxValue() {
        if (DeviceUtils.isSonyADV())
            return Integer.parseInt(parameters.get("sony-max-shutter-speed"));
        else if (shutterValues != null)
            return shutterValues.length-1;
        else if (parameters.containsKey("max-exposure-time"))
            return Integer.parseInt(parameters.get("max-exposure-time"));
        else
            return 0;
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
        if(DeviceUtils.isSonyADV())
        {
            parameters.put("sony-ae-mode", "manual");
            parameters.put("sony-shutter-speed", String.valueOf(valueToSet));

        }
        else if ( parameters.containsKey("exposure-time") || DeviceUtils.isMoto_MSM8982_8994() || DeviceUtils.isAlcatel_Idol3())
        {
            current = valueToSet;
            String shutterstring = shutterValues[current];
            if (shutterstring.contains("/")) {
                String split[] = shutterstring.split("/");
                Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
                shutterstring = "" + a;
                Cur = a;

            }
            if(!shutterValues[current].equals("Auto"))
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
        else
        {
            parameters.put("exposure-time", valueToSet + "");
            baseCameraHolder.SetCameraParameters(parameters);
        }
    }

    private void setShutterToAuto() {
        if(DeviceUtils.isAlcatel_Idol3() || DeviceUtils.isMoto_MSM8982_8994())
        {
            parameters.put("exposure-time", "0");
        }
        else if (parameters.containsKey("exposure-time"))
            parameters.put("exposure-time", 0+"");
        baseCameraHolder.SetCameraParameters(parameters);
    }

    private String setExposureTimeToParameter(String shutterstring) {
        if(DeviceUtils.isMoto_MSM8982_8994() || DeviceUtils.isAlcatel_Idol3())
        {
            try {
                parameters.put("exposure-time", String.valueOf(getMicroSec(shutterstring)));
            }
            catch (Exception ex)
            {
                System.out.println("Freedcam Manual Exposure Time Error Hal Rejected ");
            }

        }
        else if(parameters.containsKey("exposure-time")|| DeviceUtils.isXiaomiMI3W() || DeviceUtils.isRedmiNote())
        {
            shutterstring = FLOATtoSixty4(shutterstring);
            parameters.put("exposure-time", shutterstring);
        }
        baseCameraHolder.SetCameraParameters(parameters);
        return shutterstring;
    }
/* HTC M8 Value -1 = off
 *
 *  May have to use this key "non-zsl-manual-mode" set to true for raw with manual controls
 *
 *
 * Sony values Untested
 *
 */
    public Double getMicroSec(String shutterString)
    {
        Double a = Double.parseDouble(shutterString);

        return a * 1000;

    }

    public String FLOATtoSixty4(String a)
    {
        Float b =  Float.parseFloat(a);
        float c = b * 1000000;
        return String.valueOf(c);
    }


    @Override
    public String GetStringValue()
    {
        if(shutterValues != null)
            return shutterValues[current];
        else {
            try {
                return parameters.get("exposure-time");
            }
            catch (NullPointerException ex)
            {
                return "";
            }
        }
    }

    @Override
    public String[] getStringValues()
    {
        return shutterValues;
    }

    @Override
    public void RestartPreview()
    {
        //baseCameraHolder.StopPreview();
        /*if (DeviceUtils.isHTC_M8()||DeviceUtils.isZTEADV()) {
            parameters.set("zsl", "off");
            parameters.set("auto-exposure", "center-weighted");
            //parameters.set("shutter-threshold", "0.2");
        }*/
      /*  if (DeviceUtils.isZTEADV()) {
            parameters.put("slow_shutter_addition", "1");
            baseCameraHolder.SetCameraParameters(parameters);
        }*/
        /*if (DeviceUtils.isLG_G3())
        {
            parameters.put("long-shot", "on");
            baseCameraHolder.SetCameraParameters(parameters);
        }*/
        //baseCameraHolder.StartPreview();
    }
}