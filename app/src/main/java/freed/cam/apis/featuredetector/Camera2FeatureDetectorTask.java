package freed.cam.apis.featuredetector;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Size;

import com.troop.freedcam.R;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import camera2_hidden_keys.VendorKeyParser;
import camera2_hidden_keys.qcom.CameraCharacteristicsQcom;
import camera2_hidden_keys.qcom.CaptureRequestQcom;
import camera2_hidden_keys.xiaomi.CameraCharacteristicsXiaomi;
import camera2_hidden_keys.xiaomi.CaptureRequestXiaomi;
import freed.FreedApplication;
import freed.cam.apis.featuredetector.camera2.AeTargetFpsDetector;
import freed.cam.apis.featuredetector.camera2.AntiBadindingModeDetector;
import freed.cam.apis.featuredetector.camera2.ApertureDetector;
import freed.cam.apis.featuredetector.camera2.AutoExposureModeDetector;
import freed.cam.apis.featuredetector.camera2.AutoFocusModeDetector;
import freed.cam.apis.featuredetector.camera2.AwbModesDetector;
import freed.cam.apis.featuredetector.camera2.CameraControlModeDetector;
import freed.cam.apis.featuredetector.camera2.ColorCorrectionModeDetector;
import freed.cam.apis.featuredetector.camera2.ColorModeDetector;
import freed.cam.apis.featuredetector.camera2.DenoisParameterDetector;
import freed.cam.apis.featuredetector.camera2.EdgeModeDetector;
import freed.cam.apis.featuredetector.camera2.EvDetector;
import freed.cam.apis.featuredetector.camera2.ExposureTimeDetector;
import freed.cam.apis.featuredetector.camera2.FlashDetector;
import freed.cam.apis.featuredetector.camera2.HotPixelModeDetector;
import freed.cam.apis.featuredetector.camera2.IsoDetector;
import freed.cam.apis.featuredetector.camera2.ManualFocusDetector;
import freed.cam.apis.featuredetector.camera2.OisDetector;
import freed.cam.apis.featuredetector.camera2.PictureFormatDetector;
import freed.cam.apis.featuredetector.camera2.PictureSizeDetector;
import freed.cam.apis.featuredetector.camera2.SaturationDetector;
import freed.cam.apis.featuredetector.camera2.SceneModeDetector;
import freed.cam.apis.featuredetector.camera2.ShadingModesDetector;
import freed.cam.apis.featuredetector.camera2.SharpnessDetector;
import freed.cam.apis.featuredetector.camera2.ToneMapModesDetector;
import freed.cam.apis.featuredetector.camera2.VideoMediaProfilesDetector;
import freed.cam.apis.featuredetector.camera2.VideoStabilizationModeDetector;
import freed.cam.apis.featuredetector.camera2.huawei.DualPrimaryCameraDetector;
import freed.cam.apis.featuredetector.camera2.huawei.IsoExposureTimeDetector;
import freed.cam.apis.featuredetector.camera2.huawei.Raw12bitDetector;
import freed.cam.apis.featuredetector.camera2.huawei.SecondarySensorSizeDetector;
import freed.cam.apis.featuredetector.camera2.huawei.WhitebalanceRangeDetector;
import freed.cam.ui.videoprofileeditor.MediaCodecInfoParser;
import freed.renderscript.RenderScriptManager;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;


