package freed.cam.apis.camera2.modules.ring;

import android.media.Image;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.NoSuchElementException;

import freed.utils.Log;

public class ImageRingBuffer extends RingBuffer<Image>
{

    public ImageRingBuffer(int buffer_size) {
        super(buffer_size);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void offerFirst(Image t)
    {
        synchronized (LOCK)
        {
            if (current_buffer_size +1 > buffer_size) {
                Image tt = ringbuffer.pollLast();
                if (tt != null) {
                    tt.close();
                    current_buffer_size--;
                }
            }
            try {
                ringbuffer.addFirst(t);
                current_buffer_size++;
            }
            catch (NullPointerException ex)
            {
                Log.WriteEx(ex);
            }

        }
        synchronized (this)
        {
            this.notifyAll();
        }

    }
}
