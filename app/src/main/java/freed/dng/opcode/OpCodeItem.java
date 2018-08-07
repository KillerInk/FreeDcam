package freed.dng.opcode;

public class OpCodeItem {

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
}
