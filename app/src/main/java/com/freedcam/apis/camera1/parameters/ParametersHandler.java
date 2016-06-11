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

package com.freedcam.apis.camera1.parameters;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.FocusRect;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.modules.I_ModuleEvent;
import com.freedcam.apis.basecamera.parameters.AbstractParameterHandler;
import com.freedcam.apis.basecamera.parameters.modes.LocationParameter;
import com.freedcam.apis.basecamera.parameters.modes.ModuleParameters;
import com.freedcam.apis.camera1.Camera1Fragment;
import com.freedcam.apis.camera1.CameraHolder;
import com.freedcam.apis.camera1.CameraHolder.Frameworks;
import com.freedcam.apis.camera1.FocusHandler;
import com.freedcam.apis.camera1.parameters.device.I_Device;
import com.freedcam.apis.camera1.parameters.manual.BaseManualParameter;
import com.freedcam.apis.camera1.parameters.manual.BurstManualParam;
import com.freedcam.apis.camera1.parameters.manual.ExposureManualParameter;
import com.freedcam.apis.camera1.parameters.manual.FXManualParameter;
import com.freedcam.apis.camera1.parameters.manual.ZoomManualParameter;
import com.freedcam.apis.camera1.parameters.modes.BaseModeParameter;
import com.freedcam.apis.camera1.parameters.modes.CDS_Mode_Parameter;
import com.freedcam.apis.camera1.parameters.modes.CupBurstExpModeParameter;
import com.freedcam.apis.camera1.parameters.modes.ExposureLockParameter;
import com.freedcam.apis.camera1.parameters.modes.FocusPeakModeParameter;
import com.freedcam.apis.camera1.parameters.modes.HDRModeParameter;
import com.freedcam.apis.camera1.parameters.modes.JpegQualityParameter;
import com.freedcam.apis.camera1.parameters.modes.OisParameter;
import com.freedcam.apis.camera1.parameters.modes.PictureFormatHandler;
import com.freedcam.apis.camera1.parameters.modes.PictureSizeParameter;
import com.freedcam.apis.camera1.parameters.modes.PreviewFormatParameter;
import com.freedcam.apis.camera1.parameters.modes.PreviewFpsParameter;
import com.freedcam.apis.camera1.parameters.modes.PreviewSizeParameter;
import com.freedcam.apis.camera1.parameters.modes.StackModeParameter;
import com.freedcam.apis.camera1.parameters.modes.VideoStabilizationParameter;
import com.freedcam.utils.AppSettingsManager;
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
    private I_CameraUiWrapper cameraUiWrapper;
    private I_Device Device;

    public ParametersHandler(I_CameraUiWrapper cameraUiWrapper, Context context, AppSettingsManager appSettingsManager)
    {
        super(context,cameraUiWrapper);
        cameraHolder = (CameraHolder)cameraUiWrapper.GetCameraHolder();
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
            PictureFormat = new PictureFormatHandler(cameraParameters, cameraUiWrapper, this);
            cameraUiWrapper.GetModuleHandler().moduleEventHandler.addListner((I_ModuleEvent) PictureFormat);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            PictureSize = new PictureSizeParameter(cameraParameters,cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            FocusMode = new BaseModeParameter(cameraParameters, cameraUiWrapper,KEYS.FOCUS_MODE,KEYS.FOCUS_MODE_VALUES);
            FocusMode.addEventListner(((FocusHandler) cameraHolder.Focus).focusModeListner);
        } catch (Exception e) {
            Logger.exception(e);
        }

        locationParameter = new LocationParameter(cameraHolder,context,appSettingsManager);

        try {
            ManualConvergence = new BaseManualParameter(context, cameraParameters, KEYS.MANUAL_CONVERGENCE, KEYS.SUPPORTED_MANUAL_CONVERGENCE_MAX, KEYS.SUPPORTED_MANUAL_CONVERGENCE_MIN, this,1);
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
            FX = new FXManualParameter(context,cameraParameters,this);
            PictureFormat.addEventListner(((BaseManualParameter)FX).GetPicFormatListner());
            cameraUiWrapper.GetModuleHandler().moduleEventHandler.addListner(((BaseManualParameter) FX).GetModuleListner());
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            Burst = new BurstManualParam(context,cameraParameters, this);
            cameraUiWrapper.GetModuleHandler().moduleEventHandler.addListner(((BaseManualParameter) Burst).GetModuleListner());
        } catch (Exception e) {
            Logger.exception(e);
        }


        try {
            Zoom = new ZoomManualParameter(context,cameraParameters,this);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            ColorMode = new BaseModeParameter(cameraParameters,cameraUiWrapper, KEYS.COLOR_EFFECT, KEYS.COLOR_EFFECT_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createExposureMode();

        try {
            FlashMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,KEYS.FLASH_MODE,KEYS.FLASH_MODE_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        createIsoMode();

        try {
            AntiBandingMode = new BaseModeParameter(cameraParameters,cameraUiWrapper, KEYS.ANTIBANDING, KEYS.ANTIBANDING_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            JpegQuality = new JpegQualityParameter(cameraParameters, cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            AE_Bracket = new BaseModeParameter(cameraParameters,cameraUiWrapper, KEYS.AE_BRACKET_HDR, KEYS.AE_BRACKET_HDR_VALUES);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try
        {
            ImagePostProcessing = new BaseModeParameter(cameraParameters,cameraUiWrapper, KEYS.IMAGEPOSTPROCESSING, KEYS.IMAGEPOSTPROCESSING_VALUES);
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
            VideoStabilization = new VideoStabilizationParameter(cameraParameters,cameraUiWrapper);
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
                DigitalImageStabilization = new BaseModeParameter(cameraParameters,cameraUiWrapper,KEYS.SONY_VS,KEYS.SONY_VS_VALUES);
            else if (cameraParameters.get(KEYS.DIGITALIMAGESTABILIZATION)!=null)
                DigitalImageStabilization = new BaseModeParameter(cameraParameters,cameraUiWrapper,KEYS.DIGITALIMAGESTABILIZATION,KEYS.DIGITALIMAGESTABILIZATION_VALUES);
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
            CameraMode = new BaseModeParameter(cameraParameters,cameraUiWrapper, "camera-mode", "camera-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            ExposureLock = new ExposureLockParameter(cameraParameters, cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            VideoSize = new BaseModeParameter(cameraParameters,cameraUiWrapper,"video-size","video-size-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        createVideoHDR();


        try {
            CDS_Mode = new CDS_Mode_Parameter(cameraParameters,cameraUiWrapper);
        } catch (Exception e) {
            Logger.exception(e);
        }

        //Video Denoise
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

        //Temporal Noise Reduction http://nofilmschool.com/2012/03/temporal-noise-reduction-ipad-its-improvement
        try {
            TnrMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "tnr-mode", "tnr-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            oismode = new OisParameter(cameraParameters,cameraUiWrapper, "");
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            Focuspeak = new FocusPeakModeParameter(cameraUiWrapper,((Camera1Fragment)cameraUiWrapper).focusPeakProcessorAp1);
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
            captureBurstExposures = new CupBurstExpModeParameter(cameraParameters, cameraUiWrapper,appSettingsManager);
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
        Device = new DeviceSelector().getDevice(context,cameraUiWrapper,cameraParameters,cameraHolder);

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
        CCT =  Device.getCCTParameter();
        ManualSaturation = Device.getManualSaturation();
        ManualSharpness = Device.getManualSharpness();
        ManualBrightness = Device.getManualBrightness();
        ManualContrast = Device.getManualContrast();

        Module = new ModuleParameters(cameraUiWrapper,appSettingsManager);


        try {
            SetAppSettingsToParameters();
            SetParametersToCamera(cameraParameters);
        } catch (Exception e) {
            Logger.exception(e);
        }


        ParametersHasLoaded();

        cameraUiWrapper.GetModuleHandler().SetModule(appSettingsManager.GetCurrentModule());
    }

    private void createExposureMode() {
        try
        {
            if (cameraParameters.get("exposure-mode-values")!= null)
                ExposureMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,"exposure","exposure-mode-values");
            else if (cameraParameters.get("auto-exposure-values")!= null)
                ExposureMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,"auto-exposure","auto-exposure-values");
            else if(cameraParameters.get("sony-metering-mode-values")!= null)
                ExposureMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,"sony-metering-mode","sony-metering-mode-values");
            else if(cameraParameters.get("exposure-meter-values")!= null)
                ExposureMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,"exposure-meter","exposure-meter-values");
            if (ExposureMode != null)
                ExposureMode.addEventListner(((FocusHandler) cameraHolder.Focus).aeModeListner);
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createIsoMode() {
        try {
            if (cameraParameters.get("iso-mode-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,"iso","iso-mode-values");
            else if (cameraParameters.get("iso-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,"iso","iso-values");
            else if (cameraParameters.get("iso-speed-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,"iso-speed","iso-speed-values");
            else if (cameraParameters.get("sony-iso-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,"sony-iso","sony-iso-values");
            else if (cameraParameters.get("lg-iso-values")!= null)
                IsoMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,"iso","lg-iso-values");
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
                VideoHDR = new BaseModeParameter(cameraParameters,cameraUiWrapper,"video-hdr", "video-hdr-values");
            else if (cameraParameters.get("sony-video-hdr")!= null)
                VideoHDR = new BaseModeParameter(cameraParameters,cameraUiWrapper,"sony-video-hdr","sony-video-hdr-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createZeroShutterLag() {
        try {
            if (cameraParameters.get("zsl")!= null)
                ZSL = new BaseModeParameter(cameraParameters,cameraUiWrapper,"zsl","zsl-values");
            else if (cameraParameters.get("mode")!= null)
                ZSL = new BaseModeParameter(cameraParameters,cameraUiWrapper,"mode","mode-values");
            else if (cameraParameters.get("zsd-mode")!= null)
                ZSL =new BaseModeParameter(cameraParameters,cameraUiWrapper,"zsd-mode", "zsd-mode-values");
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    private void createManualExposure() {
        try
        {
            ManualExposure = new ExposureManualParameter(context,cameraParameters, this,1);
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
    public I_Device getDevice() {
        return Device;
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
            appSettingsManager.setString(AppSettingsManager.SETTING_OrientationHack , KEYS.OFF);
        }
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(KEYS.OFF))
            cameraHolder.SetCameraRotation(0);
        else
            cameraHolder.SetCameraRotation(180);
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
}
