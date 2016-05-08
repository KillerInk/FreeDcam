package com.imageconverter;

import android.graphics.Bitmap;
import android.view.Surface;

import java.nio.ByteBuffer;

/**
 * Created by troop on 08.08.2015.
 */
class ImageProcessorWrapper
{
    private final static int RGB= 3;
    public final static int ARGB = 4;
    ImageProcessorWrapper()
    {
        this.nativeHandler = INIT();
    }

    static
    {
        System.loadLibrary("freedcam");
    }

    private ByteBuffer nativeHandler = null;

    private native ByteBuffer INIT();
    private native void DrawToSurface(ByteBuffer nativeHandler, Surface view);
    private native void DrawToBitmap(ByteBuffer nativeHandler, Bitmap bitmap);
    private native void YUVtoRGB(ByteBuffer nativeHandler,byte data[], int width, int height);
    private native Bitmap GetBitmap(ByteBuffer nativeHandler);
    private native void Release(ByteBuffer nativeHandler);
    private native int[] GetRgbData(ByteBuffer nativeHandler);
    private native int[][] GetHistogram(ByteBuffer nativeHandler);
    private native void ApplyHighPassFilter(ByteBuffer nativeHandler);
    private native void loadJPEGtoARGB(ByteBuffer nativeHandler, String path);
    private native void loadJPEGtoRGB(ByteBuffer nativeHandler, String path);
    private native void unpackRAWtoRGB(ByteBuffer nativeHandler, String path);
    private native void unpackRAWtoARGB(ByteBuffer nativeHandler, String path);
    private native void stackAverageJPEGtoARGB(ByteBuffer nativeHandler, String path);


    private int width;
    private int height;

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


    public void loadFile(String path)
    {
        if(nativeHandler == null)
            return;
        if (RGB == ImageProcessorWrapper.ARGB && path.endsWith("jpg"))
            loadJPEGtoRGB(nativeHandler, path);
        else if (RGB == ImageProcessorWrapper.ARGB && (path.endsWith("dng") || path.endsWith(".raw") || path.endsWith(".bayer")))
            unpackRAWtoRGB(nativeHandler, path);
        else if (ARGB == ImageProcessorWrapper.ARGB && path.endsWith("jpg"))
            loadJPEGtoARGB(nativeHandler, path);
        else if (ARGB == ImageProcessorWrapper.ARGB && (path.endsWith("dng") || path.endsWith(".raw") || path.endsWith(".bayer")))
            unpackRAWtoARGB(nativeHandler, path);
    }

    public void ReleaseNative()
    {
        Release(nativeHandler);
        nativeHandler = null;
    }
}
