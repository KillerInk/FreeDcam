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
import android.graphics.Rect;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Key;
import android.os.Build.VERSION_CODES;

import com.huawei.camera2ex.CaptureRequestEx;

import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModuleParameters;
import freed.cam.apis.basecamera.parameters.modes.ToneMapChooser;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.FocusHandler;
import freed.cam.apis.camera2.parameters.huawei.HuaweiAeHandler;
import freed.cam.apis.camera2.parameters.manual.BurstApi2;
import freed.cam.apis.camera2.parameters.manual.ManualFocus;
import freed.cam.apis.camera2.parameters.manual.ManualToneMapCurveApi2;
import freed.cam.apis.camera2.parameters.manual.ZoomApi2;
import freed.cam.apis.camera2.parameters.modes.AeLockModeApi2;
import freed.cam.apis.camera2.parameters.modes.AeTargetRangeApi2;
import freed.cam.apis.camera2.parameters.modes.BaseModeApi2;
import freed.cam.apis.camera2.parameters.modes.DualCameraModeHuaweiApi2;
import freed.cam.apis.camera2.parameters.modes.FocusPeakModeApi2;
import freed.cam.apis.camera2.parameters.modes.JpegQualityModeApi2;
import freed.cam.apis.camera2.parameters.modes.PictureFormatParameterApi2;
import freed.cam.apis.camera2.parameters.modes.PictureSizeModeApi2;
import freed.cam.apis.camera2.parameters.modes.VideoProfilesApi2;
import freed.settings.Settings;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ParameterHandlerApi2 extends AbstractParameterHandler
{
    private final String TAG = ParameterHandlerApi2.class.getSimpleName();


    private CameraHolderApi2 cameraHolder;

    public ParameterHandlerApi2(CameraWrapperInterface wrapper)
    {
        super(wrapper);
    }


    public void Init()
    {
        this.cameraHolder = (CameraHolderApi2) cameraUiWrapper.getCameraHolder();
        List<Key<?>> keys = cameraHolder.characteristics.getAvailableCaptureRequestKeys();
        for (int i = 0; i< keys.size(); i++)
        {
            Log.d(TAG, keys.get(i).getName());
        }
        add(Settings.Module, new ModuleParameters(cameraUiWrapper));
        if (SettingsManager.get(Settings.FlashMode).isSupported())
            add(Settings.FlashMode, new BaseModeApi2(cameraUiWrapper, SettingsManager.get(Settings.FlashMode),CaptureRequest.FLASH_MODE));
        if (SettingsManager.get(Settings.SceneMode).isSupported())
            add(Settings.SceneMode, new BaseModeApi2(cameraUiWrapper, SettingsManager.get(Settings.SceneMode),CaptureRequest.CONTROL_SCENE_MODE));
        if (SettingsManager.get(Settings.AntiBandingMode).isSupported())
            add(Settings.AntiBandingMode, new BaseModeApi2(cameraUiWrapper, SettingsManager.get(Settings.AntiBandingMode), CaptureRequest.CONTROL_AE_ANTIBANDING_MODE));
        if (SettingsManager.get(Settings.ColorMode).isSupported())
            add(Settings.ColorMode, new BaseModeApi2(cameraUiWrapper, SettingsManager.get(Settings.ColorMode),CaptureRequest.CONTROL_EFFECT_MODE));
        if (SettingsManager.get(Settings.ControlMode).isSupported())
            add(Settings.ControlMode, new BaseModeApi2(cameraUiWrapper, SettingsManager.get(Settings.ControlMode),CaptureRequest.CONTROL_MODE));
        if (SettingsManager.get(Settings.Denoise).isSupported())
            add(Settings.Denoise, new BaseModeApi2(cameraUiWrapper, SettingsManager.get(Settings.Denoise),CaptureRequest.NOISE_REDUCTION_MODE));
        if (SettingsManager.get(Settings.EdgeMode).isSupported())
            add(Settings.EdgeMode, new BaseModeApi2(cameraUiWrapper, SettingsManager.get(Settings.EdgeMode),CaptureRequest.EDGE_MODE));
        if (SettingsManager.get(Settings.oismode).isSupported())
            add(Settings.oismode, new BaseModeApi2(cameraUiWrapper, SettingsManager.get(Settings.oismode),CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE));
        if (SettingsManager.get(Settings.FocusMode).isSupported()) {
            add(Settings.FocusMode, new BaseModeApi2(cameraUiWrapper, SettingsManager.get(Settings.FocusMode), CaptureRequest.CONTROL_AF_MODE));
            get(Settings.FocusMode).addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).focusModeListner);
        }
        if (SettingsManager.get(Settings.HotPixelMode).isSupported())
            add(Settings.HotPixelMode, new BaseModeApi2(cameraUiWrapper, SettingsManager.get(Settings.HotPixelMode),CaptureRequest.HOT_PIXEL_MODE));
        if (SettingsManager.get(Settings.Ae_TargetFPS).isSupported())
            add(Settings.Ae_TargetFPS, new AeTargetRangeApi2(cameraUiWrapper, SettingsManager.get(Settings.Ae_TargetFPS),CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE));

        if (SettingsManager.get(Settings.dualPrimaryCameraMode).isSupported() && !SettingsManager.getInstance().getIsFrontCamera())
        {
            add(Settings.dualPrimaryCameraMode, new DualCameraModeHuaweiApi2(cameraUiWrapper, SettingsManager.get(Settings.dualPrimaryCameraMode), CaptureRequestEx.HUAWEI_DUAL_SENSOR_MODE));
        }
        add(Settings.JpegQuality, new JpegQualityModeApi2(cameraUiWrapper));

        WbHandler wbHandler = new WbHandler(cameraUiWrapper);
        add(Settings.M_Whitebalance, wbHandler.manualWbCt);
        add(Settings.WhiteBalanceMode, wbHandler.whiteBalanceApi2);
        //dont make that avail for the ui its only internal used
        //ColorCorrectionMode = colorCorrectionMode;

        //AE mode start
        AeHandler aeHandler;
        if (SettingsManager.get(Settings.useHuaweiCamera2Extension).getBoolean())
            aeHandler = new HuaweiAeHandler(cameraUiWrapper);
        else {
            aeHandler = new AeHandler(cameraUiWrapper);
            //not used by huawei
            add(Settings.ExposureMode, aeHandler.aeModeApi2);
            get(Settings.ExposureMode).addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).aeModeListner);
        }
        //pass stuff to the parameterhandler that it get used by the ui

        add(Settings.M_ExposureTime, aeHandler.manualExposureTimeApi2);
        add(Settings.M_ExposureCompensation, aeHandler.manualExposureApi2);
        add(Settings.M_ManualIso, aeHandler.manualISoApi2);
        //ae mode end
        add(Settings.PictureSize, new PictureSizeModeApi2(cameraUiWrapper));


        //MF
        add(Settings.M_Focus,new ManualFocus(cameraUiWrapper)); ;

        //MF END

        ManualToneMapCurveApi2 manualToneMapCurveApi2 = new ManualToneMapCurveApi2(cameraUiWrapper);
        /*ManualContrast = manualToneMapCurveApi2.contrast;
        ManualBrightness = manualToneMapCurveApi2.brightness;
        black = manualToneMapCurveApi2.black;
        shadows = manualToneMapCurveApi2.shadowsp;
        midtones = manualToneMapCurveApi2.midtonesp;
        highlights = manualToneMapCurveApi2.highlightsp;
        white = manualToneMapCurveApi2.whitep;*/
        add(Settings.M_ToneCurve, manualToneMapCurveApi2.toneCurveParameter);

        add(Settings.ToneMapMode,new BaseModeApi2(cameraUiWrapper, SettingsManager.get(Settings.tonemapChooser),CaptureRequest.TONEMAP_MODE));
        get(Settings.ToneMapMode).addEventListner(manualToneMapCurveApi2);

        add(Settings.PictureFormat, new PictureFormatParameterApi2(cameraUiWrapper, SettingsManager.get(Settings.PictureFormat), null));

        add(Settings.ExposureLock, new AeLockModeApi2(cameraUiWrapper));

        add(Settings.M_Burst, new BurstApi2(cameraUiWrapper));
        add(Settings.Focuspeak, new FocusPeakModeApi2(cameraUiWrapper));
        add(Settings.VideoProfiles, new VideoProfilesApi2(cameraUiWrapper));
        add(Settings.matrixChooser, new MatrixChooserParameter(SettingsManager.getInstance().getMatrixesMap()));
        add(Settings.tonemapChooser, new ToneMapChooser(SettingsManager.getInstance().getToneMapProfiles()));
        add(Settings.M_Zoom, new ZoomApi2(cameraUiWrapper));
        SetAppSettingsToParameters();
    }

    @Override
    public void SetFocusAREA(Rect focusAreas) {

    }



    @Override
    public void SetPictureOrientation(int orientation)
    {
        if (SettingsManager.get(Settings.orientationHack).getBoolean())
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
            Log.d(TAG, "Set Orientation to:" + orientation);
            cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.JPEG_ORIENTATION, orientation,true);
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    @Override
    public float[] getFocusDistances()
    {
        return cameraHolder.GetFocusRange();
    }

    @Override
    public float getCurrentExposuretime() {
        return 0;
    }

    @Override
    public int getCurrentIso() {
        return 0;
    }


}
