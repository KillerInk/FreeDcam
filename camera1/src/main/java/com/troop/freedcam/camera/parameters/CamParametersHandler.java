package com.troop.freedcam.camera.parameters;

import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.parameters.manual.BrightnessManualParameter;
import com.troop.freedcam.camera.parameters.manual.BurstManualParam;
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
import com.troop.freedcam.camera.parameters.manual.SkintoneManualPrameter;
import com.troop.freedcam.camera.parameters.manual.ZoomManualParameter;
import com.troop.freedcam.camera.parameters.modes.AE_Bracket_HdrModeParameter;
import com.troop.freedcam.camera.parameters.modes.AntiBandingModeParameter;
import com.troop.freedcam.camera.parameters.modes.BaseModeParameter;
import com.troop.freedcam.camera.parameters.modes.CDS_Mode_Parameter;
import com.troop.freedcam.camera.parameters.modes.ColorModeParameter;
import com.troop.freedcam.camera.parameters.modes.DigitalImageStabilizationParameter;
import com.troop.freedcam.camera.parameters.modes.ExposureLockParameter;
import com.troop.freedcam.camera.parameters.modes.ExposureModeParameter;
import com.troop.freedcam.camera.parameters.modes.FlashModeParameter;
import com.troop.freedcam.camera.parameters.modes.FocusModeParameter;
import com.troop.freedcam.camera.parameters.modes.ImagePostProcessingParameter;
import com.troop.freedcam.camera.parameters.modes.IsoModeParameter;
import com.troop.freedcam.camera.parameters.modes.JpegQualityParameter;
import com.troop.freedcam.camera.parameters.modes.NightModeParameter;
import com.troop.freedcam.camera.parameters.modes.NonZslManualModeParameter;
import com.troop.freedcam.camera.parameters.modes.OisParameter;
import com.troop.freedcam.camera.parameters.modes.PictureFormatParameter;
import com.troop.freedcam.camera.parameters.modes.PictureSizeParameter;
import com.troop.freedcam.camera.parameters.modes.PreviewFormatParameter;
import com.troop.freedcam.camera.parameters.modes.PreviewFpsParameter;
import com.troop.freedcam.camera.parameters.modes.PreviewSizeParameter;
import com.troop.freedcam.camera.parameters.modes.SceneModeParameter;
import com.troop.freedcam.camera.parameters.modes.VideoHDRModeParameter;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesG3Parameter;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.camera.parameters.modes.VideoSizeParameter;
import com.troop.freedcam.camera.parameters.modes.WhiteBalanceModeParameter;
import com.troop.freedcam.camera.parameters.modes.ZeroShutterLagParameter;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.i_camera.parameters.CameraParametersEventHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by troop on 17.08.2014.
 */
public class CamParametersHandler extends AbstractParameterHandler
{

    private static String TAG = "freedcam.CameraParametersHandler";

    HashMap<String, String> cameraParameters;
    public HashMap<String, String> getParameters(){return cameraParameters;}

    boolean moreParametersToSet = false;
    public BaseCameraHolder baseCameraHolder;
    public BaseModeParameter DualMode;
    CameraUiWrapper cameraUiWrapper;

    SetParameterRunner setParameterRunner;

    public CamParametersHandler(CameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager, Handler uiHandler)
    {
        super(cameraUiWrapper.cameraHolder,appSettingsManager, uiHandler);
        ParametersEventHandler = new CameraParametersEventHandler();
        baseCameraHolder = (BaseCameraHolder) cameraHolder;
        this.cameraUiWrapper = cameraUiWrapper;
    }

    public void GetParametersFromCamera()
    {
        cameraParameters = baseCameraHolder.GetCameraParameters();
    }

