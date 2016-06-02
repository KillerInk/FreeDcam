package com.freedcam.apis.camera1.camera.parameters;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.FocusRect;
import com.freedcam.apis.basecamera.camera.modules.I_ModuleEvent;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.LocationParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.ModuleParameters;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.FocusHandler;
import com.freedcam.apis.camera1.camera.parameters.device.AbstractDevice;
import com.freedcam.apis.camera1.camera.parameters.device.Alcatel_985n;
import com.freedcam.apis.camera1.camera.parameters.device.Alcatel_Idol3;
import com.freedcam.apis.camera1.camera.parameters.device.Alcatel_Idol3_small;
import com.freedcam.apis.camera1.camera.parameters.device.Aquaris_E5;
import com.freedcam.apis.camera1.camera.parameters.device.DefaultDevice;
import com.freedcam.apis.camera1.camera.parameters.device.ForwardArt_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.GioneE7;
import com.freedcam.apis.camera1.camera.parameters.device.HTC_Desire500;
import com.freedcam.apis.camera1.camera.parameters.device.HTC_M8;
import com.freedcam.apis.camera1.camera.parameters.device.HTC_M9;
import com.freedcam.apis.camera1.camera.parameters.device.HTC_One_A9;
import com.freedcam.apis.camera1.camera.parameters.device.HTC_One_E8;
import com.freedcam.apis.camera1.camera.parameters.device.HTC_One_SV;
import com.freedcam.apis.camera1.camera.parameters.device.HTC_One_XL;
import com.freedcam.apis.camera1.camera.parameters.device.Huawei_GX8;
import com.freedcam.apis.camera1.camera.parameters.device.Huawei_Honor5x;
import com.freedcam.apis.camera1.camera.parameters.device.I_Mobile_IStyleQ6;
import com.freedcam.apis.camera1.camera.parameters.device.Jiayu_S3;
import com.freedcam.apis.camera1.camera.parameters.device.LG_G2;
import com.freedcam.apis.camera1.camera.parameters.device.LG_G3;
import com.freedcam.apis.camera1.camera.parameters.device.LG_G4;
import com.freedcam.apis.camera1.camera.parameters.device.Lenovo_K4Note_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.Lenovo_K50_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.Lenovo_K910;
import com.freedcam.apis.camera1.camera.parameters.device.Lenovo_K920;
import com.freedcam.apis.camera1.camera.parameters.device.Lenovo_VibeP1;
import com.freedcam.apis.camera1.camera.parameters.device.Meizu_M2_Note_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.Meizu_MX4_5_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.Moto_MSM8982_8994;
import com.freedcam.apis.camera1.camera.parameters.device.OnePlusOne;
import com.freedcam.apis.camera1.camera.parameters.device.OnePlusTwo;
import com.freedcam.apis.camera1.camera.parameters.device.Retro_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.Sony_C5;
import com.freedcam.apis.camera1.camera.parameters.device.Sony_M4;
import com.freedcam.apis.camera1.camera.parameters.device.Sony_M5_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.Sony_XperiaL;
import com.freedcam.apis.camera1.camera.parameters.device.THL5000_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.Vivo_Xplay3s;
import com.freedcam.apis.camera1.camera.parameters.device.Xiaomi_Mi3W;
import com.freedcam.apis.camera1.camera.parameters.device.Xiaomi_Mi4W;
import com.freedcam.apis.camera1.camera.parameters.device.Xiaomi_Mi4c;
import com.freedcam.apis.camera1.camera.parameters.device.Xiaomi_Mi_Note_Pro;
import com.freedcam.apis.camera1.camera.parameters.device.Xiaomi_Redmi_Note;
import com.freedcam.apis.camera1.camera.parameters.device.Xiaomi_Redmi_Note2_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.Xiaomi_Redmi_Note3_QC_MTK;
import com.freedcam.apis.camera1.camera.parameters.device.Yu_Yureka;
import com.freedcam.apis.camera1.camera.parameters.device.ZTE_ADV;
import com.freedcam.apis.camera1.camera.parameters.device.ZTE_ADV_IMX214;
import com.freedcam.apis.camera1.camera.parameters.device.ZTE_ADV_IMX234;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_MTK;
import com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_QcomM;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseManualParameter;
import com.freedcam.apis.camera1.camera.parameters.manual.BurstManualParam;
import com.freedcam.apis.camera1.camera.parameters.manual.CCTManualParameter;
import com.freedcam.apis.camera1.camera.parameters.manual.ExposureManualParameter;
import com.freedcam.apis.camera1.camera.parameters.manual.FXManualParameter;
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
import com.freedcam.apis.camera1.camera.parameters.modes.StackModeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.VideoProfilesG3Parameter;
import com.freedcam.apis.camera1.camera.parameters.modes.VideoProfilesParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.VideoStabilizationParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.VirtualLensFilter;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by troop on 17.08.2014.
 */
