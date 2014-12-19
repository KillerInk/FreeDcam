package com.troop.freedcam.i_camera.parameters;

import android.os.Handler;
import android.os.HandlerThread;

import com.troop.freedcam.camera.parameters.CameraParametersEventHandler;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractParameterHandler
{
    protected HandlerThread backGroundThread;
    protected Handler backGroundHandler;
    protected Handler uiHandler;

    public CameraParametersEventHandler ParametersEventHandler;

    public AbstractManualParameter ManualBrightness;
    public AbstractManualParameter ManualSharpness;
    public AbstractManualParameter ManualContrast;
    public AbstractManualParameter ManualSaturation;
    public AbstractManualParameter ManualExposure;
    public AbstractManualParameter ManualConvergence;
    public AbstractManualParameter ManualFocus;
    public AbstractManualParameter ManualShutter;
    public AbstractManualParameter CCT;
    public AbstractManualParameter FX;
    public AbstractManualParameter ISOManual;
    public AbstractManualParameter Zoom;

    public I_ModeParameter ColorMode;
    public I_ModeParameter ExposureMode;
    public I_ModeParameter FlashMode;
    public I_ModeParameter IsoMode;
    public I_ModeParameter AntiBandingMode;
    public I_ModeParameter WhiteBalanceMode;
    public I_ModeParameter PictureSize;
    public I_ModeParameter PictureFormat;
    public I_ModeParameter JpegQuality;
    public I_ModeParameter ImagePostProcessing;
    public I_ModeParameter PreviewSize;
    public I_ModeParameter PreviewFPS;
    public I_ModeParameter PreviewFormat;
    public I_ModeParameter SceneMode;
    public I_ModeParameter FocusMode;
    public I_ModeParameter RedEye;
    public I_ModeParameter LensShade;
    public I_ModeParameter ZSL;
    public I_ModeParameter SceneDetect;
    public I_ModeParameter Denoise;
    public I_ModeParameter DigitalImageStabilization;
    public I_ModeParameter MemoryColorEnhancement;
    public I_ModeParameter SkinToneEnhancment;
    public I_ModeParameter NightMode;
    public I_ModeParameter NonZslManualMode;
    public I_ModeParameter AE_Bracket;
    public I_ModeParameter Histogram;

    public I_ModeParameter VideoProfiles;
    public I_ModeParameter VideoProfilesG3;
    public I_ModeParameter VideoSize;
    public I_ModeParameter VideoHDR;
    public I_ModeParameter CameraMode;

    public boolean isExposureAndWBLocked = false;

    public boolean rawSupported;
    public boolean dngSupported;
    public String BayerMipiFormat;

    public AbstractCameraHolder cameraHolder;
    protected AppSettingsManager appSettingsManager;

    public AbstractParameterHandler(AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager, Handler backGroundHandler, Handler uiHandler)
    {
        this.appSettingsManager = appSettingsManager;
        this.cameraHolder = cameraHolder;
        this.backGroundHandler = backGroundHandler;
        this.uiHandler = uiHandler;
    }

    public void SetParametersToCamera() {};
    public void LockExposureAndWhiteBalance(boolean lock){};
}
