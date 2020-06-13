package camera2_hidden_keys.samsung;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.AbstractCaptureRequest;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureRequestSamsung extends AbstractCaptureRequest {

    public static final int CONTROL_TRANSIENT_ACTION_OFF = 0;
    public static final int CONTROL_TRANSIENT_ACTION_ZOOMING = 1;
    public static final int CONTROL_TRANSIENT_ACTION_ZOOMING_JUMP_TELE = 5;
    public static final int CONTROL_TRANSIENT_ACTION_ZOOMING_JUMP_UW = 3;
    public static final int CONTROL_TRANSIENT_ACTION_ZOOMING_JUMP_WIDE = 4;
    public static final CaptureRequest.Key<Integer> CONTROL_TRANSIENT_ACTION;

    static {
        CONTROL_TRANSIENT_ACTION = getKeyType("samsung.android.control.transientAction", Integer.TYPE);
    }
}
