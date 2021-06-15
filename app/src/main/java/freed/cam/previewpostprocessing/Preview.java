package freed.cam.previewpostprocessing;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.View;

import freed.cam.histogram.HistogramFeed;

public interface Preview
{

    public interface PreviewEvent
    {
        void onPreviewAvailable(SurfaceTexture surface, int width, int height);
        void onPreviewSizeChanged(SurfaceTexture surface, int width, int height);
        boolean onPreviewDestroyed(SurfaceTexture surface);
        void onPreviewUpdated(SurfaceTexture surface);
    }
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
    View getPreviewView();
    void setPreviewEventListner(PreviewEvent eventListner);
    int getPreviewWidth();
    int getPreviewHeight();
    void setRotation(int width, int height,int rotation);
    void setHistogramFeed(HistogramFeed feed);
    void clear();
}
