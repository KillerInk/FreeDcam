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
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModuleParameters;
import freed.cam.apis.camera1.Camera1Fragment;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.FocusHandler;
import freed.cam.apis.camera1.parameters.manual.AE_Handler_Abstract;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.cam.apis.camera1.parameters.manual.ExposureManualParameter;
import freed.cam.apis.camera1.parameters.manual.ManualIsoSony;
import freed.cam.apis.camera1.parameters.manual.ZoomManualParameter;
import freed.cam.apis.camera1.parameters.manual.focus.BaseFocusManual;
import freed.cam.apis.camera1.parameters.manual.focus.FocusManualHuawei;
import freed.cam.apis.camera1.parameters.manual.focus.FocusManualParameterHTC;
import freed.cam.apis.camera1.parameters.manual.krilin.ManualAperture;
import freed.cam.apis.camera1.parameters.manual.krilin.ManualIsoKrilin;
import freed.cam.apis.camera1.parameters.manual.krilin.ShutterManualKrilin;
import freed.cam.apis.camera1.parameters.manual.lg.AE_Handler_LGG4;
import freed.cam.apis.camera1.parameters.manual.mtk.AE_Handler_MTK;
import freed.cam.apis.camera1.parameters.manual.mtk.FocusManualMTK;
import freed.cam.apis.camera1.parameters.manual.qcom.BaseISOManual;
import freed.cam.apis.camera1.parameters.manual.qcom.BurstManualParam;
import freed.cam.apis.camera1.parameters.manual.shutter.ExposureTime_MicroSec;
import freed.cam.apis.camera1.parameters.manual.shutter.ExposureTime_MilliSec;
import freed.cam.apis.camera1.parameters.manual.shutter.ShutterManualG2pro;
import freed.cam.apis.camera1.parameters.manual.shutter.ShutterManualMeizu;
import freed.cam.apis.camera1.parameters.manual.shutter.ShutterManualParameterHTC;
import freed.cam.apis.camera1.parameters.manual.shutter.ShutterManualSony;
import freed.cam.apis.camera1.parameters.manual.shutter.ShutterManualZTE;
import freed.cam.apis.camera1.parameters.manual.whitebalance.BaseCCTManual;
import freed.cam.apis.camera1.parameters.manual.zte.FXManualParameter;
import freed.cam.apis.camera1.parameters.modes.AutoHdrMode;
import freed.cam.apis.camera1.parameters.modes.BaseModeParameter;
import freed.cam.apis.camera1.parameters.modes.ExposureLockParameter;
import freed.cam.apis.camera1.parameters.modes.FocusPeakModeParameter;
import freed.cam.apis.camera1.parameters.modes.LgHdrMode;
import freed.cam.apis.camera1.parameters.modes.MotoHDR;
import freed.cam.apis.camera1.parameters.modes.NightModeZTE;
import freed.cam.apis.camera1.parameters.modes.OpCodeParameter;
import freed.cam.apis.camera1.parameters.modes.PictureFormatHandler;
import freed.cam.apis.camera1.parameters.modes.PictureSizeParameter;
import freed.cam.apis.camera1.parameters.modes.PreviewFpsParameter;
import freed.cam.apis.camera1.parameters.modes.PreviewSizeParameter;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.cam.apis.camera1.parameters.modes.VirtualLensFilter;
import freed.settings.AppSettingsManager;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.StringUtils.FileEnding;

import static freed.settings.AppSettingsManager.FRAMEWORK_LG;
import static freed.settings.AppSettingsManager.FRAMEWORK_MTK;
import static freed.settings.AppSettingsManager.ISOMANUAL_KRILLIN;
import static freed.settings.AppSettingsManager.ISOMANUAL_MTK;
import static freed.settings.AppSettingsManager.ISOMANUAL_QCOM;
import static freed.settings.AppSettingsManager.ISOMANUAL_SONY;
import static freed.settings.AppSettingsManager.SHUTTER_G2PRO;
import static freed.settings.AppSettingsManager.SHUTTER_HTC;
import static freed.settings.AppSettingsManager.SHUTTER_KRILLIN;
import static freed.settings.AppSettingsManager.SHUTTER_LG;
import static freed.settings.AppSettingsManager.SHUTTER_MEIZU;
import static freed.settings.AppSettingsManager.SHUTTER_MTK;
import static freed.settings.AppSettingsManager.SHUTTER_QCOM_MICORSEC;
import static freed.settings.AppSettingsManager.SHUTTER_QCOM_MILLISEC;
import static freed.settings.AppSettingsManager.SHUTTER_SONY;
import static freed.settings.AppSettingsManager.SHUTTER_ZTE;

