package com.troop.freedcam.camera2.parameters;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.manual.ManualExposureApi2;
import com.troop.freedcam.camera2.parameters.manual.ManualExposureTimeApi2;
import com.troop.freedcam.camera2.parameters.manual.ManualFocus;
import com.troop.freedcam.camera2.parameters.manual.ManualISoApi2;
import com.troop.freedcam.camera2.parameters.manual.ZoomApi2;
import com.troop.freedcam.camera2.parameters.modes.AeModeApi2;
import com.troop.freedcam.camera2.parameters.modes.ColorModeApi2;
import com.troop.freedcam.camera2.parameters.modes.ControlModesApi2;
import com.troop.freedcam.camera2.parameters.modes.FlashModeApi2;
import com.troop.freedcam.camera2.parameters.modes.PictureFormatParameterApi2;
import com.troop.freedcam.camera2.parameters.modes.PictureSizeModeApi2;
import com.troop.freedcam.camera2.parameters.modes.SceneModeApi2;
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
        boolean muh = this.cameraHolder.characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE );
        FlashMode = new FlashModeApi2(uiHandler,this.cameraHolder);
        SceneMode = new SceneModeApi2(uiHandler,this.cameraHolder);
        ColorMode = new ColorModeApi2(uiHandler,this.cameraHolder);
        final AeModeApi2 AE = new AeModeApi2(uiHandler,this.cameraHolder);
        ExposureMode = AE;
        PictureSize = new PictureSizeModeApi2(uiHandler,this.cameraHolder);
        PictureFormat = new PictureFormatParameterApi2(uiHandler,this.cameraHolder);
        ManualExposure = new ManualExposureApi2(this, cameraHolder);
        final ManualISoApi2 miso = new ManualISoApi2(this,cameraHolder);
        AE.addEventListner(miso);
        ISOManual = miso;
        Zoom = new ZoomApi2(this, cameraHolder);
        final ManualExposureTimeApi2 ManualExposureTIme = new ManualExposureTimeApi2(this, cameraHolder);
        ManualShutter = ManualExposureTIme;
        AE.addEventListner(ManualExposureTIme);
        ManualFocus = new ManualFocus(this,cameraHolder);
        WhiteBalanceMode = new WhiteBalanceApi2(uiHandler,cameraHolder);
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
