package camera2_hidden_keys.qcom;

import android.hardware.camera2.CaptureResult;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.AbstractCaptureResult;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureResultQcom extends AbstractCaptureResult {
    public static final CaptureResult.Key<int[]> HISTOGRAM_STATS;

    static {
        HISTOGRAM_STATS = getKeyClass("org.codeaurora.qcamera3.histogram.stats", int[].class);
    }
}
