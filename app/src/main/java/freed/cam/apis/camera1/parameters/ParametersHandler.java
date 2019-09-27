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
import freed.cam.apis.basecamera.parameters.modes.VideoAudioSourceMode;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.FocusHandler;
import freed.cam.apis.camera1.parameters.ae.AeManagerLgCamera1;
import freed.cam.apis.camera1.parameters.ae.AeManagerMtkCamera1;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.cam.apis.camera1.parameters.manual.ExposureManualParameter;
import freed.cam.apis.camera1.parameters.manual.ManualIsoSony;
import freed.cam.apis.camera1.parameters.manual.ZoomManualParameter;
import freed.cam.apis.camera1.parameters.manual.focus.BaseFocusManual;
import freed.cam.apis.camera1.parameters.manual.focus.FocusManualHuawei;
import freed.cam.apis.camera1.parameters.manual.focus.FocusManualParameterHTC;
import freed.cam.apis.camera1.parameters.manual.kirin.ManualAperture;
import freed.cam.apis.camera1.parameters.manual.kirin.ManualIsoKirin;
import freed.cam.apis.camera1.parameters.manual.kirin.ShutterManualKirin;
import freed.cam.apis.camera1.parameters.manual.mtk.FocusManualMTK;
import freed.cam.apis.camera1.parameters.manual.qcom.BaseISOManual;
import freed.cam.apis.camera1.parameters.manual.qcom.BurstManualParam;
import freed.cam.apis.camera1.parameters.manual.shutter.ExposureTime_MS;
import freed.cam.apis.camera1.parameters.manual.shutter.ExposureTime_MicroSec;
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
import freed.cam.apis.camera1.parameters.modes.LegacyMode;
import freed.cam.apis.camera1.parameters.modes.LgHdrMode;
import freed.cam.apis.camera1.parameters.modes.MotoHDR;
import freed.cam.apis.camera1.parameters.modes.NightModeZTE;
import freed.cam.apis.camera1.parameters.modes.OpCodeParameter;
import freed.cam.apis.camera1.parameters.modes.PictureFormatHandler;
import freed.cam.apis.camera1.parameters.modes.PreviewFpsParameter;
import freed.cam.apis.camera1.parameters.modes.PreviewSizeParameter;
import freed.cam.apis.camera1.parameters.modes.VideoProfilesParameter;
import freed.cam.apis.camera1.parameters.modes.VideoStabilizationParameter;
import freed.cam.apis.camera1.parameters.modes.VirtualLensFilter;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.StringUtils.FileEnding;

import static freed.settings.SettingsManager.ISOMANUAL_KRILLIN;
import static freed.settings.SettingsManager.ISOMANUAL_MTK;
import static freed.settings.SettingsManager.ISOMANUAL_QCOM;
import static freed.settings.SettingsManager.ISOMANUAL_SONY;
import static freed.settings.SettingsManager.ISOMANUAL_Xiaomi;
import static freed.settings.SettingsManager.SHUTTER_G2PRO;
import static freed.settings.SettingsManager.SHUTTER_HTC;
import static freed.settings.SettingsManager.SHUTTER_KRILLIN;
import static freed.settings.SettingsManager.SHUTTER_LG;
import static freed.settings.SettingsManager.SHUTTER_MEIZU;
import static freed.settings.SettingsManager.SHUTTER_MTK;
import static freed.settings.SettingsManager.SHUTTER_QCOM_MICORSEC;
import static freed.settings.SettingsManager.SHUTTER_QCOM_MILLISEC;
import static freed.settings.SettingsManager.SHUTTER_SONY;
import static freed.settings.SettingsManager.SHUTTER_ZTE;

/**
 * Created by troop on 17.08.2014.
 * this class handels all camera1 releated parameters.
 */
public class ParametersHandler extends AbstractParameterHandler
{

    private final String TAG = ParametersHandler.class.getSimpleName();

