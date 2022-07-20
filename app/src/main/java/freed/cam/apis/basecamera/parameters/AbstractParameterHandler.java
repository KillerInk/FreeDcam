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

package freed.cam.apis.basecamera.parameters;

import android.text.TextUtils;

import androidx.databinding.Observable;

import java.util.HashMap;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.ZebraManualHighParameter;
import freed.cam.apis.basecamera.parameters.manual.ZebraManualLowParameter;
import freed.cam.apis.basecamera.parameters.modes.CameraSwitchParameter;
import freed.cam.apis.basecamera.parameters.modes.ClippingMode;
import freed.cam.apis.basecamera.parameters.modes.PreviewPostProcessingMode;
import freed.cam.apis.basecamera.parameters.modes.FocusPeakColorMode;
import freed.cam.apis.basecamera.parameters.modes.FocusPeakMode;
import freed.cam.apis.basecamera.parameters.modes.GpsParameter;
import freed.cam.apis.basecamera.parameters.modes.GuideList;
import freed.cam.apis.basecamera.parameters.modes.HistogramParameter;
import freed.cam.apis.basecamera.parameters.modes.Horizont;
import freed.cam.apis.basecamera.parameters.modes.IntervalDurationParameter;
import freed.cam.apis.basecamera.parameters.modes.IntervalShutterSleepParameter;
import freed.cam.apis.basecamera.parameters.modes.NightOverlayParameter;
import freed.cam.apis.basecamera.parameters.modes.SDModeParameter;
import freed.cam.apis.basecamera.parameters.modes.SelfTimerParameter;
import freed.cam.apis.basecamera.parameters.modes.ThemeMode;
import freed.cam.previewpostprocessing.PreviewController;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.SettingMode;
import freed.utils.Log;

/*
  Created by troop on 09.12.2014.
 */

/**
 * This class holds all availible parameters supported by the camera
 * Parameter can be null when unsupported.
 * Bevor accessing it, check if is not null or IsSupported
 */
public abstract class AbstractParameterHandler<C extends CameraWrapperInterface> implements ParameterHandler
{
    private final String TAG = AbstractParameterHandler.class.getSimpleName();

    private final boolean DOLOG = false;

    private void log(String s)
    {
        if (DOLOG)
            Log.d(TAG, s);
    }

    private final HashMap<SettingsManager.Key, ParameterInterface> parameterHashMap = new HashMap<>();

    protected C cameraUiWrapper;
    protected SettingsManager settingsManager;
    protected PreviewController previewController;


