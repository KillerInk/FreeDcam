package troop.com.imageconverter;

import android.graphics.Bitmap;
import android.view.Surface;

import java.nio.ByteBuffer;

/**
 * Created by troop on 08.08.2015.
 */
public class ImageProcessorWrapper
{
    public final static int RGB= 3;
    public final static int ARGB = 4;
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
    private native static void loadJPEGtoARGB(ByteBuffer nativeHandler, String path);
    private native static void loadJPEGtoRGB(ByteBuffer nativeHandler, String path);
    private native static void unpackRAWtoRGB(ByteBuffer nativeHandler, String path);
    private native static void unpackRAWtoARGB(ByteBuffer nativeHandler, String path);


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


    public void loadFile(int colorchannels, String path)
    {
        if(nativeHandler == null)
            return;
        if (RGB == colorchannels && path.endsWith("jpg"))
            loadJPEGtoRGB(nativeHandler, path);
        else if (RGB == colorchannels && (path.endsWith("dng") || path.endsWith(".raw") || path.endsWith(".bayer")))
            unpackRAWtoRGB(nativeHandler, path);
        else if (ARGB == colorchannels && path.endsWith("jpg"))
            loadJPEGtoARGB(nativeHandler, path);
        else if (ARGB == colorchannels && (path.endsWith("dng") || path.endsWith(".raw") || path.endsWith(".bayer")))
            unpackRAWtoARGB(nativeHandler, path);
    }

    public void ReleaseNative()
    {
        Release(nativeHandler);
        nativeHandler = null;
    }
}
