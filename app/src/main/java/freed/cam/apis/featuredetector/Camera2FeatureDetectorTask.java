package freed.cam.apis.featuredetector;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Range;
import android.util.Size;

import com.huawei.camera2ex.CameraCharacteristicsEx;
import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import freed.renderscript.RenderScriptManager;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.SettingMode;
import freed.utils.Log;
import freed.utils.StringFloatArray;
import freed.utils.StringUtils;
import freed.utils.VideoMediaProfile;


/**
 * Created by troop on 23.01.2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2FeatureDetectorTask extends AbstractFeatureDetectorTask {

    private final String TAG = Camera2FeatureDetectorTask.class.getSimpleName();
    private Context context;
    boolean hasCamera2Features;
    public Camera2FeatureDetectorTask(ProgressUpdate progressUpdate, Context context) {
        super(progressUpdate);
        this.context = context;
    }

    @Override
    public void detect()
    {
        publishProgress("###################");
        publishProgress("#######Camera2#####");
        publishProgress("###################");
        SettingsManager.getInstance().setCamApi(SettingsManager.API_2);
        try {
            publishProgress("Check Camera2");
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String cameras[] = manager.getCameraIdList();
            SettingsManager.getInstance().setCamerasCount(cameras.length);

            for (String s : cameras)
            {

                publishProgress("###################");
                publishProgress("#####CameraID:"+s+"####");
                publishProgress("###################");
                publishProgress("Check camera features:" + s);
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(s);
                boolean front = characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
                SettingsManager.get(SettingKeys.Module).set(SettingsManager.getInstance().getResString(R.string.module_picture));
                SettingsManager.getInstance().SetCurrentCamera(Integer.parseInt(s));
                SettingsManager.getInstance().setIsFrontCamera(front);
                int hwlvl = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);

                SettingsManager.get(SettingKeys.selfTimer).setValues(SettingsManager.getInstance().getResources().getStringArray(R.array.selftimervalues));
                SettingsManager.get(SettingKeys.selfTimer).set(SettingsManager.get(SettingKeys.selfTimer).getValues()[0]);

                if (RenderScriptManager.isSupported()) {
                    SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).setValues(SettingsManager.getInstance().getResources().getStringArray(R.array.focuspeakColors));
                    SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).set(SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).getValues()[0]);
                    SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).setIsSupported(true);
                }

                publishProgress("Camera 2 Level:" + hwlvl);

                //check first if a already checked cam have camera2features and if its now the front cam that dont have a camera2feature.
                //in that case set it to true
                //else it would override the already detected featureset from last cam and disable api2
                if (SettingsManager.getInstance().hasCamera2Features() && front) {
                    hasCamera2Features = true;
                    Log.d(TAG,"Front cam has no camera2 featureset, try to find supported things anyway");
                }
               /* else if (hwlvl != CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                    hasCamera2Features = true;*/
                else
                    hasCamera2Features = true;

                SettingsManager.getInstance().setHasCamera2Features(hasCamera2Features);
                publishProgress("IsCamera2 Full Device:" + SettingsManager.getInstance().hasCamera2Features() + " isFront:" + SettingsManager.getInstance().getIsFrontCamera());

                SettingsManager.get(SettingKeys.GuideList).setValues(SettingsManager.getInstance().getResources().getStringArray(R.array.guidelist));
                SettingsManager.get(SettingKeys.GuideList).set(SettingsManager.get(SettingKeys.GuideList).getValues()[0]);


                if (hasCamera2Features) {

                    try {
                        if(!SettingsManager.getInstance().getIsFrontCamera()) {
                            publishProgress("Detect Flash");
                            detectFlash(characteristics);
                            sendProgress(SettingsManager.get(SettingKeys.FlashMode), "Flash");
                        }
                    } catch (Exception e){
                            Log.WriteEx(e);
                            publishProgress("Detect Flash failed");
                        }

                    try {
                        publishProgress("Detect Scene");
                        detectSceneModes(characteristics);
                        sendProgress(SettingsManager.get(SettingKeys.SceneMode), "Scene");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Scene failed");
                    }

                    try {
                        publishProgress("Detect Antibanding");
                        detectIntMode(characteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES, SettingsManager.get(SettingKeys.AntiBandingMode), R.array.antibandingmodes);
                        sendProgress(SettingsManager.get(SettingKeys.AntiBandingMode), "Antibanding");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Antibanding failed");
                    }

                    try {
                        publishProgress("Detect Color");
                        detectIntMode(characteristics, CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS, SettingsManager.get(SettingKeys.ColorMode), R.array.colormodes);
                        sendProgress(SettingsManager.get(SettingKeys.ColorMode), "Color");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Color failed");
                    }

                    try {
                        publishProgress("Detect EDGE_MODE");
                        detectIntMode(characteristics, CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES, SettingsManager.get(SettingKeys.EDGE_MODE), R.array.edgeModes);
                        sendProgress(SettingsManager.get(SettingKeys.EDGE_MODE), "EDGE_MODE");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Edge failed");
                    }


                    try {
                        publishProgress("Detect OpticalImageStabilisationMode");
                        detectIntMode(characteristics, CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION, SettingsManager.get(SettingKeys.OIS_MODE), R.array.digitalImageStabModes);
                        sendProgress(SettingsManager.get(SettingKeys.OIS_MODE), "OpticalImageStabilisationMode");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Ois failed");
                    }

                    try {
                        publishProgress("Detect FocusMode");
                        detectIntMode(characteristics, CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES, SettingsManager.get(SettingKeys.FocusMode), R.array.focusModes);
                        sendProgress(SettingsManager.get(SettingKeys.FocusMode), "FocusMode");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Focus failed");
                    }


                    try {
                        publishProgress("Detect HOT_PIXEL_MODE");
                        detectIntMode(characteristics, CameraCharacteristics.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES, SettingsManager.get(SettingKeys.HOT_PIXEL_MODE), R.array.hotpixelmodes);
                        sendProgress(SettingsManager.get(SettingKeys.HOT_PIXEL_MODE), "HOT_PIXEL_MODE");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect HotPixel failed");
                    }

                    try {
                        publishProgress("Detect Denoise");
                        detectIntMode(characteristics, CameraCharacteristics.NOISE_REDUCTION_AVAILABLE_NOISE_REDUCTION_MODES, SettingsManager.get(SettingKeys.Denoise), R.array.denoiseModes);
                        sendProgress(SettingsManager.get(SettingKeys.Denoise), "Denoise");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Denoise failed");
                    }

                    try {
                        publishProgress("Detect PictureFormat");
                        detectPictureFormats(characteristics);
                        sendProgress(SettingsManager.get(SettingKeys.PictureFormat), "PictureFormat");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect PictureFormat failed");
                    }

                    try {
                        publishProgress("Detect Manual Focus");
                        detectManualFocus(characteristics);
                        sendProgress(SettingsManager.get(SettingKeys.M_Focus), "Manual Focus");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect MF failed");
                    }

                    try {
                        publishProgress("Detect PictureSizes");
                        detectPictureSizes(characteristics);
                        sendProgress(SettingsManager.get(SettingKeys.PictureSize), "PictureSizes:");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect PictureSize failed");
                    }

                    try {
                        publishProgress("Detect Video Profiles");
                        detectVideoMediaProfiles(SettingsManager.getInstance().GetCurrentCamera());
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Video Profiles failed");
                    }

                    try {
                        publishProgress("Detect ExposureModes");
                        detectIntMode(characteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES, SettingsManager.get(SettingKeys.ExposureMode), R.array.aemodes);
                        sendProgress(SettingsManager.get(SettingKeys.ExposureMode), "ExposureModes:");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect ExposureModes failed");
                    }

                    try {
                        publishProgress("Detect ExposureCompensation");
                        detectManualExposure(characteristics);
                        sendProgress(SettingsManager.get(SettingKeys.M_ExposureCompensation), "ExposureCompensation:");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect ExpoCompensation failed");
                    }

                    try {
                        publishProgress("Detect ExposureTime");
                        detectManualexposureTime(characteristics);
                        sendProgress(SettingsManager.get(SettingKeys.M_ExposureTime), "ExposureTime:");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect ExpoTime failed");
                    }

                    try {
                        publishProgress("Detect Iso");
                        detectManualIso(characteristics);
                        sendProgress(SettingsManager.get(SettingKeys.M_ManualIso), "Iso:");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Iso failed");
                    }

                    try {
                        publishProgress("Detect ColorCorrection");
                        detectColorcorrectionMode(characteristics);
                        sendProgress(SettingsManager.get(SettingKeys.COLOR_CORRECTION_MODE), "ColorCorrection");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect ColorCorrection failed");
                    }

                    try {
                        publishProgress("Detect ToneMap");
                        detectIntMode(characteristics, CameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES, SettingsManager.get(SettingKeys.TONE_MAP_MODE),R.array.tonemapmodes);
                        sendProgress(SettingsManager.get(SettingKeys.TONE_MAP_MODE), "Tonemap");
                    }
                    catch (Exception ex)
                    {
                        Log.WriteEx(ex);
                        publishProgress("Detect Tonemap Mode failed");
                    }

                    try {
                        publishProgress("Detect Whitebalance");
                        detectIntMode(characteristics, CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES, SettingsManager.get(SettingKeys.WhiteBalanceMode), R.array.whitebalancemodes);
                        sendProgress(SettingsManager.get(SettingKeys.WhiteBalanceMode), "Whitebalance");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect WB Mode failed");
                    }

                    try {
                        publishProgress("Detect CONTROL_MODE");
                        detectControlMode(characteristics);
                        sendProgress(SettingsManager.get(SettingKeys.CONTROL_MODE), "CONTROL_MODE");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Control mode failed");
                    }

                    Range[] aetargetfps = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
                    if (aetargetfps != null && aetargetfps.length>1)
                    {
                        String[] t = new String[aetargetfps.length];
                        for (int i = 0;i < aetargetfps.length; i++)
                        {
                            t[i] = aetargetfps[i].getLower()+","+aetargetfps[i].getUpper();
                        }
                        SettingsManager.get(SettingKeys.Ae_TargetFPS).setValues(t);
                        SettingsManager.get(SettingKeys.Ae_TargetFPS).setIsSupported(true);
                    }

                    detectHuaweiParameters(characteristics);

                }
            }
            SettingsManager.getInstance().SetCurrentCamera(0);
            if (!hasCamera2Features)
                SettingsManager.getInstance().setCamApi(SettingsManager.API_1);

            if (SettingsManager.getInstance().hasCamera2Features() && !SettingsManager.get(SettingKeys.openCamera1Legacy).isPresetted()) {
                SettingsManager.get(SettingKeys.openCamera1Legacy).set(true);
            }
            Log.d(TAG, "Can Open Legacy: " + SettingsManager.get(SettingKeys.openCamera1Legacy).get() + " was presetted: " + SettingsManager.get(SettingKeys.openCamera1Legacy).isPresetted());
        }
        catch (Throwable ex) {
            Log.WriteEx(ex);
            if (!hasCamera2Features)
                SettingsManager.getInstance().setHasCamera2Features(false);
            else
                SettingsManager.getInstance().setHasCamera2Features(true);

            SettingsManager.getInstance().setCamApi(SettingsManager.API_1);
        }
    }

    private void detectHuaweiParameters(CameraCharacteristics characteristics) {
        try {
            detectByteMode(characteristics, CameraCharacteristicsEx.HUAWEI_AVAILABLE_DUAL_PRIMARY, SettingsManager.get(SettingKeys.dualPrimaryCameraMode), R.array.dual_camera_mode);
        }
        catch (IllegalArgumentException ex)
        {
            Log.e(TAG, "Unsupported HUAWEI_AVAILABLE_DUAL_PRIMARY  false");
        }
        try {
            if (Objects.equals(characteristics.get(CameraCharacteristicsEx.HUAWEI_PROFESSIONAL_MODE_SUPPORTED), Byte.valueOf((byte) 1)))
            {
                int[] shutterminmax = characteristics.get(CameraCharacteristicsEx.HUAWEI_SENSOR_EXPOSURETIME_RANGE);

                int min = shutterminmax[0];
                int max = shutterminmax[1];
                long maxs = SettingsManager.getInstance().getCamera2MaxExposureTime();
                if (SettingsManager.getInstance().getCamera2MaxExposureTime() > 0)
                    max = (int) SettingsManager.getInstance().getCamera2MaxExposureTime();
                if (SettingsManager.getInstance().getCamera2MinExposureTime() >0)
                    min = (int) SettingsManager.getInstance().getCamera2MinExposureTime();
                ArrayList<String> tmp = getShutterStrings(max,min,true);
                SettingsManager.get(SettingKeys.M_ExposureTime).setIsSupported(tmp.size() > 0);
                SettingsManager.get(SettingKeys.M_ExposureTime).setValues(tmp.toArray(new String[tmp.size()]));

                int[] isominmax = characteristics.get(CameraCharacteristicsEx.HUAWEI_SENSOR_ISO_RANGE);
                min = isominmax[0];
                max = isominmax[1];
                int maxiso = SettingsManager.getInstance().getCamera2MaxIso();
                if (maxiso > 0)
                    max = (int) SettingsManager.getInstance().getCamera2MaxIso();
                ArrayList<String> ar = getIsoStrings(max, min);
                SettingsManager.get(SettingKeys.M_ManualIso).setIsSupported(ar.size() > 0);
                SettingsManager.get(SettingKeys.M_ManualIso).setValues(ar.toArray(new String[ar.size()]));

                SettingsManager.get(SettingKeys.ExposureMode).setIsSupported(false);
                SettingsManager.getInstance().setFramework(Frameworks.HuaweiCamera2Ex);
            }
        }catch (IllegalArgumentException | NullPointerException ex)
        {
            Log.d(TAG, "No Huawei Pro mode");
        }

        try {
            int[] raw12 = characteristics.get(CameraCharacteristicsEx.HUAWEI_PROFESSIONAL_RAW12_SUPPORTED);
            if (raw12!= null)
                SettingsManager.get(SettingKeys.support12bitRaw).set(true);
            Log.d(TAG,"HUAWEI_PROFESSIONAL_RAW12_SUPPORTED");
        }
        catch (IllegalArgumentException ex)
        {
            Log.e(TAG,"HUAWEI_PROFESSIONAL_RAW12_SUPPORTED false");
        }

        try {
            byte rawsupported = characteristics.get(CameraCharacteristicsEx.HUAWEI_RAW_IMAGE_SUPPORTED);
            Log.d(TAG,"HUAWEI_RAW_IMAGE_SUPPORTED");
        }
        catch (NullPointerException | IllegalArgumentException ex)
        {
            Log.e(TAG,"HUAWEI_RAW_IMAGE_SUPPORTED false");
        }
        try {
            int[] deepmap = characteristics.get(CameraCharacteristicsEx.HUAWEI_AVAILABLE_DEPTH_STREAM_CONFIGURATIONS);
            Log.d(TAG,"HUAWEI_AVAILABLE_DEPTH_STREAM_CONFIGURATIONS");
        }
        catch (IllegalArgumentException ex)
        {
            Log.e(TAG,"HUAWEI_AVAILABLE_DEPTH_STREAM_CONFIGURATIONS false");
        }
        try {
            byte fastbinder = characteristics.get(CameraCharacteristicsEx.HUAWEI_FAST_NOTIFY_SUPPORTED);
            Log.d(TAG,"HUAWEI_FAST_NOTIFY_SUPPORTED");
        }
        catch (NullPointerException | IllegalArgumentException ex)
        {
            Log.e(TAG,"HUAWEI_FAST_NOTIFY_SUPPORTED false");
        }

        try {
            byte dualcamerareporcess = characteristics.get(CameraCharacteristicsEx.HUAWEI_DUAL_PRIMARY_SINGLE_REPROCESS);
            Log.d(TAG,"HUAWEI_DUAL_PRIMARY_SINGLE_REPROCESS");
        }
        catch (NullPointerException | IllegalArgumentException ex)
        {
            Log.e(TAG,"HUAWEI_DUAL_PRIMARY_SINGLE_REPROCESS false");
        }

        try {
            byte precapture = characteristics.get(CameraCharacteristicsEx.HUAWEI_PRE_CAPTURE_SUPPORTED);
            Log.d(TAG,"HUAWEI_PRE_CAPTURE_SUPPORTED");
        }
        catch (NullPointerException | IllegalArgumentException ex)
        {
            Log.e(TAG,"HUAWEI_PRE_CAPTURE_SUPPORTED false");
        }
        try {
            byte[] hdc = characteristics.get(CameraCharacteristicsEx.HUAWEI_HDC_CALIBRATE_DATA);
            Log.d(TAG,"HUAWEI_HDC_CALIBRATE_DATA");
        }
        catch (NullPointerException | IllegalArgumentException ex)
        {
            Log.e(TAG,"HUAWEI_HDC_CALIBRATE_DATA false");
        }
        try {
            int[] hdc = characteristics.get(CameraCharacteristicsEx.HUAWEI_SENCONDARY_SENSOR_PIXEL_ARRAY_SIZE);
            Log.d(TAG,"HUAWEI_SENCONDARY_SENSOR_PIXEL_ARRAY_SIZE");
        }
        catch (NullPointerException | IllegalArgumentException ex)
        {
            Log.e(TAG,"HUAWEI_SENCONDARY_SENSOR_PIXEL_ARRAY_SIZE false");
        }
        try {
            int[] hdc = characteristics.get(CameraCharacteristicsEx.HUAWEI_SENCONDARY_SENSOR_SUPPORTED_SIZE);
            Log.d(TAG,"HUAWEI_SENCONDARY_SENSOR_SUPPORTED_SIZE");
        }
        catch (NullPointerException | IllegalArgumentException ex)
        {
            Log.e(TAG,"HUAWEI_SENCONDARY_SENSOR_SUPPORTED_SIZE false");
        }
        try {
            int[] hdc = characteristics.get(CameraCharacteristicsEx.HUAWEI_MULTICAP);
            Log.d(TAG,"HUAWEI_MULTICAP");
        }
        catch (NullPointerException | IllegalArgumentException ex)
        {
            Log.e(TAG,"HUAWEI_MULTICAP false");
        }
        try {
            int[] hdc = characteristics.get(CameraCharacteristicsEx.HUAWEI_AVAILIBLE_DEPTH_SIZES);
            Log.d(TAG,"HUAWEI_AVAILIBLE_DEPTH_SIZES");
        }
        catch (NullPointerException | IllegalArgumentException ex)
        {
            Log.e(TAG,"HUAWEI_AVAILIBLE_DEPTH_SIZES false");
        }
    }

    private void detectColorcorrectionMode(CameraCharacteristics cameraCharacteristics)
    {
        int[] colorcor = null;
        if (cameraCharacteristics.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES) != null)
            colorcor = cameraCharacteristics.get(CameraCharacteristics.COLOR_CORRECTION_AVAILABLE_ABERRATION_MODES);
        else
            colorcor = new int[]{ 0,1,2};
        Log.d(TAG, "colormodes:" + Arrays.toString(colorcor));
        String[] lookupar = SettingsManager.getInstance().getResources().getStringArray(R.array.colorcorrectionmodes);

        HashMap<String,Integer> map = new HashMap<>();
        for (int i = 0;i< colorcor.length;i++)
        {
            if(i < lookupar.length && i < colorcor.length)
                map.put(lookupar[i],colorcor[i]);
        }
        lookupar = StringUtils.IntHashmapToStringArray(map);
        SettingsManager.get(SettingKeys.COLOR_CORRECTION_MODE).setValues(lookupar);
        SettingsManager.get(SettingKeys.COLOR_CORRECTION_MODE).setIsSupported(true);
        SettingsManager.get(SettingKeys.COLOR_CORRECTION_MODE).set(SettingsManager.getInstance().getResString(R.string.fast));
    }

    private void detectFlash(CameraCharacteristics characteristics) {
        if (SettingsManager.getInstance().hasCamera2Features()) {
            //flash mode
            boolean flashavail = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            SettingsManager.get(SettingKeys.FlashMode).setIsSupported(flashavail);
            if (SettingsManager.get(SettingKeys.FlashMode).isSupported()) {
                String[] lookupar = SettingsManager.getInstance().getResources().getStringArray(R.array.flashModes);
                HashMap<String,Integer> map = new HashMap<>();
                for (int i = 0; i< lookupar.length; i++)
                {
                    map.put(lookupar[i], i);
                }
                lookupar = StringUtils.IntHashmapToStringArray(map);
                SettingsManager.get(SettingKeys.FlashMode).setValues(lookupar);
            }
        }
    }

    private void detectControlMode(CameraCharacteristics characteristics) {
        if (SettingsManager.getInstance().hasCamera2Features()) {
            //flash mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                detectIntMode(characteristics,CameraCharacteristics.CONTROL_AVAILABLE_MODES, SettingsManager.get(SettingKeys.CONTROL_MODE),R.array.controlModes);
                return;
            }
            else {
                int device = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                String[] lookupar = SettingsManager.getInstance().getResources().getStringArray(R.array.controlModes);
                int[] full = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_MODES) != null)
                    full = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_MODES);
                else if (device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL || device==CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 || device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED) {
                    full = new int[] {0,1,2,};
                }
                else if (device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                    full = new int[] {1,2,};
                SettingsManager.get(SettingKeys.CONTROL_MODE).setIsSupported(true);
                if (SettingsManager.get(SettingKeys.CONTROL_MODE).isSupported()) {
                    HashMap<String, Integer> map = new HashMap<>();
                    for (int i = 0; i < full.length; i++) {
                        map.put(lookupar[i], full[i]);
                    }
                    lookupar = StringUtils.IntHashmapToStringArray(map);
                    SettingsManager.get(SettingKeys.CONTROL_MODE).setValues(lookupar);
                    SettingsManager.get(SettingKeys.CONTROL_MODE).set(SettingsManager.getInstance().getResString(R.string.auto));
                }
            }
        }
    }

    private void detectPictureFormats(CameraCharacteristics characteristics)
    {
        StreamConfigurationMap smap =  characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        HashMap<String, Integer> hmap = new HashMap<>();
        try {
            if (smap.isOutputSupportedFor(ImageFormat.RAW10))
                hmap.put(SettingsManager.getInstance().getResString(R.string.pictureformat_dng10), ImageFormat.RAW10);
        } catch (Exception e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.RAW_SENSOR)) {
                hmap.put(SettingsManager.getInstance().getResString(R.string.pictureformat_dng16), ImageFormat.RAW_SENSOR);
                hmap.put(SettingsManager.getInstance().getResString(R.string.pictureformat_bayer), ImageFormat.RAW_SENSOR);
            }
        } catch (Exception e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.RAW12))
                hmap.put(SettingsManager.getInstance().getResString(R.string.pictureformat_dng12), ImageFormat.RAW12);
        } catch (Exception e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.JPEG))
                hmap.put(SettingsManager.getInstance().getResString(R.string.pictureformat_jpeg), ImageFormat.JPEG);
        } catch (Exception e) {
            Log.WriteEx(e);
        }
        if (
                hmap.containsKey(SettingsManager.getInstance().getResString(R.string.pictureformat_jpeg)) &&
                (
                        hmap.containsKey(SettingsManager.getInstance().getResString(R.string.pictureformat_dng10))
                                || hmap.containsKey(SettingsManager.getInstance().getResString(R.string.pictureformat_dng16))))
            hmap.put(SettingsManager.getInstance().getResString(R.string.pictureformat_jpg_p_dng), ImageFormat.JPEG);

        try {
            if (smap.isOutputSupportedFor(ImageFormat.NV16))
                Log.d(TAG, "Support NV16");
        }
        catch (IllegalArgumentException ex)
        {
            Log.WriteEx(ex);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.NV21))
                Log.d(TAG, "Support NV21");
        } catch (IllegalArgumentException e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.YUV_420_888))
                    Log.d(TAG, "Support YUV_420_888");
        } catch (IllegalArgumentException e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.YUV_422_888))
                    Log.d(TAG, "Support YUV_422_888");
        } catch (IllegalArgumentException e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.YUV_444_888))
                    Log.d(TAG, "Support YUV_444_888");
        } catch (IllegalArgumentException e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.YV12))
                    Log.d(TAG, "Support YV12");
        } catch (IllegalArgumentException e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.DEPTH16))
                    Log.d(TAG, "Support DEPTH16");
        } catch (IllegalArgumentException e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.DEPTH_POINT_CLOUD))
                    Log.d(TAG, "Support DEPTH_POINT_CLOUD");
        } catch (IllegalArgumentException e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.RAW_PRIVATE))
                    Log.d(TAG, "Support RAW_PRIVATE");
        } catch (IllegalArgumentException e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.PRIVATE))
                    Log.d(TAG, "Support PRIVATE");
        } catch (IllegalArgumentException e) {
            Log.WriteEx(e);
        }

        SettingsManager.get(SettingKeys.PictureFormat).setIsSupported(true);
        SettingsManager.get(SettingKeys.PictureFormat).set(SettingsManager.getInstance().getResString(R.string.pictureformat_jpeg));
        SettingsManager.get(SettingKeys.PictureFormat).setValues(StringUtils.IntHashmapToStringArray(hmap));
    }

    private void detectPictureSizes(CameraCharacteristics characteristics)
    {
        StreamConfigurationMap smap =  characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] size = smap.getOutputSizes(ImageFormat.JPEG);
        java.util.Arrays.sort(size,new SizeComparer());
        String[] ar = new String[size.length];
        int i = 0;
        for (Size s : size)
        {
            ar[i++] = s.getWidth()+"x"+s.getHeight();
        }

        SettingsManager.get(SettingKeys.PictureSize).setIsSupported(true);
        SettingsManager.get(SettingKeys.PictureSize).set(ar[0]);
        SettingsManager.get(SettingKeys.PictureSize).setValues(ar);
    }

    private class SizeComparer implements Comparator<Size> {

        @Override
        public int compare(Size o1, Size o2) {

            if (o1.getWidth() > o2.getWidth())
                return 0;
            else if (o1.getWidth() == o2.getWidth()) {
                if (o1.getHeight() >= o2.getHeight())
                    return 0;
                else
                    return 1;
            }
            return 1;
        }
    }

    private void detectSceneModes(CameraCharacteristics characteristics){
        String[] lookupar = SettingsManager.getInstance().getResources().getStringArray(R.array.sceneModes);
        int[]  scenes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
        if (scenes.length > 1)
            SettingsManager.get(SettingKeys.SceneMode).setIsSupported(true);
        else
            SettingsManager.get(SettingKeys.SceneMode).setIsSupported(false);

        HashMap<String,Integer> map = new HashMap<>();
        for (int i = 0; i< scenes.length; i++)
        {
            switch (scenes[i])
            {
                case CameraCharacteristics.CONTROL_SCENE_MODE_DISABLED:
                    map.put(lookupar[0], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_FACE_PRIORITY:
                    map.put(lookupar[1], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_ACTION:
                    map.put(lookupar[2], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_PORTRAIT:
                    map.put(lookupar[3], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_LANDSCAPE:
                    map.put(lookupar[4], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_NIGHT:
                    map.put(lookupar[5], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_NIGHT_PORTRAIT:
                    map.put(lookupar[6], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_THEATRE:
                    map.put(lookupar[7], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_BEACH:
                    map.put(lookupar[8], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_SNOW:
                    map.put(lookupar[9], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_SUNSET:
                    map.put(lookupar[10], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_STEADYPHOTO:
                    map.put(lookupar[11], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_FIREWORKS:
                    map.put(lookupar[12], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_SPORTS:
                    map.put(lookupar[13], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_PARTY:
                    map.put(lookupar[14], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_CANDLELIGHT:
                    map.put(lookupar[15], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_BARCODE:
                    map.put(lookupar[16], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_HIGH_SPEED_VIDEO:
                    map.put(lookupar[17], scenes[i]);
                    break;
                case CameraCharacteristics.CONTROL_SCENE_MODE_HDR:
                    map.put(lookupar[18], scenes[i]);
                    break;
            }
        }
        lookupar = StringUtils.IntHashmapToStringArray(map);
        SettingsManager.get(SettingKeys.SceneMode).setValues(lookupar);
    }


    private void detectIntMode(CameraCharacteristics characteristics, CameraCharacteristics.Key<int[]> requestKey, SettingMode settingMode, int ressourceArray)
    {
        publishProgress("detectIntMode "+settingMode+" "+ressourceArray);
        if (SettingsManager.getInstance().hasCamera2Features() && characteristics.get(requestKey) != null) {
            int[]  scenes = characteristics.get(requestKey);
            if (scenes.length >0)
                settingMode.setIsSupported(true);
            else
                settingMode.setIsSupported(false);
            String[] lookupar = SettingsManager.getInstance().getResources().getStringArray(ressourceArray);
            HashMap<String,Integer> map = new HashMap<>();
            for (int i = 0; i< scenes.length; i++)
            {
                int t = scenes[i];
                if (t <lookupar.length)
                    map.put(lookupar[t], t);
                else
                    Log.d(TAG, "failed to get scene for int:" + t);
            }
            lookupar = StringUtils.IntHashmapToStringArray(map);
            settingMode.setValues(lookupar);

        }
    }

    private void detectByteMode(CameraCharacteristics characteristics, CameraCharacteristics.Key<byte[]> requestKey, SettingMode settingMode, int ressourceArray)
    {
        if (SettingsManager.getInstance().hasCamera2Features() && characteristics.get(requestKey) != null) {

            byte[] scenes = characteristics.get(requestKey);
            if (scenes == null)
                return;
            if (scenes.length >0)
                settingMode.setIsSupported(true);
            else
                return;
            String[] lookupar = SettingsManager.getInstance().getResources().getStringArray(ressourceArray);
            HashMap<String,Integer> map = new HashMap<>();
            for (int i = 0; i< scenes.length; i++)
            {
                map.put(lookupar[i], (int)scenes[i]);
            }
            lookupar = StringUtils.IntHashmapToStringArray(map);
            settingMode.setValues(lookupar);
        }
    }


    private void detectManualFocus(CameraCharacteristics cameraCharacteristics)
    {
        SettingMode mf = SettingsManager.get(SettingKeys.M_Focus);
        float maxfocusrange = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
        if (maxfocusrange == 0)
        {
            mf.setIsSupported(false);
            return;
        }
        float step = 0.001f;
        List<Float> floats = new ArrayList<>();
        for (float i = step; i < maxfocusrange; i += step)
        {
            floats.add(i);
            if (i > 0.01f)
                step = 0.02f;
            if (i > 0.1f)
                step = 0.1f;
            if (i > 1)
                step = 0.2f;
            if (i + step > maxfocusrange)
                floats.add(maxfocusrange);
        }

        StringFloatArray focusranges = new StringFloatArray(floats.size() + 2);
        focusranges.add(0, SettingsManager.getInstance().getResString(R.string.auto),0f);
        focusranges.add(1,"∞", 0.0001f); //10000m
        int t = 2;
        for (int i = 0; i < floats.size(); i++)
        {
            focusranges.add(t++,StringUtils.getMeterString(1/floats.get(i)),floats.get(i));
        }

        if (focusranges.getSize() > 0)
            mf.setIsSupported(true);
        else
            mf.setIsSupported(false);

        mf.setValues(focusranges.getStringArray());
    }

    private void detectManualExposure(CameraCharacteristics characteristics)
    {
        SettingMode exposure = SettingsManager.get(SettingKeys.M_ExposureCompensation);
        int max = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getUpper();
        int min = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getLower();
        float step = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP).floatValue();

        List<String> strings = new ArrayList<>();
        int t = 0;
        for (int i = min; i <= max; i++) {
            strings.add(String.format("%.1f", i * step));
        }
        if (strings.size() > 0)
            exposure.setIsSupported(true);
        else
            exposure.setIsSupported(false);
        exposure.setValues(strings.toArray(new String[strings.size()]));
    }

    private void detectManualexposureTime(CameraCharacteristics characteristics)
    {
        long max = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper() / 1000;
        long min = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower() / 1000;

        if (SettingsManager.getInstance().getCamera2MaxExposureTime() >0)
            max = SettingsManager.getInstance().getCamera2MaxExposureTime();
        if (SettingsManager.getInstance().getCamera2MinExposureTime() >0)
            min = SettingsManager.getInstance().getCamera2MinExposureTime();

        ArrayList<String> tmp = getShutterStrings(max, min,false);
        SettingsManager.get(SettingKeys.M_ExposureTime).setIsSupported(tmp.size() > 0);
        SettingsManager.get(SettingKeys.M_ExposureTime).setValues(tmp.toArray(new String[tmp.size()]));

    }

    @NonNull
    private ArrayList<String> getShutterStrings(long max, long min,boolean withAutoMode) {
        String[] allvalues = SettingsManager.getInstance().getResources().getStringArray(R.array.shutter_values_autocreate);
        boolean foundmin = false;
        boolean foundmax = false;

        ArrayList<String> tmp = new ArrayList<>();
        if (withAutoMode)
            tmp.add(SettingsManager.getInstance().getResString(R.string.auto_));
        for (int i = 1; i< allvalues.length; i++ )
        {
            String s = allvalues[i];

            float a;
            if (s.contains("/")) {
                String[] split = s.split("/");
                a = Float.parseFloat(split[0]) / Float.parseFloat(split[1])*1000000f;
            }
            else
                a = Float.parseFloat(s)*1000000f;

            if (a>= min && a <= max)
                tmp.add(s);
            if (a >= min && !foundmin)
            {
                foundmin = true;
            }
            if (a > max && !foundmax)
            {
                foundmax = true;
            }
            if (foundmax && foundmin)
                break;
        }
        return tmp;
    }

    private void detectManualIso(CameraCharacteristics characteristics)
    {
        int max  = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper();
        if (SettingsManager.getInstance().getCamera2MaxIso() >0)
            max = SettingsManager.getInstance().getCamera2MaxIso();

        int min = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getLower();
        ArrayList<String> ar = getIsoStrings(max, min);
        SettingsManager.get(SettingKeys.M_ManualIso).setIsSupported(ar.size() > 0);
        SettingsManager.get(SettingKeys.M_ManualIso).setValues(ar.toArray(new String[ar.size()]));
    }

    @NonNull
    private ArrayList<String> getIsoStrings(int max, int min) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(SettingsManager.getInstance().getResString(R.string.auto_));
        for (int i = min; i <= max; i += 50) {
            //double isostep when its bigger then 3200
            if(i > 3200)
            {
                int next = (i-50) *2;
                if (next > max)
                    next = max;
                i =next;
            }
            ar.add(i + "");
        }
        return ar;
    }

    private void detectVideoMediaProfiles(int cameraid)
    {
        HashMap<String,VideoMediaProfile> supportedProfiles = getDefaultVideoMediaProfiles(cameraid);

        if (supportedProfiles.get("2160p") == null && has2160pSize()) {
            supportedProfiles.put("2160p", new VideoMediaProfile("156000 2 3 48000 30 2 10007 48000000 2 30 2160 3840 2160p Normal true"));
            supportedProfiles.put("2160p_Timelapse",new VideoMediaProfile("156000 2 3 48000 30 2 10007 48000000 2 30 2160 3840 2160p_TimeLapse Timelapse true"));
        }
        SettingsManager.getInstance().saveMediaProfiles(supportedProfiles);

        publishProgress("VideoMediaProfiles:" + getStringFromArray(supportedProfiles.keySet().toArray(new String[supportedProfiles.size()])));
    }

    private boolean has2160pSize()
    {
        String[] size = SettingsManager.get(SettingKeys.PictureSize).getValues();
        for (String s: size) {
            if (s.matches("3840x2160"))
                return true;
        }
        return false;
    }
}
