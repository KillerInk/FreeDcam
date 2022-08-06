package freed.cam.apis.camera2.modules.ring;

import java.util.ArrayDeque;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class RingBuffer<T> {
    public int buffer_size = 30;
    protected volatile int current_buffer_size = 0;
    protected final ArrayDeque<T> ringbuffer;
    protected final Object LOCK = new Object();

    public RingBuffer(int buffer_size)
    {
        this.buffer_size = buffer_size;
        ringbuffer = new ArrayDeque<>(buffer_size);
    }

    public int getCurrent_buffer_size() {
        return current_buffer_size;
    }

    public void clear()
    {
        ringbuffer.clear();
        current_buffer_size = 0;
    }

    public T pollLast() {

        T t = null;
        synchronized (LOCK)
        {
            t = ringbuffer.pollLast();
            if (t != null)
                current_buffer_size--;
        }
        return t;
    }

    public void offerFirst(T t)
    {
        if (ringbuffer == null)
            return;
        synchronized (LOCK)
        {
            if (current_buffer_size +1 > buffer_size) {
                T tt = ringbuffer.pollLast();
                if (tt != null) {
                    drop(tt);
                    current_buffer_size--;
                }
            }
            ringbuffer.addFirst(t);
            current_buffer_size++;
        }
        synchronized (this)
        {
            this.notifyAll();
        }

    }

    public int size()
    {
        return ringbuffer.size();
    }

    public abstract void drop(T tt);
}
