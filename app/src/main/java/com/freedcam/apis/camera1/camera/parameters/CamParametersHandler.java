package com.freedcam.apis.camera1.camera.parameters;

import android.content.Context;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.FocusHandler;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_LGG4;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_MTK;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_QcomM;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseManualParameter;
import com.freedcam.apis.camera1.camera.parameters.manual.BurstManualParam;
import com.freedcam.apis.camera1.camera.parameters.manual.CCTManualParameter;
import com.freedcam.apis.camera1.camera.parameters.manual.ExposureManualParameter;
import com.freedcam.apis.camera1.camera.parameters.manual.FXManualParameter;
import com.freedcam.apis.camera1.camera.parameters.manual.FocusManualClassHandler;
import com.freedcam.apis.camera1.camera.parameters.manual.ISOManualParameter;
import com.freedcam.apis.camera1.camera.parameters.manual.ShutterClassHandler;
import com.freedcam.apis.camera1.camera.parameters.manual.SkintoneManualPrameter;
import com.freedcam.apis.camera1.camera.parameters.manual.ZoomManualParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.BaseModeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.CDS_Mode_Parameter;
import com.freedcam.apis.camera1.camera.parameters.modes.CupBurstExpModeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.ExposureLockParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.FocusPeakModeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.HDRModeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.JpegQualityParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.NightModeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.NonZslManualModeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.OisParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.OpCodeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.PictureFormatHandler;
import com.freedcam.apis.camera1.camera.parameters.modes.PictureSizeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.PreviewFormatParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.PreviewFpsParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.PreviewSizeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.VideoProfilesG3Parameter;
import com.freedcam.apis.camera1.camera.parameters.modes.VideoProfilesParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.VideoStabilizationParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.VirtualLensFilter;
import com.freedcam.apis.basecamera.camera.FocusRect;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.apis.basecamera.camera.parameters.modes.LocationParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.ModuleParameters;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by troop on 17.08.2014.
 */
public class CamParametersHandler extends AbstractParameterHandler
{

    private final String TAG = CamParametersHandler.class.getSimpleName();

    private HashMap<String, String> cameraParameters;
    public HashMap<String, String> getParameters(){return cameraParameters;}
    public CameraHolderApi1 cameraHolder;
    public BaseModeParameter DualMode;
    private CameraUiWrapper cameraUiWrapper;

    public CamParametersHandler(CameraUiWrapper cameraUiWrapper, Handler uiHandler, Context context,AppSettingsManager appSettingsManager)
    {
        super(cameraUiWrapper.cameraHolder, uiHandler,context,appSettingsManager);
        cameraHolder = cameraUiWrapper.cameraHolder;
        this.cameraUiWrapper = cameraUiWrapper;
    }

    @Override
    public void SetParametersToCamera(HashMap<String, String> params)
    {
        Logger.d(TAG, "SetParametersToCam");
        cameraHolder.SetCameraParameters(params);
    }

    public void LoadParametersFromCamera()
    {
        cameraParameters = cameraHolder.GetCameraParameters();
        initParameters();
    }

    private void logParameters(HashMap<String, String> parameters)
    {
        Logger.d(TAG, "Manufactur:" + Build.MANUFACTURER);
        Logger.d(TAG, "Model:" + Build.MODEL);
        Logger.d(TAG, "Product:" + Build.PRODUCT);
        Logger.d(TAG, "OS:" + System.getProperty("os.version"));
        for(Map.Entry e : parameters.entrySet())
        {
            Logger.d(TAG, e.getKey() + "=" + e.getValue());
        }
    }


