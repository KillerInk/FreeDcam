package com.troop.freedcam.camera.parameters;

import android.os.Build;
import android.os.Handler;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.FocusHandler;
import com.troop.freedcam.camera.parameters.manual.BaseManualParameter;
import com.troop.freedcam.camera.parameters.manual.BurstManualParam;
import com.troop.freedcam.camera.parameters.manual.CCTManualParameter;
import com.troop.freedcam.camera.parameters.manual.ExposureManualParameter;
import com.troop.freedcam.camera.parameters.manual.FXManualParameter;
import com.troop.freedcam.camera.parameters.manual.ISOManualParameter;
import com.troop.freedcam.camera.parameters.manual.LG_G4AeHandler;
import com.troop.freedcam.camera.parameters.manual.ManualFocusClassHandler;
import com.troop.freedcam.camera.parameters.manual.ShutterClassHandler;
import com.troop.freedcam.camera.parameters.manual.SkintoneManualPrameter;
import com.troop.freedcam.camera.parameters.manual.ZoomManualParameter;
import com.troop.freedcam.camera.parameters.modes.BaseModeParameter;
import com.troop.freedcam.camera.parameters.modes.CDS_Mode_Parameter;
import com.troop.freedcam.camera.parameters.modes.CupBurstExpModeParameter;
import com.troop.freedcam.camera.parameters.modes.FocusPeakModeParameter;
import com.troop.freedcam.camera.parameters.modes.HDRModeParameter;
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
import com.troop.freedcam.camera.parameters.modes.VideoProfilesG3Parameter;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.camera.parameters.modes.VideoStabilizationParameter;
import com.troop.freedcam.camera.parameters.modes.VirtualLensFilter;
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
        baseCameraHolder = cameraUiWrapper.cameraHolder;
        this.cameraUiWrapper = cameraUiWrapper;
    }

    @Override
    public void SetParametersToCamera(HashMap<String, String> params) {
        cameraHolder.SetCameraParameters(params);
    }

    public void LoadParametersFromCamera()
    {
        cameraParameters = baseCameraHolder.GetCameraParameters();
        initParameters();
    }

    private void logParameters(HashMap<String, String> parameters)
    {
        Logger.d(TAG, "Manufactur:" + Build.MANUFACTURER);
        Logger.d(TAG, "Model:" + Build.MODEL);
        Logger.d(TAG, "Product:" + Build.PRODUCT);
        Logger.d(TAG, "OS:"+ System.getProperty("os.version"));
        for(Map.Entry e : parameters.entrySet())
        {
            Logger.d(TAG, e.getKey() + "=" + e.getValue());
        }
    }


    private void initParameters()
    {
        if (baseCameraHolder.DeviceFrameWork == BaseCameraHolder.Frameworks.LG)
            cameraParameters.put("lge-camera","1");
        logParameters(cameraParameters);


        //setup first Pictureformat its needed for manual parameters to
        // register their listners there if its postprocessing parameter
        try {
            PictureFormat = new PictureFormatHandler(uiHandler,cameraParameters, baseCameraHolder);
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner((PictureFormatHandler) PictureFormat);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PictureSize = new PictureSizeParameter(uiHandler,cameraParameters,baseCameraHolder, "picture-size", "picture-size-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            FocusMode = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder,"focus-mode","focus-mode-values");
            FocusMode.addEventListner(((FocusHandler) cameraHolder.Focus).focusModeListner);
        } catch (Exception e) {
            Logger.exception(e);
        }

        locationParameter = new LocationParameter(uiHandler, appSettingsManager, cameraHolder);

        createManualBrightness();

        createManualContrast();

        try {
            ManualConvergence = new BaseManualParameter(cameraParameters, "manual-convergence", "supported-manual-convergence-max", "supported-manual-convergence-min", this,1);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createManualExposure();

        try {
                ManualFocus = ManualFocusClassHandler.GetManualFocus(cameraParameters, this,baseCameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createManualSaturation();

        createManualSharpness();

        try {
            if (DeviceUtils.IS(Devices.LG_G4))
                aeHandlerG4 = new LG_G4AeHandler(cameraParameters,baseCameraHolder,this);
            else
            {
                ManualShutter = ShutterClassHandler.getShutterClass(cameraParameters, this, cameraHolder);
                ISOManual = new ISOManualParameter(cameraParameters, "", "", "",baseCameraHolder, this);
            }
        } catch (Exception e) {
            Logger.exception(e);
        }


        try {
            WhiteBalanceMode = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "whitebalance", "whitebalance-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            CCT = new CCTManualParameter(cameraParameters,"","","", this);
        } catch (Exception e) {
            Logger.exception(e);
        }
        //PictureFormat.addEventListner(((BaseManualParameter)CCT).GetPicFormatListner());
        //cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) CCT).GetModuleListner());

        try {
            Skintone = new SkintoneManualPrameter(cameraParameters,"","","",this);
            PictureFormat.addEventListner(((BaseManualParameter)Skintone).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) Skintone).GetModuleListner());
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            FX = new FXManualParameter(cameraParameters,"","","", this);
            PictureFormat.addEventListner(((BaseManualParameter)FX).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) FX).GetModuleListner());
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            Burst = new BurstManualParam(cameraParameters,"","","",this);
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) Burst).GetModuleListner());
        } catch (Exception e) {
            Logger.exception(e);
        }


        try {
            Zoom = new ZoomManualParameter(cameraParameters,"", "", "", this);
            PictureFormat.addEventListner(((BaseManualParameter)Zoom).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) Zoom).GetModuleListner());
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            ColorMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "effect", "effect-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        createExposureMode();

        try {
            FlashMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"flash-mode","flash-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        createIsoMode();

        try {
            AntiBandingMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "antibanding", "antibanding-values");
        } catch (Exception e) {
            Logger.exception(e);
        }



        try {
            JpegQuality = new JpegQualityParameter(uiHandler,cameraParameters, baseCameraHolder, "jpeg-quality", "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            AE_Bracket = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "ae-bracket-hdr", "ae-bracket-hdr-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            ImagePostProcessing = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "ipp", "ipp-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewSize = new PreviewSizeParameter(uiHandler,cameraParameters, baseCameraHolder, "preview-size", "preview-size-values", cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewFPS = new PreviewFpsParameter(uiHandler, cameraParameters, "preview-frame-rate", "preview-frame-rate-values", (BaseCameraHolder)cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewFormat = new PreviewFormatParameter(uiHandler,cameraParameters, baseCameraHolder, "preview-format", "preview-format-values", cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            SceneMode =  new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "scene-mode","scene-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            RedEye = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "redeye-reduction", "redeye-reduction-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            LensShade = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "lensshade", "lensshade-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            VideoStabilization = new VideoStabilizationParameter(uiHandler,cameraParameters,baseCameraHolder,"video-stabilization","");
        } catch (Exception e) {
            Logger.exception(e);
        }

        createZeroShutterLag();

        try {
            SceneDetect = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "scene-detect", "scene-detect-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            Denoise = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "denoise", "denoise-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if(cameraParameters.containsKey("sony-vs"))
                DigitalImageStabilization = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"sony-vs","sony-vs-values");
            else if (cameraParameters.containsKey("dis"))
                DigitalImageStabilization = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"dis","dis-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            MemoryColorEnhancement = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "mce", "mce-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            SkinToneEnhancment = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "skinToneEnhancement", "skinToneEnhancement-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            NightMode = new NightModeParameter(uiHandler,cameraParameters, baseCameraHolder,"","", cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            NonZslManualMode = new NonZslManualModeParameter(uiHandler,cameraParameters, baseCameraHolder, "non-zsl-manual-mode", "", cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        /*try {
            Histogram = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "histogram", "histogram-values");
        } catch (Exception e) {
            Logger.exception(e);
        }*/

        try {
            CameraMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "camera-mode", "camera-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            DualMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "dual_mode", "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        /*try {
            ExposureLock = new ExposureLockParameter(uiHandler,cameraParameters, baseCameraHolder, "","");
        } catch (Exception e) {
            Logger.exception(e);
        }*/

        /*try {
            VideoSize = new VideoSizeParameter(uiHandler,cameraParameters,baseCameraHolder,"video-size","video-size");
        } catch (Exception e) {
            Logger.exception(e);
        }*/

        createVideoHDR();

        try {
            if (baseCameraHolder.DeviceFrameWork == BaseCameraHolder.Frameworks.LG /*&& Build.VERSION.SDK_INT < 21*/)
                VideoProfilesG3 = new VideoProfilesG3Parameter(uiHandler,cameraParameters,baseCameraHolder,"","", cameraUiWrapper);
            else
                VideoProfiles = new VideoProfilesParameter(uiHandler,cameraParameters,baseCameraHolder,"","", cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            CDS_Mode = new CDS_Mode_Parameter(uiHandler,cameraParameters,baseCameraHolder,"","");
        } catch (Exception e) {
            Logger.exception(e);
        }

        //####No idea what they do, m9 specific, only thing they do is to freez the app####
        try {
            RdiMode = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "rdi-mode", "rdi-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            SecureMode = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "secure-mode", "secure-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        //Temporal Noise Reduction http://nofilmschool.com/2012/03/temporal-noise-reduction-ipad-its-improvement
        try {
            TnrMode = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "tnr-mode", "tnr-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            oismode = new OisParameter(uiHandler,cameraParameters,baseCameraHolder,"","");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            Focuspeak = new FocusPeakModeParameter(uiHandler,baseCameraHolder,cameraUiWrapper.previewHandler);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createHighFrameRate();

        try {
            SetCameraRotation();
            SetPictureOrientation(0);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            captureBurstExposures = new CupBurstExpModeParameter(uiHandler, cameraParameters, baseCameraHolder, "capture-burst-exposures", "");
        }
        catch (Exception e)
        {
            Logger.exception(e);
        }

        try {
            morphoHDR = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "morpho-hdr", "");

            morphoHHT = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "morpho-hht", "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if (DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV))
                LensFilter = new VirtualLensFilter(uiHandler,cameraParameters, baseCameraHolder, "", "", cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            HDRMode = new HDRModeParameter(uiHandler,cameraParameters, baseCameraHolder,"","", cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            Module = new ModuleParameters(uiHandler, appSettingsManager, cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }


        try {
            SetAppSettingsToParameters();
            SetParametersToCamera(cameraParameters);
        } catch (Exception e) {
            Logger.exception(e);
        }
        cameraUiWrapper.moduleHandler.SetModule(appSettingsManager.GetCurrentModule());

        ParametersEventHandler.ParametersHasLoaded();

        try {
            if (((BaseCameraHolder) cameraHolder).DeviceFrameWork == BaseCameraHolder.Frameworks.MTK)
                Mediatek();
        } catch (Exception e) {
            Logger.exception(e);
        }

    }

    private void createExposureMode() {
        try
        {
            if (cameraParameters.containsKey("exposure-mode-values"))
                ExposureMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"exposure","exposure-mode-values");
            else if (cameraParameters.containsKey("auto-exposure-values"))
                ExposureMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"auto-exposure","auto-exposure-values");
            else if(cameraParameters.containsKey("sony-metering-mode-values"))
                ExposureMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"sony-metering-mode","sony-metering-mode-values");
            else if(cameraParameters.containsKey("exposure-meter-values"))
                ExposureMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"exposure-meter","exposure-meter-values");
            if (ExposureMode != null)
                ExposureMode.addEventListner(((FocusHandler) cameraHolder.Focus).aeModeListner);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createIsoMode() {
        try {
            if (cameraParameters.containsKey("iso-mode-values"))
                IsoMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"iso","iso-mode-values");
            else if (cameraParameters.containsKey("iso-values"))
                IsoMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"iso","iso-values");
            else if (cameraParameters.containsKey("iso-speed-values"))
                IsoMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"iso-speed","iso-speed-values");
            else if (cameraParameters.containsKey("sony-iso-values"))
                IsoMode = new BaseModeParameter(uiHandler, cameraParameters,baseCameraHolder,"sony-iso","sony-iso-values");
            else if (cameraParameters.containsKey("lg-iso-values"))
                IsoMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"iso","lg-iso-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualBrightness() {
        try {
            if (cameraParameters.containsKey("brightness") && !cameraParameters.containsKey("brightness-values"))
            {
                //p920hack
                if (!cameraParameters.containsKey("max-brightness") && !cameraParameters.containsKey("brightness-max"))
                {
                    cameraParameters.put("max-brightness", "100");
                    cameraParameters.put("min-brightness", "0");
                }
                if (cameraParameters.containsKey("brightness-max"))
                {
                    ManualBrightness = new BaseManualParameter(cameraParameters, "brightness", "brightness-max", "brightness-min", this, 1);
                }
                else if (cameraParameters.containsKey("max-brightness"))
                    ManualBrightness = new BaseManualParameter(cameraParameters, "brightness", "max-brightness", "min-brightness", this, 1);

            }
            else if (cameraParameters.containsKey("luma-adaptation"))
                ManualBrightness =  new BaseManualParameter(cameraParameters,"luma-adaptation","max-brightness","min-brightness",this,1);

            if (ManualBrightness != null && baseCameraHolder.DeviceFrameWork != BaseCameraHolder.Frameworks.MTK) {
                PictureFormat.addEventListner(((BaseManualParameter) ManualBrightness).GetPicFormatListner());
                cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) ManualBrightness).GetModuleListner());
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualContrast() {
        try {
            if (cameraParameters.containsKey("contrast") && !cameraParameters.containsKey("contrast-values"))
            {
                //p920 hack
                if (!cameraParameters.containsKey("max-contrast") && !cameraParameters.containsKey("contrast-max")) {
                    cameraParameters.put("max-contrast", "100");
                    cameraParameters.put("min-contrast", "0");
                }
                if (cameraParameters.containsKey("contrast-max"))
                    ManualContrast =  new BaseManualParameter(cameraParameters,"contrast", "contrast-max", "contrast-min",this,1);
                else if (cameraParameters.containsKey("max-contrast"))
                    ManualContrast =  new BaseManualParameter(cameraParameters,"contrast", "max-contrast", "min-contrast",this,1);

            }
            if (ManualContrast != null && baseCameraHolder.DeviceFrameWork != BaseCameraHolder.Frameworks.MTK) {
                PictureFormat.addEventListner(((BaseManualParameter) ManualContrast).GetPicFormatListner());
                cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) ManualContrast).GetModuleListner());
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createHighFrameRate() {
        try {
            if (cameraParameters.containsKey("video-hfr-values"))
            {
                String[] hfr_values = cameraParameters.get("video-hfr-values").split(",");
                if(hfr_values.length <= 2)
                    cameraParameters.put("video-hfr-values", "off,60,120");
            }
            VideoHighFramerateVideo = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "video-hfr", "video-hfr-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createVideoHDR() {
        try {
            if (cameraParameters.containsKey("video-hdr"))
                VideoHDR = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"video-hdr", "video-hdr-values");
            else if (cameraParameters.containsKey("sony-video-hdr"))
                VideoHDR = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"sony-video-hdr","sony-video-hdr-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createZeroShutterLag() {
        try {
            if (cameraParameters.containsKey("zsl"))
                ZSL = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"zsl","zsl-values");
            else if (cameraParameters.containsKey("mode"))
                ZSL = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"mode","mode-values");
            else if (cameraParameters.containsKey("zsd-mode"))
                ZSL =new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"zsd-mode", "zsd-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualSharpness() {
        try {
            if (cameraParameters.containsKey("sharpness") && !cameraParameters.containsKey("sharpness-values"))
            {
                if (!cameraParameters.containsKey("max-sharpness") && !cameraParameters.containsKey("sharpness-max")) {
                    cameraParameters.put("max-sharpness", "100");
                    cameraParameters.put("min-sharpness", "0");
                }
                int step = 1;
                if (cameraParameters.containsKey("sharpness-step"))
                    step = Integer.parseInt(cameraParameters.get("sharpness-step"));

                if (cameraParameters.containsKey("sharpness-max"))
                {
                    ManualSharpness = new BaseManualParameter(cameraParameters, "sharpness", "sharpness-max", "sharpness-min", this,step);
                }
                else if (cameraParameters.containsKey("max-sharpness"))
                {
                    ManualSharpness = new BaseManualParameter(cameraParameters, "sharpness", "max-sharpness", "min-sharpness", this,step);
                }

            }
            if(ManualSharpness != null && baseCameraHolder.DeviceFrameWork != BaseCameraHolder.Frameworks.MTK) {
                PictureFormat.addEventListner(((BaseManualParameter) ManualSharpness).GetPicFormatListner());
                cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) ManualSharpness).GetModuleListner());
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualSaturation() {
        try
        {
            if (cameraParameters.containsKey("saturation") && !cameraParameters.containsKey("saturation-values"))
            {
                //p920 hack
                if (!cameraParameters.containsKey("max-saturation") && !cameraParameters.containsKey("saturation-max")) {
                    cameraParameters.put("max-saturation", "100");
                    cameraParameters.put("min-saturation", "0");
                }
                //check first max after evo 3d has both but max infront is empty
                if (cameraParameters.containsKey("saturation-max"))
                    ManualSaturation = new BaseManualParameter(cameraParameters, "saturation", "saturation-max", "saturation-min", this,1);
                else if (cameraParameters.containsKey("max-saturation"))
                    ManualSaturation = new BaseManualParameter(cameraParameters, "saturation", "max-saturation", "min-saturation", this,1);

            }
            if (ManualSaturation != null && baseCameraHolder.DeviceFrameWork != BaseCameraHolder.Frameworks.MTK) {
                PictureFormat.addEventListner(((BaseManualParameter) ManualSaturation).GetPicFormatListner());
                cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) ManualSaturation).GetModuleListner());
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualExposure() {
        try
        {
            float expostep = 1;
            if(cameraParameters.containsKey("exposure-compensation-step"))
                expostep = Float.parseFloat(cameraParameters.get("exposure-compensation-step"));
            if (cameraParameters.containsKey("lg-ev-ctrl"))
                ManualExposure = new ExposureManualParameter(cameraParameters,"lg-ev-ctrl","max-exposure-compensation","min-exposure-compensation", this,expostep);
            else if(cameraParameters.containsKey("exposure-compensation"))
                ManualExposure = new ExposureManualParameter(cameraParameters,"exposure-compensation","max-exposure-compensation","min-exposure-compensation", this,expostep);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    @Override
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
                //handler.postDelayed(r, 1);
                handler.post(r);

            }
            catch (Exception e)
            {
                Logger.exception(e);
            }
        }
    }
    @Override
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
                //handler.postDelayed(r, 1);
                handler.post(r);
            }
            catch (Exception e)
            {
                Logger.exception(e);
            }
        }
        else
        {
            cameraParameters.put("focus-areas", "("+focusAreas.left+ ","+ focusAreas.top+","+ focusAreas.right+ ","+ focusAreas.bottom +",1000)");
            SetParametersToCamera(cameraParameters);
        }
    }
    @Override
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
        catch (Exception e)
        {
            Logger.exception(e);
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
    @Override
    public void LockExposureAndWhiteBalance(boolean value)
    {
        isExposureAndWBLocked = value;
        if (ExposureLock.IsSupported())
            ExposureLock.SetValue(value + "", false);
        SetParametersToCamera(cameraParameters);
    }

    public void setString(String param, String value)
    {
        try
        {
            cameraParameters.put(param, value);
            cameraHolder.SetCameraParameters(cameraParameters);
        }
        catch (Exception e)
        {
            Logger.exception(e);
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

    public String ExposureTime()
    {
        if (cameraParameters.containsKey("exposure-time")) {
            final String focal = cameraParameters.get("exposure-time");
            return focal;
        }
        else
            return "non";

    }

    @Override
    public void SetAppSettingsToParameters() {
        super.SetAppSettingsToParameters();
    }
    public void FPSRangeLock (int min,int max){
        String mMin =String.valueOf(min*1000);
        String mMax =String.valueOf(max*1000);
        cameraParameters.put("preview-fps-range",mMin+","+mMax);
        cameraParameters.put("preview-frame-rate", mMax);
        SetParametersToCamera(cameraParameters);

    }

    private void Mediatek()
    {
        // cameraParameters.put("zsd-mode","on");
        //cameraParameters.put("camera-mode","0");
        cameraParameters.put("afeng_raw_dump_flag", "1");
        cameraParameters.put("rawsave-mode", "2");
        cameraParameters.put("isp-mode", "1");
        cameraParameters.put("rawfname", "/mnt/sdcard/DCIM/test.raw");
    }


}
