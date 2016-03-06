package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.utils.DeviceUtils;

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

    final public static int JPEG= 0;
    final public static int RAW = 1;
    final public static int DNG = 2;

    final static public String[] CaptureMode =
    {
        "jpeg",
        "raw",
        "dng"
    };

    /***
     * @param uihandler    Holds the ui Thread to invoke the ui from antother thread
     * @param parameters   Hold the Camera Parameters
     * @param cameraHolder Hold the camera object
     */
    public PictureFormatHandler(Handler uihandler, HashMap<String, String> parameters, BaseCameraHolder cameraHolder)
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
                    if (DeviceUtils.IS(DeviceUtils.Devices.OneA9))
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
                    case "raw":
                        setString(rawFormat,setToCam);
                        baseCameraHolder.ParameterHandler.SetDngActive(false);
                        break;
                    case "dng":
                        setString(rawFormat,setToCam);
                        baseCameraHolder.ParameterHandler.SetDngActive(true);
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
                return new String[]{CaptureMode[JPEG],CaptureMode[DNG]};
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
}
