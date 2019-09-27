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

import android.graphics.Rect;
import android.text.TextUtils;

import java.util.HashMap;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.ClippingMode;
import freed.cam.apis.basecamera.parameters.modes.EnableRenderScriptMode;
import freed.cam.apis.basecamera.parameters.modes.FocusPeakColorMode;
import freed.cam.apis.basecamera.parameters.modes.FocusPeakMode;
import freed.cam.apis.basecamera.parameters.modes.GuideList;
import freed.cam.apis.basecamera.parameters.modes.HistogramParameter;
import freed.cam.apis.basecamera.parameters.modes.Horizont;
import freed.cam.apis.basecamera.parameters.modes.IntervalDurationParameter;
import freed.cam.apis.basecamera.parameters.modes.IntervalShutterSleepParameter;
import freed.cam.apis.basecamera.parameters.modes.GpsParameter;
import freed.cam.apis.basecamera.parameters.modes.NightOverlayParameter;
import freed.cam.apis.basecamera.parameters.modes.ParameterExternalShutter;
import freed.cam.apis.basecamera.parameters.modes.SDModeParameter;
import freed.cam.events.EventBusLifeCycle;
import freed.renderscript.RenderScriptManager;
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
public abstract class AbstractParameterHandler
{
    private final String TAG = AbstractParameterHandler.class.getSimpleName();

    private final HashMap<SettingKeys.Key, ParameterInterface> parameterHashMap = new HashMap<>();

    protected CameraWrapperInterface cameraUiWrapper;


    protected AbstractParameterHandler(CameraWrapperInterface cameraUiWrapper) {
        this.cameraUiWrapper = cameraUiWrapper;
        add(SettingKeys.GuideList, new GuideList());
        add(SettingKeys.LOCATION_MODE, new GpsParameter(cameraUiWrapper));
        add(SettingKeys.INTERVAL_DURATION, new IntervalDurationParameter(cameraUiWrapper));
        add(SettingKeys.EXTERNAL_SHUTTER, new ParameterExternalShutter());
        add(SettingKeys.INTERVAL_SHUTTER_SLEEP, new IntervalShutterSleepParameter(cameraUiWrapper));
        add(SettingKeys.HorizontLvl, new Horizont());
        add(SettingKeys.SD_SAVE_LOCATION, new SDModeParameter());
        add(SettingKeys.NightOverlay, new NightOverlayParameter(cameraUiWrapper));
        if (RenderScriptManager.isSupported() && cameraUiWrapper.getFocusPeakProcessor() != null) {
            add(SettingKeys.EnableRenderScript, new EnableRenderScriptMode(cameraUiWrapper, SettingsManager.get(SettingKeys.EnableRenderScript)));
            add(SettingKeys.FOCUSPEAK_COLOR, new FocusPeakColorMode(cameraUiWrapper.getFocusPeakProcessor(), SettingKeys.FOCUSPEAK_COLOR));
            add(SettingKeys.Focuspeak, new FocusPeakMode(cameraUiWrapper));
            add(SettingKeys.HISTOGRAM, new HistogramParameter(cameraUiWrapper));
            add(SettingKeys.CLIPPING, new ClippingMode(cameraUiWrapper));
        }
    }

    public void add(SettingKeys.Key parameters, ParameterInterface parameterInterface)
    {
        Log.d(TAG, "add "+SettingsManager.getInstance().getResString(parameters.getRessourcesStringID()));
        parameterHashMap.put(parameters, parameterInterface);
    }

    public void unregisterListners()
    {
        for (EventBusLifeCycle life : parameterHashMap.values()) {
            life.stopListning();
        }
    }

