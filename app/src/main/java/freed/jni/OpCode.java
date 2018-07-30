package freed.jni;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import freed.utils.Log;
import freed.utils.StringUtils;

public class OpCode
{

    private final String TAG = OpCode.class.getSimpleName();

    static
    {
        System.loadLibrary("freedcam");
    }

    private native ByteBuffer init();
    private native void setOp2(ByteBuffer buffer ,byte[] op2);
    private native void setOp3(ByteBuffer buffer ,byte[] op3);
    private native void clear(ByteBuffer buffer);

    private ByteBuffer byteBuffer;

    public OpCode(File op2, File op3)
    {
        Log.d(TAG,"init");
        byteBuffer = init();
        if (op2.exists()) {
            try {
                Log.d(TAG,"load op2");
                setOp2(byteBuffer,RawToDng.readFile(op2));
                Log.d(TAG,"load op2 done");
            } catch (IOException e) {
                Log.WriteEx(e);
            }
        }
        if (op3.exists()) {
            try {
                Log.d(TAG,"load op3");
                setOp3(byteBuffer,RawToDng.readFile(op3));
                Log.d(TAG,"load op3 done");
            } catch (IOException e) {
                Log.WriteEx(e);
            }
        }
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
