package freed.cam.apis.camera2.modules.zsl;

import android.media.Image;

import java.util.ArrayDeque;

public abstract class ZslRingBuffer<T> {
    public static final int buffer_size = 10;
    protected final ArrayDeque<T> ringbuffer;

    public ZslRingBuffer()
    {
        ringbuffer = new ArrayDeque<>(buffer_size+1);
    }

    public void remove(T img)
    {
        ringbuffer.remove(img);
    }

    public void clear()
    {
        ringbuffer.clear();
    }

    public T getLatest()
    {
        return ringbuffer.getFirst();
    }
}
