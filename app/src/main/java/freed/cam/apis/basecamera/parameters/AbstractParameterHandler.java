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

    private final HashMap<SettingsManager.Key, ParameterInterface> parameterHashMap = new HashMap<>();

    protected C cameraUiWrapper;
    protected SettingsManager settingsManager;
    protected PreviewController previewController;


    protected AbstractParameterHandler(C cameraUiWrapper) {
        this.cameraUiWrapper = cameraUiWrapper;
        settingsManager = FreedApplication.settingsManager();
        previewController = ActivityFreeDcamMain.previewController();
        add(SettingsManager.GuideList, new GuideList());
        add(SettingsManager.LOCATION_MODE, new GpsParameter(cameraUiWrapper));
        add(SettingsManager.INTERVAL_DURATION, new IntervalDurationParameter(cameraUiWrapper));
        add(SettingsManager.INTERVAL_SHUTTER_SLEEP, new IntervalShutterSleepParameter(cameraUiWrapper));
        add(SettingsManager.HorizontLvl, new Horizont());
        add(SettingsManager.SD_SAVE_LOCATION, new SDModeParameter());
        PreviewPostProcessingMode previewPostProcessingMode = new PreviewPostProcessingMode(SettingsManager.PREVIEW_POST_PROCESSING_MODE);
        add(SettingsManager.NightOverlay, new NightOverlayParameter(cameraUiWrapper));
        add(SettingsManager.PREVIEW_POST_PROCESSING_MODE, previewPostProcessingMode);
        add(settingsManager.FOCUSPEAK_COLOR, new FocusPeakColorMode(previewController, SettingsManager.FOCUSPEAK_COLOR));
        add(settingsManager.Focuspeak, new FocusPeakMode(cameraUiWrapper, SettingKeys.Focuspeak));
        add(settingsManager.HISTOGRAM, new HistogramParameter(cameraUiWrapper));
        add(settingsManager.CLIPPING, new ClippingMode(cameraUiWrapper,SettingKeys.CLIPPING));
        add(SettingsManager.selfTimer, new SelfTimerParameter(SettingsManager.selfTimer));
        add(SettingsManager.M_ZEBRA_HIGH,new ZebraManualHighParameter(SettingsManager.M_ZEBRA_HIGH,previewController));
        add(SettingsManager.M_ZEBRA_LOW,new ZebraManualLowParameter(SettingsManager.M_ZEBRA_LOW,previewController));
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
        if (!settingsManager.getGlobal(SettingsManager.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.off.name())) {
            get(settingsManager.FOCUSPEAK_COLOR).setViewState(AbstractParameter.ViewState.Visible);
            get(settingsManager.Focuspeak).setViewState(AbstractParameter.ViewState.Visible);
            get(settingsManager.HISTOGRAM).setViewState(AbstractParameter.ViewState.Visible);
            get(settingsManager.CLIPPING).setViewState(AbstractParameter.ViewState.Visible);
        }
        else
        {
            get(settingsManager.FOCUSPEAK_COLOR).setViewState(AbstractParameter.ViewState.Hidden);
            get(settingsManager.Focuspeak).setViewState(AbstractParameter.ViewState.Hidden);
            get(settingsManager.HISTOGRAM).setViewState(AbstractParameter.ViewState.Hidden);
            get(settingsManager.CLIPPING).setViewState(AbstractParameter.ViewState.Hidden);
            get(SettingKeys.M_ZEBRA_HIGH).setViewState(AbstractParameter.ViewState.Hidden);
            get(SettingKeys.M_ZEBRA_LOW).setViewState(AbstractParameter.ViewState.Hidden);
        }
        if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.OpenGL.name()))
        {
            get(SettingKeys.M_ZEBRA_HIGH).setViewState(AbstractParameter.ViewState.Visible);
            get(SettingKeys.M_ZEBRA_LOW).setViewState(AbstractParameter.ViewState.Visible);
        }
    }

    @Override
    public void add(SettingsManager.Key parameters, ParameterInterface parameterInterface)
    {
        Log.d(TAG, "add "+ FreedApplication.getStringFromRessources(parameters.getRessourcesStringID()));
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
            Log.d(TAG, "setAppSettingsToCamera " + FreedApplication.getStringFromRessources(parametertolook.getRessourcesStringID()) + " isSupported:" + settingMode.isSupported());
            if (settingMode != null && settingMode.isSupported() && parameter != null && parameter.getStringValue() != null)
            {
                if (TextUtils.isEmpty(settingMode.get()))
                    return;
                String toset = settingMode.get();
                Log.d(TAG,"set " + FreedApplication.getStringFromRessources(parametertolook.getRessourcesStringID())+ " to :" + toset);
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
                Log.d(TAG,"set " + FreedApplication.getStringFromRessources(parametertolook.getRessourcesStringID())+ " to :" + toset);
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
                Log.d(TAG, parameter.getClass().getSimpleName());
                if (TextUtils.isEmpty(settingMode.get()) || settingMode.get() == null) {
                    String tmp = parameter.getIntValue() + "";
                    Log.d(TAG, "settingmode is empty: " + FreedApplication.getStringFromRessources(parametertolook.getRessourcesStringID()) + " get from parameter: " + tmp);
                    settingMode.set(tmp);
                } else {
                    try {
                        int tmp = Integer.parseInt(settingMode.get());
                        Log.d(TAG, "settingmode : " +  FreedApplication.getStringFromRessources(parametertolook.getRessourcesStringID()) + " set from settings: " + tmp);
                        parameter.setIntValue(tmp, setToCamera);
                    } catch (NumberFormatException ex) {
                        Log.WriteEx(ex);
                    }

                }
            }
        }
    }
}
