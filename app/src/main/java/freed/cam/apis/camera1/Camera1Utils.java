package freed.cam.apis.camera1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import freed.cam.apis.basecamera.Size;

public class Camera1Utils {

    public static Size getOptimalPreviewSize(List<Size> sizes, int w, int h, boolean FocusPeakClamp) {
        double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;
        Size optimalSize = null;
        List<Size> aspectRatioMatches = new ArrayList<>();
        double ratio;
        for (Size s : sizes)
        {
            ratio = (double) s.width / s.height;
            if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE) {
                if (s.width <= 2560 && s.height <= 1440 && s.width >= 800 && s.height >= 600)
                    aspectRatioMatches.add(s);
            }
        }

        if (aspectRatioMatches.size() > 0)
        {
            return Collections.max(aspectRatioMatches, new SizeCompare());
        }
        else
            return Collections.max(sizes,new SizeCompare());
    }

    private static class SizeCompare implements Comparator<Size>
    {
        @Override
        public int compare(Size o1, Size o2) {
            int calc = -1;
            if (o1.width > o2.width)
                calc++;
            if (o1.height > o2.height)
                calc++;
            return calc;
        }
    }
}