    private Parameters cameraParameters;
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
    public void SetParameters() {
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
        if (parameters != null) {
            try {
                String[] split = parameters.flatten().split(";");
                for (String e : split) {
                    Log.d(TAG, e);
                }
            }
            catch (NullPointerException ex)
            {
                Log.WriteEx(ex);
            }

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
        add(SettingKeys.PictureFormat, new PictureFormatHandler(cameraParameters, cameraUiWrapper, this));
        if (SettingsManager.getInstance().getDngProfilesMap()!= null
                && SettingsManager.getInstance().getDngProfilesMap().size() > 0
                && SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).isSupported())
            add(SettingKeys.OPCODE, new OpCodeParameter());

        if (SettingsManager.get(SettingKeys.PictureSize).isSupported())
            add(SettingKeys.PictureSize ,new BaseModeParameter(cameraParameters, cameraUiWrapper,SettingKeys.PictureSize));

        if (SettingsManager.get(SettingKeys.FocusMode).isSupported()) {
            add(SettingKeys.FocusMode,new BaseModeParameter(cameraParameters, cameraUiWrapper,SettingKeys.FocusMode));
            ((FocusHandler) cameraUiWrapper.getFocusHandler()).startListning();

        }

        if (SettingsManager.get(SettingKeys.WhiteBalanceMode).isSupported())
            add(SettingKeys.WhiteBalanceMode, new BaseModeParameter(cameraParameters, cameraUiWrapper,SettingKeys.WhiteBalanceMode));

        if (SettingsManager.get(SettingKeys.ExposureMode).isSupported()) {
            add(SettingKeys.ExposureMode,new BaseModeParameter(cameraParameters, cameraUiWrapper, SettingKeys.ExposureMode));
        }

        if (SettingsManager.get(SettingKeys.ColorMode).isSupported())
            add(SettingKeys.ColorMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.ColorMode));

