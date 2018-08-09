package freed.dng.opcode;

import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.LensShadingMap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import freed.jni.OpCode;
import freed.utils.Log;

public class GainMap extends OpCodeItem {

    public static int GAINMAPHEADER_BYTESIZE = 11*4 + 4*8;

    int top;
    int left;
    int bottom;
    int right;
    int plane;
    int planes;
    int rowpitch;
    int colpitch;
    int map_pointsV;
    int map_pointsH;
    double map_spacingV;
    double map_spacingH;
    double mapOriginV;
    double mapOriginH;
    int mapPlanes;
    float map[];


    public GainMap(int top,
                   int left,
                   int bottom,
                   int right,
                   int plane,
                   int planes,
                   int rowpitch,
                   int colpitch,
                   int map_pointsV,
                   int map_pointsH,
            double map_spacingV,
            double map_spacingH,
            double mapOriginV,
            double mapOriginH,
                   int mapPlanes,
            float map[])
    {
        opcodeid = 9;
        dngversion = DNGVERSION;
        qualityprocessing = 1;
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
        this.plane = plane;
        this.planes = planes;
        this.rowpitch = rowpitch;
        this.colpitch = colpitch;
        this.map_pointsV = map_pointsV;
        this.map_pointsH = map_pointsH;
        this.map_spacingV = map_spacingV;
        this.map_spacingH = map_spacingH;
        this.mapOriginV = mapOriginV;
        this.mapOriginH = mapOriginH;
        this.mapPlanes = mapPlanes;
        this.map = map;
    }

    @Override
    public int size()
    {
        return super.size() + GAINMAPHEADER_BYTESIZE + map.length * 4;
    }

    public void write(ByteBuffer byteBuffer)  {
        size_of_bytes = GAINMAPHEADER_BYTESIZE + map.length * 4;
        byteBuffer.putInt(opcodeid);
        byteBuffer.putInt(dngversion);
        byteBuffer.putInt(qualityprocessing);
        byteBuffer.putInt(size_of_bytes);
        byteBuffer.putInt(top);
        byteBuffer.putInt(left);
        byteBuffer.putInt(bottom);
        byteBuffer.putInt(right);
        byteBuffer.putInt(plane);
        byteBuffer.putInt(planes);
        byteBuffer.putInt(rowpitch);
        byteBuffer.putInt(colpitch);
        byteBuffer.putInt(map_pointsV);
        byteBuffer.putInt(map_pointsH);
        byteBuffer.putDouble(map_spacingV);
        byteBuffer.putDouble(map_spacingH);
        byteBuffer.putDouble(mapOriginV);
        byteBuffer.putDouble(mapOriginH);
        byteBuffer.putInt(mapPlanes);
        for (int i = 0; i < map.length;i++){
            byteBuffer.putFloat(map[i]);
       }
    }
}
