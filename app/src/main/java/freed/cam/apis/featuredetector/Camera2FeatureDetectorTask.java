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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import freed.settings.AppSettingsManager;
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
        AppSettingsManager.getInstance().setCamApi(AppSettingsManager.API_2);
        try {
            publishProgress("Check Camera2");
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String cameras[] = manager.getCameraIdList();

            for (String s : cameras)
            {

                publishProgress("###################");
                publishProgress("#####CameraID:"+s+"####");
                publishProgress("###################");
                publishProgress("Check camera features:" + s);
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(s);
                boolean front = characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
                AppSettingsManager.getInstance().modules.set(AppSettingsManager.getInstance().getResString(R.string.module_picture));
                AppSettingsManager.getInstance().SetCurrentCamera(Integer.parseInt(s));
                AppSettingsManager.getInstance().setIsFrontCamera(front);
                int hwlvl = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);

                AppSettingsManager.getInstance().selfTimer.setValues(AppSettingsManager.getInstance().getResources().getStringArray(R.array.selftimervalues));
                AppSettingsManager.getInstance().selfTimer.set(AppSettingsManager.getInstance().selfTimer.getValues()[0]);

                publishProgress("Camera 2 Level:" + hwlvl);

                //check first if a already checked cam have camera2features and if its now the front cam that dont have a camera2feature.
                //in that case set it to true
                //else it would override the already detected featureset from last cam and disable api2
                if (AppSettingsManager.getInstance().hasCamera2Features() && front && hwlvl == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                    hasCamera2Features = true;
                    Log.d(TAG,"Front cam has no camera2 featureset, try to find supported things anyway");
                }
                else if (hwlvl != CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                    hasCamera2Features = true;
                else
                    hasCamera2Features = false;

                AppSettingsManager.getInstance().setHasCamera2Features(hasCamera2Features);
                publishProgress("IsCamera2 Full Device:" + AppSettingsManager.getInstance().hasCamera2Features() + " isFront:" +AppSettingsManager.getInstance().getIsFrontCamera());

                AppSettingsManager.getInstance().guide.setValues(AppSettingsManager.getInstance().getResources().getStringArray(R.array.guidelist));
                AppSettingsManager.getInstance().guide.set(AppSettingsManager.getInstance().guide.getValues()[0]);


                if (hasCamera2Features) {

                    try {
                        if(!AppSettingsManager.getInstance().getIsFrontCamera()) {
                            publishProgress("Detect Flash");
                            detectFlash(characteristics);
                            sendProgress(AppSettingsManager.getInstance().flashMode, "Flash");
                        }
                    } catch (Exception e){
                            Log.WriteEx(e);
                            publishProgress("Detect Flash failed");
                        }

                    try {
                        publishProgress("Detect Scene");
                        detectSceneModes(characteristics);
                        sendProgress(AppSettingsManager.getInstance().sceneMode, "Scene");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Scene failed");
                    }

                    try {
                        publishProgress("Detect Antibanding");
                        detectIntMode(characteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES, AppSettingsManager.getInstance().antiBandingMode, R.array.antibandingmodes);
                        sendProgress(AppSettingsManager.getInstance().antiBandingMode, "Antibanding");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Antibanding failed");
                    }

                    try {
                        publishProgress("Detect Color");
                        detectIntMode(characteristics, CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS, AppSettingsManager.getInstance().colorMode, R.array.colormodes);
                        sendProgress(AppSettingsManager.getInstance().colorMode, "Color");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Color failed");
                    }

                    try {
                        publishProgress("Detect EdgeMode");
                        detectIntMode(characteristics, CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES, AppSettingsManager.getInstance().edgeMode, R.array.edgeModes);
                        sendProgress(AppSettingsManager.getInstance().edgeMode, "EdgeMode");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Edge failed");
                    }


                    try {
                        publishProgress("Detect OpticalImageStabilisationMode");
                        detectIntMode(characteristics, CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION, AppSettingsManager.getInstance().opticalImageStabilisation, R.array.digitalImageStabModes);
                        sendProgress(AppSettingsManager.getInstance().opticalImageStabilisation, "OpticalImageStabilisationMode");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Ois failed");
                    }

                    try {
                        publishProgress("Detect FocusMode");
                        detectIntMode(characteristics, CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES, AppSettingsManager.getInstance().focusMode, R.array.focusModes);
                        sendProgress(AppSettingsManager.getInstance().focusMode, "FocusMode");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Focus failed");
                    }


                    try {
                        publishProgress("Detect HotPixelMode");
                        detectIntMode(characteristics, CameraCharacteristics.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES, AppSettingsManager.getInstance().hotpixelMode, R.array.hotpixelmodes);
                        sendProgress(AppSettingsManager.getInstance().hotpixelMode, "HotPixelMode");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect HotPixel failed");
                    }

                    try {
                        publishProgress("Detect Denoise");
                        detectIntMode(characteristics, CameraCharacteristics.NOISE_REDUCTION_AVAILABLE_NOISE_REDUCTION_MODES, AppSettingsManager.getInstance().denoiseMode, R.array.denoiseModes);
                        sendProgress(AppSettingsManager.getInstance().denoiseMode, "Denoise");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Denoise failed");
                    }

                    try {
                        publishProgress("Detect PictureFormat");
                        detectPictureFormats(characteristics);
                        sendProgress(AppSettingsManager.getInstance().pictureFormat, "PictureFormat");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect PictureFormat failed");
                    }

                    try {
                        publishProgress("Detect Manual Focus");
                        detectManualFocus(characteristics);
                        sendProgress(AppSettingsManager.getInstance().manualFocus, "Manual Focus");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect MF failed");
                    }

                    try {
                        publishProgress("Detect PictureSizes");
                        detectPictureSizes(characteristics);
                        sendProgress(AppSettingsManager.getInstance().pictureSize, "PictureSizes:");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect PictureSize failed");
                    }

                    try {
                        publishProgress("Detect Video Profiles");
                        detectVideoMediaProfiles(AppSettingsManager.getInstance().GetCurrentCamera());
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Video Profiles failed");
                    }

                    try {
                        publishProgress("Detect ExposureModes");
                        detectIntMode(characteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES, AppSettingsManager.getInstance().exposureMode, R.array.aemodes);
                        sendProgress(AppSettingsManager.getInstance().exposureMode, "ExposureModes:");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect ExposureModes failed");
                    }

                    try {
                        publishProgress("Detect ExposureCompensation");
                        detectManualExposure(characteristics);
                        sendProgress(AppSettingsManager.getInstance().manualExposureCompensation, "ExposureCompensation:");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect ExpoCompensation failed");
                    }

                    try {
                        publishProgress("Detect ExposureTime");
                        detectManualexposureTime(characteristics);
                        sendProgress(AppSettingsManager.getInstance().manualExposureTime, "ExposureTime:");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect ExpoTime failed");
                    }

                    try {
                        publishProgress("Detect Iso");
                        detectManualIso(characteristics);
                        sendProgress(AppSettingsManager.getInstance().manualIso, "Iso:");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect Iso failed");
                    }

                    try {
                        publishProgress("Detect ColorCorrection");
                        detectColorcorrectionMode(characteristics);
                        sendProgress(AppSettingsManager.getInstance().colorCorrectionMode, "ColorCorrection");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect ColorCorrection failed");
                    }

                    try {
                        publishProgress("Detect ToneMap");
                        detectIntMode(characteristics, CameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES, AppSettingsManager.getInstance().toneMapMode,R.array.tonemapmodes);
                        sendProgress(AppSettingsManager.getInstance().toneMapMode, "Tonemap");
                    }
                    catch (Exception ex)
                    {
                        Log.WriteEx(ex);
                        publishProgress("Detect Tonemap Mode failed");
                    }

                    try {
                        publishProgress("Detect Whitebalance");
                        detectIntMode(characteristics, CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES, AppSettingsManager.getInstance().whiteBalanceMode, R.array.whitebalancemodes);
                        sendProgress(AppSettingsManager.getInstance().whiteBalanceMode, "Whitebalance");
                    } catch (Exception e) {
                        Log.WriteEx(e);
                        publishProgress("Detect WB Mode failed");
                    }

                    try {
                        publishProgress("Detect ControlMode");
                        detectControlMode(characteristics);
                        sendProgress(AppSettingsManager.getInstance().controlMode, "ControlMode");
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
                        AppSettingsManager.getInstance().ae_TagetFPS.setValues(t);
                        AppSettingsManager.getInstance().ae_TagetFPS.setIsSupported(true);
                    }

                    detectHuaweiParameters(characteristics);

                }
            }
            AppSettingsManager.getInstance().SetCurrentCamera(0);
            if (!hasCamera2Features)
                AppSettingsManager.getInstance().setCamApi(AppSettingsManager.API_1);

            if (AppSettingsManager.getInstance().hasCamera2Features() && !AppSettingsManager.getInstance().opencamera1Legacy.isPresetted()) {
                AppSettingsManager.getInstance().opencamera1Legacy.setBoolean(true);
            }
            Log.d(TAG, "Can Open Legacy: " + AppSettingsManager.getInstance().opencamera1Legacy.getBoolean() + " was presetted: " + AppSettingsManager.getInstance().opencamera1Legacy.isPresetted());
        }
        catch (Throwable ex) {
            Log.WriteEx(ex);
            if (!hasCamera2Features)
                AppSettingsManager.getInstance().setHasCamera2Features(false);
            else
                AppSettingsManager.getInstance().setHasCamera2Features(true);

            AppSettingsManager.getInstance().setCamApi(AppSettingsManager.API_1);
        }
    }

    private void detectHuaweiParameters(CameraCharacteristics characteristics) {
        try {
            detectByteMode(characteristics, CameraCharacteristicsEx.HUAWEI_AVAILABLE_DUAL_PRIMARY, AppSettingsManager.getInstance().dualPrimaryCameraMode, R.array.dual_camera_mode);
        }
        catch (IllegalArgumentException ex)
        {
            Log.e(TAG, "Unsupported HUAWEI_AVAILABLE_DUAL_PRIMARY  false");
        }
        try {
            if (characteristics.get(CameraCharacteristicsEx.HUAWEI_PROFESSIONAL_MODE_SUPPORTED) == Byte.valueOf((byte)1))
            {
                int[] shutterminmax = characteristics.get(CameraCharacteristicsEx.HUAWEI_SENSOR_EXPOSURETIME_RANGE);

                int min = shutterminmax[0];
                int max = shutterminmax[1];
                long maxs = AppSettingsManager.getInstance().getCamera2MaxExposureTime();
                if (AppSettingsManager.getInstance().getCamera2MaxExposureTime() > 0)
                    max = (int)AppSettingsManager.getInstance().getCamera2MaxExposureTime();
                if (AppSettingsManager.getInstance().getCamera2MinExposureTime() >0)
                    min = (int)AppSettingsManager.getInstance().getCamera2MinExposureTime();
                ArrayList<String> tmp = getShutterStrings(max,min,true);
                AppSettingsManager.getInstance().manualExposureTime.setIsSupported(tmp.size() > 0);
                AppSettingsManager.getInstance().manualExposureTime.setValues(tmp.toArray(new String[tmp.size()]));

                int[] isominmax = characteristics.get(CameraCharacteristicsEx.HUAWEI_SENSOR_ISO_RANGE);
                min = isominmax[0];
                max = isominmax[1];
                int maxiso = AppSettingsManager.getInstance().getCamera2MaxIso();
                if (maxiso > 0)
                    max = (int)AppSettingsManager.getInstance().getCamera2MaxIso();
                ArrayList<String> ar = getIsoStrings(max, min);
                AppSettingsManager.getInstance().manualIso.setIsSupported(ar.size() > 0);
                AppSettingsManager.getInstance().manualIso.setValues(ar.toArray(new String[ar.size()]));

                AppSettingsManager.getInstance().exposureMode.setIsSupported(false);
                AppSettingsManager.getInstance().useHuaweiCam2Extension.setBoolean(true);
            }
        }catch (IllegalArgumentException | NullPointerException ex)
        {
            Log.d(TAG, "No Huawei Pro mode");
        }

        try {
            int[] raw12 = characteristics.get(CameraCharacteristicsEx.HUAWEI_PROFESSIONAL_RAW12_SUPPORTED);
            if (raw12!= null)
                AppSettingsManager.getInstance().support12bitRaw.setBoolean(true);
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
        Log.d(TAG, "colormodes:" + colorcor.toString());
        String[] lookupar = AppSettingsManager.getInstance().getResources().getStringArray(R.array.colorcorrectionmodes);

        HashMap<String,Integer> map = new HashMap<>();
        for (int i = 0;i< colorcor.length;i++)
        {
            if(i < lookupar.length && i < colorcor.length)
                map.put(lookupar[i],colorcor[i]);
        }
        lookupar = StringUtils.IntHashmapToStringArray(map);
        AppSettingsManager.getInstance().colorCorrectionMode.setValues(lookupar);
        AppSettingsManager.getInstance().colorCorrectionMode.setIsSupported(true);
        AppSettingsManager.getInstance().colorCorrectionMode.set(AppSettingsManager.getInstance().getResString(R.string.fast));
    }

    private void detectFlash(CameraCharacteristics characteristics) {
        if (AppSettingsManager.getInstance().hasCamera2Features()) {
            //flash mode
            boolean flashavail = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            AppSettingsManager.getInstance().flashMode.setIsSupported(flashavail);
            if (AppSettingsManager.getInstance().flashMode.isSupported()) {
                String[] lookupar = AppSettingsManager.getInstance().getResources().getStringArray(R.array.flashModes);
                HashMap<String,Integer> map = new HashMap<>();
                for (int i = 0; i< lookupar.length; i++)
                {
                    map.put(lookupar[i], i);
                }
                lookupar = StringUtils.IntHashmapToStringArray(map);
                AppSettingsManager.getInstance().flashMode.setValues(lookupar);
            }
        }
    }

    private void detectControlMode(CameraCharacteristics characteristics) {
        if (AppSettingsManager.getInstance().hasCamera2Features()) {
            //flash mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                detectIntMode(characteristics,CameraCharacteristics.CONTROL_AVAILABLE_MODES,AppSettingsManager.getInstance().controlMode,R.array.controlModes);
                return;
            }
            else {
                int device = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                String[] lookupar = AppSettingsManager.getInstance().getResources().getStringArray(R.array.controlModes);
                int[] full = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_MODES) != null)
                    full = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_MODES);
                else if (device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL || device==CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 || device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED) {
                    full = new int[] {0,1,2,};
                }
                else if (device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                    full = new int[] {1,2,};
                AppSettingsManager.getInstance().controlMode.setIsSupported(true);
                if (AppSettingsManager.getInstance().controlMode.isSupported()) {
                    HashMap<String, Integer> map = new HashMap<>();
                    for (int i = 0; i < full.length; i++) {
                        map.put(lookupar[i], full[i]);
                    }
                    lookupar = StringUtils.IntHashmapToStringArray(map);
                    AppSettingsManager.getInstance().controlMode.setValues(lookupar);
                    AppSettingsManager.getInstance().controlMode.set(AppSettingsManager.getInstance().getResString(R.string.auto));
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
                hmap.put(AppSettingsManager.getInstance().getResString(R.string.pictureformat_dng10), ImageFormat.RAW10);
        } catch (Exception e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.RAW_SENSOR))
                hmap.put(AppSettingsManager.getInstance().getResString(R.string.pictureformat_dng16), ImageFormat.RAW_SENSOR);
        } catch (Exception e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.RAW12))
                hmap.put(AppSettingsManager.getInstance().getResString(R.string.pictureformat_dng12), ImageFormat.RAW12);
        } catch (Exception e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.JPEG))
                hmap.put(AppSettingsManager.getInstance().getResString(R.string.pictureformat_jpeg), ImageFormat.JPEG);
        } catch (Exception e) {
            Log.WriteEx(e);
        }
        if (
                hmap.containsKey(AppSettingsManager.getInstance().getResString(R.string.pictureformat_jpeg)) &&
                (
                        hmap.containsKey(AppSettingsManager.getInstance().getResString(R.string.pictureformat_dng10))
                                || hmap.containsKey(AppSettingsManager.getInstance().getResString(R.string.pictureformat_dng16))))
            hmap.put(AppSettingsManager.getInstance().getResString(R.string.pictureformat_jpg_p_dng), ImageFormat.JPEG);

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

        AppSettingsManager.getInstance().pictureFormat.setIsSupported(true);
        AppSettingsManager.getInstance().pictureFormat.set(AppSettingsManager.getInstance().getResString(R.string.pictureformat_jpeg));
        AppSettingsManager.getInstance().pictureFormat.setValues(StringUtils.IntHashmapToStringArray(hmap));
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

        AppSettingsManager.getInstance().pictureSize.setIsSupported(true);
        AppSettingsManager.getInstance().pictureSize.set(ar[0]);
        AppSettingsManager.getInstance().pictureSize.setValues(ar);
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
        String[] lookupar = AppSettingsManager.getInstance().getResources().getStringArray(R.array.sceneModes);
        int[]  scenes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
        if (scenes.length > 1)
            AppSettingsManager.getInstance().sceneMode.setIsSupported(true);
        else
            return;

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
        AppSettingsManager.getInstance().sceneMode.setValues(lookupar);
    }


    private void detectIntMode(CameraCharacteristics characteristics, CameraCharacteristics.Key<int[]> requestKey, AppSettingsManager.SettingMode settingMode, int ressourceArray)
    {
        publishProgress("detectIntMode "+settingMode+" "+ressourceArray);
        if (AppSettingsManager.getInstance().hasCamera2Features() && characteristics.get(requestKey) != null) {
            int[]  scenes = characteristics.get(requestKey);
            if (scenes.length >0)
                settingMode.setIsSupported(true);
            else
                return;
            String[] lookupar = AppSettingsManager.getInstance().getResources().getStringArray(ressourceArray);
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

    private void detectByteMode(CameraCharacteristics characteristics, CameraCharacteristics.Key<byte[]> requestKey, AppSettingsManager.SettingMode settingMode, int ressourceArray)
    {
        if (AppSettingsManager.getInstance().hasCamera2Features() && characteristics.get(requestKey) != null) {

            byte[] scenes = characteristics.get(requestKey);
            if (scenes == null)
                return;
            if (scenes.length >0)
                settingMode.setIsSupported(true);
            else
                return;
            String[] lookupar = AppSettingsManager.getInstance().getResources().getStringArray(ressourceArray);
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
        AppSettingsManager.SettingMode mf = AppSettingsManager.getInstance().manualFocus;
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
        focusranges.add(0,AppSettingsManager.getInstance().getResString(R.string.auto),0f);
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
        AppSettingsManager.SettingMode exposure = AppSettingsManager.getInstance().manualExposureCompensation;
        int max = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getUpper();
        int min = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getLower();
        float step = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP).floatValue();

        StringFloatArray ranges = new StringFloatArray((max*2)+1);
        int t = 0;
        for (int i = min; i <= max; i++) {
            String s = String.format("%.1f", i * step);
            ranges.add(t++,s,i);
        }
        if (ranges.getSize() > 0)
            exposure.setIsSupported(true);
        else
            exposure.setIsSupported(false);
        exposure.setValues(ranges.getStringArray());
    }

    private void detectManualexposureTime(CameraCharacteristics characteristics)
    {
        long max = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper() / 1000;
        long min = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower() / 1000;

        if (AppSettingsManager.getInstance().getCamera2MaxExposureTime() >0)
            max = AppSettingsManager.getInstance().getCamera2MaxExposureTime();
        if (AppSettingsManager.getInstance().getCamera2MinExposureTime() >0)
            min = AppSettingsManager.getInstance().getCamera2MinExposureTime();

        ArrayList<String> tmp = getShutterStrings(max, min,false);
        AppSettingsManager.getInstance().manualExposureTime.setIsSupported(tmp.size() > 0);
        AppSettingsManager.getInstance().manualExposureTime.setValues(tmp.toArray(new String[tmp.size()]));

    }

    @NonNull
    private ArrayList<String> getShutterStrings(long max, long min,boolean withAutoMode) {
        String[] allvalues = AppSettingsManager.getInstance().getResources().getStringArray(R.array.shutter_values_autocreate);
        boolean foundmin = false;
        boolean foundmax = false;

        ArrayList<String> tmp = new ArrayList<>();
        if (withAutoMode)
            tmp.add(AppSettingsManager.getInstance().getResString(R.string.auto_));
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
        if (AppSettingsManager.getInstance().getCamera2MaxIso() >0)
            max = AppSettingsManager.getInstance().getCamera2MaxIso();

        int min = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getLower();
        ArrayList<String> ar = getIsoStrings(max, min);
        AppSettingsManager.getInstance().manualIso.setIsSupported(ar.size() > 0);
        AppSettingsManager.getInstance().manualIso.setValues(ar.toArray(new String[ar.size()]));
    }

    @NonNull
    private ArrayList<String> getIsoStrings(int max, int min) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(AppSettingsManager.getInstance().getResString(R.string.auto_));
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
        AppSettingsManager.getInstance().saveMediaProfiles(supportedProfiles);

        publishProgress("VideoMediaProfiles:" + getStringFromArray(supportedProfiles.keySet().toArray(new String[supportedProfiles.size()])));
    }

    private boolean has2160pSize()
    {
        String[] size = AppSettingsManager.getInstance().pictureSize.getValues();
        for (String s: size) {
            if (s.matches("3840x2160"))
                return true;
        }
        return false;
    }
}
