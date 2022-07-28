package freed.cam.apis.camera2.modules.ring;

import android.hardware.camera2.TotalCaptureResult;

import java.util.NoSuchElementException;

import freed.utils.Log;

public class CaptureResultRingBuffer extends RingBuffer<TotalCaptureResult> {
    public CaptureResultRingBuffer(int buffer_size) {
        super(buffer_size);
    }

    @Override
    public void drop(TotalCaptureResult tt) {

    }

}