    public void SetParametersToCamera()
    {
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    public void LoadParametersFromCamera()
    {
        cameraParameters = baseCameraHolder.GetCameraParameters();
        setParameterRunner = new SetParameterRunner();
        initParameters();
    }

    private void logParameters(HashMap<String, String> parameters)
    {
        Log.d(TAG, "Manufactur:" + Build.MANUFACTURER);
        Log.d(TAG, "Model:" + Build.MODEL);
        Log.d(TAG, "Product:" + Build.PRODUCT);
        for(Map.Entry e : parameters.entrySet())
        {
            Log.d(TAG, e.getKey() + "=" + e.getValue());
        }
    }

    private void initParameters()
    {
        logParameters(cameraParameters);
        ManualBrightness = new BrightnessManualParameter(cameraParameters, "","","", this);
        ManualContrast = new ContrastManualParameter(cameraParameters, "", "", "",this);
        ManualConvergence = new ConvergenceManualParameter(cameraParameters, "manual-convergence", "supported-manual-convergence-max", "supported-manual-convergence-min", this);
        ManualExposure = new ExposureManualParameter(cameraParameters,"exposure-compensation","max-exposure-compensation","min-exposure-compensation", this);
        ManualFocus = new FocusManualParameter(cameraParameters,"","","", cameraHolder, this);
        ManualSaturation = new SaturationManualParameter(cameraParameters,"","","", this);
        ManualSharpness = new SharpnessManualParameter(cameraParameters, "", "", "", this);
        ManualShutter = new ShutterManualParameter(cameraParameters,"","","", cameraHolder, this);
        CCT = new CCTManualParameter(cameraParameters,"","","", this);
        Skintone = new SkintoneManualPrameter(cameraParameters,"","","",this);

        FX = new FXManualParameter(cameraParameters,"","","", this);

        Burst = new BurstManualParam(cameraParameters,"","","",this);
       /* if (DeviceUtils.isSonyADV()) {
            ISOManual = new ISOManualParameter(cameraParameters, "", "", "", this);
        }*/
        ISOManual = new ISOManualParameter(cameraParameters,"","","", this);

        Zoom = new ZoomManualParameter(cameraParameters,"", "", "", this);


        ColorMode = new ColorModeParameter(uiHandler,cameraParameters,baseCameraHolder, "effect", "effect-values");
        ExposureMode = new ExposureModeParameter(uiHandler,cameraParameters,baseCameraHolder,"","");
        FlashMode = new FlashModeParameter(uiHandler,cameraParameters,baseCameraHolder,"flash-mode","flash-mode-values");
        IsoMode = new IsoModeParameter(uiHandler,cameraParameters,baseCameraHolder,"","", cameraHolder);
        AntiBandingMode = new AntiBandingModeParameter(uiHandler,cameraParameters,baseCameraHolder, "antibanding", "antibanding-values");
        WhiteBalanceMode = new WhiteBalanceModeParameter(uiHandler,cameraParameters, baseCameraHolder, "whitebalance", "whitebalance-values");
        PictureSize = new PictureSizeParameter(uiHandler,cameraParameters,baseCameraHolder, "picture-size", "picture-size-values");

        PictureFormat = new PictureFormatParameter(uiHandler,cameraParameters, baseCameraHolder, "", "", this, appSettingsManager);


        JpegQuality = new JpegQualityParameter(uiHandler,cameraParameters, baseCameraHolder, "jpeg-quality", "");
        //defcomg was here // troopii saw it and fixed sony stuff

        AE_Bracket = new AE_Bracket_HdrModeParameter(uiHandler,cameraParameters,baseCameraHolder, "ae-bracket-hdr", "ae-bracket-hdr-values");
        ImagePostProcessing = new ImagePostProcessingParameter(uiHandler,cameraParameters,baseCameraHolder, "ipp", "ipp-values");
        PreviewSize = new PreviewSizeParameter(uiHandler,cameraParameters, baseCameraHolder, "preview-size", "preview-size-values", cameraHolder);
        PreviewFPS = new PreviewFpsParameter(uiHandler, cameraParameters, "preview-frame-rate", "preview-frame-rate-values", (BaseCameraHolder)cameraHolder);
        PreviewFormat = new PreviewFormatParameter(uiHandler,cameraParameters, baseCameraHolder, "preview-format", "preview-format-values", cameraHolder);

        SceneMode =  new SceneModeParameter(uiHandler, cameraParameters, baseCameraHolder, "scene-mode","scene-mode-values");
        FocusMode = new FocusModeParameter(uiHandler,cameraParameters, baseCameraHolder,"focus-mode","focus-mode-values");
        RedEye = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "redeye-reduction", "redeye-reduction-values");
        LensShade = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "lensshade", "lensshade-values");
        ZSL = new ZeroShutterLagParameter(uiHandler,cameraParameters, baseCameraHolder, "", "", cameraHolder);
        SceneDetect = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "scene-detect", "scene-detect-values");
        Denoise = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "denoise", "denoise-values");
