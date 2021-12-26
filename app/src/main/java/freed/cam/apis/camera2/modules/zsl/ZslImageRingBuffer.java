package freed.cam.apis.camera2.modules.zsl;

import android.media.Image;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class ZslImageRingBuffer extends ZslRingBuffer<Image>
{
    public ZslImageRingBuffer()
    {
        super();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addImage(Image img)
    {
        if (ringbuffer.size() >= buffer_size-1) {
            ringbuffer.removeLast().close();
        }
        ringbuffer.addFirst(img);
    }

}
