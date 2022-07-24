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

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;

import androidx.databinding.Observable;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModuleParameters;
import freed.cam.apis.basecamera.parameters.modes.OrientationHackParameter;
import freed.cam.apis.basecamera.parameters.modes.VideoAudioSourceMode;
import freed.cam.apis.camera1.Camera1;
import freed.cam.apis.camera1.CameraHolder;
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

/**
 * Created by troop on 17.08.2014.
 * this class handels all camera1 releated parameters.
 */
public class ParametersHandler extends AbstractParameterHandler<Camera1>
{

    private final String TAG = ParametersHandler.class.getSimpleName();

    private Parameters cameraParameters;
    public Parameters getParameters(){return cameraParameters;}

    public ParametersHandler(Camera1 cameraUiWrapper)
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
        add(SettingKeys.PICTURE_FORMAT, new PictureFormatHandler(cameraParameters, cameraUiWrapper, this));

        if (settingsManager.get(SettingKeys.PICTURE_SIZE).isSupported())
            add(SettingKeys.PICTURE_SIZE,new BaseModeParameter(cameraParameters, cameraUiWrapper,SettingKeys.PICTURE_SIZE));

        if (settingsManager.get(SettingKeys.FOCUS_MODE).isSupported()) {
            BaseModeParameter focusmode = new BaseModeParameter(cameraParameters, cameraUiWrapper,SettingKeys.FOCUS_MODE);
            add(SettingKeys.FOCUS_MODE,focusmode);
            focusmode.addOnPropertyChangedCallback(cameraUiWrapper.focusHandler.focusmodeObserver);

        }

        if (settingsManager.get(SettingKeys.WHITE_BALANCE_MODE).isSupported())
            add(SettingKeys.WHITE_BALANCE_MODE, new BaseModeParameter(cameraParameters, cameraUiWrapper,SettingKeys.WHITE_BALANCE_MODE));

        if (settingsManager.get(SettingKeys.EXPOSURE_MODE).isSupported()) {
            add(SettingKeys.EXPOSURE_MODE,new BaseModeParameter(cameraParameters, cameraUiWrapper, SettingKeys.EXPOSURE_MODE));
        }

