package freed.cam.apis.featuredetector.camera2.debug;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import java.util.Arrays;

import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.utils.Log;

public class DumpScalerStreamConfigurationMap extends BaseParameter2Detector {

    private final String TAG = DumpScalerStreamConfigurationMap.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        dump_SCALER_STREAM_CONFIGURATION_MAP(cameraCharacteristics);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void dump_SCALER_STREAM_CONFIGURATION_MAP(CameraCharacteristics characteristics)
    {
        StreamConfigurationMap smap =  characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        int[] outputformats =  smap.getOutputFormats();
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
}
