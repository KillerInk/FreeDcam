package com.troop.freecam.manager;

import android.content.SharedPreferences;

import com.troop.freecam.manager.parameters.LensShadeManager;
import com.troop.freecam.manager.parameters.ParametersManager;
import com.troop.freecam.manager.parameters.PictureParameters;

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

        public static final String Focus2D = "2d_focus";
        public static final String Focus3D = "3d_focus";
        public static final String FocusFront = "front_focus";
        
       // public static final String PictureFormat2D = "2d_pictureformat";
       // public static final String PictureFormat3D = "3d_pictureformat";
       // public static final String PictureFormatFront = "front_pictureformat";

        public static final String PictureSize2D = "2d_picturesize";
        public static final String PictureSize3D = "3d_picturesize";
        public static final String PictureSizeFront = "front_picturesize";

        public static final String Exposure2D = "2d_exposure";
        public static final String Exposure3D = "3d_exposure";
        public static final String ExposureFront = "front_exposure";

        public static final String MTRValueFront = "front_meter_priority";
        public static final String MTRValue2D = "2d_meter_priority";
        public static final String MTRValue3D = "3d_meter_priority";

        public static final String WhiteBalanceFront = "front_whitebalance";
        public static final String WhiteBalance3D = "3d_whitebalance";
        public static final String WhiteBalance2D = "2d_whitebalance";

        public static final String SceneFront = "front_scene";
        public static final String Scene3D = "3d_scene";
        public static final String Scene2D = "2d_scene";

        public static final String Color2D = "2d_color";
        public static final String Color3D = "3d_color";
        public static final String ColorFront = "front_color";

        public static final String IsoFront = "front_iso";
        public static final String Iso3D = "3d_iso";
        public static final String Iso2D = "2d_iso";

        public static final String PreviewSize2D = "2d_previewsize";
        public static final String PreviewSize3D = "3d_previewsize";
        public static final String PreviewSizeFront = "front_previewsize";

        public static final String VideoSize2D = "2d_videosize";
        public static final String VideoSize3D = "3d_videosize";
        public static final String VideoSizeFront = "front_videosize";

        public static final String PreviewFormat2D = "2d_previewformat";
        public static final String PreviewFormat3D = "3d_previewformat";
        public static final String PreviewFormatFront = "front_previewformat";

        public static final String AfPriority2D = "2d_afpriority";
        public static final String AfPriority3D = "3d_afpriority";
        public static final String AfPriorityFront = "front_afpriority";

        public static final String PreviewFps2D = "2d_previewfps";
        public static final String PreviewFps3D = "3d_previewfps";
        public static final String PreviewFpsFront = "front_previewfps";

        public static final String Antibanding2D = "2d_antibanding";
        public static final String Antibanding3D = "3d_antibanding";
        public static final String AntibandingFront = "front_antibanding";

        public static final String MODE_VIDEO = "video";
        public static final String MODE_PIC = "pic";
        public static final String MODE_HDR = "hdr";
    }

    public enum CameraValues
    {
        Front,
        Back2D,
        Back3D,
    }

    public int CameraCount = 0;
    public int CurrentCamera = 0;

    SharedPreferences preferences;
    public CamerasClass Cameras;
    public ImagePostProcessingClass ImagePostProcessing;
    public ZeroShutterLagClass ZeroShutterLag;
    public FlashModeClass FlashMode;
    public OrientationFixClass OrientationFix;
    public CropImageClass CropImage;
    public FocusModeClass FocusMode;
    public PictureSizeClass PictureSize;
    
    public ExposureModeClass ExposureMode;
    public MeteringModeClass MeteringMode;
    public WhiteBalanceModeClass WhiteBalanceMode;
    public SceneModeClass SceneMode;
    public ColorModeClass ColorMode;
    public IsoModeClass IsoMode;
    public PreviewSizeClass PreviewSize;
    public VideoSizeClass VideoSize;
    public PreviewFormatClass PreviewFormat;
    public AfPriorityClass afPriority;
    public PreviewFpsClass PreviewFps;
    public HDRSettingsClass HDRSettings;
    public AntiBandingClass Antibanding;
    public LensShadeClass LensShade;
    public CameraModes CameraMode;
    public VideoProfiles VideoProfileSETTINGS;
    public CaptureFrameRate captureFrameRate;
    public PictureFormatClass pictureFormat;


    public SettingsManager(SharedPreferences preferences) {
        this.preferences = preferences;
        Cameras = new CamerasClass();
        ImagePostProcessing = new ImagePostProcessingClass();
        ZeroShutterLag = new ZeroShutterLagClass();
        FlashMode = new FlashModeClass();
        OrientationFix = new OrientationFixClass();
        CropImage = new CropImageClass();
        FocusMode = new FocusModeClass();
        PictureSize = new PictureSizeClass();
      
        ExposureMode = new ExposureModeClass();
        MeteringMode = new MeteringModeClass();
        WhiteBalanceMode = new WhiteBalanceModeClass();
        SceneMode = new SceneModeClass();
        ColorMode = new ColorModeClass();
        IsoMode = new IsoModeClass();
        PreviewSize = new PreviewSizeClass();
        VideoSize = new VideoSizeClass();
        PreviewFormat = new PreviewFormatClass();
        afPriority = new AfPriorityClass();
        PreviewFps = new PreviewFpsClass();
        HDRSettings = new HDRSettingsClass();
        Antibanding = new AntiBandingClass();
        LensShade = new LensShadeClass();
        CameraMode = new CameraModes();
        VideoProfileSETTINGS =  new VideoProfiles();
        captureFrameRate = new CaptureFrameRate();
        pictureFormat = new PictureFormatClass();

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

        public boolean is3DMode()
        {
            if (GetCamera().equals(SettingsManager.Preferences.MODE_3D))
            {
                return true;
            }
            else
                return false;
        }

        public boolean is2DMode()
        {
            if (GetCamera().equals(SettingsManager.Preferences.MODE_2D))
            {
                return true;
            }
            else
                return false;
        }

        public boolean isFrontMode()
        {
            if (GetCamera().equals(SettingsManager.Preferences.MODE_Front))
            {
                return true;
            }
            else
                return false;
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
                    val =  preferences.getString(front, defaultVal);
            }
            return val;
        }
    }

    public class WhiteBalanceModeClass extends BaseClass
    {
        public WhiteBalanceModeClass()
        {
            super(Preferences.WhiteBalance3D, Preferences.WhiteBalance2D, Preferences.WhiteBalanceFront, "");
        }
    }

    public class AntiBandingClass extends BaseClass
    {
        public AntiBandingClass()
        {
            super(Preferences.Antibanding3D, Preferences.Antibanding2D, Preferences.AntibandingFront, "");
        }
    }

    public class PreviewFpsClass extends BaseClass
    {

        public PreviewFpsClass() {
            super(Preferences.PreviewFps3D, Preferences.PreviewFps2D, Preferences.PreviewFpsFront, "");
        }
    }

    public class PreviewSizeClass extends BaseClass
    {
        public PreviewSizeClass()
        {
            super(Preferences.PreviewSize3D, Preferences.PreviewSize2D, Preferences.PreviewSizeFront, "");
        }
    }

    public class AfPriorityClass extends BaseClass
    {
        public AfPriorityClass()
        {
            super(Preferences.AfPriority3D, Preferences.AfPriority2D, Preferences.AfPriorityFront, "");
        }
    }

    public class PreviewFormatClass extends BaseClass
    {
        public PreviewFormatClass()
        {
            super(Preferences.PreviewFormat3D, Preferences.PreviewFormat2D, Preferences.PreviewFormatFront, "");
        }
    }

    public class VideoSizeClass extends BaseClass
    {
        public VideoSizeClass()
        {
            super(Preferences.VideoSize3D, Preferences.VideoSize2D, Preferences.VideoSizeFront, "640x480");
        }
    }

    public class PictureFormatClass extends BaseClass
    {
        public PictureFormatClass()
        {
            super(PictureParameters.Preferences_PictureFormat3D, PictureParameters.Preferences_PictureFormat2D, PictureParameters.Preferences_PictureFormatFront, "jpeg");
        }
    }

    public class SceneModeClass extends BaseClass
    {
        public SceneModeClass()
        {
            super(Preferences.Scene3D, Preferences.Scene2D, Preferences.SceneFront, "");
        }
    }

    public class IsoModeClass extends BaseClass
    {
        public IsoModeClass()
        {
            super(Preferences.Iso3D, Preferences.Iso2D, Preferences.IsoFront, "");
        }
    }

    public class ColorModeClass extends BaseClass
    {
        public ColorModeClass()
        {
            super(Preferences.Color3D, Preferences.Color2D, Preferences.ColorFront, "");
        }
    }

    public class MeteringModeClass extends BaseClass
    {
        public MeteringModeClass()
        {
            super(Preferences.MTRValue3D, Preferences.MTRValue2D, Preferences.MTRValueFront, "");
        }
    }

    public class ImagePostProcessingClass extends BaseClass
    {
        public ImagePostProcessingClass()
        {
            super(SettingsManager.Preferences.IPP3D, SettingsManager.Preferences.IPP2D, SettingsManager.Preferences.IPPFront, "");
        }
    }

    public class ZeroShutterLagClass extends BaseClass
    {
        public ZeroShutterLagClass()
        {
            super(SettingsManager.Preferences.ZSL3D, SettingsManager.Preferences.ZSL2D, SettingsManager.Preferences.ZSLFront, "");
        }
    }

    public class FlashModeClass extends BaseClass
    {
        public FlashModeClass()
        {
            super(Preferences.Flash3D, Preferences.Flash2D, Preferences.FlashFront, "");
        }
    }

    public class FocusModeClass extends BaseClass
    {
        public FocusModeClass()
        {
            super(Preferences.Focus3D, Preferences.Focus2D, Preferences.FocusFront, "");
        }
    }

    public class PictureSizeClass extends BaseClass
    {
        public PictureSizeClass()
        {
            super(Preferences.PictureSize3D, Preferences.PictureSize2D, Preferences.PictureSizeFront, "");
        }
    }
    

    public class ExposureModeClass extends BaseClass
    {
        public ExposureModeClass()
        {
            super(Preferences.Exposure3D, Preferences.Exposure2D, Preferences.ExposureFront, "");
        }
    }

    public class OrientationFixClass
    {
        public boolean GET()
        {
            if(preferences.getBoolean("upsidedown", false))
                return true;
            else
                return false;
        }

        public void Set(boolean value)
        {
            preferences.edit().putBoolean("upsidedown", value).commit();
        }
    }

    public class CropImageClass
    {
        public boolean GET()
        {
            if(preferences.getBoolean("crop", false))
                return true;
            else
                return false;
        }

        public void Set(boolean value)
        {
            preferences.edit().putBoolean("crop", value).commit();
        }
    }

    public class HDRSettingsClass
    {
        public void setHighExposure(int highExposure)
        {
            preferences.edit().putInt("highexposure", highExposure).commit();
        }
        public int getHighExposure()
        {
            return preferences.getInt("highexposure", 10);
        }

        public void setNormalExposure(int highExposure)
        {
            preferences.edit().putInt("normalexposure", highExposure).commit();
        }
        public int getNormalExposure()
        {
            return preferences.getInt("normalexposure", 0);
        }

        public void setLowExposure(int highExposure)
        {
            preferences.edit().putInt("lowexposure", highExposure).commit();
        }
        public int getLowExposure()
        {
            return preferences.getInt("lowexposure", -10);
        }

        public String getHighIso()
        {
            return preferences.getString("highiso", "auto");
        }
        public void setHighIso(String iso)
        {
            preferences.edit().putString("highiso", iso).commit();
        }
        public String getNormalIso()
        {
            return preferences.getString("normaliso", "auto");
        }
        public void setNormalIso(String iso)
        {
            preferences.edit().putString("normaliso", iso).commit();
        }
        public String getLowIso()
        {
            return preferences.getString("lowiso", "auto");
        }
        public void setLowIso(String iso)
        {
            preferences.edit().putString("lowiso", iso).commit();
        }



    }
    public class LensShadeClass
    {
        public void set(boolean value)
        {
            preferences.edit().putBoolean("lensshade", value).commit();
        }

        public boolean get()
        {
            return preferences.getBoolean("lensshade", true);
        }
    }

    public class CameraModes
    {
        String value;

        public CameraModes()
        {
            value = preferences.getString("mode", Preferences.MODE_PIC);
        }

        public void set(String value)
        {
            preferences.edit().putString("mode", value).commit();
            this.value = value;
        }

        public String get() { return value;}
    }

    public class VideoProfiles
    {
        public void set(String value)
        {
            preferences.edit().putString("videoprofile", value).commit();
        }

        public String get()
        {
            return preferences.getString("videoprofile", "LOW");
        }
    }

    public class CaptureFrameRate
    {
        public void set(float value)
        {
            preferences.edit().putFloat("captureFrame", value).commit();
        }

        public float get()
        {
            return preferences.getFloat("captureFrame", 30);
        }
    }
}
