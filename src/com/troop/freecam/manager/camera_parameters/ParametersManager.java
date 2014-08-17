package com.troop.freecam.manager.camera_parameters;

import android.hardware.Camera;
import android.util.Log;
import android.view.Display;

import com.troop.freecam.camera.old.CameraManager;
import com.troop.freecam.interfaces.PreviewSizeChangedInterface;
import com.troop.freecam.manager.AppSettingsManager;
import com.troop.freecam.utils.DeviceUtils;

import java.util.List;

/**
 * Created by troop on 16.10.13.
 */
public class ParametersManager extends VideoParameters
{
    //New Pref 07-12-13


    public static final String Preferences_Denoise = "denoise";
   // public static final String Preferences_Stab = "stablization";
    public static final String Preferences_ZSL = "zsl_value";
    //public static final String Preferences_Composition = "front_ipp";
    //public static final String Preferences_HFR = "hfr_video";

    public android.hardware.Camera.Parameters getParameters(){return parameters;}

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


    boolean supportScene = false;
    boolean supportManualFocus = false;
    boolean supportManualShutter = false;
    boolean supportManualConvergence = false;
    public boolean getSupportManualConvergence() { return supportManualConvergence;}
    public boolean getSupportManualFocus(){ return  supportManualFocus;}
    public boolean getSupportManualShutter(){ return  supportManualShutter;}
    public boolean getSupportScene() { return supportScene;}

    public ManualBrightnessManager Brightness;
    public AFPriorityManager AfPriority;

    public ZeroShutterLagClass ZSLModes;
    public DenoiseClass Denoise;
    public WhiteBalanceClass WhiteBalance;
    public IsoClass Iso;

    public SceneModeClass SceneMode;
    public ImagePostProcessingClass ImagePostProcessing;
    public PreviewFormatClass PreviewFormat;
    public PreviewFpsClass PreviewFps;
    public ManualSaturationClass manualSaturation;
    public ManualSharpnessClass manualSharpness;
    public ManualExposureClass manualExposure;
    public ManualContrastClass manualContrast;
    public ManualConvergenceClass manualConvergence;

    public ManualFocusClass manualFocus;
    public ManualShutterClass manualShutter;
    public ZoomManager zoomManager;

    public ParametersManager(CameraManager cameraManager, AppSettingsManager preferences)
    {
        super(cameraManager, preferences);
    }

    public void SetCameraParameters(android.hardware.Camera.Parameters parameters)
    {
        loadingParametersFinish = false;
        super.SetCameraParameters(parameters);
        String[] paras =  parameters.flatten().split(";");
        for(int i = 0; i < paras.length; i++)
            Log.d("freecam.CameraParameters", paras[i]);
        checkParametersSupport();
        Brightness = new ManualBrightnessManager();
        AfPriority = new AFPriorityManager();

        ZSLModes = new ZeroShutterLagClass();
        Denoise = new DenoiseClass();
        WhiteBalance = new WhiteBalanceClass();
        Iso = new IsoClass();
        SceneMode = new SceneModeClass();
        ImagePostProcessing = new ImagePostProcessingClass();
        PreviewFormat = new PreviewFormatClass();
        PreviewFps = new PreviewFpsClass();
        manualSaturation = new ManualSaturationClass();
        manualSharpness = new ManualSharpnessClass();
        manualExposure = new ManualExposureClass();
        manualContrast = new ManualContrastClass();
        manualConvergence = new ManualConvergenceClass();
        manualFocus = new ManualFocusClass();

        manualShutter = new ManualShutterClass();
        zoomManager = new ZoomManager();

        loadDefaultOrLastSavedSettings();
        loadingParametersFinish = true;
        onParametersCHanged(true, enumParameters.All);
        
    }






    /*public void UpdateUI()
    {
        onParametersCHanged();
    }*/


    public void SetColor(String color)
    {
        parameters.setColorEffect(color);
        onParametersCHanged(false, enumParameters.Color);
    }

