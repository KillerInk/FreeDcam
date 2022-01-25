package freed.cam.apis.camera2.modules.ring;

import android.hardware.camera2.TotalCaptureResult;

import java.util.NoSuchElementException;

import freed.utils.Log;

public class ByteRingBuffer extends RingBuffer<byte[]> {

    public ByteRingBuffer(int buffer_size) {
        super(buffer_size);
    }

}
