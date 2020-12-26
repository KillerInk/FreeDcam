package camera2_hidden_keys.mtk;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.AbstractCaptureRequest;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureRequestMtk extends AbstractCaptureRequest {

    public static final CaptureRequest.Key<Byte> CONTROL_CAPTURE_ISP_META_REQUEST;
    public static final byte CONTROL_CAPTURE_ISP_TUNING_DATA_BUFFER = 2;
    public static final byte CONTROL_CAPTURE_ISP_TUNING_DATA_IN_METADATA = 1;
    public static final byte CONTROL_CAPTURE_ISP_TUNING_DATA_NONE = 0;
    public static final byte CONTROL_CAPTURE_ISP_TUNING_REQ_RAW = 1;
    public static final byte CONTROL_CAPTURE_ISP_TUNING_REQ_YUV = 2;

    static {

        CONTROL_CAPTURE_ISP_META_REQUEST = getKeyType("com.mediatek.control.capture.ispTuningRequest", Byte.class);
    }
}