/**
 * Created by troop on 23.01.2017.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2FeatureDetectorTask extends AbstractFeatureDetectorTask {

    private final String TAG = Camera2FeatureDetectorTask.class.getSimpleName();
    boolean hasCamera2Features;

    public int hwlvl = CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY;

    public Camera2FeatureDetectorTask(ProgressUpdate progressUpdate) {
        super(progressUpdate);
    }


    @Override
    public void detect()
    {
        new MediaCodecInfoParser().logMediaCodecInfos();

        SettingsManager.getInstance().setCamApi(SettingsManager.API_2);
        CameraManager manager = (CameraManager) FreedApplication.getContext().getSystemService(Context.CAMERA_SERVICE);

        List<String> cameraids =new ArrayList<>();
        findCameraIds(manager, cameraids);

        Log.d(TAG, "Found camera ids:" + Arrays.toString(cameraids.toArray()));
        int arr[] = new int[cameraids.size()];
        for (int i = 0; i<arr.length;i++)
            arr[i] = Integer.parseInt(cameraids.get(i));
        SettingsManager.getInstance().setCameraIds(arr);
        SettingsManager.getInstance().SetCurrentCamera(0);


        for (int c = 0; c < cameraids.size();c++)
        {

            CameraCharacteristics characteristics = null;
            try {
                characteristics = manager.getCameraCharacteristics(cameraids.get(c));
            } catch (CameraAccessException e) {
                Log.WriteEx(e);
            }
            if (characteristics == null) {
                Log.e(TAG, "Failed to get Characteristics for camera id:" + c);
                return;
            }
            boolean front = characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;

            VendorKeyParser vendorKeyParser = new VendorKeyParser();
            HashSet<String> vendorkeys = null;
            try {
                vendorKeyParser.readVendorKeys(characteristics);
                vendorkeys = vendorKeyParser.getRequests();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            SettingsManager.getInstance().SetCurrentCamera(c);

            SettingsManager.get(SettingKeys.orientationHack).setValues(new String[]{"0","90","180","270"});
            SettingsManager.get(SettingKeys.orientationHack).set("0");
            SettingsManager.get(SettingKeys.orientationHack).setIsSupported(true);

            SettingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).set(false);
            SettingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).setIsSupported(true);

            SettingsManager.getInstance().setIsFrontCamera(front);
            SettingsManager.getApi(SettingKeys.Module).set(FreedApplication.getStringFromRessources(R.string.module_picture));
            hwlvl = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);

            SettingsManager.get(SettingKeys.selfTimer).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.selftimervalues));
            SettingsManager.get(SettingKeys.selfTimer).set(SettingsManager.get(SettingKeys.selfTimer).getValues()[0]);

            SettingsManager.get(SettingKeys.VIDEO_AUDIO_SOURCE).set(FreedApplication.getStringFromRessources(R.string.video_audio_source_default));
            SettingsManager.get(SettingKeys.VIDEO_AUDIO_SOURCE).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.video_audio_source));
            SettingsManager.get(SettingKeys.VIDEO_AUDIO_SOURCE).setIsSupported(true);

            if (RenderScriptManager.isSupported()) {
                SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.focuspeakColors));
                SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).set(SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).getValues()[0]);
                SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).setIsSupported(true);
            }

            SettingsManager.getGlobal(SettingKeys.LOCATION_MODE).setIsSupported(true);

            dump_SCALER_STREAM_CONFIGURATION_MAP(characteristics);

            //check first if a already checked cam have camera2features and if its now the front cam that dont have a camera2feature.
            //in that case set it to true
            //else it would override the already detected featureset from last cam and disable api2
            if (SettingsManager.getInstance().hasCamera2Features() && front) {
                hasCamera2Features = true;
                Log.d(TAG,"Front cam has no camera2 featureset, try to find supported things anyway");
            }
            else
                hasCamera2Features = true;
            SettingsManager.getInstance().setHasCamera2Features(hasCamera2Features);

            SettingsManager.getGlobal(SettingKeys.GuideList).setValues(FreedApplication.getContext().getResources().getStringArray(R.array.guidelist));
            SettingsManager.getGlobal(SettingKeys.GuideList).set(SettingsManager.getGlobal(SettingKeys.GuideList).getValues()[0]);

            if (!SettingsManager.get(SettingKeys.ENABLE_VIDEO_OPMODE).isPresetted())
                SettingsManager.get(SettingKeys.ENABLE_VIDEO_OPMODE).setIsSupported(false);
            if (!SettingsManager.get(SettingKeys.MFNR).isPresetted())
                SettingsManager.get(SettingKeys.MFNR).setIsSupported(false);


            if (hasCamera2Features) {

                if(!SettingsManager.getInstance().getIsFrontCamera()) {
                    new FlashDetector().checkIfSupported(characteristics);
                }
                new SceneModeDetector().checkIfSupported(characteristics);
                new AntiBadindingModeDetector().checkIfSupported(characteristics);
                new ColorModeDetector().checkIfSupported(characteristics);
                new EdgeModeDetector().checkIfSupported(characteristics);
                new OisDetector().checkIfSupported(characteristics);
                new AutoFocusModeDetector().checkIfSupported(characteristics);
                new HotPixelModeDetector().checkIfSupported(characteristics);
                new VideoStabilizationModeDetector().checkIfSupported(characteristics);
                new DenoisParameterDetector().checkIfSupported(characteristics);
                new PictureFormatDetector().checkIfSupported(characteristics);
                new ManualFocusDetector().checkIfSupported(characteristics);
                new PictureSizeDetector().checkIfSupported(characteristics);
                //call this after PictureSizes got detected. it depends on the picturesizes
                new VideoMediaProfilesDetector().checkIfSupported(characteristics);
                new AutoExposureModeDetector().checkIfSupported(characteristics);
                new EvDetector().checkIfSupported(characteristics);
                new ExposureTimeDetector().checkIfSupported(characteristics);
                new IsoDetector().checkIfSupported(characteristics);
                new ApertureDetector().checkIfSupported(characteristics);
                new ColorCorrectionModeDetector().checkIfSupported(characteristics);
                new ToneMapModesDetector().checkIfSupported(characteristics);
                new ShadingModesDetector().checkIfSupported(characteristics);
                new AwbModesDetector().checkIfSupported(characteristics);
                new CameraControlModeDetector().checkIfSupported(characteristics);
                new AeTargetFpsDetector().checkIfSupported(characteristics);
                new SharpnessDetector().checkIfSupported(characteristics);
                new SaturationDetector().checkIfSupported(characteristics);

                detectHuaweiParameters(characteristics);

                dumpQCFA(characteristics);

                detectXiaomiStuff(characteristics,vendorkeys);

                dumpQcomStuff(characteristics);

                if (SettingsManager.get(SettingKeys.M_Focus).isSupported()) {
                    SettingsManager.get(SettingKeys.ZOOM_ON_MANUALFOCUS).setIsSupported(true);
                    SettingsManager.get(SettingKeys.ZOOM_ON_MANUALFOCUS).set(true);
                    SettingsManager.get(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMFACTOR).setIsSupported(true);
                    String[] zoom = new String[]{"0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};
                    SettingsManager.get(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMFACTOR).setValues(zoom);
                    SettingsManager.get(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMFACTOR).set("50");
                    SettingsManager.get(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMDURATION).setIsSupported(true);
                    String[] duration = new String[]{"0", "1", "2", "3", "4", "5"};
                    SettingsManager.get(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMDURATION).setValues(duration);
                    SettingsManager.get(SettingKeys.ZOOM_ON_MANUALFOCUS_ZOOMDURATION).set("1");
                }

            }
        }
        SettingsManager.getInstance().SetCurrentCamera(0);
        if (!hasCamera2Features || hwlvl == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            SettingsManager.getInstance().setCamApi(SettingsManager.API_1);
        }
    }

    private void detectXiaomiStuff(CameraCharacteristics characteristics, HashSet<String> vendorkeys) {
        try {
            if(isKeySupported(vendorkeys,CameraCharacteristicsXiaomi.SUPPORT_VIDEO_HDR10)) {
                int video10 = characteristics.get(CameraCharacteristicsXiaomi.SUPPORT_VIDEO_HDR10);
                Log.d(TAG, "video10bit suppported");
            }
        }catch (IllegalArgumentException | NullPointerException ex)
        {
            Log.d(TAG, "video10bit unsuppported");
        }
        try {
            if (isKeySupported(vendorkeys,CameraCharacteristicsXiaomi.EIS_QUALITY_SUPPORTED)) {
                Integer[] eismodes = characteristics.get(CameraCharacteristicsXiaomi.EIS_QUALITY_SUPPORTED);
                Log.d(TAG, "eismodes supported");
            }
        }catch (IllegalArgumentException | NullPointerException ex)
        {
            Log.d(TAG, "eismodes unsupported");
        }

        try {
            if (isKeySupported(vendorkeys,CaptureRequestXiaomi.VIDEO_RECORD_CONTROL))
                SettingsManager.get(SettingKeys.XIAOMI_VIDEO_RECORD_CONTROL).setIsSupported(true);
            Log.d(TAG, "VIDEO_RECORD_CONTROL supported");
        }catch (IllegalArgumentException | NullPointerException ex)
        {
            Log.d(TAG, "VIDEO_RECORD_CONTROL unsupported");
        }

        try {
            if (isKeySupported(vendorkeys,CaptureRequestXiaomi.PRO_VIDEO_LOG_ENABLED))
                SettingsManager.get(SettingKeys.XIAOMI_PRO_VIDEO_LOG).setIsSupported(true);
            Log.d(TAG, "VIDEO_RECORD_CONTROL supported");
        }catch (IllegalArgumentException | NullPointerException ex)
        {
            Log.d(TAG, "VIDEO_RECORD_CONTROL unsupported");
        }

        try {
            if (isKeySupported(vendorkeys, CaptureRequestQcom.HDR10_VIDEO))
                SettingsManager.get(SettingKeys.QCOM_VIDEO_HDR10).setIsSupported(true);
            Log.d(TAG, "VIDEO_RECORD_CONTROL supported");
        }catch (IllegalArgumentException | NullPointerException ex)
        {
            Log.d(TAG, "VIDEO_RECORD_CONTROL unsupported");
        }
    }

    private boolean isKeySupported(HashSet<String> vendorkeys, CaptureRequest.Key key)
    {
        return vendorkeys != null && vendorkeys.contains(key.getName());
    }

    private boolean isKeySupported(HashSet<String> vendorkeys, CameraCharacteristics.Key key)
    {
        return vendorkeys != null && vendorkeys.contains(key.getName());
    }

    private void findCameraIds(CameraManager manager, List<String> cameraids) {
        List<String> focap = new ArrayList<>();
        for (int i = 0; i< 200; i++)
        {
            try {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(String.valueOf(i));

                if (characteristics != null) {
                    String pair = "";
                    float focal[] = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                    float aperture[] = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
                    if (focal != null && focal.length>0)
                        pair += String.valueOf(focal[0]);
                    if (aperture != null && aperture.length > 0)
                        pair += String.valueOf(aperture[0]);

                    if (!focap.contains(pair))
                    {
                        focap.add(pair);
                        cameraids.add(String.valueOf(i));
                    }
                }
            }
            catch (IllegalArgumentException ex)
            {
                Log.d(TAG, "unsupported id: " + i);
            }
            catch (CameraAccessException ex)
            {
                Log.d(TAG, "unsupported id: " + i);
            }
            catch (Exception ex)
            {
                Log.d(TAG, "unsupported id: " + i);
            }
        }
    }

    private void detectHuaweiParameters(CameraCharacteristics characteristics) {
        new DualPrimaryCameraDetector().checkIfSupported(characteristics);
        new IsoExposureTimeDetector().checkIfSupported(characteristics);
        new Raw12bitDetector().checkIfSupported(characteristics);
        new SecondarySensorSizeDetector().checkIfSupported(characteristics);
        new WhitebalanceRangeDetector().checkIfSupported(characteristics);
    }

    private void dump_SCALER_STREAM_CONFIGURATION_MAP(CameraCharacteristics characteristics)
    {
        StreamConfigurationMap smap =  characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        int outputformats[] =  smap.getOutputFormats();
        String out;
        for(int outformat : outputformats)
        {
            switch (outformat)
            {
                case ImageFormat.DEPTH16:
                    Log.d(TAG,"ImageFormat.DEPTH16 : " + logResForFormat(smap,ImageFormat.DEPTH16));
                    break;
                case ImageFormat.DEPTH_JPEG:
                    Log.d(TAG,"ImageFormat.DEPTH_JPEG : " + logResForFormat(smap,ImageFormat.DEPTH_JPEG));
                    break;
                case ImageFormat.DEPTH_POINT_CLOUD:
                    Log.d(TAG,"ImageFormat.DEPTH_POINT_CLOUD : " + logResForFormat(smap,ImageFormat.DEPTH_POINT_CLOUD));
                    break;
                case ImageFormat.FLEX_RGB_888:
                    Log.d(TAG,"ImageFormat.FLEX_RGB_888 : " + logResForFormat(smap,ImageFormat.FLEX_RGB_888));
                    break;
                case ImageFormat.FLEX_RGBA_8888:
                    Log.d(TAG,"ImageFormat.FLEX_RGBA_8888 : " + logResForFormat(smap,ImageFormat.FLEX_RGBA_8888));
                    break;
                case ImageFormat.HEIC:
                    Log.d(TAG,"ImageFormat.HEIC : " + logResForFormat(smap,ImageFormat.HEIC));
                    break;
                case ImageFormat.JPEG:
                    Log.d(TAG,"ImageFormat.JPEG : " + logResForFormat(smap,ImageFormat.JPEG));
                    break;
                case ImageFormat.NV16:
                    Log.d(TAG,"ImageFormat.NV16 : " + logResForFormat(smap,ImageFormat.NV16));
                    break;
                case ImageFormat.NV21:
                    Log.d(TAG,"ImageFormat.NV21 : " + logResForFormat(smap,ImageFormat.NV21));
                    break;
                case ImageFormat.PRIVATE:
                    Log.d(TAG,"ImageFormat.NV21 : " + logResForFormat(smap,ImageFormat.PRIVATE));
                    break;
                case ImageFormat.RAW10:
                    Log.d(TAG,"ImageFormat.RAW10 : " + logResForFormat(smap,ImageFormat.RAW10));
                    break;
                case ImageFormat.RAW12:
                    Log.d(TAG,"ImageFormat.RAW12 : " + logResForFormat(smap,ImageFormat.RAW12));
                    break;
                case ImageFormat.RAW_PRIVATE:
                    Log.d(TAG,"ImageFormat.RAW_PRIVATE : " + logResForFormat(smap,ImageFormat.RAW_PRIVATE));
                    break;
                case ImageFormat.RAW_SENSOR:
                    Log.d(TAG,"ImageFormat.RAW_SENSOR : " + logResForFormat(smap,ImageFormat.RAW_SENSOR));
                    break;
                case ImageFormat.RGB_565:
                    Log.d(TAG,"ImageFormat.RGB_565 : " + logResForFormat(smap,ImageFormat.RGB_565));
                    break;
                case ImageFormat.UNKNOWN:
                    Log.d(TAG,"ImageFormat.UNKNOWN : " + logResForFormat(smap,ImageFormat.UNKNOWN));
                    break;
                case ImageFormat.Y8:
                    Log.d(TAG,"ImageFormat.Y8 : " + logResForFormat(smap,ImageFormat.Y8));
                    break;
                case ImageFormat.YUV_420_888:
                    Log.d(TAG,"ImageFormat.YUV_420_888 : " + logResForFormat(smap,ImageFormat.YUV_420_888));
                    break;
                case ImageFormat.YUV_422_888:
                    Log.d(TAG,"ImageFormat.YUV_422_888 : " + logResForFormat(smap,ImageFormat.YUV_422_888));
                    break;
                case ImageFormat.YUV_444_888:
                    Log.d(TAG,"ImageFormat.YUV_444_888 : " + logResForFormat(smap,ImageFormat.YUV_444_888));
                    break;
                case ImageFormat.YUY2:
                    Log.d(TAG,"ImageFormat.YUY2 : " + logResForFormat(smap,ImageFormat.YUY2));
                    break;
                case ImageFormat.YV12:
                    Log.d(TAG,"ImageFormat.YV12 : " + logResForFormat(smap,ImageFormat.YV12));
                    break;
            }
        }
    }

    private String logResForFormat(StreamConfigurationMap smap, int imageFormat)
    {
        Size[] sizes =  smap.getOutputSizes(imageFormat);
        return Arrays.toString(sizes);
    }


    private void dumpQCFA(CameraCharacteristics cameraCharacteristics)
    {
        try {
            byte isQcfa = cameraCharacteristics.get(CameraCharacteristicsQcom.is_qcfa_sensor);
            Log.d(TAG, "isQcfa:" + isQcfa);
            Integer[] qcfa_dimens = cameraCharacteristics.get(CameraCharacteristicsQcom.qcfa_dimension);
            Log.d(TAG, "qcfa_dimens:" + Arrays.toString(qcfa_dimens));
            Integer[] qcfa_streamSizes = cameraCharacteristics.get(CameraCharacteristicsQcom.qcfa_availableStreamConfigurations);
            Log.d(TAG, "qcfa avail stream config" + Arrays.toString(qcfa_streamSizes));
            Integer[] active_array_size = cameraCharacteristics.get(CameraCharacteristicsQcom.qcfa_activeArraySize);
            Log.d(TAG, "qcfa acitve array size: " + Arrays.toString(active_array_size));
        }
        catch (IllegalArgumentException | NullPointerException ex)
        {
            Log.d(TAG, "No QCFA sensor");
        }
        //Integer customhw = cameraCharacteristics.get(CameraCharacteristicsQcom.customhw);
        //Log.d(TAG, "customhw: " + customhw);
        try {

            byte qcfaenabled = cameraCharacteristics.get(CameraCharacteristicsXiaomi.qcfa_enabled);
            Log.d(TAG, "qcfa enabled:" + qcfaenabled);
            byte qcfasupported = cameraCharacteristics.get(CameraCharacteristicsXiaomi.qcfa_supported);
            Log.d(TAG, "qcfa supported:" + qcfasupported);
        }
        catch (IllegalArgumentException | NullPointerException ex)
        {
            Log.d(TAG, "No QCFA sensor");
        }
    }

    private void dumpQcomStuff(CameraCharacteristics characteristics)
    {
        try {
            int[] sensor_mode_table = characteristics.get(CameraCharacteristicsQcom.sensorModeTable);
            Camera2Util.dumpIntArray(sensor_mode_table,TAG, "sensor_mode_table");
            Log.d(TAG,"qcom sensor mode table");
        }
        catch (NullPointerException | IllegalArgumentException ex)
        {
            Log.e(TAG,"qcom sensor mode table false");
        }

        try {
            int[] supported_video_hdr_modes = characteristics.get(CameraCharacteristicsQcom.support_video_hdr_modes);
            Camera2Util.dumpIntArray(supported_video_hdr_modes,TAG, "supported_video_hdr_modes");
            Log.d(TAG,"supported_video_hdr_modes");
        }
        catch (NullPointerException | IllegalArgumentException ex)
        {
            Log.e(TAG,"supported_video_hdr_modes false");
        }
    }
}
