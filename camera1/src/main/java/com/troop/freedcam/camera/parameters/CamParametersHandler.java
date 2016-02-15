package com.troop.freedcam.camera.parameters;

import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.FocusHandler;
import com.troop.freedcam.camera.parameters.manual.BaseManualParameter;
import com.troop.freedcam.camera.parameters.manual.BrightnessManualParameter;
import com.troop.freedcam.camera.parameters.manual.BurstManualParam;
import com.troop.freedcam.camera.parameters.manual.CCTManualParameter;
import com.troop.freedcam.camera.parameters.manual.ContrastManualParameter;
import com.troop.freedcam.camera.parameters.manual.ConvergenceManualParameter;
import com.troop.freedcam.camera.parameters.manual.ExposureManualParameter;
import com.troop.freedcam.camera.parameters.manual.FXManualParameter;
import com.troop.freedcam.camera.parameters.manual.FocusManualParameter;
import com.troop.freedcam.camera.parameters.manual.FocusManualParameterHTC;
import com.troop.freedcam.camera.parameters.manual.FocusManualParameterLG;
import com.troop.freedcam.camera.parameters.manual.ISOManualParameter;
import com.troop.freedcam.camera.parameters.manual.LG_G4AeHandler;
import com.troop.freedcam.camera.parameters.manual.SaturationManualParameter;
import com.troop.freedcam.camera.parameters.manual.SharpnessManualParameter;
import com.troop.freedcam.camera.parameters.manual.ShutterManualParameter;
import com.troop.freedcam.camera.parameters.manual.ShutterManualParameterHTC;
import com.troop.freedcam.camera.parameters.manual.ShutterManualZTE;
import com.troop.freedcam.camera.parameters.manual.SkintoneManualPrameter;
import com.troop.freedcam.camera.parameters.manual.ZoomManualParameter;
import com.troop.freedcam.camera.parameters.modes.AE_Bracket_HdrModeParameter;
import com.troop.freedcam.camera.parameters.modes.AntiBandingModeParameter;
import com.troop.freedcam.camera.parameters.modes.BaseModeParameter;
import com.troop.freedcam.camera.parameters.modes.CDS_Mode_Parameter;
import com.troop.freedcam.camera.parameters.modes.ColorModeParameter;
import com.troop.freedcam.camera.parameters.modes.CupBurstExpModeParameter;
import com.troop.freedcam.camera.parameters.modes.DigitalImageStabilizationParameter;
import com.troop.freedcam.camera.parameters.modes.ExposureLockParameter;
import com.troop.freedcam.camera.parameters.modes.ExposureModeParameter;
import com.troop.freedcam.camera.parameters.modes.FlashModeParameter;
import com.troop.freedcam.camera.parameters.modes.FocusModeParameter;
import com.troop.freedcam.camera.parameters.modes.FocusPeakModeParameter;
import com.troop.freedcam.camera.parameters.modes.HDRModeParameter;
import com.troop.freedcam.camera.parameters.modes.HighFramerateVideo;
import com.troop.freedcam.camera.parameters.modes.HighSpeedVideo;
import com.troop.freedcam.camera.parameters.modes.ImagePostProcessingParameter;
import com.troop.freedcam.camera.parameters.modes.IsoModeParameter;
import com.troop.freedcam.camera.parameters.modes.JpegQualityParameter;
import com.troop.freedcam.camera.parameters.modes.NightModeParameter;
import com.troop.freedcam.camera.parameters.modes.NonZslManualModeParameter;
import com.troop.freedcam.camera.parameters.modes.OisParameter;
import com.troop.freedcam.camera.parameters.modes.PictureFormatHandler;
import com.troop.freedcam.camera.parameters.modes.PictureSizeParameter;
import com.troop.freedcam.camera.parameters.modes.PreviewFormatParameter;
import com.troop.freedcam.camera.parameters.modes.PreviewFpsParameter;
import com.troop.freedcam.camera.parameters.modes.PreviewSizeParameter;
import com.troop.freedcam.camera.parameters.modes.SceneModeParameter;
import com.troop.freedcam.camera.parameters.modes.VideoHDRModeParameter;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesG3Parameter;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.camera.parameters.modes.VideoSizeParameter;
import com.troop.freedcam.camera.parameters.modes.VideoStabilizationParameter;
import com.troop.freedcam.camera.parameters.modes.VirtualLensFilter;
import com.troop.freedcam.camera.parameters.modes.WhiteBalanceModeParameter;
import com.troop.freedcam.camera.parameters.modes.ZeroShutterLagParameter;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.i_camera.parameters.CameraParametersEventHandler;
import com.troop.freedcam.i_camera.parameters.LocationParameter;
import com.troop.freedcam.i_camera.parameters.ModuleParameters;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.DeviceUtils.Devices;
import com.troop.freedcam.utils.StringUtils;

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
    public BaseCameraHolder baseCameraHolder;
    public BaseModeParameter DualMode;
    CameraUiWrapper cameraUiWrapper;
    LG_G4AeHandler aeHandlerG4;

    public CamParametersHandler(CameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager, Handler uiHandler)
    {
        super(cameraUiWrapper.cameraHolder,appSettingsManager, uiHandler);
        ParametersEventHandler = new CameraParametersEventHandler(uiHandler);
        baseCameraHolder = (BaseCameraHolder) cameraHolder;
        this.cameraUiWrapper = cameraUiWrapper;
    }

    public void SetParametersToCamera()
    {
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    public void LoadParametersFromCamera()
    {
        cameraParameters = baseCameraHolder.GetCameraParameters();
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
        if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
            setupLg_G4Parameters();

        logParameters(cameraParameters);
        //setup first Pictureformat its needed for manual parameters to
        // register their listners there if its postprocessing parameter
        try {
            PictureFormat = new PictureFormatHandler(uiHandler,cameraParameters, baseCameraHolder);
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner((PictureFormatHandler)PictureFormat);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            FocusMode = new FocusModeParameter(uiHandler,cameraParameters, baseCameraHolder,"focus-mode","focus-mode-values");
            FocusMode.addEventListner(((FocusHandler) cameraHolder.Focus).focusModeListner);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        locationParameter = new LocationParameter(uiHandler, appSettingsManager, cameraHolder);
        try
        {
            ManualBrightness = new BrightnessManualParameter(cameraParameters, "","","", this);
            PictureFormat.addEventListner(((BaseManualParameter)ManualBrightness).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter)ManualBrightness).GetModuleListner());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            ManualContrast = new ContrastManualParameter(cameraParameters, "", "", "",this);
            PictureFormat.addEventListner(((BaseManualParameter)ManualContrast).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) ManualContrast).GetModuleListner());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            ManualConvergence = new ConvergenceManualParameter(cameraParameters, "manual-convergence", "supported-manual-convergence-max", "supported-manual-convergence-min", this);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            ManualExposure = new ExposureManualParameter(cameraParameters,"exposure-compensation","max-exposure-compensation","min-exposure-compensation", this);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            if (DeviceUtils.IS(Devices.LG_G4) || (DeviceUtils.IS(Devices.LG_G3) && (Build.VERSION.SDK_INT < 21 || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) || DeviceUtils.IS(Devices.LG_G2))
                ManualFocus = new FocusManualParameterLG(cameraParameters,"","","", cameraHolder, this);
            else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
                ManualFocus = new FocusManualParameterHTC(cameraParameters,"","","", cameraHolder,this);
            else
                ManualFocus = new FocusManualParameter(cameraParameters,"","","", cameraHolder, this);
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
        try {
            ManualSaturation = new SaturationManualParameter(cameraParameters,"","","", this);
            PictureFormat.addEventListner(((BaseManualParameter)ManualSaturation).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) ManualSaturation).GetModuleListner());
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
        try
        {
            ManualSharpness = new SharpnessManualParameter(cameraParameters, "", "", "", this);
            PictureFormat.addEventListner(((BaseManualParameter)ManualSharpness).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) ManualSharpness).GetModuleListner());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            if (DeviceUtils.IS(Devices.LG_G4))
                aeHandlerG4 = new LG_G4AeHandler(cameraParameters,baseCameraHolder,this);
            else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
                ManualShutter = new ShutterManualParameterHTC(cameraParameters,"","","", cameraHolder, cameraChanged,this);
            else if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
                ManualShutter = new ShutterManualZTE(cameraParameters,"","","", cameraHolder, cameraChanged, this);
            else
                ManualShutter = new ShutterManualParameter(cameraParameters,"","","", cameraHolder,cameraChanged, this);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        try {
            if (!DeviceUtils.IS(Devices.LG_G4))
            {
                ISOManual = new ISOManualParameter(cameraParameters, "", "", "",baseCameraHolder, this);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            WhiteBalanceMode = new WhiteBalanceModeParameter(uiHandler,cameraParameters, baseCameraHolder, "whitebalance", "whitebalance-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            CCT = new CCTManualParameter(cameraParameters,"","","", this);
            PictureFormat.addEventListner(((BaseManualParameter)CCT).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) CCT).GetModuleListner());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try{
            Skintone = new SkintoneManualPrameter(cameraParameters,"","","",this);
            PictureFormat.addEventListner(((BaseManualParameter)Skintone).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) Skintone).GetModuleListner());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            FX = new FXManualParameter(cameraParameters,"","","", this);
            PictureFormat.addEventListner(((BaseManualParameter)FX).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) FX).GetModuleListner());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            Burst = new BurstManualParam(cameraParameters,"","","",this);
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) Burst).GetModuleListner());

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        try {
            Zoom = new ZoomManualParameter(cameraParameters,"", "", "", this);
            PictureFormat.addEventListner(((BaseManualParameter)Zoom).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) Zoom).GetModuleListner());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            ColorMode = new ColorModeParameter(uiHandler,cameraParameters,baseCameraHolder, "effect", "effect-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            ExposureMode = new ExposureModeParameter(uiHandler,cameraParameters,baseCameraHolder,"","");
            ExposureMode.addEventListner(((FocusHandler)cameraHolder.Focus).aeModeListner);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            FlashMode = new FlashModeParameter(uiHandler,cameraParameters,baseCameraHolder,"flash-mode","flash-mode-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            IsoMode = new IsoModeParameter(uiHandler,cameraParameters,baseCameraHolder,"","", cameraHolder);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            AntiBandingMode = new AntiBandingModeParameter(uiHandler,cameraParameters,baseCameraHolder, "antibanding", "antibanding-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            PictureSize = new PictureSizeParameter(uiHandler,cameraParameters,baseCameraHolder, "picture-size", "picture-size-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        try {
            JpegQuality = new JpegQualityParameter(uiHandler,cameraParameters, baseCameraHolder, "jpeg-quality", "");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            AE_Bracket = new AE_Bracket_HdrModeParameter(uiHandler,cameraParameters,baseCameraHolder, "ae-bracket-hdr", "ae-bracket-hdr-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            ImagePostProcessing = new ImagePostProcessingParameter(uiHandler,cameraParameters,baseCameraHolder, "ipp", "ipp-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            PreviewSize = new PreviewSizeParameter(uiHandler,cameraParameters, baseCameraHolder, "preview-size", "preview-size-values", cameraHolder);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            PreviewFPS = new PreviewFpsParameter(uiHandler, cameraParameters, "preview-frame-rate", "preview-frame-rate-values", (BaseCameraHolder)cameraHolder);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            PreviewFormat = new PreviewFormatParameter(uiHandler,cameraParameters, baseCameraHolder, "preview-format", "preview-format-values", cameraHolder);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            SceneMode =  new SceneModeParameter(uiHandler, cameraParameters, baseCameraHolder, "scene-mode","scene-mode-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        try {
            RedEye = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "redeye-reduction", "redeye-reduction-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            LensShade = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "lensshade", "lensshade-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            VideoStabilization = new VideoStabilizationParameter(uiHandler,cameraParameters,baseCameraHolder,"video-stabilization","");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            ZSL = new ZeroShutterLagParameter(uiHandler,cameraParameters, baseCameraHolder, "", "", cameraHolder);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            SceneDetect = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "scene-detect", "scene-detect-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            Denoise = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "denoise", "denoise-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            DigitalImageStabilization = new DigitalImageStabilizationParameter(uiHandler,cameraParameters, baseCameraHolder, "", "");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            MemoryColorEnhancement = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "mce", "mce-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            SkinToneEnhancment = new DigitalImageStabilizationParameter(uiHandler,cameraParameters, baseCameraHolder, "skinToneEnhancement", "skinToneEnhancement-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            NightMode = new NightModeParameter(uiHandler,cameraParameters, baseCameraHolder,"","", cameraUiWrapper);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            NonZslManualMode = new NonZslManualModeParameter(uiHandler,cameraParameters, baseCameraHolder, "non-zsl-manual-mode", "", cameraHolder);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            Histogram = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "histogram", "histogram-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            CameraMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "camera-mode", "camera-mode-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            DualMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "dual_mode", "");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            ExposureLock = new ExposureLockParameter(uiHandler,cameraParameters, baseCameraHolder, "","");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            VideoSize = new VideoSizeParameter(uiHandler,cameraParameters,baseCameraHolder,"video-size","video-size");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            VideoHDR = new VideoHDRModeParameter(uiHandler,cameraParameters, baseCameraHolder, "", "", cameraHolder);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            VideoHighFramerateVideo = new HighFramerateVideo(uiHandler,cameraParameters, baseCameraHolder, "", "", cameraUiWrapper);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            VideoHighSpeedVideo = new HighSpeedVideo(uiHandler,cameraParameters, baseCameraHolder, "", "", cameraUiWrapper);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            if (baseCameraHolder.DeviceFrameWork == BaseCameraHolder.Frameworks.LG /*&& Build.VERSION.SDK_INT < 21*/)
                VideoProfilesG3 = new VideoProfilesG3Parameter(uiHandler,cameraParameters,baseCameraHolder,"","", cameraUiWrapper);
            else
                VideoProfiles = new VideoProfilesParameter(uiHandler,cameraParameters,baseCameraHolder,"","", cameraUiWrapper);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            CDS_Mode = new CDS_Mode_Parameter(uiHandler,cameraParameters,baseCameraHolder,"","");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            //####No idea what they do, m9 specific, only thing they do is to freez the app####
            RdiMode = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "rdi-mode", "rdi-mode-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            SecureMode = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "secure-mode", "secure-mode-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            //Temporal Noise Reduction http://nofilmschool.com/2012/03/temporal-noise-reduction-ipad-its-improvement
            TnrMode = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "tnr-mode", "tnr-mode-values");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            oismode = new OisParameter(uiHandler,cameraParameters,baseCameraHolder,"","");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            Focuspeak = new FocusPeakModeParameter(uiHandler,baseCameraHolder,cameraUiWrapper.previewHandler);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            SetCameraRotation();
            SetPictureOrientation(0);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            captureBurstExposures = new CupBurstExpModeParameter(uiHandler, cameraParameters, baseCameraHolder, "capture-burst-exposures", "", appSettingsManager);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            morphoHDR = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "morpho-hdr", "");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            morphoHHT = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "morpho-hht", "");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            LensFilter = new VirtualLensFilter(uiHandler,cameraParameters, baseCameraHolder, "", "", cameraUiWrapper);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        try {
            HDRMode = new HDRModeParameter(uiHandler,cameraParameters, baseCameraHolder,"","", cameraUiWrapper);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        Module = new ModuleParameters(uiHandler, appSettingsManager, cameraUiWrapper);
        ParametersEventHandler.ParametersHasLoaded();
        SetAppSettingsToParametersx();
        cameraHolder.SetCameraParameters(cameraParameters);
        //camMode();
        if (((BaseCameraHolder) cameraHolder).DeviceFrameWork == BaseCameraHolder.Frameworks.MTK)
            Mediatek();


    }

    public void SetMeterAREA(FocusRect meteringAreas)
    {
        if(DeviceUtils.IS(Devices.ZTE_ADV))
        {
            try
            {
                final FocusRect lF = meteringAreas;
                Handler handler = new Handler();
                Runnable r = new Runnable() {
                    public void run() {
                        setString("metering-areas", "(" + lF.left + "," + lF.top + "," + lF.right + "," + lF.bottom + ",100)");
                        baseCameraHolder.SetCameraParameters(cameraParameters);
                    }
                };
                handler.postDelayed(r, 1);

            }
            catch (Exception ex)
            {

            }
        }
    }

    public void SetFocusAREA(FocusRect focusAreas, FocusRect meteringAreas)
    {
        if(DeviceUtils.IS(Devices.ZTE_ADV))
        {
            try
            {
                final FocusRect lF = focusAreas;
               Handler handler = new Handler();
                Runnable r = new Runnable() {
                    public void run() {
                        setString("focus-areas", "(" + lF.left + "," + lF.top + "," + lF.right + "," + lF.bottom + ",1000)");
                        baseCameraHolder.SetCameraParameters(cameraParameters);
                    }
                };
                handler.postDelayed(r, 1);
            }
            catch (Exception ex)
            {
            }
        }
        else
        {
            cameraParameters.put("focus-areas", "("+focusAreas.left+ ","+ focusAreas.top+","+ focusAreas.right+ ","+ focusAreas.bottom +",1000)");
            SetParametersToCamera();
        }
    }

    public void SetPictureOrientation(int orientation)
    {
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.ON))
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
            appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack , StringUtils.OFF);
        }
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.OFF))
            ((BaseCameraHolder)cameraHolder).SetCameraRotation(0);
        else
            ((BaseCameraHolder)cameraHolder).SetCameraRotation(180);
    }

    public void LockExposureAndWhiteBalance(boolean value)
    {
        isExposureAndWBLocked = value;
        if (ExposureLock.IsSupported())
            ExposureLock.SetValue(value + "", false);
        SetParametersToCamera();
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


    public void SetAppSettingsToParametersx() {
        super.SetAppSettingsToParameters();
        cameraUiWrapper.moduleHandler.SetModule(appSettingsManager.GetCurrentModule());
    }
    public void FPSRangeLock (int min,int max){

        String mMin =String.valueOf(min*1000);
        String mMax =String.valueOf(max*1000);
        cameraParameters.put("preview-fps-range",mMin+","+mMax);
        cameraParameters.put("preview-frame-rate", mMax);
        baseCameraHolder.ParameterHandler.SetParametersToCamera();

    }

    private void Mediatek()
    {
       // cameraParameters.put("zsd-mode","on");
        //cameraParameters.put("camera-mode","0");
        cameraParameters.put("afeng_raw_dump_flag","1");
        cameraParameters.put("rawsave-mode","2");
        cameraParameters.put("isp-mode","1");
        cameraParameters.put("rawfname", "/mnt/sdcard/DCIM/test.raw");


        cameraHolder.SetCameraParameters(cameraParameters);
        baseCameraHolder.StopPreview();
        baseCameraHolder.StartPreview();
    }

    private void setupLg_G4Parameters()
    {
        cameraParameters.put("lge-camera", "1");
    }


}
