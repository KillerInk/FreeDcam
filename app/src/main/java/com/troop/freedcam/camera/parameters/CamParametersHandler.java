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
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.util.List;

/**
 * Created by troop on 17.08.2014.
 */
public class CamParametersHandler extends AbstractParameterHandler implements I_ParameterChanged
{

    String TAG = "freedcam.CameraParametersHandler";

    Camera.Parameters cameraParameters;
    public Camera.Parameters getParameters(){return cameraParameters;}

    boolean moreParametersToSet = false;

    SetParameterRunner setParameterRunner;

    public CamParametersHandler(AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager,Handler backGroundHandler, Handler uiHandler)
    {
        super(cameraHolder,appSettingsManager, backGroundHandler, uiHandler);
        ParametersEventHandler = new CameraParametersEventHandler();
    }

    public void GetParametersFromCamera()
    {
        BaseCameraHolder baseCameraHolder = (BaseCameraHolder) cameraHolder;
        cameraParameters = baseCameraHolder.GetCameraParameters();
    }

    public void SetParametersToCamera()
    {
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    public void LoadParametersFromCamera()
    {
        BaseCameraHolder baseCameraHolder = (BaseCameraHolder) cameraHolder;
        cameraParameters = baseCameraHolder.GetCameraParameters();
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
        ManualBrightness = new BrightnessManualParameter(cameraParameters, "","","", this);
        ManualContrast = new ContrastManualParameter(cameraParameters, "", "", "",this);
        ManualConvergence = new ConvergenceManualParameter(cameraParameters, "manual-convergence", "supported-manual-convergence-max", "supported-manual-convergence-min", this);
        ManualExposure = new ExposureManualParameter(cameraParameters,"","","", this);
        ManualFocus = new FocusManualParameter(cameraParameters,"","","", cameraHolder, this);
        ManualSaturation = new SaturationManualParameter(cameraParameters,"","","", this);
        ManualSharpness = new SharpnessManualParameter(cameraParameters, "", "", "", this);
        ManualShutter = new ShutterManualParameter(cameraParameters,"","","", cameraHolder, this);
        CCT = new CCTManualParameter(cameraParameters,"","","", this);
        FX = new FXManualParameter(cameraParameters,"","","", this);
        ISOManual = new ISOManualParameter(cameraParameters,"","","", this);
        Zoom = new ZoomManualParameter(cameraParameters,"", "", "", this);


        ColorMode = new ColorModeParameter(cameraParameters,this, "", "");
        ExposureMode = new ExposureModeParameter(cameraParameters,this,"","");
        FlashMode = new FlashModeParameter(cameraParameters,this,"","");
        IsoMode = new IsoModeParameter(cameraParameters,this,"","", cameraHolder);
        AntiBandingMode = new AntiBandingModeParameter(cameraParameters,this, "antibanding", "antibanding-values");
        WhiteBalanceMode = new WhiteBalanceModeParameter(cameraParameters, this, "whitebalance", "whitebalance-values");
        PictureSize = new PictureSizeParameter(cameraParameters,this, "", "");
        PictureFormat = new PictureFormatParameter(cameraParameters, this, "picture-format", "picture-format-values", this, appSettingsManager);
        JpegQuality = new JpegQualityParameter(cameraParameters, this, "jpeg-quality", "");
        AE_Bracket = new AE_Bracket_HdrModeParameter(cameraParameters,this, "ae-bracket-hdr", "ae-bracket-hdr-values");
        ImagePostProcessing = new ImagePostProcessingParameter(cameraParameters,this, "ipp", "ipp-values");
        PreviewSize = new PreviewSizeParameter(cameraParameters, this, "preview-size", "preview-size-values", cameraHolder);
        /*PreviewFPS = new PreviewFpsParameter(cameraParameters, this, "preview-frame-rate", "preview-frame-rate-values", cameraHolder);*/
        PreviewFormat = new PreviewFormatParameter(cameraParameters, this, "preview-format", "preview-format-values", cameraHolder);

        SceneMode =  new SceneModeParameter(cameraParameters, this, "","");
        FocusMode = new FocusModeParameter(cameraParameters, this,"","");
        RedEye = new RedEyeParameter(cameraParameters, this, "redeye-reduction", "redeye-reduction-values");
        LensShade = new LensshadeParameter(cameraParameters, this, "lensshade", "lensshade-values");
        ZSL = new ZeroShutterLagParameter(cameraParameters, this, "", "", cameraHolder);
        SceneDetect = new SceneDetectParameter(cameraParameters, this, "scene-detect", "scene-detect-values");
        Denoise = new DenoiseParameter(cameraParameters, this, "denoise", "denoise-values");
        DigitalImageStabilization = new DigitalImageStabilizationParameter(cameraParameters, this, "dis", "dis-values", cameraHolder);
        MemoryColorEnhancement = new MemoryColorEnhancementParameter(cameraParameters, this, "mce", "mce-values", cameraHolder);
        SkinToneEnhancment = new SkinToneParameter(cameraParameters, this, "skinToneEnhancement", "skinToneEnhancement-values", cameraHolder);
        NightMode = new NightModeParameter(cameraParameters, this,"","");
        NonZslManualMode = new NonZslManualModeParameter(cameraParameters, this, "non-zsl-manual-mode", "", cameraHolder);
        Histogram = new HistogramModeParameter(cameraParameters,this, "histogram", "histogram-values");
        CameraMode = new HistogramModeParameter(cameraParameters,this, "camera-mode", "camera-mode-values");


        VideoSize = new VideoSizeParameter(cameraParameters,this,"video-size","video-size");
        VideoHDR = new VideoHDRModeParameter(cameraParameters, this, "video-hdr", "video-hdr-values", cameraHolder);

        if (DeviceUtils.isLGADV() && Build.VERSION.SDK_INT < 21)
            VideoProfilesG3 = new VideoProfilesG3Parameter(cameraParameters,this,"","", cameraHolder);
        else
            VideoProfiles = new VideoProfilesParameter(cameraParameters,this,"","", cameraHolder);

        checkRawSupport();

        appSettingsManager.context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ParametersEventHandler.ParametersHasLoaded();
            }
        });

    }

    private void checkRawSupport()
    {
        String rawFormats[] = cameraParameters.get("picture-format-values").split(",");

        for (String s : rawFormats)
        {
            if (s.contains("bayer") || s.contains("raw"))
            {
                rawSupported = true;
            }
            if (s.contains("bayer-mipi")) {
                dngSupported = true;
                BayerMipiFormat = s;
                if (DeviceUtils.isHTC_M8())
                    BayerMipiFormat = StringUtils.BayerMipiGRBG();
            }
        }
        /*if(DeviceUtils.isLGADV() && Build.VERSION.SDK_INT == 21)
        {
            BayerMipiFormat = "bayer-qcom-10bggr";
        }*/
        if (DeviceUtils.isMediaTekTHL5000())
        {
            rawSupported =true;
        }
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
            cameraParameters.set("rawsave-mode", 0);
            cameraParameters.set("isp-mode", 0);
            Log.d(TAG, "THL5000 set mode to jpeg");
        }
        else
        {
            cameraParameters.set("rawsave-mode", 1);
            cameraParameters.set("isp-mode", 1);
            Log.d(TAG, "THL5000 set mode to RAW");
        }
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    public void setTHL5000rawFilename(String filename)
    {
        cameraParameters.set("rawfname", filename);
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    public void setString(String param, String value)
    {
        cameraParameters.set(param, value);
        //cameraHolder.SetCameraParameters(cameraParameters);
    }
}
