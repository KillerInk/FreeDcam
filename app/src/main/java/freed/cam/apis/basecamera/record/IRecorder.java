package freed.cam.apis.basecamera.record;

import android.view.Surface;

public interface IRecorder {
    Surface getSurface();
    boolean prepare();
    void start();
    void stop();
    void release();
}