    private void checkParametersSupport()
    {
        try {
            if(DeviceUtils.isHTCADV() || DeviceUtils.isLGADV())
            {
                supportManualFocus = true;
            }
        }
        catch (Exception ex)
        {
            supportManualFocus = false;
        }
        Log.d(TAG, "support manualFocus:" + supportManualFocus);
        try {
            if(DeviceUtils.isHTCADV() || DeviceUtils.isZTEADV())
            {
                supportManualShutter = true;

            }

        }
        catch (Exception ex)
        {
            supportManualShutter = false;
        }
        Log.d(TAG, "support Shutter:" + supportManualShutter);
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


    }

    protected void loadDefaultOrLastSavedSettings()
    {
        /*if(DeviceUtils.isQualcomm())
        {
            camera_parameters.set("denoise","denoise-off");
            camera_parameters.set("power-mode","Normal_Power");
            camera_parameters.set("mce","disable");
        }*/
        try {
            super.loadDefaultOrLastSavedSettings();

            if (getSupportAfpPriority() && !preferences.afPriority.Get().equals(""))
                AfPriority.Set(preferences.afPriority.Get(), false);

            if (getSupportIso() && !preferences.IsoMode.Get().equals(""))
                Iso.set(preferences.IsoMode.Get(), false);

            if (getSupportScene() && !preferences.SceneMode.Get().equals(""))
                SceneMode.set(preferences.SceneMode.Get(), false);

            if (getSupportWhiteBalance() && !preferences.WhiteBalanceMode.Get().equals(""))
                WhiteBalance.set(preferences.WhiteBalanceMode.Get(), false);

        /*if (getSupportAutoExposure() && !preferences.MeteringMode.Get().equals(""))
            camera_parameters.set("auto-exposure", preferences.MeteringMode.Get());*/
        if (DeviceUtils.isZTEADV())
        {
            parameters.set("ois_key","on");
        }


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
                ZSLModes.setValue(cameraManager.Settings.ZeroShutterLag.Get(), false);

            if (getSupportIPP() && !cameraManager.Settings.ImagePostProcessing.Get().equals(""))
                ImagePostProcessing.Set(cameraManager.Settings.ImagePostProcessing.Get(), false);
            if (!cameraManager.Settings.PreviewFps.Get().equals(""))
                PreviewFps.Set(cameraManager.Settings.PreviewFps.Get(), false);
            //camera_parameters.set("rawsave-mode", "1");
            //camera_parameters.set("rawfname", "/mnt/sdcard/test.raw");
            cameraManager.ReloadCameraParameters(false);
            Log.d("freecam.ParametersManager", "Finished Loading Default Or Last Saved Settings");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        //cameraManager.ReloadCameraParameters(false);
    }

    public PreviewSizeChangedInterface setPreviewSizeCHanged;

    private void onpreviewsizehasChanged(int w, int h)
    {
        if (setPreviewSizeCHanged != null)
            setPreviewSizeCHanged.onPreviewsizeHasChanged(w, h);
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
        cameraManager.ReloadCameraParameters(false);
        onParametersCHanged(enumParameters.FlashMode);
    }



    public void setFocusMode(String focusMode)
    {
        parameters.setFocusMode(focusMode);
        onParametersCHanged(enumParameters.FocusMode);
        cameraManager.ReloadCameraParameters(false);
    }

    /*public void SetExposureCompensation(int exp)
    {
        //cameraManager.camera_parameters.setExposureCompensation(exp);
        camera_parameters.set("exposure-compensation", exp);
        onParametersCHanged();
        try
        {
            cameraManager.ReloadCameraParameters(false);
            //cameraManager.activity.exposureTextView.setText("Exposure: " + String.valueOf(camera_parameters.getExposureCompensation()));
            Log.d(TAG, "Exposure:"+String.valueOf(cameraManager.mCamera.getParameters().getExposureCompensation()));
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Exposure set failed");
            ex.printStackTrace();
        }
    }*/

    public void setNightEnable(String val)
    {
        parameters.set("night_key",val);
        super.SetCameraParameters(parameters);
        //camera.setParameters(camera_parameters);
        onParametersCHanged(enumParameters.Tripod);
        cameraManager.ReloadCameraParameters(false);
    }







    //TODO FINDout min max value
    public void SetMFocus(int focus)
    {
        //mainActivity.focusButton.setEnabled(false);
        parameters.set("manual-focus", 0);
        parameters.setFocusMode("normal");
        parameters.set("manualfocus_step", focus);

        try
        {
            cameraManager.ReloadCameraParameters(false);

        }
        catch (Exception ex)
        {
            Log.e(TAG, "ManualFocus Failed");
        }
        onParametersCHanged(enumParameters.ManualFocus);
    }



    //preferences are set during CameraManager.Reset()
    /*private void setToPreferencesToCamera()
    {
        cameraManager.mCamera.setParameters(camera_parameters);
    }*/

    public boolean doCropping()
    {
        return preferences.CropImage.GET();
    }

    //TODO this is a wrong implementation, vnf-supported is used false;
    //TODO if it returns false its not supported set and get with vnf=enable,disable
    public class DenoiseClass
    {
        String value;
        String values;
        public DenoiseClass()
        {
            try
            {
                String d = parameters.get("vnf-supported");
                if (d.equals("true"))
                {
                    supportVNF = true;
                    value = "vnf";
                    values = "vnf-values";
                }
                else
                    supportVNF = false;
            }
            catch (Exception ex)
            {
                supportVNF = false;
            }
            if (supportVNF == false)
            {
                try
                {
                    String d = parameters.get("denoise-supported");
                    if (d.equals("true"))
                    {
                        supportVNF = true;
                        value = "denoise";
                        values = "denoise-values";
                    }
                }
                catch (Exception ex)
                {
                    supportVNF = false;
                }
            }
            Log.d(TAG,"supportVNF = Denoise:" + supportVNF);
        }
        public String[] getDenoiseValues()
        {
            String[] noise =  new String[0];

            noise = parameters.get(values).split(",");
            return noise;
        }

        public String getDenoiseValue()
        {
            return parameters.get(value);
        }

        public void Set(String toset)
        {
            Log.d(TAG,"set Denoise to:" + toset);
            parameters.set(value, toset);
            cameraManager.ReloadCameraParameters(false);
        }
    }

    public class ManualBrightnessManager
    {
        String brightnessValue;
        public ManualBrightnessManager()
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
                cameraManager.ReloadCameraParameters(false);
            }
            catch (Exception ex)
            {
                Log.e("brightness Set Fail", ex.getMessage());
            }
            onParametersCHanged(enumParameters.ManualBrightness);
            //cameraManager.activity.brightnessTextView.setText(String.valueOf(camera_parameters.get(brightnessValue)));

        }

