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

package com.freedcam.apis.camera1.camera.parameters;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.FocusRect;
import com.freedcam.apis.basecamera.camera.modules.I_ModuleEvent;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.apis.basecamera.camera.parameters.modes.LocationParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.ModuleParameters;
import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.apis.camera1.camera.CameraHolder.Frameworks;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.FocusHandler;
import com.freedcam.apis.camera1.camera.parameters.device.AbstractDevice;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseManualParamMTK;
import com.freedcam.apis.camera1.camera.parameters.manual.BaseManualParameter;
import com.freedcam.apis.camera1.camera.parameters.manual.BurstManualParam;
import com.freedcam.apis.camera1.camera.parameters.manual.ExposureManualParameter;
import com.freedcam.apis.camera1.camera.parameters.manual.FXManualParameter;
import com.freedcam.apis.camera1.camera.parameters.manual.ZoomManualParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.BaseModeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.CDS_Mode_Parameter;
import com.freedcam.apis.camera1.camera.parameters.modes.CupBurstExpModeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.ExposureLockParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.FocusPeakModeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.HDRModeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.JpegQualityParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.NightModeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.OisParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.PictureFormatHandler;
import com.freedcam.apis.camera1.camera.parameters.modes.PictureSizeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.PreviewFormatParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.PreviewFpsParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.PreviewSizeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.StackModeParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.VideoStabilizationParameter;
import com.freedcam.apis.camera1.camera.parameters.modes.VirtualLensFilter;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.DeviceUtils.Devices;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.freedcam.utils.StringUtils.FileEnding;

import java.util.ArrayList;

/**
 * Created by troop on 17.08.2014.
 */
public class ParametersHandler extends AbstractParameterHandler
{

    private final String TAG = ParametersHandler.class.getSimpleName();

    private Parameters cameraParameters;
    public Parameters getParameters(){return cameraParameters;}
    public CameraHolder cameraHolder;
    public BaseModeParameter DualMode;
    private CameraUiWrapper cameraUiWrapper;
    public AbstractDevice Device;

    public ParametersHandler(CameraUiWrapper cameraUiWrapper, Context context, AppSettingsManager appSettingsManager)
    {
        super(cameraUiWrapper.cameraHolder,context,appSettingsManager);
        cameraHolder = cameraUiWrapper.cameraHolder;
        this.cameraUiWrapper = cameraUiWrapper;
    }

    public void SetParametersToCamera(Parameters params)
    {
        Logger.d(TAG, "SetParametersToCam");
        cameraHolder.SetCameraParameters(params);
    }

