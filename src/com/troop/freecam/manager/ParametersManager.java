package com.troop.freecam.manager;

import android.content.SharedPreferences;
import android.util.Log;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.manager.interfaces.ParametersChangedInterface;
import com.troop.freecam.manager.interfaces.PreviewSizeChangedInterface;
import com.troop.freecam.utils.DeviceUtils;

/**
 * Created by troop on 16.10.13.
 */
public class ParametersManager
{
    public static final String SwitchCamera = "switchcam";
    public static final String SwitchCamera_MODE_3D = "3D";
    public static final String SwitchCamera_MODE_2D = "2D";
    public static final String SwitchCamera_MODE_Front = "Front";
    public static final String Preferences_Flash3D = "3d_flash";
    public static final String Preferences_Flash2D = "2d_flash";
    public static final String Preferences_Focus2D = "2d_focus";
    public static final String Preferences_Focus3D = "3d_focus";
    public static final String Preferences_FocusFront = "front_focus";
    public static final String Preferences_WhiteBalanceFront = "front_whitebalance";
    public static final String Preferences_WhiteBalance3D = "3d_whitebalance";
    public static final String Preferences_WhiteBalance2D = "2d_whitebalance";
    public static final String Preferences_SceneFront = "front_scene";
    public static final String Preferences_Scene3D = "3d_scene";
    public static final String Preferences_Scene2D = "2d_scene";
    public static final String Preferences_Color2D = "2d_color";
    public static final String Preferences_Color3D = "3d_color";
    public static final String Preferences_ColorFront = "front_color";
    public static final String Preferences_IsoFront = "front_iso";
    public static final String Preferences_Iso3D = "3d_iso";
    public static final String Preferences_Iso2D = "2d_iso";
    public static final String Preferences_Exposure2D = "2d_exposure";
    public static final String Preferences_Exposure3D = "3d_exposure";
    public static final String Preferences_ExposureFront = "front_exposure";
    public static final String Preferences_PictureSize2D = "2d_picturesize";
    public static final String Preferences_PictureSize3D = "3d_picturesize";
    public static final String Preferences_PictureSizeFront = "front_picturesize";
    public static final String Preferences_PreviewSize2D = "2d_previewsize";
    public static final String Preferences_PreviewSize3D = "3d_previewsize";
    public static final String Preferences_PreviewSizeFront = "front_previewsize";
    public static final String Preferences_IPP2D = "2d_ipp";
    public static final String Preferences_IPP3D = "3d_ipp";
    public static final String Preferences_IPPFront = "front_ipp";
    //New Pref 07-12-13
    public static final String Preferences_PictureFormat = "picture_format";
    public static final String Preferences_PreviewFormat = "preview_format";
    public static final String Preferences_AFPValue = "focus_priority";
    public static final String Preferences_MTRValue = "meter_priority";
    public static final String Preferences_Denoise = "denoise";
   // public static final String Preferences_Stab = "stablization";
    public static final String Preferences_ZSL = "zsl_value";
    //public static final String Preferences_Composition = "front_ipp";
    //public static final String Preferences_HFR = "hfr_video";

    CameraManager cameraManager;
    MainActivity mainActivity;
    android.hardware.Camera.Parameters parameters;
    public android.hardware.Camera.Parameters getParameters(){return parameters;}
    SharedPreferences preferences;
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
    private ParametersChangedInterface parametersChanged;

    public ParametersManager(CameraManager cameraManager, SharedPreferences preferences)
    {
        this.cameraManager = cameraManager;
        this.preferences = preferences;
    }

