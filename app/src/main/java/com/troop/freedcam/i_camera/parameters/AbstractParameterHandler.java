package com.troop.freedcam.i_camera.parameters;

import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;

import com.troop.freedcam.camera.parameters.CameraParametersEventHandler;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractParameterHandler
{
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
    public AbstractManualParameter ManualFNumber;
    public AbstractManualParameter Burst;
    public AbstractManualParameter CCT;
    public AbstractManualParameter FX;
    public AbstractManualParameter ISOManual;
    public AbstractManualParameter Zoom;

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

    public AbstractModeParameter VideoProfiles;
    public AbstractModeParameter VideoProfilesG3;
    public AbstractModeParameter VideoSize;
    public AbstractModeParameter VideoHDR;
    public AbstractModeParameter CameraMode;

    //SonyApi
    public AbstractModeParameter ContShootMode;
    public AbstractModeParameter ContShootModeSpeed;
    //

    public boolean isExposureAndWBLocked = false;
    public boolean isDngActive = false;
    public boolean isAeBracketActive = false;

    public AbstractCameraHolder cameraHolder;
    protected AppSettingsManager appSettingsManager;

    public AbstractParameterHandler(AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager, Handler uiHandler)
    {
        this.appSettingsManager = appSettingsManager;
        this.cameraHolder = cameraHolder;
        this.uiHandler = uiHandler;
        GuideList = new GuideList();
    }

    public void SetParametersToCamera() {};
    public void LockExposureAndWhiteBalance(boolean lock){};

    public void SetFocusAREA(FocusRect focusAreas, int weight){};
}