        if (settingsManager.get(SettingKeys.COLOR_MODE).isSupported())
            add(SettingKeys.COLOR_MODE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.COLOR_MODE));

        if (settingsManager.get(SettingKeys.FLASH_MODE).isSupported())
            add(SettingKeys.FLASH_MODE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.FLASH_MODE));

        if (settingsManager.get(SettingKeys.ISO_MODE).isSupported())
            add(SettingKeys.ISO_MODE,new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.ISO_MODE));

        if (settingsManager.get(SettingKeys.ANTI_BANDING_MODE).isSupported())
            add(SettingKeys.ANTI_BANDING_MODE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.ANTI_BANDING_MODE));

        if (settingsManager.get(SettingKeys.IMAGE_POST_PROCESSING).isSupported())
            add(SettingKeys.IMAGE_POST_PROCESSING, new BaseModeParameter(cameraParameters, cameraUiWrapper, SettingKeys.IMAGE_POST_PROCESSING));

        if (settingsManager.get(SettingKeys.JPEG_QUALITY).isSupported())
            add(SettingKeys.JPEG_QUALITY, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.JPEG_QUALITY));

        if (settingsManager.get(SettingKeys.AE_BRACKET).isSupported())
            add(SettingKeys.AE_BRACKET, new BaseModeParameter(cameraParameters, cameraUiWrapper, SettingKeys.AE_BRACKET));

        if (settingsManager.get(SettingKeys.PREVIEW_SIZE).isSupported())
            add(SettingKeys.PREVIEW_SIZE,  new PreviewSizeParameter(cameraParameters,cameraUiWrapper,SettingKeys.PREVIEW_SIZE));

        if (settingsManager.get(SettingKeys.PREVIEW_FPS).isSupported())
            add(SettingKeys.PREVIEW_FPS, new PreviewFpsParameter(cameraParameters,cameraUiWrapper,SettingKeys.PREVIEW_FPS));

        if (settingsManager.get(SettingKeys.PREVIEW_FPS_RANGE).isSupported())
            add(SettingKeys.PREVIEW_FPS_RANGE, new BaseModeParameter(cameraParameters, cameraUiWrapper, SettingKeys.PREVIEW_FPS_RANGE));

        if (settingsManager.get(SettingKeys.PREVIEW_FORMAT).isSupported())
            add(SettingKeys.PREVIEW_FORMAT,  new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.PREVIEW_FORMAT));

        if (settingsManager.get(SettingKeys.SCENE_MODE).isSupported())
            add(SettingKeys.SCENE_MODE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.SCENE_MODE));

        if (settingsManager.get(SettingKeys.RED_EYE).isSupported())
            add(SettingKeys.RED_EYE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.RED_EYE));

        if (settingsManager.get(SettingKeys.LENS_SHADE).isSupported())
            add(SettingKeys.LENS_SHADE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.LENS_SHADE));

        if (settingsManager.get(SettingKeys.ZSL).isSupported())
            add(SettingKeys.ZSL, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.ZSL));

        if (settingsManager.get(SettingKeys.SCENE_DETECT).isSupported())
            add(SettingKeys.SCENE_DETECT, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.SCENE_DETECT));

        if (settingsManager.get(SettingKeys.MEMORY_COLOR_ENHANCEMENT).isSupported())
            add(SettingKeys.MEMORY_COLOR_ENHANCEMENT, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.MEMORY_COLOR_ENHANCEMENT));

        if (settingsManager.get(SettingKeys.VIDEO_SIZE).isSupported())
            add(SettingKeys.VIDEO_SIZE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.VIDEO_SIZE));

        if (settingsManager.get(SettingKeys.CDS_MODE).isSupported())
            add(SettingKeys.CDS_MODE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.CDS_MODE));

        if (settingsManager.get(SettingKeys.OIS_MODE).isSupported())
            add(SettingKeys.OIS_MODE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.OIS_MODE));

        if (settingsManager.get(SettingKeys.VIDEO_HDR).isSupported())
            add(SettingKeys.VIDEO_HDR, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.VIDEO_HDR));

        if (settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).isSupported())
            add(SettingKeys.VIDEO_HIGH_FRAMERATE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.VIDEO_HIGH_FRAMERATE));

        if(settingsManager.get(SettingKeys.VIDEO_STABILIZATION).isSupported())
            add(SettingKeys.VIDEO_STABILIZATION, new VideoStabilizationParameter(cameraParameters,cameraUiWrapper));

        if (settingsManager.get(SettingKeys.NIGHT_MODE).isSupported()) {
            switch (settingsManager.get(SettingKeys.NIGHT_MODE).getType()) {
                case SettingsManager.NIGHTMODE_XIAOMI:
                    //NightMode = new NightModeXiaomi(cameraParameters, cameraUiWrapper);
                    break;
                case SettingsManager.NIGHTMODE_ZTE:
                    add(SettingKeys.NIGHT_MODE, new NightModeZTE(cameraParameters, cameraUiWrapper));
                    break;
            }
        }
        
        if (settingsManager.get(SettingKeys.HDR_MODE).isSupported()){
            switch (settingsManager.get(SettingKeys.HDR_MODE).getType())
            {
                case SettingsManager.HDR_MORPHO:
                    //HDRMode = new MorphoHdrModeParameters(cameraParameters,cameraUiWrapper,appS.hdrMode);
                    break;
                case SettingsManager.HDR_AUTO:
                    AutoHdrMode autoHdrMode = new AutoHdrMode(cameraParameters,cameraUiWrapper,SettingKeys.HDR_MODE);
                    add(SettingKeys.HDR_MODE, autoHdrMode);
                    break;
                case SettingsManager.HDR_LG:
                    LgHdrMode lgHdrMode = new LgHdrMode(cameraParameters,cameraUiWrapper,SettingKeys.HDR_MODE);
                    add(SettingKeys.HDR_MODE,lgHdrMode);
                    break;
                case SettingsManager.HDR_MOTO:
                    MotoHDR motoHDR = new MotoHDR(cameraParameters,cameraUiWrapper,SettingKeys.HDR_MODE);
                    add(SettingKeys.HDR_MODE, motoHDR);
                    break;
            }
        }

        if (settingsManager.getDngProfilesMap() != null && settingsManager.getDngProfilesMap().size() > 0 && settingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).isSupported())
            add(SettingKeys.MATRIX_SET, new MatrixChooserParameter(settingsManager.getMatrixesMap()));

        if(settingsManager.get(SettingKeys.DENOISE).isSupported())
            add(SettingKeys.DENOISE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.DENOISE));

        if(settingsManager.get(SettingKeys.NON_ZSL_MANUAL_MODE).isSupported())
            add(SettingKeys.NON_ZSL_MANUAL_MODE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.NON_ZSL_MANUAL_MODE));

        if (settingsManager.get(SettingKeys.DIGITAL_IMAGE_STABILIZATION).isSupported())
            add(SettingKeys.DIGITAL_IMAGE_STABILIZATION, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.DIGITAL_IMAGE_STABILIZATION));

        if (settingsManager.get(SettingKeys.TNR).isSupported())
            add(SettingKeys.TNR, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.TNR));
        if (settingsManager.get(SettingKeys.TNR_V).isSupported())
            add(SettingKeys.TNR_V, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.TNR_V));
        if (settingsManager.get(SettingKeys.PDAF).isSupported())
            add(SettingKeys.PDAF, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.PDAF));
        if (settingsManager.get(SettingKeys.SEE_MORE).isSupported())
            add(SettingKeys.SEE_MORE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.SEE_MORE));
        if (settingsManager.get(SettingKeys.TRUE_POTRAIT).isSupported())
            add(SettingKeys.TRUE_POTRAIT, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.TRUE_POTRAIT));
        if (settingsManager.get(SettingKeys.RE_FOCUS).isSupported())
            add(SettingKeys.RE_FOCUS, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.RE_FOCUS));
        if (settingsManager.get(SettingKeys.OPTI_ZOOM).isSupported())
            add(SettingKeys.OPTI_ZOOM, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.OPTI_ZOOM));
        if (settingsManager.get(SettingKeys.RDI).isSupported())
            add(SettingKeys.RDI, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.RDI));
        if (settingsManager.get(SettingKeys.CHROMA_FLASH).isSupported())
            add(SettingKeys.CHROMA_FLASH, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.CHROMA_FLASH));

        add(SettingKeys.VIDEO_PROFILES, new VideoProfilesParameter(cameraUiWrapper));
        add(SettingKeys.VIDEO_AUDIO_SOURCE, new VideoAudioSourceMode(cameraUiWrapper,SettingKeys.VIDEO_AUDIO_SOURCE));

        add(SettingKeys.EXPOSURE_LOCK, new ExposureLockParameter(cameraParameters, cameraUiWrapper));
        add(SettingKeys.ORIENTATION_HACK,new OrientationHackParameter(cameraUiWrapper,SettingKeys.ORIENTATION_HACK));



        SetCameraRotation();

        SetPictureOrientation(0);

        add(SettingKeys.MODULE, new ModuleParameters(cameraUiWrapper));

        /*
        MANUALSTUFF
         */

        if (settingsManager.get(SettingKeys.M_FOCUS).isSupported())
        {
            if (settingsManager.getFrameWork() == Frameworks.MTK)
            {
                add(SettingKeys.M_FOCUS, new FocusManualMTK(cameraParameters, cameraUiWrapper,SettingKeys.M_FOCUS));
            }
            else
            {
                //htc mf
                if (settingsManager.get(SettingKeys.M_FOCUS).getCamera1ParameterKEY().equals(FreedApplication.getStringFromRessources(R.string.focus)))
                     add(SettingKeys.M_FOCUS, new FocusManualParameterHTC(cameraParameters,cameraUiWrapper,SettingKeys.M_FOCUS));
                    //huawai mf
                else if (settingsManager.get(SettingKeys.M_FOCUS).getCamera1ParameterKEY().equals(FreedApplication.getStringFromRessources(R.string.hw_manual_focus_step_value)))
                    add(SettingKeys.M_FOCUS, new FocusManualHuawei(cameraParameters, cameraUiWrapper, SettingKeys.M_FOCUS));
                    //qcom
                else
                    add(SettingKeys.M_FOCUS, new BaseFocusManual(cameraParameters,cameraUiWrapper,SettingKeys.M_FOCUS));
            }

        }

        if (settingsManager.get(SettingKeys.M_SATURATION).isSupported()) {
            add(SettingKeys.M_SATURATION, new BaseManualParameter(cameraParameters, cameraUiWrapper, SettingKeys.M_SATURATION));
        }

        if (settingsManager.get(SettingKeys.M_SHARPNESS).isSupported())
            add(SettingKeys.M_SHARPNESS, new BaseManualParameter(cameraParameters,cameraUiWrapper,SettingKeys.M_SHARPNESS));

        if (settingsManager.get(SettingKeys.M_Brightness).isSupported())
            add(SettingKeys.M_Brightness, new BaseManualParameter(cameraParameters,cameraUiWrapper,SettingKeys.M_Brightness));

        if(settingsManager.get(SettingKeys.M_CONTRAST).isSupported())
            add(SettingKeys.M_CONTRAST, new BaseManualParameter(cameraParameters,cameraUiWrapper,SettingKeys.M_CONTRAST));


        if (settingsManager.get(SettingKeys.LENS_FILTER).isSupported())
            add(SettingKeys.LENS_FILTER, new VirtualLensFilter(cameraParameters,cameraUiWrapper));

        if (settingsManager.getFrameWork() == Frameworks.LG)//its needed else cam ignores manuals like shutter and iso
            cameraParameters.set("lge-camera","1");
        else  if (settingsManager.getFrameWork() == Frameworks.MTK){
            cameraParameters.set("afeng_raw_dump_flag", "1");
            cameraParameters.set("rawsave-mode", "2");
            cameraParameters.set("isp-mode", "1");
            cameraParameters.set("rawfname", StringUtils.GetInternalSDCARD()+"/DCIM/test."+ FileEnding.BAYER);
        }

        if (settingsManager.get(SettingKeys.M_EXPOSURE_TIME).isSupported())
        {
            int type = settingsManager.get(SettingKeys.M_EXPOSURE_TIME).getType();
            switch (type)
            {
                case SHUTTER_HTC:
                    //HTCVideoMode = new BaseModeParameter(cameraParameters, cameraUiWrapper, "video-mode", "video-hfr-values");
                    add(SettingKeys.M_EXPOSURE_TIME, new ShutterManualParameterHTC(cameraParameters,cameraUiWrapper, SettingKeys.M_EXPOSURE_TIME));
                    break;
                case SHUTTER_QCOM_MILLISEC:
                    add(SettingKeys.M_EXPOSURE_TIME, new ExposureTime_MS(cameraUiWrapper,cameraParameters,SettingKeys.M_EXPOSURE_TIME));
                    break;
                case SHUTTER_QCOM_MICORSEC:
                    add(SettingKeys.M_EXPOSURE_TIME, new ExposureTime_MicroSec(cameraUiWrapper,cameraParameters));
                    break;
                case SHUTTER_MTK:
                    AeManagerMtkCamera1 aeManagerMtkCamera1 = new AeManagerMtkCamera1(cameraUiWrapper,cameraParameters);
                    add(SettingKeys.M_EXPOSURE_TIME, aeManagerMtkCamera1.getExposureTime());
                    add(SettingKeys.M_MANUAL_ISO, aeManagerMtkCamera1.getIso());
                    break;
                case SHUTTER_LG:
                    AeManagerLgCamera1 aeManagerLgCamera1 = new AeManagerLgCamera1(cameraUiWrapper,cameraParameters);
                    add(SettingKeys.M_EXPOSURE_TIME, aeManagerLgCamera1.getExposureTime());
                    add(SettingKeys.M_MANUAL_ISO, aeManagerLgCamera1.getIso());
                    break;
                case SHUTTER_MEIZU:
                    add(SettingKeys.M_EXPOSURE_TIME, new ShutterManualMeizu(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_KRILLIN:
                    add(SettingKeys.M_EXPOSURE_TIME, new ShutterManualKirin(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_SONY:
                    add(SettingKeys.M_EXPOSURE_TIME, new ShutterManualSony(cameraParameters,cameraUiWrapper));
                    break;
                case SHUTTER_G2PRO:
                    add(SettingKeys.M_EXPOSURE_TIME, new ShutterManualG2pro(cameraParameters,cameraUiWrapper,SettingKeys.M_EXPOSURE_TIME));
                    break;
                case SHUTTER_ZTE:
                    add(SettingKeys.M_EXPOSURE_TIME, new ShutterManualZTE(cameraParameters,cameraUiWrapper));
            }

        }

        //mtk and g4 aehandler set it already
        Log.d(TAG, "manual Iso supported:" + settingsManager.get(SettingKeys.M_MANUAL_ISO).isSupported());
        if (settingsManager.get(SettingKeys.M_MANUAL_ISO).isSupported()
                && settingsManager.get(SettingKeys.M_MANUAL_ISO).getValues() != null
                && settingsManager.get(SettingKeys.M_MANUAL_ISO).getValues().length > 0
                && get(SettingKeys.M_MANUAL_ISO) == null)
        {
            switch (settingsManager.get(SettingKeys.M_MANUAL_ISO).getType())
            {
                case ISOMANUAL_QCOM:
                    add(SettingKeys.M_MANUAL_ISO, new BaseISOManual(cameraParameters,cameraUiWrapper,SettingKeys.M_MANUAL_ISO));
                    break;
                case ISOMANUAL_SONY:
                    add(SettingKeys.M_MANUAL_ISO, new ManualIsoSony(cameraUiWrapper,cameraParameters,SettingKeys.M_MANUAL_ISO));
                    break;
                case ISOMANUAL_KRILLIN:
                    add(SettingKeys.M_MANUAL_ISO,  new ManualIsoKirin(cameraParameters,cameraUiWrapper,SettingKeys.M_MANUAL_ISO));
                    break;
                case ISOMANUAL_Xiaomi :
                    //not supported
                    break;
                case ISOMANUAL_MTK: //get set due aehandler
                    break;

            }
        }

        if (settingsManager.get(SettingKeys.M_APERTURE).isSupported())
            add(SettingKeys.M_FNUMBER, new ManualAperture(cameraUiWrapper,cameraParameters));

        if (settingsManager.get(SettingKeys.M_WHITEBALANCE).isSupported()) {
            add(SettingKeys.M_WHITEBALANCE, new BaseCCTManual(cameraParameters, cameraUiWrapper, SettingKeys.M_WHITEBALANCE));
            BaseModeParameter wb = (BaseModeParameter) get(SettingKeys.WHITE_BALANCE_MODE);
            wb.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    BaseModeParameter wbb = (BaseModeParameter) sender;
                    if (((BaseModeParameter) sender).getStringValue() == "manual")
                        get(SettingKeys.M_WHITEBALANCE).setViewState(AbstractParameter.ViewState.Visible);
                    else
                        get(SettingKeys.M_WHITEBALANCE).setViewState(AbstractParameter.ViewState.Hidden);
                }
            });
        }

        add(SettingKeys.M_EXPOSURE_COMPENSATION, new ExposureManualParameter(cameraParameters, cameraUiWrapper,SettingKeys.M_EXPOSURE_COMPENSATION));

        if (settingsManager.get(SettingKeys.M_FX).isSupported()) {
            add(SettingKeys.M_FX, new FXManualParameter(cameraParameters, cameraUiWrapper,SettingKeys.M_FX));
        }

        if (settingsManager.get(SettingKeys.M_BURST).isSupported()){
            add(SettingKeys.M_BURST, new BurstManualParam(cameraParameters, cameraUiWrapper,SettingKeys.M_BURST));
        }

        add(SettingKeys.M_ZOOM, new ZoomManualParameter(cameraParameters, cameraUiWrapper,SettingKeys.M_ZOOM));

        if (settingsManager.get(SettingKeys.DUAL_PRIMARY_CAMERA_MODE).isSupported())
            add(SettingKeys.DUAL_PRIMARY_CAMERA_MODE, new BaseModeParameter(cameraParameters,cameraUiWrapper,SettingKeys.DUAL_PRIMARY_CAMERA_MODE));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            add(SettingKeys.OPEN_CAMERA_1_LEGACY, new LegacyMode(cameraUiWrapper,settingsManager.get(SettingKeys.OPEN_CAMERA_1_LEGACY)));


        //set last used settings
        SetAppSettingsToParameters();
        setManualSettingsToParameters();

        cameraUiWrapper.getModuleHandler().setModule(settingsManager.GetCurrentModule());
    }

    @Override
    public void SetFocusAREA(Rect focusAreas)
    {
        if (settingsManager.get(SettingKeys.USE_QCOM_FOCUS).get())
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
        if (!settingsManager.get(SettingKeys.ORIENTATION_HACK).get().equals("0") || settingsManager.getIsFrontCamera())
        {
            int or = orientation +Integer.parseInt(settingsManager.get(SettingKeys.ORIENTATION_HACK).get());
            if (or >360)
                or = or - 360;
            orientation = or;
        }


        cameraParameters.setRotation(orientation);
        Log.d(TAG, "SetPictureOrientation");
        SetParametersToCamera(cameraParameters);

    }

    @Override
    public float getCurrentExposuretime()
    {
        Camera.Parameters parameters = ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCameraParameters();
        if (settingsManager.getFrameWork() == Frameworks.MTK) {
            if (parameters.get(FreedApplication.getStringFromRessources(R.string.eng_capture_shutter_speed)) != null) {
                if (Float.parseFloat(parameters.get(FreedApplication.getStringFromRessources(R.string.eng_capture_shutter_speed))) == 0) {
                    return 0;
                } else
                    return Float.parseFloat(parameters.get(FreedApplication.getStringFromRessources(R.string.eng_capture_shutter_speed)))/ 1000000;
            } else if (parameters.get(FreedApplication.getStringFromRessources(R.string.cap_ss)) != null) {
                if (Float.parseFloat(parameters.get(FreedApplication.getStringFromRessources(R.string.cap_ss))) == 0) {
                    return 0;
                } else
                    return Float.parseFloat(parameters.get(FreedApplication.getStringFromRessources(R.string.cap_ss)))/ 1000000;
            } else
                return 0;
        }
        else
        {
            if (parameters.get(FreedApplication.getStringFromRessources(R.string.cur_exposure_time))!= null)
                return Float.parseFloat(parameters.get(FreedApplication.getStringFromRessources(R.string.cur_exposure_time)))/1000;
        }
        return 0;
    }

    @Override
    public int getCurrentIso() {
        Camera.Parameters parameters = ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCameraParameters();
        if (settingsManager.getFrameWork() == Frameworks.MTK)
        {
            if(parameters.get(FreedApplication.getStringFromRessources(R.string.eng_capture_sensor_gain))!= null) {
                if (Integer.parseInt(parameters.get(FreedApplication.getStringFromRessources(R.string.eng_capture_sensor_gain))) == 0) {
                    return 0;
                }
                return Integer.parseInt(parameters.get(FreedApplication.getStringFromRessources(R.string.eng_capture_sensor_gain))) / 256 * 100;
            }
            else if(parameters.get(FreedApplication.getStringFromRessources(R.string.cap_isp_g))!= null)
            {
                if (Integer.parseInt(parameters.get(FreedApplication.getStringFromRessources(R.string.cap_isp_g))) == 0) {
                    return 0;
                }
                return Integer.parseInt(parameters.get(FreedApplication.getStringFromRessources(R.string.cap_isp_g))) / 256 * 100;
            }
            else
                return 0;
        }
        else
        {
            if (parameters.get(FreedApplication.getStringFromRessources(R.string.cur_iso))!= null)
                return Integer.parseInt(parameters.get(FreedApplication.getStringFromRessources(R.string.cur_iso)));
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

        int or = Integer.parseInt(settingsManager.get(SettingKeys.ORIENTATION_HACK).get());
        ((CameraHolder) cameraUiWrapper.getCameraHolder()).SetCameraRotation(or);
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
        CameraThreadHandler.restartPreviewAsync();
        cameraParameters.set("slow_shutter",Shutter);
        cameraParameters.set("slow_shutter_addition", "1");
        Log.d(TAG,"SetZTE_RESET_AE_SETSHUTTER");
        SetParametersToCamera(cameraParameters);


    }
}
