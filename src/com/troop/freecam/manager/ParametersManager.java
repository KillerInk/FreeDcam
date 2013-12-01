package com.troop.freecam.manager;

import android.content.SharedPreferences;
import android.graphics.Camera;
import android.util.Log;

import com.troop.freecam.CameraManager;

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


    CameraManager cameraManager;
    android.hardware.Camera.Parameters parameters;
    public android.hardware.Camera.Parameters getParameters(){return parameters;}
    SharedPreferences preferences;
    boolean supportSharpness = false;
    public boolean getSupportSharpness() { return supportSharpness;};

    public ParametersManager(CameraManager cameraManager, SharedPreferences preferences)
    {
        this.cameraManager = cameraManager;
        this.preferences = preferences;
    }

    public void SetCameraParameters(android.hardware.Camera.Parameters parameters)
    {
        this.parameters = parameters;
        checkParametersSupport();
        loadDefaultOrLastSavedSettings();
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
    }

    private void loadDefaultOrLastSavedSettings()
    {
        String tmp = preferences.getString(SwitchCamera, SwitchCamera_MODE_3D);
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
        }
        if (tmp.equals("Front"))
        {
            parameters.setFocusMode(preferences.getString(Preferences_FocusFront, "auto"));
            parameters.setWhiteBalance(preferences.getString(Preferences_WhiteBalanceFront,"auto"));
            parameters.setSceneMode(preferences.getString(Preferences_SceneFront,"auto"));
            parameters.setColorEffect(preferences.getString(Preferences_ColorFront,"none"));
            parameters.set("iso", preferences.getString(Preferences_IsoFront, "auto"));
            parameters.set("exposure", preferences.getString(Preferences_ExposureFront , "auto"));
            setPictureSize(preferences.getString(Preferences_PictureSizeFront , "320x240"));
            setPreviewSize(preferences.getString(Preferences_PreviewSizeFront, "320x240"));
            parameters.set("ipp",preferences.getString(Preferences_IPPFront, "ldc-nsf"));
        }
        cameraManager.mCamera.setParameters(parameters);
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
    }

    public void SetExposureCompensation(int exp)
    {
        //cameraManager.parameters.setExposureCompensation(exp);
        parameters.set("exposure-compensation", exp);
        try
        {
            cameraManager.mCamera.setParameters(parameters);
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
        try
        {
            cameraManager.mCamera.setParameters(parameters);
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
        try
        {
            cameraManager.mCamera.setParameters(parameters);
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
        setToPreferencesToCamera();
    }

    private void setToPreferencesToCamera()
    {
        cameraManager.mCamera.setParameters(parameters);
    }
}
