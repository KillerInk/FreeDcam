package freed.dng.opcode;

import java.io.IOException;
import java.nio.ByteBuffer;

public class FixVignetteRadial extends OpCodeItem {

    public static int FIX_VIGNETTERADIAL_HEADER_BYTESIZE = 6*8;

    double k1;
    double k2;
    double k3;
    double k4;
    double cx; //center x = 0.5 1= left
    double cy;// center y =0.5  1 = top

    public FixVignetteRadial(double k1,double k2, double k3, double k4, double cx, double cy)
    {
        super();
        opcodeid = 3;
        dngversion = DNGVERSION;
        qualityprocessing = 1;
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
        this.k4 = k4;
        this.cx = cx;
        this.cy = cy;
    }

    @Override
    public int size() {
        return super.size() + FIX_VIGNETTERADIAL_HEADER_BYTESIZE;
    }

    @Override
    public void write(ByteBuffer byteBuffer) throws IOException {
        size_of_bytes = FIX_VIGNETTERADIAL_HEADER_BYTESIZE;
        byteBuffer.putInt(opcodeid);
        byteBuffer.putInt(dngversion);
        byteBuffer.putInt(qualityprocessing);
        byteBuffer.putInt(size_of_bytes);
        byteBuffer.putDouble(k1);
        byteBuffer.putDouble(k2);
        byteBuffer.putDouble(k3);
        byteBuffer.putDouble(k4);
        byteBuffer.putDouble(cx);
        byteBuffer.putDouble(cy);
    }
}
