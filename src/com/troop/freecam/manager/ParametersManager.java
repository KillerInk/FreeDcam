package com.troop.freecam.manager;

import android.hardware.Camera;
import android.util.Log;
import android.view.Display;

import com.troop.freecam.MainActivity;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.interfaces.ParametersChangedInterface;
import com.troop.freecam.interfaces.PreviewSizeChangedInterface;
import com.troop.freecam.utils.DeviceUtils;

import java.util.List;

/**
 * Created by troop on 16.10.13.
 */
public class ParametersManager
{
    //New Pref 07-12-13
    public static final String Preferences_PictureFormat = "picture_format";

    public static final String Preferences_Denoise = "denoise";
   // public static final String Preferences_Stab = "stablization";
    public static final String Preferences_ZSL = "zsl_value";
    //public static final String Preferences_Composition = "front_ipp";
    //public static final String Preferences_HFR = "hfr_video";
    final static String TAG = "freecam.ParametersManager";

    CameraManager cameraManager;
    MainActivity mainActivity;
    android.hardware.Camera.Parameters parameters;
    public android.hardware.Camera.Parameters getParameters(){return parameters;}
    SettingsManager preferences;
    boolean supportSharpness = false;
    public boolean getSupportSharpness() { return supportSharpness;}
    boolean supportContrast = false;
    public boolean getSupportContrast() { return  supportContrast;}
    boolean supportBrightness = false;
    public boolean getSupportBrightness() { return  supportBrightness;}
    boolean supportSaturation = false;
    public boolean getSupportSaturation() { return  supportSaturation;}
    boolean supportFlash = false;
    public boolean getSupportFlash() { return  supportFlash;}
    boolean supportVNF = false;
    public boolean getSupportVNF() { return supportVNF;}
    boolean supportAutoExposure = false;
    public boolean getSupportAutoExposure() { return supportAutoExposure;}
    boolean supportAfpPriority = false;
    public boolean getSupportAfpPriority() { return supportAfpPriority;}
    boolean supportIPP = false;
    public boolean getSupportIPP() { return supportIPP;}
    boolean supportZSL = false;
    public boolean getSupportZSL() { return supportZSL;}
    boolean supportWhiteBalance = false;
    public boolean getSupportWhiteBalance() { return supportWhiteBalance; }
    boolean supportIso = false;
    public boolean getSupportIso() { return  supportIso; }
    boolean supportExposureMode = false;
    public boolean getSupportExposureMode() { return supportExposureMode; }
    boolean supportScene = false;
    boolean supportManualFocus = false;
    public boolean getSupportManualFocus(){ return  supportManualFocus;}
    public boolean getSupportScene() { return supportScene;}
    private ParametersChangedInterface parametersChanged;
    public BrightnessManager Brightness;
    public AFPriorityManager AfPriority;
    public VideoModes videoModes;
    public ZeroShutterLagClass ZSLModes;
    public DenoiseClass Denoise;
    public WhiteBalanceClass WhiteBalance;
    public IsoClass Iso;
    public ExposureModeClass ExposureMode;
    public SceneModeClass SceneMode;
    public ImagePostProcessingClass ImagePostProcessing;
    public PreviewFormatClass PreviewFormat;
    public PreviewFpsClass PreviewFps;
    public ManualSharpnessClass manualSharpness;
    public ManualExposureClass manualExposure;
    public ManualContrastClass manualContrast;

    public enum enumParameters
    {
        All,
        Denoise,
        ManualBrightness,
        AfPriority,
        VideoModes,
        ZeroShutterLag,
        ManualWhiteBalance,
        Iso,
        ExposureMode,
        Scene,
        Ipp,
        PreviewFormat,
        PreviewSize,
        PreviewFps,
        ManualSharpness,
        ManualExposure,
        ManualContrast,
        ManualFocus,
        WhiteBalanceMode,
        FlashMode,
        PictureSize,
        FocusMode,
    }

    private boolean loadingParametersFinish = false;

    public ParametersManager(CameraManager cameraManager, SettingsManager preferences)
    {
        this.cameraManager = cameraManager;
        this.preferences = preferences;

    }

