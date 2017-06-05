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
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.GuideList;
import freed.cam.apis.basecamera.parameters.modes.Horizont;
import freed.cam.apis.basecamera.parameters.modes.IntervalDurationParameter;
import freed.cam.apis.basecamera.parameters.modes.IntervalShutterSleepParameter;
import freed.cam.apis.basecamera.parameters.modes.LocationParameter;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
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

    public ManualParameterInterface ManualBrightness;
    public ManualParameterInterface ManualSharpness;
    public ManualParameterInterface ManualContrast;
    public ManualParameterInterface ManualSaturation;
    public ManualParameterInterface ManualExposure;
    public ManualParameterInterface ManualConvergence;
    public ManualParameterInterface ManualFocus;
    public ManualParameterInterface ManualShutter;
    public ManualParameterInterface ManualFNumber;
    public ManualParameterInterface Burst;
    public ManualParameterInterface CCT;
    public ManualParameterInterface FX;
    public ManualParameterInterface ManualIso;
    public ManualParameterInterface Zoom;
    public ManualParameterInterface ProgramShift;
    public ManualParameterInterface PreviewZoom;


    public ModeParameterInterface ColorMode;
    public ModeParameterInterface ExposureMode;
    public ModeParameterInterface AE_PriorityMode;
    public ModeParameterInterface FlashMode;
    public ModeParameterInterface IsoMode;
    public ModeParameterInterface AntiBandingMode;
    public ModeParameterInterface WhiteBalanceMode;
    public ModeParameterInterface PictureSize;
    public ModeParameterInterface PictureFormat;
    public ModeParameterInterface HDRMode;
    public ModeParameterInterface JpegQuality;
    //defcomg was here
    public ModeParameterInterface GuideList;
    //done
    public ModeParameterInterface ImagePostProcessing;
    public ModeParameterInterface PreviewSize;
    public ModeParameterInterface PreviewFPS;
    public ModeParameterInterface PreviewFormat;
    public ModeParameterInterface PreviewFpsRange;
    public ModeParameterInterface SceneMode;
    public ModeParameterInterface FocusMode;
    public ModeParameterInterface RedEye;
    public ModeParameterInterface LensShade;
    public ModeParameterInterface ZSL;
    public ModeParameterInterface SceneDetect;
    public ModeParameterInterface Denoise;
    //5/26/2017
    public ModeParameterInterface PDAF;
    public ModeParameterInterface TNR;
    public ModeParameterInterface TNR_V;
    public ModeParameterInterface RDI;
    public ModeParameterInterface TruePotrait;
    public ModeParameterInterface ReFocus;
    public ModeParameterInterface SeeMore;
    public ModeParameterInterface OptiZoom;
    public ModeParameterInterface ChromaFlash;


    public ModeParameterInterface DigitalImageStabilization;
    public ModeParameterInterface VideoStabilization;
    public ModeParameterInterface MemoryColorEnhancement;
    public ModeParameterInterface NightMode;
    public ModeParameterInterface NonZslManualMode;
    public ModeParameterInterface AE_Bracket;
    public ModeParameterInterface ExposureLock;
    public ModeParameterInterface CDS_Mode;

    public ModeParameterInterface HTCVideoMode;
    public ModeParameterInterface HTCVideoModeHSR;
    public ModeParameterInterface VideoProfiles;
    public ModeParameterInterface VideoSize;
    public ModeParameterInterface VideoHDR;
    public ModeParameterInterface VideoHighFramerateVideo;
    public ModeParameterInterface LensFilter;
    public ModeParameterInterface Horizont;

    //SonyApi
    public ModeParameterInterface ContShootMode;
    public ModeParameterInterface ContShootModeSpeed;
    public ModeParameterInterface ObjectTracking;
    public ModeParameterInterface PostViewSize;
    public ModeParameterInterface Focuspeak;
    public ModeParameterInterface Module;
    public ModeParameterInterface ZoomSetting;
    public ModeParameterInterface NightOverlay;

    //huawei
    public ModeParameterInterface dualPrimaryCameraMode;
    public ModeParameterInterface autoFocusMode;

    private boolean isDngActive;
    public boolean IsDngActive(){ return isDngActive; }
    public void SetDngActive(boolean active) {
        isDngActive = active;}



    //camera2 modes
    public ModeParameterInterface EdgeMode;
    public ModeParameterInterface ColorCorrectionMode;
    public ModeParameterInterface HotPixelMode;
    public ModeParameterInterface ToneMapMode;
    public ModeParameterInterface ControlMode;

    public ModeParameterInterface oismode;

    public ModeParameterInterface SdSaveLocation;

    public ModeParameterInterface locationParameter;

    public boolean IntervalCapture;
    public boolean IntervalCaptureFocusSet;

    public ModeParameterInterface IntervalDuration;
    public ModeParameterInterface IntervalShutterSleep;

    public ModeParameterInterface opcode;
    public ModeParameterInterface bayerformat;
    public ModeParameterInterface matrixChooser;
    public ModeParameterInterface scalePreview;

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
        //setMode(ImagePostProcessing, AppSettingsManager.IMAGEPOSTPROCESSINGMODE);
        setAppSettingsToCamera(SceneMode,appSettingsManager.sceneMode);
        //setMode(SceneMode, AppSettingsManager.SCENEMODE);
        setAppSettingsToCamera(FocusMode,appSettingsManager.focusMode);
        //setMode(FocusMode, AppSettingsManager.FOCUSMODE);
        setAppSettingsToCamera(RedEye,appSettingsManager.redEyeMode);
        //setMode(RedEye,AppSettingsManager.REDEYEMODE);
        setAppSettingsToCamera(LensShade,appSettingsManager.lenshade);
        //setMode(LensShade,AppSettingsManager.LENSHADEMODE);
        setAppSettingsToCamera(ZSL,appSettingsManager.zeroshutterlag);
        //setMode(ZSL, AppSettingsManager.ZSLMODE);
        //setMode(SceneDetect, AppSettingsManager.SCENEDETECTMODE);
        setAppSettingsToCamera(SceneDetect,appSettingsManager.sceneDetectMode);
        setAppSettingsToCamera(Denoise,appSettingsManager.denoiseMode);
        //setMode(Denoise, AppSettingsManager.DENOISETMODE);
        //setMode(DigitalImageStabilization, AppSettingsManager.DIGITALIMAGESTABMODE);
        setAppSettingsToCamera(DigitalImageStabilization,appSettingsManager.digitalImageStabilisationMode);
        //setMode(MemoryColorEnhancement, AppSettingsManager.MEMORYCOLORENHANCEMENTMODE);
        setAppSettingsToCamera(MemoryColorEnhancement,appSettingsManager.memoryColorEnhancement);
        setMode(NightMode, AppSettingsManager.NIGHTMODE);
        setAppSettingsToCamera(NonZslManualMode, appSettingsManager.nonZslManualMode);

        //setMode(Histogram, AppSettingsManager.HISTOGRAM);
        setMode(VideoProfiles, AppSettingsManager.VIDEOPROFILE);
        setAppSettingsToCamera(VideoHDR, appSettingsManager.videoHDR);
        setAppSettingsToCamera(VideoSize, appSettingsManager.videoSize);
        setAppSettingsToCamera(VideoStabilization,appSettingsManager.videoStabilisation);
        setAppSettingsToCamera(VideoHighFramerateVideo,appSettingsManager.videoHFR);
        //setMode(WhiteBalanceMode,AppSettingsManager.WHITEBALANCEMODE);
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

    protected void setMode(ModeParameterInterface parameter, String settings_key)
    {
        if (parameter != null && parameter.IsSupported() && settings_key != null && !settings_key.equals(""))
        {
            Log.d(TAG, parameter.getClass().getSimpleName() + " load settings: " + settings_key);
            if (appSettingsManager.getApiString(settings_key).equals("") || appSettingsManager.getApiString(settings_key) == null)
            {
                String tmp = parameter.GetValue();
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

    protected void setAppSettingsToCamera(ModeParameterInterface parameter, AppSettingsManager.SettingMode settingMode)
    {
        if (settingMode.isSupported() && parameter != null && parameter.GetValues() != null)
        {
            String toset = settingMode.get();
            Log.d(TAG,"set to :" + toset);
            if (toset.equals("") || toset.equals("none"))
                settingMode.set(parameter.GetValue());
            else
                parameter.SetValue(toset,true);
        }
    }

    protected void setManualMode(ManualParameterInterface parameter, String settings_key)
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
