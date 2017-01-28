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

import android.graphics.Rect;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.util.Log;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.modes.LocationParameter;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModuleParameters;
import freed.cam.apis.camera1.Camera1Fragment;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.FocusHandler;
import freed.cam.apis.camera1.parameters.device.I_Device;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.cam.apis.camera1.parameters.manual.ExposureManualParameter;
import freed.cam.apis.camera1.parameters.manual.ZoomManualParameter;
import freed.cam.apis.camera1.parameters.manual.focus.BaseFocusManual;
import freed.cam.apis.camera1.parameters.manual.focus.FocusManualHuawei;
import freed.cam.apis.camera1.parameters.manual.htc.FocusManualParameterHTC;
import freed.cam.apis.camera1.parameters.manual.mtk.FocusManualMTK;
import freed.cam.apis.camera1.parameters.manual.qcom.BurstManualParam;
import freed.cam.apis.camera1.parameters.manual.zte.FXManualParameter;
import freed.cam.apis.camera1.parameters.modes.BaseModeParameter;
import freed.cam.apis.camera1.parameters.modes.ExposureLockParameter;
import freed.cam.apis.camera1.parameters.modes.FocusPeakModeParameter;
import freed.cam.apis.camera1.parameters.modes.PictureFormatHandler;
import freed.cam.apis.camera1.parameters.modes.PictureSizeParameter;
import freed.cam.apis.camera1.parameters.modes.PreviewFpsParameter;
import freed.cam.apis.camera1.parameters.modes.PreviewSizeParameter;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.utils.AppSettingsManager;
import freed.utils.DeviceUtils.Devices;
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
        Log.d(TAG, "SetParametersToCam");
        ((CameraHolder) cameraUiWrapper.GetCameraHolder()).SetCameraParameters(params);
    }

    @Override
    protected void SetParameters() {
        ((CameraHolder) cameraUiWrapper.GetCameraHolder()).SetCameraParameters(cameraParameters);
    }

    public void LoadParametersFromCamera()
    {
        cameraParameters = ((CameraHolder) cameraUiWrapper.GetCameraHolder()).GetCameraParameters();
        initParameters();
    }

    private void logParameters(Parameters parameters)
    {
        Log.d(TAG, "Manufactur:" + Build.MANUFACTURER);
        Log.d(TAG, "Model:" + Build.MODEL);
        Log.d(TAG, "Product:" + Build.PRODUCT);
        Log.d(TAG, "OS:" + System.getProperty("os.version"));
        String[] split = parameters.flatten().split(";");
        for(String e : split)
        {
            Log.d(TAG,e);
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
        PictureFormat = new PictureFormatHandler(cameraParameters, cameraUiWrapper, this);
        cameraUiWrapper.GetModuleHandler().addListner((ModuleChangedEvent) PictureFormat);
        AppSettingsManager appS = cameraUiWrapper.GetAppSettingsManager();
        if (appS.pictureSize.isSupported())
            PictureSize = new PictureSizeParameter(cameraParameters, cameraUiWrapper);

        if (appS.focusMode.isSupported()) {
            FocusMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.FOCUS_MODE, appS.focusMode.getValues());
            FocusMode.addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).focusModeListner);
        }

        if (appS.whiteBalanceMode.isSupported())
            WhiteBalanceMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.WHITEBALANCE, appS.whiteBalanceMode.getValues());

        if (appS.exposureMode.isSupported())
            ExposureMode = new BaseModeParameter(cameraParameters,cameraUiWrapper, appS.exposureMode.getKEY(), appS.exposureMode.getValues());

        if (appS.colorMode.isSupported())
            ColorMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,KEYS.COLOR_EFFECT, appS.colorMode.getValues());

        if (appS.flashMode.isSupported())
            ColorMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,KEYS.FLASH_MODE, appS.focusMode.getValues());

        if (appS.isoMode.isSupported())
            IsoMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.isoMode.getKEY(), appS.isoMode.getValues());

        if (appS.antiBandingMode.isSupported())
            AntiBandingMode = new BaseModeParameter(cameraParameters,cameraUiWrapper, KEYS.ANTIBANDING, appS.antiBandingMode.getValues());

        if (appS.imagePostProcessing.isSupported())
            ImagePostProcessing = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.IMAGEPOSTPROCESSING, appS.imagePostProcessing.getValues());

        if (appS.previewSize.isSupported())
            PreviewSize =  new PreviewSizeParameter(cameraParameters,cameraUiWrapper,"preview-size", appS.pictureSize.getValues());

        if (appS.jpegQuality.isSupported())
            JpegQuality = new BaseModeParameter(cameraParameters,cameraUiWrapper,KEYS.JPEG, appS.jpegQuality.getValues());

        if (appS.aeBracket.isSupported())
            AE_Bracket = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.AE_BRACKET_HDR, appS.aeBracket.getValues());

        if (appS.previewFps.isSupported())
            PreviewFPS = new PreviewFpsParameter(cameraParameters,cameraUiWrapper,KEYS.PREVIEW_FRAME_RATE,appS.previewFps.getValues());

        if (appS.previewFormat.isSupported())
            PreviewFormat = new BaseModeParameter(cameraParameters,cameraUiWrapper,"preview-format", appS.previewFormat.getValues());

        if (appS.sceneMode.isSupported())
            SceneMode = new BaseModeParameter(cameraParameters,cameraUiWrapper,KEYS.SCENE_MODE,appS.sceneMode.getValues());

        if (appS.redEyeMode.isSupported())
            RedEye = new BaseModeParameter(cameraParameters,cameraUiWrapper,KEYS.REDEYE_REDUCTION, appS.redEyeMode.getValues());

        if (appS.lenshade.isSupported())
            LensShade = new BaseModeParameter(cameraParameters,cameraUiWrapper,KEYS.LENSSHADE,appS.lenshade.getValues());

        if (appS.zeroshutterlag.isSupported())
            ZSL = new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.zeroshutterlag.getKEY(), appS.zeroshutterlag.getValues());

        if (appS.sceneDetectMode.isSupported())
            SceneDetect = new BaseModeParameter(cameraParameters,cameraUiWrapper, KEYS.SCENE_DETECT, appS.sceneDetectMode.getValues());

        if (appS.memoryColorEnhancement.isSupported())
            MemoryColorEnhancement = new BaseModeParameter(cameraParameters,cameraUiWrapper,KEYS.MEMORYCOLORENHANCEMENT, appS.memoryColorEnhancement.getValues());

        if (appS.videoSize.isSupported())
            VideoSize = new BaseModeParameter(cameraParameters,cameraUiWrapper,"video-size",appS.videoSize.getValues());

        if (appS.correlatedDoubleSampling.isSupported())
            CDS_Mode = new BaseModeParameter(cameraParameters,cameraUiWrapper,"cds-mode", appS.correlatedDoubleSampling.getValues());

        if (appS.opticalImageStabilisation.isSupported())
            oismode = new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.opticalImageStabilisation.getKEY(),appS.opticalImageStabilisation.getValues());

        if (appS.videoHDR.isSupported())
            VideoHDR = new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.videoHDR.getKEY(),appS.videoHDR.getValues());

        if (appS.videoHFR.isSupported())
            VideoHighFramerateVideo = new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.videoHFR.getKEY(),appS.videoHFR.getValues());


        if (appSettingsManager.manualFocus.isSupported())
        {
            if (appSettingsManager.getFrameWork() == AppSettingsManager.FRAMEWORK_MTK)
            {
                ManualFocus = new FocusManualMTK(cameraParameters, cameraUiWrapper,appS.manualFocus);
            }
            else
            {
                //htc mf
                if (appSettingsManager.manualFocus.getKEY().equals(KEYS.FOCUS))
                    ManualFocus = new FocusManualParameterHTC(cameraParameters,cameraUiWrapper);
                    //huawai mf
                else if (appS.manualFocus.getKEY().equals(KEYS.HW_MANUAL_FOCUS_STEP_VALUE))
                    ManualFocus = new FocusManualHuawei(cameraParameters, cameraUiWrapper, appS.manualFocus);
                    //qcom
                else
                    ManualFocus = new BaseFocusManual(cameraParameters,cameraUiWrapper,appS.manualFocus);
            }

        }

        if (appS.manualSaturation.isSupported()) {
            ManualSaturation = new BaseManualParameter(cameraParameters, cameraUiWrapper, appS.manualSaturation);
        }

        if (appS.manualSharpness.isSupported())
            ManualSharpness = new BaseManualParameter(cameraParameters,cameraUiWrapper,appS.manualSharpness);

        if (appS.manualBrightness.isSupported())
            ManualBrightness = new BaseManualParameter(cameraParameters,cameraUiWrapper,appS.manualBrightness);

        if(appS.manualContrast.isSupported())
            ManualContrast = new BaseManualParameter(cameraParameters,cameraUiWrapper,appS.manualContrast);

        if (appS.getDngProfilesMap().size() > 0)
            matrixChooser = new MatrixChooserParameter(cameraUiWrapper.GetAppSettingsManager().getMatrixesMap());


        VideoProfiles = new VideoProfilesParameter(cameraUiWrapper);


        locationParameter = new LocationParameter(cameraUiWrapper);

        ManualConvergence = new BaseManualParameter(cameraParameters, KEYS.MANUAL_CONVERGENCE, KEYS.SUPPORTED_MANUAL_CONVERGENCE_MAX, KEYS.SUPPORTED_MANUAL_CONVERGENCE_MIN, cameraUiWrapper,1);

        ManualExposure = new ExposureManualParameter(cameraParameters, cameraUiWrapper,1);


        //createManualSaturation();


            FX = new FXManualParameter(cameraParameters, cameraUiWrapper);
            PictureFormat.addEventListner(((BaseManualParameter) FX).GetPicFormatListner());
            cameraUiWrapper.GetModuleHandler().addListner(((BaseManualParameter) FX).GetModuleListner());


            Burst = new BurstManualParam(cameraParameters, cameraUiWrapper);
            cameraUiWrapper.GetModuleHandler().addListner(((BaseManualParameter) Burst).GetModuleListner());



            Zoom = new ZoomManualParameter(cameraParameters, cameraUiWrapper);








        /*try {
            SkinToneEnhancment = new BaseModeParameter(cameraParameters, cameraUiWrapper, KEYS.SKINETONEENHANCEMENT, KEYS.SKINETONEENHANCEMENT_VALUES);
        } catch (Exception ex) {
            ex.printStackTrace();
            SkinToneEnhancment = null;
        }*/

       /* try {
            CameraMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "camera-mode", "camera-mode-values");
        } catch (Exception ex) {
            ex.printStackTrace();
            CameraMode = null;
        }*/

            ExposureLock = new ExposureLockParameter(cameraParameters, cameraUiWrapper);




      /*  try {
            RdiMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "rdi-mode", "rdi-mode-values");
        } catch (Exception ex) {
            ex.printStackTrace();
            RdiMode = null;
        }*/

       /* try {
            SecureMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "secure-mode", "secure-mode-values");
        } catch (Exception ex) {
            ex.printStackTrace();
            SecureMode = null;
        }*/


       /* try {
            TnrMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "tnr-mode", "tnr-mode-values");
        } catch (Exception ex) {
            ex.printStackTrace();
            TnrMode = null;
        }*/


            Focuspeak = new FocusPeakModeParameter(cameraUiWrapper,((Camera1Fragment) cameraUiWrapper).focusPeakProcessorAp1);


            SetCameraRotation();
            SetPictureOrientation(0);



        //load device specific stuff
        Device = new DeviceSelector().getDevice(cameraUiWrapper, cameraParameters, appSettingsManager);

        if (Device == null)
        {
            Log.d(TAG,"################# DEVICES IS NULL! FAIL!");
            throw new NullPointerException("DEVICE IS NULL");
        }


        NonZslManualMode = Device.getNonZslManualMode();
        opcode = Device.getOpCodeParameter();
        Denoise = Device.getDenoiseParameter();
        LensFilter = Device.getLensFilter();
        NightMode = Device.getNightMode();

        ManualShutter = Device.getExposureTimeParameter();


        ManualIso = Device.getIsoParameter();
        CCT = Device.getCCTParameter();



        DigitalImageStabilization = Device.getDigitalImageStabilisation();
        HDRMode = Device.getHDRMode();
        VideoStabilization = Device.getVideoStabilisation();

        Module = new ModuleParameters(cameraUiWrapper, appSettingsManager);


        //set last used settings
        SetAppSettingsToParameters();
        /*SetParametersToCamera(cameraParameters);*/

        cameraUiWrapper.GetModuleHandler().SetModule(appSettingsManager.GetCurrentModule());
    }

    @Override
    public void SetMeterAREA(final FocusRect meteringAreas)
    {
        if(appSettingsManager.getDevice() == Devices.ZTE_ADV || appSettingsManager.getDevice() == Devices.ZTEADV234 || appSettingsManager.getDevice() == Devices.ZTEADVIMX214)
        {
            cameraParameters.set("touch-aec","on");
            cameraParameters.set("selectable-zone-af","spot-metering");
            cameraParameters.set("raw-size","4208x3120");
            cameraParameters.set("touch-index-aec", meteringAreas.x + "," + meteringAreas.y);
            SetParametersToCamera(cameraParameters);
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

    public static Rect viewToCameraArea(Rect v,int xPrev,int yPrev)
    {

        Rect rect = new Rect();

        rect.left = v.left * 2000 / xPrev  - 1000;
        rect.top = v.top * 2000/ yPrev - 1000;
        rect.right = v.right * 2000/xPrev -1000;
        rect.bottom = v.bottom * 2000/yPrev -1000;
        return rect;
    }

    @Override
    public void SetPictureOrientation(int orientation)
    {
        if (appSettingsManager.getApiString(AppSettingsManager.SETTING_OrientationHack).equals(KEYS.ON))
        {
            int or = orientation +180;
            if (or >360)
                or = or - 360;
            orientation = or;
        }

        cameraParameters.setRotation(orientation);
        SetParametersToCamera(cameraParameters);

    }

    @Override
    public float[] getFocusDistances() {
        float focusdistance[] = new float[3];
        ((CameraHolder)cameraUiWrapper.GetCameraHolder()).GetCameraParameters().getFocusDistances(focusdistance);
        return focusdistance;
    }

    public void SetCameraRotation()
    {
        if (appSettingsManager.getApiString(AppSettingsManager.SETTING_OrientationHack).equals(""))
        {
            appSettingsManager.setApiString(AppSettingsManager.SETTING_OrientationHack , KEYS.OFF);
        }
        if (appSettingsManager.getApiString(AppSettingsManager.SETTING_OrientationHack).equals(KEYS.OFF))
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
            Log.e(TAG,e.getMessage());
        }
    }



    public void SetZTE_AE()
    {
        cameraParameters.set("slow_shutter", "-1");
        //cameraParameters.set("slow_shutter_addition", "0");
        SetParametersToCamera(cameraParameters);


    }

    public void SetZTE_RESET_AE_SETSHUTTER(String Shutter)
    {
        SetZTE_AE();
        cameraUiWrapper.StopPreview();
        cameraUiWrapper.StartPreview();
        cameraParameters.set("slow_shutter",Shutter);
        cameraParameters.set("slow_shutter_addition", "1");
        SetParametersToCamera(cameraParameters);


    }
}