    public void SetCameraParameters(android.hardware.Camera.Parameters parameters)
    {
        loadingParametersFinish = false;
        this.parameters = parameters;
        String[] paras =  parameters.flatten().split(";");
        for(int i = 0; i < paras.length; i++)
            Log.d("CameraParameters", paras[i]);
        checkParametersSupport();
        Brightness = new BrightnessManager();
        AfPriority = new AFPriorityManager();
        videoModes = new VideoModes();
        ZSLModes = new ZeroShutterLagClass();
        Denoise = new DenoiseClass();
        WhiteBalance = new WhiteBalanceClass();
        Iso = new IsoClass();
        ExposureMode = new ExposureModeClass();
        SceneMode = new SceneModeClass();
        ImagePostProcessing = new ImagePostProcessingClass();
        PreviewFormat = new PreviewFormatClass();
        PreviewFps = new PreviewFpsClass();
        manualSharpness = new ManualSharpnessClass();
        manualExposure = new ManualExposureClass();
        manualContrast = new ManualContrastClass();
        loadDefaultOrLastSavedSettings();
        loadingParametersFinish = true;
        onParametersCHanged(true, enumParameters.All);
    }

    public void setParametersChanged(ParametersChangedInterface parametersChangedInterface)
    {
        this.parametersChanged = parametersChangedInterface;
    }

    /*public void UpdateUI()
    {
        onParametersCHanged();
    }*/

    private void onParametersCHanged(enumParameters paras)
    {
        if (parametersChanged != null && loadingParametersFinish && parameters != null)
            parametersChanged.parametersHasChanged(false, paras);
    }

    private void onParametersCHanged(boolean reloadGui, enumParameters paras)
    {
        if (parametersChanged != null && loadingParametersFinish && parameters != null)
            parametersChanged.parametersHasChanged(reloadGui, paras);
    }

    private void checkParametersSupport()
    {
        try {
            int i = parameters.getInt("manual-focus");
            supportManualFocus = true;
        }
        catch (Exception ex)
        {
            supportManualFocus = false;
        }
        Log.d(TAG, "support manualFocus:" + supportManualFocus);
        try
        {
            int i = parameters.getInt("sharpness");
            supportSharpness = true;
        }
        catch (Exception ex)
        {
            supportSharpness = false;
        }
        Log.d(TAG, "supportSharpness:" + supportSharpness);
        try
        {
            int i = parameters.getInt("contrast");
            supportContrast = true;
        }
        catch (Exception ex)
        {
            supportContrast = false;
        }

        try
        {
            int i = parameters.getInt("saturation");
            supportSaturation = true;
        }
        catch (Exception ex)
        {
            supportSaturation = false;
        }
        Log.d(TAG, "support Saturation:" + supportSaturation);
        if (parameters.getFlashMode() != null)
            supportFlash = true;
        else
            supportFlash = false;
        Log.d(TAG, "support Flash:" + supportFlash);
        try
        {
            if (!parameters.get("vnf-supported").equals("") &&  !parameters.get("vnf-supported").equals(false))
            {
                supportVNF = true;
            }
        }
        catch (Exception ex)
        {
            supportVNF = false;
        }
        Log.d(TAG,"supportVNF = Videostab:" + supportVNF);
        try
        {
            if (!parameters.get("auto-exposure-values").equals(""))
                supportAutoExposure= true;
        }
        catch (Exception ex)
        {
            supportAutoExposure = false;
        }
        Log.d(TAG,"support autoexposure" + supportAutoExposure);
    }

