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

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.manual.ManualParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.GuideList;
import freed.cam.apis.basecamera.parameters.modes.Horizont;
import freed.cam.apis.basecamera.parameters.modes.IntervalDurationParameter;
import freed.cam.apis.basecamera.parameters.modes.IntervalShutterSleepParameter;
import freed.cam.apis.basecamera.parameters.modes.LocationParameter;
import freed.cam.apis.basecamera.parameters.modes.ModeParameterInterface;
import freed.cam.apis.basecamera.parameters.modes.SDModeParameter;
import freed.cam.apis.camera1.parameters.device.I_Device;
import freed.utils.AppSettingsManager;
import freed.utils.Logger;

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

    /**
     * a list of listners that wait for ParametersHasLoaded() event
     * when that get thrown the parameters are fully created and rdy to use
     */
    private final ArrayList<I_ParametersLoaded> parametersLoadedListner;

    protected AppSettingsManager appSettingsManager;

    protected CameraWrapperInterface cameraUiWrapper;

    public ManualParameterInterface ManualBrightness;
    public ManualParameterInterface ManualEdge;
    public ManualParameterInterface ManualHue;
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
    public ManualParameterInterface Skintone;
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
    public ModeParameterInterface SceneMode;
    public ModeParameterInterface FocusMode;
    public ModeParameterInterface RedEye;
    public ModeParameterInterface LensShade;
    public ModeParameterInterface ZSL;
    public ModeParameterInterface SceneDetect;
    public ModeParameterInterface Denoise;
    public ModeParameterInterface DigitalImageStabilization;
    public ModeParameterInterface VideoStabilization;
    public ModeParameterInterface MemoryColorEnhancement;
    public ModeParameterInterface SkinToneEnhancment;
    public ModeParameterInterface NightMode;
    public ModeParameterInterface NonZslManualMode;
    public ModeParameterInterface AE_Bracket;
    public ModeParameterInterface Histogram;
    public ModeParameterInterface ExposureLock;
    public ModeParameterInterface CDS_Mode;

    public ModeParameterInterface HTCVideoMode;
    public ModeParameterInterface VideoProfiles;
    public ModeParameterInterface VideoSize;
    public ModeParameterInterface VideoHDR;
    public ModeParameterInterface VideoHighFramerateVideo;
    public ModeParameterInterface LensFilter;
    public ModeParameterInterface CameraMode;
    public ModeParameterInterface Horizont;

    //yet only seen on m9
    public ModeParameterInterface RdiMode;
    public ModeParameterInterface TnrMode;
    public ModeParameterInterface SecureMode;

    //SonyApi
    public ModeParameterInterface ContShootMode;
    public ModeParameterInterface ContShootModeSpeed;
    public ModeParameterInterface ObjectTracking;
    public ModeParameterInterface PostViewSize;
    public ModeParameterInterface Focuspeak;
    public ModeParameterInterface Module;
    public ModeParameterInterface ZoomSetting;
    //public AbstractModeParameter PreviewZoom;
    public boolean isExposureAndWBLocked;
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

    public ModeParameterInterface captureBurstExposures;

    public ModeParameterInterface morphoHDR;
    public ModeParameterInterface morphoHHT;

    public ModeParameterInterface aeb1;
    public ModeParameterInterface aeb2;
    public ModeParameterInterface aeb3;

    public ModeParameterInterface opcode;
    public ModeParameterInterface bayerformat;
    public ModeParameterInterface matrixChooser;
    public ModeParameterInterface imageStackMode;

    public AbstractParameterHandler(CameraWrapperInterface cameraUiWrapper) {
        this.cameraUiWrapper = cameraUiWrapper;
        uiHandler = new Handler(Looper.getMainLooper());
        this.appSettingsManager = cameraUiWrapper.GetAppSettingsManager();
        parametersLoadedListner = new ArrayList<>();
        parametersLoadedListner.clear();

        GuideList = new GuideList();
        locationParameter = new LocationParameter(cameraUiWrapper);
        IntervalDuration = new IntervalDurationParameter();
        IntervalShutterSleep = new IntervalShutterSleepParameter(cameraUiWrapper);
        Horizont = new Horizont();
        SdSaveLocation = new SDModeParameter(appSettingsManager);

    }

    public abstract I_Device getDevice();

    public abstract void SetFocusAREA(FocusRect focusAreas);
    public abstract void SetMeterAREA(FocusRect meteringAreas);

    public abstract void SetPictureOrientation(int or);

    public void SetEVBracket(String ev){}

    public void SetAppSettingsToParameters()
    {
        setMode(locationParameter, AppSettingsManager.SETTING_LOCATION);
        setMode(ColorMode, AppSettingsManager.SETTING_COLORMODE);
        setMode(ExposureMode, AppSettingsManager.SETTING_EXPOSUREMODE);
        setMode(FlashMode, AppSettingsManager.SETTING_FLASHMODE);
        setMode(IsoMode, AppSettingsManager.SETTING_ISOMODE);
        setMode(AntiBandingMode, AppSettingsManager.SETTING_ANTIBANDINGMODE);
        setMode(WhiteBalanceMode, AppSettingsManager.SETTING_WHITEBALANCEMODE);
        setMode(PictureSize, AppSettingsManager.SETTING_PICTURESIZE);
        setMode(PictureFormat, AppSettingsManager.SETTING_PICTUREFORMAT);
        setMode(bayerformat,AppSettingsManager.SETTTING_BAYERFORMAT);
        setMode(oismode, AppSettingsManager.SETTING_OIS);

        setMode(JpegQuality, AppSettingsManager.SETTING_JPEGQUALITY);
        setMode(GuideList, AppSettingsManager.SETTING_GUIDE);
        setMode(ImagePostProcessing, AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);
        setMode(SceneMode, AppSettingsManager.SETTING_SCENEMODE);
        setMode(FocusMode, AppSettingsManager.SETTING_FOCUSMODE);
        setMode(RedEye,AppSettingsManager.SETTING_REDEYE_MODE);
        setMode(LensShade,AppSettingsManager.SETTING_LENSSHADE_MODE);
        setMode(ZSL, AppSettingsManager.SETTING_ZEROSHUTTERLAG_MODE);
        setMode(SceneDetect, AppSettingsManager.SETTING_SCENEDETECT_MODE);
        setMode(Denoise, AppSettingsManager.SETTING_DENOISE_MODE);
        setMode(DigitalImageStabilization, AppSettingsManager.SETTING_DIS_MODE);
        setMode(MemoryColorEnhancement, AppSettingsManager.SETTING_MCE_MODE);
        setMode(NightMode, AppSettingsManager.SETTING_NIGHTEMODE);
        setMode(NonZslManualMode, AppSettingsManager.SETTING_NONZSLMANUALMODE);

        setMode(Histogram, AppSettingsManager.SETTING_HISTOGRAM);
        setMode(VideoProfiles, AppSettingsManager.SETTING_VIDEPROFILE);
        setMode(VideoHDR, AppSettingsManager.SETTING_VIDEOHDR);
        setMode(VideoSize, AppSettingsManager.SETTING_VIDEOSIZE);
        setMode(VideoStabilization,AppSettingsManager.SETTING_VIDEOSTABILIZATION);
        setMode(VideoHighFramerateVideo,AppSettingsManager.SETTING_HighFramerateVideo);
        setMode(WhiteBalanceMode,AppSettingsManager.SETTING_WHITEBALANCEMODE);
        setMode(ImagePostProcessing,AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);
        setMode(ColorCorrectionMode, AppSettingsManager.SETTING_COLORCORRECTION);
        setMode(EdgeMode, AppSettingsManager.SETTING_EDGE);
        setMode(HotPixelMode, AppSettingsManager.SETTING_HOTPIXEL);
        setMode(ToneMapMode, AppSettingsManager.SETTING_TONEMAP);
        setMode(ControlMode, AppSettingsManager.SETTING_CONTROLMODE);
        setMode(IntervalDuration,AppSettingsManager.SETTING_INTERVAL_DURATION);
        setMode(IntervalShutterSleep, AppSettingsManager.SETTING_INTERVAL);
        setMode(Horizont, AppSettingsManager.SETTING_HORIZONT);

        setMode(HDRMode, AppSettingsManager.SETTING_HDRMODE);
        setMode(aeb1, AppSettingsManager.SETTING_AEB1);
        setMode(aeb2, AppSettingsManager.SETTING_AEB2);
        setMode(aeb3, AppSettingsManager.SETTING_AEB3);
        setMode(captureBurstExposures, AppSettingsManager.SETTING_CAPTUREBURSTEXPOSURES);
        //setMode(AE_Bracket, AppSettingsManager.SETTING_AEBRACKET);

        setMode(morphoHDR, AppSettingsManager.SETTING_MORPHOHDR);
        setMode(morphoHHT, AppSettingsManager.SETTING_MORPHOHHT);
        setMode(matrixChooser, AppSettingsManager.SETTTING_CUSTOMMATRIX);
        setMode(imageStackMode,AppSettingsManager.SETTING_STACKMODE);

        //setMode(PreviewZoom, AppSettingsManager.SETTINGS_PREVIEWZOOM);


        setManualMode(ManualContrast, AppSettingsManager.MCONTRAST);
        setManualMode(ManualConvergence,AppSettingsManager.MCONVERGENCE);
        setManualMode(ManualExposure, AppSettingsManager.MEXPOSURE);
        //setManualMode(ManualFocus, AppSettingsManager.MF);
        setManualMode(ManualSharpness,AppSettingsManager.MSHARPNESS);
        setManualMode(ManualShutter, AppSettingsManager.MSHUTTERSPEED);
        setManualMode(ManualBrightness, AppSettingsManager.MBRIGHTNESS);
        //setManualMode(ManualIso, AppSettingsManager.MISO);
        setManualMode(ManualSaturation, AppSettingsManager.MSATURATION);
        //setManualMode(CCT,AppSettingsManager.MWB);


    }

    protected void setMode(ModeParameterInterface parameter, String settings_key)
    {
        if (parameter != null && parameter.IsSupported() && settings_key != null && !settings_key.equals(""))
        {
            Logger.d(TAG, parameter.getClass().getSimpleName() + " load settings: " + settings_key);
            if (appSettingsManager.getString(settings_key).equals("") || appSettingsManager.getString(settings_key) == null)
            {
                String tmp = parameter.GetValue();
                Logger.d(TAG, settings_key + " is empty, set default from camera : " +tmp);
                appSettingsManager.setString(settings_key, tmp);
            }
            else
            {
                String tmp = appSettingsManager.getString(settings_key);
                Logger.d(TAG, "Found AppSetting: "+settings_key+" set to: " + tmp);
                parameter.SetValue(tmp, false);
            }
        }
    }

    protected void setManualMode(ManualParameterInterface parameter, String settings_key)
    {
        if (parameter != null && parameter.IsSupported() && settings_key != null && !settings_key.equals(""))
        {
            Logger.d(TAG, parameter.getClass().getSimpleName() + " load settings: " + settings_key);
            if (appSettingsManager.getString(settings_key).equals("") || appSettingsManager.getString(settings_key).equals(null))
            {
                String tmp = parameter.GetValue()+"";
                Logger.d(TAG, settings_key + " is empty, set default from camera : " +tmp);
                appSettingsManager.setString(settings_key, tmp);
            }
            else
            {
                try {
                    int tmp = Integer.parseInt(appSettingsManager.getString(settings_key));
                    Logger.d(TAG, "Found AppSetting: "+settings_key+" set to: " + tmp);
                    parameter.SetValue(tmp);
                }
                catch (NumberFormatException ex)
                {
                    Logger.exception(ex);
                }

            }
        }
    }

    public void AddParametersLoadedListner(I_ParametersLoaded parametersLoaded)
    {
        parametersLoadedListner.add(parametersLoaded);
    }

    public void ParametersHasLoaded()
    {
        if (parametersLoadedListner == null)
            return;
        for(int i = 0; i< parametersLoadedListner.size(); i++)
        {

            if (parametersLoadedListner.get(i) == null) {
                parametersLoadedListner.remove(i);
                i--;
            }
            else {
                final int t = i;
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (parametersLoadedListner.size()> 0 && t < parametersLoadedListner.size())
                            parametersLoadedListner.get(t).ParametersLoaded(cameraUiWrapper);
                    }
                });

            }
        }
    }

    public void CLEAR()
    {

        parametersLoadedListner.clear();
    }
}
