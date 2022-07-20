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
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Key;
import android.os.Build;
import android.os.Build.VERSION_CODES;

import androidx.databinding.Observable;

import java.util.List;

import camera2_hidden_keys.huawei.CaptureRequestHuawei;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.ae.AeStates;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.basecamera.parameters.modes.ModuleParameters;
import freed.cam.apis.basecamera.parameters.modes.OrientationHackParameter;
import freed.cam.apis.basecamera.parameters.modes.ToneMapChooser;
import freed.cam.apis.basecamera.parameters.modes.VideoAudioSourceMode;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.parameters.ae.AeManagerCamera2;
import freed.cam.apis.camera2.parameters.ae.AeManagerCamera2Qcom;
import freed.cam.apis.camera2.parameters.ae.AeManagerHuaweiCamera2;
import freed.cam.apis.camera2.parameters.ae.FreedAeManger;
import freed.cam.apis.camera2.parameters.manual.BurstApi2;
import freed.cam.apis.camera2.parameters.manual.ManualApertureApi2;
import freed.cam.apis.camera2.parameters.manual.ManualFocus;
import freed.cam.apis.camera2.parameters.manual.ManualSaturationQcomApi2;
import freed.cam.apis.camera2.parameters.manual.ManualSharpnessQcomApi2;
import freed.cam.apis.camera2.parameters.manual.ManualToneMapCurveApi2;
import freed.cam.apis.camera2.parameters.manual.ManualWbCtApi2Hw;
import freed.cam.apis.camera2.parameters.manual.ZoomApi2;
import freed.cam.apis.camera2.parameters.modes.AeLockModeApi2;
import freed.cam.apis.camera2.parameters.modes.AeTargetRangeApi2;
import freed.cam.apis.camera2.parameters.modes.BaseModeApi2;
import freed.cam.apis.camera2.parameters.modes.DualCameraModeHuaweiApi2;
import freed.cam.apis.camera2.parameters.modes.FlashMode;
import freed.cam.apis.camera2.parameters.modes.FocusMode;
import freed.cam.apis.camera2.parameters.modes.JpegQualityModeApi2;
import freed.cam.apis.camera2.parameters.modes.MFNR;
import freed.cam.apis.camera2.parameters.modes.PictureFormatParameterApi2;
import freed.cam.apis.camera2.parameters.modes.PictureSizeModeApi2;
import freed.cam.apis.camera2.parameters.modes.RawSizeModeApi2;
import freed.cam.apis.camera2.parameters.modes.SecondarySensorSizeModeApi2;
import freed.cam.apis.camera2.parameters.modes.VideoProfilesApi2;
import freed.cam.apis.camera2.parameters.modes.XiaomiMfnr;
import freed.cam.apis.camera2.parameters.modes.YuvSizeModeApi2;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.utils.Log;
import freed.utils.OrientationUtil;
import freed.settings.VideoToneCurveProfile;

