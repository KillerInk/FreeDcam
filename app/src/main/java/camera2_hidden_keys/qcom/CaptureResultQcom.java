package camera2_hidden_keys.qcom;

import android.hardware.camera2.CaptureResult;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.AbstractCaptureResult;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureResultQcom extends AbstractCaptureResult {

    /***
     * Returns histogram data in RGBA format. 4*256 = 1024
     *  int t = 0;
     *             for (int i = 0; i< histodata.length; i+=4)
     *             {
     *                 redHistogram[t] = histodata[i];
     *                 greenHistogram[t] = histodata[i+1];
     *                 blueHistogram[t] = histodata[i+2];
     *                 t++;
     *             }
     */
    public static final CaptureResult.Key<int[]> HISTOGRAM_STATS;

    static {
        HISTOGRAM_STATS = getKeyClass("org.codeaurora.qcamera3.histogram.stats", int[].class);
    }
}