//sony-is for images sony-vs for video

        DigitalImageStabilization = new DigitalImageStabilizationParameter(uiHandler,cameraParameters, baseCameraHolder, "", "");

        MemoryColorEnhancement = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "mce", "mce-values");
        SkinToneEnhancment = new DigitalImageStabilizationParameter(uiHandler,cameraParameters, baseCameraHolder, "skinToneEnhancement", "skinToneEnhancement-values");
        NightMode = new NightModeParameter(uiHandler,cameraParameters, baseCameraHolder,"","");
        NonZslManualMode = new NonZslManualModeParameter(uiHandler,cameraParameters, baseCameraHolder, "non-zsl-manual-mode", "", cameraHolder);
        Histogram = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "histogram", "histogram-values");
        CameraMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "camera-mode", "camera-mode-values");
        DualMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "dual_mode", "");

        ExposureLock = new ExposureLockParameter(uiHandler,cameraParameters, baseCameraHolder, "","");


        VideoSize = new VideoSizeParameter(uiHandler,cameraParameters,baseCameraHolder,"video-size","video-size");

        VideoHDR = new VideoHDRModeParameter(uiHandler,cameraParameters, baseCameraHolder, "", "", cameraHolder);

        if (baseCameraHolder.hasLGFrameWork /*&& Build.VERSION.SDK_INT < 21*/)
            VideoProfilesG3 = new VideoProfilesG3Parameter(uiHandler,cameraParameters,baseCameraHolder,"","", cameraUiWrapper);
        else
            VideoProfiles = new VideoProfilesParameter(uiHandler,cameraParameters,baseCameraHolder,"","", cameraUiWrapper);

        CDS_Mode = new CDS_Mode_Parameter(uiHandler,cameraParameters,baseCameraHolder,"","");

        //####No idea what they do, m9 specific, only thing they do is to freez the app####
        RdiMode = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "rdi-mode", "rdi-mode-values");
        SecureMode = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "secure-mode", "secure-mode-values");
        //Temporal Noise Reduction http://nofilmschool.com/2012/03/temporal-noise-reduction-ipad-its-improvement
        TnrMode = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "tnr-mode", "tnr-mode-values");
        //############################

        oismode = new OisParameter(uiHandler,cameraParameters,baseCameraHolder,"","");

        SetCameraRotation();
        SetPictureOrientation(0);
        SetAppSettingsToParameters();
        if (appSettingsManager.getString(AppSettingsManager.SETTING_DNG).equals(""))
            appSettingsManager.setString(AppSettingsManager.SETTING_DNG, "false");
        else if (appSettingsManager.getString(AppSettingsManager.SETTING_DNG).equals("true"))
            isDngActive = true;
        cameraHolder.SetCameraParameters(cameraParameters);

        //appSettingsManager.context.runOnUiThread(new Runnable() {
          //  @Override
            //public void run() {
                ParametersEventHandler.ParametersHasLoaded();
            //}
        //});
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
                Thread.sleep(200);
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

    //focus-areas=(0, 0, 0, 0, 0)
    public void SetFocusAREA(FocusRect focusAreas, FocusRect meteringAreas)
    {
        ((BaseCameraHolder)cameraHolder).SetFocusAreas(focusAreas, meteringAreas);

    }

    public void SetPictureOrientation(int orientation)
    {
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals("true"))
        {
            int or = orientation +180;
            if (or >360)
                or = or - 360;
            orientation = or;
        }
        try
        {
            ((BaseCameraHolder)cameraHolder).SetOrientation(orientation);
            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void SetCameraRotation()
    {
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(""))
        {
            appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack , "false");
        }
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals("false"))
            ((BaseCameraHolder)cameraHolder).SetCameraRotation(0);
        else
            ((BaseCameraHolder)cameraHolder).SetCameraRotation(180);
    }



    public void LockExposureAndWhiteBalance(boolean value)
    {
        isExposureAndWBLocked = value;
        if (ExposureLock.IsSupported())
            ExposureLock.SetValue(value+"", false);
        SetParametersToCamera();
    }

    public String GetRawSize()
    {
        return cameraParameters.get("raw-size");
    }

    //rawsave-mode=2
    public void setTHL5000Raw(boolean raw)
    {
        cameraHolder.StopPreview();
        Log.d(TAG, "THL5000 try to set mode");
        if (!raw) {
            cameraParameters.put("rawsave-mode", 0+"");
            cameraParameters.put("isp-mode", 0+"");
            Log.d(TAG, "THL5000 set mode to jpeg");
        }
        else
        {
            cameraParameters.put("rawsave-mode", 2+"");
            cameraParameters.put("isp-mode", 0+"");
            Log.d(TAG, "THL5000 set mode to RAW");
        }
        cameraHolder.SetCameraParameters(cameraParameters);
        cameraHolder.StartPreview();
    }

    //rawfname=/storage/sdcard0/DCIM/CameraEM/Capture20141230-160133ISOAuto.raw;
    public void setTHL5000rawFilename(String filename)
    {
        cameraParameters.put("rawfname", filename);
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    public void setString(String param, String value)
    {
        try
        {
            cameraParameters.put(param, value);
            cameraHolder.SetCameraParameters(cameraParameters);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }


    }

    public void setRawSize(String size)
    {
        cameraParameters.put("raw-size", size);
        cameraHolder.SetCameraParameters(cameraParameters);
    }


    public float GetFnumber()
    {
        if (cameraParameters.containsKey("f-number")) {
            final String fnum = cameraParameters.get("f-number");
            return Float.parseFloat(fnum);
        }
        else
            return 0;
    }

    public float GetFocal()
    {
        if (cameraParameters.containsKey("focal-length")) {
            final String focal = cameraParameters.get("focal-length");
            return Float.parseFloat(focal);
        }
        else
            return 0;
    }

}