public class CamParametersHandler extends AbstractParameterHandler
{

    private final String TAG = CamParametersHandler.class.getSimpleName();

    private Camera.Parameters cameraParameters;
    public Camera.Parameters getParameters(){return cameraParameters;}
    public CameraHolderApi1 cameraHolder;
    public BaseModeParameter DualMode;
    private CameraUiWrapper cameraUiWrapper;
    public AbstractDevice Device;

    public CamParametersHandler(CameraUiWrapper cameraUiWrapper, Handler uiHandler, Context context,AppSettingsManager appSettingsManager)
    {
        super(cameraUiWrapper.cameraHolder, uiHandler,context,appSettingsManager);
        cameraHolder = cameraUiWrapper.cameraHolder;
        this.cameraUiWrapper = cameraUiWrapper;
    }

    public void SetParametersToCamera(Camera.Parameters params)
    {
        Logger.d(TAG, "SetParametersToCam");
        cameraHolder.SetCameraParameters(params);
    }

    public void LoadParametersFromCamera()
    {
        cameraParameters = cameraHolder.GetCameraParameters();
        initParameters();
    }

    private void logParameters(Camera.Parameters parameters)
    {
        Logger.d(TAG, "Manufactur:" + Build.MANUFACTURER);
        Logger.d(TAG, "Model:" + Build.MODEL);
        Logger.d(TAG, "Product:" + Build.PRODUCT);
        Logger.d(TAG, "OS:" + System.getProperty("os.version"));
        String[] split = parameters.flatten().split(";");
        for(String e : split)
        {
            Logger.d(TAG,e);
        }
    }



