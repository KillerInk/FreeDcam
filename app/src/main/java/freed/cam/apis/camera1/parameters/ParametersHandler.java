/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.apis.camera1.parameters;

import android.hardware.Camera.Parameters;
import android.os.Build;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.modes.LocationParameter;
import freed.cam.apis.basecamera.parameters.modes.ModuleParameters;
import freed.cam.apis.camera1.Camera1Fragment;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.CameraHolder.Frameworks;
import freed.cam.apis.camera1.FocusHandler;
import freed.cam.apis.camera1.parameters.device.I_Device;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.cam.apis.camera1.parameters.manual.ExposureManualParameter;
import freed.cam.apis.camera1.parameters.manual.ZoomManualParameter;
import freed.cam.apis.camera1.parameters.manual.qcom.BurstManualParam;
import freed.cam.apis.camera1.parameters.manual.zte.FXManualParameter;
import freed.cam.apis.camera1.parameters.modes.BaseModeParameter;
import freed.cam.apis.camera1.parameters.modes.CDS_Mode_Parameter;
import freed.cam.apis.camera1.parameters.modes.CupBurstExpModeParameter;
import freed.cam.apis.camera1.parameters.modes.ExposureLockParameter;
import freed.cam.apis.camera1.parameters.modes.FocusPeakModeParameter;
import freed.cam.apis.camera1.parameters.modes.HDRModeParameter;
import freed.cam.apis.camera1.parameters.modes.JpegQualityParameter;
import freed.cam.apis.camera1.parameters.modes.OisParameter;
import freed.cam.apis.camera1.parameters.modes.PictureFormatHandler;
import freed.cam.apis.camera1.parameters.modes.PictureSizeParameter;
import freed.cam.apis.camera1.parameters.modes.PreviewFormatParameter;
import freed.cam.apis.camera1.parameters.modes.PreviewFpsParameter;
import freed.cam.apis.camera1.parameters.modes.PreviewSizeParameter;
import freed.cam.apis.camera1.parameters.modes.StackModeParameter;
import freed.cam.apis.camera1.parameters.modes.VideoStabilizationParameter;
import freed.utils.AppSettingsManager;
import freed.utils.DeviceUtils.Devices;
import freed.utils.Logger;
import freed.utils.StringUtils;
import freed.utils.StringUtils.FileEnding;

/**
 * Created by troop on 17.08.2014.
 * this class handels all camera1 releated parameters.
 */
public class ParametersHandler extends AbstractParameterHandler
{

    private final String TAG = ParametersHandler.class.getSimpleName();

    private Parameters cameraParameters;
    public Parameters getParameters(){return cameraParameters;}
    private I_Device Device;

    public ParametersHandler(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
    }

    public void SetParametersToCamera(Parameters params)
    {
        Logger.d(TAG, "SetParametersToCam");
        ((CameraHolder) cameraUiWrapper.GetCameraHolder()).SetCameraParameters(params);
    }

    public void LoadParametersFromCamera()
    {
        cameraParameters = ((CameraHolder) cameraUiWrapper.GetCameraHolder()).GetCameraParameters();
        initParameters();
    }

    private void logParameters(Parameters parameters)
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


    /**
     * init and check the parameters used by camera1
     */
    private void initParameters()
    {

        logParameters(cameraParameters);


        //setup first Pictureformat its needed for manual parameters to
        // register their listners there if its postprocessing parameter
        try {
            PictureFormat = new PictureFormatHandler(cameraParameters, cameraUiWrapper, this);
            cameraUiWrapper.GetModuleHandler().addListner((ModuleChangedEvent) PictureFormat);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PictureSize = new PictureSizeParameter(cameraParameters, cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            FocusMode = new BaseModeParameter(cameraParameters, cameraUiWrapper,KEYS.FOCUS_MODE,KEYS.FOCUS_MODE_VALUES);
            FocusMode.addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).focusModeListner);
        } catch (Exception e) {
            Logger.exception(e);
        }

