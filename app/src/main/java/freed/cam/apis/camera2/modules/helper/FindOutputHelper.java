package freed.cam.apis.camera2.modules.helper;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.util.Size;

import com.huawei.camera2ex.CameraCharacteristicsEx;
import com.troop.freedcam.R;

import java.util.Arrays;
import java.util.Collections;

import freed.cam.apis.camera2.CameraHolderApi2;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FindOutputHelper
{
    private final   String TAG = FindOutputHelper.class.getSimpleName();
    public Output getHuaweiOutput(CameraHolderApi2 cameraHolder) {
        String picFormat = SettingsManager.get(SettingKeys.PictureFormat).get();
        Output output =  new Output();
        if (SettingsManager.get(SettingKeys.dualPrimaryCameraMode).isSupported())
        {
            try {
                if (CameraCharacteristicsEx.HUAWEI_RAW_FORMAT != null && cameraHolder.characteristics.get(CameraCharacteristicsEx.HUAWEI_RAW_FORMAT) != null)
                    output.raw_format = cameraHolder.characteristics.get(CameraCharacteristicsEx.HUAWEI_RAW_FORMAT).intValue();
                else
                    output.raw_format = ImageFormat.RAW_SENSOR;
            }
            catch (IllegalArgumentException | NullPointerException ex)
            {
                Log.WriteEx(ex);
                output.raw_format = ImageFormat.RAW_SENSOR;
            }


            Log.d(TAG, "Use RawFormat: " + output.raw_format);

            String camera = SettingsManager.get(SettingKeys.dualPrimaryCameraMode).get();
            Log.d(TAG, "sencodary sensor size supported: " + SettingsManager.get(SettingKeys.secondarySensorSize).isSupported());
            //handel the first cam or the case that the second sensor has same size as first
            if (camera.equals(SettingsManager.getInstance().getResString(R.string.hw_dualcamera_Primary)) || camera.equals(SettingsManager.getInstance().getResString(R.string.hw_dualcamera_Dual))) {
                if (output.raw_format == ImageFormat.RAW_SENSOR && !SettingsManager.get(SettingKeys.secondarySensorSize).isSupported())
                    return getStockOutput(cameraHolder);
                else
                {
                    String picSize = SettingsManager.get(SettingKeys.PictureSize).get();
                    String[] split = picSize.split("x");
                    output.jpeg_width = Integer.parseInt(split[0]);
                    output.jpeg_height = Integer.parseInt(split[1]);
                    Log.d(TAG, "Jpeg size: " + picSize);
                    if (picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_dng16))
                            || picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_jpg_p_dng))
                            || picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_bayer))) {
                        Rect subsize = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
                        output.raw_width = subsize.width();
                        output.raw_height = subsize.height();
                        Log.d(TAG, "raw size: " + output.raw_width +"x" + output.raw_height);
                    }
                    else {
                        output.raw_format = 0;
                        output.raw_width =0;
                        output.raw_height = 0;
                    }
                }
            }
            else if (camera.equals(SettingsManager.getInstance().getResString(R.string.hw_dualcamera_Secondary)))
            {
                if (output.raw_format == ImageFormat.RAW_SENSOR && !SettingsManager.get(SettingKeys.secondarySensorSize).isSupported())
                    return getStockOutput(cameraHolder);
                else {
                    String picSize = SettingsManager.get(SettingKeys.secondarySensorSize).get();
                    String[] split = picSize.split("x");
                    output.jpeg_width = Integer.parseInt(split[0]);
                    output.jpeg_height = Integer.parseInt(split[1]);

                    if (picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_dng16))
                            || picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_jpg_p_dng))
                            || picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_bayer))) {
                        Log.d(TAG, "ImageReader RAW_SENSOR");
                        int[] subsize = cameraHolder.characteristics.get(CameraCharacteristicsEx.HUAWEI_SENCONDARY_SENSOR_PIXEL_ARRAY_SIZE);
                        if (subsize != null) {
                            if (subsize.length > 2) {
                                output.raw_width = subsize[2];
                                output.raw_height = subsize[3];
                            } else {
                                output.raw_width = subsize[0];
                                output.raw_height = subsize[1];
                            }
                            Log.d(TAG, "raw size: " + output.raw_width + "x" + output.raw_height);
                        } else {
                            Size largestImageSize = Collections.max(
                                    Arrays.asList(cameraHolder.map.getOutputSizes(output.raw_format)),
                                    new CameraHolderApi2.CompareSizesByArea());
                            output.raw_width = largestImageSize.getWidth();
                            output.raw_height = largestImageSize.getHeight();
                            Log.d(TAG, "raw size: " + output.raw_width + "x" + output.raw_height);
                        }
                    }
                }
            }
        }
        else
            return getStockOutput(cameraHolder);
        return output;
    }

    public Output getStockOutput(CameraHolderApi2 cameraHolder) {
        String picFormat = SettingsManager.get(SettingKeys.PictureFormat).get();
        Output output = new Output();
        String picSize = SettingsManager.get(SettingKeys.PictureSize).get();
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


        if (picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_dng16))
                || picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_jpg_p_dng))
                || picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_bayer))) {
            Log.d(TAG, "ImageReader RAW_SENSOR");
            largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW_SENSOR)), new CameraHolderApi2.CompareSizesByArea());
            output.raw_width = largestImageSize.getWidth();
            output.raw_height = largestImageSize.getHeight();
            output.raw_format = ImageFormat.RAW_SENSOR;

        } else if (picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_dng10))) {
            Log.d(TAG, "ImageReader RAW10");
            largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW10)), new CameraHolderApi2.CompareSizesByArea());
            output.raw_width = largestImageSize.getWidth();
            output.raw_height = largestImageSize.getHeight();
            output.raw_format = ImageFormat.RAW10;

        } else if (picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_dng12))) {
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
        return output;
    }
}
