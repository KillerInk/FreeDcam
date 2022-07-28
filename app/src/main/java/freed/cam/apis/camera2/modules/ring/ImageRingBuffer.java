package freed.cam.apis.camera2.modules.ring;

import android.media.Image;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.NoSuchElementException;

import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ImageRingBuffer extends RingBuffer<Image>
{

    public ImageRingBuffer(int buffer_size) {
        super(buffer_size);
    }

    @Override
    public void drop(Image tt) {
        tt.close();
    }


    @Override
    public void clear() {
        Image img = null;
        while ((img = pollLast()) != null)
            img.close();
        current_buffer_size = 0;
    }
}
