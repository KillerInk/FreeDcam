package com.troop.freedcam.camera.parameters;

import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
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
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.camera.parameters.modes.VideoSizeParameter;
import com.troop.freedcam.camera.parameters.modes.WhiteBalanceModeParameter;
import com.troop.freedcam.camera.parameters.modes.ZeroShutterLagParameter;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.util.List;

/**
 * Created by troop on 17.08.2014.
 */
public class CamParametersHandler implements I_ParameterChanged
{

    String TAG = "freedcam.CameraParametersHandler";
    public BaseCameraHolder cameraHolder;
    Camera.Parameters cameraParameters;
    public Camera.Parameters getParameters(){return cameraParameters;}

    public boolean rawSupported;
    public boolean dngSupported;
    public String BayerMipiFormat;


    public BrightnessManualParameter ManualBrightness;
    public SharpnessManualParameter ManualSharpness;
    public ContrastManualParameter ManualContrast;
    public SaturationManualParameter ManualSaturation;
    public ExposureManualParameter ManualExposure;
    public ConvergenceManualParameter ManualConvergence;
    public FocusManualParameter ManualFocus;
    public ShutterManualParameter ManualShutter;
    public CCTManualParameter CCT;
    public FXManualParameter FX;
    public ISOManualParameter ISOManual;

    public ColorModeParameter ColorMode;
    public ExposureModeParameter ExposureMode;
    public FlashModeParameter FlashMode;
    public IsoModeParameter IsoMode;
    public AntiBandingModeParameter AntiBandingMode;
    public WhiteBalanceModeParameter WhiteBalanceMode;
    public PictureSizeParameter PictureSize;
    public PictureFormatParameter PictureFormat;
    public JpegQualityParameter JpegQuality;
    public ImagePostProcessingParameter ImagePostProcessing;
    public PreviewSizeParameter PreviewSize;
    public PreviewFpsParameter PreviewFPS;
    public PreviewFormatParameter PreviewFormat;
    public ZoomManualParameter Zoom;
    public SceneModeParameter SceneMode;
    public FocusModeParameter FocusMode;
    public RedEyeParameter RedEye;
    public LensshadeParameter LensShade;
    public ZeroShutterLagParameter ZSL;
    public SceneDetectParameter SceneDetect;
    public DenoiseParameter Denoise;
    public DigitalImageStabilizationParameter DigitalImageStabilization;
    public MemoryColorEnhancementParameter MemoryColorEnhancement;
    public SkinToneParameter SkinToneEnhancment;
    public NightModeParameter NightMode;
    public NonZslManualModeParameter NonZslManualMode;
    public AE_Bracket_HdrModeParameter AE_Bracket;
    public HistogramModeParameter Histogram;

    public VideoProfilesParameter VideoProfiles;
    public VideoSizeParameter VideoSize;

    //public I_ParametersLoaded OnParametersLoaded;

    public CameraParametersEventHandler ParametersEventHandler;

    boolean moreParametersToSet = false;

    SetParameterRunner setParameterRunner;

    public CamParametersHandler(BaseCameraHolder cameraHolder)
    {
        this.cameraHolder = cameraHolder;
        ParametersEventHandler = new CameraParametersEventHandler();
    }

    public void GetParametersFromCamera()
    {
        cameraParameters = cameraHolder.GetCameraParameters();
    }

