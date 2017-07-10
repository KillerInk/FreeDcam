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

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.GuideList;
import freed.cam.apis.basecamera.parameters.modes.Horizont;
import freed.cam.apis.basecamera.parameters.modes.IntervalDurationParameter;
import freed.cam.apis.basecamera.parameters.modes.IntervalShutterSleepParameter;
import freed.cam.apis.basecamera.parameters.modes.LocationParameter;
import freed.cam.apis.basecamera.parameters.modes.NightOverlayParameter;
import freed.cam.apis.basecamera.parameters.modes.SDModeParameter;
import freed.utils.AppSettingsManager;
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

    protected AppSettingsManager appSettingsManager;

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
        this.appSettingsManager = cameraUiWrapper.getAppSettingsManager();

        GuideList = new GuideList(appSettingsManager);
        locationParameter = new LocationParameter(cameraUiWrapper);
        IntervalDuration = new IntervalDurationParameter(cameraUiWrapper);
        IntervalShutterSleep = new IntervalShutterSleepParameter(cameraUiWrapper);
        Horizont = new Horizont();
        SdSaveLocation = new SDModeParameter(appSettingsManager);
        NightOverlay = new NightOverlayParameter(cameraUiWrapper);

    }

    public abstract void SetFocusAREA(Rect focusAreas);

    public abstract void SetPictureOrientation(int or);

    public abstract float[] getFocusDistances();

    public abstract float getCurrentExposuretime();

    public abstract int getCurrentIso();

    public void SetAppSettingsToParameters()
    {
        setMode(locationParameter, AppSettingsManager.SETTING_LOCATION);
        setAppSettingsToCamera(ColorMode,appSettingsManager.colorMode);
        setAppSettingsToCamera(ExposureMode,appSettingsManager.exposureMode);
        setAppSettingsToCamera(FlashMode,appSettingsManager.flashMode);
        setAppSettingsToCamera(IsoMode,appSettingsManager.isoMode);
        setAppSettingsToCamera(AntiBandingMode,appSettingsManager.antiBandingMode);
        setAppSettingsToCamera(WhiteBalanceMode,appSettingsManager.whiteBalanceMode);
        setAppSettingsToCamera(PictureSize,appSettingsManager.pictureSize);
        setAppSettingsToCamera(PictureFormat,appSettingsManager.pictureFormat);
        setAppSettingsToCamera(bayerformat,appSettingsManager.rawPictureFormat);
        setAppSettingsToCamera(oismode,appSettingsManager.opticalImageStabilisation);
        setAppSettingsToCamera(JpegQuality,appSettingsManager.jpegQuality);
        setAppSettingsToCamera(GuideList, appSettingsManager.guide);
        setAppSettingsToCamera(ImagePostProcessing,appSettingsManager.imagePostProcessing);
        setAppSettingsToCamera(SceneMode,appSettingsManager.sceneMode);
        setAppSettingsToCamera(FocusMode,appSettingsManager.focusMode);
        setAppSettingsToCamera(RedEye,appSettingsManager.redEyeMode);
        setAppSettingsToCamera(LensShade,appSettingsManager.lenshade);
        setAppSettingsToCamera(ZSL,appSettingsManager.zeroshutterlag);
        setAppSettingsToCamera(SceneDetect,appSettingsManager.sceneDetectMode);
        setAppSettingsToCamera(Denoise,appSettingsManager.denoiseMode);
        setAppSettingsToCamera(DigitalImageStabilization,appSettingsManager.digitalImageStabilisationMode);
        setAppSettingsToCamera(MemoryColorEnhancement,appSettingsManager.memoryColorEnhancement);
        setMode(NightMode, AppSettingsManager.NIGHTMODE);
        setAppSettingsToCamera(NonZslManualMode, appSettingsManager.nonZslManualMode);

        setAppSettingsToCamera(VideoProfiles, appSettingsManager.videoProfile);
        setAppSettingsToCamera(VideoHDR, appSettingsManager.videoHDR);
        setAppSettingsToCamera(VideoSize, appSettingsManager.videoSize);
        setAppSettingsToCamera(VideoStabilization,appSettingsManager.videoStabilisation);
        setAppSettingsToCamera(VideoHighFramerateVideo,appSettingsManager.videoHFR);
        setAppSettingsToCamera(WhiteBalanceMode,appSettingsManager.whiteBalanceMode);
        setAppSettingsToCamera(ColorCorrectionMode, appSettingsManager.colorCorrectionMode);
        setAppSettingsToCamera(EdgeMode, appSettingsManager.edgeMode);
        setAppSettingsToCamera(EdgeMode,appSettingsManager.edgeMode);
        setAppSettingsToCamera(HotPixelMode, appSettingsManager.hotpixelMode);
        setAppSettingsToCamera(ToneMapMode, appSettingsManager.toneMapMode);
        setAppSettingsToCamera(ControlMode, appSettingsManager.controlMode);
        setAppSettingsToCamera(IntervalDuration,appSettingsManager.intervalDuration);
        setAppSettingsToCamera(IntervalShutterSleep, appSettingsManager.interval);
        setMode(Horizont, AppSettingsManager.SETTING_HORIZONT);

        setAppSettingsToCamera(HDRMode, appSettingsManager.hdrMode);

        setAppSettingsToCamera(matrixChooser, appSettingsManager.matrixset);
        setAppSettingsToCamera(dualPrimaryCameraMode, appSettingsManager.dualPrimaryCameraMode);
        setAppSettingsToCamera(RDI, appSettingsManager.rawdumpinterface);
        setAppSettingsToCamera(ae_TargetFPS, appSettingsManager.ae_TagetFPS);

        //setManualMode(ManualContrast, AppSettingsManager.MCONTRAST);
        //setManualMode(ManualConvergence,AppSettingsManager.MCONVERGENCE);
        //setManualMode(ManualExposure, AppSettingsManager.MEXPOSURE);
        //setManualMode(ManualFocus, AppSettingsManager.MF);
        //setManualMode(ManualSharpness,AppSettingsManager.MSHARPNESS);
        //setManualMode(ManualShutter, AppSettingsManager.MSHUTTERSPEED);
        //setManualMode(ManualBrightness, AppSettingsManager.MBRIGHTNESS);
        //setManualMode(ManualIso, AppSettingsManager.MISO);
        //setManualMode(ManualSaturation, AppSettingsManager.MSATURATION);
        //setManualMode(CCT,AppSettingsManager.MWB);


    }

    protected void SetParameters()
    {}

    protected void setMode(ParameterInterface parameter, String settings_key)
    {
        if (parameter != null && parameter.IsSupported() && settings_key != null && !settings_key.equals(""))
        {
            Log.d(TAG, parameter.getClass().getSimpleName() + " load settings: " + settings_key);
            if (appSettingsManager.getApiString(settings_key).equals("") || appSettingsManager.getApiString(settings_key) == null)
            {
                String tmp = parameter.GetStringValue();
                Log.d(TAG, settings_key + " is empty, set default from camera : " +tmp);
                appSettingsManager.setApiString(settings_key, tmp);
            }
            else
            {
                String tmp = appSettingsManager.getApiString(settings_key);
                Log.d(TAG, "Found AppSetting: "+settings_key+" set to: " + tmp);
                parameter.SetValue(tmp, false);
            }
        }
    }

    protected void setAppSettingsToCamera(ParameterInterface parameter, AppSettingsManager.SettingMode settingMode)
    {
        if (settingMode.isSupported() && parameter != null && parameter.GetStringValue() != null)
        {
            String toset = settingMode.get();
            Log.d(TAG,"set to :" + toset);
            if (toset.equals("") || toset.equals("none"))
                settingMode.set(parameter.GetStringValue());
            else
                parameter.SetValue(toset,false);
            parameter.fireStringValueChanged(toset);
        }
    }

    protected void setManualMode(ParameterInterface parameter, String settings_key)
    {
        if (parameter != null && parameter.IsSupported() && settings_key != null && !settings_key.equals(""))
        {
            Log.d(TAG, parameter.getClass().getSimpleName() + " load settings: " + settings_key);
            if (appSettingsManager.getApiString(settings_key).equals("") || appSettingsManager.getApiString(settings_key).equals(null))
            {
                String tmp = parameter.GetValue()+"";
                Log.d(TAG, settings_key + " is empty, set default from camera : " +tmp);
                appSettingsManager.setApiString(settings_key, tmp);
            }
            else
            {
                try {
                    int tmp = Integer.parseInt(appSettingsManager.getApiString(settings_key));
                    Log.d(TAG, "Found AppSetting: "+settings_key+" set to: " + tmp);
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