    public void SetCameraParameters(android.hardware.Camera.Parameters parameters)
    {
        this.parameters = parameters;
        Log.d("CameraParameters", parameters.flatten());
        checkParametersSupport();
        loadDefaultOrLastSavedSettings();
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
        if (parametersChanged != null)
            parametersChanged.parametersHasChanged(false);
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
            int i = parameters.getInt("brightness");
            supportBrightness = true;
        }
        catch (Exception ex)
        {
            supportBrightness = false;
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

        try {
            if (DeviceUtils.isQualcomm())
                if(!parameters.get("selectable-zone-af-values").equals(""))
                    supportAfpPriority = true;
            if (DeviceUtils.isOmap())
                if(!parameters.get("auto-convergence-mode-values").equals(""))
                    supportAfpPriority = true;
        }
        catch (Exception ex)
        {
            supportAfpPriority = false;
        }
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

    private void loadDefaultOrLastSavedSettings()
    {
        String tmp = preferences.getString(SwitchCamera, SwitchCamera_MODE_Front);
        parameters.set("preview-format", "yuv420p");
        if (tmp.equals("3D"))
        {
            parameters.setFlashMode(preferences.getString(Preferences_Flash3D, "auto"));
            parameters.setFocusMode(preferences.getString(Preferences_Focus3D, "auto"));
            parameters.setWhiteBalance(preferences.getString(Preferences_WhiteBalance3D,"auto"));
            parameters.setSceneMode(preferences.getString(Preferences_Scene3D,"auto"));
            parameters.setColorEffect(preferences.getString(Preferences_Color3D,"none"));
            parameters.set("iso", preferences.getString(Preferences_Iso3D, "auto"));
            parameters.set("exposure", preferences.getString(Preferences_Exposure3D , "auto"));
            setPictureSize(preferences.getString(Preferences_PictureSize3D , "320x240"));
            //setPictureSize("2592x1458");
            setPreviewSize(preferences.getString(Preferences_PreviewSize3D, "320x240"));
            parameters.set("ipp",preferences.getString(Preferences_IPP3D, "ldc-nsf"));

        }

        if(tmp.equals("2D"))
        {
            parameters.setFlashMode(preferences.getString(Preferences_Flash2D, "auto"));
            parameters.setFocusMode(preferences.getString(Preferences_Focus2D, "auto"));
            parameters.setWhiteBalance(preferences.getString(Preferences_WhiteBalance2D,"auto"));
            parameters.setSceneMode(preferences.getString(Preferences_Scene2D,"auto"));
            parameters.setColorEffect(preferences.getString(Preferences_Color2D,"none"));
            parameters.set("iso", preferences.getString(Preferences_Iso2D, "auto"));

            parameters.set("exposure", preferences.getString(Preferences_Exposure2D , "auto"));

            setPictureSize(preferences.getString(Preferences_PictureSize2D , "320x240"));
            setPreviewSize(preferences.getString(Preferences_PreviewSize2D, "320x240"));

            parameters.set("ipp",preferences.getString(Preferences_IPP2D, "ldc-nsf"));

            if(DeviceUtils.isQualcomm())
            {
                parameters.set("denoise","denoise-off");
                parameters.set("power-mode","Normal_Power");
                parameters.set("mce","disable");
            }
        }
        if (tmp.equals("Front"))
        {
            if (preferences.getString(Preferences_FocusFront, null) != null)
            {
                parameters.setFocusMode(preferences.getString(Preferences_FocusFront, null));
            }
            if (preferences.getString(Preferences_WhiteBalanceFront, null) != null)
                parameters.setWhiteBalance(preferences.getString(Preferences_WhiteBalanceFront, null));
            if (preferences.getString(Preferences_SceneFront, null) != null)
                parameters.setSceneMode(preferences.getString(Preferences_SceneFront,null));
            if (preferences.getString(Preferences_ColorFront, null) != null)
                parameters.setColorEffect(preferences.getString(Preferences_ColorFront,"none"));
            if (preferences.getString(Preferences_IsoFront, null) != null)
                parameters.set("iso", preferences.getString(Preferences_IsoFront, null));
            if (preferences.getString(Preferences_ExposureFront , null) != null)
                parameters.set("exposure", preferences.getString(Preferences_ExposureFront , "auto"));
            if (preferences.getString(Preferences_PictureSizeFront , null) != null)
                setPictureSize(preferences.getString(Preferences_PictureSizeFront , "320x240"));
            if (preferences.getString(Preferences_PreviewSizeFront, null) != null)
                setPreviewSize(preferences.getString(Preferences_PreviewSizeFront, "320x240"));
            if (preferences.getString(Preferences_IPPFront, null) != null)
                parameters.set("ipp",preferences.getString(Preferences_IPPFront, "ldc-nsf"));
        }
        //parameters.set("rawsave-mode", "1");
        //parameters.set("rawfname", "/mnt/sdcard/test.raw");

        onParametersCHanged();
        setToPreferencesToCamera();
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
            setToPreferencesToCamera();
            cameraManager.activity.exposureTextView.setText("Exposure: " + String.valueOf(parameters.getExposureCompensation()));
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
            setToPreferencesToCamera();
            Log.d("ParametersMAnager", "Contrast:"+String.valueOf(cameraManager.mCamera.getParameters().getExposureCompensation()));
        }
        catch (Exception ex)
        {
            Log.e("Contrast Set Fail", ex.getMessage());
        }
        cameraManager.activity.contrastTextView.setText(String.valueOf(parameters.get("contrast")));

    }

