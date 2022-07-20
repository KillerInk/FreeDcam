package freed.cam.apis.featuredetector.camera2;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.Arrays;
import java.util.HashMap;

import camera2_hidden_keys.huawei.CameraCharacteristicsHuawei;
import freed.FreedApplication;
import freed.settings.SettingKeys;
import freed.utils.Log;
import freed.utils.StringUtils;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PictureFormatDetector extends BaseParameter2Detector {

    private final String TAG = PictureFormatDetector.class.getSimpleName();

    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        detectPictureFormats(cameraCharacteristics);
    }


    private void detectPictureFormats(CameraCharacteristics characteristics)
    {
        StreamConfigurationMap smap =  characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        HashMap<String, Integer> hmap = new HashMap<>();
        try {
            if (smap.isOutputSupportedFor(ImageFormat.RAW10)) {
                hmap.put(FreedApplication.getStringFromRessources(R.string.pictureformat_dng10), ImageFormat.RAW10);
                hmap.put(FreedApplication.getStringFromRessources(R.string.pictureformat_bayer10), ImageFormat.RAW10);
            }
        } catch (Exception e) {
            Log.WriteEx(e);
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.RAW_SENSOR)) {
                hmap.put(FreedApplication.getStringFromRessources(R.string.pictureformat_dng16), ImageFormat.RAW_SENSOR);
                hmap.put(FreedApplication.getStringFromRessources(R.string.pictureformat_bayer), ImageFormat.RAW_SENSOR);
                Size[] size = smap.getOutputSizes(ImageFormat.RAW_SENSOR);
                /*if (size == null || size.length == 1) {
                    int[] rawsizes = characteristics.get(CameraCharacteristicsXiaomi.availableRawSizes);
                    if (rawsizes != null && rawsizes.length > 0)
                    {
                        List<Size> s = new ArrayList<>();
                        for (int i= 0; i < rawsizes.length; i+=2)
                        {
                            s.add(new Size(rawsizes[i], rawsizes[i+1]));
                        }
                        size = s.toArray(new Size[s.size()]);
                    }
                }*/
                if (size != null)
                {
                    Log.d(TAG, "RAW_SENSORSIZES:" + Arrays.toString(size));
                    if (size.length > 1)
                    {
                        settingsManager.get(SettingKeys.RAW_SIZE).setIsSupported(true);
                        String[] rawsizes = new String[size.length];
                        for (int i = 0; i<size.length;i++)
                        {
                            rawsizes[i] = size[i].getWidth() + "x" + size[i].getHeight();
                        }
                        settingsManager.get(SettingKeys.RAW_SIZE).setValues(rawsizes);
                        settingsManager.get(SettingKeys.RAW_SIZE).set(rawsizes[0]);
                    }
                    else
                    {
                        settingsManager.get(SettingKeys.RAW_SIZE).setIsSupported(false);
                    }
                }
            }
        } catch (Exception e) {
            Log.WriteEx(e);
        }
        try {
            if (characteristics.get(CameraCharacteristicsHuawei.HUAWEI_RAW_FORMAT) != null) {
                Byte rawFormat = characteristics.get(CameraCharacteristicsHuawei.HUAWEI_RAW_FORMAT);
                hmap.put(FreedApplication.getStringFromRessources(R.string.pictureformat_dng16), rawFormat.intValue());
                hmap.put(FreedApplication.getStringFromRessources(R.string.pictureformat_bayer), rawFormat.intValue());
                Size[] size = smap.getOutputSizes(ImageFormat.RAW_SENSOR);
                if (size != null)
                {
                    Log.d(TAG, "RAW_SENSORSIZES:" + Arrays.toString(size));
                    if (size.length > 1)
                    {
                        settingsManager.get(SettingKeys.RAW_SIZE).setIsSupported(true);
                        String[] rawsizes = new String[size.length];
                        for (int i = 0; i<size.length;i++)
                        {
                            rawsizes[i] = size[i].getWidth() + "x" + size[i].getHeight();
                            Log.d(TAG, "Add new RawSize:" + rawsizes[i]);
                        }
                        settingsManager.get(SettingKeys.RAW_SIZE).setValues(rawsizes);
                        settingsManager.get(SettingKeys.RAW_SIZE).set(rawsizes[0]);
                    }
                    else
                    {
                        settingsManager.get(SettingKeys.RAW_SIZE).setIsSupported(false);
                    }
                }
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            Log.d(TAG, "Dont support HUAWEI_RAW_FORMAT");
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.RAW12))
                hmap.put(FreedApplication.getStringFromRessources(R.string.pictureformat_dng12), ImageFormat.RAW12);
        } catch (Exception e) {
            Log.d(TAG, "Dont support RAW12");
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.JPEG))
                hmap.put(FreedApplication.getStringFromRessources(R.string.pictureformat_jpeg), ImageFormat.JPEG);
        } catch (Exception e) {
            Log.d(TAG, "Dont support JPEG");
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.YUV_420_888)) {
                hmap.put(FreedApplication.getStringFromRessources(R.string.pictureformat_yuv), ImageFormat.YUV_420_888);
                Size[] size = smap.getOutputSizes(ImageFormat.YUV_420_888);
                if (size != null)
                {
                    Log.d(TAG, "RAW_SENSORSIZES:" + Arrays.toString(size));
                    if (size.length > 1)
                    {
                        settingsManager.get(SettingKeys.YUV_SIZE).setIsSupported(true);
                        String[] rawsizes = new String[size.length];
                        for (int i = 0; i<size.length;i++)
                        {
                            rawsizes[i] = size[i].getWidth() + "x" + size[i].getHeight();
                        }
                        settingsManager.get(SettingKeys.YUV_SIZE).setValues(rawsizes);
                        settingsManager.get(SettingKeys.YUV_SIZE).set(rawsizes[0]);
                    }
                    else
                    {
                        settingsManager.get(SettingKeys.YUV_SIZE).setIsSupported(false);
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Dont support YUV_420_888");
        }
        if (
                hmap.containsKey(FreedApplication.getStringFromRessources(R.string.pictureformat_jpeg)) &&
                        (
                                hmap.containsKey(FreedApplication.getStringFromRessources(R.string.pictureformat_dng10))
                                        || hmap.containsKey(FreedApplication.getStringFromRessources(R.string.pictureformat_dng16)))
        )
            hmap.put(FreedApplication.getStringFromRessources(R.string.pictureformat_jpg_p_dng), ImageFormat.JPEG);

        try {
            if (smap.isOutputSupportedFor(ImageFormat.NV16))
                Log.d(TAG, "Support NV16");
        }
        catch (IllegalArgumentException ex)
        {
            Log.d(TAG, "Dont support NV16");
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.NV21))
                Log.d(TAG, "Support NV21");
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Dont support NV21");
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.YUV_420_888))
                Log.d(TAG, "Support YUV_420_888");
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Dont support YUV_420_888");
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.YUV_422_888))
                Log.d(TAG, "Support YUV_422_888");
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Dont support YUV_422_888");
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.YUV_444_888))
                Log.d(TAG, "Support YUV_444_888");
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Dont support YUV_444_888");
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.YV12))
                Log.d(TAG, "Support YV12");
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Dont support yv12");
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.DEPTH16))
                Log.d(TAG, "Support DEPTH16");
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Dont support DEPTH16");
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.DEPTH_POINT_CLOUD))
                Log.d(TAG, "Support DEPTH_POINT_CLOUD");
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Dont support DEPTH_POINT_CLOUD");
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.RAW_PRIVATE))
                Log.d(TAG, "Support RAW_PRIVATE");
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Dont support RAW_PRIVATE");
        }
        try {
            if (smap.isOutputSupportedFor(ImageFormat.PRIVATE))
                Log.d(TAG, "Support PRIVATE");
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "Dont support PRIVATE");
        }

        settingsManager.get(SettingKeys.PICTURE_FORMAT).setIsSupported(true);
        if(hmap.containsKey(FreedApplication.getStringFromRessources(R.string.pictureformat_jpeg)))
            settingsManager.get(SettingKeys.PICTURE_FORMAT).set(FreedApplication.getStringFromRessources(R.string.pictureformat_jpeg));
        else if (hmap.containsKey(FreedApplication.getStringFromRessources(R.string.pictureformat_yuv)))
            settingsManager.get(SettingKeys.PICTURE_FORMAT).set(FreedApplication.getStringFromRessources(R.string.pictureformat_yuv));
        settingsManager.get(SettingKeys.PICTURE_FORMAT).setValues(StringUtils.IntHashmapToStringArray(hmap));
    }
}
