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

package freed.cam.apis.camera2.parameters;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Key;
import android.os.Build.VERSION_CODES;

import java.util.List;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModuleParameters;
import freed.cam.apis.camera1.parameters.device.I_Device;
import freed.cam.apis.camera1.parameters.modes.StackModeParameter;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.FocusHandler;
import freed.cam.apis.camera2.parameters.manual.BurstApi2;
import freed.cam.apis.camera2.parameters.manual.ManualFocus;
import freed.cam.apis.camera2.parameters.manual.ManualToneMapCurveApi2;
import freed.cam.apis.camera2.parameters.manual.ZoomApi2;
import freed.cam.apis.camera2.parameters.modes.AntibandingApi2;
import freed.cam.apis.camera2.parameters.modes.ColorModeApi2;
import freed.cam.apis.camera2.parameters.modes.ControlModesApi2;
import freed.cam.apis.camera2.parameters.modes.DenoiseModeApi2;
import freed.cam.apis.camera2.parameters.modes.EdgeModeApi2;
import freed.cam.apis.camera2.parameters.modes.FlashModeApi2;
import freed.cam.apis.camera2.parameters.modes.FocusModeApi2;
import freed.cam.apis.camera2.parameters.modes.FocusPeakModeApi2;
import freed.cam.apis.camera2.parameters.modes.HotPixelModeApi2;
import freed.cam.apis.camera2.parameters.modes.ImageStabApi2;
import freed.cam.apis.camera2.parameters.modes.OisModeApi2;
import freed.cam.apis.camera2.parameters.modes.PictureFormatParameterApi2;
import freed.cam.apis.camera2.parameters.modes.PictureSizeModeApi2;
import freed.cam.apis.camera2.parameters.modes.SceneModeApi2;
import freed.cam.apis.camera2.parameters.modes.ToneMapModeApi2;
import freed.cam.apis.camera2.parameters.modes.VideoProfilesApi2;
import freed.utils.AppSettingsManager;
import freed.utils.Logger;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ParameterHandlerApi2 extends AbstractParameterHandler
{
    private final String TAG = ParameterHandlerApi2.class.getSimpleName();
    private ManualToneMapCurveApi2 manualToneMapCurveApi2;

    private CameraHolderApi2 cameraHolder;

    public ParameterHandlerApi2(CameraWrapperInterface wrapper)
    {
        super(wrapper);
    }


    public void Init()
    {
        this.cameraHolder = (CameraHolderApi2) cameraUiWrapper.GetCameraHolder();
        List<Key<?>> keys = cameraHolder.characteristics.getAvailableCaptureRequestKeys();
        for (int i = 0; i< keys.size(); i++)
        {
            Logger.d(TAG, keys.get(i).getName());
        }
        Module = new ModuleParameters(cameraUiWrapper, appSettingsManager);
        FlashMode = new FlashModeApi2(cameraUiWrapper);
        SceneMode = new SceneModeApi2(cameraUiWrapper);
        ColorMode = new ColorModeApi2(cameraUiWrapper);

        WbHandler wbHandler = new WbHandler(cameraUiWrapper);
        //AE mode start
        AeHandler aeHandler = new AeHandler(cameraUiWrapper);
        //ae mode end
        AntiBandingMode = new AntibandingApi2(cameraUiWrapper);
        PictureSize = new PictureSizeModeApi2(cameraUiWrapper);

        FocusMode = new FocusModeApi2(cameraUiWrapper);

        //shuttertime END
        //MF
        ManualFocus mf = new ManualFocus(cameraUiWrapper);
        ManualFocus = mf;
        //MF END

        EdgeMode = new EdgeModeApi2(cameraUiWrapper);
        DigitalImageStabilization = new ImageStabApi2(cameraUiWrapper);
        HotPixelMode = new HotPixelModeApi2(cameraUiWrapper);
        Denoise = new DenoiseModeApi2(cameraUiWrapper);
        manualToneMapCurveApi2 = new ManualToneMapCurveApi2(cameraUiWrapper);
        ManualContrast = manualToneMapCurveApi2.contrast;
        ManualBrightness = manualToneMapCurveApi2.brightness;

        ToneMapMode = new ToneMapModeApi2(cameraUiWrapper);
        ToneMapMode.addEventListner(manualToneMapCurveApi2);

        PictureFormat = new PictureFormatParameterApi2(cameraUiWrapper);

        FocusMode.addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).focusModeListner);
        ExposureMode.addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).aeModeListner);
        ((FocusHandler) cameraUiWrapper.getFocusHandler()).ParametersLoaded(cameraUiWrapper);

        ControlMode = new ControlModesApi2(cameraUiWrapper);

        Burst = new BurstApi2(cameraUiWrapper);
        Focuspeak = new FocusPeakModeApi2(cameraUiWrapper);
        //VideoSize = new VideoSizeModeApi2(uiHandler,cameraHolder);
        VideoProfiles = new VideoProfilesApi2(cameraUiWrapper);
        oismode = new OisModeApi2(cameraUiWrapper);
        matrixChooser = new MatrixChooserParameter();
        imageStackMode = new StackModeParameter();
        Zoom = new ZoomApi2(cameraUiWrapper);


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
    public I_Device getDevice() {
        return null;
    }

    @Override
    public void SetFocusAREA(FocusRect focusAreas) {

    }

    @Override
    public void SetMeterAREA(FocusRect meteringAreas) {

    }

    @Override
    public void SetPictureOrientation(int orientation)
    {
        if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(KEYS.ON))
        {
            int or = orientation +180;
            if (or >360)
                or = or - 360;
            orientation = or;
        }
        if (cameraHolder == null || cameraHolder.isWorking)
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
        setMode(Denoise, AppSettingsManager.SETTING_DENOISE_MODE);
        setMode(DigitalImageStabilization, AppSettingsManager.SETTING_DIS_MODE);
        //setMode(SkinToneEnhancment, AppSettingsManager.SETTING_SKINTONE_MODE);
        setMode(NightMode, AppSettingsManager.SETTING_NIGHTEMODE);
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
