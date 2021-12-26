package freed.cam.apis.camera2.modules.zsl;

import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;

public class ZslCaptureResultRingBuffer extends ZslRingBuffer<TotalCaptureResult>{
    public void addCaptureResult(TotalCaptureResult result)
    {
        if (ringbuffer.size() >= buffer_size-1) {
            ringbuffer.removeLast();
        }
        ringbuffer.addFirst(result);
    }
}
