package troop.com.imageconverter;

import android.graphics.Bitmap;
import android.view.Surface;

import java.nio.ByteBuffer;

/**
 * Created by troop on 08.08.2015.
 */
public class ImageProcessorWrapper
{

    ImageProcessorWrapper()
    {
        this.nativeHandler = INIT();
    }

    static
    {
        System.loadLibrary("RSSupport");
        System.loadLibrary("imageconverter");
    }

    private ByteBuffer nativeHandler = null;

    private synchronized static native ByteBuffer INIT();
    private native void DrawToSurface(ByteBuffer nativeHandler, Surface view);
    private native void DrawToBitmap(ByteBuffer nativeHandler, Bitmap bitmap);
    private synchronized static native void YUVtoRGB(ByteBuffer nativeHandler,byte data[], int width, int height);
    private synchronized static native Bitmap GetBitmap(ByteBuffer nativeHandler);
    private synchronized static native void Release(ByteBuffer nativeHandler);
    private synchronized static native int[] GetRgbData(ByteBuffer nativeHandler);
    private synchronized static native int[][] GetHistogram(ByteBuffer nativeHandler);
    private synchronized static native void ApplyHighPassFilter(ByteBuffer nativeHandler);

    int width;
    int height;

    public void ProcessFrame(byte[]data, int width, int height)
    {
        this.width = width;
        this.height = height;
        //Logger.d(ImageProcessorWrapper.class.getSimpleName(), "YuvSize:" + data.length);
        YUVtoRGB(nativeHandler,data, width, height);
    }

    public int[] GetPixelData()
    {
        return GetRgbData(nativeHandler);
    }
    public Bitmap GetNativeBitmap() {
        return GetBitmap(nativeHandler);
    }
    public void Init()
    {
        INIT();
    }

    public void SetSurface(Surface surface) {
        DrawToSurface(nativeHandler, surface);
    }

    public void DrawToBitmapFromNative(Bitmap map)
    {
        DrawToBitmap(nativeHandler, map);
    }

    public int[][] GetHistogramData()
    {
        return GetHistogram(nativeHandler);
    }
    public void ApplyHPF()
    {
        ApplyHighPassFilter(nativeHandler);
    }

    public void ReleaseNative()
    {
        Release(nativeHandler);
        nativeHandler = null;
    }
}
