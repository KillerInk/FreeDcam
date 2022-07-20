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

    //[0]
    public static final CaptureResult.Key<Integer> org_codeaurora_qcamera3_iso_exp_priority_select_priority;
    //[0.0]
    public static final CaptureResult.Key<Float> org_codeaurora_qcamera3_iso_exp_priority_use_gain_value;
    //[0]
    public static final CaptureResult.Key<Long> org_codeaurora_qcamera3_iso_exp_priority_use_iso_exp_priority;
    //[0]
    public static final CaptureResult.Key<Integer> org_codeaurora_qcamera3_iso_exp_priority_use_iso_value;

    static {
        HISTOGRAM_STATS = getKeyClass("org.codeaurora.qcamera3.histogram.stats", int[].class);
        org_codeaurora_qcamera3_iso_exp_priority_select_priority= getKeyType("org.codeaurora.qcamera3.iso_exp_priority.select_priority", int.class);
        org_codeaurora_qcamera3_iso_exp_priority_use_gain_value= getKeyType("org.codeaurora.qcamera3.iso_exp_priority.use_gain_value", float.class);
        org_codeaurora_qcamera3_iso_exp_priority_use_iso_exp_priority= getKeyType("org.codeaurora.qcamera3.iso_exp_priority.use_iso_exp_priority", long.class);
        org_codeaurora_qcamera3_iso_exp_priority_use_iso_value= getKeyType("org.codeaurora.qcamera3.iso_exp_priority.use_iso_value", int.class);
    }
}