    public void LoadParametersFromCamera()
    {
        cameraParameters = cameraHolder.GetCameraParameters();
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



    private void initParameters()
    {

        logParameters(cameraParameters);


        //setup first Pictureformat its needed for manual parameters to
        // register their listners there if its postprocessing parameter
        try {
            PictureFormat = new PictureFormatHandler(cameraParameters, cameraHolder, this);
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner((I_ModuleEvent) PictureFormat);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PictureSize = new PictureSizeParameter(cameraParameters,cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            FocusMode = new BaseModeParameter(cameraParameters, cameraHolder,KEYS.FOCUS_MODE,KEYS.FOCUS_MODE_VALUES);
            FocusMode.addEventListner(((FocusHandler) cameraHolder.Focus).focusModeListner);
        } catch (Exception e) {
            Logger.exception(e);
        }

        locationParameter = new LocationParameter(cameraHolder,context,appSettingsManager);

        createManualBrightness();

        createManualContrast();

        try {
            ManualConvergence = new BaseManualParameter(cameraParameters, KEYS.MANUAL_CONVERGENCE, KEYS.SUPPORTED_MANUAL_CONVERGENCE_MAX, KEYS.SUPPORTED_MANUAL_CONVERGENCE_MIN, this,1);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createManualExposure();


        //createManualSaturation();

        createManualSharpness();


        try {
            WhiteBalanceMode = new BaseModeParameter(cameraParameters, cameraHolder, KEYS.WHITEBALANCE, KEYS.WHITEBALANCE_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            FX = new FXManualParameter(cameraParameters,this);
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
            Zoom = new ZoomManualParameter(cameraParameters,this);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            ColorMode = new BaseModeParameter(cameraParameters,cameraHolder, KEYS.COLOR_EFFECT, KEYS.COLOR_EFFECT_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createExposureMode();

        try {
            FlashMode = new BaseModeParameter(cameraParameters,cameraHolder,KEYS.FLASH_MODE,KEYS.FLASH_MODE_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createIsoMode();

        try {
            AntiBandingMode = new BaseModeParameter(cameraParameters,cameraHolder, KEYS.ANTIBANDING, KEYS.ANTIBANDING_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            JpegQuality = new JpegQualityParameter(cameraParameters, cameraHolder, "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            AE_Bracket = new BaseModeParameter(cameraParameters,cameraHolder, KEYS.AE_BRACKET_HDR, KEYS.AE_BRACKET_HDR_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try
        {
            ImagePostProcessing = new BaseModeParameter(cameraParameters,cameraHolder, KEYS.IMAGEPOSTPROCESSING, KEYS.IMAGEPOSTPROCESSING_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewSize = new PreviewSizeParameter(cameraParameters, cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewFPS = new PreviewFpsParameter(cameraParameters, cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PreviewFormat = new PreviewFormatParameter(cameraParameters, cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            SceneMode =  new BaseModeParameter(cameraParameters, cameraHolder, KEYS.SCENE_MODE,KEYS.SCENE_MODE_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            RedEye = new BaseModeParameter(cameraParameters, cameraHolder, KEYS.REDEYE_REDUCTION, KEYS.REDEYE_REDUCTION_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            LensShade = new BaseModeParameter(cameraParameters, cameraHolder, KEYS.LENSSHADE, KEYS.LENSSHADE_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            VideoStabilization = new VideoStabilizationParameter(cameraParameters,cameraHolder);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createZeroShutterLag();

        try {
            SceneDetect = new BaseModeParameter(cameraParameters, cameraHolder, KEYS.SCENE_DETECT, KEYS.SCENE_DETECT_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }


        try {
            if(cameraParameters.get(KEYS.SONY_VS)!=null)
                DigitalImageStabilization = new BaseModeParameter(cameraParameters,cameraHolder,KEYS.SONY_VS,KEYS.SONY_VS_VALUES);
            else if (cameraParameters.get(KEYS.DIGITALIMAGESTABILIZATION)!=null)
                DigitalImageStabilization = new BaseModeParameter(cameraParameters,cameraHolder,KEYS.DIGITALIMAGESTABILIZATION,KEYS.DIGITALIMAGESTABILIZATION_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            MemoryColorEnhancement = new BaseModeParameter(cameraParameters, cameraHolder, KEYS.MEMORYCOLORENHANCEMENT, KEYS.MEMORYCOLORENHANCEMENT_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            SkinToneEnhancment = new BaseModeParameter(cameraParameters, cameraHolder, KEYS.SKINETONEENHANCEMENT, KEYS.SKINETONEENHANCEMENT_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            NightMode = new NightModeParameter(cameraParameters, cameraHolder, "", cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }


        try {
            CameraMode = new BaseModeParameter(cameraParameters,cameraHolder, "camera-mode", "camera-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            DualMode = new BaseModeParameter(cameraParameters,cameraHolder, "dual_mode", "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            ExposureLock = new ExposureLockParameter(cameraParameters, cameraHolder, "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            VideoSize = new BaseModeParameter(cameraParameters,cameraHolder,"video-size","video-size");
        } catch (Exception e) {
            Logger.exception(e);
        }

        createVideoHDR();


        try {
            CDS_Mode = new CDS_Mode_Parameter(cameraParameters,cameraHolder,"");
        } catch (Exception e) {
            Logger.exception(e);
        }

        //####No idea what they do, m9 specific, only thing they do is to freez the app####
        //Video Denoise
        try {
            RdiMode = new BaseModeParameter(cameraParameters, cameraHolder, "rdi-mode", "rdi-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            SecureMode = new BaseModeParameter(cameraParameters, cameraHolder, "secure-mode", "secure-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        //Temporal Noise Reduction http://nofilmschool.com/2012/03/temporal-noise-reduction-ipad-its-improvement
        try {
            TnrMode = new BaseModeParameter(cameraParameters, cameraHolder, "tnr-mode", "tnr-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            oismode = new OisParameter(uiHandler,cameraParameters,cameraHolder, "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            Focuspeak = new FocusPeakModeParameter(cameraHolder,cameraUiWrapper.focusPeakProcessorAp1);
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
            captureBurstExposures = new CupBurstExpModeParameter(cameraParameters, cameraHolder, "",appSettingsManager);
        }
        catch (Exception e)
        {
            Logger.exception(e);
        }

        try {
            morphoHDR = new BaseModeParameter(cameraParameters, cameraHolder, "morpho-hdr", "");

            morphoHHT = new BaseModeParameter(cameraParameters, cameraHolder, "morpho-hht", "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if(appSettingsManager.getDevice() == Devices.ZTE_ADV || appSettingsManager.getDevice() == Devices.ZTEADV234 ||appSettingsManager.getDevice() == Devices.ZTEADVIMX214)
                LensFilter = new VirtualLensFilter(cameraParameters, cameraHolder, "", cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            HDRMode = new HDRModeParameter(cameraParameters, cameraHolder, "", cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        imageStackMode = new StackModeParameter();

        //load device specific stuff
        Device = new DeviceSelector().getDevice(cameraUiWrapper,cameraParameters,cameraHolder);

        if (Device == null)
        {
            Logger.d(TAG,"################# DEVICES IS NULL! FAIL!");
            throw new NullPointerException("DEVICE IS NULL");
        }

        ManualShutter = Device.getExposureTimeParameter();
        ManualFocus = Device.getManualFocusParameter();
        ManualIso = Device.getIsoParameter();
        CCT =  Device.getCCTParameter();
        VideoProfiles = Device.getVideoProfileMode();
        Skintone = Device.getSkintoneParameter();
        NonZslManualMode = Device.getNonZslManualMode();
        opcode = Device.getOpCodeParameter();
        Denoise = Device.getDenoiseParameter();
        ManualSaturation = Device.getManualSaturation();

        Module = new ModuleParameters(cameraUiWrapper,appSettingsManager);



        try {
            SetAppSettingsToParameters();
            SetParametersToCamera(cameraParameters);
        } catch (Exception e) {
            Logger.exception(e);
        }
        cameraUiWrapper.moduleHandler.SetModule(appSettingsManager.GetCurrentModule());

        ParametersHasLoaded();
    }

    private void createExposureMode() {
        try
        {
            if (cameraParameters.get("exposure-mode-values")!= null)
                ExposureMode = new BaseModeParameter(cameraParameters,cameraHolder,"exposure","exposure-mode-values");
            else if (cameraParameters.get("auto-exposure-values")!= null)
                ExposureMode = new BaseModeParameter(cameraParameters,cameraHolder,"auto-exposure","auto-exposure-values");
            else if(cameraParameters.get("sony-metering-mode-values")!= null)
                ExposureMode = new BaseModeParameter(cameraParameters,cameraHolder,"sony-metering-mode","sony-metering-mode-values");
            else if(cameraParameters.get("exposure-meter-values")!= null)
                ExposureMode = new BaseModeParameter(cameraParameters,cameraHolder,"exposure-meter","exposure-meter-values");
            if (ExposureMode != null)
                ExposureMode.addEventListner(((FocusHandler) cameraHolder.Focus).aeModeListner);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createIsoMode() {
        try {
            if (cameraParameters.get("iso-mode-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters,cameraHolder,"iso","iso-mode-values");
            else if (cameraParameters.get("iso-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters,cameraHolder,"iso","iso-values");
            else if (cameraParameters.get("iso-speed-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters,cameraHolder,"iso-speed","iso-speed-values");
            else if (cameraParameters.get("sony-iso-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters,cameraHolder,"sony-iso","sony-iso-values");
            else if (cameraParameters.get("lg-iso-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters,cameraHolder,"iso","lg-iso-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualBrightness() {
        try {

            if (cameraParameters.get("brightness")!= null && cameraParameters.get("brightness-values")!= null)
            {
                if (cameraHolder.DeviceFrameWork == Frameworks.MTK)
                    ManualBrightness =  new BaseManualParamMTK(cameraParameters,"brightness", "brightness-values",this);
                else
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
                else if(appSettingsManager.getDevice() == Devices.p8lite)
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
                if (cameraHolder.DeviceFrameWork == Frameworks.MTK)
                    ManualContrast =  new BaseManualParamMTK(cameraParameters,"contrast","contrast-values",this);
                else
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
                else if(appSettingsManager.getDevice()== Devices.p8lite)
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

            if (cameraHolder.DeviceFrameWork == Frameworks.MTK)
                VideoHighFramerateVideo = new BaseModeParameter(cameraParameters, cameraHolder, "hsvr-prv-fps", "hsvr-prv-fps-values");
            else
                VideoHighFramerateVideo = new BaseModeParameter(cameraParameters, cameraHolder, "video-hfr", "video-hfr-values");

        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createVideoHDR() {
        try {
            if (cameraParameters.get("video-hdr")!= null)
                VideoHDR = new BaseModeParameter(cameraParameters,cameraHolder,"video-hdr", "video-hdr-values");
            else if (cameraParameters.get("sony-video-hdr")!= null)
                VideoHDR = new BaseModeParameter(cameraParameters,cameraHolder,"sony-video-hdr","sony-video-hdr-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createZeroShutterLag() {
        try {
            if (cameraParameters.get("zsl")!= null)
                ZSL = new BaseModeParameter(cameraParameters,cameraHolder,"zsl","zsl-values");
            else if (cameraParameters.get("mode")!= null)
                ZSL = new BaseModeParameter(cameraParameters,cameraHolder,"mode","mode-values");
            else if (cameraParameters.get("zsd-mode")!= null)
                ZSL =new BaseModeParameter(cameraParameters,cameraHolder,"zsd-mode", "zsd-mode-values");
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
                if (cameraHolder.DeviceFrameWork == Frameworks.MTK)
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
        if(appSettingsManager.getDevice() == Devices.ZTE_ADV || appSettingsManager.getDevice() == Devices.ZTEADV234 ||appSettingsManager.getDevice() == Devices.ZTEADVIMX214)
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
        if(appSettingsManager.getDevice() == Devices.ZTE_ADV || appSettingsManager.getDevice() == Devices.ZTEADV234 ||appSettingsManager.getDevice() == Devices.ZTEADVIMX214)
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
            Area a = new Area(new Rect(focusAreas.left,focusAreas.top,focusAreas.right,focusAreas.bottom),1000);
            ArrayList<Area> ar = new ArrayList<>();
            ar.add(a);
            cameraParameters.setFocusAreas(ar);
            SetParametersToCamera(cameraParameters);
        }
    }

    public float getMTKShutterSpeed()
    {
        if(cameraParameters.get("eng-capture-shutter-speed")!= null) {
            if (Float.parseFloat(cameraHolder.GetParamsDirect("eng-capture-shutter-speed")) == 0) {
                return 0.0f;
            } else
                return Float.parseFloat(cameraParameters.get("eng-capture-shutter-speed")) / 1000;
        }
        else if(cameraParameters.get("cap-ss")!= null)
        {
            if (Float.parseFloat(cameraParameters.get("cap-ss")) == 0) {
                return 0.0f;
            } else
                return Float.parseFloat(cameraParameters.get("cap-ss")) / 1000;
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

    public int getQCISO()
    {
        if(cameraHolder.GetParamsDirect("cur-iso")!= null)
        {
            int a= Integer.parseInt(cameraHolder.GetParamsDirect("cur-iso"));
            return a;

        }
        return 0;

    }

    public String getQCShutterSpeed()
    {

        if(cameraHolder.GetParamsDirect("cur-exposure-time")!= null)
        {
            int a= (int)(Float.parseFloat(cameraHolder.GetParamsDirect("cur-exposure-time")) * 1000);
            return "1/"+a;

        }
        return KEYS.AUTO;

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
        cameraParameters.set("rawfname", StringUtils.GetInternalSDCARD()+"/DCIM/FreeDCam/mtk_."+ FileEnding.BAYER);
        cameraParameters.set("zsd-mode", "on");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Logger.e(TAG,e.getMessage());
        }
    }

    public float GetFnumber()
    {
        if (cameraParameters.get("f-number")!= null) {
            String fnum = cameraParameters.get("f-number");
            return Float.parseFloat(fnum);
        }
        else
            return 0;
    }

    public float GetFocal()
    {
        if (cameraParameters.get("focal-length")!= null) {
            String focal = cameraParameters.get("focal-length");
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
}
