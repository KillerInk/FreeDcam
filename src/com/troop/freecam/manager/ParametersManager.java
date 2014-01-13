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
        loadDefaultOrLastSavedSettings();
        loadingParametersFinish = true;
        onParametersCHanged(true);
    }

    public void setParametersChanged(ParametersChangedInterface parametersChangedInterface)
    {
        this.parametersChanged = parametersChangedInterface;
    }

    public void UpdateUI()
    {
        onParametersCHanged();
    }

    private void onParametersCHanged()
    {
        if (parametersChanged != null && loadingParametersFinish)
            parametersChanged.parametersHasChanged(false);
    }

    private void onParametersCHanged(boolean reloadGui)
    {
        if (parametersChanged != null && loadingParametersFinish)
            parametersChanged.parametersHasChanged(reloadGui);
    }

    private void checkParametersSupport()
    {
        try
        {
            int i = parameters.getInt("sharpness");
            supportSharpness = true;
        }
        catch (Exception ex)
        {
            supportSharpness = false;
        }
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

        if (parameters.getFlashMode() != null)
            supportFlash = true;
        else
            supportFlash = false;
        try
        {
            if (!parameters.get("vnf-supported").equals(""))
                supportVNF = true;
        }
        catch (Exception ex)
        {
            supportVNF = false;
        }
        try
        {
            if (!parameters.get("auto-exposure-values").equals(""))
                supportAutoExposure= true;
        }
        catch (Exception ex)
        {
            supportAutoExposure = false;
        }
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
        //parameters.set("rawsave-mode", "1");
        //parameters.set("rawfname", "/mnt/sdcard/test.raw");

        Log.d("freecam.ParametersManager", "Finished Loading Default Or Last Saved Settings");

        onParametersCHanged();
        cameraManager.Restart(false);
    }

    public PreviewSizeChangedInterface setPreviewSizeCHanged;

    private void onpreviewsizehasChanged(int w, int h)
    {
        if (setPreviewSizeCHanged != null)
            setPreviewSizeCHanged.onPreviewsizeHasChanged(w, h);
    }

    private void setPictureSize(String s)
    {
        String[] widthHeight = s.split("x");
        int w = Integer.parseInt(widthHeight[0]);
        int h = Integer.parseInt(widthHeight[1]);
        parameters.setPictureSize(w,h);
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
    }

    public void SetPreviewSizeToCameraParameters(int w, int h)
    {
        parameters.setPreviewSize(w,h);
        onpreviewsizehasChanged(w,h);
    }

    public void SetExposureCompensation(int exp)
    {
        //cameraManager.parameters.setExposureCompensation(exp);
        parameters.set("exposure-compensation", exp);
        onParametersCHanged();
        try
        {
            cameraManager.Restart(false);
            //cameraManager.activity.exposureTextView.setText("Exposure: " + String.valueOf(parameters.getExposureCompensation()));
            Log.d("ParametersMAnager", "Exposure:"+String.valueOf(cameraManager.mCamera.getParameters().getExposureCompensation()));
        }
        catch (Exception ex)
        {
            Log.e("Exposure Set Fail", ex.getMessage());
        }
    }

    public void SetContrast(int contrast)
    {
        parameters.set("contrast", contrast);
        onParametersCHanged();
        try
        {
            cameraManager.Restart(false);
            Log.d("ParametersMAnager", "Contrast:"+String.valueOf(cameraManager.mCamera.getParameters().getExposureCompensation()));
        }
        catch (Exception ex)
        {
            Log.e("Contrast Set Fail", ex.getMessage());
        }
    }



    public void SetMFocus(int focus)
    {
        //mainActivity.focusButton.setEnabled(false);
        parameters.set("manual-focus", 0);
        parameters.setFocusMode("normal");
        parameters.set("manualfocus_step", focus);
        onParametersCHanged();
        try
        {
            cameraManager.Restart(false);

        }
        catch (Exception ex)
        {
            Log.e("brightness Set Fail", ex.getMessage());
        }
    }

    public void SetJpegQuality(int quality)
    {
        parameters.set("jpeg-quality", quality);
        onParametersCHanged();
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





    public class DenoiseClass
    {
        public String[] getDenoiseValues()
        {
            String[] noise =  new String[0];
            if(DeviceUtils.isOmap())
            {
                noise = parameters.get("vnf-supported").split(",");
                if (noise.length == 1)
                {
                    String[] tmp = new String[2];
                    tmp[0] = noise[0];
                    tmp[1] = "false";
                    noise = tmp;
                }
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
        }

        public void Set(int bright)
        {
            parameters.set(brightnessValue, bright);
            onParametersCHanged();
            try
            {
                cameraManager.Restart(false);
            }
            catch (Exception ex)
            {
                Log.e("brightness Set Fail", ex.getMessage());
            }
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
        }

        public void Set(String value)
        {
            parameters.set(afpValue, value);
            onParametersCHanged();
            try
            {
                cameraManager.Restart(false);
            }
            catch (Exception ex)
            {
                Log.e("brightness Set Fail", ex.getMessage());
            }
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
        }

        public String[] getValues()
        {
            return values;
        }

        public void setValue(String toapplie)
        {
            parameters.set(value, toapplie);
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
        }

        public String[] getValues()
        {
            return values;
        }

        public void set(String value)
        {
            getParameters().setWhiteBalance(value);
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
        }

        public String[] getValues()
        {
            return values;
        }

        public void set(String value)
        {
            parameters.set("iso", value);
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
            getParameters().set("exposure", value);
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
            getParameters().setSceneMode(val);
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
            parameters.set("ipp", val);
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
            parameters.set("preview-format", val);
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
}
