package freed.dng.opcode;

import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.LensShadingMap;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class OpCodeCreator {

    final String TAG = OpCodeCreator.class.getSimpleName();


    public byte[] createOpCode2(CameraCharacteristics cameraCharacteristics, CaptureResult captureResult)
    {
        int size = 0;
        ArrayList<OpCodeItem> list = new ArrayList<>();
        int cfa = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT);

        LensShadingMap lensShadingMap = captureResult.get(CaptureResult.STATISTICS_LENS_SHADING_CORRECTION_MAP);
        if (lensShadingMap != null) {
            int lsWidth = lensShadingMap.getRowCount();
            int lsHeight = lensShadingMap.getColumnCount();
            Rect rect;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                rect = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PRE_CORRECTION_ACTIVE_ARRAY_SIZE);
            else
                rect = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
            int xmin = rect.left;
            int ymin = rect.top;
            int width = rect.right;
            int height = rect.bottom;


            GainMap[] gainMaps = addGainMapsForMetadata(lsWidth, lsHeight, xmin, ymin, width, height, lensShadingMap, cfa);
            for (int i = 0; i < gainMaps.length; i++) {
                list.add(gainMaps[i]);
            }
            size += gainMaps[0].size() * 4;
            Log.d(TAG, "opcode2 size = " + size);
        }

        Point hot[] = captureResult.get(CaptureResult.STATISTICS_HOT_PIXEL_MAP);
        if (hot != null && hot.length >0)
        {
            FixBadPixelsList fixBadPixelsList = new FixBadPixelsList(cfa,hot);
            if (fixBadPixelsList != null) {
                list.add(fixBadPixelsList);
                size += fixBadPixelsList.size();
            }
        }

        if (size > 0) {
            //increase size by 4 byte for the opcode list count
            size += 4;

            ByteBuffer buffer = ByteBuffer.allocate(size);
            buffer.order(ByteOrder.BIG_ENDIAN);

            try {
                //write opcode list count
                buffer.putInt(list.size());
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).write(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte ret[] = buffer.array().clone();

            try {
                buffer.clear();
                buffer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret;
        }
        return null;
    }

    GainMap[] addGainMapsForMetadata(int lsmWidth,
                                  int lsmHeight,
                                  int activeAreaTop,
                                  int activeAreaLeft,
                                  int activeAreaBottom,
                                  int activeAreaRight,
                                  LensShadingMap lensShadingMap,
                                  int cfa) {
        int activeAreaWidth = activeAreaRight - activeAreaLeft;
        int activeAreaHeight = activeAreaBottom - activeAreaTop;
        double spacingV = 1.0 / lsmHeight;
        double spacingH = 1.0 / lsmWidth;
        int size = lsmWidth * lsmHeight;
        float redMap[] = new float[size];
        float greenEvenMap[] = new float[size];
        float greenOddMap[] = new float[size];
        float blueMap[] = new float[size];

        int lsmMapSize = lsmWidth * lsmHeight * 4;
        // Split lens shading map channels into separate arrays
        int j = 0;
        for (int h = 0; h< lensShadingMap.getColumnCount();h++)
        {
            for (int w = 0; w< lensShadingMap.getRowCount(); w++)
            {
                redMap[j] = lensShadingMap.getGainFactor(0,h,w);
                greenEvenMap[j] = lensShadingMap.getGainFactor(1,h,w);
                greenOddMap[j] = lensShadingMap.getGainFactor(2,h,w);
                blueMap[j] = lensShadingMap.getGainFactor(3,h,w);
                j++;
            }
        }

        int redTop = 0;
        int redLeft = 0;
        int greenEvenTop = 0;
        int greenEvenLeft = 1;
        int greenOddTop = 1;
        int greenOddLeft = 0;
        int blueTop = 1;
        int blueLeft = 1;
        switch (cfa) {
            case 3://CFA_RGGB:
                redTop = 0;
                redLeft = 0;
                greenEvenTop = 0;
                greenEvenLeft = 1;
                greenOddTop = 1;
                greenOddLeft = 0;
                blueTop = 1;
                blueLeft = 1;
                break;
            case 0://CFA_GRBG:
                redTop = 0;
                redLeft = 1;
                greenEvenTop = 0;
                greenEvenLeft = 0;
                greenOddTop = 1;
                greenOddLeft = 1;
                blueTop = 1;
                blueLeft = 0;
                break;
            case 1://CFA_GBRG:
                redTop = 1;
                redLeft = 0;
                greenEvenTop = 0;
                greenEvenLeft = 0;
                greenOddTop = 1;
                greenOddLeft = 1;
                blueTop = 0;
                blueLeft = 1;
                break;
            case 2://CFA_BGGR:
                redTop = 1;
                redLeft = 1;
                greenEvenTop = 0;
                greenEvenLeft = 1;
                greenOddTop = 1;
                greenOddLeft = 0;
                blueTop = 0;
                blueLeft = 0;
                break;
            default:
                Log.d(TAG,"%s: Unknown CFA layout %d" + cfa);
                return null;
        }

        GainMap red = new GainMap(/*top*/redTop,
                /*left*/redLeft,
                /*bottom*/activeAreaHeight - 1,
                /*right*/activeAreaWidth - 1,
                /*plane*/0,
                /*planes*/1,
                /*rowPitch*/2,
                /*colPitch*/2,
                /*mapPointsV*/lsmHeight,
                /*mapPointsH*/lsmWidth,
                /*mapSpacingV*/spacingV,
                /*mapSpacingH*/spacingH,
                /*mapOriginV*/0,
                /*mapOriginH*/0,
                /*mapPlanes*/1,
                /*mapGains*/redMap);
        GainMap green_even = new GainMap(/*top*/greenEvenTop,
                /*left*/greenEvenLeft,
                /*bottom*/activeAreaHeight - 1,
                /*right*/activeAreaWidth - 1,
                /*plane*/0,
                /*planes*/1,
                /*rowPitch*/2,
                /*colPitch*/2,
                /*mapPointsV*/lsmHeight,
                /*mapPointsH*/lsmWidth,
                /*mapSpacingV*/spacingV,
                /*mapSpacingH*/spacingH,
                /*mapOriginV*/0,
                /*mapOriginH*/0,
                /*mapPlanes*/1,
                /*mapGains*/greenEvenMap);
        GainMap green_odd = new GainMap(/*top*/greenOddTop,
                /*left*/greenOddLeft,
                /*bottom*/activeAreaHeight - 1,
                /*right*/activeAreaWidth - 1,
                /*plane*/0,
                /*planes*/1,
                /*rowPitch*/2,
                /*colPitch*/2,
                /*mapPointsV*/lsmHeight,
                /*mapPointsH*/lsmWidth,
                /*mapSpacingV*/spacingV,
                /*mapSpacingH*/spacingH,
                /*mapOriginV*/0,
                /*mapOriginH*/0,
                /*mapPlanes*/1,
                /*mapGains*/greenOddMap);
        GainMap blue = new GainMap(/*top*/blueTop,
                /*left*/blueLeft,
                /*bottom*/activeAreaHeight - 1,
                /*right*/activeAreaWidth - 1,
                /*plane*/0,
                /*planes*/1,
                /*rowPitch*/2,
                /*colPitch*/2,
                /*mapPointsV*/lsmHeight,
                /*mapPointsH*/lsmWidth,
                /*mapSpacingV*/spacingV,
                /*mapSpacingH*/spacingH,
                /*mapOriginV*/0,
                /*mapOriginH*/0,
                /*mapPlanes*/1,
                /*mapGains*/blueMap);
        return new GainMap[]{red,green_even,green_odd,blue};
    }

}
