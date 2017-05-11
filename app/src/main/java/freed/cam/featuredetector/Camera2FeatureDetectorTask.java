package freed.cam.featuredetector;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Size;

import com.huawei.camera2ex.CameraCharacteristicsEx;
import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.HashMap;

import freed.cam.apis.basecamera.modules.VideoMediaProfile;
import freed.utils.AppSettingsManager;
import freed.utils.Log;
import freed.utils.StringFloatArray;
import freed.utils.StringUtils;


/**
 * Created by troop on 23.01.2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2FeatureDetectorTask extends AbstractFeatureDetectorTask {

    private final String TAG = Camera2FeatureDetectorTask.class.getSimpleName();
    private Context context;
    boolean fulldevice;
    public Camera2FeatureDetectorTask(ProgressUpdate progressUpdate, AppSettingsManager appSettingsManager, Context context) {
        super(progressUpdate, appSettingsManager);
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params)
    {
        publishProgress("###################");
        publishProgress("#######Camera2#####");
        publishProgress("###################");
        appSettingsManager.setCamApi(AppSettingsManager.API_2);
        try {
            publishProgress("Check Camera2");
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            String cameras[] = manager.getCameraIdList();

            for (String s : cameras)
            {
                appSettingsManager.modules.set(appSettingsManager.getResString(R.string.module_picture));
                appSettingsManager.SetCurrentCamera(Integer.parseInt(s));
                publishProgress("###################");
                publishProgress("#####CameraID:"+s+"####");
                publishProgress("###################");
                publishProgress("Check camera features:" + s);
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(s);
                boolean front = characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;
                appSettingsManager.setIsFrontCamera(front);
                int hwlvl = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);

                publishProgress("Camera 2 Level:" + hwlvl);

                switch (hwlvl)
                {
                    case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3:
                        fulldevice = true;

                    case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL:
                        fulldevice = true;
                        break;
                    case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED:
                        fulldevice = true;
                        break;
                    case CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY:
                        fulldevice = false;
                        break;

                }

                appSettingsManager.SetCamera2FullSupported(fulldevice);
                publishProgress("IsCamera2 Full Device:" + appSettingsManager.IsCamera2FullSupported() + " isFront:" +appSettingsManager.getIsFrontCamera());

                appSettingsManager.guide.setValues(appSettingsManager.getResources().getStringArray(R.array.guidelist));
                appSettingsManager.guide.set(appSettingsManager.guide.getValues()[0]);


                if (fulldevice) {
try {


    publishProgress("Detect Flash");
    detectFlash(characteristics);
    sendProgress(appSettingsManager.flashMode, "Flash");

    publishProgress("Detect Scene");
    detectIntMode(characteristics, CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES, appSettingsManager.sceneMode, R.array.sceneModes);
    sendProgress(appSettingsManager.sceneMode, "Scene");

    publishProgress("Detect Antibanding");
    detectIntMode(characteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES, appSettingsManager.antiBandingMode, R.array.antibandingmodes);
    sendProgress(appSettingsManager.antiBandingMode, "Antibanding");

    publishProgress("Detect Color");
    detectIntMode(characteristics, CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS, appSettingsManager.colorMode, R.array.colormodes);
    sendProgress(appSettingsManager.colorMode, "Color");

try {
    publishProgress("Detect EdgeMode");
    detectIntMode(characteristics, CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES, appSettingsManager.edgeMode, R.array.edgeModes);
    sendProgress(appSettingsManager.edgeMode, "EdgeMode");
}
catch (Exception err)
{
    publishProgress("Detect OIS" + err.getMessage());
}


    try {
        publishProgress("Detect OpticalImageStabilisationMode");
        detectIntMode(characteristics, CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION, appSettingsManager.opticalImageStabilisation, R.array.digitalImageStabModes);
        sendProgress(appSettingsManager.opticalImageStabilisation, "OpticalImageStabilisationMode");

    }
    catch (Exception err)
    {
        publishProgress("Detect OIS" + err.getMessage());
    }
    publishProgress("Detect FocusMode");
    detectIntMode(characteristics, CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES, appSettingsManager.focusMode, R.array.focusModes);
    sendProgress(appSettingsManager.focusMode, "FocusMode");


    publishProgress("Detect HotPixelMode");
    detectIntMode(characteristics, CameraCharacteristics.HOT_PIXEL_AVAILABLE_HOT_PIXEL_MODES, appSettingsManager.hotpixelMode, R.array.hotpixelmodes);
    sendProgress(appSettingsManager.hotpixelMode, "HotPixelMode");

    publishProgress("Detect Denoise");
    detectIntMode(characteristics, CameraCharacteristics.NOISE_REDUCTION_AVAILABLE_NOISE_REDUCTION_MODES, appSettingsManager.denoiseMode, R.array.denoiseModes);
    sendProgress(appSettingsManager.denoiseMode, "Denoise");

    publishProgress("Detect PictureFormat");
    detectPictureFormats(characteristics);
    sendProgress(appSettingsManager.pictureFormat, "PictureFormat");

    publishProgress("Detect Manual Focus");
    detectManualFocus(characteristics);
    sendProgress(appSettingsManager.manualFocus, "Manual Focus");

    publishProgress("Detect PictureSizes");
    detectPictureSizes(characteristics);
    sendProgress(appSettingsManager.pictureSize, "PictureSizes:");

    publishProgress("Detect Curr Cam");
    detectVideoMediaProfiles(appSettingsManager.GetCurrentCamera());

    publishProgress("Detect ExposureModes");
    detectIntMode(characteristics, CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES, appSettingsManager.exposureMode, R.array.aemodes);
    sendProgress(appSettingsManager.exposureMode, "ExposureModes:");

    publishProgress("Detect ExposureCompensation");
    detectManualExposure(characteristics);
    sendProgress(appSettingsManager.manualExposureCompensation, "ExposureCompensation:");

    publishProgress("Detect ExposureTime");
    detectManualexposureTime(characteristics);
    sendProgress(appSettingsManager.manualExposureTime, "ExposureTime:");

    publishProgress("Detect Iso");
    detectManualIso(characteristics);
    sendProgress(appSettingsManager.manualIso, "Iso:");

    publishProgress("Detect ColorCorrection");
    detectColorcorrectionMode(characteristics);
    sendProgress(appSettingsManager.colorCorrectionMode, "ColorCorrection");

    publishProgress("Detect Whitebalance");
    detectIntMode(characteristics, CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES, appSettingsManager.whiteBalanceMode, R.array.whitebalancemodes);
    sendProgress(appSettingsManager.whiteBalanceMode, "Whitebalance");

    publishProgress("Detect ControlMode");
    detectControlMode(characteristics);
    sendProgress(appSettingsManager.controlMode, "ControlMode");


                    try {
                        detectByteMode(characteristics, CameraCharacteristicsEx.HUAWEI_AVAILABLE_DUAL_PRIMARY, appSettingsManager.dualPrimaryCameraMode, R.array.dual_camera_mode);
                    }
                    catch (IllegalArgumentException ex)
                    {
                        Log.e(TAG, "Unsupported HUAWEI_AVAILABLE_DUAL_PRIMARY  false");
                    }
                    try {
                        int[] raw12 = characteristics.get(CameraCharacteristicsEx.HUAWEI_PROFESSIONAL_RAW12_SUPPORTED);
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
                    }try {
                        int[] hdc = characteristics.get(CameraCharacteristicsEx.HUAWEI_AVAILIBLE_DEPTH_SIZES);
                        Log.d(TAG,"HUAWEI_AVAILIBLE_DEPTH_SIZES");
                    }
                    catch (NullPointerException | IllegalArgumentException ex)
                    {
                        Log.e(TAG,"HUAWEI_AVAILIBLE_DEPTH_SIZES false");
                    }
}
catch (IllegalArgumentException ex)
{
    Log.e(TAG, ex+"");
}

                }
            }
            appSettingsManager.SetCurrentCamera(0);
            if (!fulldevice)
                appSettingsManager.setCamApi(AppSettingsManager.API_1);

            if (appSettingsManager.IsCamera2FullSupported() && !appSettingsManager.opencamera1Legacy.isPresetted()) {
                appSettingsManager.opencamera1Legacy.setBoolean(true);
            }
            Log.d(TAG, "Can Open Legacy: " + appSettingsManager.opencamera1Legacy.getBoolean() + " was presetted: " + appSettingsManager.opencamera1Legacy.isPresetted());
        }
        catch (Throwable ex) {
            Log.WriteEx(ex);
            if (!fulldevice)
            appSettingsManager.SetCamera2FullSupported(false);
            else
                appSettingsManager.SetCamera2FullSupported(true);

            appSettingsManager.setCamApi(AppSettingsManager.API_1);
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        appSettingsManager.SetCurrentCamera(0);
        //startFreedcam();
    }

    private void detectColorcorrectionMode(CameraCharacteristics cameraCharacteristics)
    {
        String[] lookupar = appSettingsManager.getResources().getStringArray(R.array.colorcorrectionmodes);
        HashMap<String,Integer> map = new HashMap<>();
        map.put(lookupar[0],0);
        map.put(lookupar[1],1);
        map.put(lookupar[2],2);
        lookupar = StringUtils.IntHashmapToStringArray(map);
        appSettingsManager.colorCorrectionMode.setValues(lookupar);
        appSettingsManager.colorCorrectionMode.setIsSupported(true);
    }

    private void detectFlash(CameraCharacteristics characteristics) {
        if (appSettingsManager.IsCamera2FullSupported()) {
            //flash mode
            boolean flashavail = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            appSettingsManager.flashMode.setIsSupported(flashavail);
            if (appSettingsManager.flashMode.isSupported()) {
                String[] lookupar = appSettingsManager.getResources().getStringArray(R.array.flashModes);
                HashMap<String,Integer> map = new HashMap<>();
                for (int i = 0; i< lookupar.length; i++)
                {
                    map.put(lookupar[i], i);
                }
                lookupar = StringUtils.IntHashmapToStringArray(map);
                appSettingsManager.flashMode.setValues(lookupar);
            }
        }
    }

    private void detectControlMode(CameraCharacteristics characteristics) {
        if (appSettingsManager.IsCamera2FullSupported()) {
            //flash mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                detectIntMode(characteristics,CameraCharacteristics.CONTROL_AVAILABLE_MODES,appSettingsManager.controlMode,R.array.controlModes);
                return;
            }
            else {
                int device = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                String[] lookupar = appSettingsManager.getResources().getStringArray(R.array.controlModes);
                int[] full = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_MODES) != null)
                    full = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_MODES);
                else if (device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL || device==CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 || device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED) {
                    full = new int[] {0,1,2,};
                }
                else if (device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                    full = new int[] {1,2,};
                appSettingsManager.controlMode.setIsSupported(true);
                if (appSettingsManager.controlMode.isSupported()) {
                    HashMap<String, Integer> map = new HashMap<>();
                    for (int i = 0; i < full.length; i++) {
                        map.put(lookupar[i], full[i]);
                    }
                    lookupar = StringUtils.IntHashmapToStringArray(map);
                    appSettingsManager.controlMode.setValues(lookupar);
                    appSettingsManager.controlMode.set(appSettingsManager.getResString(R.string.auto));
                }
            }
        }
    }

    private void detectPictureFormats(CameraCharacteristics characteristics)
    {
        StreamConfigurationMap smap =  characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        HashMap<String, Integer> hmap = new HashMap<>();
        if (smap.isOutputSupportedFor(ImageFormat.RAW10))
            hmap.put(appSettingsManager.getResString(R.string.pictureformat_dng10), ImageFormat.RAW10);
        if (smap.isOutputSupportedFor(ImageFormat.RAW_SENSOR))
            hmap.put(appSettingsManager.getResString(R.string.pictureformat_dng16), ImageFormat.RAW_SENSOR);
        if (smap.isOutputSupportedFor(ImageFormat.RAW12))
            hmap.put(appSettingsManager.getResString(R.string.pictureformat_dng12), ImageFormat.RAW12);
        if (smap.isOutputSupportedFor(ImageFormat.JPEG))
            hmap.put(appSettingsManager.getResString(R.string.pictureformat_jpeg), ImageFormat.JPEG);
        appSettingsManager.pictureFormat.setIsSupported(true);
        appSettingsManager.pictureFormat.set(appSettingsManager.getResString(R.string.pictureformat_jpeg));
        appSettingsManager.pictureFormat.setValues(StringUtils.IntHashmapToStringArray(hmap));
    }

    private void detectPictureSizes(CameraCharacteristics characteristics)
    {
        StreamConfigurationMap smap =  characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] size = smap.getOutputSizes(ImageFormat.JPEG);
        String[] ar = new String[size.length];
        int i = 0;
        for (Size s : size)
        {
            ar[i++] = s.getWidth()+"x"+s.getHeight();
        }

        appSettingsManager.pictureSize.setIsSupported(true);
        appSettingsManager.pictureSize.set(ar[0]);
        appSettingsManager.pictureSize.setValues(ar);
    }

    private void detectIntMode(CameraCharacteristics characteristics, CameraCharacteristics.Key<int[]> requestKey, AppSettingsManager.SettingMode settingMode, int ressourceArray)
    {
        publishProgress("detectIntMode "+settingMode+" "+ressourceArray);
        if (appSettingsManager.IsCamera2FullSupported()) {

            int[]  scenes = characteristics.get(requestKey);
            if (scenes.length >0)
                settingMode.setIsSupported(true);
            else
                return;
            String[] lookupar = appSettingsManager.getResources().getStringArray(ressourceArray);
            HashMap<String,Integer> map = new HashMap<>();
            for (int i = 0; i< scenes.length; i++)
            {
                map.put(lookupar[i], i);
            }
            lookupar = StringUtils.IntHashmapToStringArray(map);
            settingMode.setValues(lookupar);
        }
    }

    private void detectByteMode(CameraCharacteristics characteristics, CameraCharacteristics.Key<byte[]> requestKey, AppSettingsManager.SettingMode settingMode, int ressourceArray)
    {
        if (appSettingsManager.IsCamera2FullSupported() && characteristics.get(requestKey) != null) {

            byte[] scenes = characteristics.get(requestKey);
            if (scenes == null)
                return;
            if (scenes.length >0)
                settingMode.setIsSupported(true);
            else
                return;
            String[] lookupar = appSettingsManager.getResources().getStringArray(ressourceArray);
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
        AppSettingsManager.SettingMode mf = appSettingsManager.manualFocus;
        float maxfocusrange = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
        if (maxfocusrange == 0)
        {
            mf.setIsSupported(false);
            return;
        }
        float step = 0.2f;
        int count = (int)(maxfocusrange/step)+2;
        StringFloatArray focusranges = new StringFloatArray(count);
        focusranges.add(0,appSettingsManager.getResString(R.string.auto),0f);
        focusranges.add(1,"∞", 0f);
        int t = 2;
        for (float i = step; i < maxfocusrange; i += step)
        {
            focusranges.add(t++,StringUtils.getMeterString(1/i),i);
        }
        if (focusranges.getSize() > 0)
            mf.setIsSupported(true);
        else
            mf.setIsSupported(false);

        mf.setValues(focusranges.getStringArray());
    }

    private void detectManualExposure(CameraCharacteristics characteristics)
    {
        AppSettingsManager.SettingMode exposure = appSettingsManager.manualExposureCompensation;
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

        if (appSettingsManager.getCamera2MaxExposureTime() >0)
            max = appSettingsManager.getCamera2MaxExposureTime();

        String[] allvalues = appSettingsManager.getResources().getStringArray(R.array.shutter_values_autocreate);
        boolean foundmin = false;
        boolean foundmax = false;
        ArrayList<String> tmp = new ArrayList<>();
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
        appSettingsManager.manualExposureTime.setIsSupported(tmp.size() > 0);
        appSettingsManager.manualExposureTime.setValues(tmp.toArray(new String[tmp.size()]));

    }

    private void detectManualIso(CameraCharacteristics characteristics)
    {
        int max  = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper();
        if (appSettingsManager.getCamera2MaxIso() >0)
            max = appSettingsManager.getCamera2MaxIso();

        int min = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getLower();
        ArrayList<String> ar = new ArrayList<>();
        ar.add("auto");
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
        appSettingsManager.manualIso.setIsSupported(ar.size() > 0);
        appSettingsManager.manualIso.setValues(ar.toArray(new String[ar.size()]));
    }

    private void detectVideoMediaProfiles(int cameraid)
    {
        HashMap<String,VideoMediaProfile> supportedProfiles = getDefaultVideoMediaProfiles(cameraid);

        if (supportedProfiles.get("2160p") == null && has2160pSize()) {
            supportedProfiles.put("2160p", new VideoMediaProfile("156000 2 3 48000 30 2 10007 48000000 2 30 2160 3840 2160p Normal true"));
            supportedProfiles.put("2160p_Timelapse",new VideoMediaProfile("156000 2 3 48000 30 2 10007 48000000 2 30 2160 3840 2160p_TimeLapse Timelapse true"));
        }
        appSettingsManager.saveMediaProfiles(supportedProfiles);

        publishProgress("VideoMediaProfiles:" + getStringFromArray(supportedProfiles.keySet().toArray(new String[supportedProfiles.size()])));
    }

    private boolean has2160pSize()
    {
        String[] size = appSettingsManager.pictureSize.getValues();
        for (String s: size) {
            if (s.matches("3840x2160"))
                return true;
        }
        return false;
    }
}
