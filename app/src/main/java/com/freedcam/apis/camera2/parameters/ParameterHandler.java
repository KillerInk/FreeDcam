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

package com.freedcam.apis.camera2.parameters;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Key;
import android.os.Build.VERSION_CODES;

import com.freedcam.apis.basecamera.parameters.AbstractParameterHandler;
import com.freedcam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.basecamera.parameters.modes.ModuleParameters;
import com.freedcam.apis.camera1.parameters.modes.StackModeParameter;
import com.freedcam.apis.camera2.CameraHolder;
import com.freedcam.apis.camera2.CameraUiWrapper;
import com.freedcam.apis.camera2.FocusHandler;
import com.freedcam.apis.camera2.parameters.manual.BurstApi2;
import com.freedcam.apis.camera2.parameters.manual.ManualFocus;
import com.freedcam.apis.camera2.parameters.manual.ManualToneMapCurveApi2;
import com.freedcam.apis.camera2.parameters.modes.AntibandingApi2;
import com.freedcam.apis.camera2.parameters.modes.ColorModeApi2;
import com.freedcam.apis.camera2.parameters.modes.ControlModesApi2;
import com.freedcam.apis.camera2.parameters.modes.DenoiseModeApi2;
import com.freedcam.apis.camera2.parameters.modes.EdgeModeApi2;
import com.freedcam.apis.camera2.parameters.modes.FlashModeApi2;
import com.freedcam.apis.camera2.parameters.modes.FocusModeApi2;
import com.freedcam.apis.camera2.parameters.modes.FocusPeakModeApi2;
import com.freedcam.apis.camera2.parameters.modes.HotPixelModeApi2;
import com.freedcam.apis.camera2.parameters.modes.ImageStabApi2;
import com.freedcam.apis.camera2.parameters.modes.OisModeApi2;
import com.freedcam.apis.camera2.parameters.modes.PictureFormatParameterApi2;
import com.freedcam.apis.camera2.parameters.modes.PictureSizeModeApi2;
import com.freedcam.apis.camera2.parameters.modes.SceneModeApi2;
import com.freedcam.apis.camera2.parameters.modes.ToneMapModeApi2;
import com.freedcam.apis.camera2.parameters.modes.VideoProfilesApi2;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.util.List;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ParameterHandler extends AbstractParameterHandler
{
    private static String TAG = ParameterHandler.class.getSimpleName();
    private ManualToneMapCurveApi2 manualToneMapCurveApi2;
    private CameraUiWrapper wrapper;

    private CameraHolder cameraHolder;

    public ParameterHandler(CameraUiWrapper cameraHolder, Context context, AppSettingsManager appSettingsManager)
    {
        super(cameraHolder.cameraHolder,context,appSettingsManager);
        wrapper = cameraHolder;
        this.cameraHolder = cameraHolder.cameraHolder;

    }


    public void Init()
    {
        List<Key<?>> keys = cameraHolder.characteristics.getAvailableCaptureRequestKeys();
        for (int i = 0; i< keys.size(); i++)
        {
            Logger.d(TAG, keys.get(i).getName());
        }
        Module = new ModuleParameters(wrapper,appSettingsManager);
        FlashMode = new FlashModeApi2(cameraHolder);
        SceneMode = new SceneModeApi2(cameraHolder);
        ColorMode = new ColorModeApi2(cameraHolder);

        WbHandler wbHandler = new WbHandler(cameraHolder,this);
        //AE mode start
        AeHandler aeHandler = new AeHandler(cameraHolder,this);
        //ae mode end
        AntiBandingMode = new AntibandingApi2(cameraHolder);
        PictureSize = new PictureSizeModeApi2(cameraHolder);

        FocusMode = new FocusModeApi2(cameraHolder);

        //shuttertime END
        //MF
        ManualFocus mf = new ManualFocus(this,cameraHolder);
        ManualFocus = mf;
        //MF END

        EdgeMode = new EdgeModeApi2(cameraHolder);
        DigitalImageStabilization = new ImageStabApi2(cameraHolder);
        HotPixelMode = new HotPixelModeApi2(cameraHolder);
        Denoise = new DenoiseModeApi2(cameraHolder);
        manualToneMapCurveApi2 = new ManualToneMapCurveApi2(this,cameraHolder);
        ManualContrast = manualToneMapCurveApi2.contrast;
        ManualBrightness = manualToneMapCurveApi2.brightness;

        ToneMapMode = new ToneMapModeApi2(cameraHolder);
        ToneMapMode.addEventListner(manualToneMapCurveApi2);

        PictureFormat = new PictureFormatParameterApi2(cameraHolder);

        FocusMode.addEventListner(((FocusHandler)cameraHolder.Focus).focusModeListner);
        WhiteBalanceMode.addEventListner(((FocusHandler) cameraHolder.Focus).awbModeListner);
        ExposureMode.addEventListner(((FocusHandler) cameraHolder.Focus).aeModeListner);
        ((FocusHandler) cameraHolder.Focus).ParametersLoaded();

        ControlMode = new ControlModesApi2(cameraHolder);

        Burst = new BurstApi2(this,cameraHolder);
        Focuspeak = new FocusPeakModeApi2(cameraHolder);
        //VideoSize = new VideoSizeModeApi2(uiHandler,cameraHolder);
        VideoProfiles = new VideoProfilesApi2(cameraHolder,wrapper);
        oismode = new OisModeApi2(cameraHolder);
        matrixChooser = new MatrixChooserParameter();
        imageStackMode = new StackModeParameter();

        uiHandler.post(new Runnable() {
            @Override
            public void run()
            {
                try {
                    ParametersHasLoaded();
                }
                catch (NullPointerException ex)
                {
                    Logger.exception(ex);
                }
            }
        });
        SetAppSettingsToParameters();

    }


    @Override
    public void LockExposureAndWhiteBalance(boolean lock) {

    }

    @Override
    public void SetPictureOrientation(int orientation)
    {
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.ON))
        {
            int or = orientation +180;
            if (or >360)
                or = or - 360;
            orientation = or;
        }
        if (cameraHolder == null || cameraHolder.isWorking || !cameraHolder.isPreviewRunning)
            return;
        try
        {
            Logger.d(TAG, "Set Orientation to:" + orientation);
            cameraHolder.SetParameterRepeating(CaptureRequest.JPEG_ORIENTATION, orientation);
        }
        catch (NullPointerException e)
        {
            Logger.exception(e);
        }
    }

    @Override
    public void SetAppSettingsToParameters()
    {
        setMode(ColorMode, AppSettingsManager.SETTING_COLORMODE);
        setMode(ExposureMode, AppSettingsManager.SETTING_EXPOSUREMODE);
        setMode(FlashMode, AppSettingsManager.SETTING_FLASHMODE);
        setMode(IsoMode, AppSettingsManager.SETTING_ISOMODE);
        setMode(AntiBandingMode, AppSettingsManager.SETTING_ANTIBANDINGMODE);
        setMode(WhiteBalanceMode, AppSettingsManager.SETTING_WHITEBALANCEMODE);
        setMode(PictureSize, AppSettingsManager.SETTING_PICTURESIZE);
        setMode(PictureFormat, AppSettingsManager.SETTING_PICTUREFORMAT);
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
        //setMode(SkinToneEnhancment, AppSettingsManager.SETTING_SKINTONE_MODE);
        setMode(NightMode, AppSettingsManager.SETTING_NIGHTEMODE);
        setMode(NonZslManualMode, AppSettingsManager.SETTING_NONZSLMANUALMODE);
        setMode(AE_Bracket, AppSettingsManager.SETTING_AEBRACKET);
        setMode(Histogram, AppSettingsManager.SETTING_HISTOGRAM);
        setMode(VideoProfiles, AppSettingsManager.SETTING_VIDEPROFILE);
        setMode(VideoHDR, AppSettingsManager.SETTING_VIDEOHDR);
        setMode(VideoSize, AppSettingsManager.SETTING_VIDEOSIZE);
        setMode(WhiteBalanceMode,AppSettingsManager.SETTING_WHITEBALANCEMODE);
        setMode(ImagePostProcessing,AppSettingsManager.SETTING_IMAGEPOSTPROCESSINGMODE);
        setMode(ColorCorrectionMode, AppSettingsManager.SETTING_COLORCORRECTION);
        setMode(EdgeMode, AppSettingsManager.SETTING_EDGE);
        setMode(HotPixelMode, AppSettingsManager.SETTING_HOTPIXEL);
        setMode(ToneMapMode, AppSettingsManager.SETTING_TONEMAP);
        setMode(ControlMode, AppSettingsManager.SETTING_CONTROLMODE);
        setMode(imageStackMode, AppSettingsManager.SETTING_STACKMODE);
        //setMode(Focuspeak, AppSettingsManager.SETTING_FOCUSPEAK);

        //setManualMode(ManualBrightness, AppSettingsManager.MWB);
        //setManualMode(ManualContrast, AppSettingsManager.MCONTRAST);
        setManualMode(ManualConvergence, AppSettingsManager.MCONVERGENCE);
        setManualMode(ManualExposure, AppSettingsManager.MEXPOSURE);
        //setManualMode(ManualFocus, AppSettingsManager.MF);
        setManualMode(ManualSharpness,AppSettingsManager.MSHARPNESS);
        setManualMode(ManualShutter, AppSettingsManager.MSHUTTERSPEED);
        setManualMode(ManualBrightness, AppSettingsManager.MBRIGHTNESS);
        //setManualMode(ManualIso, AppSettingsManager.MISO);
        setManualMode(ManualSaturation, AppSettingsManager.MSATURATION);
        setManualMode(CCT,AppSettingsManager.MCCT);

    }


}