/**
 * Created by troop on 17.08.2014.
 * this class handels all camera1 releated parameters.
 */
public class ParametersHandler extends AbstractParameterHandler
{

    private final String TAG = ParametersHandler.class.getSimpleName();

    private Parameters cameraParameters;
    private AE_Handler_Abstract aehandler;
    public Parameters getParameters(){return cameraParameters;}

    public ParametersHandler(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
    }

    public void SetParametersToCamera(Parameters params)
    {
        Log.d(TAG, "SetParametersToCam");
        ((CameraHolder) cameraUiWrapper.getCameraHolder()).SetCameraParameters(params);
    }

    @Override
    protected void SetParameters() {
        SetParametersToCamera(cameraParameters);
    }

    public void LoadParametersFromCamera()
    {
        cameraParameters = ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCameraParameters();
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
        add(freed.cam.apis.basecamera.parameters.Parameters.PictureFormat, new PictureFormatHandler(cameraParameters, cameraUiWrapper, this));
        if (AppSettingsManager.getInstance().getDngProfilesMap()!= null && AppSettingsManager.getInstance().getDngProfilesMap().size() > 0 && AppSettingsManager.getInstance().rawPictureFormat.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.opcode, new OpCodeParameter());
        cameraUiWrapper.getModuleHandler().addListner((ModuleChangedEvent) get(freed.cam.apis.basecamera.parameters.Parameters.PictureFormat));
        AppSettingsManager appS = AppSettingsManager.getInstance();
        if (appS.pictureSize.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.PictureSize ,new PictureSizeParameter(cameraParameters, cameraUiWrapper));

        if (appS.focusMode.isSupported()) {
            add(freed.cam.apis.basecamera.parameters.Parameters.FocusMode,new BaseModeParameter(cameraParameters, cameraUiWrapper,appS.focusMode));
            get(freed.cam.apis.basecamera.parameters.Parameters.FocusMode).addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).focusModeListner);
        }

        if (appS.whiteBalanceMode.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.WhiteBalanceMode, new BaseModeParameter(cameraParameters, cameraUiWrapper,appS.whiteBalanceMode));

        if (appS.exposureMode.isSupported()) {
            add(freed.cam.apis.basecamera.parameters.Parameters.ExposureMode,new BaseModeParameter(cameraParameters, cameraUiWrapper, appS.exposureMode));
            get(freed.cam.apis.basecamera.parameters.Parameters.ExposureMode).addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).aeModeListner);
        }

        if (appS.colorMode.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.ColorMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.colorMode));

        if (appS.flashMode.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.FlashMode, new BaseModeParameter(cameraParameters,cameraUiWrapper, appS.flashMode));

        if (appS.isoMode.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.IsoMode ,new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.isoMode));

        if (appS.antiBandingMode.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.AntiBandingMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.antiBandingMode));

        if (appS.imagePostProcessing.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.ImagePostProcessing, new BaseModeParameter(cameraParameters, cameraUiWrapper, appS.imagePostProcessing));

        if (appS.jpegQuality.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.JpegQuality, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.jpegQuality));

        if (appS.aeBracket.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.AE_Bracket, new BaseModeParameter(cameraParameters, cameraUiWrapper, appS.aeBracket));

        if (appS.previewSize.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.PreviewSize,  new PreviewSizeParameter(cameraParameters,cameraUiWrapper,appS.previewSize));

        if (appS.previewFps.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.PreviewFPS, new PreviewFpsParameter(cameraParameters,cameraUiWrapper,appS.previewFps));

        if (appS.previewFpsRange.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.PreviewFPS, new BaseModeParameter(cameraParameters, cameraUiWrapper, appS.previewFpsRange));

        if (appS.previewFormat.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.PreviewFormat,  new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.previewFormat));

        if (appS.sceneMode.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.SceneMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.sceneMode));

        if (appS.redEyeMode.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.RedEye, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.redEyeMode));

        if (appS.lenshade.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.LensShade, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.lenshade));

        if (appS.zeroshutterlag.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.ZSL, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.zeroshutterlag));

        if (appS.sceneDetectMode.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.SceneDetect, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.sceneDetectMode));

        if (appS.memoryColorEnhancement.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.MemoryColorEnhancement, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.memoryColorEnhancement));

        if (appS.videoSize.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.VideoSize, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.videoSize));

        if (appS.correlatedDoubleSampling.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.CDS_Mode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.correlatedDoubleSampling));

        if (appS.opticalImageStabilisation.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.oismode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.opticalImageStabilisation));

        if (appS.videoHDR.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.VideoHDR, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.videoHDR));

        if (appS.videoHFR.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.VideoHighFramerate, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.videoHFR));

        if (appS.nightMode.isSupported()) {
            switch (AppSettingsManager.getInstance().nightMode.getType()) {
                case AppSettingsManager.NIGHTMODE_XIAOMI:
                    //NightMode = new NightModeXiaomi(cameraParameters, cameraUiWrapper);
                    break;
                case AppSettingsManager.NIGHTMODE_ZTE:
                    add(freed.cam.apis.basecamera.parameters.Parameters.NightMode, new NightModeZTE(cameraParameters, cameraUiWrapper));
                    break;
            }
        }
        
        if (appS.hdrMode.isSupported()){
            switch (appS.hdrMode.getType())
            {
                case AppSettingsManager.HDR_MORPHO:
                    //HDRMode = new MorphoHdrModeParameters(cameraParameters,cameraUiWrapper,appS.hdrMode);
                    break;
                case AppSettingsManager.HDR_AUTO:
                    add(freed.cam.apis.basecamera.parameters.Parameters.HDRMode, new AutoHdrMode(cameraParameters,cameraUiWrapper,appS.hdrMode));
                    break;
                case AppSettingsManager.HDR_LG:
                    add(freed.cam.apis.basecamera.parameters.Parameters.HDRMode,new LgHdrMode(cameraParameters,cameraUiWrapper,appS.hdrMode));
                    break;
                case AppSettingsManager.HDR_MOTO:
                    add(freed.cam.apis.basecamera.parameters.Parameters.HDRMode, new MotoHDR(cameraParameters,cameraUiWrapper,appS.hdrMode));
                    break;
            }
        }

        if (AppSettingsManager.getInstance().getDngProfilesMap() != null && appS.getDngProfilesMap().size() > 0 && appS.rawPictureFormat.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.matrixChooser, new MatrixChooserParameter(AppSettingsManager.getInstance().getMatrixesMap()));

        if(appS.denoiseMode.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.Denoise, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.denoiseMode));

        if(appS.nonZslManualMode.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.NonZslManualMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.nonZslManualMode));

        if (appS.digitalImageStabilisationMode.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.DigitalImageStabilization, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.digitalImageStabilisationMode));

        if (appS.temporal_nr.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.TNR, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.temporal_nr));
        if (appS.temporal_video_nr.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.TNR_V, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.temporal_video_nr));
        if (appS.pdafcontrol.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.PDAF, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.pdafcontrol));
        if (appS.seemore_tonemap.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.SeeMore, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.seemore_tonemap));
        if (appS.truepotrait.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.TruePotrait, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.truepotrait));
        if (appS.refocus.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.ReFocus, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.refocus));
        if (appS.optizoom.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.OptiZoom, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.optizoom));
        if (appS.rawdumpinterface.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.RDI, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.rawdumpinterface));
        if (appS.chromaflash.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.ChromaFlash, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.chromaflash));

        add(freed.cam.apis.basecamera.parameters.Parameters.VideoProfiles, new VideoProfilesParameter(cameraUiWrapper));

        add(freed.cam.apis.basecamera.parameters.Parameters.ExposureLock, new ExposureLockParameter(cameraParameters, cameraUiWrapper));

        add(freed.cam.apis.basecamera.parameters.Parameters.Focuspeak, new FocusPeakModeParameter(cameraUiWrapper,((Camera1Fragment) cameraUiWrapper).focusPeakProcessorAp1));

        SetCameraRotation();

        SetPictureOrientation(0);

        add(freed.cam.apis.basecamera.parameters.Parameters.Module, new ModuleParameters(cameraUiWrapper));

        /*
        MANUALSTUFF
         */

        if (AppSettingsManager.getInstance().manualFocus.isSupported())
        {
            if (AppSettingsManager.getInstance().getFrameWork() == FRAMEWORK_MTK)
            {
                add(freed.cam.apis.basecamera.parameters.Parameters.M_Focus, new FocusManualMTK(cameraParameters, cameraUiWrapper,appS.manualFocus));
            }
            else
            {
                //htc mf
                if (AppSettingsManager.getInstance().manualFocus.getKEY().equals(cameraUiWrapper.getResString(R.string.focus)))
                     add(freed.cam.apis.basecamera.parameters.Parameters.M_Focus, new FocusManualParameterHTC(cameraParameters,cameraUiWrapper));
                    //huawai mf
                else if (appS.manualFocus.getKEY().equals(AppSettingsManager.getInstance().getResString(R.string.hw_manual_focus_step_value)))
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_Focus, new FocusManualHuawei(cameraParameters, cameraUiWrapper, appS.manualFocus));
                    //qcom
                else
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_Focus, new BaseFocusManual(cameraParameters,cameraUiWrapper,appS.manualFocus));
            }

        }

        if (appS.manualSaturation.isSupported()) {
            add(freed.cam.apis.basecamera.parameters.Parameters.M_Saturation, new BaseManualParameter(cameraParameters, cameraUiWrapper, appS.manualSaturation));
        }

        if (appS.manualSharpness.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.M_Sharpness, new BaseManualParameter(cameraParameters,cameraUiWrapper,appS.manualSharpness));

        if (appS.manualBrightness.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.M_Brightness, new BaseManualParameter(cameraParameters,cameraUiWrapper,appS.manualBrightness));

        if(appS.manualContrast.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.M_Contrast, new BaseManualParameter(cameraParameters,cameraUiWrapper,appS.manualContrast));


        if (appS.virtualLensfilter.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.LensFilter, new VirtualLensFilter(cameraParameters,cameraUiWrapper));

        if (appS.getFrameWork() == FRAMEWORK_LG)//its needed else cam ignores manuals like shutter and iso
            cameraParameters.set("lge-camera","1");
        else  if (appS.getFrameWork() == FRAMEWORK_MTK){
            cameraParameters.set("afeng_raw_dump_flag", "1");
            cameraParameters.set("rawsave-mode", "2");
            cameraParameters.set("isp-mode", "1");
            cameraParameters.set("rawfname", StringUtils.GetInternalSDCARD()+"/DCIM/test."+ FileEnding.BAYER);
        }

        if (appS.manualExposureTime.isSupported())
        {
            int type = appS.manualExposureTime.getType();
            switch (type)
            {
                case SHUTTER_HTC:
                    //HTCVideoMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "video-mode", "video-hfr-values");
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ExposureTime, new ShutterManualParameterHTC(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_QCOM_MICORSEC:
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ExposureTime, new ExposureTime_MicroSec(cameraUiWrapper,cameraParameters));
                    break;
                case SHUTTER_QCOM_MILLISEC:
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ExposureTime, new ExposureTime_MilliSec(cameraUiWrapper,cameraParameters));
                    break;
                case SHUTTER_MTK:

                    aehandler = new AE_Handler_MTK(cameraParameters,cameraUiWrapper,1600);
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ExposureTime, aehandler.getShutterManual());
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ManualIso, aehandler.getManualIso());
                    break;
                case SHUTTER_LG:
                    aehandler = new AE_Handler_LGG4(cameraParameters,cameraUiWrapper);
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ExposureTime, aehandler.getShutterManual());
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ManualIso, aehandler.getManualIso());
                    break;
                case SHUTTER_MEIZU:
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ExposureTime, new ShutterManualMeizu(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_KRILLIN:
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ExposureTime, new ShutterManualKrilin(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_SONY:
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ExposureTime, new ShutterManualSony(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_G2PRO:
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ExposureTime, new ShutterManualG2pro(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_ZTE:
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ExposureTime, new ShutterManualZTE(cameraParameters,cameraUiWrapper));
            }

        }

        //mtk and g4 aehandler set it already
        Log.d(TAG, "manual Iso supported:" + appS.manualIso.isSupported());
        if (appS.manualIso.isSupported() && aehandler == null && appS.manualIso.getValues() != null && appS.manualIso.getValues().length > 0)
        {
            switch (appS.manualIso.getType())
            {
                case ISOMANUAL_QCOM:
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ManualIso, new BaseISOManual(cameraParameters,cameraUiWrapper));
                    break;
                case ISOMANUAL_SONY:
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ManualIso, new ManualIsoSony(cameraUiWrapper,cameraParameters));
                    break;
                case ISOMANUAL_KRILLIN:
                    add(freed.cam.apis.basecamera.parameters.Parameters.M_ManualIso,  new ManualIsoKrilin(cameraParameters,cameraUiWrapper));
                    break;
                case ISOMANUAL_MTK: //get set due aehandler
                    break;

            }
        }

        if (appS.manualAperture.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.M_Fnumber, new ManualAperture(cameraUiWrapper,cameraParameters));

        if (appS.manualWhiteBalance.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.M_Whitebalance, new BaseCCTManual(cameraParameters,cameraUiWrapper));

        add(freed.cam.apis.basecamera.parameters.Parameters.M_3D_Convergence, new BaseManualParameter(cameraParameters,
                cameraUiWrapper.getResString(R.string.manual_convergence),
                cameraUiWrapper.getResString(R.string.supported_manual_convergence_max),
                cameraUiWrapper.getResString(R.string.supported_manual_convergence_min),
                cameraUiWrapper,1));

        add(freed.cam.apis.basecamera.parameters.Parameters.M_ExposureCompensation, new ExposureManualParameter(cameraParameters, cameraUiWrapper,1));

        add(freed.cam.apis.basecamera.parameters.Parameters.M_FX, new FXManualParameter(cameraParameters, cameraUiWrapper));
        get(freed.cam.apis.basecamera.parameters.Parameters.PictureFormat).addEventListner(((BaseManualParameter) get(freed.cam.apis.basecamera.parameters.Parameters.M_FX)).GetPicFormatListner());
        cameraUiWrapper.getModuleHandler().addListner(((BaseManualParameter)get(freed.cam.apis.basecamera.parameters.Parameters.M_FX)).GetModuleListner());

        if (appS.manualBurst.isSupported()){
            add(freed.cam.apis.basecamera.parameters.Parameters.M_Burst, new BurstManualParam(cameraParameters, cameraUiWrapper));
            cameraUiWrapper.getModuleHandler().addListner(((BaseManualParameter) get(freed.cam.apis.basecamera.parameters.Parameters.M_Burst)).GetModuleListner());
        }

        add(freed.cam.apis.basecamera.parameters.Parameters.M_Zoom, new ZoomManualParameter(cameraParameters, cameraUiWrapper));

        if (appS.dualPrimaryCameraMode.isSupported())
            add(freed.cam.apis.basecamera.parameters.Parameters.dualPrimaryCameraMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.dualPrimaryCameraMode));


        //set last used settings
        SetAppSettingsToParameters();

        cameraUiWrapper.getModuleHandler().setModule(AppSettingsManager.getInstance().GetCurrentModule());
    }

    @Override
    public void SetFocusAREA(Rect focusAreas)
    {
        if (AppSettingsManager.getInstance().qcomAFocus.getBoolean())
            setQcomFocus(focusAreas);
        else
            setAndroidFocus(focusAreas);
    }

    private void setQcomFocus(Rect focusRect)
    {
        cameraParameters.set("touch-aec", "on");
        cameraParameters.set("touch-index-af", focusRect.centerX() + "," +focusRect.centerY());
        Log.d(TAG,"setQcomFocus");
        SetParametersToCamera(cameraParameters);
    }

    private void setAndroidFocus(Rect focusAreas)
    {
        if (focusAreas != null) {
            List<Camera.Area> l = new ArrayList<>();
            l.add(new Camera.Area(focusAreas, 1000));
            cameraParameters.setFocusAreas(l);
        }
        else
            cameraParameters.setFocusAreas(null);
        SetParametersToCamera(cameraParameters);
    }

    @Override
    public void SetPictureOrientation(int orientation)
    {
        if (AppSettingsManager.getInstance().orientationhack.getBoolean())
        {
            int or = orientation +180;
            if (or >360)
                or = or - 360;
            orientation = or;
        }

        cameraParameters.setRotation(orientation);
        Log.d(TAG, "SetPictureOrientation");
        SetParametersToCamera(cameraParameters);

    }

    @Override
    public float[] getFocusDistances() {
        float focusdistance[] = new float[3];
        ((CameraHolder)cameraUiWrapper.getCameraHolder()).GetCameraParameters().getFocusDistances(focusdistance);
        return focusdistance;
    }

    @Override
    public float getCurrentExposuretime()
    {
        Camera.Parameters parameters = ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCameraParameters();
        if (AppSettingsManager.getInstance().getFrameWork() == AppSettingsManager.FRAMEWORK_MTK) {
            if (parameters.get(AppSettingsManager.getInstance().getResString(R.string.eng_capture_shutter_speed)) != null) {
                if (Float.parseFloat(parameters.get(AppSettingsManager.getInstance().getResString(R.string.eng_capture_shutter_speed))) == 0) {
                    return 0;
                } else
                    return Float.parseFloat(parameters.get(AppSettingsManager.getInstance().getResString(R.string.eng_capture_shutter_speed)))/ 1000000;
            } else if (parameters.get(AppSettingsManager.getInstance().getResString(R.string.cap_ss)) != null) {
                if (Float.parseFloat(parameters.get(AppSettingsManager.getInstance().getResString(R.string.cap_ss))) == 0) {
                    return 0;
                } else
                    return Float.parseFloat(parameters.get(AppSettingsManager.getInstance().getResString(R.string.cap_ss)))/ 1000000;
            } else
                return 0;
        }
        else
        {
            if (parameters.get(cameraUiWrapper.getResString(R.string.cur_exposure_time))!= null)
                return Float.parseFloat(parameters.get(cameraUiWrapper.getResString(R.string.cur_exposure_time)))*1000000;
        }
        return 0;
    }

    @Override
    public int getCurrentIso() {
        Camera.Parameters parameters = ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCameraParameters();
        if (AppSettingsManager.getInstance().getFrameWork() == FRAMEWORK_MTK)
        {
            if(parameters.get(AppSettingsManager.getInstance().getResString(R.string.eng_capture_sensor_gain))!= null) {
                if (Integer.parseInt(parameters.get(AppSettingsManager.getInstance().getResString(R.string.eng_capture_sensor_gain))) == 0) {
                    return 0;
                }
                return Integer.parseInt(parameters.get(AppSettingsManager.getInstance().getResString(R.string.eng_capture_sensor_gain))) / 256 * 100;
            }
            else if(parameters.get(AppSettingsManager.getInstance().getResString(R.string.cap_isp_g))!= null)
            {
                if (Integer.parseInt(parameters.get(AppSettingsManager.getInstance().getResString(R.string.cap_isp_g))) == 0) {
                    return 0;
                }
                return Integer.parseInt(parameters.get(AppSettingsManager.getInstance().getResString(R.string.cap_isp_g))) / 256 * 100;
            }
            else
                return 0;
        }
        else
        {
            if (parameters.get(cameraUiWrapper.getResString(R.string.cur_iso))!= null)
                return Integer.parseInt(parameters.get(cameraUiWrapper.getResString(R.string.cur_iso)));
        }
        return 0;
    }

    public float getFnumber()
    {
        if (cameraParameters.get("f-number")!= null) {
            return Float.parseFloat(cameraParameters.get("f-number"));
        }
        else
            return 2.0f;
    }

    public float getFocal()
    {
        return cameraParameters.getFocalLength();
    }

    public void SetCameraRotation()
    {

        if (!AppSettingsManager.getInstance().orientationhack.getBoolean())
            ((CameraHolder) cameraUiWrapper.getCameraHolder()).SetCameraRotation(0);
        else
            ((CameraHolder) cameraUiWrapper.getCameraHolder()).SetCameraRotation(180);
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

    public void Set_RAWFNAME(String filename)
    {
        cameraParameters.set("rawfname", filename);
        SetParametersToCamera(cameraParameters);
    }



    public void SetZTE_AE()
    {
        cameraParameters.set("slow_shutter", "-1");
        //cameraParameters.set("slow_shutter_addition", "0");
        Log.d(TAG,"SetZte_Ae");
        SetParametersToCamera(cameraParameters);


    }

    public void SetZTE_RESET_AE_SETSHUTTER(String Shutter)
    {
        SetZTE_AE();
        cameraUiWrapper.stopPreview();
        cameraUiWrapper.startPreview();
        cameraParameters.set("slow_shutter",Shutter);
        cameraParameters.set("slow_shutter_addition", "1");
        Log.d(TAG,"SetZTE_RESET_AE_SETSHUTTER");
        SetParametersToCamera(cameraParameters);


    }
}