    public void SetParametersToCamera()
    {
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    public void LoadParametersFromCamera()
    {
        cameraParameters = cameraHolder.GetCameraParameters();
        setParameterRunner = new SetParameterRunner();
        initParameters();
    }

    private void logParameters(Camera.Parameters parameters)
    {
        String[] paras =  parameters.flatten().split(";");
        for(int i = 0; i < paras.length; i++)
            Log.d(TAG, paras[i]);
        Log.d(TAG, Build.MODEL) ;
    }

    private void initParameters()
    {
        logParameters(cameraParameters);
        ManualBrightness = new BrightnessManualParameter(cameraParameters, "","","");
        ManualContrast = new ContrastManualParameter(cameraParameters, "", "", "");
        ManualConvergence = new ConvergenceManualParameter(cameraParameters, "manual-convergence", "supported-manual-convergence-max", "supported-manual-convergence-min");
        ManualExposure = new ExposureManualParameter(cameraParameters,"","","");
        ManualFocus = new FocusManualParameter(cameraParameters,"","","", cameraHolder);
        ManualSaturation = new SaturationManualParameter(cameraParameters,"","","");
        ManualSharpness = new SharpnessManualParameter(cameraParameters, "", "", "");
        ManualShutter = new ShutterManualParameter(cameraParameters,"","","", cameraHolder);
        CCT = new CCTManualParameter(cameraParameters,"","","");
        FX = new FXManualParameter(cameraParameters,"","","");
        ISOManual = new ISOManualParameter(cameraParameters,"","","");

        ColorMode = new ColorModeParameter(cameraParameters,this, "", "");
        ExposureMode = new ExposureModeParameter(cameraParameters,this,"","");
        FlashMode = new FlashModeParameter(cameraParameters,this,"","");
        IsoMode = new IsoModeParameter(cameraParameters,this,"","", cameraHolder);
        AntiBandingMode = new AntiBandingModeParameter(cameraParameters,this, "antibanding", "antibanding-values");
        WhiteBalanceMode = new WhiteBalanceModeParameter(cameraParameters, this, "whitebalance", "whitebalance-values");
        PictureSize = new PictureSizeParameter(cameraParameters,this, "", "");
        PictureFormat = new PictureFormatParameter(cameraParameters, this, "picture-format", "picture-format-values");
        JpegQuality = new JpegQualityParameter(cameraParameters, this, "jpeg-quality", "");
        AE_Bracket = new AE_Bracket_HdrModeParameter(cameraParameters,this, "ae-bracket-hdr", "ae-bracket-hdr-values");
        ImagePostProcessing = new ImagePostProcessingParameter(cameraParameters,this, "ipp", "ipp-values");
        PreviewSize = new PreviewSizeParameter(cameraParameters, this, "preview-size", "preview-size-values", cameraHolder);
        /*PreviewFPS = new PreviewFpsParameter(cameraParameters, this, "preview-frame-rate", "preview-frame-rate-values", cameraHolder);
        PreviewFormat = new PreviewFormatParameter(cameraParameters, this, "preview-format", "preview-format-values", cameraHolder);*/
        Zoom = new ZoomManualParameter(cameraParameters,"", "", "");
        SceneMode =  new SceneModeParameter(cameraParameters, this, "","");
        FocusMode = new FocusModeParameter(cameraParameters, this,"","");
        RedEye = new RedEyeParameter(cameraParameters, this, "redeye-reduction", "redeye-reduction-values");
        LensShade = new LensshadeParameter(cameraParameters, this, "lensshade", "lensshade-values");
        ZSL = new ZeroShutterLagParameter(cameraParameters, this, "zsl", "zsl-values", cameraHolder);
        SceneDetect = new SceneDetectParameter(cameraParameters, this, "scene-detect", "scene-detect-values");
        Denoise = new DenoiseParameter(cameraParameters, this, "denoise", "denoise-values");
        DigitalImageStabilization = new DigitalImageStabilizationParameter(cameraParameters, this, "dis", "dis-values", cameraHolder);
        MemoryColorEnhancement = new MemoryColorEnhancementParameter(cameraParameters, this, "mce", "mce-values", cameraHolder);
        SkinToneEnhancment = new SkinToneParameter(cameraParameters, this, "skinToneEnhancement", "skinToneEnhancement-values", cameraHolder);
        NightMode = new NightModeParameter(cameraParameters, this,"","");
        NonZslManualMode = new NonZslManualModeParameter(cameraParameters, this, "non-zsl-manual-mode", "", cameraHolder);
        Histogram = new HistogramModeParameter(cameraParameters,this, "histogram", "histogram-values");

        VideoSize = new VideoSizeParameter(cameraParameters,this,"","");
        VideoProfiles = new VideoProfilesParameter(cameraParameters,this,"","", cameraHolder);

        String rawFormats[] = PictureFormat.GetValues();

        if (DeviceUtils.isMediaTekTHL5000())
        {
            rawSupported =true;
        }
        else if (DeviceUtils.isOmap())
        {
            rawSupported = true;
        }
        else if (!DeviceUtils.isHTCADV()) {
            for (String s : rawFormats) {
                if (s.contains("bayer"))
                    rawSupported = true;
                if (s.contains("bayer-mipi")) {
                    dngSupported = true;
                    BayerMipiFormat = s;
                }
            }
        }
        else
        {
            dngSupported = true;
            rawSupported = true;
            BayerMipiFormat = StringUtils.BayerMipiGRBG();
        }

        ParametersEventHandler.ParametersHasLoaded();
    }


    Handler handler = new Handler();
    @Override
    public void ParameterChanged()
    {
        //cameraHolder.SetCameraParameters(cameraParameters);
        if (!setParameterRunner.isRunning)
            handler.post(setParameterRunner);
            //setParameterRunner.run();
        else
            moreParametersToSet = true;

    }

    class SetParameterRunner implements Runnable
    {
        private boolean isRunning = false;

        @Override
        public void run()
        {
            isRunning = true;
            cameraHolder.SetCameraParameters(cameraParameters);
            try {
                //maybe need to incrase the sleeptime if a device crash when setting the manual parameters like manual exposure or manual saturation
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isRunning = false;
            //logParameters(cameraHolder.GetCamera().getParameters());
            if (moreParametersToSet)
            {
                moreParametersToSet = false;
                run();
            }
        }
    }

    public void SetFocusAREA(List<Camera.Area> focusAreas)
    {
        Camera.Parameters para = cameraParameters;
        if (para.getMaxNumFocusAreas() > 0) {
            para.setFocusAreas(focusAreas);
            //cameraHolder.SetCameraParameters(para);
        }
        if (para.getMaxNumMeteringAreas() > 0) {
            para.setMeteringAreas(focusAreas);
            //cameraHolder.SetCameraParameters(para);
        }
    }

    public void SetPictureOrientation(int orientation)
    {
        try {
            cameraParameters.setRotation(orientation);
            cameraHolder.SetCameraParameters(cameraParameters);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public boolean isExposureAndWBLocked = false;

    public void LockExposureAndWhiteBalance(boolean value)
    {
        isExposureAndWBLocked = value;
        if (cameraParameters.isAutoExposureLockSupported())
            cameraParameters.setAutoExposureLock(value);
        if (cameraParameters.isAutoWhiteBalanceLockSupported())
            cameraParameters.setAutoWhiteBalanceLock(value);
        SetParametersToCamera();
    }

    public String GetRawSize()
    {
        return cameraParameters.get("raw-size");
    }

    public void setTHL5000Raw(boolean raw)
    {
        Log.d(TAG, "THL5000 try to set mode");
        if (!raw) {
            cameraParameters.set("rawsave-mode", 1);
            cameraParameters.set("isp-mode", 0);
            Log.d(TAG, "THL5000 set mode to jpeg");
        }
        else
        {
            cameraParameters.set("rawsave-mode", 2);
            cameraParameters.set("isp-mode", 3);
            Log.d(TAG, "THL5000 set mode to RAW");
        }
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    public void setTHL5000rawFilename(String filename)
    {
        cameraParameters.set("rawfname", filename);
        cameraHolder.SetCameraParameters(cameraParameters);
    }
}
