package freed.dng.opcode;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class OpCodeItem {

    public static int DNGVERSION = 16973824;
    public static int OpCodeItemByteSize = 4*4;

    int opcodeid;
    int dngversion;
    int qualityprocessing;
    int size_of_bytes;

    public int size()
    {
        return OpCodeItemByteSize;
    }

    public abstract void write(ByteBuffer byteBuffer) throws IOException;
}
