package com.troop.freecam.manager;

import android.content.SharedPreferences;

import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 04.01.14.
 */
public class SettingsManager
{
    public class Preferences
    {
    public static final String SwitchCamera = "switchcam";
    public static final String MODE_3D = "3D";
    public static final String MODE_2D = "2D";
    public static final String MODE_Front = "Front";

    public static final String IPP2D = "2d_ipp";
    public static final String IPP3D = "3d_ipp";
    public static final String IPPFront = "front_ipp";

    public static final String ZSL2D = "2d_zsl";
    public static final String ZSL3D = "3d_zsl";
    public static final String ZSLFront = "front_zsl";

    public static final String Flash3D = "3d_flash";
    public static final String Flash2D = "2d_flash";
    public static final String FlashFront = "front_flash";
    }

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
    public FlashModeClass FlashMode;

    public SettingsManager(CameraManager cameraManager, SharedPreferences preferences) {
        this.cameraManager = cameraManager;
        this.preferences = preferences;
        Cameras = new CamerasClass();
        ImagePostProcessing = new ImagePostProcessingClass();
        ZeroShutterLag = new ZeroShutterLagClass();
        FlashMode = new FlashModeClass();
    }

    public class CamerasClass
    {
        public void SetCamera(String value)
        {
            preferences.edit().putString(Preferences.SwitchCamera, value).commit();
        }

        public String GetCamera()
        {
            return preferences.getString(Preferences.SwitchCamera, Preferences.MODE_Front);
        }

        public CameraValues GetCameraEnum()
        {
            if (GetCamera() == Preferences.MODE_3D)
                return CameraValues.Back3D;
            else if (GetCamera() == Preferences.MODE_2D)
                return CameraValues.Back2D;
            else
                return CameraValues.Front;
        }

        public void SetCameraEnum(CameraValues values)
        {
            if (values == CameraValues.Back3D)
                SetCamera(Preferences.MODE_3D);
            if (values == CameraValues.Back2D)
                SetCamera(Preferences.MODE_2D);
            if (values == CameraValues.Front)
                SetCamera(Preferences.MODE_Front);
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
                    val =  preferences.getString(SettingsManager.Preferences.IPPFront, defaultVal);
            }
            return val;
        }
    }

    public class ImagePostProcessingClass extends BaseClass
    {
        public ImagePostProcessingClass()
        {
            super(SettingsManager.Preferences.IPP3D, SettingsManager.Preferences.IPP2D, SettingsManager.Preferences.IPPFront, "ldc-nsf");
        }
    }

    public class ZeroShutterLagClass extends BaseClass
    {
        public ZeroShutterLagClass()
        {
            super(SettingsManager.Preferences.ZSL3D, SettingsManager.Preferences.ZSL2D, SettingsManager.Preferences.ZSLFront, "high-quality");
        }
    }

    public class FlashModeClass extends BaseClass
    {
        public FlashModeClass()
        {
            super(Preferences.Flash3D, Preferences.Flash2D, Preferences.FlashFront, "off");
        }
    }
}
