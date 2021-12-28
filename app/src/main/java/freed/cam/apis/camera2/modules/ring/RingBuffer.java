package freed.cam.apis.camera2.modules.ring;

import java.util.ArrayDeque;

public abstract class RingBuffer<T> {
    public static final int buffer_size = 20;
    protected final ArrayDeque<T> ringbuffer;

    public RingBuffer()
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

    public T pollFirst()
    {
        return ringbuffer.pollFirst();
    }

    public T pollLast()
    {
        return ringbuffer.pollLast();
    }
}
