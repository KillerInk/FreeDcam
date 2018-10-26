package freed.dng.opcode;

import android.graphics.Point;
import android.graphics.Rect;

import java.io.IOException;
import java.nio.ByteBuffer;

public class FixBadPixelsList extends OpCodeItem
{

    public static final int FixBadPixelSize = 3*4;
    int bayerphase;
    int badpointcount = 0;
    int badrectcount = 0;
    Point badpoints[];
    Rect badrect[];

    FixBadPixelsList(int cfa, Point[] hotpixel)
    {
        opcodeid = 5;
        dngversion = DNGVERSION;
        qualityprocessing = 1;
        bayerphase = cfa;
        badpoints = hotpixel;
    }

    @Override
    public int size()
    {
        return super.size() + FixBadPixelSize + badpoints.length * 2 * 4;
    }

    @Override
    public void write(ByteBuffer byteBuffer) throws IOException {
        size_of_bytes = FixBadPixelSize + (badpoints.length*2);
        byteBuffer.putInt(opcodeid);
        byteBuffer.putInt(dngversion);
        byteBuffer.putInt(qualityprocessing);
        byteBuffer.putInt(size_of_bytes);
        for (int i = 0; i< badpoints.length;i++)
        {
            byteBuffer.putInt(badpoints[i].x);
            byteBuffer.putInt(badpoints[i].y);
        }

    }
}
