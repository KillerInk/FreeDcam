package troop.com.imageconverter;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

/**
 * Created by troop on 08.08.2015.
 */
public class ImageProcessor
{
    static
    {
        System.loadLibrary("imageconverter");
    }

    private static native ByteBuffer INIT();
    private static native void YUVtoRGB(byte data[], int width, int height);
    private static native Bitmap GetBitmap();
    private static native void Release();


    public void ProcessFrame(byte[]data, int width, int height)
    {
        YUVtoRGB(data, width, height);
    }

    public Bitmap GetNativeBitmap()
    {
        return GetBitmap();
    }

    public void ReleaseNative()
    {
        Release();
    }
}