    public void SetBrightness(int bright)
    {
        parameters.set("brightness", bright);
        onParametersCHanged();
        try
        {
            setToPreferencesToCamera();
            Log.d("ParametersMAnager", "brightness:"+String.valueOf(cameraManager.mCamera.getParameters().getExposureCompensation()));
        }
        catch (Exception ex)
        {
            Log.e("brightness Set Fail", ex.getMessage());
        }
        cameraManager.activity.brightnessTextView.setText(String.valueOf(parameters.get("brightness")));

    }

    public void SetMFocus(int focus)
    {
        mainActivity.focusButton.setEnabled(false);
        parameters.set("manual-focus", 0);
        parameters.setFocusMode("normal");
        parameters.set("manualfocus_step", focus);
        onParametersCHanged();
        try
        {
            setToPreferencesToCamera();
            Log.d("ParametersMAnager", "brightness:"+String.valueOf(cameraManager.mCamera.getParameters().getExposureCompensation()));
        }
        catch (Exception ex)
        {
            Log.e("brightness Set Fail", ex.getMessage());
        }
        cameraManager.activity.brightnessTextView.setText(String.valueOf(parameters.get("brightness")));

    }

    public void SetJpegQuality(int quality)
    {
        parameters.set("jpeg-quality", quality);
        onParametersCHanged();
        setToPreferencesToCamera();
    }

    private void setToPreferencesToCamera()
    {
        cameraManager.mCamera.setParameters(parameters);
    }

    public boolean doCropping()
    {
       return preferences.getBoolean("crop", false);
    }

    public boolean is3DMode()
    {
        String camvalue = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_2D);
        if (camvalue.equals(ParametersManager.SwitchCamera_MODE_3D))
        {
            return true;
        }
        else
            return false;
    }

    public boolean is2DMode()
    {
        String camvalue = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_2D);
        if (camvalue.equals(ParametersManager.SwitchCamera_MODE_2D))
        {
            return true;
        }
        else
            return false;
    }

    public boolean isFrontMode()
    {
        String camvalue = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_2D);
        if (camvalue.equals(ParametersManager.SwitchCamera_MODE_Front))
        {
            return true;
        }
        else
            return false;
    }

    public boolean isOrientationFIX()
    {
        if(preferences.getBoolean("upsidedown", false))
            return true;
        else
            return false;
    }

    public void setOrientationFix(boolean value)
    {
        preferences.edit().putBoolean("upsidedown", value).commit();
    }

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
        return preferences.getString(Preferences_Denoise, "false");
    }
}
