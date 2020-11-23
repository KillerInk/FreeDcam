package freed.jni;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;


public class LibRawJniWrapper
{
    private ByteBuffer byteBuffer;

    static
    {
        System.loadLibrary("freedcam");
    }

    private native ByteBuffer init();
    private native void openFile(ByteBuffer byteBuffer, String filename);
    private native void openFD(ByteBuffer byteBuffer, int fd);
    private native Bitmap getBitmap(ByteBuffer byteBuffer);
    private native void release(ByteBuffer byteBuffer);
    private native void getExifInfo(ByteBuffer byteBuffer, ByteBuffer exifInfo);

    public LibRawJniWrapper()
    {
        byteBuffer = init();
    }

    public void openFile(String filepath)
    {
        openFile(byteBuffer,filepath);
    }

    public void openFile(int filedecriptor)
    {
        openFD(byteBuffer,filedecriptor);
    }

    public Bitmap getBitmap()
    {
        return getBitmap(byteBuffer);
    }

    public Bitmap getBitmap(String filepath)
    {
        openFile(byteBuffer,filepath);
        Bitmap map = getBitmap(byteBuffer);
        release(byteBuffer);
        return map;
    }

    public Bitmap getBitmap(int filedescriptor)
    {
        openFD(byteBuffer,filedescriptor);
        Bitmap map = getBitmap(byteBuffer);
        release(byteBuffer);
        return map;
    }

    public void release()
    {
        release(byteBuffer);
    }

    public void getExifInfo(ExifInfo exifInfo)
    {
        getExifInfo(byteBuffer,exifInfo.getByteBuffer());
    }
}
