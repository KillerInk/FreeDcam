package com.troop.freecamv2.camera.parameters;

import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.troop.freecamv2.camera.BaseCameraHolder;
import com.troop.freecamv2.camera.parameters.manual.BrightnessManualParameter;
import com.troop.freecamv2.camera.parameters.manual.CCTManualParameter;
import com.troop.freecamv2.camera.parameters.manual.ContrastManualParameter;
import com.troop.freecamv2.camera.parameters.manual.ConvergenceManualParameter;
import com.troop.freecamv2.camera.parameters.manual.ExposureManualParameter;
import com.troop.freecamv2.camera.parameters.manual.FXManualParameter;
import com.troop.freecamv2.camera.parameters.manual.FocusManualParameter;
import com.troop.freecamv2.camera.parameters.manual.ISOManualParameter;
import com.troop.freecamv2.camera.parameters.manual.SaturationManualParameter;
import com.troop.freecamv2.camera.parameters.manual.SharpnessManualParameter;
import com.troop.freecamv2.camera.parameters.manual.ShutterManualParameter;
import com.troop.freecamv2.camera.parameters.manual.ZoomManualParameter;
import com.troop.freecamv2.camera.parameters.modes.AE_Bracket_HdrModeParameter;
import com.troop.freecamv2.camera.parameters.modes.AntiBandingModeParameter;
import com.troop.freecamv2.camera.parameters.modes.BaseModeParameter;
import com.troop.freecamv2.camera.parameters.modes.ColorModeParameter;
import com.troop.freecamv2.camera.parameters.modes.DenoiseParameter;
import com.troop.freecamv2.camera.parameters.modes.DigitalImageStabilizationParameter;
import com.troop.freecamv2.camera.parameters.modes.ExposureModeParameter;
import com.troop.freecamv2.camera.parameters.modes.FlashModeParameter;
import com.troop.freecamv2.camera.parameters.modes.FocusModeParameter;
import com.troop.freecamv2.camera.parameters.modes.ImagePostProcessingParameter;
import com.troop.freecamv2.camera.parameters.modes.IsoModeParameter;
import com.troop.freecamv2.camera.parameters.modes.JpegQualityParameter;
import com.troop.freecamv2.camera.parameters.modes.LensshadeParameter;
import com.troop.freecamv2.camera.parameters.modes.MemoryColorEnhancementParameter;
import com.troop.freecamv2.camera.parameters.modes.NightModeParameter;
import com.troop.freecamv2.camera.parameters.modes.NonZslManualModeParameter;
import com.troop.freecamv2.camera.parameters.modes.PictureFormatParameter;
import com.troop.freecamv2.camera.parameters.modes.PictureSizeParameter;
import com.troop.freecamv2.camera.parameters.modes.PreviewFormatParameter;
import com.troop.freecamv2.camera.parameters.modes.PreviewFpsParameter;
import com.troop.freecamv2.camera.parameters.modes.PreviewSizeParameter;
import com.troop.freecamv2.camera.parameters.modes.RedEyeParameter;
import com.troop.freecamv2.camera.parameters.modes.SceneDetectParameter;
import com.troop.freecamv2.camera.parameters.modes.SceneModeParameter;
import com.troop.freecamv2.camera.parameters.modes.SkinToneParameter;
import com.troop.freecamv2.camera.parameters.modes.WhiteBalanceModeParameter;
import com.troop.freecamv2.camera.parameters.modes.ZeroShutterLagParameter;
import com.troop.freecamv2.utils.DeviceUtils;

import java.util.List;

/**
 * Created by troop on 17.08.2014.
 */
public class CamParametersHandler implements I_ParameterChanged
{

    String TAG = "freecam.CameraParametersHandler";
    public BaseCameraHolder cameraHolder;
    Camera.Parameters cameraParameters;

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
        IsoMode = new IsoModeParameter(cameraParameters,this,"","");
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
        ZSL = new ZeroShutterLagParameter(cameraParameters, this, "zsl", "zsl-values");
        SceneDetect = new SceneDetectParameter(cameraParameters, this, "scene-detect", "scene-detect-values");
        Denoise = new DenoiseParameter(cameraParameters, this, "denoise", "denoise-values");
        DigitalImageStabilization = new DigitalImageStabilizationParameter(cameraParameters, this, "dis", "dis-values", cameraHolder);
        MemoryColorEnhancement = new MemoryColorEnhancementParameter(cameraParameters, this, "mce", "mce-values", cameraHolder);
        SkinToneEnhancment = new SkinToneParameter(cameraParameters, this, "skinToneEnhancement", "skinToneEnhancement-values");
        NightMode = new NightModeParameter(cameraParameters, this,"","");
        NonZslManualMode = new NonZslManualModeParameter(cameraParameters, this, "non-zsl-manual-mode", "");
        String rawFormats[] = PictureFormat.GetValues();
        for (String s : rawFormats)
        {
            if (s.contains("bayer"))
                rawSupported = true;
            if (s.contains("bayer-mipi")) {
                dngSupported = true;
                BayerMipiFormat = s;
            }
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

    public void LockExposureAndWhiteBalance(boolean value)
    {
        if (cameraParameters.isAutoExposureLockSupported())
            cameraParameters.setAutoExposureLock(value);
        if (cameraParameters.isAutoWhiteBalanceLockSupported())
            cameraParameters.setAutoWhiteBalanceLock(value);
        cameraHolder.SetCameraParameters(cameraParameters);
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
}
