package com.troop.freecam.manager;

import android.content.SharedPreferences;

import com.troop.freecam.CameraManager;

/**
 * Created by troop on 04.01.14.
 */
public class SettingsManager
{
    public static final String SwitchCamera = "switchcam";
    public static final String SwitchCamera_MODE_3D = "3D";
    public static final String SwitchCamera_MODE_2D = "2D";
    public static final String SwitchCamera_MODE_Front = "Front";

    public static final String Preferences_IPP2D = "2d_ipp";
    public static final String Preferences_IPP3D = "3d_ipp";
    public static final String Preferences_IPPFront = "front_ipp";

    enum CameraValues
    {
        Front,
        Back2D,
        Back3D,
    }

    CameraManager cameraManager;
    SharedPreferences preferences;
    public CamerasClass Cameras;
    public ImagePostProcessingClass ImagePostProcessing;

    public SettingsManager(CameraManager cameraManager, SharedPreferences preferences) {
        this.cameraManager = cameraManager;
        this.preferences = preferences;
        Cameras = new CamerasClass();
        ImagePostProcessing = new ImagePostProcessingClass();
    }

    public class CamerasClass
    {
        public void SetCamera(String value)
        {
            preferences.edit().putString(SwitchCamera, value).commit();
        }

        public String GetCamera()
        {
            return preferences.getString(SwitchCamera, SwitchCamera_MODE_Front);
        }

        public CameraValues GetCameraEnum()
        {
            if (GetCamera() == SwitchCamera_MODE_3D)
                return CameraValues.Back3D;
            else if (GetCamera() == SwitchCamera_MODE_2D)
                return CameraValues.Back2D;
            else
                return CameraValues.Front;
        }

        public void SetCameraEnum(CameraValues values)
        {
            if (values == CameraValues.Back3D)
                SetCamera(SwitchCamera_MODE_3D);
            if (values == CameraValues.Back2D)
                SetCamera(SwitchCamera_MODE_2D);
            if (values == CameraValues.Front)
                SetCamera(SwitchCamera_MODE_Front);
        }
    }

    public class ImagePostProcessingClass
    {
        public void Set(String val)
        {
            switch (Cameras.GetCameraEnum())
            {
                case Back3D:
                    preferences.edit().putString(SettingsManager.Preferences_IPP3D, val).commit();
                    break;
                case Front:
                    preferences.edit().putString(SettingsManager.Preferences_IPPFront, val).commit();
                    break;
                case Back2D:
                    preferences.edit().putString(SettingsManager.Preferences_IPP2D, val).commit();
                    break;
            }
        }

        public String Get()
        {
            String val;
            switch (Cameras.GetCameraEnum())
            {
                case Back3D:
                    val = preferences.getString(SettingsManager.Preferences_IPP3D, "ldc-nsf");
                    break;
                case Front:
                    val = preferences.getString(SettingsManager.Preferences_IPPFront, "ldc-nsf");
                    break;
                case Back2D:
                    val =  preferences.getString(SettingsManager.Preferences_IPP2D, "ldc-nsf");
                break;
                default:
                    val =  preferences.getString(SettingsManager.Preferences_IPPFront, "ldc-nsf");
            }
            return val;
        }
    }
}
