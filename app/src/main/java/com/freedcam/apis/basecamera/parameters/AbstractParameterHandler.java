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

package com.freedcam.apis.basecamera.parameters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.freedcam.apis.basecamera.FocusRect;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.interfaces.I_ManualParameter;
import com.freedcam.apis.basecamera.interfaces.I_ModeParameter;
import com.freedcam.apis.basecamera.parameters.modes.GuideList;
import com.freedcam.apis.basecamera.parameters.modes.Horizont;
import com.freedcam.apis.basecamera.parameters.modes.IntervalDurationParameter;
import com.freedcam.apis.basecamera.parameters.modes.IntervalShutterSleepParameter;
import com.freedcam.apis.basecamera.parameters.modes.LocationParameter;
import com.freedcam.apis.basecamera.parameters.modes.SDModeParameter;
import com.freedcam.apis.camera1.parameters.device.I_Device;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

import java.util.ArrayList;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractParameterHandler
{
    final String TAG = AbstractParameterHandler.class.getSimpleName();
    /**
     * Holds the UI/Main Thread
     */
    protected Handler uiHandler;
    private ArrayList<I_ParametersLoaded> parametersLoadedListner;
    protected Context context;

    public AppSettingsManager appSettingsManager;

    protected I_CameraUiWrapper cameraUiWrapper;

    public I_ManualParameter ManualBrightness;
    public I_ManualParameter ManualEdge;
    public I_ManualParameter ManualHue;
    public I_ManualParameter ManualSharpness;
    public I_ManualParameter ManualContrast;
    public I_ManualParameter ManualSaturation;
    public I_ManualParameter ManualExposure;
    public I_ManualParameter ManualConvergence;
    public I_ManualParameter ManualFocus;
    public I_ManualParameter ManualShutter;
    public I_ManualParameter ManualFNumber;
    public I_ManualParameter Burst;
    public I_ManualParameter CCT;
    public I_ManualParameter FX;
    public I_ManualParameter ManualIso;
    public I_ManualParameter Zoom;
    public I_ManualParameter Skintone;
    public I_ManualParameter ProgramShift;
    public I_ManualParameter PreviewZoom;


    public I_ModeParameter ColorMode;
    public I_ModeParameter ExposureMode;
    public I_ModeParameter AE_PriorityMode;
    public I_ModeParameter FlashMode;
    public I_ModeParameter IsoMode;
    public I_ModeParameter AntiBandingMode;
    public I_ModeParameter WhiteBalanceMode;
    public I_ModeParameter PictureSize;
    public I_ModeParameter PictureFormat;
    public I_ModeParameter HDRMode;
    public I_ModeParameter JpegQuality;
    //defcomg was here
    public I_ModeParameter GuideList;
    //done
    public I_ModeParameter ImagePostProcessing;
    public I_ModeParameter PreviewSize;
    public I_ModeParameter PreviewFPS;
    public I_ModeParameter PreviewFormat;
    public I_ModeParameter SceneMode;
    public I_ModeParameter FocusMode;
    public I_ModeParameter RedEye;
    public I_ModeParameter LensShade;
    public I_ModeParameter ZSL;
    public I_ModeParameter SceneDetect;
    public I_ModeParameter Denoise;
    public I_ModeParameter DigitalImageStabilization;
    public I_ModeParameter VideoStabilization;
    public I_ModeParameter MemoryColorEnhancement;
    public I_ModeParameter SkinToneEnhancment;
    public I_ModeParameter NightMode;
    public I_ModeParameter NonZslManualMode;
    public I_ModeParameter AE_Bracket;
    public I_ModeParameter Histogram;
    public I_ModeParameter ExposureLock;
    public I_ModeParameter CDS_Mode;

    public I_ModeParameter VideoProfiles;
    public I_ModeParameter VideoSize;
    public I_ModeParameter VideoHDR;
    public I_ModeParameter VideoHighFramerateVideo;
    public I_ModeParameter LensFilter;
    public I_ModeParameter CameraMode;
    public I_ModeParameter Horizont;

    //yet only seen on m9
    public I_ModeParameter RdiMode;
    public I_ModeParameter TnrMode;
    public I_ModeParameter SecureMode;

    //SonyApi
    public I_ModeParameter ContShootMode;
    public I_ModeParameter ContShootModeSpeed;
    public I_ModeParameter ObjectTracking;
    public I_ModeParameter PostViewSize;
    public I_ModeParameter Focuspeak;
    public I_ModeParameter Module;
    public I_ModeParameter ZoomSetting;
    //public AbstractModeParameter PreviewZoom;
    public boolean isExposureAndWBLocked = false;
    private boolean isDngActive = false;
    public boolean IsDngActive(){ return isDngActive; }
    public void SetDngActive(boolean active) {
        isDngActive = active;}



    //camera2 modes
    public I_ModeParameter EdgeMode;
    public I_ModeParameter ColorCorrectionMode;
    public I_ModeParameter HotPixelMode;
    public I_ModeParameter ToneMapMode;
    public I_ModeParameter ControlMode;

    public I_ModeParameter oismode;

    public I_ModeParameter SdSaveLocation;

    public I_ModeParameter locationParameter;

    public boolean IntervalCapture = false;
    public boolean IntervalCaptureFocusSet = false;

    public I_ModeParameter IntervalDuration;
    public I_ModeParameter IntervalShutterSleep;

    public I_ModeParameter captureBurstExposures;

    public I_ModeParameter morphoHDR;
    public I_ModeParameter morphoHHT;

    public I_ModeParameter aeb1;
    public I_ModeParameter aeb2;
    public I_ModeParameter aeb3;

    public I_ModeParameter opcode;
    public I_ModeParameter bayerformat;
    public I_ModeParameter matrixChooser;
    public I_ModeParameter imageStackMode;

    public AbstractParameterHandler(Context context, I_CameraUiWrapper cameraUiWrapper) {
        super();
        this.cameraUiWrapper = cameraUiWrapper;
        uiHandler = new Handler(Looper.getMainLooper());
        this.context = context;
        this.appSettingsManager = cameraUiWrapper.GetAppSettingsManager();
        parametersLoadedListner = new ArrayList<>();
        parametersLoadedListner.clear();

        GuideList = new GuideList();
        locationParameter = new LocationParameter(cameraUiWrapper.GetCameraHolder(), context, appSettingsManager);
        IntervalDuration = new IntervalDurationParameter();
        IntervalShutterSleep = new IntervalShutterSleepParameter(context);
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

    protected void setMode(I_ModeParameter parameter, String settings_key)
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

    protected void setManualMode(I_ManualParameter parameter, String settings_key)
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
        for(int i= 0; i< parametersLoadedListner.size(); i++)
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
                            parametersLoadedListner.get(t).ParametersLoaded();
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
