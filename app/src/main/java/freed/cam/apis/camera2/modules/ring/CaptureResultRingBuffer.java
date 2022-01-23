package freed.cam.apis.camera2.modules.ring;

import android.hardware.camera2.TotalCaptureResult;

import java.util.NoSuchElementException;

import freed.utils.Log;

public class CaptureResultRingBuffer extends RingBuffer<TotalCaptureResult> {
    public void addCaptureResult(TotalCaptureResult result)
    {
        try
        {
            if (ringbuffer.size() >= buffer_size-1) {
                ringbuffer.removeLast();
            }
        }
        catch (NoSuchElementException ex)
        {
            Log.WriteEx(ex);
        }
        ringbuffer.addFirst(result);
    }
}
