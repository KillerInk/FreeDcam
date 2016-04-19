package jni.staxxer;

import com.troop.filelogger.Logger;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by GeorgeKiarie on 14/04/2016.
 */
public class StaxxerJNI {

    static
    {
        System.loadLibrary("Staxxer");
    }
    private ByteBuffer nativeHandler = null;
    private static native void StoreMerged(ByteBuffer nativeHandler,byte[] fromRS);
    private static native byte[] GetMerged(ByteBuffer nativeHandler);
    private static native byte[] GetRGB(byte[] fromCamera);
    private static native ByteBuffer Create();
    private static native void Release(ByteBuffer nativeHandler);

    private StaxxerJNI()
    {
        if (nativeHandler != null) {
            Release(nativeHandler);
            nativeHandler = null;
        }

        nativeHandler = Create();
    }

    public static StaxxerJNI GetInstance()
    {
        return new StaxxerJNI();
    }

    public byte[] ExtractRGB(byte[] fromCamera) throws NullPointerException
    {

        if (nativeHandler != null) {
           return GetRGB(fromCamera);

        }
        else
        {
            return null;
        }
    }

    public void StoreMerged(byte[] fromRS)
    {
        if(nativeHandler != null) {
            StoreMerged(nativeHandler, fromRS);
        }

    }

    public byte[] GetMerged()
    {
        if(nativeHandler != null) {
           return GetMerged(nativeHandler);
        }
        else
            return null;
    }

    public void RELEASE()
    {
        if (nativeHandler !=null)
        {
            Release(nativeHandler);
            nativeHandler = null;
        }
    }

}
