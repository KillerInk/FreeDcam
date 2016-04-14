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
    private static native byte[] SetJpegData(ByteBuffer nativeHandler,byte[] fileBytes, int width,int height);
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

    public byte[] SetJpegData(final byte[] fileBytes, int width,int height) throws NullPointerException
    {
        if (fileBytes == null) {
            throw new NullPointerException();
        }
        if (nativeHandler != null) {
           return SetJpegData(nativeHandler, fileBytes, width, height);

        }
        else
        {
            return null;
        }
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
