package freed.cam.previewpostprocessing;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;

import freed.views.AutoFitTextureView;

public interface Preview
{
    void setTextureView(TextureView autofitTextureView);
    void close();
    SurfaceTexture getSurfaceTexture();
    Surface getInputSurface();
    void setOutputSurface(Surface surface);
    void setSize(int width, int height);
    boolean isSucessfullLoaded();
    void setBlue(boolean blue);
    void setRed(boolean red);
    void setGreen(boolean green);
    void setFocusPeak(boolean on);
    boolean isFocusPeak();
    void setClipping(boolean on);
    boolean isClipping();
    void setHistogram(boolean on);
    boolean isHistogram();
    void start();
    void stop();
}