    protected AbstractParameterHandler(C cameraUiWrapper) {
        this.cameraUiWrapper = cameraUiWrapper;
        settingsManager = FreedApplication.settingsManager();
        previewController = ActivityFreeDcamMain.previewController();
        add(SettingKeys.CAMERA_SWITCH, new CameraSwitchParameter());
        add(SettingKeys.THEME, new ThemeMode(SettingKeys.THEME));
        add(SettingKeys.GuideList, new GuideList());
        add(SettingKeys.LOCATION_MODE, new GpsParameter(cameraUiWrapper));
        add(SettingKeys.INTERVAL_DURATION, new IntervalDurationParameter(cameraUiWrapper));
        add(SettingKeys.INTERVAL_SHUTTER_SLEEP, new IntervalShutterSleepParameter(cameraUiWrapper));
        add(SettingKeys.HorizontLvl, new Horizont());
        add(SettingKeys.SD_SAVE_LOCATION, new SDModeParameter());
        PreviewPostProcessingMode previewPostProcessingMode = new PreviewPostProcessingMode(SettingKeys.PREVIEW_POST_PROCESSING_MODE);
        add(SettingKeys.NightOverlay, new NightOverlayParameter(cameraUiWrapper));
        add(SettingKeys.PREVIEW_POST_PROCESSING_MODE, previewPostProcessingMode);
        add(SettingKeys.FOCUSPEAK_COLOR, new FocusPeakColorMode(previewController, SettingKeys.FOCUSPEAK_COLOR));
        add(SettingKeys.Focuspeak, new FocusPeakMode(cameraUiWrapper, SettingKeys.Focuspeak));
        add(SettingKeys.HISTOGRAM, new HistogramParameter(cameraUiWrapper));
        add(SettingKeys.CLIPPING, new ClippingMode(cameraUiWrapper,SettingKeys.CLIPPING));
        add(SettingKeys.selfTimer, new SelfTimerParameter(SettingsManager.selfTimer));
        add(SettingKeys.M_ZEBRA_HIGH,new ZebraManualHighParameter(SettingKeys.M_ZEBRA_HIGH,previewController));
        add(SettingKeys.M_ZEBRA_LOW,new ZebraManualLowParameter(SettingKeys.M_ZEBRA_LOW,previewController));
        applyPreviewPostprocessingVisibility();
        previewPostProcessingMode.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                applyPreviewPostprocessingVisibility();
            }
        });

    }

    private void applyPreviewPostprocessingVisibility()
    {
        try {


        if (settingsManager == null)
            return;
        if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE) == null)
            return;
        if (!settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.off.name())) {
            get(SettingKeys.FOCUSPEAK_COLOR).setViewState(AbstractParameter.ViewState.Visible);
            get(SettingKeys.Focuspeak).setViewState(AbstractParameter.ViewState.Visible);
            get(SettingKeys.HISTOGRAM).setViewState(AbstractParameter.ViewState.Visible);
            get(SettingKeys.CLIPPING).setViewState(AbstractParameter.ViewState.Visible);
        }
        else
        {
            get(SettingKeys.FOCUSPEAK_COLOR).setViewState(AbstractParameter.ViewState.Hidden);
            get(SettingKeys.Focuspeak).setViewState(AbstractParameter.ViewState.Hidden);
            get(SettingKeys.HISTOGRAM).setViewState(AbstractParameter.ViewState.Hidden);
            get(SettingKeys.CLIPPING).setViewState(AbstractParameter.ViewState.Hidden);
            get(SettingKeys.M_ZEBRA_HIGH).setViewState(AbstractParameter.ViewState.Hidden);
            get(SettingKeys.M_ZEBRA_LOW).setViewState(AbstractParameter.ViewState.Hidden);
        }
        if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.OpenGL.name()))
        {
            get(SettingKeys.M_ZEBRA_HIGH).setViewState(AbstractParameter.ViewState.Visible);
            get(SettingKeys.M_ZEBRA_LOW).setViewState(AbstractParameter.ViewState.Visible);
        }
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void add(SettingKeys.Key parameters, ParameterInterface parameterInterface)
    {
        log("add "+ FreedApplication.getStringFromRessources(parameters.getRessourcesStringID()));
        parameterHashMap.put(parameters, parameterInterface);
    }


    @Override
    public ParameterInterface get(SettingsManager.Key parameters)
    {
        return parameterHashMap.get(parameters);
    }

    @Override
    public void SetAppSettingsToParameters()
    {
        setGlobalAppSettingsToCamera(SettingsManager.LOCATION_MODE,false);
        setGlobalAppSettingsToCamera(SettingsManager.GuideList,false);
        setGlobalAppSettingsToCamera(SettingsManager.HorizontLvl,false);
        setAppSettingsToCamera(SettingsManager.ColorMode,false);
        setAppSettingsToCamera(SettingsManager.FlashMode,false);
        setAppSettingsToCamera(SettingsManager.IsoMode,false);
        setAppSettingsToCamera(SettingsManager.AntiBandingMode,false);
        setAppSettingsToCamera(SettingsManager.WhiteBalanceMode,false);
        setAppSettingsToCamera(SettingsManager.PictureSize,false);
        setAppSettingsToCamera(SettingsManager.RawSize,false);
        setAppSettingsToCamera(SettingsManager.PictureFormat,false);
        setAppSettingsToCamera(SettingsManager.BAYERFORMAT,false);
        setAppSettingsToCamera(SettingsManager.OIS_MODE,false);
        setAppSettingsToCamera(SettingsManager.JpegQuality,false);
        setAppSettingsToCamera(SettingsManager.ImagePostProcessing,false);
        setAppSettingsToCamera(SettingsManager.SceneMode,false);
        setAppSettingsToCamera(SettingsManager.FocusMode,false);
        setAppSettingsToCamera(SettingsManager.RedEye,false);
        setAppSettingsToCamera(SettingsManager.LensShade,false);
        setAppSettingsToCamera(SettingsManager.ZSL,false);
        setAppSettingsToCamera(SettingsManager.SceneDetect,false);
        setAppSettingsToCamera(SettingsManager.Denoise,false);
        setAppSettingsToCamera(SettingsManager.DigitalImageStabilization,false);
        setAppSettingsToCamera(SettingsManager.MemoryColorEnhancement,false);
        setAppSettingsToCamera(SettingsManager.NightMode,false);
        setAppSettingsToCamera(SettingsManager.NonZslManualMode,false);
        setAppSettingsToCamera(SettingsManager.VideoProfiles,false);
        setAppSettingsToCamera(SettingsManager.VideoHDR,false);
        setAppSettingsToCamera(SettingsManager.VideoSize,false);
        setAppSettingsToCamera(SettingsManager.VideoStabilization,false);
        setAppSettingsToCamera(SettingsManager.VideoHighFramerate,false);
        setAppSettingsToCamera(SettingsManager.WhiteBalanceMode,false);
        setAppSettingsToCamera(SettingsManager.COLOR_CORRECTION_MODE,false);
        setAppSettingsToCamera(SettingsManager.EDGE_MODE,false);
        setAppSettingsToCamera(SettingsManager.HOT_PIXEL_MODE,false);
        setAppSettingsToCamera(SettingsManager.DISTORTION_CORRECTION_MODE,false);
        setAppSettingsToCamera(SettingsManager.FACE_DETECTOR_MODE,false);
        setAppSettingsToCamera(SettingsManager.TONE_MAP_MODE,false);
        setAppSettingsToCamera(SettingsManager.CONTROL_MODE,false);
        setAppSettingsToCamera(SettingsManager.INTERVAL_DURATION,false);
        setAppSettingsToCamera(SettingsManager.INTERVAL_SHUTTER_SLEEP,false);
        setAppSettingsToCamera(SettingsManager.HDRMode,false);
        setAppSettingsToCamera(SettingsManager.MATRIX_SET,false);
        setAppSettingsToCamera(SettingsManager.dualPrimaryCameraMode,false);
        setAppSettingsToCamera(SettingsManager.RDI,false);
        setAppSettingsToCamera(SettingsManager.Ae_TargetFPS,false);
        setAppSettingsToCamera(SettingsManager.secondarySensorSize, false);
        setAppSettingsToCamera(SettingsManager.ExposureMode,false);
        setAppSettingsToCamera(SettingsManager.FOCUSPEAK_COLOR, true);
        setAppSettingsToCamera(SettingsManager.HISTOGRAM, true);
        setAppSettingsToCamera(SettingsManager.CLIPPING, true);
        setAppSettingsToCamera(SettingsManager.Focuspeak, true);
        setAppSettingsToCamera(SettingsManager.MFNR, true);
        setAppSettingsToCamera(SettingsManager.XIAOMI_MFNR, true);
        setManualMode(SettingsManager.M_ZEBRA_HIGH, true);
        setManualMode(SettingsManager.M_ZEBRA_LOW, true);

    }

    @Override
    public void setManualSettingsToParameters()
    {
        setManualMode(SettingsManager.M_Contrast,false);
        setManualMode(SettingsManager.M_3D_Convergence,false);
        setManualMode(SettingsManager.M_Focus,false);
        setManualMode(SettingsManager.M_Sharpness,false);
        setManualMode(SettingsManager.M_ExposureTime,false);
        setManualMode(SettingsManager.M_Brightness,false);
        setManualMode(SettingsManager.M_ManualIso,false);
        setManualMode(SettingsManager.M_Saturation,false);
        setManualMode(SettingsManager.M_Whitebalance,false);
        setManualMode(SettingsManager.M_ExposureCompensation,true);
    }

    public void SetParameters()
    {}

    private void setAppSettingsToCamera(SettingsManager.Key parametertolook, boolean setToCamera)
    {
        if (settingsManager.get(parametertolook) instanceof SettingMode){
            ParameterInterface parameter = get(parametertolook);
            SettingMode settingMode = (SettingMode) settingsManager.get(parametertolook);
            log("setAppSettingsToCamera " + FreedApplication.getStringFromRessources(parametertolook.getRessourcesStringID()) + " isSupported:" + settingMode.isSupported());
            if (settingMode != null && settingMode.isSupported() && parameter != null && parameter.getStringValue() != null)
            {
                if (TextUtils.isEmpty(settingMode.get()))
                    return;
                String toset = settingMode.get();
                log("set " + FreedApplication.getStringFromRessources(parametertolook.getRessourcesStringID())+ " to :" + toset);
                if (TextUtils.isEmpty(toset) || toset.equals("none"))
                    settingMode.set(parameter.getStringValue());
                else
                    parameter.setStringValue(toset,setToCamera);
                parameter.fireStringValueChanged(toset);
            }
        }
    }

    private void setGlobalAppSettingsToCamera(SettingsManager.GlobalKey parametertolook, boolean setToCamera)
    {
        if (settingsManager.getGlobal(parametertolook) instanceof SettingMode){
            ParameterInterface parameter = get(parametertolook);
            SettingMode settingMode = (SettingMode) settingsManager.getGlobal(parametertolook);
            if (settingMode != null && settingMode.isSupported() && parameter != null && parameter.getStringValue() != null)
            {
                if (TextUtils.isEmpty(settingMode.get()))
                    return;
                String toset = settingMode.get();
                log("set " + FreedApplication.getStringFromRessources(parametertolook.getRessourcesStringID())+ " to :" + toset);
                if (TextUtils.isEmpty(toset) || toset.equals("none"))
                    settingMode.set(parameter.getStringValue());
                else
                    parameter.setStringValue(toset,setToCamera);
                parameter.fireStringValueChanged(toset);
            }
        }
    }


    private void setManualMode(SettingsManager.Key parametertolook, boolean setToCamera)
    {
        if (settingsManager.get(parametertolook) instanceof SettingMode) {
            ParameterInterface parameter = get(parametertolook);
            SettingMode settingMode = (SettingMode) settingsManager.get(parametertolook);
            if (parameter != null && settingMode != null && settingMode.isSupported()) {
                log(parameter.getClass().getSimpleName());
                if (TextUtils.isEmpty(settingMode.get()) || settingMode.get() == null) {
                    String tmp = parameter.getIntValue() + "";
                    log("settingmode is empty: " + FreedApplication.getStringFromRessources(parametertolook.getRessourcesStringID()) + " get from parameter: " + tmp);
                    settingMode.set(tmp);
                } else {
                    try {
                        int tmp = Integer.parseInt(settingMode.get());
                        log("settingmode : " +  FreedApplication.getStringFromRessources(parametertolook.getRessourcesStringID()) + " set from settings: " + tmp);
                        parameter.setIntValue(tmp, setToCamera);
                    } catch (NumberFormatException ex) {
                        Log.WriteEx(ex);
                    }

                }
            }
        }
    }
}