        public int Get()
        {
            return parameters.getInt(brightnessValue);
        }

        public int GetMaxValue()
        {
            int max = 100;
            try {
                max = parameters.getInt("max-brightness");
            }
            catch (Exception ex)
            {

            }
            if (max > 0)
                return max;
            else return 100;
        }
        public int GetMinValue()
        {
            int min = 100;
            try {
                min = parameters.getInt("min-brightness");
            }
            catch (Exception ex)
            {

            }
            if (min > 0)
                return min;
            else return 0;
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

        public void Set(String value, boolean setToCamera)
        {
            String def = Get();

            Log.e(TAG, "Try to set Afp from" + def + " to " + value);
            if (!def.equals(value))
            {
                try
                {
                    parameters.set(afpValue, value);
                    if (setToCamera)
                        cameraManager.ReloadCameraParameters(false);
                }
                catch (Exception ex)
                {
                    Log.e(TAG, "Set afp failed back to def");
                    //camera_parameters.set(afpValue, def);
                    //cameraManager.ReloadCameraParameters(false);
                }
            }
            onParametersCHanged(enumParameters.AfPriority);
        }
		public String LazyHijack()
        {
            return parameters.get("picture-format");
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

        public void setValue(String toapplie, boolean settocamrera)
        {
            //String def = getValue();
            //if (!def.equals(toapplie))
            //{
                try {
                    Log.d(TAG, "Try to set Zeroshutterlag to:"+ toapplie);
                    parameters.set(value, toapplie);
                    if  (settocamrera)
                        cameraManager.ReloadCameraParameters(false);
                }
                catch (Exception ex)
                {
                    Log.e(TAG, "ZSL set failed set to " + toapplie);
                    //camera_parameters.set(def, toapplie);
                    //cameraManager.ReloadCameraParameters(false);
                }
                onParametersCHanged(enumParameters.ZeroShutterLag);
            //}

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

        public void set(String value , boolean setToCamera)
        {
            try {
                getParameters().setWhiteBalance(value);
                if (setToCamera)
                    cameraManager.ReloadCameraParameters(false);
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
                s_isoValues = "iso-mode-values";
                values = getParameters().get(s_isoValues).split(",");
                if (values != null && values.length > 0)
                    supportIso = true;
            }
            catch (Exception ex)
            {
                supportIso = false;
            }
            if (!supportIso)
            {
                try {
                    s_isoValues = "iso-values";
                    values = getParameters().get(s_isoValues).split(",");
                    if (values != null && values.length > 0)
                        supportIso = true;
                } catch (Exception ex) {
                    supportIso = false;
                }
            }
            Log.d(TAG,"support IsoModes:" + supportIso);
        }

        public String[] getValues()
        {
            return values;
        }

        public void set(String value, boolean setToCamera)
        {
            try {
                Log.d(TAG, "set iso to:" + value);
                parameters.set("iso", value);
                if (setToCamera)
                    cameraManager.ReloadCameraParameters(false);
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

        public void set(String val, boolean setTocamera)
        {
            String dev = get();
            if (!dev.equals(val))
            {
                try {
                    Log.d(TAG,"Try set Scene to:" + val);
                    getParameters().setSceneMode(val);
                    if(setTocamera)
                        cameraManager.ReloadCameraParameters(false);
                }
                catch (Exception ex)
                {
                    Log.e(TAG,"Scene set failed set back to" + dev);
                    getParameters().setSceneMode(dev);
                    cameraManager.ReloadCameraParameters(false);
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

        public void Set(String val, boolean settocam)
        {
            String dev = Get();
            if (!dev.equals(val))
            {
                try {
                    Log.d(TAG, "try set ipp to:" + val);
                    parameters.set("ipp", val);
                    if (settocam)
                    cameraManager.ReloadCameraParameters(false);
                }
                catch (Exception ex)
                {
                    Log.e(TAG,"ipp set failed, set back to:" + dev);
                    //camera_parameters.set("ipp", dev);
                    //cameraManager.ReloadCameraParameters(false);
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

        public void Set(String val ,boolean settocam)
        {
            String dev = Get();
            if (!dev.equals(val))
            {
                try
                {
                    Log.d(TAG, "try set previewformat to:" + val);
                    parameters.set("preview-format", val);
                    if (settocam)
                        cameraManager.ReloadCameraParameters(false);
                }
                catch (Exception ex)
                {
                    Log.e(TAG,"preview format set failed, set back to:" + dev);
                    parameters.set("preview-format", dev);
                    cameraManager.ReloadCameraParameters(false);
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

        public void Set(String val, boolean settocam)
        {
            int i = Integer.parseInt(val);
            int dev = parameters.getPreviewFrameRate();
            if (i != dev)
            {
                try {
                    Log.d(TAG, "try set preview fps  to:" + i + " from " + dev);
                    parameters.setPreviewFrameRate(i);
                    cameraManager.mCamera.stopPreview();
                    if (settocam)
                        cameraManager.ReloadCameraParameters(false);
                    cameraManager.mCamera.startPreview();
                }
                catch (Exception ex)
                {
                    Log.e(TAG,"preview fps set failed set:" + dev);
                    parameters.setPreviewFrameRate(dev);
                    cameraManager.ReloadCameraParameters(false);
                }
            }
            onParametersCHanged(enumParameters.PreviewFps);

        }
    }

    public class ManualSaturationClass
    {
        public int getMax()
        {
            int max = 0;
            try {
                max = Integer.parseInt(parameters.get("max-saturation"));
            }
            catch (Exception ex)
            {
            }
            try
            {
                max = Integer.parseInt(parameters.get("saturation-max"));
            }
            catch (Exception ex)
            {}
            if (max == 0)
                max = 100;
            return max;
        }

        public int getMin()
        {
            int min = 0;
            try {
                min = Integer.parseInt(parameters.get("min-saturation"));
            }
            catch (Exception ex)
            {
            }
            try
            {
                min = Integer.parseInt(parameters.get("saturation-min"));
            }
            catch (Exception ex)
            {}
            return min;
        }

        public int getValue()
        {
            return Integer.parseInt(parameters.get("saturation"));
        }

        public void set(int toset)
        {
            try
            {
                parameters.set("saturation", toset);
                cameraManager.ReloadCameraParameters(false);
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Manual saturation set failed");
            }
            onParametersCHanged(enumParameters.ManualSaturation);

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
            {}
            try {
                max = Integer.parseInt(parameters.get("max-sharpness"));
            }
            catch (Exception ex)
            {}
            if(max == 0)
                max = 100;
            return max;
        }

        public int getMin()
        {
            int min = 0;
            try {
                min = Integer.parseInt(parameters.get("sharpness-min"));
            }
            catch (Exception ex)
            {}
            try {
                min = Integer.parseInt(parameters.get("min-sharpness"));
            }
            catch (Exception ex)
            {}
            return min;
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
                cameraManager.ReloadCameraParameters(false);
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Manual Sharpness set failed");
            }
            onParametersCHanged(enumParameters.ManualSharpness);

        }
    }

    public class ManualShutterClass
    {
        public int getMax()
        {
            int max = 0;
            try {

                if(DeviceUtils.isZTEADV())
                {
                    max = 19;
                }
                if (DeviceUtils.isHTCADV())
                {
                    max = 47;
                }
            }
            catch (Exception ex)
            {
                if(DeviceUtils.isZTEADV())
                {
                    max = 16;
                }
                if (DeviceUtils.isHTCADV())
                {
                    max = 47;
                }
            }
            return max;
        }

        public int getValue()
        { int cv = 0;
            if(DeviceUtils.isZTEADV())
            {
                cv = 2;
            }
            if (DeviceUtils.isHTCADV())
            {
                cv = 10;
            }
            return cv;
        }

        public void set(int toset)
        {
            try
            {
                if(DeviceUtils.isHTCADV())
                {
                    switch (toset)
                    {
                        case 1 :
                            parameters.set("shutter", "4.0");
                            break;

                        case 2 :
                            parameters.set("shutter", "3.2");
                            break;

                        case 3 :
                            parameters.set("shutter", "2.5");
                            break;

                        case 4 :
                            parameters.set("shutter", "1.6");
                            break;

                        case 5 :
                            parameters.set("shutter", "1.3");
                            break;

                        case 6 :
                            parameters.set("shutter", "1.0");
                            break;

                        case 7 :
                            parameters.set("shutter", "0.8");
                            break;

                        case 8 :
                            parameters.set("shutter", "0.6");
                            break;

                        case 9 :
                            parameters.set("shutter", "0.5");
                            break;

                        case 10 :
                            parameters.set("shutter", "0.4");
                            break;

                        case 11 :
                            parameters.set("shutter", "0.3");
                            break;

                        case 12 :
                            parameters.set("shutter", "0.25");
                            break;

                        case 13 :
                            parameters.set("shutter", "0.2");
                            break;

                        case 14 :
                            parameters.set("shutter", "0.125");
                            break;

                        case 15 :
                            parameters.set("shutter", "0.1");
                            break;

                        case 16 :
                            parameters.set("shutter", "0.07");
                            break;

                        case 17 :
                            parameters.set("shutter", "0.06");
                            break;

                        case 18 :
                            parameters.set("shutter", "0.05");
                            break;

                        case 19 :
                            parameters.set("shutter", "0.04");
                            break;

                        case 20 :
                            parameters.set("shutter", "0.03");
                            break;

                        case 21 :
                            parameters.set("shutter", "0.025");
                            break;

                        case 22 :
                            parameters.set("shutter", "0.02");
                            break;

                        case 23 :
                            parameters.set("shutter", "0.01");
                            break;

                        case 24 :
                            parameters.set("shutter", "0.0125");
                            break;

                        case 25 :
                            parameters.set("shutter", "0.01");
                            break;

                        case 26 :
                            float a = 1 / 125;
                            String b = String.valueOf(a);
                            parameters.set("shutter", b);
                            break;

                        case 27 :
                            float a27 = 1 / 200;
                            String b27 = String.valueOf(a27);
                            parameters.set("shutter", b27);
                            break;

                        case 28 :
                            float a28 = 1 / 250;
                            String b28 = String.valueOf(a28);
                            parameters.set("shutter", b28);
                            break;

                        case 29 :
                            float a29 = 1 / 300;
                            String b29 = String.valueOf(a29);
                            parameters.set("shutter", b29);
                            break;

                        case 30 :
                            float a30 = 1 / 400;
                            String b30 = String.valueOf(a30);
                            parameters.set("shutter", b30);
                            break;

                        case 31 :
                            float a31 = 1 / 500;
                            String b31 = String.valueOf(a31);
                            parameters.set("shutter", b31);
                            break;

                        case 32 :
                            float a32 = 1 / 640;
                            String b32 = String.valueOf(a32);
                            parameters.set("shutter", b32);
                            break;

                        case 33 :
                            float a33 = 1 / 800;
                            String b33 = String.valueOf(a33);
                            parameters.set("shutter", b33);
                            break;

                        case 34 :
                            float a34 = 1 / 1000;
                            String b34 = String.valueOf(a34);
                            parameters.set("shutter", b34);
                            break;

                        case 35 :
                            float a35 = 1 / 1250;
                            String b35 = String.valueOf(a35);
                            parameters.set("shutter", b35);
                            break;

                        case 36 :
                            float a36 = 1 / 1600;
                            String b36 = String.valueOf(a36);
                            parameters.set("shutter", b36);
                            break;

                        case 37 :
                            float a37 = 1 / 2000;
                            String b37 = String.valueOf(a37);
                            parameters.set("shutter", b37);
                            break;

                        case 38 :
                            float a38 = 1 / 2500;
                            String b38 = String.valueOf(a38);
                            parameters.set("shutter", b38);
                            break;

                        case 39 :
                            float a39 = 1 / 3200;
                            String b39 = String.valueOf(a39);
                            parameters.set("shutter", b39);
                            break;

                        case 40 :
                            float a40 = 1 / 4000;
                            String b40 = String.valueOf(a40);
                            parameters.set("shutter", "4.0");
                            break;

                        case 41 :
                            float a41 = 1 / 5000;
                            String b41 = String.valueOf(a41);
                            parameters.set("shutter", b41);
                            break;

                        case 42 :
                            float a42 = 1 / 6400;
                            String b42 = String.valueOf(a42);
                            parameters.set("shutter", b42);
                            break;

                        case 43 :
                            float a43 = 1 / 8000;
                            String b43 = String.valueOf(a43);
                            parameters.set("shutter", b43);
                            break;

                    }

                    cameraManager.ReloadCameraParameters(false);
                }
                if (DeviceUtils.isZTEADV())
                {
                    parameters.set("exposure-time", toset);
                    cameraManager.ReloadCameraParameters(false);
                }

            }
            catch (Exception ex)
            {
                Log.e(TAG, "Manual Exposure set failed");
            }
            onParametersCHanged(enumParameters.ManualShutter);

        }
    }

    public class ManualFocusClass
    {
        public int getMax()
        {
            int max = 0;
            try {
                if(DeviceUtils.isHTCADV())
                {
                    max = Integer.parseInt(parameters.get("max-focus"));
                }
                if (DeviceUtils.isLGADV())
                {
                    max = Integer.parseInt(parameters.get("shutter"));
                }
                if(DeviceUtils.isZTEADV())
                {
                    max = Integer.parseInt(parameters.get("max-focus-pos-index"));
                }
            }
            catch (Exception ex)
            {
                max = 100;
            }
            return max;
        }

        public int getMin()
        {
            int min = 0;
            if (DeviceUtils.isZTEADV())
                min = parameters.getInt("min-focus-pos-index");
            return min;
        }

        public int getValue()
        {
            int val = 0;
            if(DeviceUtils.isHTCADV())
            {
                val = Integer.parseInt(parameters.get("current-focus-step"));
            }
            if(DeviceUtils.isLGADV())
            {
                val = Integer.parseInt(parameters.get("manualfocus_step"));

            }
            if (DeviceUtils.isZTEADV())
                val = parameters.getInt("focus-pos-index");

            return val;
        }

        public void set(int toset)
        {
            try
            {
                if(DeviceUtils.isHTCADV())
                {
                    parameters.set("focus", toset);
                    cameraManager.ReloadCameraParameters(false);
                }
                if(DeviceUtils.isLGADV())
                {
                    parameters.setFocusMode("normal");
                    parameters.set("manual-focus", toset);
                    cameraManager.ReloadCameraParameters(false);
                }
                if(DeviceUtils.isZTEADV())
                {
                    parameters.set("focus-pos-index", toset);
                    cameraManager.ReloadCameraParameters(false);
                }

            }
            catch (Exception ex)
            {
                Log.e(TAG, "Manual Focus set failed");
            }
            onParametersCHanged(enumParameters.ManualFocus);

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
                cameraManager.ReloadCameraParameters(false);
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Set Exposure failed set back to last");
                parameters.setExposureCompensation(def);
                cameraManager.ReloadCameraParameters(false);
            }
            onParametersCHanged(enumParameters.ManualExposure);
        }
    }

    public class ManualContrastClass
    {
        public int getMax()
        {
            int max = 100;
            try
            {
                 max = Integer.parseInt(parameters.get("max-contrast"));
            }
            catch (Exception ex)
            {
            }
            try
            {
                max = Integer.parseInt(parameters.get("contrast-max"));
            }
            catch (Exception ex)
            {
            }
            return max;
        }

        public int getMin()
        {
            int min = 0;
            try {
                min = Integer.parseInt(parameters.get("min-contrast"));
                min = Integer.parseInt(parameters.get("contrast-min"));
            }
            catch (Exception ex)
            {
            }
            try {

                min = Integer.parseInt(parameters.get("contrast-min"));
            }
            catch (Exception ex)
            {
            }
            return min;
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
                cameraManager.ReloadCameraParameters(false);

            }
            catch (Exception ex)
            {
                Log.e(TAG,"Contrast Set Fail");
                ex.printStackTrace();
            }
            onParametersCHanged(enumParameters.ManualContrast);
        }
    }

    public class ManualConvergenceClass
    {
        int min = 0;
        int max = 0;
        public ManualConvergenceClass()
        {

            try
            {
                max = Integer.parseInt(parameters.get("supported-manual-convergence-max"));
                min = Integer.parseInt(parameters.get("supported-manual-convergence-min"));

                supportManualConvergence = true;
            }
            catch (Exception ex)
            {
                supportManualConvergence = false;
            }
        }

        public int get()
        {
            return Integer.parseInt(parameters.get("manual-convergence"));
        }

        public void set(int val)
        {
            try
            {
                Log.d(TAG, "Try to set manual convergence to " + val);
                parameters.set("manual-convergence", val);
                cameraManager.ReloadCameraParameters(false);
            }
            catch (Exception ex)
            {
                Log.e(TAG, "manualconvergence set failed");
            }
        }

        public int getMin()
        {
            return min;
        }
        public int getMax()
        {
            return max;
        }
    }

    public class ZoomManager
    {
        int min;
        int max;
        int current;

        public ZoomManager()
        {
            max = parameters.getMaxZoom();
            min = 1;
        }

        public int get()
        {
            return parameters.getZoom();
        }

        public void set(int value)
        {
            parameters.setZoom(value);
            cameraManager.ReloadCameraParameters(false);
        }
    }
}
