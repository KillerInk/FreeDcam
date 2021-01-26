package camera2_hidden_keys.lg;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.AbstractCaptureRequest;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureRequestLg extends AbstractCaptureRequest {

    public static final CaptureRequest.Key<Byte> KEY_EIS_END_STREAM;

    static {
        KEY_EIS_END_STREAM = getKeyType("org.quic.camera.recording.endOfStream", Byte.class);
    }


}