    private void initParameters()
    {
        if (cameraHolder.DeviceFrameWork == CameraHolderApi1.Frameworks.LG)
            cameraParameters.put("lge-camera","1");
        logParameters(cameraParameters);


        //setup first Pictureformat its needed for manual parameters to
        // register their listners there if its postprocessing parameter
        try {
            PictureFormat = new PictureFormatHandler(uiHandler,cameraParameters, cameraHolder, this);
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner((PictureFormatHandler) PictureFormat);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PictureSize = new PictureSizeParameter(uiHandler,cameraParameters,cameraHolder, "picture-size-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            FocusMode = new BaseModeParameter(uiHandler,cameraParameters, cameraHolder,"focus-mode","focus-mode-values");
            FocusMode.addEventListner(((FocusHandler) cameraHolder.Focus).focusModeListner);
        } catch (Exception e) {
            Logger.exception(e);
        }

        locationParameter = new LocationParameter(uiHandler, cameraHolder,context,appSettingsManager);

        createManualBrightness();

        createManualContrast();

        try {
            ManualConvergence = new BaseManualParameter(cameraParameters, "manual-convergence", "supported-manual-convergence-max", "supported-manual-convergence-min", this,1);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createManualExposure();

        try {
                ManualFocus = FocusManualClassHandler.GetManualFocus(cameraParameters, this,cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createManualSaturation();

        createManualSharpness();

        try {
            AE_Handler_LGG4 aeHandlerG4;
            if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
                aeHandlerG4 = new AE_Handler_LGG4(cameraParameters,cameraHolder,this);
            else if(cameraParameters.containsKey("m-ss") && cameraParameters.containsKey("m-sr-g"))
            {
                AE_Handler_MTK aeHandlerMTK = new AE_Handler_MTK(cameraParameters, cameraHolder, this);
            }
            else if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.AlcatelIdol3_Moto_MSM8982_8994) ||DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.QC_Manual_New))
            {
                AE_Handler_QcomM aeHandlerQcomM = new AE_Handler_QcomM(uiHandler, cameraParameters, cameraHolder, this);
            }
            else
            {
                ManualShutter = ShutterClassHandler.getShutterClass(cameraParameters, this, cameraHolder);
                ISOManual = new ISOManualParameter(cameraParameters, "", "", "",cameraHolder, this);
            }
        } catch (Exception e) {
            Logger.exception(e);
        }


        try {
            WhiteBalanceMode = new BaseModeParameter(uiHandler,cameraParameters, cameraHolder, "whitebalance", "whitebalance-values");
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
            Skintone = new SkintoneManualPrameter(cameraParameters, "","",this);
            PictureFormat.addEventListner(((BaseManualParameter)Skintone).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) Skintone).GetModuleListner());
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            FX = new FXManualParameter(cameraParameters, "","", this);
            PictureFormat.addEventListner(((BaseManualParameter)FX).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) FX).GetModuleListner());
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            Burst = new BurstManualParam(cameraParameters, this);
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) Burst).GetModuleListner());
        } catch (Exception e) {
            Logger.exception(e);
        }


        try {
            Zoom = new ZoomManualParameter(cameraParameters, "", "", this);
            PictureFormat.addEventListner(((BaseManualParameter)Zoom).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) Zoom).GetModuleListner());
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            ColorMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder, "effect", "effect-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        createExposureMode();

        try {
            FlashMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"flash-mode","flash-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        createIsoMode();

        try {
            AntiBandingMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder, "antibanding", "antibanding-values");
        } catch (Exception e) {
            Logger.exception(e);
        }



        try {
            JpegQuality = new JpegQualityParameter(uiHandler,cameraParameters, cameraHolder, "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            AE_Bracket = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder, "ae-bracket-hdr", "ae-bracket-hdr-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
           // if (DeviceUtils.IS(Devices.p8lite))
            ImagePostProcessing = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder, "ipp", "ipp-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewSize = new PreviewSizeParameter(uiHandler,cameraParameters, cameraHolder, "preview-size-values", cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewFPS = new PreviewFpsParameter(uiHandler, cameraParameters, "preview-frame-rate-values", (CameraHolderApi1)cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewFormat = new PreviewFormatParameter(uiHandler,cameraParameters, cameraHolder, "preview-format-values", cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            SceneMode =  new BaseModeParameter(uiHandler, cameraParameters, cameraHolder, "scene-mode","scene-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            RedEye = new BaseModeParameter(uiHandler, cameraParameters, cameraHolder, "redeye-reduction", "redeye-reduction-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            LensShade = new BaseModeParameter(uiHandler,cameraParameters, cameraHolder, "lensshade", "lensshade-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            VideoStabilization = new VideoStabilizationParameter(uiHandler,cameraParameters,cameraHolder, "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        createZeroShutterLag();

        try {
            SceneDetect = new BaseModeParameter(uiHandler,cameraParameters, cameraHolder, "scene-detect", "scene-detect-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if(cameraParameters.containsKey("3dnr-mode")) {
                if (cameraParameters.get("3dnr-mode-values").equals("on,off")) {
                    Denoise = new BaseModeParameter(uiHandler, cameraParameters, cameraHolder, "3dnr-mode", "3dnr-mode-values");
                }
            }
            else {
                Denoise = new BaseModeParameter(uiHandler, cameraParameters, cameraHolder, "denoise", "denoise-values");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if(cameraParameters.containsKey("sony-vs"))
                DigitalImageStabilization = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"sony-vs","sony-vs-values");
            else if (cameraParameters.containsKey("dis"))
                DigitalImageStabilization = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"dis","dis-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            MemoryColorEnhancement = new BaseModeParameter(uiHandler,cameraParameters, cameraHolder, "mce", "mce-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            SkinToneEnhancment = new BaseModeParameter(uiHandler,cameraParameters, cameraHolder, "skinToneEnhancement", "skinToneEnhancement-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            NightMode = new NightModeParameter(uiHandler,cameraParameters, cameraHolder, "", cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            NonZslManualMode = new NonZslManualModeParameter(uiHandler,cameraParameters, cameraHolder, "", cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        /*try {
            Histogram = new BaseModeParameter(uiHandler,cameraParameters,cameraHolderApi1, "histogram", "histogram-values");
        } catch (Exception e) {
            Logger.exception(e);
        }*/

        try {
            CameraMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder, "camera-mode", "camera-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            DualMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder, "dual_mode", "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            ExposureLock = new ExposureLockParameter(uiHandler,cameraParameters, cameraHolder, "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            VideoSize = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"video-size","video-size");
        } catch (Exception e) {
            Logger.exception(e);
        }

        createVideoHDR();

        try {
            if (cameraHolder.DeviceFrameWork == CameraHolderApi1.Frameworks.LG /*&& Build.VERSION.SDK_INT < 21*/)
                VideoProfilesG3 = new VideoProfilesG3Parameter(uiHandler,cameraParameters,cameraHolder, "", cameraUiWrapper);
            else
                VideoProfiles = new VideoProfilesParameter(uiHandler,cameraParameters,cameraHolder, "", cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            CDS_Mode = new CDS_Mode_Parameter(uiHandler,cameraParameters,cameraHolder,"");
        } catch (Exception e) {
            Logger.exception(e);
        }

        //####No idea what they do, m9 specific, only thing they do is to freez the app####
        try {
            RdiMode = new BaseModeParameter(uiHandler, cameraParameters, cameraHolder, "rdi-mode", "rdi-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            SecureMode = new BaseModeParameter(uiHandler, cameraParameters, cameraHolder, "secure-mode", "secure-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        //Temporal Noise Reduction http://nofilmschool.com/2012/03/temporal-noise-reduction-ipad-its-improvement
        try {
            TnrMode = new BaseModeParameter(uiHandler, cameraParameters, cameraHolder, "tnr-mode", "tnr-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            oismode = new OisParameter(uiHandler,cameraParameters,cameraHolder, "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            Focuspeak = new FocusPeakModeParameter(uiHandler,cameraHolder,cameraUiWrapper.previewHandler);
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
            captureBurstExposures = new CupBurstExpModeParameter(uiHandler, cameraParameters, cameraHolder, "",appSettingsManager);
        }
        catch (Exception e)
        {
            Logger.exception(e);
        }

        try {
            morphoHDR = new BaseModeParameter(uiHandler, cameraParameters, cameraHolder, "morpho-hdr", "");

            morphoHHT = new BaseModeParameter(uiHandler, cameraParameters, cameraHolder, "morpho-hht", "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if (DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV))
                LensFilter = new VirtualLensFilter(uiHandler,cameraParameters, cameraHolder, "", cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            HDRMode = new HDRModeParameter(uiHandler,cameraParameters, cameraHolder, "", cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        opcode = new OpCodeParameter(uiHandler);

        if (DeviceUtils.isCamera1DNGSupportedDevice())
            matrixChooser = new MatrixChooserParameter(uiHandler);

        try {
            Module = new ModuleParameters(uiHandler, cameraUiWrapper,appSettingsManager);
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

        ParametersHasLoaded();

        try {
            if (cameraHolder.DeviceFrameWork == CameraHolderApi1.Frameworks.MTK)
                Mediatek();
        } catch (Exception e) {
            Logger.exception(e);
        }

    }

    private void createExposureMode() {
        try
        {
            if (cameraParameters.containsKey("exposure-mode-values"))
                ExposureMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"exposure","exposure-mode-values");
            else if (cameraParameters.containsKey("auto-exposure-values"))
                ExposureMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"auto-exposure","auto-exposure-values");
            else if(cameraParameters.containsKey("sony-metering-mode-values"))
                ExposureMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"sony-metering-mode","sony-metering-mode-values");
            else if(cameraParameters.containsKey("exposure-meter-values"))
                ExposureMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"exposure-meter","exposure-meter-values");
            if (ExposureMode != null)
                ExposureMode.addEventListner(((FocusHandler) cameraHolder.Focus).aeModeListner);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createIsoMode() {
        try {
            if (cameraParameters.containsKey("iso-mode-values"))
                IsoMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"iso","iso-mode-values");
            else if (cameraParameters.containsKey("iso-values"))
                IsoMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"iso","iso-values");
            else if (cameraParameters.containsKey("iso-speed-values"))
                IsoMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"iso-speed","iso-speed-values");
            else if (cameraParameters.containsKey("sony-iso-values"))
                IsoMode = new BaseModeParameter(uiHandler, cameraParameters,cameraHolder,"sony-iso","sony-iso-values");
            else if (cameraParameters.containsKey("lg-iso-values"))
                IsoMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"iso","lg-iso-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualBrightness() {
        try {
            if (cameraParameters.containsKey("brightness") && cameraParameters.containsKey("brightness-values"))
            {
                cameraParameters.put("brightness-max", "3");
                cameraParameters.put("brightness-min", "0");
                ManualBrightness =  new BaseManualParameter(cameraParameters,"brightness", "brightness-max", "brightness-min",this,1);

            }

            else if (cameraParameters.containsKey("brightness") && !cameraParameters.containsKey("brightness-values"))
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
                else if(DeviceUtils.IS(DeviceUtils.Devices.p8lite))
                    ManualBrightness = new BaseManualParameter(cameraParameters, "brightness", "max-brightness", "min-brightness", this, 50);
                else if (cameraParameters.containsKey("max-brightness"))
                    ManualBrightness = new BaseManualParameter(cameraParameters, "brightness", "max-brightness", "min-brightness", this, 1);

            }
            else if (cameraParameters.containsKey("luma-adaptation"))
                ManualBrightness =  new BaseManualParameter(cameraParameters,"luma-adaptation","max-brightness","min-brightness",this,1);

            if (ManualBrightness != null ) {
                PictureFormat.addEventListner(((BaseManualParameter) ManualBrightness).GetPicFormatListner());
                cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) ManualBrightness).GetModuleListner());
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualContrast() {
        try {
            if (cameraParameters.containsKey("contrast") && cameraParameters.containsKey("contrast-values"))
            {
                cameraParameters.put("contrast-max", "3");
                cameraParameters.put("contrast-min", "0");
                ManualContrast =  new BaseManualParameter(cameraParameters,"contrast", "contrast-max", "contrast-min",this,1);

            }
            else if (cameraParameters.containsKey("contrast") && !cameraParameters.containsKey("contrast-values"))
            {
                //p920 hack
                if (!cameraParameters.containsKey("max-contrast") && !cameraParameters.containsKey("contrast-max")) {
                    cameraParameters.put("max-contrast", "100");
                    cameraParameters.put("min-contrast", "0");
                }
                if (cameraParameters.containsKey("contrast-max"))
                    ManualContrast =  new BaseManualParameter(cameraParameters,"contrast", "contrast-max", "contrast-min",this,1);
                else if(DeviceUtils.IS(DeviceUtils.Devices.p8lite))
                    ManualContrast =  new BaseManualParameter(cameraParameters,"contrast", "max-contrast", "min-contrast",this,25);
                else if (cameraParameters.containsKey("max-contrast"))
                    ManualContrast =  new BaseManualParameter(cameraParameters,"contrast", "max-contrast", "min-contrast",this,1);



            }
            if (ManualContrast != null ) {
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
                String hfrvals = cameraParameters.get("video-hfr-values");
                if (!hfrvals.equals("off"))
                {
                    if (hfrvals.equals(""))
                        cameraParameters.put("video-hfr-values", "off,60,120");
                }
            }
            VideoHighFramerateVideo = new BaseModeParameter(uiHandler, cameraParameters, cameraHolder, "video-hfr", "video-hfr-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createVideoHDR() {
        try {
            if (cameraParameters.containsKey("video-hdr"))
                VideoHDR = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"video-hdr", "video-hdr-values");
            else if (cameraParameters.containsKey("sony-video-hdr"))
                VideoHDR = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"sony-video-hdr","sony-video-hdr-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createZeroShutterLag() {
        try {
            if (cameraParameters.containsKey("zsl"))
                ZSL = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"zsl","zsl-values");
            else if (cameraParameters.containsKey("mode"))
                ZSL = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"mode","mode-values");
            else if (cameraParameters.containsKey("zsd-mode"))
                ZSL =new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"zsd-mode", "zsd-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualSharpness() {
        try {

            if (cameraParameters.containsKey("edge") && cameraParameters.containsKey("edge-values"))
            {
                cameraParameters.put("edge-max", "3");
                cameraParameters.put("edge-min", "0");
                ManualSharpness =  new BaseManualParameter(cameraParameters,"edge", "edge-max", "edge-min",this,1);

            }
            else if (cameraParameters.containsKey("sharpness") && !cameraParameters.containsKey("sharpness-values"))
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
            if(ManualSharpness != null ) {
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
            if (cameraParameters.containsKey("saturation") && cameraParameters.containsKey("saturation-values"))
            {
                cameraParameters.put("saturation-max", "3");
                cameraParameters.put("saturation-min", "0");
                ManualSaturation =  new BaseManualParameter(cameraParameters,"saturation", "saturation-max", "saturation-min",this,1);

            }
            else if (cameraParameters.containsKey("saturation") && !cameraParameters.containsKey("saturation-values"))
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
            if (ManualSaturation != null ) {
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
                ManualExposure = new ExposureManualParameter(cameraParameters,"lg-ev-ctrl", "min-exposure-compensation", this,expostep);
            else if(cameraParameters.containsKey("exposure-compensation"))
                ManualExposure = new ExposureManualParameter(cameraParameters,"exposure-compensation", "min-exposure-compensation", this,expostep);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    @Override
    public void SetMeterAREA(FocusRect meteringAreas)
    {
        if(DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV))
        {
            try
            {
                final FocusRect lF = meteringAreas;
                Handler handler = new Handler();
                Runnable r = new Runnable() {
                    public void run() {
                        //cameraParameters.put("metering-areas", "(" + lF.left + "," + lF.top + "," + lF.right + "," + lF.bottom + ",100)");
                        cameraParameters.put("touch-aec","on");
                        cameraParameters.put("raw-size","4208x3120");
                        cameraParameters.put("touch-index-aec", lF.x + "," + lF.y);
                        cameraHolder.SetCameraParameters(cameraParameters);
                    }
                };
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
        if(DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV))
        {
            try
            {
                final FocusRect lF = focusAreas;
                Handler handler = new Handler();

                Runnable r = new Runnable() {
                    public void run() {
                    //    ((CameraHolderApi1)cameraHolder).baseSetParamTest();

                       // cameraParameters.put("focus-areas", "(" + lF.left + "," + lF.top + "," + lF.right + "," + lF.bottom + ",1000)");
                       // cameraHolder.SetCameraParameters(cameraParameters);
                        cameraParameters.put("touch-aec","on");
                        cameraParameters.put("raw-size","4208x3120");
                        cameraParameters.put("touch-index-af", lF.x + "," + lF.y);
                        cameraHolder.SetCameraParameters(cameraParameters);
                    }
                };
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

    public boolean isMTK()
    {
           return DeviceUtils.IS(DeviceUtils.Devices.Alcatel_985n) || cameraHolder.DeviceFrameWork == CameraHolderApi1.Frameworks.MTK || DeviceUtils.IS(DeviceUtils.Devices.SonyC5_MTK);


    }

    public float getMTKShutterSpeed()
    {
        if(cameraParameters.containsKey("eng-capture-shutter-speed")) {
            if (Float.parseFloat((cameraParameters.get("eng-capture-shutter-speed"))) == 0) {
                return 0.0f;
            } else
                return Float.parseFloat((cameraParameters.get("eng-capture-shutter-speed"))) / 1000000;
        }
        else if(cameraParameters.containsKey("cap-ss"))
        {
            if (Float.parseFloat((cameraParameters.get("cap-ss"))) == 0) {
                return 0.0f;
            } else
                return Float.parseFloat((cameraParameters.get("cap-ss"))) / 1000000;
        }
        else
            return 0.0f;
    }

    public int getMTKISO()
    {
        if(cameraParameters.containsKey("eng-capture-sensor-gain")) {
            if (Integer.parseInt(cameraParameters.get("eng-capture-sensor-gain")) == 0) {
                return 0;
            }
            return Integer.parseInt(cameraParameters.get("eng-capture-sensor-gain")) / 256 * 100;
        }
        else if(cameraParameters.containsKey("cap-sr-g"))
        {
            if (Integer.parseInt(cameraParameters.get("cap-sr-g")) == 0) {
                return 0;
            }
            return Integer.parseInt(cameraParameters.get("cap-sr-g")) / 256 * 100;
        }
        else
            return 0;
    }

    public float getQCShutterSpeed()
    {

        if(cameraParameters.containsKey("cur-exposure-time"))
        {
            float a= Float.parseFloat(cameraParameters.get("cur-exposure-time")) * 1000;
            return a / 1000000;

        }
        else
        return 0.0f;

    }

    @Override
    public void SetEVBracket(String ev)
    {
        if (cameraParameters.containsKey("lg-ev-ctrl"))
            cameraParameters.put("lg-ev-ctrl",ev);
        else
            cameraParameters.put("exposure-compensation",ev);
        SetParametersToCamera(cameraParameters);
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
            cameraHolder.SetOrientation(orientation);
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
            cameraHolder.SetCameraRotation(0);
        else
            cameraHolder.SetCameraRotation(180);
    }
    @Override
    public void LockExposureAndWhiteBalance(boolean value)
    {
        isExposureAndWBLocked = value;
        if (ExposureLock != null && ExposureLock.IsSupported())
            ExposureLock.SetValue(value + "", false);
        SetParametersToCamera(cameraParameters);
    }

    public void initMTKSHit()    {


        cameraParameters.put("afeng_raw_dump_flag", "1");
        cameraParameters.put("isp-mode", "1");
        cameraParameters.put("rawsave-mode", "2");
        cameraParameters.put("rawfname", "/mnt/sdcard/DCIM/FreeDCam/mtk_."+StringUtils.FileEnding.BAYER);
        cameraParameters.put("zsd-mode", "on");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Logger.e(TAG,e.getMessage());
        }
    }

    /*public void setString(String param, String value)
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
    }*/

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
            return cameraParameters.get("exposure-time");
        }
        else
            return "non";

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
        cameraParameters.put("rawfname", "/mnt/sdcard/DCIM/test."+StringUtils.FileEnding.BAYER);
    }


    public void SetZTESlowShutter()
    {
        cameraParameters.put("slow_shutter", "-1");
        SetParametersToCamera(cameraParameters);
    }

    public void Set_RAWFNAME(String filepath)
    {
        cameraParameters.put("rawfname", filepath);
        SetParametersToCamera(cameraParameters);
    }

    public void SetLGCamera()
    {
        if (true)
            cameraParameters.put("lge-camera", "1");
        else
            cameraParameters.put("lge-camera", "0");
        SetParametersToCamera(cameraParameters);
    }

    public void SetDualRecorder()
    {
        if (false)
            cameraParameters.put("dual-recorder", "1");
        else
            cameraParameters.put("dual-recorder", "0");
        SetParametersToCamera(cameraParameters);
    }


}