    public void registerListners()
    {
        for (EventBusLifeCycle life : parameterHashMap.values()) {
            try {
                life.startListning();
            }
            catch(org.greenrobot.eventbus.EventBusException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public ParameterInterface get(SettingKeys.Key parameters)
    {
        return parameterHashMap.get(parameters);
    }

    public abstract void SetFocusAREA(Rect focusAreas);

    public abstract void SetPictureOrientation(int or);

    public abstract float[] getFocusDistances();

    public abstract float getCurrentExposuretime();

    public abstract int getCurrentIso();

    protected void SetAppSettingsToParameters()
    {
        setAppSettingsToCamera(SettingKeys.LOCATION_MODE,false);
        setAppSettingsToCamera(SettingKeys.ColorMode,false);
        setAppSettingsToCamera(SettingKeys.FlashMode,false);
        setAppSettingsToCamera(SettingKeys.IsoMode,false);
        setAppSettingsToCamera(SettingKeys.AntiBandingMode,false);
        setAppSettingsToCamera(SettingKeys.WhiteBalanceMode,false);
        setAppSettingsToCamera(SettingKeys.PictureSize,false);
        setAppSettingsToCamera(SettingKeys.RawSize,false);
        setAppSettingsToCamera(SettingKeys.PictureFormat,false);
        setAppSettingsToCamera(SettingKeys.BAYERFORMAT,false);
        setAppSettingsToCamera(SettingKeys.OIS_MODE,false);
        setAppSettingsToCamera(SettingKeys.JpegQuality,false);
        setAppSettingsToCamera(SettingKeys.GuideList,false);
        setAppSettingsToCamera(SettingKeys.ImagePostProcessing,false);
        setAppSettingsToCamera(SettingKeys.SceneMode,false);
        setAppSettingsToCamera(SettingKeys.FocusMode,false);
        setAppSettingsToCamera(SettingKeys.RedEye,false);
        setAppSettingsToCamera(SettingKeys.LensShade,false);
        setAppSettingsToCamera(SettingKeys.ZSL,false);
        setAppSettingsToCamera(SettingKeys.SceneDetect,false);
        setAppSettingsToCamera(SettingKeys.Denoise,false);
        setAppSettingsToCamera(SettingKeys.DigitalImageStabilization,false);
        setAppSettingsToCamera(SettingKeys.MemoryColorEnhancement,false);
        setMode(get(SettingKeys.NightMode), SettingsManager.NIGHTMODE);
        setAppSettingsToCamera(SettingKeys.NonZslManualMode,false);

        setAppSettingsToCamera(SettingKeys.VideoProfiles,false);
        setAppSettingsToCamera(SettingKeys.VideoHDR,false);
        setAppSettingsToCamera(SettingKeys.VideoSize,false);
        setAppSettingsToCamera(SettingKeys.VideoStabilization,false);
        setAppSettingsToCamera(SettingKeys.VideoHighFramerate,false);
        setAppSettingsToCamera(SettingKeys.WhiteBalanceMode,false);
        setAppSettingsToCamera(SettingKeys.COLOR_CORRECTION_MODE,false);
        setAppSettingsToCamera(SettingKeys.EDGE_MODE,false);
        setAppSettingsToCamera(SettingKeys.HOT_PIXEL_MODE,false);
        setAppSettingsToCamera(SettingKeys.TONE_MAP_MODE,false);
        setAppSettingsToCamera(SettingKeys.CONTROL_MODE,false);
        setAppSettingsToCamera(SettingKeys.INTERVAL_DURATION,false);
        setAppSettingsToCamera(SettingKeys.INTERVAL_SHUTTER_SLEEP,false);
        setAppSettingsToCamera(SettingKeys.HorizontLvl,false);

        setAppSettingsToCamera(SettingKeys.HDRMode,false);

        setAppSettingsToCamera(SettingKeys.MATRIX_SET,false);
        setAppSettingsToCamera(SettingKeys.dualPrimaryCameraMode,false);
        setAppSettingsToCamera(SettingKeys.RDI,false);
        setAppSettingsToCamera(SettingKeys.Ae_TargetFPS,false);
        setAppSettingsToCamera(SettingKeys.secondarySensorSize, false);

        setAppSettingsToCamera(SettingKeys.ExposureMode,true);
        if (RenderScriptManager.isSupported() && cameraUiWrapper.getFocusPeakProcessor() != null) {
            setAppSettingsToCamera(SettingKeys.FOCUSPEAK_COLOR, true);
            setAppSettingsToCamera(SettingKeys.HISTOGRAM, true);
            setAppSettingsToCamera(SettingKeys.CLIPPING, true);
        }
    }

    public void setManualSettingsToParameters()
    {
        setManualMode(SettingKeys.M_Contrast,false);
        setManualMode(SettingKeys.M_3D_Convergence,false);
        setManualMode(SettingKeys.M_Focus,false);
        setManualMode(SettingKeys.M_Sharpness,false);
        setManualMode(SettingKeys.M_ExposureTime,false);
        setManualMode(SettingKeys.M_Brightness,false);
        setManualMode(SettingKeys.M_ManualIso,false);
        setManualMode(SettingKeys.M_Saturation,false);
        setManualMode(SettingKeys.M_Whitebalance,false);
        setManualMode(SettingKeys.M_ExposureCompensation,true);
    }

    public void SetParameters()
    {}

    private void setMode(ParameterInterface parameter, String settings_key)
    {
        if (parameter != null && settings_key != null && !TextUtils.isEmpty(settings_key))
        {
            Log.d(TAG, parameter.getClass().getSimpleName() + " load settings: " + settings_key);
            if (TextUtils.isEmpty(SettingsManager.getInstance().getApiString(settings_key)) || SettingsManager.getInstance().getApiString(settings_key) == null)
            {
                String tmp = parameter.GetStringValue();
                Log.d(TAG, settings_key + " is empty, set default from camera : " +tmp);
                SettingsManager.getInstance().setApiString(settings_key, tmp);
            }
            else
            {
                String tmp = SettingsManager.getInstance().getApiString(settings_key);
                Log.d(TAG, "Found AppSetting: "+settings_key+" set to: " + tmp);
                parameter.SetValue(tmp, false);
            }
        }
    }

    private void setAppSettingsToCamera(SettingKeys.Key parametertolook, boolean setToCamera)
    {
        if (SettingsManager.get(parametertolook) instanceof SettingMode){
            ParameterInterface parameter = get(parametertolook);
            SettingMode settingMode = (SettingMode) SettingsManager.get(parametertolook);
            if (settingMode != null && settingMode.isSupported() && parameter != null && parameter.GetStringValue() != null)
            {
                if (TextUtils.isEmpty(settingMode.get()))
                    return;
                String toset = settingMode.get();
                Log.d(TAG,"set " + SettingsManager.getInstance().getResString(parametertolook.getRessourcesStringID())+ " to :" + toset);
                if (TextUtils.isEmpty(toset) || toset.equals("none"))
                    settingMode.set(parameter.GetStringValue());
                else
                    parameter.SetValue(toset,setToCamera);
                parameter.fireStringValueChanged(toset);
            }
        }
    }

    private void setManualMode(SettingKeys.Key parametertolook, boolean setToCamera)
    {
        if (SettingsManager.get(parametertolook) instanceof SettingMode) {
            ParameterInterface parameter = get(parametertolook);
            SettingMode settingMode = (SettingMode) SettingsManager.get(parametertolook);
            if (parameter != null && settingMode != null && settingMode.isSupported()) {
                Log.d(TAG, parameter.getClass().getSimpleName());
                if (TextUtils.isEmpty(settingMode.get()) || settingMode.get() == null) {
                    String tmp = parameter.GetValue() + "";
                    Log.d(TAG, "settingmode is empty: " + SettingsManager.getInstance().getResString(parametertolook.getRessourcesStringID()) + " get from parameter: " + tmp);
                    settingMode.set(tmp);
                } else {
                    try {
                        int tmp = Integer.parseInt(settingMode.get());
                        Log.d(TAG, "settingmode : " +  SettingsManager.getInstance().getResString(parametertolook.getRessourcesStringID()) + " set from settings: " + tmp);
                        parameter.SetValue(tmp, setToCamera);
                    } catch (NumberFormatException ex) {
                        Log.WriteEx(ex);
                    }

                }
            }
        }
    }
}
