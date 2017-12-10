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
import freed.cam.apis.camera1.parameters.modes.PictureFormatHandler;
import freed.cam.apis.camera2.parameters.manual.ManualToneMapCurveApi2;
import freed.settings.AppSettingsManager;
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

    private final HashMap<Parameters, ParameterInterface> parameterHashMap = new HashMap<>();

    /**
     * Holds the UI/Main Thread
     */
    protected Handler uiHandler;

    protected CameraWrapperInterface cameraUiWrapper;

    /*public ParameterInterface ManualBrightness;
    public ParameterInterface ManualSharpness;
    public ParameterInterface ManualContrast;
    public ParameterInterface ManualSaturation;
    public ParameterInterface ManualExposure;
    public ParameterInterface ManualConvergence;
    public ParameterInterface ManualFocus;
    public ParameterInterface ManualShutter;
    public ParameterInterface ManualFNumber;
    public ParameterInterface Burst;
    public ParameterInterface CCT;
    public ParameterInterface FX;
    public ParameterInterface ManualIso;
    public ParameterInterface Zoom;
    public ParameterInterface ProgramShift;
    public ParameterInterface PreviewZoom;


    public ParameterInterface ColorMode;
    public ParameterInterface ExposureMode;
    public ParameterInterface AE_PriorityMode;
    public ParameterInterface FlashMode;
    public ParameterInterface IsoMode;
    public ParameterInterface AntiBandingMode;
    public ParameterInterface WhiteBalanceMode;
    public ParameterInterface PictureSize;
    public ParameterInterface PictureFormat;
    public ParameterInterface HDRMode;
    public ParameterInterface JpegQuality;
    //defcomg was here
    public ParameterInterface GuideList;
    //done
    public ParameterInterface ImagePostProcessing;
    public ParameterInterface PreviewSize;
    public ParameterInterface PreviewFPS;
    public ParameterInterface PreviewFormat;
    public ParameterInterface PreviewFpsRange;
    public ParameterInterface SceneMode;
    public ParameterInterface FocusMode;
    public ParameterInterface RedEye;
    public ParameterInterface LensShade;
    public ParameterInterface ZSL;
    public ParameterInterface SceneDetect;
    public ParameterInterface Denoise;
    //5/26/2017
    public ParameterInterface PDAF;
    public ParameterInterface TNR;
    public ParameterInterface TNR_V;
    public ParameterInterface RDI;
    public ParameterInterface TruePotrait;
    public ParameterInterface ReFocus;
    public ParameterInterface SeeMore;
    public ParameterInterface OptiZoom;
    public ParameterInterface ChromaFlash;


    public ParameterInterface DigitalImageStabilization;
    public ParameterInterface VideoStabilization;
    public ParameterInterface MemoryColorEnhancement;
    public ParameterInterface NightMode;
    public ParameterInterface NonZslManualMode;
    public ParameterInterface AE_Bracket;
    public ParameterInterface ExposureLock;
    public ParameterInterface CDS_Mode;

    public ParameterInterface HTCVideoMode;
    public ParameterInterface HTCVideoModeHSR;
    public ParameterInterface VideoProfiles;
    public ParameterInterface VideoSize;
    public ParameterInterface VideoHDR;
    public ParameterInterface VideoHighFramerateVideo;
    public ParameterInterface LensFilter;
    public ParameterInterface Horizont;
    public ParameterInterface ae_TargetFPS;

    //SonyApi
    public ParameterInterface ContShootMode;
    public ParameterInterface ContShootModeSpeed;
    public ParameterInterface ObjectTracking;
    public ParameterInterface PostViewSize;
    public ParameterInterface Focuspeak;
    public ParameterInterface Module;
    public ParameterInterface ZoomSetting;
    public ParameterInterface NightOverlay;

    //huawei
    public ParameterInterface dualPrimaryCameraMode;





    //camera2 modes
    public ParameterInterface EdgeMode;
    public ParameterInterface ColorCorrectionMode;
    public ParameterInterface HotPixelMode;
    public ParameterInterface ToneMapMode;
    public ParameterInterface black;
    public ParameterInterface shadows;
    public ParameterInterface midtones;
    public ParameterInterface highlights;
    public ParameterInterface white;
    public ManualToneMapCurveApi2.ToneCurveParameter toneCurveParameter;
    public ParameterInterface ControlMode;

    public ParameterInterface oismode;

    public ParameterInterface SdSaveLocation;

    public ParameterInterface locationParameter;

    public boolean IntervalCapture;
    public boolean IntervalCaptureFocusSet;

    public ParameterInterface IntervalDuration;
    public ParameterInterface IntervalShutterSleep;

    public ParameterInterface opcode;
    public ParameterInterface bayerformat;
    public ParameterInterface matrixChooser;
    public ParameterInterface tonemapChooser;
    public ParameterInterface scalePreview;*/

    private boolean isDngActive;
    public boolean IsDngActive(){ return isDngActive; }
    public void SetDngActive(boolean active) {
        isDngActive = active;}

    public AbstractParameterHandler(CameraWrapperInterface cameraUiWrapper) {
        this.cameraUiWrapper = cameraUiWrapper;
        uiHandler = new Handler(Looper.getMainLooper());
        add(Parameters.GuideList, new GuideList());
        add(Parameters.locationParameter, new LocationParameter(cameraUiWrapper));
        add(Parameters.IntervalDuration, new IntervalDurationParameter(cameraUiWrapper));
        add(Parameters.IntervalShutterSleep, new IntervalShutterSleepParameter(cameraUiWrapper));
        add(Parameters.HorizontLvl, new Horizont());
        add(Parameters.SdSaveLocation, new SDModeParameter());
        add(Parameters.NightOverlay, new NightOverlayParameter(cameraUiWrapper));
    }

    public void add(Parameters parameters, ParameterInterface parameterInterface)
    {
        if (!parameterHashMap.containsKey(parameters))
            parameterHashMap.put(parameters, parameterInterface);
    }

    public ParameterInterface get(Parameters parameters)
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
        setMode(get(Parameters.locationParameter), AppSettingsManager.getInstance().SETTING_LOCATION);
        setAppSettingsToCamera(get(Parameters.ColorMode),AppSettingsManager.getInstance().getInstance().colorMode);
        setAppSettingsToCamera(get(Parameters.ExposureMode),AppSettingsManager.getInstance().getInstance().exposureMode);
        setAppSettingsToCamera(get(Parameters.FlashMode),AppSettingsManager.getInstance().getInstance().flashMode);
        setAppSettingsToCamera(get(Parameters.IsoMode),AppSettingsManager.getInstance().getInstance().isoMode);
        setAppSettingsToCamera(get(Parameters.AntiBandingMode),AppSettingsManager.getInstance().getInstance().antiBandingMode);
        setAppSettingsToCamera(get(Parameters.WhiteBalanceMode),AppSettingsManager.getInstance().getInstance().whiteBalanceMode);
        setAppSettingsToCamera(get(Parameters.PictureSize),AppSettingsManager.getInstance().getInstance().pictureSize);
        setAppSettingsToCamera(get(Parameters.PictureFormat),AppSettingsManager.getInstance().getInstance().pictureFormat);
        setAppSettingsToCamera(get(Parameters.bayerformat),AppSettingsManager.getInstance().getInstance().rawPictureFormat);
        setAppSettingsToCamera(get(Parameters.oismode),AppSettingsManager.getInstance().getInstance().opticalImageStabilisation);
        setAppSettingsToCamera(get(Parameters.JpegQuality),AppSettingsManager.getInstance().getInstance().jpegQuality);
        setAppSettingsToCamera(get(Parameters.GuideList), AppSettingsManager.getInstance().getInstance().guide);
        setAppSettingsToCamera(get(Parameters.ImagePostProcessing),AppSettingsManager.getInstance().getInstance().imagePostProcessing);
        setAppSettingsToCamera(get(Parameters.SceneMode),AppSettingsManager.getInstance().getInstance().sceneMode);
        setAppSettingsToCamera(get(Parameters.FocusMode),AppSettingsManager.getInstance().getInstance().focusMode);
        setAppSettingsToCamera(get(Parameters.RedEye),AppSettingsManager.getInstance().getInstance().redEyeMode);
        setAppSettingsToCamera(get(Parameters.LensShade),AppSettingsManager.getInstance().getInstance().lenshade);
        setAppSettingsToCamera(get(Parameters.ZSL),AppSettingsManager.getInstance().getInstance().zeroshutterlag);
        setAppSettingsToCamera(get(Parameters.SceneDetect),AppSettingsManager.getInstance().getInstance().sceneDetectMode);
        setAppSettingsToCamera(get(Parameters.Denoise),AppSettingsManager.getInstance().getInstance().denoiseMode);
        setAppSettingsToCamera(get(Parameters.DigitalImageStabilization),AppSettingsManager.getInstance().getInstance().digitalImageStabilisationMode);
        setAppSettingsToCamera(get(Parameters.MemoryColorEnhancement),AppSettingsManager.getInstance().getInstance().memoryColorEnhancement);
        setMode(get(Parameters.NightMode), AppSettingsManager.getInstance().NIGHTMODE);
        setAppSettingsToCamera(get(Parameters.NonZslManualMode), AppSettingsManager.getInstance().getInstance().nonZslManualMode);

        setAppSettingsToCamera(get(Parameters.VideoProfiles), AppSettingsManager.getInstance().getInstance().videoProfile);
        setAppSettingsToCamera(get(Parameters.VideoHDR), AppSettingsManager.getInstance().getInstance().videoHDR);
        setAppSettingsToCamera(get(Parameters.VideoSize), AppSettingsManager.getInstance().getInstance().videoSize);
        setAppSettingsToCamera(get(Parameters.VideoStabilization),AppSettingsManager.getInstance().getInstance().videoStabilisation);
        setAppSettingsToCamera(get(Parameters.VideoHighFramerate),AppSettingsManager.getInstance().getInstance().videoHFR);
        setAppSettingsToCamera(get(Parameters.WhiteBalanceMode),AppSettingsManager.getInstance().getInstance().whiteBalanceMode);
        setAppSettingsToCamera(get(Parameters.ColorCorrectionMode), AppSettingsManager.getInstance().getInstance().colorCorrectionMode);
        setAppSettingsToCamera(get(Parameters.EdgeMode), AppSettingsManager.getInstance().edgeMode);
        setAppSettingsToCamera(get(Parameters.HotPixelMode), AppSettingsManager.getInstance().hotpixelMode);
        setAppSettingsToCamera(get(Parameters.ToneMapMode), AppSettingsManager.getInstance().toneMapMode);
        setAppSettingsToCamera(get(Parameters.ControlMode), AppSettingsManager.getInstance().controlMode);
        setAppSettingsToCamera(get(Parameters.IntervalDuration),AppSettingsManager.getInstance().intervalDuration);
        setAppSettingsToCamera(get(Parameters.IntervalShutterSleep), AppSettingsManager.getInstance().interval);
        setMode(get(Parameters.HorizontLvl), AppSettingsManager.getInstance().SETTING_HORIZONT);

        setAppSettingsToCamera(get(Parameters.HDRMode), AppSettingsManager.getInstance().hdrMode);

        setAppSettingsToCamera(get(Parameters.matrixChooser), AppSettingsManager.getInstance().matrixset);
        setAppSettingsToCamera(get(Parameters.dualPrimaryCameraMode), AppSettingsManager.getInstance().dualPrimaryCameraMode);
        setAppSettingsToCamera(get(Parameters.RDI), AppSettingsManager.getInstance().rawdumpinterface);
        setAppSettingsToCamera(get(Parameters.Ae_TargetFPS), AppSettingsManager.getInstance().ae_TagetFPS);
    }

    public void setManualSettingsToParameters()
    {
        setManualMode(get(Parameters.M_Contrast), AppSettingsManager.getInstance().manualContrast);
        setManualMode(get(Parameters.M_3D_Convergence),AppSettingsManager.getInstance().manualConvergence);
        setManualMode(get(Parameters.M_ExposureCompensation), AppSettingsManager.getInstance().manualExposureCompensation);
        setManualMode(get(Parameters.M_Focus), AppSettingsManager.getInstance().manualFocus);
        setManualMode(get(Parameters.M_Shaprness),AppSettingsManager.getInstance().manualSharpness);
        setManualMode(get(Parameters.M_ExposureTime), AppSettingsManager.getInstance().manualExposureTime);
        setManualMode(get(Parameters.M_Brightness), AppSettingsManager.getInstance().manualBrightness);
        setManualMode(get(Parameters.M_ManualIso), AppSettingsManager.getInstance().manualIso);
        setManualMode(get(Parameters.M_Saturation), AppSettingsManager.getInstance().manualSaturation);
        setManualMode(get(Parameters.M_Whitebalance),AppSettingsManager.getInstance().manualWhiteBalance);
    }

    protected void SetParameters()
    {}

    protected void setMode(ParameterInterface parameter, String settings_key)
    {
        if (parameter != null && parameter.IsSupported() && settings_key != null && !TextUtils.isEmpty(settings_key))
        {
            Log.d(TAG, parameter.getClass().getSimpleName() + " load settings: " + settings_key);
            if (TextUtils.isEmpty(AppSettingsManager.getInstance().getApiString(settings_key)) || AppSettingsManager.getInstance().getApiString(settings_key) == null)
            {
                String tmp = parameter.GetStringValue();
                Log.d(TAG, settings_key + " is empty, set default from camera : " +tmp);
                AppSettingsManager.getInstance().setApiString(settings_key, tmp);
            }
            else
            {
                String tmp = AppSettingsManager.getInstance().getApiString(settings_key);
                Log.d(TAG, "Found AppSetting: "+settings_key+" set to: " + tmp);
                parameter.SetValue(tmp, false);
            }
        }
    }

    protected void setAppSettingsToCamera(ParameterInterface parameter, AppSettingsManager.SettingMode settingMode)
    {
        if (settingMode.isSupported() && parameter != null && parameter.GetStringValue() != null)
        {
            if (TextUtils.isEmpty(settingMode.get()))
                return;
            String toset = settingMode.get();
            Log.d(TAG,"set to :" + toset);
            if (TextUtils.isEmpty(toset) || toset.equals("none"))
                settingMode.set(parameter.GetStringValue());
            else
                parameter.SetValue(toset,false);
            parameter.fireStringValueChanged(toset);
        }
    }

    protected void setManualMode(ParameterInterface parameter, AppSettingsManager.SettingMode settingMode)
    {
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
                    parameter.SetValue(tmp);
                }
                catch (NumberFormatException ex)
                {
                    Log.WriteEx(ex);
                }

            }
        }
    }
}
