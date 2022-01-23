package freed.cam.apis.camera2.modules.ring;

import android.media.Image;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.NoSuchElementException;

import freed.utils.Log;

public class ImageRingBuffer extends RingBuffer<Image>
{
    public ImageRingBuffer()
    {
        super();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addImage(Image img)
    {
        try
        {
            if (ringbuffer.size() >= buffer_size-1)
                ringbuffer.removeLast().close();
        }
        catch (NoSuchElementException ex)
        {
            Log.WriteEx(ex);
        }

        ringbuffer.addFirst(img);
    }
}
