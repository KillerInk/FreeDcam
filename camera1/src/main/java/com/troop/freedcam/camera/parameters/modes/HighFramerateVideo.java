package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.parameters.IntervalDurationParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by GeorgeKiarie on 9/22/2015.
 */
public class HighFramerateVideo extends  BaseModeParameter
{
    BaseCameraHolder cameraHolder;
    CameraUiWrapper cameraUiWrapper;
    static final String TAG ="Video FPS Class";
    boolean FpsTriggered = false;
    String FpEss = "";



    private List<String> FHD;
    private List<String>  HD;
    private List<String>  SD;

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

        this.cameraHolder = parameterChanged;
        this.cameraUiWrapper = cameraUiWrapper;

        FHD = new ArrayList<>();
        HD = new ArrayList<>();
        SD = new ArrayList<>();



    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String[] GetValues() {

        if(!DeviceUtils.isLGFrameWork() && parameters.get("hfr-size-values").split(",").length >= 1)
        {
            String[] split1 = parameters.get("hfr-size-values").split(",");
            String[] split2 = parameters.get("video-hfr-values").split(",");
            List<String> Trimmed = new ArrayList<>(Arrays.asList(split2));
            Trimmed.remove("off");
            if (!Trimmed.isEmpty())
            {
                for (String s:Trimmed)
                {
                    Log.e(TAG,"Index :"+Trimmed.indexOf(s)+" "+ s);
                    if(Integer.parseInt(split1[Trimmed.indexOf(s)].split("x")[0]) >= 1920)
                    {
                        switch (s) {
                            case "60":
                                FHD.add("1080p@60");
                                break;

                            case "90":
                                FHD.add("1080p@90");
                                break;

                            case "120":
                                FHD.add("1080p@120");
                                break;

                        }
                    }
                    else if(Integer.parseInt(split1[Trimmed.indexOf(s)].split("x")[0]) >= 1280 && Integer.parseInt(split1[Trimmed.indexOf(s)].split("x")[0]) < 1919)
                    {
                        switch (s) {
                            case "60":
                                HD.add("720p@60");
                                break;

                            case "90":
                                HD.add("720p@90");
                                break;

                            case "120":
                                HD.add("720p@120");
                                break;

                            case "150":
                                HD.add("720p@150");
                                break;
                            case "240":
                                HD.add("720p@240");
                                break;
                            case "480":
                                HD.add("720p@480");
                                break;

                        }
                    }

                }
            }
            try {
                String SizeV = cameraUiWrapper.appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE);

                String[] f1080p = {"Default", "1080p@120","1080p@90", "1080p@60", "1080p@50", "1080p@48", "1080p@30", "1080p@29", "1080p@25", "1080p@24", "1080p@15"};
                String[] f720p = {"Default","720p@480", "720p@240","720p@150","720p@120", "720p@60", "720p@50", "720p@48", "720p@30", "720p@29", "720p@25", "720p@24", "720@15"};




                List<String> DynamicFHD = new ArrayList<>(Arrays.asList(f1080p));
                List<String> DynamicHD = new ArrayList<>(Arrays.asList(f720p));


                if (SizeV.equals("HIGH") || SizeV.equals("1080p")) {


                    if(FHD.contains("1080p@120") && !FHD.isEmpty())
                    {

                        return DynamicFHD.toArray(new String[DynamicFHD.size()]);
                    }
                    else if(FHD.contains("1080p@90") && !FHD.isEmpty())
                    {
                        DynamicFHD.remove("1080p@120");
                        return DynamicFHD.toArray(new String[DynamicFHD.size()]);
                    }
                    else if(FHD.contains("1080p@60") && !FHD.isEmpty())
                    {
                        DynamicFHD.remove("1080p@120");
                        DynamicFHD.remove("1080p@90");
                        return DynamicFHD.toArray(new String[DynamicFHD.size()]);
                    }
                    else {
                        DynamicFHD.remove("1080p@120");
                        DynamicFHD.remove("1080p@90");
                        DynamicFHD.remove("1080p@60");
                        DynamicFHD.remove("1080p@50");
                        DynamicFHD.remove("1080p@48");
                        return DynamicFHD.toArray(new String[DynamicFHD.size()]);
                    }



                } else if (SizeV.equals("LOW") || SizeV.equals("720p")) {

                    if(HD.contains("720p@480") && !HD.isEmpty())
                    {

                        return DynamicHD.toArray(new String[DynamicHD.size()]);
                    }
                    else if(HD.contains("720p@240") && !HD.isEmpty())
                    {
                        DynamicHD.remove("720p@480");
                        return DynamicHD.toArray(new String[DynamicHD.size()]);
                    }
                    else if(HD.contains("720p@150") && !HD.isEmpty())
                    {
                        DynamicHD.remove("720p@480");
                        DynamicHD.remove("720p@240");
                        return DynamicHD.toArray(new String[DynamicHD.size()]);
                    }
                    else if(HD.contains("720p@120") && !HD.isEmpty())
                    {
                        DynamicHD.remove("720p@480");
                        DynamicHD.remove("720p@240");
                        DynamicHD.remove("720p@150");
                        return DynamicHD.toArray(new String[DynamicHD.size()]);
                    }
                    else if(HD.contains("720p@90") && !HD.isEmpty())
                    {
                        DynamicHD.remove("720p@480");
                        DynamicHD.remove("720p@240");
                        DynamicHD.remove("720p@150");
                        DynamicHD.remove("720p@120");
                        return DynamicHD.toArray(new String[DynamicHD.size()]);
                    }
                    else if(HD.contains("720p@60") && !HD.isEmpty())
                    {
                        DynamicHD.remove("720p@480");
                        DynamicHD.remove("720p@240");
                        DynamicHD.remove("720p@150");
                        DynamicHD.remove("720p@120");
                        DynamicHD.remove("720p@90");

                        return DynamicHD.toArray(new String[DynamicHD.size()]);
                    }
                    else {
                        DynamicHD.remove("720p@480");
                        DynamicHD.remove("720p@240");
                        DynamicHD.remove("720p@150");
                        DynamicHD.remove("720p@120");
                        DynamicHD.remove("720p@90");
                        DynamicHD.remove("720p@60");
                        DynamicHD.remove("720p@50");
                        DynamicHD.remove("720p@48");

                        return DynamicHD.toArray(new String[DynamicHD.size()]);
                    }

                } else if (SizeV.equals("4kUHD")) {
                    return new String[]{"Default", "UHDp@30", "UHD@29", "UHD@25", "UHD@24", "UHD@15"};
                }
                else
                {

                    return new String[]{"Default" , SizeV+"@30", SizeV+"@29",SizeV+"@25",SizeV+"@24",SizeV+"@15"};
                }

            }
            catch (NullPointerException e)
            {

            }
        }
        else if(DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W())
        {
            try {
                String SizeV = cameraUiWrapper.appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE);


                if (SizeV.equals("HIGH") || SizeV.equals("1080p")) {
                    return new String[]{"Default","1080p@30", "1080p@29", "1080p@25", "1080p@24", "1080p@15"};
                } else if (SizeV.equals("LOW") || SizeV.equals("720p")) {
                    return new String[]{"Default", "720p@120", "720p@60", "720p@50", "720p@48", "720p@30", "720p@29", "720p@25", "720p@24", "720p@15"};

                } else if (SizeV.equals("4kUHD")) {
                    return new String[]{"Default", "UHDp@30", "UHD@29", "UHD@25", "UHD@24", "UHD@15"};
                }
                else
                    return new String[]{"Default"};
            }
            catch (NullPointerException e){ }

        }
        else if(DeviceUtils.isG4())
        {
            try {
                String SizeV = cameraUiWrapper.appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE);


                if (SizeV.equals("HIGH") || SizeV.equals("1080p")) {
                    return new String[]{"Default", "1080p@60", "1080p@50", "1080p@48", "1080p@30", "1080p@29", "1080p@25", "1080p@24", "1080p@15"};
                } else if (SizeV.equals("LOW") || SizeV.equals("720p")) {
                    return new String[]{"Default", "720p@120", "720p@60", "720p@50", "720p@48", "720p@30", "720p@29", "720p@25", "720p@24", "720p@15"};

                } else if (SizeV.equals("4kUHD")) {
                    return new String[]{"Default", "UHD@30", "UHD@29", "UHD@25", "UHD@24", "UHD@15"};
                }
                else
                    return new String[]{"Default"};
            }
            catch (NullPointerException e)
            {

            }

        }
        else if(DeviceUtils.isG2()||DeviceUtils.isLG_G3()||DeviceUtils.isZTEADV())
        {
            try {
                String SizeV = cameraUiWrapper.appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE);


                if (SizeV.equals("HIGH") || SizeV.equals("1080p")) {
                    return new String[]{"Default", "1080p@60", "1080p@50", "1080p@48", "1080p@30", "1080p@29", "1080p@25", "1080p@24", "1080p@15"};
                } else if (SizeV.equals("LOW") || SizeV.equals("720p")) {
                    return new String[]{"Default", "720p@120", "720p@60", "720p@50", "720p@48", "720p@30", "720p@29", "720p@25", "720p@24", "720p@15"};

                } else if (SizeV.equals("4kUHD")) {
                    return new String[]{"Default", "UHD@30", "UHD@29", "UHD@25", "UHD@24", "UHD@15"};
                }
                else
                    return new String[]{"Default"};
            }
            catch (NullPointerException e)
            {

            }

        }

        String SizeV = cameraUiWrapper.appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE);
        return new String[]{"Default" , SizeV+"@30", SizeV+"@29",SizeV+"@25",SizeV+"@24",SizeV+"@15"};
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        FpsTriggered = true;
        FpEss = valueToSet;
        super.IdentifySub = valueToSet;
        if(valueToSet.equals("Default") ||Integer.parseInt(valueToSet.split("@")[1]) <= 30 )
        {
            super.SetValue("off", setToCam);
           // super.BackgroundValueHasChanged(valueToSet.split("@")[1]);
        }
        else if(parameters.containsKey("video-hfr")) {
            if (Integer.parseInt(valueToSet.split("@")[1]) > 3 ) {
                if (Integer.parseInt(valueToSet.split("@")[1]) >= 48 && Integer.parseInt(valueToSet.split("@")[1]) < 90) {

                    switch (valueToSet.split("@")[0]) {
                        case "720p":
                            if (HD.contains("720p@480")) {
                            super.SetValue("480", setToCam);
                        } else if (HD.contains("720p@240")) {
                            super.SetValue("240", setToCam);
                        } else if (HD.contains("720p@150")) {
                            super.SetValue("150", setToCam);
                        } else if (HD.contains("720p@120")) {
                            super.SetValue("120", setToCam);
                        } else if (HD.contains("720p@90")) {
                            super.SetValue("90", setToCam);
                        }
                        break;
                        case "1080p":
                        if (FHD.contains("1080p@120")) {
                            super.SetValue("120", setToCam);
                        } else if (FHD.contains("1080p@90")) {
                            super.SetValue("90", setToCam);
                        } else
                            super.SetValue("60", setToCam);
                            break;
                    }


                } else if (Integer.parseInt(valueToSet.split("@")[1]) >= 90 && Integer.parseInt(valueToSet.split("@")[1]) < 120) {
                    switch (valueToSet.split("@")[0]) {
                        case "720p":
                            if (HD.contains("720p@480")) {
                                super.SetValue("480", setToCam);
                            } else if (HD.contains("720p@240")) {
                                super.SetValue("240", setToCam);
                            } else if (HD.contains("720p@150")) {
                                super.SetValue("150", setToCam);
                            } else if (HD.contains("720p@120")) {
                                super.SetValue("120", setToCam);
                            } else if (HD.contains("720p@90")) {
                                super.SetValue("90", setToCam);
                            }
                            break;
                        case "1080p":
                            if (FHD.contains("1080p@120")) {
                                super.SetValue("120", setToCam);
                            } else if (FHD.contains("1080p@90")) {
                                super.SetValue("90", setToCam);
                            } else
                                super.SetValue("90", setToCam);
                            break;
                    }
                }
               else if (Integer.parseInt(valueToSet.split("@")[1]) >= 120 && Integer.parseInt(valueToSet.split("@")[1]) < 150) {
                    switch (valueToSet.split("@")[0]) {
                        case "720p":
                            if (HD.contains("720p@480")) {
                                super.SetValue("480", setToCam);
                            } else if (HD.contains("720p@240")) {
                                super.SetValue("240", setToCam);
                            } else if (HD.contains("720p@150")) {
                                super.SetValue("150", setToCam);
                            } else if (HD.contains("720p@120")) {
                                super.SetValue("120", setToCam);
                            }
                            break;
                        case "1080p":
                            if (FHD.contains("1080p@120")) {
                                super.SetValue("120", setToCam);
                            } else if (FHD.contains("1080p@90")) {
                                super.SetValue("90", setToCam);
                            } else
                                super.SetValue("120", setToCam);
                            break;
                    }
                }

               else if (Integer.parseInt(valueToSet.split("@")[1]) >= 150 && Integer.parseInt(valueToSet.split("@")[1]) < 240) {
                    if (HD.contains("720p@480")) {
                        super.SetValue("480", setToCam);
                    } else if (HD.contains("720p@240")) {
                        super.SetValue("240", setToCam);
                    } else if (HD.contains("720p@150")) {
                        super.SetValue("150", setToCam);
                    } else if (HD.contains("720p@120")) {
                        super.SetValue("120", setToCam);
                    }
                }

                else if (Integer.parseInt(valueToSet.split("@")[1]) >= 240 && Integer.parseInt(valueToSet.split("@")[1]) < 480) {
                    if (HD.contains("720p@480")) {
                        super.SetValue("480", setToCam);
                    } else if (HD.contains("720p@240")) {
                        super.SetValue("240", setToCam);
                    } else if (HD.contains("720p@150")) {
                        super.SetValue("150", setToCam);
                    } else if (HD.contains("720p@120")) {
                        super.SetValue("120", setToCam);
                    }
                }
               else if (Integer.parseInt(valueToSet.split("@")[1]) >= 480) {
                    if (HD.contains("720p@480")) {
                        super.SetValue("480", setToCam);
                    } else if (HD.contains("720p@240")) {
                        super.SetValue("240", setToCam);
                    } else if (HD.contains("720p@150")) {
                        super.SetValue("150", setToCam);
                    } else if (HD.contains("720p@120")) {
                        super.SetValue("120", setToCam);
                    }
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
        if(FpsTriggered)
        {
            return FpEss;
        }
        else
            return "Default";

    }
}
