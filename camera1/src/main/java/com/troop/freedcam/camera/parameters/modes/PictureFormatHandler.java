package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 05.02.2016.
 */
public class PictureFormatHandler extends BaseModeParameter
{
    private final String TAG = PictureFormatHandler.class.getSimpleName();
    private final String PICFORMATVALUES = "picture-format-values";
    private final String PICFORMAT = "picture-format";
    private boolean rawSupported = false;
    private String captureMode = "jpeg";
    private String rawFormat;

    private String[] rawFormats;

    final public static int JPEG= 0;
    private final static int RAW = 1;
    private final static int DNG = 2;

    private BayerFormat BayerFormats;

    final static public String[] CaptureMode =
    {
        "jpeg",
        "bayer",
        "dng"
    };

    /***
     * @param uihandler    Holds the ui Thread to invoke the ui from antother thread
     * @param parameters   Hold the Camera Parameters
     * @param cameraHolder Hold the camera object
     */
    public PictureFormatHandler(Handler uihandler, HashMap<String, String> parameters, BaseCameraHolder cameraHolder, CamParametersHandler camParametersHandler)
    {
        super(uihandler, parameters, cameraHolder, "", "");
        switch (cameraHolder.DeviceFrameWork)
        {
            case Normal://normal has no break so it runs always through lg
            case LG:
                if (parameters.containsKey(PICFORMATVALUES))
                {
                    isSupported = true;
                    if (DeviceUtils.IS(DeviceUtils.Devices.LG_G2))
                        rawFormat = "bayer-mipi-10bggr";
                    if (DeviceUtils.IS(DeviceUtils.Devices.HTC_OneA9))
                        rawFormat = "bayer-mipi-10rggb";
                    else
                    {
                        String formats = parameters.get("picture-format-values");
                        if (formats.contains("bayer-mipi") || formats.contains("raw"))
                        {
                            rawSupported = true;
                            String forms[] = formats.split(",");
                            for (String s : forms) {
                                if (s.contains("bayer-mipi") || s.contains("raw"))
                                {
                                    rawFormat = s;
                                    break;
                                }
                            }
                        }
                        if (formats.contains("bayer"))
                        {
                            ArrayList<String> tmp = new ArrayList<>();
                            String forms[] = formats.split(",");
                            for (String s : forms) {
                                if (s.contains("bayer"))
                                {
                                    tmp.add(s);
                                }
                            }
                            rawFormats = new String[tmp.size()];
                            tmp.toArray(rawFormats);
                            if (tmp.size()>0) {
                                BayerFormats = new BayerFormat(uihandler, parameters, cameraHolder, "");
                                camParametersHandler.bayerformat = BayerFormats;
                            }

                        }
                    }
                }
                break;
            case MTK:
                isSupported = true;
                rawSupported = true;
                break;
        }
        Logger.d(TAG, "rawsupported:" + rawSupported);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {

        Logger.d(TAG, "SetValue:" + valueToSet);
        captureMode = valueToSet;
        switch (baseCameraHolder.DeviceFrameWork)
        {
            case Normal:
            case LG:
                switch (valueToSet)
                {
                    case "jpeg":
                        setString(valueToSet,setToCam);
                        break;
                    case "bayer":
                        setString(rawFormat,setToCam);
                        baseCameraHolder.GetParameterHandler().SetDngActive(false);
                        break;
                    case "dng":
                        setString(rawFormat,setToCam);
                        baseCameraHolder.GetParameterHandler().SetDngActive(true);
                        break;
                }
                break;
            case MTK:
                //handeld due appsettings
                break;
        }
        BackgroundValueHasChanged(valueToSet);
    }

    private void setString(String val, boolean setTocam)
    {
        Logger.d(TAG, "setString:" +val);
        parameters.put(PICFORMAT, val);
        baseCameraHolder.SetCameraParameters(parameters);
        if(baseCameraHolder.DeviceFrameWork == BaseCameraHolder.Frameworks.LG && setTocam)
        {
            baseCameraHolder.StopPreview();
            baseCameraHolder.StartPreview();
        }
        firststart = false;
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String GetValue() {
        return captureMode;
    }

    @Override
    public String[] GetValues()
    {
        if (rawSupported && DeviceUtils.isCamera1NO_RAW_STREM())
            return new String[]{CaptureMode[JPEG]};
        if (rawSupported && !DeviceUtils.isCamera1DNGSupportedDevice())
            return new String[]{CaptureMode[JPEG],CaptureMode[RAW]};
        else if(rawSupported && DeviceUtils.isCamera1DNGSupportedDevice())
                return new String[]{CaptureMode[JPEG],CaptureMode[DNG],CaptureMode[RAW]};
        else
            return new String[]{CaptureMode[JPEG]};
    }

    @Override
    public String ModuleChanged(String module)
    {
        switch (module)
        {
            case ModuleHandler.MODULE_PICTURE:
            case ModuleHandler.MODULE_INTERVAL:
            case ModuleHandler.MODULE_HDR:
                BackgroundIsSupportedChanged(true);
                break;
            case ModuleHandler.MODULE_VIDEO:
                BackgroundIsSupportedChanged(false);
                break;
        }
        return super.ModuleChanged(module);
    }

    @Override
    public void onValueChanged(String val) {
        super.onValueChanged(val);
    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {
        super.onIsSupportedChanged(isSupported);
    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {
        super.onIsSetSupportedChanged(isSupported);
    }

    @Override
    public void onValuesChanged(String[] values) {
        super.onValuesChanged(values);
    }

    public class BayerFormat extends BaseModeParameter
    {

        /***
         * @param uihandler    Holds the ui Thread to invoke the ui from antother thread
         * @param parameters   Hold the Camera Parameters
         * @param cameraHolder Hold the camera object
         * @param values
         */
        public BayerFormat(Handler uihandler, HashMap<String, String> parameters, BaseCameraHolder cameraHolder, String values) {
            super(uihandler, parameters, cameraHolder, "", "");
        }

        @Override
        public String GetValue()
        {
            return rawFormat;
        }

        @Override
        public String[] GetValues() {
            return rawFormats;
        }

        @Override
        public boolean IsSupported() {
            return rawFormats != null && rawFormats.length>0;
        }

        @Override
        public boolean IsVisible() {
            return  rawFormats != null && rawFormats.length>0;
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCam)
        {
            rawFormat = valueToSet;
            if (captureMode.equals("bayer")|| captureMode.equals("dng"))
                PictureFormatHandler.this.SetValue(captureMode,true);
        }
    }
}
