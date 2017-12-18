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
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.util.HashMap;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.GuideList;
import freed.cam.apis.basecamera.parameters.modes.Horizont;
import freed.cam.apis.basecamera.parameters.modes.IntervalDurationParameter;
import freed.cam.apis.basecamera.parameters.modes.IntervalShutterSleepParameter;
import freed.cam.apis.basecamera.parameters.modes.LocationParameter;
import freed.cam.apis.basecamera.parameters.modes.NightOverlayParameter;
import freed.cam.apis.basecamera.parameters.modes.SDModeParameter;
import freed.settings.Settings;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 09.12.2014.
 */

/**
 * This class holds all availible parameters supported by the camera
 * Parameter can be null when unsupported.
 * Bevor accessing it, check if is not null or IsSupported
 */
public abstract class AbstractParameterHandler
{
    final String TAG = AbstractParameterHandler.class.getSimpleName();

    private final HashMap<Settings, ParameterInterface> parameterHashMap = new HashMap<>();

    /**
     * Holds the UI/Main Thread
     */
    protected Handler uiHandler;

    protected CameraWrapperInterface cameraUiWrapper;


    public AbstractParameterHandler(CameraWrapperInterface cameraUiWrapper) {
        this.cameraUiWrapper = cameraUiWrapper;
        uiHandler = new Handler(Looper.getMainLooper());
        add(Settings.GuideList, new GuideList());
        add(Settings.locationParameter, new LocationParameter(cameraUiWrapper));
        add(Settings.IntervalDuration, new IntervalDurationParameter(cameraUiWrapper));
        add(Settings.IntervalShutterSleep, new IntervalShutterSleepParameter(cameraUiWrapper));
        add(Settings.HorizontLvl, new Horizont());
        add(Settings.SdSaveLocation, new SDModeParameter());
        add(Settings.NightOverlay, new NightOverlayParameter(cameraUiWrapper));
    }

    public void add(Settings parameters, ParameterInterface parameterInterface)
    {
        Log.d(TAG, "add "+parameters.name());
        parameterHashMap.put(parameters, parameterInterface);
    }

    public ParameterInterface get(Settings parameters)
    {
        return parameterHashMap.get(parameters);
    }

    public abstract void SetFocusAREA(Rect focusAreas);

    public abstract void SetPictureOrientation(int or);

    public abstract float[] getFocusDistances();

    public abstract float getCurrentExposuretime();

    public abstract int getCurrentIso();