    private void loadDefaultOrLastSavedSettings()
    {
        /*if(DeviceUtils.isQualcomm())
        {
            parameters.set("denoise","denoise-off");
            parameters.set("power-mode","Normal_Power");
            parameters.set("mce","disable");
        }*/

        if (getSupportAfpPriority() && !preferences.afPriority.Get().equals(""))
            AfPriority.Set(preferences.afPriority.Get());

        if (getSupportIso() && !preferences.IsoMode.Get().equals(""))
            Iso.set(preferences.IsoMode.Get());

        if (getSupportScene() && !preferences.SceneMode.Get().equals(""))
            SceneMode.set(preferences.SceneMode.Get());

        if (getSupportWhiteBalance() && !preferences.WhiteBalanceMode.Get().equals(""))
            WhiteBalance.set(preferences.WhiteBalanceMode.Get());

        if (getSupportAutoExposure() && !preferences.MeteringMode.Get().equals(""))
            parameters.set("auto-exposure", preferences.MeteringMode.Get());

        if (getSupportExposureMode() && !cameraManager.Settings.ExposureMode.Get().equals(""))
            ExposureMode.set(cameraManager.Settings.ExposureMode.Get());

        if (!cameraManager.Settings.PictureSize.Get().equals(""))
            setPictureSize(cameraManager.Settings.PictureSize.Get());
        //if (!cameraManager.Settings.PreviewSize.Get().equals(""))
            //setPreviewSize(cameraManager.Settings.PreviewSize.Get());
        setOptimalPreviewSize();

        if (!cameraManager.Settings.FocusMode.Get().equals(""))
            parameters.setFocusMode(cameraManager.Settings.FocusMode.Get());

        if (!preferences.ColorMode.Get().equals(""))
            parameters.setColorEffect(preferences.ColorMode.Get());

        if (getSupportFlash() && !cameraManager.Settings.FlashMode.Get().equals(""))
            parameters.setFlashMode(cameraManager.Settings.FlashMode.Get());

        if (getSupportZSL() && !cameraManager.Settings.ZeroShutterLag.Get().equals(""))
            ZSLModes.setValue(cameraManager.Settings.ZeroShutterLag.Get());

        if (getSupportIPP() && !cameraManager.Settings.ImagePostProcessing.Get().equals(""))
            ImagePostProcessing.Set(cameraManager.Settings.ImagePostProcessing.Get());
        if (!cameraManager.Settings.PreviewFps.Get().equals(""))
            PreviewFps.Set(cameraManager.Settings.PreviewFps.Get());
        //parameters.set("rawsave-mode", "1");
        //parameters.set("rawfname", "/mnt/sdcard/test.raw");

        Log.d("freecam.ParametersManager", "Finished Loading Default Or Last Saved Settings");

        cameraManager.Restart(false);
    }

    public PreviewSizeChangedInterface setPreviewSizeCHanged;

    private void onpreviewsizehasChanged(int w, int h)
    {
        if (setPreviewSizeCHanged != null)
            setPreviewSizeCHanged.onPreviewsizeHasChanged(w, h);
    }

