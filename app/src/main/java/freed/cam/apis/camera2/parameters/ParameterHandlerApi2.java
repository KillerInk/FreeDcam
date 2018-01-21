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
import freed.cam.apis.basecamera.parameters.modes.FocusPeakMode;
import freed.cam.apis.basecamera.parameters.modes.HistogramParameter;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModuleParameters;
import freed.cam.apis.basecamera.parameters.modes.ToneMapChooser;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.FocusHandler;
import freed.cam.apis.camera2.parameters.ae.AeManagerCamera2;
import freed.cam.apis.camera2.parameters.ae.AeManagerHuaweiCamera2;
import freed.cam.apis.camera2.parameters.manual.BurstApi2;
import freed.cam.apis.camera2.parameters.manual.ManualFocus;
import freed.cam.apis.camera2.parameters.manual.ManualToneMapCurveApi2;
import freed.cam.apis.camera2.parameters.manual.ZoomApi2;
import freed.cam.apis.camera2.parameters.modes.AeLockModeApi2;
import freed.cam.apis.camera2.parameters.modes.AeTargetRangeApi2;
import freed.cam.apis.camera2.parameters.modes.BaseModeApi2;
import freed.cam.apis.camera2.parameters.modes.DualCameraModeHuaweiApi2;
import freed.cam.apis.camera2.parameters.modes.JpegQualityModeApi2;
import freed.cam.apis.camera2.parameters.modes.PictureFormatParameterApi2;
import freed.cam.apis.camera2.parameters.modes.PictureSizeModeApi2;
import freed.cam.apis.camera2.parameters.modes.VideoProfilesApi2;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
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
    private Camera2Fragment camera2Fragment;

    public ParameterHandlerApi2(CameraWrapperInterface wrapper)
    {
        super(wrapper);
        this.camera2Fragment = (Camera2Fragment) wrapper;
    }


    public void Init()
    {
        this.cameraHolder = (CameraHolderApi2) cameraUiWrapper.getCameraHolder();
        List<Key<?>> keys = cameraHolder.characteristics.getAvailableCaptureRequestKeys();
        for (int i = 0; i< keys.size(); i++)
        {
            Log.d(TAG, keys.get(i).getName());
        }
        add(SettingKeys.Module, new ModuleParameters(cameraUiWrapper));
        if (SettingsManager.get(SettingKeys.FlashMode).isSupported())
            add(SettingKeys.FlashMode, new BaseModeApi2(cameraUiWrapper, SettingKeys.FlashMode,CaptureRequest.FLASH_MODE));
        if (SettingsManager.get(SettingKeys.SceneMode).isSupported())
            add(SettingKeys.SceneMode, new BaseModeApi2(cameraUiWrapper, SettingKeys.SceneMode,CaptureRequest.CONTROL_SCENE_MODE));
        if (SettingsManager.get(SettingKeys.AntiBandingMode).isSupported())
            add(SettingKeys.AntiBandingMode, new BaseModeApi2(cameraUiWrapper, SettingKeys.AntiBandingMode, CaptureRequest.CONTROL_AE_ANTIBANDING_MODE));
        if (SettingsManager.get(SettingKeys.ColorMode).isSupported())
            add(SettingKeys.ColorMode, new BaseModeApi2(cameraUiWrapper, SettingKeys.ColorMode,CaptureRequest.CONTROL_EFFECT_MODE));
        if (SettingsManager.get(SettingKeys.CONTROL_MODE).isSupported())
            add(SettingKeys.CONTROL_MODE, new BaseModeApi2(cameraUiWrapper, SettingKeys.CONTROL_MODE,CaptureRequest.CONTROL_MODE));
        if (SettingsManager.get(SettingKeys.Denoise).isSupported())
            add(SettingKeys.Denoise, new BaseModeApi2(cameraUiWrapper, SettingKeys.Denoise,CaptureRequest.NOISE_REDUCTION_MODE));
        if (SettingsManager.get(SettingKeys.EDGE_MODE).isSupported())
            add(SettingKeys.EDGE_MODE, new BaseModeApi2(cameraUiWrapper, SettingKeys.EDGE_MODE,CaptureRequest.EDGE_MODE));
        if (SettingsManager.get(SettingKeys.OIS_MODE).isSupported())
            add(SettingKeys.OIS_MODE, new BaseModeApi2(cameraUiWrapper, SettingKeys.OIS_MODE,CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE));
        if (SettingsManager.get(SettingKeys.FocusMode).isSupported()) {
            add(SettingKeys.FocusMode, new BaseModeApi2(cameraUiWrapper, SettingKeys.FocusMode, CaptureRequest.CONTROL_AF_MODE));
            get(SettingKeys.FocusMode).addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).focusModeListner);
        }
        if (SettingsManager.get(SettingKeys.HOT_PIXEL_MODE).isSupported())
            add(SettingKeys.HOT_PIXEL_MODE, new BaseModeApi2(cameraUiWrapper, SettingKeys.HOT_PIXEL_MODE,CaptureRequest.HOT_PIXEL_MODE));
        if (SettingsManager.get(SettingKeys.Ae_TargetFPS).isSupported())
            add(SettingKeys.Ae_TargetFPS, new AeTargetRangeApi2(cameraUiWrapper, SettingKeys.Ae_TargetFPS,CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE));

        if (SettingsManager.get(SettingKeys.dualPrimaryCameraMode).isSupported() && !SettingsManager.getInstance().getIsFrontCamera())
        {
            add(SettingKeys.dualPrimaryCameraMode, new DualCameraModeHuaweiApi2(cameraUiWrapper, SettingKeys.dualPrimaryCameraMode, CaptureRequestEx.HUAWEI_DUAL_SENSOR_MODE));
        }

        add(SettingKeys.JpegQuality, new JpegQualityModeApi2(cameraUiWrapper));

        if (SettingsManager.get(SettingKeys.M_Whitebalance).isSupported()) {
            try {
                WbHandler wbHandler = new WbHandler(cameraUiWrapper);
                add(SettingKeys.M_Whitebalance, wbHandler.manualWbCt);
                add(SettingKeys.WhiteBalanceMode, wbHandler.whiteBalanceApi2);
            } catch (NullPointerException ex) {
                Log.d(TAG, "seem whitebalance is unsupported");
                Log.WriteEx(ex);
            }
        }

        //dont make that avail for the ui its only internal used
        //COLOR_CORRECTION_MODE = colorCorrectionMode;

        //AE mode start
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.HuaweiCamera2Ex) {
            AeManagerHuaweiCamera2 aeManager = new AeManagerHuaweiCamera2(camera2Fragment);
            add(SettingKeys.M_ExposureCompensation, aeManager.getExposureCompensation());
            add(SettingKeys.M_ManualIso, aeManager.getIso());
            add(SettingKeys.M_ExposureTime, aeManager.getExposureTime());
        }
        else {
            AeManagerCamera2 aeManager = new AeManagerCamera2(cameraUiWrapper);
            add(SettingKeys.M_ExposureCompensation, aeManager.getExposureCompensation());
            add(SettingKeys.M_ManualIso, aeManager.getIso());
            add(SettingKeys.M_ExposureTime, aeManager.getExposureTime());
            //not used by huawei
            add(SettingKeys.ExposureMode, aeManager.getAeMode());
            //get(Settings.ExposureMode).addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).aeModeListner);
        }
        //pass stuff to the parameterhandler that it get used by the ui

        //ae mode end
        add(SettingKeys.PictureSize, new PictureSizeModeApi2(cameraUiWrapper));


        //MF
        add(SettingKeys.M_Focus,new ManualFocus(cameraUiWrapper));

        //MF END

        ManualToneMapCurveApi2 manualToneMapCurveApi2 = new ManualToneMapCurveApi2(cameraUiWrapper);
        /*ManualContrast = manualToneMapCurveApi2.contrast;
        ManualBrightness = manualToneMapCurveApi2.brightness;
        black = manualToneMapCurveApi2.black;
        shadows = manualToneMapCurveApi2.shadowsp;
        midtones = manualToneMapCurveApi2.midtonesp;
        highlights = manualToneMapCurveApi2.highlightsp;
        white = manualToneMapCurveApi2.whitep;*/
        add(SettingKeys.TONE_CURVE_PARAMETER, manualToneMapCurveApi2.toneCurveParameter);

        add(SettingKeys.TONE_MAP_MODE,new BaseModeApi2(cameraUiWrapper, SettingKeys.TONE_MAP_MODE,CaptureRequest.TONEMAP_MODE));
        get(SettingKeys.TONE_MAP_MODE).addEventListner(manualToneMapCurveApi2);

        add(SettingKeys.PictureFormat, new PictureFormatParameterApi2(cameraUiWrapper, SettingKeys.PictureFormat, null));

        add(SettingKeys.ExposureLock, new AeLockModeApi2(cameraUiWrapper));

        add(SettingKeys.M_Burst, new BurstApi2(cameraUiWrapper));
        add(SettingKeys.VideoProfiles, new VideoProfilesApi2(cameraUiWrapper));
        add(SettingKeys.MATRIX_SET, new MatrixChooserParameter(SettingsManager.getInstance().getMatrixesMap()));
        add(SettingKeys.TONEMAP_SET, new ToneMapChooser(SettingsManager.getInstance().getToneMapProfiles()));
        add(SettingKeys.M_Zoom, new ZoomApi2(cameraUiWrapper));
        SetAppSettingsToParameters();
    }

    @Override
    public void SetFocusAREA(Rect focusAreas) {

    }



    @Override
    public void SetPictureOrientation(int orientation)
    {
        if (SettingsManager.get(SettingKeys.orientationHack).get())
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
            camera2Fragment.captureSessionHandler.SetParameterRepeating(CaptureRequest.JPEG_ORIENTATION, orientation,true);
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    @Override
    public float[] getFocusDistances()
    {
        return camera2Fragment.cameraBackroundValuesChangedListner.GetFocusRange();
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