    public void SetAppSettingsToParameters()
    {
        setMode(get(Settings.locationParameter), SettingsManager.getInstance().SETTING_LOCATION);
        setAppSettingsToCamera(Settings.ColorMode,false);
        setAppSettingsToCamera(Settings.FlashMode,false);
        setAppSettingsToCamera(Settings.IsoMode,false);
        setAppSettingsToCamera(Settings.AntiBandingMode,false);
        setAppSettingsToCamera(Settings.WhiteBalanceMode,false);
        setAppSettingsToCamera(Settings.PictureSize,false);
        setAppSettingsToCamera(Settings.PictureFormat,false);
        setAppSettingsToCamera(Settings.bayerformat,false);
        setAppSettingsToCamera(Settings.oismode,false);
        setAppSettingsToCamera(Settings.JpegQuality,false);
        setAppSettingsToCamera(Settings.GuideList,false);
        setAppSettingsToCamera(Settings.ImagePostProcessing,false);
        setAppSettingsToCamera(Settings.SceneMode,false);
        setAppSettingsToCamera(Settings.FocusMode,false);
        setAppSettingsToCamera(Settings.RedEye,false);
        setAppSettingsToCamera(Settings.LensShade,false);
        setAppSettingsToCamera(Settings.ZSL,false);
        setAppSettingsToCamera(Settings.SceneDetect,false);
        setAppSettingsToCamera(Settings.Denoise,false);
        setAppSettingsToCamera(Settings.DigitalImageStabilization,false);
        setAppSettingsToCamera(Settings.MemoryColorEnhancement,false);
        setMode(get(Settings.NightMode), SettingsManager.getInstance().NIGHTMODE);
        setAppSettingsToCamera(Settings.NonZslManualMode,false);

        setAppSettingsToCamera(Settings.VideoProfiles,false);
        setAppSettingsToCamera(Settings.VideoHDR,false);
        setAppSettingsToCamera(Settings.VideoSize,false);
        setAppSettingsToCamera(Settings.VideoStabilization,false);
        setAppSettingsToCamera(Settings.VideoHighFramerate,false);
        setAppSettingsToCamera(Settings.WhiteBalanceMode,false);
        setAppSettingsToCamera(Settings.ColorCorrectionMode,false);
        setAppSettingsToCamera(Settings.EdgeMode,false);
        setAppSettingsToCamera(Settings.HotPixelMode,false);
        setAppSettingsToCamera(Settings.ToneMapMode,false);
        setAppSettingsToCamera(Settings.ControlMode,false);
        setAppSettingsToCamera(Settings.IntervalDuration,false);
        setAppSettingsToCamera(Settings.IntervalShutterSleep,false);
        setMode(get(Settings.HorizontLvl), SettingsManager.getInstance().SETTING_HORIZONT);

        setAppSettingsToCamera(Settings.HDRMode,false);

        setAppSettingsToCamera(Settings.matrixChooser,false);
        setAppSettingsToCamera(Settings.dualPrimaryCameraMode,false);
        setAppSettingsToCamera(Settings.RDI,false);
        setAppSettingsToCamera(Settings.Ae_TargetFPS,false);

        setAppSettingsToCamera(Settings.ExposureMode,true);
    }

    public void setManualSettingsToParameters()
    {
        setManualMode(Settings.M_Contrast,false);
        setManualMode(Settings.M_3D_Convergence,false);
        setManualMode(Settings.M_Focus,false);
        setManualMode(Settings.M_Sharpness,false);
        setManualMode(Settings.M_ExposureTime,false);
        setManualMode(Settings.M_Brightness,false);
        setManualMode(Settings.M_ManualIso,false);
        setManualMode(Settings.M_Saturation,false);
        setManualMode(Settings.M_Whitebalance,false);
        setManualMode(Settings.M_ExposureCompensation,true);
    }

    protected void SetParameters()
    {}

    protected void setMode(ParameterInterface parameter, String settings_key)
    {
        if (parameter != null && parameter.IsSupported() && settings_key != null && !TextUtils.isEmpty(settings_key))
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

    protected void setAppSettingsToCamera(Settings parametertolook, boolean setToCamera)
    {
        ParameterInterface parameter = get(parametertolook);
        SettingsManager.SettingMode settingMode = SettingsManager.get(parametertolook);
        if (settingMode != null && settingMode.isSupported() && parameter != null && parameter.GetStringValue() != null)
        {
            if (TextUtils.isEmpty(settingMode.get()))
                return;
            String toset = settingMode.get();
            Log.d(TAG,"set to :" + toset);
            if (TextUtils.isEmpty(toset) || toset.equals("none"))
                settingMode.set(parameter.GetStringValue());
            else
                parameter.SetValue(toset,setToCamera);
            parameter.fireStringValueChanged(toset);
        }
    }

    protected void setManualMode(Settings parametertolook, boolean setToCamera)
    {
        ParameterInterface parameter = get(parametertolook);
        SettingsManager.SettingMode settingMode = SettingsManager.get(parametertolook);
        if (parameter != null && parameter.IsSupported() && settingMode != null && settingMode.isSupported())
        {
            Log.d(TAG, parameter.getClass().getSimpleName());
            if (TextUtils.isEmpty(settingMode.get()) || settingMode.get() == null)
            {
                String tmp = parameter.GetValue()+"";
                settingMode.set(tmp);
            }
            else
            {
                try {
                    int tmp = Integer.parseInt(settingMode.get());
                    parameter.SetValue(tmp, setToCamera);
                }
                catch (NumberFormatException ex)
                {
                    Log.WriteEx(ex);
                }

            }
        }
    }
}