    public void setPictureSize(String s)
    {
        String[] widthHeight = s.split("x");
        int w = Integer.parseInt(widthHeight[0]);
        int h = Integer.parseInt(widthHeight[1]);
        parameters.setPictureSize(w,h);
        onParametersCHanged(enumParameters.PictureSize);
        cameraManager.Restart(false);
        Log.d(TAG, "set picture size to " + s);
    }
    private void setPreviewSize(String s)
    {
        String[] widthHeight = s.split("x");
        int w = Integer.parseInt(widthHeight[0]);
        int h = Integer.parseInt(widthHeight[1]);
        parameters.setPreviewSize(w, h);
        onpreviewsizehasChanged(w,h);
    }
    private void setOptimalPreviewSize()
    {
        Display display = cameraManager.activity.getWindowManager().getDefaultDisplay();
        Camera.Size optimal = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(),display.getWidth() , display.getHeight());
        parameters.setPreviewSize(optimal.width, optimal.height);
        Log.d(TAG, "set optimal previewsize to " + optimal.width + "x" + optimal.height);
    }

    public void SetPreviewSizeToCameraParameters(int w, int h)
    {
        parameters.setPreviewSize(w,h);
        Log.d(TAG, "set previewsize to " + w + "x" + h);
        onpreviewsizehasChanged(w, h);
        onParametersCHanged(enumParameters.PreviewSize);
    }

    public void setFlashMode(String flash)
    {
        parameters.setFlashMode(flash);
        cameraManager.Restart(false);
        onParametersCHanged(enumParameters.FlashMode);
    }

    public void setFocusMode(String focusMode)
    {
        parameters.setFocusMode(focusMode);
        onParametersCHanged(enumParameters.FocusMode);
        cameraManager.Restart(false);
    }

    /*public void SetExposureCompensation(int exp)
    {
        //cameraManager.parameters.setExposureCompensation(exp);
        parameters.set("exposure-compensation", exp);
        onParametersCHanged();
        try
        {
            cameraManager.Restart(false);
            //cameraManager.activity.exposureTextView.setText("Exposure: " + String.valueOf(parameters.getExposureCompensation()));
            Log.d(TAG, "Exposure:"+String.valueOf(cameraManager.mCamera.getParameters().getExposureCompensation()));
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Exposure set failed");
            ex.printStackTrace();
        }
    }*/





    //TODO FINDout min max value
    public void SetMFocus(int focus)
    {
        //mainActivity.focusButton.setEnabled(false);
        parameters.set("manual-focus", 0);
        parameters.setFocusMode("normal");
        parameters.set("manualfocus_step", focus);
        onParametersCHanged(enumParameters.ManualFocus);
        try
        {
            cameraManager.Restart(false);

        }
        catch (Exception ex)
        {
            Log.e(TAG, "ManualFocus Failed");
        }
    }

    public void SetJpegQuality(int quality)
    {
        parameters.set("jpeg-quality", quality);
        //onParametersCHanged(enumParameters.All);
        //setToPreferencesToCamera();
    }

    //preferences are set during CameraManager.Reset()
    /*private void setToPreferencesToCamera()
    {
        cameraManager.mCamera.setParameters(parameters);
    }*/

    public boolean doCropping()
    {
        return preferences.CropImage.GET();
    }

    //TODO this is a wrong implementation, vnf-supported is used false;
    //TODO if it returns false its not supported set and get with vnf=enable,disable
    public class DenoiseClass
    {
        public String[] getDenoiseValues()
        {
            String[] noise =  new String[0];
            if(DeviceUtils.isOmap())
            {
                noise = parameters.get("vnf").split(",");

            }
            if(DeviceUtils.isQualcomm())
                noise = parameters.get("denoise-values").split(",");
            return noise;
        }

        public String getDenoiseValue()
        {
            if(DeviceUtils.isOmap())
                return parameters.get("vnf");
            if(DeviceUtils.isQualcomm())
                return parameters.get("denoise");
            return "";
        }
    }

    public class BrightnessManager
    {
        String brightnessValue;
        public BrightnessManager()
        {
            try
            {
                int i = parameters.getInt("brightness");
                supportBrightness = true;
                brightnessValue = "brightness";
            }
            catch (Exception ex)
            {
                supportBrightness = false;
            }
            if (!supportBrightness)
            {
                try
                {
                    int i = parameters.getInt("luma-adaptation");
                    supportBrightness = true;
                    brightnessValue = "luma-adaptation";
                }
                catch (Exception ex)
                {
                    supportBrightness = false;
                }
            }
            Log.d(TAG, "support brightness:"+ supportBrightness);
        }

        public void Set(int bright)
        {


            try
            {
                parameters.set(brightnessValue, bright);
                cameraManager.Restart(false);
            }
            catch (Exception ex)
            {
                Log.e("brightness Set Fail", ex.getMessage());
            }
            onParametersCHanged(enumParameters.ManualBrightness);
            //cameraManager.activity.brightnessTextView.setText(String.valueOf(parameters.get(brightnessValue)));

        }

        public int Get()
        {
            return parameters.getInt(brightnessValue);
        }

    }

    public class AFPriorityManager
    {
        String afpValue;
        String AfpValues;

        public AFPriorityManager()
        {
            try {
                if (DeviceUtils.isQualcomm())
                {
                    if(!parameters.get("selectable-zone-af-values").isEmpty())
                    {
                        supportAfpPriority = true;
                        afpValue = "selectable-zone-af";
                        AfpValues = "selectable-zone-af-values";
                        if (getValues().length == 0)
                            supportAfpPriority = false;
                    }
                    else
                        supportAfpPriority = false;
                }
                if (DeviceUtils.isOmap())
                {
                    if(!parameters.get("auto-convergence-mode-values").isEmpty())
                    {
                        supportAfpPriority = true;
                        afpValue = "auto-convergence-mode";
                        AfpValues= "auto-convergence-mode-values";
                        if (getValues().length == 0)
                            supportAfpPriority = false;
                    }
                    else
                        supportAfpPriority = false;
                }

            }
            catch (Exception ex)
            {
                supportAfpPriority = false;
            }
            Log.d(TAG, "support afp:" + supportAfpPriority);
        }

        public void Set(String value)
        {
            String def = Get();

            Log.e(TAG, "Try to set Afp from" + def + " to " + value);
            if (!def.equals(value))
            {
                try
                {
                    parameters.set(afpValue, value);
                    cameraManager.Restart(false);
                }
                catch (Exception ex)
                {
                    Log.e(TAG, "Set afp failed back to def");
                    parameters.set(afpValue, def);
                    cameraManager.Restart(false);
                }
            }
            onParametersCHanged(enumParameters.AfPriority);
        }
        public String Get()
        {
            return parameters.get(afpValue);
        }

        public String[] getValues()
        {
            return parameters.get(AfpValues).split(",");
        }
    }

    public class VideoModes
    {
        List<Camera.Size> sizes;
        public int Width;
        public int Height;

        public VideoModes()
        {
            sizes = parameters.getSupportedVideoSizes();
            if (sizes == null || sizes.size() == 0)
                sizes = parameters.getSupportedPreviewSizes();
            SetProfile(preferences.VideoSize.Get());

        }

        public String[] getStringValues()
        {
            String[] ar = new String[sizes.size()];
            for (int i = 0; i< sizes.size(); i++)
            {
                ar[i] = (sizes.get(i).width + "x" + sizes.get(i).height);
            }
            return ar;
        }

        public void SetProfile(String tmp)
        {
            String[] widthHeight = tmp.split("x");
            Width = Integer.parseInt(widthHeight[0]);
            Height = Integer.parseInt(widthHeight[1]);
            preferences.VideoSize.Set(Width + "x" + Height);
            onParametersCHanged(enumParameters.VideoModes);
        }


    }

    public class ZeroShutterLagClass
    {
        String value;
        String[] values;

        public ZeroShutterLagClass()
        {
            try
            {
                if(DeviceUtils.isQualcomm())
                {
                    value = "zsl";
                    values = parameters.get("zsl-values").split(",");
                }
                if(DeviceUtils.isOmap())
                {
                    value = "mode";
                    values = parameters.get("mode-values").split(",");
                }
                if (values.length > 0)
                {
                    supportZSL = true;
                    parameters.set(value, values[0]);
                }
            }
            catch (Exception ex)
            {
                supportZSL = false;
            }
            Log.d(TAG, "zsl support:" + supportZSL);
        }

        public String[] getValues()
        {
            return values;
        }

        public void setValue(String toapplie)
        {
            String def = getValue();
            if (!def.equals(toapplie))
            {
                try {
                    Log.d(TAG, "Try to set Zeroshutterlag to:"+ toapplie);
                    parameters.set(value, toapplie);
                    cameraManager.Restart(false);
                }
                catch (Exception ex)
                {
                    Log.e(TAG, "ZSL set failed set to " + def);
                    parameters.set(def, toapplie);
                    cameraManager.Restart(false);
                }
                onParametersCHanged(enumParameters.ZeroShutterLag);
            }

        }

        public String getValue()
        {
            return parameters.get(value);
        }
    }

    public class WhiteBalanceClass
    {
        String[] values;
        public WhiteBalanceClass()
        {
            try {
                values = getParameters().getSupportedWhiteBalance().toArray(new String[getParameters().getSupportedWhiteBalance().size()]);
                if (values.length>0)
                    supportWhiteBalance = true;
            }
            catch (Exception ex)
            {
                supportWhiteBalance = false;
            }
            Log.d(TAG,"support Whitebalance:" + supportWhiteBalance);
        }

        public String[] getValues()
        {
            return values;
        }

        public void set(String value)
        {
            try {
                getParameters().setWhiteBalance(value);
                cameraManager.Restart(false);
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Whitebalance set failed");
            }
            onParametersCHanged(enumParameters.WhiteBalanceMode);

        }

        public String get()
        {
            return getParameters().getWhiteBalance();
        }
    }

    public class IsoClass
    {
        String[] values;
        String s_isoValues;

        public IsoClass()
        {
            try
            {
                if(DeviceUtils.isOmap())
                    s_isoValues = "iso-mode-values";
                if(DeviceUtils.isQualcomm())
                    s_isoValues = "iso-values";
                values = getParameters().get(s_isoValues).split(",");
                if (values != null && values.length > 0)
                    supportIso = true;
            }
            catch (Exception ex)
            {
                supportIso = false;
            }
            Log.d(TAG,"support IsoModes:" + supportIso);
        }

        public String[] getValues()
        {
            return values;
        }

        public void set(String value)
        {
            try {
                parameters.set("iso", value);
                cameraManager.Restart(false);
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Iso set failed");
            }
            onParametersCHanged(enumParameters.Iso);

        }

        public String get()
        {
            return parameters.get("iso");
        }
    }

    public class ExposureModeClass
    {
        String[] exposureValues;
        public ExposureModeClass()
        {
            try
            {
                if(DeviceUtils.isOmap())
                    exposureValues = getParameters().get("exposure-mode-values").split(",");
                if (exposureValues != null && exposureValues.length > 0)
                    supportExposureMode = true;

            }
            catch (Exception ex)
            {
                supportExposureMode = false;
            }
            Log.d(TAG, "support ExposureModes:" + supportExposureMode);
        }

        public String get()
        {
            return getParameters().get("exposure");
        }

        public String[] getExposureValues()
        {
            return exposureValues;
        }

        public void set(String value)
        {
            String def = get();
            if (!def.equals(value))
            {
                try {
                    Log.d(TAG, "Try set ExposureMode to " +value);
                    getParameters().set("exposure", value);
                    cameraManager.Restart(false);
                }
                catch (Exception ex)
                {
                    Log.e(TAG,"Exposure set failed, set back to"+def);
                    getParameters().set("exposure",def);
                    cameraManager.Restart(false);
                }
            }
            onParametersCHanged(enumParameters.ExposureMode);

        }
    }

    public class SceneModeClass
    {
        String[] values;

        public SceneModeClass()
        {
            try {
                values = getParameters().getSupportedSceneModes().toArray(new String[getParameters().getSupportedSceneModes().size()]);
                if (values.length > 0)
                    supportScene = true;
            }
            catch (Exception ex)
            {
                supportScene = false;
            }
            Log.d(TAG,"support SceneModes:" + supportScene);
        }

        public String[] getValues()
        {
            return values;
        }

        public String get()
        {
            return getParameters().getSceneMode();
        }

        public void set(String val)
        {
            String dev = get();
            if (!dev.equals(val))
            {
                try {
                    Log.d(TAG,"Try set Scene to:" + val);
                    getParameters().setSceneMode(val);
                    cameraManager.Restart(false);
                }
                catch (Exception ex)
                {
                    Log.e(TAG,"Scene set failed set back to" + dev);
                    getParameters().setSceneMode(dev);
                    cameraManager.Restart(false);
                }
                onParametersCHanged(enumParameters.Scene);
            }
        }
    }

    public class ImagePostProcessingClass
    {
        public ImagePostProcessingClass()
        {
            try {
                String ipps = parameters.get("ipp-values");
                if (!ipps.isEmpty())
                    supportIPP = true;
            }
            catch (Exception ex)
            {
                supportIPP = false;
            }
            Log.d(TAG, "support Ipp:" + supportIPP);
        }

        public String[] getValues()
        {
            return getParameters().get("ipp-values").split(",");
        }

        public String Get()
        {
            return getParameters().get("ipp");
        }

        public void Set(String val)
        {
            String dev = Get();
            if (!dev.equals(val))
            {
                try {
                    Log.d(TAG, "try set ipp to:" + val);
                    parameters.set("ipp", val);
                    cameraManager.Restart(false);
                }
                catch (Exception ex)
                {
                    Log.e(TAG,"ipp set failed, set back to:" + dev);
                    parameters.set("ipp", dev);
                    cameraManager.Restart(false);
                }
            }
            onParametersCHanged(enumParameters.Ipp);
        }
    }

    public class PreviewFormatClass
    {
        public String[] getValues()
        {
            return getParameters().get("preview-format-values").split(",");
        }

        public String Get()
        {
            return getParameters().get("preview-format");
        }

        public void Set(String val)
        {
            String dev = Get();
            if (!dev.equals(val))
            {
                try
                {
                    Log.d(TAG, "try set previewformat to:" + val);
                    parameters.set("preview-format", val);
                    cameraManager.Restart(false);
                }
                catch (Exception ex)
                {
                    Log.e(TAG,"preview format set failed, set back to:" + dev);
                    parameters.set("preview-format", dev);
                    cameraManager.Restart(false);
                }
            }
            onParametersCHanged(enumParameters.PreviewFormat);
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public class PreviewFpsClass
    {
        public String[] GetValues()
        {
            String[] ret = new String[parameters.getSupportedPreviewFrameRates().size()];
            Log.d(TAG,"Listing supported Preview FPS");
            for (int i = 0; i< ret.length; i++)
            {
                ret[i] = parameters.getSupportedPreviewFrameRates().get(i) + "";
                Log.d(TAG, ret[i]);
            }
            return ret;
        }

        public int Get()
        {
            return parameters.getPreviewFrameRate();
        }

        public void Set(String val)
        {
            int i = Integer.parseInt(val);
            int dev = parameters.getPreviewFrameRate();
            if (i != dev)
            {
                try {
                    Log.d(TAG, "try set preview fps  to:" + i + " from " + dev);
                    parameters.setPreviewFrameRate(i);
                    cameraManager.mCamera.stopPreview();
                    cameraManager.Restart(false);
                    cameraManager.mCamera.startPreview();
                }
                catch (Exception ex)
                {
                    Log.e(TAG,"preview fps set failed set:" + dev);
                    parameters.setPreviewFrameRate(dev);
                    cameraManager.Restart(false);
                }
            }
            onParametersCHanged(enumParameters.PreviewFps);

        }
    }

    public class ManualSharpnessClass
    {
        public int getMax()
        {
            int max = 0;
            try {
                max = Integer.parseInt(parameters.get("sharpness-max"));
            }
            catch (Exception ex)
            {
                max = 100;
            }
            return max;
        }

        public int getValue()
        {
            return Integer.parseInt(parameters.get("sharpness"));
        }

        public void set(int toset)
        {
            try
            {
                parameters.set("sharpness", toset);
                cameraManager.Restart(false);
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Manual Sharpness set failed");
            }
            onParametersCHanged(enumParameters.ManualSharpness);

        }
    }

    public class ManualExposureClass
    {
        public int getMin()
        {
            return parameters.getMinExposureCompensation();
        }

        public int getMax()
        {
            return parameters.getMaxExposureCompensation();
        }

        public int getValue()
        {
            return parameters.getExposureCompensation();
        }

        public void set(int toset)
        {
            int def = parameters.getExposureCompensation();
            Log.d(TAG, "Try to set exposure from: "+ def+ " to "+ toset);
            try
            {
                parameters.setExposureCompensation(toset);
                cameraManager.Restart(false);
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Set Exposure failed set back to last");
                parameters.setExposureCompensation(def);
                cameraManager.Restart(false);
            }
            onParametersCHanged(enumParameters.ManualExposure);
        }
    }

    public class ManualContrastClass
    {
        public int getMax()
        {
            int max = 0;
            try {
                max = Integer.parseInt(parameters.get("contrast-max"));
            }
            catch (Exception ex)
            {
                max = 100;
            }
            return max;
        }

        public int getValue()
        {
            return Integer.parseInt(parameters.get("contrast"));
        }

        public void set(int contrast)
        {
            parameters.set("contrast", contrast);
            Log.d(TAG,"Set contrast to " + contrast);
            try
            {
                cameraManager.Restart(false);

            }
            catch (Exception ex)
            {
                Log.e(TAG,"Contrast Set Fail");
                ex.printStackTrace();
            }
            onParametersCHanged(enumParameters.ManualContrast);
        }
    }
}
