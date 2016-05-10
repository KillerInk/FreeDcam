package com.freedcam.apis.camera1.camera.parameters.modes;

import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraHolderApi1.Frameworks;
import com.freedcam.apis.camera1.camera.modules.ModuleHandler;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 05.02.2016.
 */
public class PictureFormatHandler extends BaseModeParameter
{
    private final String TAG = PictureFormatHandler.class.getSimpleName();
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
    public PictureFormatHandler(Handler uihandler, HashMap<String, String> parameters, CameraHolderApi1 cameraHolder, CamParametersHandler camParametersHandler)
    {
        super(uihandler, parameters, cameraHolder, "", "");
        if (cameraHolderApi1.DeviceFrameWork == Frameworks.MTK)
        {
            Logger.d(TAG,"mtk");
            isSupported = true;
            rawSupported = true;
        }
        else
        {
            Logger.d(TAG,"default");
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
        Logger.d(TAG, "rawsupported:" + rawSupported + "isSupported:"+isSupported);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {

        Logger.d(TAG, "SetValue:" + valueToSet);
        captureMode = valueToSet;
        final Frameworks f = cameraHolderApi1.DeviceFrameWork;
        if (cameraHolderApi1.DeviceFrameWork == Frameworks.Normal || cameraHolderApi1.DeviceFrameWork == Frameworks.LG)
        {
            switch (valueToSet)
            {
                case "jpeg":
                    setString(valueToSet,setToCam);
                    break;
                case "bayer":
                    setString(rawFormat,setToCam);
                    cameraHolderApi1.GetParameterHandler().SetDngActive(false);
                    break;
                case "dng":
                    setString(rawFormat,setToCam);
                    cameraHolderApi1.GetParameterHandler().SetDngActive(true);
                    break;
            }
        }
        BackgroundValueHasChanged(valueToSet);
    }

    private void setString(String val, boolean setTocam)
    {
        Logger.d(TAG, "setString:" +val);
        parameters.put("picture-format", val);
        cameraHolderApi1.SetCameraParameters(parameters);
        if(cameraHolderApi1.DeviceFrameWork == CameraHolderApi1.Frameworks.LG && setTocam)
        {
            cameraHolderApi1.StopPreview();
            cameraHolderApi1.StartPreview();
        }
        firststart = false;
    }

    @Override
    public boolean IsSupported()
    {
        Logger.d(TAG,"IsSupported:"+isSupported);
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
        public BayerFormat(Handler uihandler, HashMap<String, String> parameters, CameraHolderApi1 cameraHolder, String values) {
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