        locationParameter = new LocationParameter(cameraUiWrapper);

        try {
            ManualConvergence = new BaseManualParameter(cameraParameters, KEYS.MANUAL_CONVERGENCE, KEYS.SUPPORTED_MANUAL_CONVERGENCE_MAX, KEYS.SUPPORTED_MANUAL_CONVERGENCE_MIN, cameraUiWrapper,1);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createManualExposure();


        //createManualSaturation();



        try {
            WhiteBalanceMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.WHITEBALANCE, KEYS.WHITEBALANCE_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            FX = new FXManualParameter(cameraParameters, cameraUiWrapper);
            PictureFormat.addEventListner(((BaseManualParameter) FX).GetPicFormatListner());
            cameraUiWrapper.GetModuleHandler().addListner(((BaseManualParameter) FX).GetModuleListner());
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            Burst = new BurstManualParam(cameraParameters, cameraUiWrapper);
            cameraUiWrapper.GetModuleHandler().addListner(((BaseManualParameter) Burst).GetModuleListner());
        } catch (Exception e) {
            Logger.exception(e);
        }


        try {
            Zoom = new ZoomManualParameter(cameraParameters, cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            ColorMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.COLOR_EFFECT, KEYS.COLOR_EFFECT_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createExposureMode();

        try {
            FlashMode = new BaseModeParameter(cameraParameters, cameraUiWrapper,KEYS.FLASH_MODE,KEYS.FLASH_MODE_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createIsoMode();

        try {
            AntiBandingMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.ANTIBANDING, KEYS.ANTIBANDING_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            JpegQuality = new JpegQualityParameter(cameraParameters, cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            AE_Bracket = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.AE_BRACKET_HDR, KEYS.AE_BRACKET_HDR_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try
        {
            ImagePostProcessing = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.IMAGEPOSTPROCESSING, KEYS.IMAGEPOSTPROCESSING_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewSize = new PreviewSizeParameter(cameraParameters, cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewFPS = new PreviewFpsParameter(cameraParameters, cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewFormat = new PreviewFormatParameter(cameraParameters, cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            SceneMode =  new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.SCENE_MODE,KEYS.SCENE_MODE_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            RedEye = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.REDEYE_REDUCTION, KEYS.REDEYE_REDUCTION_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            LensShade = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.LENSSHADE, KEYS.LENSSHADE_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            VideoStabilization = new VideoStabilizationParameter(cameraParameters, cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createZeroShutterLag();

        try {
            SceneDetect = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.SCENE_DETECT, KEYS.SCENE_DETECT_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }


        try {
            if(cameraParameters.get(KEYS.SONY_VS)!=null)
                DigitalImageStabilization = new BaseModeParameter(cameraParameters, cameraUiWrapper,KEYS.SONY_VS,KEYS.SONY_VS_VALUES);
            else if (cameraParameters.get(KEYS.DIGITALIMAGESTABILIZATION)!=null)
                DigitalImageStabilization = new BaseModeParameter(cameraParameters, cameraUiWrapper,KEYS.DIGITALIMAGESTABILIZATION,KEYS.DIGITALIMAGESTABILIZATION_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            MemoryColorEnhancement = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.MEMORYCOLORENHANCEMENT, KEYS.MEMORYCOLORENHANCEMENT_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            SkinToneEnhancment = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.SKINETONEENHANCEMENT, KEYS.SKINETONEENHANCEMENT_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            CameraMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "camera-mode", "camera-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            ExposureLock = new ExposureLockParameter(cameraParameters, cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            VideoSize = new BaseModeParameter(cameraParameters, cameraUiWrapper,"video-size","video-size-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        createVideoHDR();


        try {
            CDS_Mode = new CDS_Mode_Parameter(cameraParameters, cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }


        try {
            RdiMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "rdi-mode", "rdi-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            SecureMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "secure-mode", "secure-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }


        try {
            TnrMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "tnr-mode", "tnr-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            oismode = new OisParameter(cameraParameters, cameraUiWrapper, "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            Focuspeak = new FocusPeakModeParameter(cameraUiWrapper,((Camera1Fragment) cameraUiWrapper).focusPeakProcessorAp1);
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
            captureBurstExposures = new CupBurstExpModeParameter(cameraParameters, cameraUiWrapper, appSettingsManager);
        }
        catch (Exception e)
        {
            Logger.exception(e);
        }

        try {
            HDRMode = new HDRModeParameter(cameraParameters, cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        imageStackMode = new StackModeParameter();

        //load device specific stuff
        Device = new DeviceSelector().getDevice(cameraUiWrapper, cameraParameters);

        if (Device == null)
        {
            Logger.d(TAG,"################# DEVICES IS NULL! FAIL!");
            throw new NullPointerException("DEVICE IS NULL");
        }

        VideoProfiles = Device.getVideoProfileMode();
        Skintone = Device.getSkintoneParameter();
        NonZslManualMode = Device.getNonZslManualMode();
        opcode = Device.getOpCodeParameter();
        Denoise = Device.getDenoiseParameter();
        LensFilter = Device.getLensFilter();
        NightMode = Device.getNightMode();

        ManualShutter = Device.getExposureTimeParameter();
        ManualFocus = Device.getManualFocusParameter();
        ManualIso = Device.getIsoParameter();
        CCT = Device.getCCTParameter();
        ManualSaturation = Device.getManualSaturation();
        ManualSharpness = Device.getManualSharpness();
        ManualBrightness = Device.getManualBrightness();
        ManualContrast = Device.getManualContrast();

        Module = new ModuleParameters(cameraUiWrapper, appSettingsManager);


        try
        {
            //set last used settings
            SetAppSettingsToParameters();
            SetParametersToCamera(cameraParameters);
        } catch (Exception e) {
            Logger.exception(e);
        }


        //parameters are setup notfiy ui that its rdy to use
        ParametersHasLoaded();

        cameraUiWrapper.GetModuleHandler().SetModule(appSettingsManager.GetCurrentModule());
    }

    private void createExposureMode() {
        try
        {
            if (cameraParameters.get("exposure-mode-values")!= null)
                ExposureMode = new BaseModeParameter(cameraParameters, cameraUiWrapper,"exposure","exposure-mode-values");
            else if (cameraParameters.get("auto-exposure-values")!= null)
                ExposureMode = new BaseModeParameter(cameraParameters, cameraUiWrapper,"auto-exposure","auto-exposure-values");
            else if(cameraParameters.get("sony-metering-mode-values")!= null)
                ExposureMode = new BaseModeParameter(cameraParameters, cameraUiWrapper,"sony-metering-mode","sony-metering-mode-values");
            else if(cameraParameters.get("exposure-meter-values")!= null)
                ExposureMode = new BaseModeParameter(cameraParameters, cameraUiWrapper,"exposure-meter","exposure-meter-values");
            if (ExposureMode != null)
                ExposureMode.addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).aeModeListner);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createIsoMode() {
        try {
            if (cameraParameters.get("iso-mode-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters, cameraUiWrapper,"iso","iso-mode-values");
            else if (cameraParameters.get("iso-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters, cameraUiWrapper,"iso","iso-values");
            else if (cameraParameters.get("iso-speed-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters, cameraUiWrapper,"iso-speed","iso-speed-values");
            else if (cameraParameters.get("sony-iso-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters, cameraUiWrapper,"sony-iso","sony-iso-values");
            else if (cameraParameters.get("lg-iso-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters, cameraUiWrapper,"iso","lg-iso-values");
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
            //Code style break here device lookup
            if(appSettingsManager.getDevice() == Devices.Htc_M8 ||appSettingsManager.getDevice() == Devices.Htc_M9||appSettingsManager.getDevice() == Devices.HTC_OneA9||appSettingsManager.getDevice() == Devices.HTC_OneE8 )
                HTCVideoMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "video-mode", "video-hfr-values");

            if (((CameraHolder) cameraUiWrapper.GetCameraHolder()).DeviceFrameWork == Frameworks.MTK)
                VideoHighFramerateVideo = new BaseModeParameter(cameraParameters, cameraUiWrapper, "hsvr-prv-fps", "hsvr-prv-fps-values");
            else
                VideoHighFramerateVideo = new BaseModeParameter(cameraParameters, cameraUiWrapper, "video-hfr", "video-hfr-values");

        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createVideoHDR() {
        try {
            if (cameraParameters.get("video-hdr")!= null)
                VideoHDR = new BaseModeParameter(cameraParameters, cameraUiWrapper,"video-hdr", "video-hdr-values");
            else if (cameraParameters.get("sony-video-hdr")!= null)
                VideoHDR = new BaseModeParameter(cameraParameters, cameraUiWrapper,"sony-video-hdr","sony-video-hdr-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createZeroShutterLag() {
        try {
            if (cameraParameters.get("zsl")!= null)
                ZSL = new BaseModeParameter(cameraParameters, cameraUiWrapper,"zsl","zsl-values");
            else if (cameraParameters.get("mode")!= null)
                ZSL = new BaseModeParameter(cameraParameters, cameraUiWrapper,"mode","mode-values");
            else if (cameraParameters.get("zsd-mode")!= null)
                ZSL =new BaseModeParameter(cameraParameters, cameraUiWrapper,"zsd-mode", "zsd-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualExposure() {
        try
        {
            ManualExposure = new ExposureManualParameter(cameraParameters, cameraUiWrapper,1);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    @Override
    public void SetMeterAREA(final FocusRect meteringAreas)
    {
        if(appSettingsManager.getDevice() == Devices.ZTE_ADV || appSettingsManager.getDevice() == Devices.ZTEADV234 || appSettingsManager.getDevice() == Devices.ZTEADVIMX214)
        {
            try
            {
                cameraParameters.set("touch-aec","on");
                cameraParameters.set("selectable-zone-af","spot-metering");
                cameraParameters.set("raw-size","4208x3120");
                cameraParameters.set("touch-index-aec", meteringAreas.x + "," + meteringAreas.y);
                SetParametersToCamera(cameraParameters);
            }
            catch (Exception e)
            {
                Logger.exception(e);
            }
        }
    }

    @Override
    public I_Device getDevice() {
        return Device;
    }

    @Override
    public void SetFocusAREA(FocusRect focusAreas)
    {
        getDevice().SetFocusArea(focusAreas);
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
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(KEYS.ON))
        {
            int or = orientation +180;
            if (or >360)
                or = or - 360;
            orientation = or;
        }
        try
        {
            cameraParameters.setRotation(orientation);
            SetParametersToCamera(cameraParameters);
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
            appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack , KEYS.OFF);
        }
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(KEYS.OFF))
            ((CameraHolder) cameraUiWrapper.GetCameraHolder()).SetCameraRotation(0);
        else
            ((CameraHolder) cameraUiWrapper.GetCameraHolder()).SetCameraRotation(180);
    }

    public void SetupMTK()    {


        cameraParameters.set("afeng_raw_dump_flag", "1");
        cameraParameters.set("isp-mode", "1");
        cameraParameters.set("rawsave-mode", "2");
        cameraParameters.set("rawfname", StringUtils.GetInternalSDCARD()+"/DCIM/FreeDCam/mtk_."+ FileEnding.BAYER);
        cameraParameters.set("zsd-mode", "on");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Logger.e(TAG,e.getMessage());
        }
    }



    public void SetZTE_AE()
    {
        cameraParameters.set("slow_shutter", "-1");
        SetParametersToCamera(cameraParameters);
    }
}
