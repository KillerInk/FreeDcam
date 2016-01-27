package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.parameters.IntervalDurationParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by GeorgeKiarie on 9/22/2015.
 */
public class HighFramerateVideo extends  BaseModeParameter
{
    BaseCameraHolder cameraHolder;
    CameraUiWrapper cameraUiWrapper;

    boolean hasFHD60 = false;
    boolean hasFHD90 = false;
    boolean hasFHD120 = false;

    boolean hasHD120 = false;
    boolean hasHD90 = false;
    boolean hasHD60 = false;
    boolean hasHD150 = false;
    boolean hasHD240 = false;

    boolean hasSD120 = false;
    boolean hasSD90 = false;
    boolean hasSD60 = false;
    boolean hasSD150 = false;
    boolean hasSD240 = false;
    boolean hasSD480 = false;

    StringBuilder FHD;
    StringBuilder HD;
    StringBuilder SD;

    public HighFramerateVideo(Handler handler, HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values,  CameraUiWrapper cameraUiWrapper)
    {

        super(handler,parameters, parameterChanged, value, values);

        if(DeviceUtils.isZTEADV()||DeviceUtils.isZTEADV234() ||DeviceUtils.isZTEADVIMX214() ||DeviceUtils.isMoto_MSM8974()||DeviceUtils.isMoto_MSM8982_8994()||DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W()) {
            String tmp = parameters.get("video-hfr");
            if (tmp != null && !tmp.equals("")) {

                this.values = "video-hfr-values";
                this.value = "video-hfr";
            }
            this.isSupported = true;
        }
        else
        {
            this.isSupported = true;
        }
        try {
        //   System.out.println("Kraatus90 HFR Sizes"+ parameters.get("hfr-size-values"));
       //     System.out.println("Kraatus90 HFR values" + parameters.get("video-hfr-values"));
       //    System.out.println("Kraatus90 HFR "+ parameters.get("video-hfr"));

        }
        catch (Exception ex)
        {

        }
        this.cameraHolder = parameterChanged;
        this.cameraUiWrapper = cameraUiWrapper;

        FHD = new StringBuilder();
        FHD = new StringBuilder();
        SD = new StringBuilder();



    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String[] GetValues() {

        if (!DeviceUtils.VideoFpsKnown() && parameters.containsKey("video-hfr"))

        {
            if (parameters.containsKey("video-hfr")) {

            String[] split1 = parameters.get("hfr-size-values").split(",");

            String[] split2 = parameters.get("video-hfr-values").split(",");


            int item = split1.length;

            if (item > 1) {

                for (String s : split1
                        ) {
                    int runMe = 0;
                    if (!s.equals("off")) {
                        int a = Integer.parseInt(s.split("x")[0]);

                        if (a >= 1920) {
                            switch (split2[runMe]) {
                                case "60":
                                    FHD.append(" 1080p@60 ");
                                    break;

                                case "90":
                                    FHD.append(" 1080p@90 ");
                                    break;

                                case "120":
                                    FHD.append(" 1080p@60 ");
                                    break;

                            }

                        }

                        if (a >= 1280 && a < 1919) {
                            switch (split2[0]) {
                                case "60":
                                    HD.append(" 720p@60 ");
                                    break;

                                case "90":
                                    HD.append(" 720p@90 ");
                                    break;

                                case "120":
                                    HD.append(" 720p@120 ");
                                    break;

                                case "150":
                                    HD.append(" 720p@150 ");
                                    break;
                                case "240":
                                    HD.append(" 720p@240 ");
                                    break;
                                case "480":
                                    HD.append(" 720p@480 ");
                                    break;

                            }

                        }

                        if (a >= 800 && a < 1279) {
                            switch (split2[0]) {
                                case "60":
                                    SD.append(" 480p@60 ");
                                    break;

                                case "90":
                                    SD.append(" 480p@90 ");
                                    break;

                                case "120":
                                    SD.append(" 480@120 ");
                                    break;

                                case "150":
                                    SD.append(" 480p@150 ");
                                    break;
                                case "240":
                                    SD.append(" 480p@240 ");
                                    break;
                                case "480":
                                    SD.append(" 480p@480 ");
                                    break;

                            }


                        }
                    }
                    runMe++;

                }
            }
        }

    }
        else if(DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W())
        {
            return new String[]{"Default" ,"1080p@30", "1080p@29","1080p@25","1080p@24","1080p@15","720p@120","720@60" ,"720@50","720@48", "720@30", "720p@29","720p@25","720@24","720@15"};
        }
        else if(DeviceUtils.isG4())
        {
            return new String[]{"Default" ,"1080p@90","1080p@60" ,"1080p@50","1080p@48", "1080p@30", "1080p@29","1080p@25","1080p@24","1080p@15","720p@150","720p@120","720@60" ,"720@50","720@48", "720@30", "720p@29","720p@25","720@24","720@15"};
        }
        else if(DeviceUtils.isG2()||DeviceUtils.isLG_G3()||DeviceUtils.isZTEADV())
        {
            try {
                String SizeV = cameraUiWrapper.appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE);


                if (SizeV.equals("HIGH") || SizeV.equals("1080p")) {
                    return new String[]{"Default", "1080p@60", "1080p@50", "1080p@48", "1080p@30", "1080p@29", "1080p@25", "1080p@24", "1080p@15"};
                } else if (SizeV.equals("LOW") || SizeV.equals("720p")) {
                    return new String[]{"Default", "720p@120", "720@60", "720@50", "720@48", "720@30", "720p@29", "720p@25", "720@24", "720@15"};

                } else if (SizeV.equals("4kUHD")) {
                    return new String[]{"Default", "UHDp@30", "UHD@29", "UHD@25", "UHD@24", "UHD@15"};
                }
            }
            catch (NullPointerException e)
            {
                
            }







        }

        return new String[]{"Default" , "1080p@30", "1080p@29","1080p@25","1080p@24","1080p@15","720@30", "720p@29","720p@25","720@24","720@15"};
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if(valueToSet.equals("Default") &&  parameters.containsKey("video-hfr"))
        {
            super.SetValue("off", setToCam);
        }
        else if(parameters.containsKey("video-hfr")) {
            if (Integer.parseInt(valueToSet.split("@")[1]) > 3 ) {
                if (Integer.parseInt(valueToSet.split("@")[1]) >= 60 && Integer.parseInt(valueToSet.split("@")[1]) < 90) {
                    super.SetValue("60", setToCam);


                } else if (Integer.parseInt(valueToSet.split("@")[1]) >= 90 && Integer.parseInt(valueToSet.split("@")[1]) < 120) {
                    super.SetValue("90", setToCam);
                }

                if (Integer.parseInt(valueToSet.split("@")[1]) >= 120 && Integer.parseInt(valueToSet.split("@")[1]) < 150) {
                    super.SetValue("120", setToCam);
                }

                if (Integer.parseInt(valueToSet.split("@")[1]) >= 150 && Integer.parseInt(valueToSet.split("@")[1]) < 240) {
                    super.SetValue("150", setToCam);
                }

                if (Integer.parseInt(valueToSet.split("@")[1]) >= 240 && Integer.parseInt(valueToSet.split("@")[1]) < 480) {
                    super.SetValue("240", setToCam);
                }
                if (Integer.parseInt(valueToSet.split("@")[1]) >= 480) {
                    super.SetValue("480", setToCam);
                }


            }
        }


        if (cameraUiWrapper.moduleHandler.GetCurrentModule() != null && cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_VIDEO))
            cameraUiWrapper.moduleHandler.GetCurrentModule().LoadNeededParameters();
        //baseCameraHolder.StartPreview();
    }

    @Override
    public String GetValue()
    {


            return "Default";

    }
}
