package com.troop.freecam.manager.camera_parameters;

import android.util.Log;

import com.troop.freecam.camera.old.CameraManager;
import com.troop.freecam.manager.AppSettingsManager;

/**
 * Created by troop on 09.08.2014.
 */
public class PictureParameters extends LensShadeManager
{
    public PictureParameters(CameraManager cameraManager, AppSettingsManager preferences) {
        super(cameraManager, preferences);
    }

    public static final String  Preferences_PictureFormat3D = "picture_format_3d";
    public static final String  Preferences_PictureFormat2D = "picture_format_2d";
    public static final String  Preferences_PictureFormatFront = "picture_format_front";
    public static String Preferences_PictureFormatx = "jpeg";
    boolean val;

    @Override
    protected void loadDefaultOrLastSavedSettings() {
        super.loadDefaultOrLastSavedSettings();
        if (!cameraManager.Settings.PictureSize.Get().equals(""))
            setPictureSize(cameraManager.Settings.PictureSize.Get());
        if (!cameraManager.Settings.pictureFormat.Get().equals(""))
            setPictureFormat(cameraManager.Settings.pictureFormat.Get());
    }

    public void GetCamP (String value )
    {
        if (value != "jpeg")
        {
            val = true;
        }

    }

    public boolean isRaw()
    {
        return val;
    }


    public void setCamP (String value,String v2 )
    {
        parameters.set(value, v2);

    }

    public String getCamP (String value )
    {
        String o = parameters.get(value);
        return o;

    }

    public void string_set(String S)
    {
        Preferences_PictureFormatx = S;
    }

    public void setPictureSize(String s)
    {
        String[] widthHeight = s.split("x");
        int w = Integer.parseInt(widthHeight[0]);
        int h = Integer.parseInt(widthHeight[1]);
        parameters.setPictureSize(w,h);
        onParametersCHanged(enumParameters.PictureSize);
        cameraManager.ReloadCameraParameters(false);
        Log.d(TAG, "set picture size to " + s);
    }

    public void setPictureFormat(String format)
    {
        parameters.set("picture-format", format);
        cameraManager.ReloadCameraParameters(false);
        onParametersCHanged(enumParameters.PictureFormat);
    }

    public String getPictureFormat()
    {
        if (parameters != null)
            return parameters.get("picture-format");
        else
            return "";
    }

    public String[] getPictureFormatValues()
    {
        return parameters.get("picture-format-values").split(",");
    }

    public void setExynosRaw(String val)
    {
        parameters.set("capture-mode",val);
        super.SetCameraParameters(parameters);
        onParametersCHanged(enumParameters.ExynosRaw);
        cameraManager.ReloadCameraParameters(false);
    }

    public void SetJpegQuality(int quality)
    {
        parameters.set("jpeg-quality", quality);
        //onParametersCHanged(enumParameters.All);
        //setToPreferencesToCamera();
    }
}
