package com.troop.freedcam.camera2.parameters;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.FocusHandlerApi2;
import com.troop.freedcam.camera2.parameters.manual.ManualExposureApi2;
import com.troop.freedcam.camera2.parameters.manual.ManualExposureTimeApi2;
import com.troop.freedcam.camera2.parameters.manual.ManualFocus;
import com.troop.freedcam.camera2.parameters.manual.ManualISoApi2;
import com.troop.freedcam.camera2.parameters.manual.ManualToneMapCurveApi2;
import com.troop.freedcam.camera2.parameters.manual.ManualWbCtApi2;
import com.troop.freedcam.camera2.parameters.manual.ZoomApi2;
import com.troop.freedcam.camera2.parameters.modes.AeModeApi2;
import com.troop.freedcam.camera2.parameters.modes.AntibandingApi2;
import com.troop.freedcam.camera2.parameters.modes.ColorCorrectionModeApi2;
import com.troop.freedcam.camera2.parameters.modes.ColorModeApi2;
import com.troop.freedcam.camera2.parameters.modes.ControlModesApi2;
import com.troop.freedcam.camera2.parameters.modes.DenoiseModeApi2;
import com.troop.freedcam.camera2.parameters.modes.EdgeModeApi2;
import com.troop.freedcam.camera2.parameters.modes.FlashModeApi2;
import com.troop.freedcam.camera2.parameters.modes.FocusModeApi2;
import com.troop.freedcam.camera2.parameters.modes.HotPixelModeApi2;
import com.troop.freedcam.camera2.parameters.modes.ImageStabApi2;
import com.troop.freedcam.camera2.parameters.modes.PictureFormatParameterApi2;
import com.troop.freedcam.camera2.parameters.modes.PictureSizeModeApi2;
import com.troop.freedcam.camera2.parameters.modes.SceneModeApi2;
import com.troop.freedcam.camera2.parameters.modes.ToneMapModeApi2;
import com.troop.freedcam.camera2.parameters.modes.WhiteBalanceApi2;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.i_camera.parameters.CameraParametersEventHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.util.List;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ParameterHandlerApi2 extends AbstractParameterHandler
{
    private static String TAG = StringUtils.TAG + ParameterHandlerApi2.class.getSimpleName();
    private ManualToneMapCurveApi2 manualToneMapCurveApi2;

    BaseCameraHolderApi2 cameraHolder;

    public ParameterHandlerApi2(AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager, Handler uiHandler)
    {
        super(cameraHolder, appSettingsManager, uiHandler);
        this.cameraHolder = (BaseCameraHolderApi2) cameraHolder;
        ParametersEventHandler = new CameraParametersEventHandler();
    }


    public void Init()
    {
        List<CaptureRequest.Key<?>> keys = cameraHolder.characteristics.getAvailableCaptureRequestKeys();
        for (int i = 0; i< keys.size(); i++)
        {
            Log.d(TAG, keys.get(i).getName());
        }
        FlashMode = new FlashModeApi2(uiHandler,this.cameraHolder);
        SceneMode = new SceneModeApi2(uiHandler,this.cameraHolder);
        ColorMode = new ColorModeApi2(uiHandler,this.cameraHolder);
        WhiteBalanceMode = new WhiteBalanceApi2(uiHandler,cameraHolder);
        //AE mode start
        final AeModeApi2 AE = new AeModeApi2(uiHandler,this.cameraHolder);
        ExposureMode = AE;
        //ae mode end
        AntiBandingMode = new AntibandingApi2(uiHandler,cameraHolder);
        PictureSize = new PictureSizeModeApi2(uiHandler,this.cameraHolder);

        FocusMode = new FocusModeApi2(uiHandler, cameraHolder);
        ManualExposure = new ManualExposureApi2(this, cameraHolder);
        //manual iso
        final ManualISoApi2 miso = new ManualISoApi2(this,cameraHolder);
        AE.addEventListner(miso);
        ISOManual = miso;
        //manual iso END
        Zoom = new ZoomApi2(this, cameraHolder);
        //shuttertime
        final ManualExposureTimeApi2 ManualExposureTIme = new ManualExposureTimeApi2(this, cameraHolder);
        ManualShutter = ManualExposureTIme;
        AE.addEventListner(ManualExposureTIme);
        //shuttertime END
        //MF
        final ManualFocus mf = new ManualFocus(this,cameraHolder);
        ManualFocus = mf;
        FocusMode.addEventListner(mf);
        //MF END
        ColorCorrectionMode = new ColorCorrectionModeApi2(uiHandler,cameraHolder);
        //CCT START
        final  ManualWbCtApi2 cct = new ManualWbCtApi2(this,cameraHolder);
        CCT = cct;
        ColorCorrectionMode.addEventListner(cct);
        WhiteBalanceMode.addEventListner(cct);
        //cct end

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
        WhiteBalanceMode.addEventListner(((FocusHandlerApi2)cameraHolder.Focus).awbModeListner);
        ExposureMode.addEventListner(((FocusHandlerApi2)cameraHolder.Focus).aeModeListner);
        ((FocusHandlerApi2) cameraHolder.Focus).ParametersLoaded();

        ControlMode = new ControlModesApi2(uiHandler, this.cameraHolder);

        SetAppSettingsToParameters();
        uiHandler.post(new Runnable() {
            @Override
            public void run()
            {
                try {
                    ParametersEventHandler.ParametersHasLoaded();
                }
                catch (NullPointerException ex)
                {

                }

            }
        });

    }


}
