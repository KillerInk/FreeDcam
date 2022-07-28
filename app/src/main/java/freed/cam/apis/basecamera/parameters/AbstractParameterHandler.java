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
import freed.cam.apis.basecamera.parameters.modes.FocusPeakColorMode;
import freed.cam.apis.basecamera.parameters.modes.FocusPeakMode;
import freed.cam.apis.basecamera.parameters.modes.GpsParameter;
import freed.cam.apis.basecamera.parameters.modes.GuideList;
import freed.cam.apis.basecamera.parameters.modes.HistogramParameter;
import freed.cam.apis.basecamera.parameters.modes.Horizont;
import freed.cam.apis.basecamera.parameters.modes.IntervalDurationParameter;
import freed.cam.apis.basecamera.parameters.modes.IntervalShutterSleepParameter;
import freed.cam.apis.basecamera.parameters.modes.NightOverlayParameter;
import freed.cam.apis.basecamera.parameters.modes.PreviewPostProcessingMode;
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
        add(SettingKeys.GUIDE_LIST, new GuideList());
        add(SettingKeys.LOCATION_MODE, new GpsParameter(cameraUiWrapper));
        add(SettingKeys.INTERVAL_DURATION, new IntervalDurationParameter(cameraUiWrapper));
        add(SettingKeys.INTERVAL_SHUTTER_SLEEP, new IntervalShutterSleepParameter(cameraUiWrapper));
        add(SettingKeys.HORIZONT_LVL, new Horizont());
        add(SettingKeys.SD_SAVE_LOCATION, new SDModeParameter());
        PreviewPostProcessingMode previewPostProcessingMode = new PreviewPostProcessingMode(SettingKeys.PREVIEW_POST_PROCESSING_MODE);
        add(SettingKeys.NIGHT_OVERLAY, new NightOverlayParameter(cameraUiWrapper));
        add(SettingKeys.PREVIEW_POST_PROCESSING_MODE, previewPostProcessingMode);
        add(SettingKeys.FOCUSPEAK_COLOR, new FocusPeakColorMode(previewController, SettingKeys.FOCUSPEAK_COLOR));
        add(SettingKeys.FOCUSPEAK, new FocusPeakMode(cameraUiWrapper, SettingKeys.FOCUSPEAK));
        add(SettingKeys.HISTOGRAM, new HistogramParameter(cameraUiWrapper));
        add(SettingKeys.CLIPPING, new ClippingMode(cameraUiWrapper,SettingKeys.CLIPPING));
        add(SettingKeys.SELF_TIMER, new SelfTimerParameter(SettingsManager.SELF_TIMER));
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
            get(SettingKeys.FOCUSPEAK).setViewState(AbstractParameter.ViewState.Visible);
            get(SettingKeys.HISTOGRAM).setViewState(AbstractParameter.ViewState.Visible);
            get(SettingKeys.CLIPPING).setViewState(AbstractParameter.ViewState.Visible);
        }
        else
        {
            get(SettingKeys.FOCUSPEAK_COLOR).setViewState(AbstractParameter.ViewState.Hidden);
            get(SettingKeys.FOCUSPEAK).setViewState(AbstractParameter.ViewState.Hidden);
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
        setGlobalAppSettingsToCamera(SettingsManager.GUIDE_LIST,false);
        setGlobalAppSettingsToCamera(SettingsManager.HORIZONT_LVL,false);
        setAppSettingsToCamera(SettingsManager.COLOR_MODE,false);

        setAppSettingsToCamera(SettingsManager.ISO_MODE,false);
        setAppSettingsToCamera(SettingsManager.ANTI_BANDING_MODE,false);
        setAppSettingsToCamera(SettingsManager.WHITE_BALANCE_MODE,false);
        setAppSettingsToCamera(SettingsManager.PICTURE_SIZE,false);
        setAppSettingsToCamera(SettingsManager.RAW_SIZE,false);
        setAppSettingsToCamera(SettingsManager.PICTURE_FORMAT,false);
        setAppSettingsToCamera(SettingsManager.BAYERFORMAT,false);
        setAppSettingsToCamera(SettingsManager.OIS_MODE,false);
        setAppSettingsToCamera(SettingsManager.JPEG_QUALITY,false);
        setAppSettingsToCamera(SettingsManager.IMAGE_POST_PROCESSING,false);
        setAppSettingsToCamera(SettingsManager.SCENE_MODE,false);
        setAppSettingsToCamera(SettingsManager.FOCUS_MODE,false);
        setAppSettingsToCamera(SettingsManager.RED_EYE,false);
        setAppSettingsToCamera(SettingsManager.LENS_SHADE,false);
        setAppSettingsToCamera(SettingsManager.ZSL,false);
        setAppSettingsToCamera(SettingsManager.SCENE_DETECT,false);
        setAppSettingsToCamera(SettingsManager.DENOISE,false);
        setAppSettingsToCamera(SettingsManager.DIGITAL_IMAGE_STABILIZATION,false);
        setAppSettingsToCamera(SettingsManager.MEMORY_COLOR_ENHANCEMENT,false);
        setAppSettingsToCamera(SettingsManager.NIGHT_MODE,false);
        setAppSettingsToCamera(SettingsManager.NON_ZSL_MANUAL_MODE,false);
        setAppSettingsToCamera(SettingsManager.VIDEO_PROFILES,false);
        setAppSettingsToCamera(SettingsManager.VIDEO_HDR,false);
        setAppSettingsToCamera(SettingsManager.VIDEO_SIZE,false);
        setAppSettingsToCamera(SettingsManager.VIDEO_STABILIZATION,false);
        setAppSettingsToCamera(SettingsManager.VIDEO_HIGH_FRAMERATE,false);
        setAppSettingsToCamera(SettingsManager.WHITE_BALANCE_MODE,false);
        setAppSettingsToCamera(SettingsManager.COLOR_CORRECTION_MODE,false);
        setAppSettingsToCamera(SettingsManager.EDGE_MODE,false);
        setAppSettingsToCamera(SettingsManager.HOT_PIXEL_MODE,false);
        setAppSettingsToCamera(SettingsManager.DISTORTION_CORRECTION_MODE,false);
        setAppSettingsToCamera(SettingsManager.FACE_DETECTOR_MODE,false);
        setAppSettingsToCamera(SettingsManager.TONE_MAP_MODE,false);
        setAppSettingsToCamera(SettingsManager.CONTROL_MODE,false);
        setAppSettingsToCamera(SettingsManager.INTERVAL_DURATION,false);
        setAppSettingsToCamera(SettingsManager.INTERVAL_SHUTTER_SLEEP,false);
        setAppSettingsToCamera(SettingsManager.HDR_MODE,false);
        setAppSettingsToCamera(SettingsManager.MATRIX_SET,false);
        setAppSettingsToCamera(SettingsManager.DUAL_PRIMARY_CAMERA_MODE,false);
        setAppSettingsToCamera(SettingsManager.RDI,false);
        setAppSettingsToCamera(SettingsManager.AE_TARGET_FPS,false);
        setAppSettingsToCamera(SettingsManager.SECONDARY_SENSOR_SIZE, false);
        setAppSettingsToCamera(SettingsManager.EXPOSURE_MODE,false);
        setAppSettingsToCamera(SettingsManager.MFNR, false);
        setAppSettingsToCamera(SettingsManager.XIAOMI_MFNR, false);
        setAppSettingsToCamera(SettingKeys.AE_METERING,false);

        setAppSettingsToCamera(SettingsManager.FOCUSPEAK_COLOR, true);
        setAppSettingsToCamera(SettingsManager.HISTOGRAM, true);
        setAppSettingsToCamera(SettingsManager.CLIPPING, true);
        setAppSettingsToCamera(SettingsManager.FOCUSPEAK, true);
        setManualMode(SettingsManager.M_ZEBRA_HIGH, true);
        setManualMode(SettingsManager.M_ZEBRA_LOW, true);


    }

    @Override
    public void setManualSettingsToParameters()
    {
        setManualMode(SettingsManager.M_CONTRAST,false);
        setManualMode(SettingsManager.M_FOCUS,false);
        setManualMode(SettingsManager.M_SHARPNESS,false);
        setManualMode(SettingsManager.M_EXPOSURE_TIME,false);
        setManualMode(SettingsManager.M_Brightness,false);
        setManualMode(SettingsManager.M_MANUAL_ISO,false);
        setManualMode(SettingsManager.M_SATURATION,false);
        setManualMode(SettingsManager.M_WHITEBALANCE,false);
        setAppSettingsToCamera(SettingsManager.FLASH_MODE,false);
        setManualMode(SettingsManager.M_EXPOSURE_COMPENSATION,true);
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
