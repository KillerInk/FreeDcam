package com.troop.freedcam.i_camera.parameters;

import android.os.Handler;

import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractParameterHandler
{
    /**
     * Holds the UI/Main Thread
     */
    protected Handler uiHandler;

    /**
     * Holds the basic parameters has loaded event, listner should register there if the need to know if the parameters has loaded/changed
     */
    public CameraParametersEventHandler ParametersEventHandler;

    public AbstractManualParameter ManualBrightness;
    public AbstractManualParameter ManualSharpness;
    public AbstractManualParameter ManualContrast;
    public AbstractManualParameter ManualSaturation;
    public AbstractManualParameter ManualExposure;
    public AbstractManualParameter ManualConvergence;
    public AbstractManualParameter ManualFocus;
    public AbstractManualParameter ManualShutter;
    public AbstractManualParameter ManualFNumber;
    public AbstractManualParameter Burst;
    public AbstractManualParameter CCT;
    public AbstractManualParameter FX;
    public AbstractManualParameter ISOManual;
    public AbstractManualParameter Zoom;
    public AbstractManualParameter Skintone;
    public AbstractManualParameter ProgramShift;

    public AbstractModeParameter ColorMode;
    public AbstractModeParameter ExposureMode;
    public AbstractModeParameter FlashMode;
    public AbstractModeParameter IsoMode;
    public AbstractModeParameter AntiBandingMode;
    public AbstractModeParameter WhiteBalanceMode;
    public AbstractModeParameter PictureSize;
    public AbstractModeParameter PictureFormat;
    public AbstractModeParameter JpegQuality;
    //defcomg was here
    public AbstractModeParameter GuideList;
    //done
    public AbstractModeParameter ImagePostProcessing;
    public AbstractModeParameter PreviewSize;
    public AbstractModeParameter PreviewFPS;
    public AbstractModeParameter PreviewFormat;
    public AbstractModeParameter SceneMode;
    public AbstractModeParameter FocusMode;
    public AbstractModeParameter RedEye;
    public AbstractModeParameter LensShade;
    public AbstractModeParameter ChromaFlash;
    public AbstractModeParameter ZSL;
    public AbstractModeParameter SceneDetect;
    public AbstractModeParameter Denoise;
    public AbstractModeParameter DigitalImageStabilization;
    public AbstractModeParameter MemoryColorEnhancement;
    public AbstractModeParameter SkinToneEnhancment;
    public AbstractModeParameter NightMode;
    public AbstractModeParameter NonZslManualMode;
    public AbstractModeParameter AE_Bracket;
    public AbstractModeParameter Histogram;
    public AbstractModeParameter ExposureLock;
    public AbstractModeParameter CDS_Mode;

    public AbstractModeParameter VideoProfiles;
    public AbstractModeParameter VideoProfilesG3;
    public AbstractModeParameter VideoSize;
    public AbstractModeParameter VideoHDR;
    public AbstractModeParameter VideoHighSpeedVideo;
    public AbstractModeParameter CameraMode;

    //yet only seen on m9
    public AbstractModeParameter RdiMode;
    public AbstractModeParameter TnrMode;
    public AbstractModeParameter SecureMode;

    //SonyApi
    public AbstractModeParameter ContShootMode;
    public AbstractModeParameter ContShootModeSpeed;
    public AbstractModeParameter ObjectTracking;
    public AbstractModeParameter PostViewSize;
    public AbstractModeParameter Focuspeak;
    //
    public AbstractModeParameter ThemeList;
    public boolean isExposureAndWBLocked = false;
    private boolean isDngActive = false;
    public boolean IsDngActive(){ return this.isDngActive; };
    public void SetDngActive(boolean active) {this.isDngActive = active;}
    public boolean isAeBracketActive = false;

    public AbstractCameraHolder cameraHolder;
    public AbstractCameraChanged cameraChanged;
    protected AppSettingsManager appSettingsManager;

    //camera2 modes
    public AbstractModeParameter EdgeMode;
    public AbstractModeParameter ColorCorrectionMode;
    public AbstractModeParameter HotPixelMode;
    public AbstractModeParameter ToneMapMode;
    public AbstractModeParameter ControlMode;

    public AbstractModeParameter oismode;

    public LocationParameter locationParameter;

    public boolean IntervalCapture = false;
    public boolean IntervalCaptureFocusSet = false;

    public AbstractParameterHandler(AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager, Handler uiHandler)
    {
        this.appSettingsManager = appSettingsManager;
        this.cameraHolder = cameraHolder;
        this.uiHandler = uiHandler;
        GuideList = new GuideList(uiHandler);
        ThemeList = new ThemeList(uiHandler);
        locationParameter = new LocationParameter(uiHandler, appSettingsManager,cameraHolder);
    }

    public void SetParametersToCamera() {};
    public void LockExposureAndWhiteBalance(boolean lock){};

    public void SetFocusAREA(FocusRect focusAreas, FocusRect meteringAreas){};
    public void SetMeterAREA(FocusRect meteringAreas){};
    public void SetPictureOrientation(int or){};

    public void SetAppSettingsToParameters()
    {
        setMode(locationParameter, AppSettingsManager.SETTING_LOCATION);
        setMode(ColorMode, AppSettingsManager.SETTING_COLORMODE);
        setMode(ExposureMode, AppSettingsManager.SETTING_EXPOSUREMODE);
        setMode(FlashMode, AppSettingsManager.SETTING_FLASHMODE);
        setMode(IsoMode, AppSettingsManager.SETTING_ISOMODE);
        setMode(AntiBandingMode, AppSettingsManager.SETTING_ANTIBANDINGMODE);
        setMode(WhiteBalanceMode, AppSettingsManager.SETTING_WHITEBALANCEMODE);
        setMode(PictureSize, AppSettingsManager.SETTING_PICTURESIZE);
        setMode(PictureFormat, AppSettingsManager.SETTING_PICTUREFORMAT);
        setMode(oismode, AppSettingsManager.SETTING_OIS);

        setMode(JpegQuality, AppSettingsManager.SETTING_JPEGQUALITY);
        setMode(GuideList, AppSettingsManager.SETTING_GUIDE);
        setMode(ImagePostProcessing, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);
        setMode(SceneMode, AppSettingsManager.SETTING_SCENEMODE);
        setMode(FocusMode, AppSettingsManager.SETTING_FOCUSMODE);
        setMode(RedEye,AppSettingsManager.SETTING_REDEYE_MODE);
        setMode(LensShade,AppSettingsManager.SETTING_LENSSHADE_MODE);
        setMode(ChromaFlash,AppSettingsManager.SETTING_CHROMAFLASH_MODE);
        setMode(ZSL, AppSettingsManager.SETTING_ZEROSHUTTERLAG_MODE);
        setMode(SceneDetect, AppSettingsManager.SETTING_SCENEDETECT_MODE);
        setMode(Denoise, AppSettingsManager.SETTING_DENOISE_MODE);
        setMode(DigitalImageStabilization, AppSettingsManager.SETTING_DIS_MODE);
        setMode(MemoryColorEnhancement, AppSettingsManager.SETTING_MCE_MODE);
        //setMode(SkinToneEnhancment, AppSettingsManager.SETTING_SKINTONE_MODE);
        setMode(NightMode, AppSettingsManager.SETTING_NIGHTEMODE);
        setMode(NonZslManualMode, AppSettingsManager.SETTING_NONZSLMANUALMODE);
        setMode(AE_Bracket, AppSettingsManager.SETTING_AEBRACKET);
        setMode(Histogram, AppSettingsManager.SETTING_HISTOGRAM);
        setMode(VideoProfiles, AppSettingsManager.SETTING_VIDEPROFILE);
        setMode(VideoProfilesG3, AppSettingsManager.SETTING_VIDEPROFILE);
        setMode(VideoHDR, AppSettingsManager.SETTING_VIDEOHDR);
        setMode(VideoSize, AppSettingsManager.SETTING_VIDEOSIZE);
        setMode(WhiteBalanceMode,AppSettingsManager.SETTING_WHITEBALANCEMODE);
        setMode(ImagePostProcessing,AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);
        setMode(ColorCorrectionMode, AppSettingsManager.SETTING_COLORCORRECTION);
        setMode(EdgeMode, AppSettingsManager.SETTING_EDGE);
        setMode(HotPixelMode, AppSettingsManager.SETTING_HOTPIXEL);
        setMode(ToneMapMode, AppSettingsManager.SETTING_TONEMAP);
        setMode(ControlMode, AppSettingsManager.SETTING_CONTROLMODE);
        //setMode(Focuspeak, AppSettingsManager.SETTING_FOCUSPEAK);
        if (appSettingsManager.getString(AppSettingsManager.SETTING_DNG).equals(""))
            appSettingsManager.setString(AppSettingsManager.SETTING_DNG, "false");
        if (appSettingsManager.getString(AppSettingsManager.SETTING_DNG).equals("true"))
            this.isDngActive = true;
        else
            this.isDngActive = false;

        setManualMode(ManualBrightness, AppSettingsManager.MWB);
        setManualMode(ManualContrast, AppSettingsManager.MCONTRAST);
        setManualMode(ManualConvergence,AppSettingsManager.MCONVERGENCE);
        setManualMode(ManualExposure, AppSettingsManager.MEXPOSURE);
        //setManualMode(ManualFocus, AppSettingsManager.MF);
        setManualMode(ManualSharpness,AppSettingsManager.MSHARPNESS);
        setManualMode(ManualShutter, AppSettingsManager.MSHUTTERSPEED);
        setManualMode(ManualBrightness, AppSettingsManager.MBRIGHTNESS);
        //setManualMode(ISOManual, AppSettingsManager.MISO);
        setManualMode(ManualSaturation, AppSettingsManager.MSATURATION);
        //setManualMode(CCT,AppSettingsManager.MWB);


    }

    protected void setMode(AbstractModeParameter parameter, String settingsval)
    {
        if (parameter != null && parameter.IsSupported() && settingsval != null && !settingsval.equals(""))
        {
            if (appSettingsManager.getString(settingsval).equals(""))
                appSettingsManager.setString(settingsval, parameter.GetValue());
            else
                parameter.SetValue(appSettingsManager.getString(settingsval), false);
        }
    }

    protected void setManualMode(AbstractManualParameter parameter, String settingsval)
    {
        if (parameter != null && parameter.IsSupported() && settingsval != null && !settingsval.equals(""))
        {
            if (appSettingsManager.getString(settingsval).equals(""))
                appSettingsManager.setString(settingsval, parameter.GetValue()+"");
            else
                parameter.SetValue(Integer.parseInt(appSettingsManager.getString(settingsval)));
        }
    }
}
