package freed.jni;

import java.nio.ByteBuffer;

import freed.dng.CustomMatrix;
import freed.dng.DngProfile;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class RawStack {

    private final String TAG = RawStack.class.getSimpleName();
    static
    {
        System.loadLibrary("freedcam");
    }

    private ByteBuffer byteBuffer;

    private native ByteBuffer init();
    private native void setBaseFrame(ByteBuffer buffer, byte[] fileBytes,int width, int heigt);
    private native void setBaseFrameBuffer(ByteBuffer buffer, ByteBuffer fileBytes,int width, int heigt);
    private native void stackFrame(ByteBuffer buffer, byte[] nextframe);
    private native void stackFrameBuffer(ByteBuffer buffer, ByteBuffer nextframe);
    private native void writeDng(ByteBuffer buffer, ByteBuffer dngprofile, ByteBuffer customMatrix, String outfile, ByteBuffer exifinfo);
    private native void writeJpeg(ByteBuffer buffer, ByteBuffer dngprofile, ByteBuffer customMatrix, String outfile, ByteBuffer exifinfo);
    private native void SetOpCode(ByteBuffer opcode,ByteBuffer byteBuffer);
    private native byte[] getOutput(ByteBuffer byteBuffer);
    private native void setUpShift(ByteBuffer byteBuffer, int upshift);
    private native void clear(ByteBuffer byteBuffer);

    public RawStack()
    {
        byteBuffer = init();
    }

    public synchronized void setShift(int shift)
    {
        if (byteBuffer != null)
            setUpShift(byteBuffer,shift);
    }

    public synchronized void setFirstFrame(byte[] bytes, int width, int height)
    {
        setBaseFrame(byteBuffer,bytes,width,height);
    }

    public synchronized void setFirstFrame(ByteBuffer bytes, int width, int height)
    {
        setBaseFrameBuffer(byteBuffer,bytes,width,height);
    }

    public synchronized void stackNextFrame(byte[] bytes)
    {
        stackFrame(byteBuffer,bytes);
    }

    public synchronized void stackNextFrame(ByteBuffer bytes)
    {
        stackFrameBuffer(byteBuffer,bytes);
    }

    public synchronized void saveDng(DngProfile profile, CustomMatrix customMatrix, String fileout, ExifInfo exifInfo)
    {
        if (SettingsManager.getInstance().getOpCode() != null) {
            Log.d(TAG, "setOpCode");
            SetOpCode(SettingsManager.getInstance().getOpCode().getByteBuffer(), byteBuffer);
        }
        writeDng(byteBuffer,profile.getByteBuffer(),customMatrix.getByteBuffer(),fileout,exifInfo.getByteBuffer());
        byteBuffer = null;
    }

    public synchronized void savePNG(DngProfile profile, CustomMatrix customMatrix, String fileout, ExifInfo exifInfo)
    {
        if (byteBuffer != null)
            writeJpeg(byteBuffer,profile.getByteBuffer(),customMatrix.getByteBuffer(),fileout,exifInfo.getByteBuffer());
    }

    public synchronized byte[] getOutputBuffer()
    {
        if (byteBuffer != null)
            return getOutput(byteBuffer);
        return null;
    }

    public void clear()
    {
        if (byteBuffer != null)
            clear(byteBuffer);
    }
}
