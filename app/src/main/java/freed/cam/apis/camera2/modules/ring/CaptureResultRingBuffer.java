package freed.cam.apis.camera2.modules.ring;

import android.hardware.camera2.TotalCaptureResult;

public class CaptureResultRingBuffer extends RingBuffer<TotalCaptureResult> {
    public void addCaptureResult(TotalCaptureResult result)
    {
        if (ringbuffer.size() >= buffer_size-1) {
            ringbuffer.removeLast();
        }
        ringbuffer.addFirst(result);
    }
}
