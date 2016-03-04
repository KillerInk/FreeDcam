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
import com.troop.freedcam.camera.parameters.manual.ShutterClassHandler;
import com.troop.freedcam.camera.parameters.manual.SkintoneManualPrameter;
import com.troop.freedcam.camera.parameters.manual.ZoomManualParameter;
import com.troop.freedcam.camera.parameters.modes.BaseModeParameter;
import com.troop.freedcam.camera.parameters.modes.CDS_Mode_Parameter;
import com.troop.freedcam.camera.parameters.modes.ExposureModeParameter;
import com.troop.freedcam.camera.parameters.modes.FlashModeParameter;
import com.troop.freedcam.camera.parameters.modes.FocusModeParameter;
import com.troop.freedcam.camera.parameters.modes.FocusPeakModeParameter;
import com.troop.freedcam.camera.parameters.modes.HDRModeParameter;
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
        baseCameraHolder = (BaseCameraHolder) cameraHolder;
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
        Log.d(TAG, "Manufactur:" + Build.MANUFACTURER);
        Log.d(TAG, "Model:" + Build.MODEL);
        Log.d(TAG, "Product:" + Build.PRODUCT);
        Log.d(TAG, "OS:"+ System.getProperty("os.version"));
        for(Map.Entry e : parameters.entrySet())
        {
            Log.d(TAG, e.getKey() + "=" + e.getValue());
        }
    }


    private void initParameters()
    {
        logParameters(cameraParameters);


        //setup first Pictureformat its needed for manual parameters to
        // register their listners there if its postprocessing parameter
        try {
            PictureFormat = new PictureFormatHandler(uiHandler,cameraParameters, baseCameraHolder);
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner((PictureFormatHandler)PictureFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FocusMode = new FocusModeParameter(uiHandler,cameraParameters, baseCameraHolder,"focus-mode","focus-mode-values");
            FocusMode.addEventListner(((FocusHandler) cameraHolder.Focus).focusModeListner);
        } catch (Exception e) {
            e.printStackTrace();
        }

        locationParameter = new LocationParameter(uiHandler, appSettingsManager, cameraHolder);

        try {
            ManualBrightness = new BrightnessManualParameter(cameraParameters, "","","", this);
            PictureFormat.addEventListner(((BaseManualParameter)ManualBrightness).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter)ManualBrightness).GetModuleListner());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ManualContrast = new ContrastManualParameter(cameraParameters, "", "", "",this);
            PictureFormat.addEventListner(((BaseManualParameter)ManualContrast).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) ManualContrast).GetModuleListner());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ManualConvergence = new ConvergenceManualParameter(cameraParameters, "manual-convergence", "supported-manual-convergence-max", "supported-manual-convergence-min", this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ManualExposure = new ExposureManualParameter(cameraParameters,"exposure-compensation","max-exposure-compensation","min-exposure-compensation", this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (DeviceUtils.IS(Devices.LG_G4) || (DeviceUtils.IS(Devices.LG_G3) && (Build.VERSION.SDK_INT < 21 || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) || DeviceUtils.IS(Devices.LG_G2))
                ManualFocus = new FocusManualParameterLG(cameraParameters,"","","", cameraHolder, this);
            else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
                ManualFocus = new FocusManualParameterHTC(cameraParameters,"","","", cameraHolder,this);
            else
                ManualFocus = new FocusManualParameter(cameraParameters,"","","", cameraHolder, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ManualSaturation = new SaturationManualParameter(cameraParameters,"","","", this);
            PictureFormat.addEventListner(((BaseManualParameter)ManualSaturation).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) ManualSaturation).GetModuleListner());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ManualSharpness = new SharpnessManualParameter(cameraParameters, "", "", "", this);
            PictureFormat.addEventListner(((BaseManualParameter)ManualSharpness).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) ManualSharpness).GetModuleListner());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (DeviceUtils.IS(Devices.LG_G4))
                aeHandlerG4 = new LG_G4AeHandler(cameraParameters,baseCameraHolder,this);
            else
                ManualShutter = ShutterClassHandler.getShutterClass(cameraParameters, this, cameraHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            ISOManual = new ISOManualParameter(cameraParameters, "", "", "",baseCameraHolder, this);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            WhiteBalanceMode = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "whitebalance", "whitebalance-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            CCT = new CCTManualParameter(cameraParameters,"","","", this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //PictureFormat.addEventListner(((BaseManualParameter)CCT).GetPicFormatListner());
        //cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) CCT).GetModuleListner());

        try {
            Skintone = new SkintoneManualPrameter(cameraParameters,"","","",this);
            PictureFormat.addEventListner(((BaseManualParameter)Skintone).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) Skintone).GetModuleListner());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FX = new FXManualParameter(cameraParameters,"","","", this);
            PictureFormat.addEventListner(((BaseManualParameter)FX).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) FX).GetModuleListner());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Burst = new BurstManualParam(cameraParameters,"","","",this);
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) Burst).GetModuleListner());
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Zoom = new ZoomManualParameter(cameraParameters,"", "", "", this);
            PictureFormat.addEventListner(((BaseManualParameter)Zoom).GetPicFormatListner());
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(((BaseManualParameter) Zoom).GetModuleListner());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ColorMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "effect", "effect-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ExposureMode = new ExposureModeParameter(uiHandler,cameraParameters,baseCameraHolder,"","");
            ExposureMode.addEventListner(((FocusHandler) cameraHolder.Focus).aeModeListner);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FlashMode = new FlashModeParameter(uiHandler,cameraParameters,baseCameraHolder,"flash-mode","flash-mode-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            IsoMode = new IsoModeParameter(uiHandler,cameraParameters,baseCameraHolder,"","", cameraHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            AntiBandingMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "antibanding", "antibanding-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            PictureSize = new PictureSizeParameter(uiHandler,cameraParameters,baseCameraHolder, "picture-size", "picture-size-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JpegQuality = new JpegQualityParameter(uiHandler,cameraParameters, baseCameraHolder, "jpeg-quality", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //AE_Bracket = new AE_Bracket_HdrModeParameter(uiHandler,cameraParameters,baseCameraHolder, "ae-bracket-hdr", "ae-bracket-hdr-values");

        try {
            ImagePostProcessing = new ImagePostProcessingParameter(uiHandler,cameraParameters,baseCameraHolder, "ipp", "ipp-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            PreviewSize = new PreviewSizeParameter(uiHandler,cameraParameters, baseCameraHolder, "preview-size", "preview-size-values", cameraHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            PreviewFPS = new PreviewFpsParameter(uiHandler, cameraParameters, "preview-frame-rate", "preview-frame-rate-values", (BaseCameraHolder)cameraHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            PreviewFormat = new PreviewFormatParameter(uiHandler,cameraParameters, baseCameraHolder, "preview-format", "preview-format-values", cameraHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SceneMode =  new SceneModeParameter(uiHandler, cameraParameters, baseCameraHolder, "scene-mode","scene-mode-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            RedEye = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "redeye-reduction", "redeye-reduction-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            LensShade = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "lensshade", "lensshade-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            VideoStabilization = new VideoStabilizationParameter(uiHandler,cameraParameters,baseCameraHolder,"video-stabilization","");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (cameraParameters.containsKey("zsl"))
                ZSL = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"zsl","zsl-values");
            else if (cameraParameters.containsKey("mode"))
                ZSL = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"mode","mode-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SceneDetect = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "scene-detect", "scene-detect-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Denoise = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "denoise", "denoise-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(cameraParameters.containsKey("sony-vs"))
                DigitalImageStabilization = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"sony-vs","sony-vs-values");
            else if (cameraParameters.containsKey("dis"))
                DigitalImageStabilization = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"dis","dis-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            MemoryColorEnhancement = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "mce", "mce-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SkinToneEnhancment = new BaseModeParameter(uiHandler,cameraParameters, baseCameraHolder, "skinToneEnhancement", "skinToneEnhancement-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            NightMode = new NightModeParameter(uiHandler,cameraParameters, baseCameraHolder,"","", cameraUiWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            NonZslManualMode = new NonZslManualModeParameter(uiHandler,cameraParameters, baseCameraHolder, "non-zsl-manual-mode", "", cameraHolder);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*try {
            Histogram = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "histogram", "histogram-values");
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            CameraMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "camera-mode", "camera-mode-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            DualMode = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder, "dual_mode", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*try {
            ExposureLock = new ExposureLockParameter(uiHandler,cameraParameters, baseCameraHolder, "","");
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*try {
            VideoSize = new VideoSizeParameter(uiHandler,cameraParameters,baseCameraHolder,"video-size","video-size");
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            if (cameraParameters.containsKey("video-hdr"))
                VideoHDR = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"video-hdr", "video-hdr-values");
            else if (cameraParameters.containsKey("sony-video-hdr"))
                VideoHDR = new BaseModeParameter(uiHandler,cameraParameters,baseCameraHolder,"sony-video-hdr","sony-video-hdr-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (baseCameraHolder.DeviceFrameWork == BaseCameraHolder.Frameworks.LG /*&& Build.VERSION.SDK_INT < 21*/)
                VideoProfilesG3 = new VideoProfilesG3Parameter(uiHandler,cameraParameters,baseCameraHolder,"","", cameraUiWrapper);
            else
                VideoProfiles = new VideoProfilesParameter(uiHandler,cameraParameters,baseCameraHolder,"","", cameraUiWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            CDS_Mode = new CDS_Mode_Parameter(uiHandler,cameraParameters,baseCameraHolder,"","");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //####No idea what they do, m9 specific, only thing they do is to freez the app####
        try {
            RdiMode = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "rdi-mode", "rdi-mode-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SecureMode = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "secure-mode", "secure-mode-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Temporal Noise Reduction http://nofilmschool.com/2012/03/temporal-noise-reduction-ipad-its-improvement
        try {
            TnrMode = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "tnr-mode", "tnr-mode-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            oismode = new OisParameter(uiHandler,cameraParameters,baseCameraHolder,"","");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Focuspeak = new FocusPeakModeParameter(uiHandler,baseCameraHolder,cameraUiWrapper.previewHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (cameraParameters.containsKey("video-hfr-values"))
            {
                String[] hfr_values = cameraParameters.get("video-hfr-values").split(",");
                if(hfr_values.length <= 2)
                    cameraParameters.put("video-hfr-values", "off,60,120");
            }
            VideoHighFramerateVideo = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "video-hfr", "video-hfr-values");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            SetCameraRotation();
            SetPictureOrientation(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //captureBurstExposures = new CupBurstExpModeParameter(uiHandler, cameraParameters, baseCameraHolder, "capture-burst-exposures", "", appSettingsManager);

        try {
            morphoHDR = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "morpho-hdr", "");

            morphoHHT = new BaseModeParameter(uiHandler, cameraParameters, baseCameraHolder, "morpho-hht", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV))
                LensFilter = new VirtualLensFilter(uiHandler,cameraParameters, baseCameraHolder, "", "", cameraUiWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            HDRMode = new HDRModeParameter(uiHandler,cameraParameters, baseCameraHolder,"","", cameraUiWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Module = new ModuleParameters(uiHandler, appSettingsManager, cameraUiWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            SetAppSettingsToParameters();
            SetParametersToCamera(cameraParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ParametersEventHandler.ParametersHasLoaded();

        try {
            if (((BaseCameraHolder) cameraHolder).DeviceFrameWork == BaseCameraHolder.Frameworks.MTK)
                Mediatek();
        } catch (Exception e) {
            e.printStackTrace();
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
                handler.postDelayed(r, 1);

            }
            catch (Exception ex)
            {

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
                handler.postDelayed(r, 1);
            }
            catch (Exception ex)
            {
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

    @Override
    public void SetAppSettingsToParameters() {
        super.SetAppSettingsToParameters();
        cameraUiWrapper.moduleHandler.SetModule(appSettingsManager.GetCurrentModule());
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
