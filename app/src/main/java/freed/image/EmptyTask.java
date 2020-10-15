package freed.image;

import com.troop.freedcam.image.ImageTask;

public class EmptyTask extends ImageTask {
    @Override
    public boolean process() {
        return false;
    }
}
