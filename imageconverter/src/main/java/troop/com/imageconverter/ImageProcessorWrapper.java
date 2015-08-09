package troop.com.imageconverter;

import android.graphics.Bitmap;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by troop on 08.08.2015.
 */
public class ImageProcessorWrapper
{
    static
    {
        System.loadLibrary("imageconverter");
    }

    private static native ByteBuffer INIT();
    private static native void YUVtoRGB(byte data[], int width, int height);
    private static native Bitmap GetBitmap();
    private static native void Release();
    private static native int[] GetRgbData();
    private static native int[][] GetHistogram();
    private static native void ApplyHighPassFilter();


    public void ProcessFrame(byte[]data, int width, int height)
    {
        Log.d(ImageProcessorWrapper.class.getSimpleName(), "YuvSize:" + data.length);
        YUVtoRGB(data, width, height);
    }

    public int[] GetPixelData()
    {
        return GetRgbData();
    }
    public Bitmap GetNativeBitmap()
    {
        return GetBitmap();
    }

    public void ReleaseNative()
    {
        Release();
    }
    public void Init()
    {
        INIT();
    }

    public int[][] GetHistogramData()
    {
        return GetHistogram();
    }
    public void ApplyHPF()
    {
        ApplyHighPassFilter();
    }
}