        if (SettingsManager.get(SettingKeys.FlashMode).isSupported())
            add(SettingKeys.FlashMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.FlashMode));

        if (SettingsManager.get(SettingKeys.IsoMode).isSupported())
            add(SettingKeys.IsoMode ,new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.IsoMode));

        if (SettingsManager.get(SettingKeys.AntiBandingMode).isSupported())
            add(SettingKeys.AntiBandingMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.AntiBandingMode));

        if (SettingsManager.get(SettingKeys.ImagePostProcessing).isSupported())
            add(SettingKeys.ImagePostProcessing, new BaseModeParameter(cameraParameters, cameraUiWrapper, SettingKeys.ImagePostProcessing));

        if (SettingsManager.get(SettingKeys.JpegQuality).isSupported())
            add(SettingKeys.JpegQuality, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.JpegQuality));

        if (SettingsManager.get(SettingKeys.AE_Bracket).isSupported())
            add(SettingKeys.AE_Bracket, new BaseModeParameter(cameraParameters, cameraUiWrapper, SettingKeys.AE_Bracket));

        if (SettingsManager.get(SettingKeys.PreviewSize).isSupported())
            add(SettingKeys.PreviewSize,  new PreviewSizeParameter(cameraParameters,cameraUiWrapper,SettingKeys.PreviewSize));

        if (SettingsManager.get(SettingKeys.PreviewFPS).isSupported())
            add(SettingKeys.PreviewFPS, new PreviewFpsParameter(cameraParameters,cameraUiWrapper,SettingKeys.PreviewFPS));

        if (SettingsManager.get(SettingKeys.PreviewFpsRange).isSupported())
            add(SettingKeys.PreviewFpsRange, new BaseModeParameter(cameraParameters, cameraUiWrapper, SettingKeys.PreviewFpsRange));

        if (SettingsManager.get(SettingKeys.PreviewFormat).isSupported())
            add(SettingKeys.PreviewFormat,  new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.PreviewFormat));

        if (SettingsManager.get(SettingKeys.SceneMode).isSupported())
            add(SettingKeys.SceneMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.SceneMode));

        if (SettingsManager.get(SettingKeys.RedEye).isSupported())
            add(SettingKeys.RedEye, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.RedEye));

        if (SettingsManager.get(SettingKeys.LensShade).isSupported())
            add(SettingKeys.LensShade, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.LensShade));

        if (SettingsManager.get(SettingKeys.ZSL).isSupported())
            add(SettingKeys.ZSL, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.ZSL));

        if (SettingsManager.get(SettingKeys.SceneDetect).isSupported())
            add(SettingKeys.SceneDetect, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.SceneDetect));

        if (SettingsManager.get(SettingKeys.MemoryColorEnhancement).isSupported())
            add(SettingKeys.MemoryColorEnhancement, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.MemoryColorEnhancement));

        if (SettingsManager.get(SettingKeys.VideoSize).isSupported())
            add(SettingKeys.VideoSize, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.VideoSize));

        if (SettingsManager.get(SettingKeys.CDS_Mode).isSupported())
            add(SettingKeys.CDS_Mode, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.CDS_Mode));

        if (SettingsManager.get(SettingKeys.OIS_MODE).isSupported())
            add(SettingKeys.OIS_MODE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.OIS_MODE));

        if (SettingsManager.get(SettingKeys.VideoHDR).isSupported())
            add(SettingKeys.VideoHDR, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.VideoHDR));

        if (SettingsManager.get(SettingKeys.VideoHighFramerate).isSupported())
            add(SettingKeys.VideoHighFramerate, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.VideoHighFramerate));

        if(SettingsManager.get(SettingKeys.VideoStabilization).isSupported())
            add(SettingKeys.VideoStabilization, new VideoStabilizationParameter(cameraParameters,cameraUiWrapper));

        if (SettingsManager.get(SettingKeys.NightMode).isSupported()) {
            switch (SettingsManager.get(SettingKeys.NightMode).getType()) {
                case SettingsManager.NIGHTMODE_XIAOMI:
                    //NightMode = new NightModeXiaomi(cameraParameters, cameraUiWrapper);
                    break;
                case SettingsManager.NIGHTMODE_ZTE:
                    add(SettingKeys.NightMode, new NightModeZTE(cameraParameters, cameraUiWrapper));
                    break;
            }
        }
        
        if (SettingsManager.get(SettingKeys.HDRMode).isSupported()){
            switch (SettingsManager.get(SettingKeys.HDRMode).getType())
            {
                case SettingsManager.HDR_MORPHO:
                    //HDRMode = new MorphoHdrModeParameters(cameraParameters,cameraUiWrapper,appS.hdrMode);
                    break;
                case SettingsManager.HDR_AUTO:
                    AutoHdrMode autoHdrMode = new AutoHdrMode(cameraParameters,cameraUiWrapper,SettingKeys.HDRMode);
                    add(SettingKeys.HDRMode, autoHdrMode);
                    break;
                case SettingsManager.HDR_LG:
                    LgHdrMode lgHdrMode = new LgHdrMode(cameraParameters,cameraUiWrapper,SettingKeys.HDRMode);
                    add(SettingKeys.HDRMode,lgHdrMode);
                    break;
                case SettingsManager.HDR_MOTO:
                    MotoHDR motoHDR = new MotoHDR(cameraParameters,cameraUiWrapper,SettingKeys.HDRMode);
                    add(SettingKeys.HDRMode, motoHDR);
                    break;
            }
        }

        if (SettingsManager.getInstance().getDngProfilesMap() != null && SettingsManager.getInstance().getDngProfilesMap().size() > 0 && SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).isSupported())
            add(SettingKeys.MATRIX_SET, new MatrixChooserParameter(SettingsManager.getInstance().getMatrixesMap()));

        if(SettingsManager.get(SettingKeys.Denoise).isSupported())
            add(SettingKeys.Denoise, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.Denoise));

        if(SettingsManager.get(SettingKeys.NonZslManualMode).isSupported())
            add(SettingKeys.NonZslManualMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.NonZslManualMode));

        if (SettingsManager.get(SettingKeys.DigitalImageStabilization).isSupported())
            add(SettingKeys.DigitalImageStabilization, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.DigitalImageStabilization));

        if (SettingsManager.get(SettingKeys.TNR).isSupported())
            add(SettingKeys.TNR, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.TNR));
        if (SettingsManager.get(SettingKeys.TNR_V).isSupported())
            add(SettingKeys.TNR_V, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.TNR_V));
        if (SettingsManager.get(SettingKeys.PDAF).isSupported())
            add(SettingKeys.PDAF, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.PDAF));
        if (SettingsManager.get(SettingKeys.SeeMore).isSupported())
            add(SettingKeys.SeeMore, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.SeeMore));
        if (SettingsManager.get(SettingKeys.TruePotrait).isSupported())
            add(SettingKeys.TruePotrait, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.TruePotrait));
        if (SettingsManager.get(SettingKeys.ReFocus).isSupported())
            add(SettingKeys.ReFocus, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.ReFocus));
        if (SettingsManager.get(SettingKeys.OptiZoom).isSupported())
            add(SettingKeys.OptiZoom, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.OptiZoom));
        if (SettingsManager.get(SettingKeys.RDI).isSupported())
            add(SettingKeys.RDI, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.RDI));
        if (SettingsManager.get(SettingKeys.ChromaFlash).isSupported())
            add(SettingKeys.ChromaFlash, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.ChromaFlash));

        add(SettingKeys.VideoProfiles, new VideoProfilesParameter(cameraUiWrapper));
        add(SettingKeys.VIDEO_AUDIO_SOURCE, new VideoAudioSourceMode(cameraUiWrapper,SettingKeys.VIDEO_AUDIO_SOURCE));

        add(SettingKeys.ExposureLock, new ExposureLockParameter(cameraParameters, cameraUiWrapper));



        SetCameraRotation();

        SetPictureOrientation(0);

        add(SettingKeys.Module, new ModuleParameters(cameraUiWrapper));

        /*
        MANUALSTUFF
         */

        if (SettingsManager.get(SettingKeys.M_Focus).isSupported())
        {
            if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
            {
                add(SettingKeys.M_Focus, new FocusManualMTK(cameraParameters, cameraUiWrapper,SettingKeys.M_Focus));
            }
            else
            {
                //htc mf
                if (SettingsManager.get(SettingKeys.M_Focus).getKEY().equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.focus)))
                     add(SettingKeys.M_Focus, new FocusManualParameterHTC(cameraParameters,cameraUiWrapper,SettingKeys.M_Focus));
                    //huawai mf
                else if (SettingsManager.get(SettingKeys.M_Focus).getKEY().equals(SettingsManager.getInstance().getResString(R.string.hw_manual_focus_step_value)))
                    add(SettingKeys.M_Focus, new FocusManualHuawei(cameraParameters, cameraUiWrapper, SettingKeys.M_Focus));
                    //qcom
                else
                    add(SettingKeys.M_Focus, new BaseFocusManual(cameraParameters,cameraUiWrapper,SettingKeys.M_Focus));
            }

        }

        if (SettingsManager.get(SettingKeys.M_Saturation).isSupported()) {
            add(SettingKeys.M_Saturation, new BaseManualParameter(cameraParameters, cameraUiWrapper, SettingKeys.M_Saturation));
        }

        if (SettingsManager.get(SettingKeys.M_Sharpness).isSupported())
            add(SettingKeys.M_Sharpness, new BaseManualParameter(cameraParameters,cameraUiWrapper,SettingKeys.M_Sharpness));

        if (SettingsManager.get(SettingKeys.M_Brightness).isSupported())
            add(SettingKeys.M_Brightness, new BaseManualParameter(cameraParameters,cameraUiWrapper,SettingKeys.M_Brightness));

        if(SettingsManager.get(SettingKeys.M_Contrast).isSupported())
            add(SettingKeys.M_Contrast, new BaseManualParameter(cameraParameters,cameraUiWrapper,SettingKeys.M_Contrast));


        if (SettingsManager.get(SettingKeys.LensFilter).isSupported())
            add(SettingKeys.LensFilter, new VirtualLensFilter(cameraParameters,cameraUiWrapper));

        if (SettingsManager.getInstance().getFrameWork() == Frameworks.LG)//its needed else cam ignores manuals like shutter and iso
            cameraParameters.set("lge-camera","1");
        else  if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK){
            cameraParameters.set("afeng_raw_dump_flag", "1");
            cameraParameters.set("rawsave-mode", "2");
            cameraParameters.set("isp-mode", "1");
            cameraParameters.set("rawfname", StringUtils.GetInternalSDCARD()+"/DCIM/test."+ FileEnding.BAYER);
        }

        if (SettingsManager.get(SettingKeys.M_ExposureTime).isSupported())
        {
            int type = SettingsManager.get(SettingKeys.M_ExposureTime).getType();
            switch (type)
            {
                case SHUTTER_HTC:
                    //HTCVideoMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "video-mode", "video-hfr-values");
                    add(SettingKeys.M_ExposureTime, new ShutterManualParameterHTC(cameraParameters,cameraUiWrapper, SettingKeys.M_ExposureTime));
                    break;
                case SHUTTER_QCOM_MILLISEC:
                    add(SettingKeys.M_ExposureTime, new ExposureTime_MS(cameraUiWrapper,cameraParameters,SettingKeys.M_ExposureTime));
                    break;
                case SHUTTER_QCOM_MICORSEC:
                    add(SettingKeys.M_ExposureTime, new ExposureTime_MicroSec(cameraUiWrapper,cameraParameters));
                    break;
                case SHUTTER_MTK:
                    AeManagerMtkCamera1 aeManagerMtkCamera1 = new AeManagerMtkCamera1(cameraUiWrapper,cameraParameters);
                    add(SettingKeys.M_ExposureTime, aeManagerMtkCamera1.getExposureTime());
                    add(SettingKeys.M_ManualIso, aeManagerMtkCamera1.getIso());
                    break;
                case SHUTTER_LG:
                    AeManagerLgCamera1 aeManagerLgCamera1 = new AeManagerLgCamera1(cameraUiWrapper,cameraParameters);
                    add(SettingKeys.M_ExposureTime, aeManagerLgCamera1.getExposureTime());
                    add(SettingKeys.M_ManualIso, aeManagerLgCamera1.getIso());
                    break;
                case SHUTTER_MEIZU:
                    add(SettingKeys.M_ExposureTime, new ShutterManualMeizu(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_KRILLIN:
                    add(SettingKeys.M_ExposureTime, new ShutterManualKirin(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_SONY:
                    add(SettingKeys.M_ExposureTime, new ShutterManualSony(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_G2PRO:
                    add(SettingKeys.M_ExposureTime, new ShutterManualG2pro(cameraParameters,cameraUiWrapper,SettingKeys.M_ExposureTime));
                    break;
                case SHUTTER_ZTE:
                    add(SettingKeys.M_ExposureTime, new ShutterManualZTE(cameraParameters,cameraUiWrapper));
            }

        }

        //mtk and g4 aehandler set it already
        Log.d(TAG, "manual Iso supported:" + SettingsManager.get(SettingKeys.M_ManualIso).isSupported());
        if (SettingsManager.get(SettingKeys.M_ManualIso).isSupported()
                && SettingsManager.get(SettingKeys.M_ManualIso).getValues() != null
                && SettingsManager.get(SettingKeys.M_ManualIso).getValues().length > 0
                && get(SettingKeys.M_ManualIso) == null)
        {
            switch (SettingsManager.get(SettingKeys.M_ManualIso).getType())
            {
                case ISOMANUAL_QCOM:
                    add(SettingKeys.M_ManualIso, new BaseISOManual(cameraParameters,cameraUiWrapper,SettingKeys.M_ManualIso));
                    break;
                case ISOMANUAL_SONY:
                    add(SettingKeys.M_ManualIso, new ManualIsoSony(cameraUiWrapper,cameraParameters,SettingKeys.M_ManualIso));
                    break;
                case ISOMANUAL_KRILLIN:
                    add(SettingKeys.M_ManualIso,  new ManualIsoKirin(cameraParameters,cameraUiWrapper,SettingKeys.M_ManualIso));
                    break;
                case ISOMANUAL_Xiaomi :
                    //not supported
                    break;
                case ISOMANUAL_MTK: //get set due aehandler
                    break;

            }
        }

        if (SettingsManager.get(SettingKeys.M_Aperture).isSupported())
            add(SettingKeys.M_Fnumber, new ManualAperture(cameraUiWrapper,cameraParameters));

        if (SettingsManager.get(SettingKeys.M_Whitebalance).isSupported())
            add(SettingKeys.M_Whitebalance, new BaseCCTManual(cameraParameters,cameraUiWrapper,SettingKeys.M_Whitebalance));

        add(SettingKeys.M_ExposureCompensation, new ExposureManualParameter(cameraParameters, cameraUiWrapper,SettingKeys.M_ExposureCompensation));

        if (SettingsManager.get(SettingKeys.M_FX).isSupported()) {
            add(SettingKeys.M_FX, new FXManualParameter(cameraParameters, cameraUiWrapper,SettingKeys.M_FX));
        }

        if (SettingsManager.get(SettingKeys.M_Burst).isSupported()){
            add(SettingKeys.M_Burst, new BurstManualParam(cameraParameters, cameraUiWrapper,SettingKeys.M_Burst));
        }

        add(SettingKeys.M_Zoom, new ZoomManualParameter(cameraParameters, cameraUiWrapper,SettingKeys.M_Zoom));

        if (SettingsManager.get(SettingKeys.dualPrimaryCameraMode).isSupported())
            add(SettingKeys.dualPrimaryCameraMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.dualPrimaryCameraMode));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            add(SettingKeys.openCamera1Legacy, new LegacyMode(cameraUiWrapper,SettingsManager.get(SettingKeys.openCamera1Legacy)));


        registerListners();
        //set last used settings
        SetAppSettingsToParameters();

        cameraUiWrapper.getModuleHandler().setModule(SettingsManager.getInstance().GetCurrentModule());
    }

    @Override
    public void SetFocusAREA(Rect focusAreas)
    {
        if (SettingsManager.get(SettingKeys.useQcomFocus).get())
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
        if (cameraParameters == null)
            return;
        if (SettingsManager.get(SettingKeys.orientationHack).get() || SettingsManager.getInstance().getIsFrontCamera())
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
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK) {
            if (parameters.get(SettingsManager.getInstance().getResString(R.string.eng_capture_shutter_speed)) != null) {
                if (Float.parseFloat(parameters.get(SettingsManager.getInstance().getResString(R.string.eng_capture_shutter_speed))) == 0) {
                    return 0;
                } else
                    return Float.parseFloat(parameters.get(SettingsManager.getInstance().getResString(R.string.eng_capture_shutter_speed)))/ 1000000;
            } else if (parameters.get(SettingsManager.getInstance().getResString(R.string.cap_ss)) != null) {
                if (Float.parseFloat(parameters.get(SettingsManager.getInstance().getResString(R.string.cap_ss))) == 0) {
                    return 0;
                } else
                    return Float.parseFloat(parameters.get(SettingsManager.getInstance().getResString(R.string.cap_ss)))/ 1000000;
            } else
                return 0;
        }
        else
        {
            if (parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.cur_exposure_time))!= null)
                return Float.parseFloat(parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.cur_exposure_time)))/1000;
        }
        return 0;
    }

    @Override
    public int getCurrentIso() {
        Camera.Parameters parameters = ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCameraParameters();
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            if(parameters.get(SettingsManager.getInstance().getResString(R.string.eng_capture_sensor_gain))!= null) {
                if (Integer.parseInt(parameters.get(SettingsManager.getInstance().getResString(R.string.eng_capture_sensor_gain))) == 0) {
                    return 0;
                }
                return Integer.parseInt(parameters.get(SettingsManager.getInstance().getResString(R.string.eng_capture_sensor_gain))) / 256 * 100;
            }
            else if(parameters.get(SettingsManager.getInstance().getResString(R.string.cap_isp_g))!= null)
            {
                if (Integer.parseInt(parameters.get(SettingsManager.getInstance().getResString(R.string.cap_isp_g))) == 0) {
                    return 0;
                }
                return Integer.parseInt(parameters.get(SettingsManager.getInstance().getResString(R.string.cap_isp_g))) / 256 * 100;
            }
            else
                return 0;
        }
        else
        {
            if (parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.cur_iso))!= null)
                return Integer.parseInt(parameters.get(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.cur_iso)));
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

    public int getFlash()
    {
        Camera.Parameters parameters = ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCameraParameters();
        if(parameters.get("flash-on").equals("true") ){
            return 1;
        }
        return 0;
    }

    public float getFocal()
    {
        return cameraParameters.getFocalLength();
    }

    public void SetCameraRotation()
    {

        if (!SettingsManager.get(SettingKeys.orientationHack).get())
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
        cameraUiWrapper.stopPreviewAsync();
        cameraUiWrapper.startPreviewAsync();
        cameraParameters.set("slow_shutter",Shutter);
        cameraParameters.set("slow_shutter_addition", "1");
        Log.d(TAG,"SetZTE_RESET_AE_SETSHUTTER");
        SetParametersToCamera(cameraParameters);


    }
}
