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

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.GuideList;
import freed.cam.apis.basecamera.parameters.modes.Horizont;
import freed.cam.apis.basecamera.parameters.modes.IntervalDurationParameter;
import freed.cam.apis.basecamera.parameters.modes.IntervalShutterSleepParameter;
import freed.cam.apis.basecamera.parameters.modes.LocationParameter;
import freed.cam.apis.basecamera.parameters.modes.NightOverlayParameter;
import freed.cam.apis.basecamera.parameters.modes.SDModeParameter;
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
    /**
     * Holds the UI/Main Thread
     */
    protected Handler uiHandler;

    protected CameraWrapperInterface cameraUiWrapper;

    public ParameterInterface ManualBrightness;
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

    private boolean isDngActive;
    public boolean IsDngActive(){ return isDngActive; }
    public void SetDngActive(boolean active) {
        isDngActive = active;}



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
    public ParameterInterface scalePreview;

    public AbstractParameterHandler(CameraWrapperInterface cameraUiWrapper) {
        this.cameraUiWrapper = cameraUiWrapper;
        uiHandler = new Handler(Looper.getMainLooper());

        GuideList = new GuideList();
        locationParameter = new LocationParameter(cameraUiWrapper);
        IntervalDuration = new IntervalDurationParameter(cameraUiWrapper);
        IntervalShutterSleep = new IntervalShutterSleepParameter(cameraUiWrapper);
        Horizont = new Horizont();
        SdSaveLocation = new SDModeParameter();
        NightOverlay = new NightOverlayParameter(cameraUiWrapper);

    }

    public abstract void SetFocusAREA(Rect focusAreas);

    public abstract void SetPictureOrientation(int or);

    public abstract float[] getFocusDistances();

    public abstract float getCurrentExposuretime();

    public abstract int getCurrentIso();

    public void SetAppSettingsToParameters()
    {
        setMode(locationParameter, AppSettingsManager.getInstance().SETTING_LOCATION);
        setAppSettingsToCamera(ColorMode,AppSettingsManager.getInstance().getInstance().colorMode);
        setAppSettingsToCamera(ExposureMode,AppSettingsManager.getInstance().getInstance().exposureMode);
        setAppSettingsToCamera(FlashMode,AppSettingsManager.getInstance().getInstance().flashMode);
        setAppSettingsToCamera(IsoMode,AppSettingsManager.getInstance().getInstance().isoMode);
        setAppSettingsToCamera(AntiBandingMode,AppSettingsManager.getInstance().getInstance().antiBandingMode);
        setAppSettingsToCamera(WhiteBalanceMode,AppSettingsManager.getInstance().getInstance().whiteBalanceMode);
        setAppSettingsToCamera(PictureSize,AppSettingsManager.getInstance().getInstance().pictureSize);
        setAppSettingsToCamera(PictureFormat,AppSettingsManager.getInstance().getInstance().pictureFormat);
        setAppSettingsToCamera(bayerformat,AppSettingsManager.getInstance().getInstance().rawPictureFormat);
        setAppSettingsToCamera(oismode,AppSettingsManager.getInstance().getInstance().opticalImageStabilisation);
        setAppSettingsToCamera(JpegQuality,AppSettingsManager.getInstance().getInstance().jpegQuality);
        setAppSettingsToCamera(GuideList, AppSettingsManager.getInstance().getInstance().guide);
        setAppSettingsToCamera(ImagePostProcessing,AppSettingsManager.getInstance().getInstance().imagePostProcessing);
        setAppSettingsToCamera(SceneMode,AppSettingsManager.getInstance().getInstance().sceneMode);
        setAppSettingsToCamera(FocusMode,AppSettingsManager.getInstance().getInstance().focusMode);
        setAppSettingsToCamera(RedEye,AppSettingsManager.getInstance().getInstance().redEyeMode);
        setAppSettingsToCamera(LensShade,AppSettingsManager.getInstance().getInstance().lenshade);
        setAppSettingsToCamera(ZSL,AppSettingsManager.getInstance().getInstance().zeroshutterlag);
        setAppSettingsToCamera(SceneDetect,AppSettingsManager.getInstance().getInstance().sceneDetectMode);
        setAppSettingsToCamera(Denoise,AppSettingsManager.getInstance().getInstance().denoiseMode);
        setAppSettingsToCamera(DigitalImageStabilization,AppSettingsManager.getInstance().getInstance().digitalImageStabilisationMode);
        setAppSettingsToCamera(MemoryColorEnhancement,AppSettingsManager.getInstance().getInstance().memoryColorEnhancement);
        setMode(NightMode, AppSettingsManager.getInstance().NIGHTMODE);
        setAppSettingsToCamera(NonZslManualMode, AppSettingsManager.getInstance().getInstance().nonZslManualMode);

        setAppSettingsToCamera(VideoProfiles, AppSettingsManager.getInstance().getInstance().videoProfile);
        setAppSettingsToCamera(VideoHDR, AppSettingsManager.getInstance().getInstance().videoHDR);
        setAppSettingsToCamera(VideoSize, AppSettingsManager.getInstance().getInstance().videoSize);
        setAppSettingsToCamera(VideoStabilization,AppSettingsManager.getInstance().getInstance().videoStabilisation);
        setAppSettingsToCamera(VideoHighFramerateVideo,AppSettingsManager.getInstance().getInstance().videoHFR);
        setAppSettingsToCamera(WhiteBalanceMode,AppSettingsManager.getInstance().getInstance().whiteBalanceMode);
        setAppSettingsToCamera(ColorCorrectionMode, AppSettingsManager.getInstance().getInstance().colorCorrectionMode);
        setAppSettingsToCamera(EdgeMode, AppSettingsManager.getInstance().edgeMode);
        setAppSettingsToCamera(HotPixelMode, AppSettingsManager.getInstance().hotpixelMode);
        setAppSettingsToCamera(ToneMapMode, AppSettingsManager.getInstance().toneMapMode);
        setAppSettingsToCamera(ControlMode, AppSettingsManager.getInstance().controlMode);
        setAppSettingsToCamera(IntervalDuration,AppSettingsManager.getInstance().intervalDuration);
        setAppSettingsToCamera(IntervalShutterSleep, AppSettingsManager.getInstance().interval);
        setMode(Horizont, AppSettingsManager.getInstance().SETTING_HORIZONT);

        setAppSettingsToCamera(HDRMode, AppSettingsManager.getInstance().hdrMode);

        setAppSettingsToCamera(matrixChooser, AppSettingsManager.getInstance().matrixset);
        setAppSettingsToCamera(dualPrimaryCameraMode, AppSettingsManager.getInstance().dualPrimaryCameraMode);
        setAppSettingsToCamera(RDI, AppSettingsManager.getInstance().rawdumpinterface);
        setAppSettingsToCamera(ae_TargetFPS, AppSettingsManager.getInstance().ae_TagetFPS);
    }

    public void setManualSettingsToParameters()
    {
        setManualMode(ManualContrast, AppSettingsManager.getInstance().manualContrast);
        setManualMode(ManualConvergence,AppSettingsManager.getInstance().manualConvergence);
        setManualMode(ManualExposure, AppSettingsManager.getInstance().manualExposureCompensation);
        setManualMode(ManualFocus, AppSettingsManager.getInstance().manualFocus);
        setManualMode(ManualSharpness,AppSettingsManager.getInstance().manualSharpness);
        setManualMode(ManualShutter, AppSettingsManager.getInstance().manualExposureTime);
        setManualMode(ManualBrightness, AppSettingsManager.getInstance().manualBrightness);
        setManualMode(ManualIso, AppSettingsManager.getInstance().manualIso);
        setManualMode(ManualSaturation, AppSettingsManager.getInstance().manualSaturation);
        setManualMode(CCT,AppSettingsManager.getInstance().manualWhiteBalance);
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
