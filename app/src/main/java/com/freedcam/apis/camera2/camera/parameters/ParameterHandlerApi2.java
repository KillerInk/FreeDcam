package com.freedcam.apis.camera2.camera.parameters;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.parameters.modes.StackModeParameter;
import com.freedcam.apis.camera2.camera.CameraHolderApi2;
import com.freedcam.apis.camera2.camera.CameraUiWrapperApi2;
import com.freedcam.apis.camera2.camera.FocusHandlerApi2;
import com.freedcam.apis.camera2.camera.parameters.manual.BurstApi2;
import com.freedcam.apis.camera2.camera.parameters.manual.ManualFocus;
import com.freedcam.apis.camera2.camera.parameters.manual.ManualToneMapCurveApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.AntibandingApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.ColorModeApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.ControlModesApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.DenoiseModeApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.EdgeModeApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.FlashModeApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.FocusModeApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.FocusPeakModeApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.HotPixelModeApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.ImageStabApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.OisModeApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.PictureFormatParameterApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.PictureSizeModeApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.SceneModeApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.ToneMapModeApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.VideoProfilesApi2;
import com.freedcam.apis.basecamera.camera.parameters.modes.MatrixChooserParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.ModuleParameters;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.StringUtils;


import java.util.List;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ParameterHandlerApi2 extends AbstractParameterHandler
{
    private static String TAG = ParameterHandlerApi2.class.getSimpleName();
    private ManualToneMapCurveApi2 manualToneMapCurveApi2;
    private CameraUiWrapperApi2 wrapper;

    private CameraHolderApi2 cameraHolder;

    public ParameterHandlerApi2(CameraUiWrapperApi2 cameraHolder, Handler uiHandler, Context context,AppSettingsManager appSettingsManager)
    {
        super(cameraHolder.cameraHolder, uiHandler,context,appSettingsManager);
        this.wrapper = cameraHolder;
        this.cameraHolder = cameraHolder.cameraHolder;

    }


    public void Init()
    {
        List<CaptureRequest.Key<?>> keys = cameraHolder.characteristics.getAvailableCaptureRequestKeys();
        for (int i = 0; i< keys.size(); i++)
        {
            Logger.d(TAG, keys.get(i).getName());
        }
        Module = new ModuleParameters(uiHandler, wrapper,appSettingsManager);
        FlashMode = new FlashModeApi2(uiHandler,this.cameraHolder);
        SceneMode = new SceneModeApi2(uiHandler,this.cameraHolder);
        ColorMode = new ColorModeApi2(uiHandler,this.cameraHolder);

        WbHandler wbHandler = new WbHandler(uiHandler,cameraHolder,this);
        //AE mode start
        AeHandlerApi2 aeHandlerApi2 = new AeHandlerApi2(uiHandler,cameraHolder,this);
        //ae mode end
        AntiBandingMode = new AntibandingApi2(uiHandler,cameraHolder);
        PictureSize = new PictureSizeModeApi2(uiHandler,this.cameraHolder);

        FocusMode = new FocusModeApi2(uiHandler, cameraHolder);

        //shuttertime END
        //MF
        final ManualFocus mf = new ManualFocus(this,cameraHolder);
        ManualFocus = mf;
        //MF END

        EdgeMode = new EdgeModeApi2(uiHandler,cameraHolder);
        DigitalImageStabilization = new ImageStabApi2(uiHandler,cameraHolder);
        HotPixelMode = new HotPixelModeApi2(uiHandler,cameraHolder);
        Denoise = new DenoiseModeApi2(uiHandler,cameraHolder);
        manualToneMapCurveApi2 = new ManualToneMapCurveApi2(this,cameraHolder);
        ManualContrast = manualToneMapCurveApi2.contrast;
        ManualBrightness = manualToneMapCurveApi2.brightness;

        ToneMapMode = new ToneMapModeApi2(uiHandler,cameraHolder);
        ToneMapMode.addEventListner(manualToneMapCurveApi2);

        PictureFormat = new PictureFormatParameterApi2(uiHandler,this.cameraHolder);

        FocusMode.addEventListner(((FocusHandlerApi2)cameraHolder.Focus).focusModeListner);
        WhiteBalanceMode.addEventListner(((FocusHandlerApi2) cameraHolder.Focus).awbModeListner);
        ExposureMode.addEventListner(((FocusHandlerApi2) cameraHolder.Focus).aeModeListner);
        ((FocusHandlerApi2) cameraHolder.Focus).ParametersLoaded();

        ControlMode = new ControlModesApi2(uiHandler, this.cameraHolder);

        Burst = new BurstApi2(this,cameraHolder);
        Focuspeak = new FocusPeakModeApi2(uiHandler,cameraHolder);
        //VideoSize = new VideoSizeModeApi2(uiHandler,cameraHolder);
        VideoProfiles = new VideoProfilesApi2(uiHandler,cameraHolder,(CameraUiWrapperApi2)wrapper);
        oismode = new OisModeApi2(uiHandler,cameraHolder);
        matrixChooser = new MatrixChooserParameter(uiHandler);
        imageStackMode = new StackModeParameter(null,null,null,null,null);

        uiHandler.post(new Runnable() {
            @Override
            public void run()
            {
                try {
                    ParametersHasLoaded();
                }
                catch (NullPointerException ex)
                {

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
        if (cameraHolder == null || cameraHolder.mPreviewRequestBuilder == null || cameraHolder.isWorking || !cameraHolder.isPreviewRunning)
            return;
        try
        {
            Logger.d(TAG, "Set Orientation to:" + orientation);
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, orientation);
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
        setMode(VideoProfilesG3, AppSettingsManager.SETTING_VIDEPROFILE);
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
        //setManualMode(ISOManual, AppSettingsManager.MISO);
        setManualMode(ManualSaturation, AppSettingsManager.MSATURATION);
        setManualMode(CCT,AppSettingsManager.MCCT);

    }


}