    private void initParameters()
    {

        logParameters(cameraParameters);


        //setup first Pictureformat its needed for manual parameters to
        // register their listners there if its postprocessing parameter
        try {
            PictureFormat = new PictureFormatHandler(uiHandler,cameraParameters, cameraHolder, this);
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner((I_ModuleEvent) PictureFormat);
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


        createManualSaturation();

        createManualSharpness();


        try
        {
            loadDeviceSpecificStuff();
        } catch (Exception e) {
            Logger.exception(e);
        }


        try {
            WhiteBalanceMode = new BaseModeParameter(uiHandler,cameraParameters, cameraHolder, "whitebalance", "whitebalance-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            CCT = new CCTManualParameter(cameraParameters,this);
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
            PreviewSize = new PreviewSizeParameter(uiHandler,cameraParameters, cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewFPS = new PreviewFpsParameter(uiHandler, cameraParameters, "preview-frame-rate-values", cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewFormat = new PreviewFormatParameter(uiHandler,cameraParameters, cameraHolder);
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
            if(cameraParameters.get("3dnr-mode")!=null) {
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
            if(cameraParameters.get("sony-vs")!=null)
                DigitalImageStabilization = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"sony-vs","sony-vs-values");
            else if (cameraParameters.get("dis")!=null)
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
            Focuspeak = new FocusPeakModeParameter(uiHandler,cameraHolder,cameraUiWrapper.focusPeakProcessorAp1);
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

        imageStackMode = new StackModeParameter(uiHandler,cameraParameters,cameraHolder,"","");

        opcode = new OpCodeParameter(uiHandler);

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
            if (cameraParameters.get("exposure-mode-values")!= null)
                ExposureMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"exposure","exposure-mode-values");
            else if (cameraParameters.get("auto-exposure-values")!= null)
                ExposureMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"auto-exposure","auto-exposure-values");
            else if(cameraParameters.get("sony-metering-mode-values")!= null)
                ExposureMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"sony-metering-mode","sony-metering-mode-values");
            else if(cameraParameters.get("exposure-meter-values")!= null)
                ExposureMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"exposure-meter","exposure-meter-values");
            if (ExposureMode != null)
                ExposureMode.addEventListner(((FocusHandler) cameraHolder.Focus).aeModeListner);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createIsoMode() {
        try {
            if (cameraParameters.get("iso-mode-values")!= null)
                IsoMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"iso","iso-mode-values");
            else if (cameraParameters.get("iso-values")!= null)
                IsoMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"iso","iso-values");
            else if (cameraParameters.get("iso-speed-values")!= null)
                IsoMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"iso-speed","iso-speed-values");
            else if (cameraParameters.get("sony-iso-values")!= null)
                IsoMode = new BaseModeParameter(uiHandler, cameraParameters,cameraHolder,"sony-iso","sony-iso-values");
            else if (cameraParameters.get("lg-iso-values")!= null)
                IsoMode = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"iso","lg-iso-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualBrightness() {
        try {
            if (cameraParameters.get("brightness")!= null && cameraParameters.get("brightness-values")!= null)
            {
                cameraParameters.set("brightness-max", "3");
                cameraParameters.set("brightness-min", "0");
                ManualBrightness =  new BaseManualParameter(cameraParameters,"brightness", "brightness-max", "brightness-min",this,1);

            }
            else if (cameraParameters.get("brightness")!= null && cameraParameters.get("brightness-values")== null)
            {
                //p920hack
                if (cameraParameters.get("max-brightness")!= null && cameraParameters.get("brightness-max")!= null)
                {
                    cameraParameters.set("max-brightness", "100");
                    cameraParameters.set("min-brightness", "0");
                }
                if (cameraParameters.get("brightness-max")!= null)
                {
                    ManualBrightness = new BaseManualParameter(cameraParameters, "brightness", "brightness-max", "brightness-min", this, 1);
                }
                else if(DeviceUtils.IS(DeviceUtils.Devices.p8lite))
                    ManualBrightness = new BaseManualParameter(cameraParameters, "brightness", "max-brightness", "min-brightness", this, 50);
                else if (cameraParameters.get("max-brightness")!= null)
                    ManualBrightness = new BaseManualParameter(cameraParameters, "brightness", "max-brightness", "min-brightness", this, 1);

            }
            else if (cameraParameters.get("luma-adaptation")!= null)
                ManualBrightness =  new BaseManualParameter(cameraParameters,"luma-adaptation","max-brightness","min-brightness",this,1);

            if (ManualBrightness != null ) {
                PictureFormat.addEventListner(((BaseManualParameter) ManualBrightness).GetPicFormatListner());
                cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) ManualBrightness).GetModuleListner());
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualContrast()
    {
        try {
            if (cameraParameters.get("contrast")!= null && cameraParameters.get("contrast-values")!= null)
            {
                cameraParameters.set("contrast-max", "3");
                cameraParameters.set("contrast-min", "0");
                ManualContrast =  new BaseManualParameter(cameraParameters,"contrast", "contrast-max", "contrast-min",this,1);

            }
            else if (cameraParameters.get("contrast")!= null && cameraParameters.get("contrast-values")== null)
            {
                //p920 hack
                if (cameraParameters.get("max-contrast")!= null && cameraParameters.get("contrast-max")!= null) {
                    cameraParameters.set("max-contrast", "100");
                    cameraParameters.set("min-contrast", "0");
                }
                if (cameraParameters.get("contrast-max")!= null)
                    ManualContrast =  new BaseManualParameter(cameraParameters,"contrast", "contrast-max", "contrast-min",this,1);
                else if(DeviceUtils.IS(DeviceUtils.Devices.p8lite))
                    ManualContrast =  new BaseManualParameter(cameraParameters,"contrast", "max-contrast", "min-contrast",this,25);
                else if (cameraParameters.get("max-contrast")!= null)
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
            if (cameraParameters.get("video-hfr-values")!= null)
            {
                String hfrvals = cameraParameters.get("video-hfr-values");
                if (!hfrvals.equals("off"))
                {
                    if (hfrvals.equals(""))
                        cameraParameters.set("video-hfr-values", "off,60,120");
                }
            }
            VideoHighFramerateVideo = new BaseModeParameter(uiHandler, cameraParameters, cameraHolder, "video-hfr", "video-hfr-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createVideoHDR() {
        try {
            if (cameraParameters.get("video-hdr")!= null)
                VideoHDR = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"video-hdr", "video-hdr-values");
            else if (cameraParameters.get("sony-video-hdr")!= null)
                VideoHDR = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"sony-video-hdr","sony-video-hdr-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createZeroShutterLag() {
        try {
            if (cameraParameters.get("zsl")!= null)
                ZSL = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"zsl","zsl-values");
            else if (cameraParameters.get("mode")!= null)
                ZSL = new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"mode","mode-values");
            else if (cameraParameters.get("zsd-mode")!= null)
                ZSL =new BaseModeParameter(uiHandler,cameraParameters,cameraHolder,"zsd-mode", "zsd-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualSharpness() {
        try {

            if (cameraParameters.get("edge")!= null && cameraParameters.get("edge-values")!= null)
            {
                cameraParameters.set("edge-max", "3");
                cameraParameters.set("edge-min", "0");
                ManualSharpness =  new BaseManualParameter(cameraParameters,"edge", "edge-max", "edge-min",this,1);

            }
            else if (cameraParameters.get("sharpness")!= null && cameraParameters.get("sharpness-values")== null)
            {
                if (cameraParameters.get("max-sharpness")!= null && cameraParameters.get("sharpness-max")!= null) {
                    cameraParameters.set("max-sharpness", "100");
                    cameraParameters.set("min-sharpness", "0");
                }
                int step = 1;
                if (cameraParameters.get("sharpness-step")!= null)
                    step = Integer.parseInt(cameraParameters.get("sharpness-step"));

                if (cameraParameters.get("sharpness-max")!= null)
                {
                    ManualSharpness = new BaseManualParameter(cameraParameters, "sharpness", "sharpness-max", "sharpness-min", this,step);
                }
                else if (cameraParameters.get("max-sharpness")!= null)
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
            if (cameraParameters.get("saturation")!= null && cameraParameters.get("saturation-values")!= null)
            {
                cameraParameters.set("saturation-max", "3");
                cameraParameters.set("saturation-min", "0");
                ManualSaturation =  new BaseManualParameter(cameraParameters,"saturation", "saturation-max", "saturation-min",this,1);

            }
            else if (cameraParameters.get("saturation")!= null && cameraParameters.get("saturation-values")== null)
            {
                //p920 hack
                if (cameraParameters.get("max-saturation")!= null && cameraParameters.get("saturation-max")!= null) {
                    cameraParameters.set("max-saturation", "100");
                    cameraParameters.set("min-saturation", "0");
                }
                //check first max after evo 3d has both but max infront is empty
                if (cameraParameters.get("saturation-max")!= null)
                    ManualSaturation = new BaseManualParameter(cameraParameters, "saturation", "saturation-max", "saturation-min", this,1);
                else if (cameraParameters.get("max-saturation")!= null)
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
            ManualExposure = new ExposureManualParameter(cameraParameters, this,1);
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
                        cameraParameters.set("touch-aec","on");
                        cameraParameters.set("selectable-zone-af","spot-metering");
                        cameraParameters.set("raw-size","4208x3120");
                        cameraParameters.set("touch-index-aec", lF.x + "," + lF.y);
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
    public void SetFocusAREA(final FocusRect focusAreas, FocusRect meteringAreas)
    {
        if(DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV))
        {
            try
            {
                Handler handler = new Handler();

                Runnable r = new Runnable() {
                    public void run() {
                        cameraParameters.set("touch-aec","on");
                        cameraParameters.set("raw-size","4208x3120");
                        cameraParameters.set("touch-index-af", focusAreas.x + "," + focusAreas.y);
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
            final Camera.Area a = new Camera.Area(new Rect(focusAreas.left,focusAreas.top,focusAreas.right,focusAreas.bottom),1000);
            ArrayList<Camera.Area> ar = new ArrayList<>();
            ar.add(a);
            cameraParameters.setFocusAreas(ar);
            SetParametersToCamera(cameraParameters);
        }
    }

    public boolean isMTK()
    {
           return DeviceUtils.IS(DeviceUtils.Devices.Alcatel_985n) || cameraHolder.DeviceFrameWork == CameraHolderApi1.Frameworks.MTK || DeviceUtils.IS(DeviceUtils.Devices.SonyC5_MTK);


    }

    public float getMTKShutterSpeed()
    {
        if(cameraParameters.get("eng-capture-shutter-speed")!= null) {
            if (Float.parseFloat((cameraHolder.GetParamsDirect("eng-capture-shutter-speed"))) == 0) {
                return 0.0f;
            } else
                return Float.parseFloat((cameraParameters.get("eng-capture-shutter-speed"))) / 1000000;
        }
        else if(cameraParameters.get("cap-ss")!= null)
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
        if(cameraParameters.get("eng-capture-sensor-gain")!= null) {
            if (Integer.parseInt(cameraHolder.GetParamsDirect("eng-capture-sensor-gain")) == 0) {
                return 0;
            }
            return Integer.parseInt(cameraHolder.GetParamsDirect("eng-capture-sensor-gain")) / 256 * 100;
        }
        else if(cameraParameters.get("cap-sr-g")!= null)
        {
            if (Integer.parseInt(cameraHolder.GetParamsDirect("cap-sr-g")) == 0) {
                return 0;
            }
            return Integer.parseInt(cameraHolder.GetParamsDirect("cap-sr-g")) / 256 * 100;
        }
        else
            return 0;
    }

    public float getQCISO()
    {

        if(cameraParameters.get("cur-exposure-time")!= null)
        {
            float a= Float.parseFloat(cameraHolder.GetParamsDirect("cur-exposure-time")) * 1000;
            return a / 1000000;

        }
        else
            return 0.0f;

    }

    public float getQCShutterSpeed()
    {

        if(cameraParameters.get("cur-exposure-time")!= null)
        {
            float a= Float.parseFloat(cameraHolder.GetParamsDirect("cur-exposure-time")) * 1000;
            return a / 1000000;

        }
        else
        return 0.0f;

    }

    @Override
    public void SetEVBracket(String ev)
    {
        if (cameraParameters.get("lg-ev-ctrl")!= null)
            cameraParameters.set("lg-ev-ctrl",ev);
        else
            cameraParameters.set("exposure-compensation",ev);
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


        cameraParameters.set("afeng_raw_dump_flag", "1");
        cameraParameters.set("isp-mode", "1");
        cameraParameters.set("rawsave-mode", "2");
        cameraParameters.set("rawfname", StringUtils.GetInternalSDCARD()+"/DCIM/FreeDCam/mtk_."+StringUtils.FileEnding.BAYER);
        cameraParameters.set("zsd-mode", "on");
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
        if (cameraParameters.get("f-number")!= null) {
            final String fnum = cameraParameters.get("f-number");
            return Float.parseFloat(fnum);
        }
        else
            return 0;
    }

    public float GetFocal()
    {
        if (cameraParameters.get("focal-length")!= null) {
            final String focal = cameraParameters.get("focal-length");
            return Float.parseFloat(focal);
        }
        else
            return 0;
    }

    public String ExposureTime()
    {
        if (cameraParameters.get("exposure-time")!= null) {
            return cameraParameters.get("exposure-time");
        }
        else
            return "non";

    }

    public void FPSRangeLock (int min,int max){
        String mMin =String.valueOf(min*1000);
        String mMax =String.valueOf(max*1000);
        cameraParameters.set("preview-fps-range",mMin+","+mMax);
        cameraParameters.set("preview-frame-rate", mMax);
        SetParametersToCamera(cameraParameters);

    }

    private void Mediatek()
    {
        // cameraParameters.put("zsd-mode","on");
        //cameraParameters.put("camera-mode","0");
        cameraParameters.set("afeng_raw_dump_flag", "1");
        cameraParameters.set("rawsave-mode", "2");
        cameraParameters.set("isp-mode", "1");
        cameraParameters.set("rawfname", StringUtils.GetInternalSDCARD()+"/DCIM/test."+StringUtils.FileEnding.BAYER);
    }


    public void SetZTESlowShutter()
    {
        cameraParameters.set("slow_shutter", "-1");
        SetParametersToCamera(cameraParameters);
    }

    public void Set_RAWFNAME(String filepath)
    {
        cameraParameters.set("rawfname", filepath);
        SetParametersToCamera(cameraParameters);
    }

    public void SetLGCamera()
    {
        cameraParameters.set("lge-camera", "1");
        SetParametersToCamera(cameraParameters);
    }

    public void SetDualRecorder()
    {
        if (false)
            cameraParameters.set("dual-recorder", "1");
        else
            cameraParameters.set("dual-recorder", "0");
        SetParametersToCamera(cameraParameters);
    }


    private void loadDeviceSpecificStuff()
    {
        switch (DeviceUtils.DEVICE())
        {
            case Alcatel_985n:
                Device = new Alcatel_985n(uiHandler,cameraParameters,cameraUiWrapper);
                break;
//                case p8:
//                    break;
//                case p8lite:
//                    break;
//                case honor6:
//                    break;
            case Aquaris_E5:
                Device = new Aquaris_E5(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Alcatel_Idol3:
                Device = new Alcatel_Idol3(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Alcatel_Idol3_small:
                Device = new Alcatel_Idol3_small(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case GioneE7:
                Device = new GioneE7(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case ForwardArt_MTK:
                Device = new ForwardArt_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Htc_M8:
                Device = new HTC_M8(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Htc_M9:
                Device = new HTC_M9(uiHandler, cameraParameters,cameraUiWrapper);
                break;
            case Htc_One_Sv:
                Device = new HTC_One_SV(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Htc_One_Xl:
                Device = new HTC_One_XL(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case HTC_OneA9:
                Device = new HTC_One_A9(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case HTC_OneE8:
                Device = new HTC_One_E8(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case HTC_Desire500:
                Device = new HTC_Desire500(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Huawei_GX8:
                Device = new Huawei_GX8(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Huawei_HONOR5x:
                Device = new Huawei_Honor5x(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case I_Mobile_I_StyleQ6:
                Device = new I_Mobile_IStyleQ6(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Jiayu_S3:
                Device = new Jiayu_S3(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case LenovoK910:
                Device = new Lenovo_K910(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case LenovoK920:
                Device = new Lenovo_K920(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Lenovo_K4Note_MTK:
                Device = new Lenovo_K4Note_MTK(uiHandler, cameraParameters,cameraUiWrapper);
                break;
            case Lenovo_K50_MTK:
                Device = new Lenovo_K50_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Lenovo_VibeP1:
                Device = new Lenovo_VibeP1(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case LG_G2:
                Device = new LG_G2(uiHandler,cameraParameters,cameraUiWrapper);
                break;
//                case LG_G2pro:
//                    break;
            case LG_G3:
                Device = new LG_G3(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case LG_G4:
                Device = new LG_G4(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case MeizuMX4_MTK:
            case MeizuMX5_MTK:
                Device = new Meizu_MX4_5_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Meizu_m2Note_MTK:
                Device = new Meizu_M2_Note_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                break;
//                case Moto_MSM8974:
//                    break;
            case Moto_MSM8982_8994:
                Device = new Moto_MSM8982_8994(uiHandler,cameraParameters,cameraUiWrapper);
                break;
//                case MotoG3:
//                    break;
//                case MotoG_Turbo:
//                    break;
//                case Nexus4:
//                    break;
            case OnePlusOne:
                Device = new OnePlusOne(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case OnePlusTwo:
                Device = new OnePlusTwo(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Xiaomi_RedmiNote:
                Device = new Xiaomi_Redmi_Note(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Xiaomi_RedmiNote2_MTK:
                Device = new Xiaomi_Redmi_Note2_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Retro_MTK:
                Device = new Retro_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                break;
//                case Samsung_S6_edge:
//                    break;
//                case Samsung_S6_edge_plus:
//                    break;
//                case SonyADV:
//                    break;
            case SonyM5_MTK:
                Device = new Sony_M5_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case SonyM4_QC:
                Device = new Sony_M4(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case SonyC5_MTK:
                Device = new Sony_C5(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Sony_XperiaL:
                Device = new Sony_XperiaL(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case THL5000_MTK:
                Device = new THL5000_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Vivo_Xplay3s:
                Device = new Vivo_Xplay3s(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case XiaomiMI3W:
                Device = new Xiaomi_Mi3W(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case XiaomiMI4W:
                Device = new Xiaomi_Mi4W(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case XiaomiMI4C:
                Device = new Xiaomi_Mi4c(uiHandler,cameraParameters,cameraUiWrapper);
                break;
//                case XiaomiMI5:
//                    break;
            case XiaomiMI_Note_Pro:
                Device = new Xiaomi_Mi_Note_Pro(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Xiaomi_Redmi_Note3:
                Device = new Xiaomi_Redmi_Note3_QC_MTK(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case Yu_Yureka:
                Device = new Yu_Yureka(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case ZTE_ADV:
                Device = new ZTE_ADV(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case ZTEADVIMX214:
                Device = new ZTE_ADV_IMX214(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            case ZTEADV234:
                Device = new ZTE_ADV_IMX234(uiHandler,cameraParameters,cameraUiWrapper);
                break;
            default:
                Device = new DefaultDevice(uiHandler,cameraParameters,cameraUiWrapper);
                loadOldLookup();
                break;
        }
        if (Device != null)
            setDeviceParameters(Device);
    }

    private void loadOldLookup()
    {
        Logger.d(TAG, "Use ShutterClassHandler and ISOManualParameter");
        ManualShutter = ShutterClassHandler.getShutterClass(cameraParameters, this, cameraHolder);
        ManualIso = new ISOManualParameter(cameraParameters, this);
    }

    private void setDeviceParameters(AbstractDevice device)
    {
        AbstractManualParameter shutter = device.getExposureTimeParameter();
        if (shutter != null)
            ManualShutter = shutter;
        AbstractManualParameter mf = device.getManualFocusParameter();
        if (mf != null)
            ManualFocus = mf;
        AbstractManualParameter iso = device.getIsoParameter();
        if (iso != null)
            ManualIso = iso;
        AbstractManualParameter cct =  device.getCCTParameter();
        if (cct !=  null)
            CCT =cct;
        AbstractModeParameter video = device.getVideoProfileMode();
        if (video != null)
            VideoProfiles = video;
        if (device.IsDngSupported())
        {
            matrixChooser = new MatrixChooserParameter(uiHandler);
        }
    }

}
