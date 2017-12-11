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
import freed.settings.Settings;
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
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StringUtils;
import freed.utils.StringUtils.FileEnding;

import static freed.settings.SettingsManager.FRAMEWORK_LG;
import static freed.settings.SettingsManager.FRAMEWORK_MTK;
import static freed.settings.SettingsManager.ISOMANUAL_KRILLIN;
import static freed.settings.SettingsManager.ISOMANUAL_MTK;
import static freed.settings.SettingsManager.ISOMANUAL_QCOM;
import static freed.settings.SettingsManager.ISOMANUAL_SONY;
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
        add(Settings.PictureFormat, new PictureFormatHandler(cameraParameters, cameraUiWrapper, this));
        if (SettingsManager.getInstance().getDngProfilesMap()!= null
                && SettingsManager.getInstance().getDngProfilesMap().size() > 0
                && SettingsManager.get(Settings.rawPictureFormatSetting).isSupported())
            add(Settings.opcode, new OpCodeParameter());
        cameraUiWrapper.getModuleHandler().addListner((ModuleChangedEvent) get(Settings.PictureFormat));
        SettingsManager appS = SettingsManager.getInstance();

        if (appS.get(Settings.PictureSize).isSupported())
            add(Settings.PictureSize ,new PictureSizeParameter(cameraParameters, cameraUiWrapper));

        if (appS.get(Settings.FocusMode).isSupported()) {
            add(Settings.FocusMode,new BaseModeParameter(cameraParameters, cameraUiWrapper,appS.get(Settings.FocusMode)));
            get(Settings.FocusMode).addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).focusModeListner);
        }

        if (appS.get(Settings.WhiteBalanceMode).isSupported())
            add(Settings.WhiteBalanceMode, new BaseModeParameter(cameraParameters, cameraUiWrapper,appS.get(Settings.WhiteBalanceMode)));

        if (appS.get(Settings.ExposureMode).isSupported()) {
            add(Settings.ExposureMode,new BaseModeParameter(cameraParameters, cameraUiWrapper, appS.get(Settings.ExposureMode)));
            get(Settings.ExposureMode).addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).aeModeListner);
        }

        if (appS.get(Settings.ColorMode).isSupported())
            add(Settings.ColorMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.ColorMode)));

        if (appS.get(Settings.FlashMode).isSupported())
            add(Settings.FlashMode, new BaseModeParameter(cameraParameters,cameraUiWrapper, appS.get(Settings.FlashMode)));

        if (appS.get(Settings.IsoMode).isSupported())
            add(Settings.IsoMode ,new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.IsoMode)));

        if (appS.get(Settings.AntiBandingMode).isSupported())
            add(Settings.AntiBandingMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.AntiBandingMode)));

        if (appS.get(Settings.ImagePostProcessing).isSupported())
            add(Settings.ImagePostProcessing, new BaseModeParameter(cameraParameters, cameraUiWrapper, appS.get(Settings.ImagePostProcessing)));

        if (appS.get(Settings.JpegQuality).isSupported())
            add(Settings.JpegQuality, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.JpegQuality)));

        if (appS.get(Settings.AE_Bracket).isSupported())
            add(Settings.AE_Bracket, new BaseModeParameter(cameraParameters, cameraUiWrapper, appS.get(Settings.AE_Bracket)));

        if (appS.get(Settings.PreviewSize).isSupported())
            add(Settings.PreviewSize,  new PreviewSizeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.PreviewSize)));

        if (appS.get(Settings.PreviewFPS).isSupported())
            add(Settings.PreviewFPS, new PreviewFpsParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.PreviewFPS)));

        if (appS.get(Settings.PreviewFpsRange).isSupported())
            add(Settings.PreviewFPS, new BaseModeParameter(cameraParameters, cameraUiWrapper, appS.get(Settings.PreviewFpsRange)));

        if (appS.get(Settings.PreviewFormat).isSupported())
            add(Settings.PreviewFormat,  new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.PreviewFormat)));

        if (appS.get(Settings.SceneMode).isSupported())
            add(Settings.SceneMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.SceneMode)));

        if (appS.get(Settings.RedEye).isSupported())
            add(Settings.RedEye, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.RedEye)));

        if (appS.get(Settings.LensShade).isSupported())
            add(Settings.LensShade, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.LensShade)));

        if (appS.get(Settings.ZSL).isSupported())
            add(Settings.ZSL, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.ZSL)));

        if (appS.get(Settings.SceneDetect).isSupported())
            add(Settings.SceneDetect, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.SceneDetect)));

        if (appS.get(Settings.MemoryColorEnhancement).isSupported())
            add(Settings.MemoryColorEnhancement, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.MemoryColorEnhancement)));

        if (appS.get(Settings.VideoSize).isSupported())
            add(Settings.VideoSize, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.VideoSize)));

        if (appS.get(Settings.CDS_Mode).isSupported())
            add(Settings.CDS_Mode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.CDS_Mode)));

        if (appS.get(Settings.oismode).isSupported())
            add(Settings.oismode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.oismode)));

        if (appS.get(Settings.VideoHDR).isSupported())
            add(Settings.VideoHDR, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.VideoHDR)));

        if (appS.get(Settings.VideoHighFramerate).isSupported())
            add(Settings.VideoHighFramerate, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.VideoHighFramerate)));

        if (appS.get(Settings.NightMode).isSupported()) {
            switch (SettingsManager.get(Settings.NightMode).getType()) {
                case SettingsManager.NIGHTMODE_XIAOMI:
                    //NightMode = new NightModeXiaomi(cameraParameters, cameraUiWrapper);
                    break;
                case SettingsManager.NIGHTMODE_ZTE:
                    add(Settings.NightMode, new NightModeZTE(cameraParameters, cameraUiWrapper));
                    break;
            }
        }
        
        if (appS.get(Settings.HDRMode).isSupported()){
            switch (appS.get(Settings.HDRMode).getType())
            {
                case SettingsManager.HDR_MORPHO:
                    //HDRMode = new MorphoHdrModeParameters(cameraParameters,cameraUiWrapper,appS.hdrMode);
                    break;
                case SettingsManager.HDR_AUTO:
                    add(Settings.HDRMode, new AutoHdrMode(cameraParameters,cameraUiWrapper,appS.get(Settings.HDRMode)));
                    break;
                case SettingsManager.HDR_LG:
                    add(Settings.HDRMode,new LgHdrMode(cameraParameters,cameraUiWrapper,appS.get(Settings.HDRMode)));
                    break;
                case SettingsManager.HDR_MOTO:
                    add(Settings.HDRMode, new MotoHDR(cameraParameters,cameraUiWrapper,appS.get(Settings.HDRMode)));
                    break;
            }
        }

        if (SettingsManager.getInstance().getDngProfilesMap() != null && appS.getDngProfilesMap().size() > 0 && appS.get(Settings.rawPictureFormatSetting).isSupported())
            add(Settings.matrixChooser, new MatrixChooserParameter(SettingsManager.getInstance().getMatrixesMap()));

        if(appS.get(Settings.Denoise).isSupported())
            add(Settings.Denoise, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.Denoise)));

        if(appS.get(Settings.NonZslManualMode).isSupported())
            add(Settings.NonZslManualMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.NonZslManualMode)));

        if (appS.get(Settings.DigitalImageStabilization).isSupported())
            add(Settings.DigitalImageStabilization, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.DigitalImageStabilization)));

        if (appS.get(Settings.TNR).isSupported())
            add(Settings.TNR, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.TNR)));
        if (appS.get(Settings.TNR_V).isSupported())
            add(Settings.TNR_V, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.TNR_V)));
        if (appS.get(Settings.PDAF).isSupported())
            add(Settings.PDAF, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.PDAF)));
        if (appS.get(Settings.SeeMore).isSupported())
            add(Settings.SeeMore, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.SeeMore)));
        if (appS.get(Settings.TruePotrait).isSupported())
            add(Settings.TruePotrait, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.TruePotrait)));
        if (appS.get(Settings.ReFocus).isSupported())
            add(Settings.ReFocus, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.ReFocus)));
        if (appS.get(Settings.OptiZoom).isSupported())
            add(Settings.OptiZoom, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.OptiZoom)));
        if (appS.get(Settings.RDI).isSupported())
            add(Settings.RDI, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.RDI)));
        if (appS.get(Settings.ChromaFlash).isSupported())
            add(Settings.ChromaFlash, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.ChromaFlash)));

        add(Settings.VideoProfiles, new VideoProfilesParameter(cameraUiWrapper));

        add(Settings.ExposureLock, new ExposureLockParameter(cameraParameters, cameraUiWrapper));

        add(Settings.Focuspeak, new FocusPeakModeParameter(cameraUiWrapper,((Camera1Fragment) cameraUiWrapper).focusPeakProcessorAp1));

        SetCameraRotation();

        SetPictureOrientation(0);

        add(Settings.Module, new ModuleParameters(cameraUiWrapper));

        /*
        MANUALSTUFF
         */

        if (SettingsManager.get(Settings.M_Focus).isSupported())
        {
            if (SettingsManager.getInstance().getFrameWork() == FRAMEWORK_MTK)
            {
                add(Settings.M_Focus, new FocusManualMTK(cameraParameters, cameraUiWrapper,appS.get(Settings.M_Focus)));
            }
            else
            {
                //htc mf
                if (appS.get(Settings.M_Focus).getKEY().equals(cameraUiWrapper.getResString(R.string.focus)))
                     add(Settings.M_Focus, new FocusManualParameterHTC(cameraParameters,cameraUiWrapper));
                    //huawai mf
                else if (appS.get(Settings.M_Focus).getKEY().equals(SettingsManager.getInstance().getResString(R.string.hw_manual_focus_step_value)))
                    add(Settings.M_Focus, new FocusManualHuawei(cameraParameters, cameraUiWrapper, appS.get(Settings.M_Focus)));
                    //qcom
                else
                    add(Settings.M_Focus, new BaseFocusManual(cameraParameters,cameraUiWrapper,appS.get(Settings.M_Focus)));
            }

        }

        if (appS.get(Settings.M_Saturation).isSupported()) {
            add(Settings.M_Saturation, new BaseManualParameter(cameraParameters, cameraUiWrapper, appS.get(Settings.M_Saturation)));
        }

        if (appS.get(Settings.M_Sharpness).isSupported())
            add(Settings.M_Sharpness, new BaseManualParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.M_Sharpness)));

        if (appS.get(Settings.M_Brightness).isSupported())
            add(Settings.M_Brightness, new BaseManualParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.M_Brightness)));

        if(appS.get(Settings.M_Contrast).isSupported())
            add(Settings.M_Contrast, new BaseManualParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.M_Contrast)));


        if (appS.get(Settings.LensFilter).isSupported())
            add(Settings.LensFilter, new VirtualLensFilter(cameraParameters,cameraUiWrapper));

        if (appS.getFrameWork() == FRAMEWORK_LG)//its needed else cam ignores manuals like shutter and iso
            cameraParameters.set("lge-camera","1");
        else  if (appS.getFrameWork() == FRAMEWORK_MTK){
            cameraParameters.set("afeng_raw_dump_flag", "1");
            cameraParameters.set("rawsave-mode", "2");
            cameraParameters.set("isp-mode", "1");
            cameraParameters.set("rawfname", StringUtils.GetInternalSDCARD()+"/DCIM/test."+ FileEnding.BAYER);
        }

        if (appS.get(Settings.M_ExposureTime).isSupported())
        {
            int type = appS.get(Settings.M_ExposureTime).getType();
            switch (type)
            {
                case SHUTTER_HTC:
                    //HTCVideoMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "video-mode", "video-hfr-values");
                    add(Settings.M_ExposureTime, new ShutterManualParameterHTC(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_QCOM_MICORSEC:
                    add(Settings.M_ExposureTime, new ExposureTime_MicroSec(cameraUiWrapper,cameraParameters));
                    break;
                case SHUTTER_QCOM_MILLISEC:
                    add(Settings.M_ExposureTime, new ExposureTime_MilliSec(cameraUiWrapper,cameraParameters));
                    break;
                case SHUTTER_MTK:

                    aehandler = new AE_Handler_MTK(cameraParameters,cameraUiWrapper,1600);
                    add(Settings.M_ExposureTime, aehandler.getShutterManual());
                    add(Settings.M_ManualIso, aehandler.getManualIso());
                    break;
                case SHUTTER_LG:
                    aehandler = new AE_Handler_LGG4(cameraParameters,cameraUiWrapper);
                    add(Settings.M_ExposureTime, aehandler.getShutterManual());
                    add(Settings.M_ManualIso, aehandler.getManualIso());
                    break;
                case SHUTTER_MEIZU:
                    add(Settings.M_ExposureTime, new ShutterManualMeizu(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_KRILLIN:
                    add(Settings.M_ExposureTime, new ShutterManualKrilin(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_SONY:
                    add(Settings.M_ExposureTime, new ShutterManualSony(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_G2PRO:
                    add(Settings.M_ExposureTime, new ShutterManualG2pro(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_ZTE:
                    add(Settings.M_ExposureTime, new ShutterManualZTE(cameraParameters,cameraUiWrapper));
            }

        }

        //mtk and g4 aehandler set it already
        Log.d(TAG, "manual Iso supported:" + appS.get(Settings.M_ManualIso).isSupported());
        if (appS.get(Settings.M_ManualIso).isSupported() && aehandler == null && appS.get(Settings.M_ManualIso).getValues() != null && appS.get(Settings.M_ManualIso).getValues().length > 0)
        {
            switch (appS.get(Settings.M_ManualIso).getType())
            {
                case ISOMANUAL_QCOM:
                    add(Settings.M_ManualIso, new BaseISOManual(cameraParameters,cameraUiWrapper));
                    break;
                case ISOMANUAL_SONY:
                    add(Settings.M_ManualIso, new ManualIsoSony(cameraUiWrapper,cameraParameters));
                    break;
                case ISOMANUAL_KRILLIN:
                    add(Settings.M_ManualIso,  new ManualIsoKrilin(cameraParameters,cameraUiWrapper));
                    break;
                case ISOMANUAL_MTK: //get set due aehandler
                    break;

            }
        }

        if (appS.get(Settings.M_Aperture).isSupported())
            add(Settings.M_Fnumber, new ManualAperture(cameraUiWrapper,cameraParameters));

        if (appS.get(Settings.M_Whitebalance).isSupported())
            add(Settings.M_Whitebalance, new BaseCCTManual(cameraParameters,cameraUiWrapper));

        add(Settings.M_3D_Convergence, new BaseManualParameter(cameraParameters,
                cameraUiWrapper.getResString(R.string.manual_convergence),
                cameraUiWrapper.getResString(R.string.supported_manual_convergence_max),
                cameraUiWrapper.getResString(R.string.supported_manual_convergence_min),
                cameraUiWrapper,1));

        add(Settings.M_ExposureCompensation, new ExposureManualParameter(cameraParameters, cameraUiWrapper,1));

        add(Settings.M_FX, new FXManualParameter(cameraParameters, cameraUiWrapper));
        get(Settings.PictureFormat).addEventListner(((BaseManualParameter) get(Settings.M_FX)).GetPicFormatListner());
        cameraUiWrapper.getModuleHandler().addListner(((BaseManualParameter)get(Settings.M_FX)).GetModuleListner());

        if (appS.get(Settings.M_Burst).isSupported()){
            add(Settings.M_Burst, new BurstManualParam(cameraParameters, cameraUiWrapper));
            cameraUiWrapper.getModuleHandler().addListner(((BaseManualParameter) get(Settings.M_Burst)).GetModuleListner());
        }

        add(Settings.M_Zoom, new ZoomManualParameter(cameraParameters, cameraUiWrapper));

        if (appS.get(Settings.dualPrimaryCameraMode).isSupported())
            add(Settings.dualPrimaryCameraMode, new BaseModeParameter(cameraParameters,cameraUiWrapper,appS.get(Settings.dualPrimaryCameraMode)));


        //set last used settings
        SetAppSettingsToParameters();

        cameraUiWrapper.getModuleHandler().setModule(SettingsManager.getInstance().GetCurrentModule());
    }

    @Override
    public void SetFocusAREA(Rect focusAreas)
    {
        if (SettingsManager.get(Settings.useQcomFocus).getBoolean())
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
        if (SettingsManager.get(Settings.orientationHack).getBoolean())
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
        if (SettingsManager.getInstance().getFrameWork() == SettingsManager.FRAMEWORK_MTK) {
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
            if (parameters.get(cameraUiWrapper.getResString(R.string.cur_exposure_time))!= null)
                return Float.parseFloat(parameters.get(cameraUiWrapper.getResString(R.string.cur_exposure_time)))*1000000;
        }
        return 0;
    }

    @Override
    public int getCurrentIso() {
        Camera.Parameters parameters = ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCameraParameters();
        if (SettingsManager.getInstance().getFrameWork() == FRAMEWORK_MTK)
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

        if (!SettingsManager.get(Settings.orientationHack).getBoolean())
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