/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ParameterHandlerApi2 extends AbstractParameterHandler<Camera2>
{
    private final String TAG = ParameterHandlerApi2.class.getSimpleName();
    private final boolean dumpAvailRequestKeys = false;


    private CameraHolderApi2 cameraHolder;
    private ManualToneMapCurveApi2 manualToneMapCurveApi2;
    private AeManagerCamera2 aeManagerCamera2;

    public AeManagerCamera2 getAeManagerCamera2() {
        return aeManagerCamera2;
    }

    public ParameterHandlerApi2(Camera2 wrapper)
    {
        super(wrapper);
    }

    public void Init()
    {
        this.cameraHolder = cameraUiWrapper.getCameraHolder();
        List<Key<?>> keys = cameraHolder.characteristics.getAvailableCaptureRequestKeys();
        if (dumpAvailRequestKeys)
            for (int i = 0; i< keys.size(); i++)
            {
                Log.d(TAG, keys.get(i).getName());
            }
        add(SettingKeys.Module, new ModuleParameters(cameraUiWrapper));
        if (settingsManager.get(SettingKeys.FlashMode).isSupported())
            add(SettingKeys.FlashMode, new FlashMode(cameraUiWrapper, SettingKeys.FlashMode));
        if (settingsManager.get(SettingKeys.SceneMode).isSupported())
            add(SettingKeys.SceneMode, new BaseModeApi2(cameraUiWrapper, SettingKeys.SceneMode,CaptureRequest.CONTROL_SCENE_MODE));
        if (settingsManager.get(SettingKeys.AntiBandingMode).isSupported())
            add(SettingKeys.AntiBandingMode, new BaseModeApi2(cameraUiWrapper, SettingKeys.AntiBandingMode, CaptureRequest.CONTROL_AE_ANTIBANDING_MODE));
        if (settingsManager.get(SettingKeys.ColorMode).isSupported())
            add(SettingKeys.ColorMode, new BaseModeApi2(cameraUiWrapper, SettingKeys.ColorMode,CaptureRequest.CONTROL_EFFECT_MODE));
        if (settingsManager.get(SettingKeys.CONTROL_MODE).isSupported())
            add(SettingKeys.CONTROL_MODE, new BaseModeApi2(cameraUiWrapper, SettingKeys.CONTROL_MODE,CaptureRequest.CONTROL_MODE));
        if (settingsManager.get(SettingKeys.Denoise).isSupported())
            add(SettingKeys.Denoise, new BaseModeApi2(cameraUiWrapper, SettingKeys.Denoise,CaptureRequest.NOISE_REDUCTION_MODE));
        if (settingsManager.get(SettingKeys.EDGE_MODE).isSupported())
            add(SettingKeys.EDGE_MODE, new BaseModeApi2(cameraUiWrapper, SettingKeys.EDGE_MODE,CaptureRequest.EDGE_MODE));
        if (settingsManager.get(SettingKeys.DISTORTION_CORRECTION_MODE).isSupported() && Build.VERSION.SDK_INT >= VERSION_CODES.P)
            add(SettingKeys.DISTORTION_CORRECTION_MODE, new BaseModeApi2(cameraUiWrapper, SettingKeys.DISTORTION_CORRECTION_MODE,CaptureRequest.DISTORTION_CORRECTION_MODE));
        if (settingsManager.get(SettingKeys.FACE_DETECTOR_MODE).isSupported())
            add(SettingKeys.FACE_DETECTOR_MODE, new BaseModeApi2(cameraUiWrapper, SettingKeys.FACE_DETECTOR_MODE,CaptureRequest.STATISTICS_FACE_DETECT_MODE));


        if (settingsManager.get(SettingKeys.OIS_MODE).isSupported())
            add(SettingKeys.OIS_MODE, new BaseModeApi2(cameraUiWrapper, SettingKeys.OIS_MODE,CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE));
        if (settingsManager.get(SettingKeys.FocusMode).isSupported()) {
            FocusMode focusMode = new FocusMode(cameraUiWrapper, SettingKeys.FocusMode, CaptureRequest.CONTROL_AF_MODE);
            add(SettingKeys.FocusMode, focusMode);
            focusMode.addOnPropertyChangedCallback(cameraUiWrapper.focusHandler.focusmodeObserver);
        }
        if (settingsManager.get(SettingKeys.HOT_PIXEL_MODE).isSupported())
            add(SettingKeys.HOT_PIXEL_MODE, new BaseModeApi2(cameraUiWrapper, SettingKeys.HOT_PIXEL_MODE,CaptureRequest.HOT_PIXEL_MODE));
        if (settingsManager.get(SettingKeys.Ae_TargetFPS).isSupported())
            add(SettingKeys.Ae_TargetFPS, new AeTargetRangeApi2(cameraUiWrapper, SettingKeys.Ae_TargetFPS,CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE));

        if (settingsManager.get(SettingKeys.dualPrimaryCameraMode).isSupported() && !settingsManager.getIsFrontCamera())
        {
            add(SettingKeys.dualPrimaryCameraMode, new DualCameraModeHuaweiApi2(cameraUiWrapper, SettingKeys.dualPrimaryCameraMode, CaptureRequestHuawei.HUAWEI_DUAL_SENSOR_MODE));
        }

        if (settingsManager.get(SettingKeys.VideoStabilization).isSupported())
            add(SettingKeys.VideoStabilization, new BaseModeApi2(cameraUiWrapper,SettingKeys.VideoStabilization, CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE));

        add(SettingKeys.JpegQuality, new JpegQualityModeApi2(cameraUiWrapper));
        if (settingsManager.get(SettingKeys.MFNR).isSupported())
            add(SettingKeys.MFNR, new MFNR(cameraUiWrapper));
        if (settingsManager.get(SettingKeys.XIAOMI_MFNR).isSupported())
            add(SettingKeys.XIAOMI_MFNR, new XiaomiMfnr(cameraUiWrapper));

        if (settingsManager.get(SettingKeys.COLOR_CORRECTION_MODE).isSupported() ) {
            try {
                WbHandler wbHandler = new WbHandler(cameraUiWrapper);
                if (wbHandler.manualWbCt != null && !settingsManager.get(SettingKeys.useHuaweiWhiteBalance).get())
                    add(SettingKeys.M_Whitebalance, wbHandler.manualWbCt);
                else
                {
                    add(SettingKeys.M_Whitebalance, new ManualWbCtApi2Hw(cameraUiWrapper));
                }

                add(SettingKeys.WhiteBalanceMode, wbHandler.whiteBalanceApi2);
            } catch (NullPointerException ex) {
                Log.d(TAG, "seem whitebalance is unsupported");
                Log.WriteEx(ex);
            }
        }

        //AE mode start
        if (settingsManager.getFrameWork() == Frameworks.HuaweiCamera2Ex) {
            aeManagerCamera2 = new AeManagerHuaweiCamera2(cameraUiWrapper);
            add(SettingKeys.M_ExposureCompensation, aeManagerCamera2.getExposureCompensation());
            add(SettingKeys.M_ManualIso, aeManagerCamera2.getIso());
            add(SettingKeys.M_ExposureTime, aeManagerCamera2.getExposureTime());
        }
        else {
            if (settingsManager.get(SettingKeys.USE_QCOM_AE).get() && !settingsManager.getGlobal(SettingKeys.USE_FREEDCAM_AE).get())
                aeManagerCamera2 = new AeManagerCamera2Qcom(cameraUiWrapper);
            else if (settingsManager.getGlobal(SettingKeys.USE_FREEDCAM_AE).get())
                aeManagerCamera2 = cameraUiWrapper.getFreedAeManger();
            else
                aeManagerCamera2 = new AeManagerCamera2(cameraUiWrapper);
            add(SettingKeys.M_ExposureCompensation, aeManagerCamera2.getExposureCompensation());
            add(SettingKeys.M_ManualIso, aeManagerCamera2.getIso());
            add(SettingKeys.M_ExposureTime, aeManagerCamera2.getExposureTime());
            //not used by huawei and not used anywhere on camera2
            //add(SettingKeys.ExposureMode, aeManagerCamera2.getAeMode());
        }

        //ae mode end
        add(SettingKeys.PictureSize, new PictureSizeModeApi2(cameraUiWrapper));


        //MF
        if (settingsManager.get(SettingKeys.M_Focus).isSupported())
            add(SettingKeys.M_Focus,new ManualFocus(cameraUiWrapper));

        //MF END

        if (settingsManager.get(SettingKeys.M_Aperture).isSupported())
        {
            add(SettingKeys.M_Aperture, new ManualApertureApi2(cameraUiWrapper, SettingKeys.M_Aperture));
        }

        if (settingsManager.get(SettingKeys.M_Sharpness).isSupported())
            add(SettingKeys.M_Sharpness, new ManualSharpnessQcomApi2(cameraUiWrapper));

        if (settingsManager.get(SettingKeys.M_Saturation).isSupported())
            add(SettingKeys.M_Saturation, new ManualSaturationQcomApi2(cameraUiWrapper));

        manualToneMapCurveApi2 = new ManualToneMapCurveApi2(cameraUiWrapper);
        /*ManualContrast = manualToneMapCurveApi2.contrast;
        ManualBrightness = manualToneMapCurveApi2.brightness;
        black = manualToneMapCurveApi2.black;
        shadows = manualToneMapCurveApi2.shadowsp;
        midtones = manualToneMapCurveApi2.midtonesp;
        highlights = manualToneMapCurveApi2.highlightsp;
        white = manualToneMapCurveApi2.whitep;*/
        add(SettingKeys.TONE_CURVE_PARAMETER, manualToneMapCurveApi2.toneCurveParameter);

        BaseModeApi2 tonemapmode = new BaseModeApi2(cameraUiWrapper, SettingKeys.TONE_MAP_MODE,CaptureRequest.TONEMAP_MODE);
        add(SettingKeys.TONE_MAP_MODE,tonemapmode);
        tonemapmode.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                    manualToneMapCurveApi2.onToneMapModeChanged(tonemapmode.getStringValue());
            }
        });


        add(SettingKeys.PictureFormat, new PictureFormatParameterApi2(cameraUiWrapper, SettingKeys.PictureFormat, null));

        add(SettingKeys.ExposureLock, new AeLockModeApi2(cameraUiWrapper));

        add(SettingKeys.M_Burst, new BurstApi2(cameraUiWrapper));
        add(SettingKeys.VideoProfiles, new VideoProfilesApi2(cameraUiWrapper));
        add(SettingKeys.VIDEO_AUDIO_SOURCE, new VideoAudioSourceMode(cameraUiWrapper,SettingKeys.VIDEO_AUDIO_SOURCE));
        add(SettingKeys.MATRIX_SET, new MatrixChooserParameter(settingsManager.getMatrixesMap()));
        add(SettingKeys.TONEMAP_SET, new ToneMapChooser(settingsManager.getToneMapProfiles()));
        add(SettingKeys.M_Zoom, new ZoomApi2(cameraUiWrapper));

        //disable due not working
        //if (settingsManager.get(SettingKeys.secondarySensorSize).isSupported())
        //    add(SettingKeys.secondarySensorSize, new SecondarySensorSizeModeApi2(cameraUiWrapper));

        if (settingsManager.get(SettingKeys.RawSize).isSupported())
            add(SettingKeys.RawSize, new RawSizeModeApi2(cameraUiWrapper, SettingKeys.RawSize));
        if (settingsManager.get(SettingKeys.YuvSize).isSupported())
            add(SettingKeys.YuvSize, new YuvSizeModeApi2(cameraUiWrapper));
        if (settingsManager.get(SettingKeys.LensShade).isSupported())
            add(SettingKeys.LensShade, new BaseModeApi2(cameraUiWrapper,SettingKeys.LensShade,CaptureRequest.SHADING_MODE));

        add(SettingKeys.orientationHack,new OrientationHackParameter(cameraUiWrapper,SettingKeys.orientationHack));
    }

    @Override
    public void SetFocusAREA(Rect focusAreas) {

    }

    @Override
    public void SetPictureOrientation(int orientation)
    {
        orientation = OrientationUtil.getOrientation(orientation);
        if (cameraHolder == null || cameraHolder.isWorking)
            return;
        try
        {
            Log.d(TAG, "Set Orientation to:" + orientation);
            cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.JPEG_ORIENTATION, orientation,true);
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    @Override
    public float[] getFocusDistances()
    {
        return cameraUiWrapper.cameraBackroundValuesChangedListner.GetFocusRange();
    }

    @Override
    public float getCurrentExposuretime() {
        return 0;
    }

    @Override
    public int getCurrentIso() {
        return 0;
    }

    @Override
    public void SetAppSettingsToParameters() {
        super.SetAppSettingsToParameters();
        if (settingsManager.get(SettingKeys.TONE_MAP_MODE) != null
                && settingsManager.get(SettingKeys.TONE_MAP_MODE).get() != null
                && settingsManager.get(SettingKeys.TONE_MAP_MODE).get().equals("CONTRAST_CURVE")) {
            VideoToneCurveProfile profile = settingsManager.getVideoToneCurveProfiles().get(settingsManager.get(SettingKeys.TONE_CURVE_PARAMETER).get());
            if (profile != null)
                ((ManualToneMapCurveApi2.ToneCurveParameter) cameraUiWrapper.getParameterHandler().get(SettingKeys.TONE_CURVE_PARAMETER)).setCurveToCamera(pointFtoFloatArray(profile.rgb));
        }
    }

    public static float[] pointFtoFloatArray(PointF[] pointFs)
    {
        float[] ar = new float[pointFs.length*2];
        int count = 0;
        for (int i = 0; i< pointFs.length; i++)
        {
            ar[count++] = pointFs[i].x;
            ar[count++] = pointFs[i].y;
        }
        return ar;
    }
}
