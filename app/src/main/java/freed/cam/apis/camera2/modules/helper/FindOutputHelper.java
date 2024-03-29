package freed.cam.apis.camera2.modules.helper;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.text.TextUtils;
import android.util.Size;

import com.troop.freedcam.R;

import java.util.Arrays;
import java.util.Collections;

import camera2_hidden_keys.huawei.CameraCharacteristicsHuawei;
import freed.FreedApplication;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FindOutputHelper
{
    private final   String TAG = FindOutputHelper.class.getSimpleName();
    public Output getHuaweiOutput(CameraHolderApi2 cameraHolder, SettingsManager settingsManager) {
        String picFormat = settingsManager.get(SettingKeys.PICTURE_FORMAT).get();
        Log.d(TAG, "PictureFormat "  + picFormat);
        Output output =  new Output();
        if (settingsManager.get(SettingKeys.DUAL_PRIMARY_CAMERA_MODE).isSupported())
        {
            findRawFormat(cameraHolder, output);


            Log.d(TAG, "Use RawFormat: " + output.raw_format);

            String camera = settingsManager.get(SettingKeys.DUAL_PRIMARY_CAMERA_MODE).get();
            Log.d(TAG, "secondary sensor size supported: " + settingsManager.get(SettingKeys.SECONDARY_SENSOR_SIZE).isSupported());
            //handel the first cam or the case that the second sensor has same size as first
            if (camera.equals(FreedApplication.getStringFromRessources(R.string.hw_dualcamera_Primary)) || camera.equals(FreedApplication.getStringFromRessources(R.string.hw_dualcamera_Dual))) {
                if (findColorOutput(cameraHolder, picFormat, output,settingsManager))
                    return getStockOutput(cameraHolder,settingsManager);
            }
            else if (camera.equals(FreedApplication.getStringFromRessources(R.string.hw_dualcamera_Secondary)))
            {
                if (findMonoOutput(cameraHolder, picFormat, output,settingsManager))
                    return getStockOutput(cameraHolder,settingsManager);
            }
        }
        else
            return getStockOutput(cameraHolder,settingsManager);
        Log.d(TAG, "Final huawei output" + output);
        return output;
    }

    private boolean findMonoOutput(CameraHolderApi2 cameraHolder, String picFormat, Output output,SettingsManager settingsManager) {
        Log.d(TAG, "mono sensor");
        if (output.raw_format == ImageFormat.RAW_SENSOR && !settingsManager.get(SettingKeys.SECONDARY_SENSOR_SIZE).isSupported())
            return true;
        else {
            Log.d(TAG, "get Jpeg size");
            String picSize = settingsManager.get(SettingKeys.SECONDARY_SENSOR_SIZE).get();
            if (!TextUtils.isEmpty(picSize)) {
                Log.d(TAG, "Jpeg Secondary sensor size: " + picSize);
                String[] split = picSize.split("x");
                output.jpeg_width = Integer.parseInt(split[0]);
                output.jpeg_height = Integer.parseInt(split[1]);
            }
            else
            {
                Log.d(TAG, "Jpeg Secondary sensor size is empty");
                picSize = settingsManager.get(SettingKeys.SECONDARY_SENSOR_SIZE).getValues()[0];
                Log.d(TAG, "Jpeg Secondary sensor size: " + picSize);
                String[] split = picSize.split("x");
                output.jpeg_width = Integer.parseInt(split[0]);
                output.jpeg_height = Integer.parseInt(split[1]);
            }

            if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_dng16))
                    || picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_jpg_p_dng))
                    || picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_bayer))) {
                Log.d(TAG, "get raw size");
                int[] subsize = cameraHolder.characteristics.get(CameraCharacteristicsHuawei.HUAWEI_SENCONDARY_SENSOR_PIXEL_ARRAY_SIZE);
                Log.d(TAG, "HUAWEI_SENCONDARY_SENSOR_PIXEL_ARRAY_SIZE is null" + (subsize == null));
                if (subsize.length > 2) { // if this is null it crash correct
                    output.raw_width = subsize[2];
                    output.raw_height = subsize[3];
                } else {
                    output.raw_width = subsize[0];
                    output.raw_height = subsize[1];
                }
            }
            else
            {
                Log.d(TAG, "no raw capture");
                output.raw_width = 0;
                output.raw_height = 0;
                output.raw_format = 0;
            }
        }
        return false;
    }

    /**
     *
     * @param cameraHolder that represent the camerainstance
     * @param picFormat
     * @param output the output for the colorcam
     * @return false if values are found
     */
    private boolean findColorOutput(CameraHolderApi2 cameraHolder, String picFormat, Output output, SettingsManager settingsManager) {
        if (output.raw_format == ImageFormat.RAW_SENSOR && !settingsManager.get(SettingKeys.SECONDARY_SENSOR_SIZE).isSupported())
            return true;
        else
        {
            String picSize = settingsManager.get(SettingKeys.PICTURE_SIZE).get();
            String[] split = picSize.split("x");
            output.jpeg_width = Integer.parseInt(split[0]);
            output.jpeg_height = Integer.parseInt(split[1]);
            Log.d(TAG, "Jpeg size: " + picSize);
            if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_dng16))
                    || picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_jpg_p_dng))
                    || picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_bayer))) {
                Log.d(TAG, "getRawsize");
                Rect subsize = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                output.raw_width = subsize.width();
                output.raw_height = subsize.height();
                Log.d(TAG, "raw size: " + output.raw_width +"x" + output.raw_height);
            }
            else {
                Log.d(TAG, "no raw capture");
                output.raw_format = 0;
                output.raw_width =0;
                output.raw_height = 0;
            }
        }
        return false;
    }

    /**
     * @param cameraHolder that represent the camerainstance
     * @param output returns the rawformat from the huawei camera2 api
     */
    private void findRawFormat(CameraHolderApi2 cameraHolder, Output output) {
        try {
            if (CameraCharacteristicsHuawei.HUAWEI_RAW_FORMAT != null && cameraHolder.characteristics.get(CameraCharacteristicsHuawei.HUAWEI_RAW_FORMAT) != null)
                output.raw_format = cameraHolder.characteristics.get(CameraCharacteristicsHuawei.HUAWEI_RAW_FORMAT).intValue();
            else
                output.raw_format = ImageFormat.RAW_SENSOR;
        }
        catch (IllegalArgumentException | NullPointerException ex)
        {
            Log.d(TAG, "Dont support huawei raw format");
            output.raw_format = ImageFormat.RAW_SENSOR;
        }
    }

    /**
     *
     * @param cameraHolder that represent the camerainstance
     * @return the output from stock camera2 api
     */
    public Output getStockOutput(CameraHolderApi2 cameraHolder,SettingsManager settingsManager) {
        Log.d(TAG, "getStockOutput");
        String picFormat = settingsManager.get(SettingKeys.PICTURE_FORMAT).get();
        Output output = new Output();
        String picSize = settingsManager.get(SettingKeys.PICTURE_SIZE).get();
        Size largestImageSize = Collections.max(
                Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.JPEG)),
                new CameraHolderApi2.CompareSizesByArea());

        String[] split = picSize.split("x");
        if (split.length < 2) {
            output.jpeg_width = largestImageSize.getWidth();
            output.jpeg_height = largestImageSize.getHeight();
        } else {
            output.jpeg_width = Integer.parseInt(split[0]);
            output.jpeg_height = Integer.parseInt(split[1]);
        }


        if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_dng16))
                || picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_jpg_p_dng))
                || picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_bayer))) {
            Log.d(TAG, "ImageReader RAW_SENSOR");
            if (settingsManager.get(SettingKeys.RAW_SIZE).isSupported())
            {
                String[] splitraw = settingsManager.get(SettingKeys.RAW_SIZE).get().split("x");
                output.raw_width = Integer.parseInt(splitraw[0]);
                output.raw_height = Integer.parseInt(splitraw[1]);
            }
            else {
                largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW_SENSOR)), new CameraHolderApi2.CompareSizesByArea());
                output.raw_width = largestImageSize.getWidth();
                output.raw_height = largestImageSize.getHeight();

            }
            output.raw_format = ImageFormat.RAW_SENSOR;
        } else if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_dng10))
        | picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_bayer10))) {
            Log.d(TAG, "ImageReader RAW10");
            if (settingsManager.get(SettingKeys.RAW_SIZE).isSupported())
            {
                String[] splitraw = settingsManager.get(SettingKeys.RAW_SIZE).get().split("x");
                output.raw_width = Integer.parseInt(splitraw[0]);
                output.raw_height = Integer.parseInt(splitraw[1]);
            }
            else {
                largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW10)), new CameraHolderApi2.CompareSizesByArea());
                output.raw_width = largestImageSize.getWidth();
                output.raw_height = largestImageSize.getHeight();

            }
            output.raw_format = ImageFormat.RAW10;

        } else if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_dng12))) {
            Log.d(TAG, "ImageReader RAW12");
            largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW12)), new CameraHolderApi2.CompareSizesByArea());
            output.raw_width = largestImageSize.getWidth();
            output.raw_height = largestImageSize.getHeight();
            output.raw_format = ImageFormat.RAW12;
        } else {
            output.raw_format =0;
            output.raw_width = 0;
            output.raw_height = 0;
        }
        Log.d(TAG, "Final stock output" + output);
        return output;
    }
}
