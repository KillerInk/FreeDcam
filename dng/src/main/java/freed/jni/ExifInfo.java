package freed.jni;

import java.nio.ByteBuffer;

/**
 * Created by KillerInk on 01.03.2018.
 */

public class ExifInfo {

    private ByteBuffer byteBuffer;

    static
    {
        System.loadLibrary("freedcam");
    }

    private native ByteBuffer init();
    private native void clear(ByteBuffer byteBuffer);
    private native void SetIso(ByteBuffer byteBuffer,int iso);
    private native void SetFlash(ByteBuffer byteBuffer,int flash);
    private native void SetExposureTime(ByteBuffer byteBuffer,double expotime);
    private native void SetFocalLength(ByteBuffer byteBuffer,float focal);
    private native void SetFnumber(ByteBuffer byteBuffer,float fnum);
    private native void SetExposureIndex(ByteBuffer byteBuffer,float expoindex);
    private native void SetImageDescription(ByteBuffer byteBuffer,String imgdesc);
    private native void SetOrientation(ByteBuffer byteBuffer,String orientation);

    public ExifInfo()
    {
        byteBuffer = init();
    }

    public ExifInfo(int iso, int flash, double expotime, float focal, float fnum, float expoindex, String imgdesc, String orientation)
    {
        this();
        if (byteBuffer == null)
            return;
        SetIso(byteBuffer, iso);
        SetFlash(byteBuffer, flash);
        SetExposureTime(byteBuffer, expotime);
        SetFocalLength(byteBuffer, focal);
        SetFnumber(byteBuffer, fnum);
        SetExposureIndex(byteBuffer, expoindex);
        SetImageDescription(byteBuffer, imgdesc);
        SetOrientation(byteBuffer, orientation);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (byteBuffer == null)
            return;
        clear(byteBuffer);
        byteBuffer = null;
    }

    public ByteBuffer getByteBuffer()
    {
        return byteBuffer;
    }
}
