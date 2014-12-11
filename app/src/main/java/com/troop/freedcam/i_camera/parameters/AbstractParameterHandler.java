package com.troop.freedcam.i_camera.parameters;

import com.troop.freedcam.camera.parameters.CameraParametersEventHandler;
import com.troop.freedcam.camera.parameters.manual.BrightnessManualParameter;
import com.troop.freedcam.camera.parameters.manual.CCTManualParameter;
import com.troop.freedcam.camera.parameters.manual.ContrastManualParameter;
import com.troop.freedcam.camera.parameters.manual.ConvergenceManualParameter;
import com.troop.freedcam.camera.parameters.manual.ExposureManualParameter;
import com.troop.freedcam.camera.parameters.manual.FXManualParameter;
import com.troop.freedcam.camera.parameters.manual.FocusManualParameter;
import com.troop.freedcam.camera.parameters.manual.ISOManualParameter;
import com.troop.freedcam.camera.parameters.manual.SaturationManualParameter;
import com.troop.freedcam.camera.parameters.manual.SharpnessManualParameter;
import com.troop.freedcam.camera.parameters.manual.ShutterManualParameter;
import com.troop.freedcam.camera.parameters.manual.ZoomManualParameter;
import com.troop.freedcam.camera.parameters.modes.AE_Bracket_HdrModeParameter;
import com.troop.freedcam.camera.parameters.modes.AntiBandingModeParameter;
import com.troop.freedcam.camera.parameters.modes.ColorModeParameter;
import com.troop.freedcam.camera.parameters.modes.DenoiseParameter;
import com.troop.freedcam.camera.parameters.modes.DigitalImageStabilizationParameter;
import com.troop.freedcam.camera.parameters.modes.ExposureModeParameter;
import com.troop.freedcam.camera.parameters.modes.FlashModeParameter;
import com.troop.freedcam.camera.parameters.modes.FocusModeParameter;
import com.troop.freedcam.camera.parameters.modes.HistogramModeParameter;
import com.troop.freedcam.camera.parameters.modes.ImagePostProcessingParameter;
import com.troop.freedcam.camera.parameters.modes.IsoModeParameter;
import com.troop.freedcam.camera.parameters.modes.JpegQualityParameter;
import com.troop.freedcam.camera.parameters.modes.LensshadeParameter;
import com.troop.freedcam.camera.parameters.modes.MemoryColorEnhancementParameter;
import com.troop.freedcam.camera.parameters.modes.NightModeParameter;
import com.troop.freedcam.camera.parameters.modes.NonZslManualModeParameter;
import com.troop.freedcam.camera.parameters.modes.PictureFormatParameter;
import com.troop.freedcam.camera.parameters.modes.PictureSizeParameter;
import com.troop.freedcam.camera.parameters.modes.PreviewFormatParameter;
import com.troop.freedcam.camera.parameters.modes.PreviewFpsParameter;
import com.troop.freedcam.camera.parameters.modes.PreviewSizeParameter;
import com.troop.freedcam.camera.parameters.modes.RedEyeParameter;
import com.troop.freedcam.camera.parameters.modes.SceneDetectParameter;
import com.troop.freedcam.camera.parameters.modes.SceneModeParameter;
import com.troop.freedcam.camera.parameters.modes.SkinToneParameter;
import com.troop.freedcam.camera.parameters.modes.VideoHDRModeParameter;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesG3Parameter;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.camera.parameters.modes.VideoSizeParameter;
import com.troop.freedcam.camera.parameters.modes.WhiteBalanceModeParameter;
import com.troop.freedcam.camera.parameters.modes.ZeroShutterLagParameter;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractParameterHandler
{
    public CameraParametersEventHandler ParametersEventHandler;

    public I_ManualParameter ManualBrightness;
    public I_ManualParameter ManualSharpness;
    public I_ManualParameter ManualContrast;
    public I_ManualParameter ManualSaturation;
    public I_ManualParameter ManualExposure;
    public I_ManualParameter ManualConvergence;
    public I_ManualParameter ManualFocus;
    public I_ManualParameter ManualShutter;
    public I_ManualParameter CCT;
    public I_ManualParameter FX;
    public I_ManualParameter ISOManual;
    public I_ManualParameter Zoom;

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

    public void SetParametersToCamera() {};
    public void LockExposureAndWhiteBalance(boolean lock){};
}
