package com.troop.freecam.manager;

import android.content.SharedPreferences;

import com.troop.freecam.camera.CameraManager;

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

    public static final String Preferences_ZSL2D = "2d_zsl";
    public static final String Preferences_ZSL3D = "3d_zsl";
    public static final String Preferences_ZSLFront = "front_zsl";

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
    public ZeroShutterLagClass ZeroShutterLag;

    public SettingsManager(CameraManager cameraManager, SharedPreferences preferences) {
        this.cameraManager = cameraManager;
        this.preferences = preferences;
        Cameras = new CamerasClass();
        ImagePostProcessing = new ImagePostProcessingClass();
        ZeroShutterLag = new ZeroShutterLagClass();
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

    public class BaseClass
    {
        public String threeD;
        protected String twoD;
        protected String front;
        protected String defaultVal;

        public BaseClass(String threeD, String twoD, String front, String defaultVal) {
            this.threeD = threeD;
            this.twoD = twoD;
            this.front = front;
            this.defaultVal = defaultVal;
        }

        public void Set(String val)
        {
            switch (Cameras.GetCameraEnum())
            {
                case Back3D:
                    preferences.edit().putString(threeD, val).commit();
                    break;
                case Front:
                    preferences.edit().putString(front, val).commit();
                    break;
                case Back2D:
                    preferences.edit().putString(twoD, val).commit();
                    break;
            }
        }

        public String Get()
        {
            String val;
            switch (Cameras.GetCameraEnum())
            {
                case Back3D:
                    val = preferences.getString(threeD, defaultVal);
                    break;
                case Front:
                    val = preferences.getString(front, defaultVal);
                    break;
                case Back2D:
                    val =  preferences.getString(twoD, defaultVal);
                    break;
                default:
                    val =  preferences.getString(SettingsManager.Preferences_IPPFront, defaultVal);
            }
            return val;
        }
    }

    public class ImagePostProcessingClass extends BaseClass
    {
        public ImagePostProcessingClass()
        {
            super(SettingsManager.Preferences_IPP3D, SettingsManager.Preferences_IPP2D, SettingsManager.Preferences_IPPFront, "ldc-nsf");
        }
    }

    public class ZeroShutterLagClass extends BaseClass
    {
        public ZeroShutterLagClass()
        {
            super(SettingsManager.Preferences_ZSL3D, SettingsManager.Preferences_ZSL2D, SettingsManager.Preferences_ZSLFront, "high-quality");
        }
    }
}
