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
import freed.cam.apis.basecamera.parameters.Parameters;
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
import freed.settings.AppSettingsManager;
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
        add(Parameters.Module, new ModuleParameters(cameraUiWrapper));
        if (AppSettingsManager.getInstance().flashMode.isSupported())
            add(Parameters.FlashMode, new BaseModeApi2(cameraUiWrapper,AppSettingsManager.getInstance().flashMode,CaptureRequest.FLASH_MODE));
        if (AppSettingsManager.getInstance().sceneMode.isSupported())
            add(Parameters.SceneMode, new BaseModeApi2(cameraUiWrapper,AppSettingsManager.getInstance().sceneMode,CaptureRequest.CONTROL_SCENE_MODE));
        if (AppSettingsManager.getInstance().antiBandingMode.isSupported())
            add(Parameters.AntiBandingMode, new BaseModeApi2(cameraUiWrapper,AppSettingsManager.getInstance().antiBandingMode, CaptureRequest.CONTROL_AE_ANTIBANDING_MODE));
        if (AppSettingsManager.getInstance().colorMode.isSupported())
            add(Parameters.ColorMode, new BaseModeApi2(cameraUiWrapper,AppSettingsManager.getInstance().colorMode,CaptureRequest.CONTROL_EFFECT_MODE));
        if (AppSettingsManager.getInstance().controlMode.isSupported())
            add(Parameters.ControlMode, new BaseModeApi2(cameraUiWrapper,AppSettingsManager.getInstance().controlMode,CaptureRequest.CONTROL_MODE));
        if (AppSettingsManager.getInstance().denoiseMode.isSupported())
            add(Parameters.Denoise, new BaseModeApi2(cameraUiWrapper,AppSettingsManager.getInstance().denoiseMode,CaptureRequest.NOISE_REDUCTION_MODE));
        if (AppSettingsManager.getInstance().edgeMode.isSupported())
            add(Parameters.EdgeMode, new BaseModeApi2(cameraUiWrapper,AppSettingsManager.getInstance().edgeMode,CaptureRequest.EDGE_MODE));
        if (AppSettingsManager.getInstance().opticalImageStabilisation.isSupported())
            add(Parameters.oismode, new BaseModeApi2(cameraUiWrapper,AppSettingsManager.getInstance().opticalImageStabilisation,CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE));
        if (AppSettingsManager.getInstance().focusMode.isSupported()) {
            add(Parameters.FocusMode, new BaseModeApi2(cameraUiWrapper, AppSettingsManager.getInstance().focusMode, CaptureRequest.CONTROL_AF_MODE));
            get(Parameters.FocusMode).addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).focusModeListner);
        }
        if (AppSettingsManager.getInstance().hotpixelMode.isSupported())
            add(Parameters.HotPixelMode, new BaseModeApi2(cameraUiWrapper,AppSettingsManager.getInstance().hotpixelMode,CaptureRequest.HOT_PIXEL_MODE));
        if (AppSettingsManager.getInstance().ae_TagetFPS.isSupported())
            add(Parameters.Ae_TargetFPS, new AeTargetRangeApi2(cameraUiWrapper,AppSettingsManager.getInstance().ae_TagetFPS,CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE));

        if (AppSettingsManager.getInstance().dualPrimaryCameraMode.isSupported() && !AppSettingsManager.getInstance().getIsFrontCamera())
        {
            add(Parameters.dualPrimaryCameraMode, new DualCameraModeHuaweiApi2(cameraUiWrapper,AppSettingsManager.getInstance().dualPrimaryCameraMode, CaptureRequestEx.HUAWEI_DUAL_SENSOR_MODE));
        }
        add(Parameters.JpegQuality, new JpegQualityModeApi2(cameraUiWrapper));

        WbHandler wbHandler = new WbHandler(cameraUiWrapper);
        add(Parameters.M_Whitebalance, wbHandler.manualWbCt);
        add(Parameters.WhiteBalanceMode, wbHandler.whiteBalanceApi2);
        //dont make that avail for the ui its only internal used
        //ColorCorrectionMode = colorCorrectionMode;

        //AE mode start
        AeHandler aeHandler;
        if (AppSettingsManager.getInstance().useHuaweiCam2Extension.getBoolean())
            aeHandler = new HuaweiAeHandler(cameraUiWrapper);
        else {
            aeHandler = new AeHandler(cameraUiWrapper);
            //not used by huawei
            add(Parameters.ExposureMode, aeHandler.aeModeApi2);
            get(Parameters.ExposureMode).addEventListner(((FocusHandler) cameraUiWrapper.getFocusHandler()).aeModeListner);
        }
        //pass stuff to the parameterhandler that it get used by the ui

        add(Parameters.M_ExposureTime, aeHandler.manualExposureTimeApi2);
        add(Parameters.M_ExposureCompensation, aeHandler.manualExposureApi2);
        add(Parameters.M_ManualIso, aeHandler.manualISoApi2);
        //ae mode end
        add(Parameters.PictureSize, new PictureSizeModeApi2(cameraUiWrapper));


        //MF
        add(Parameters.M_Focus,new ManualFocus(cameraUiWrapper)); ;

        //MF END

        ManualToneMapCurveApi2 manualToneMapCurveApi2 = new ManualToneMapCurveApi2(cameraUiWrapper);
        /*ManualContrast = manualToneMapCurveApi2.contrast;
        ManualBrightness = manualToneMapCurveApi2.brightness;
        black = manualToneMapCurveApi2.black;
        shadows = manualToneMapCurveApi2.shadowsp;
        midtones = manualToneMapCurveApi2.midtonesp;
        highlights = manualToneMapCurveApi2.highlightsp;
        white = manualToneMapCurveApi2.whitep;*/
        add(Parameters.M_ToneCurve, manualToneMapCurveApi2.toneCurveParameter);

        add(Parameters.ToneMapMode,new BaseModeApi2(cameraUiWrapper,AppSettingsManager.getInstance().toneMapMode,CaptureRequest.TONEMAP_MODE));
        get(Parameters.ToneMapMode).addEventListner(manualToneMapCurveApi2);

        add(Parameters.PictureFormat, new PictureFormatParameterApi2(cameraUiWrapper, AppSettingsManager.getInstance().pictureFormat, null));

        add(Parameters.ExposureLock, new AeLockModeApi2(cameraUiWrapper));

        add(Parameters.M_Burst, new BurstApi2(cameraUiWrapper));
        add(Parameters.Focuspeak, new FocusPeakModeApi2(cameraUiWrapper));
        add(Parameters.VideoProfiles, new VideoProfilesApi2(cameraUiWrapper));
        add(Parameters.matrixChooser, new MatrixChooserParameter(AppSettingsManager.getInstance().getMatrixesMap()));
        add(Parameters.tonemapChooser, new ToneMapChooser(AppSettingsManager.getInstance().getToneMapProfiles()));
        add(Parameters.ZoomSetting, new ZoomApi2(cameraUiWrapper));
        SetAppSettingsToParameters();
    }

    @Override
    public void SetFocusAREA(Rect focusAreas) {

    }



    @Override
    public void SetPictureOrientation(int orientation)
    {
        if (AppSettingsManager.getInstance().orientationhack.getBoolean())
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
            cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.JPEG_ORIENTATION, orientation);
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
