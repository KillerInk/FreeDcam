package freed.cam.previewpostprocessing;

import android.graphics.SurfaceTexture;
import android.view.View;

import freed.cam.histogram.HistogramFeed;

public interface Preview
{

    interface PreviewEvent
    {
        void onPreviewAvailable(SurfaceTexture surface, int width, int height);
        void onPreviewSizeChanged(SurfaceTexture surface, int width, int height);
        boolean onPreviewDestroyed(SurfaceTexture surface);
        void onPreviewUpdated(SurfaceTexture surface);
    }
    SurfaceTexture getSurfaceTexture();
    void setSize(int width, int height);
    void setBlue(boolean blue);
    void setRed(boolean red);
    void setGreen(boolean green);
    void setFocusPeak(boolean on);
    boolean isFocusPeak();
    void setClipping(boolean on);
    boolean isClipping();
    void setHistogram(boolean on);
    boolean isHistogram();
    void setColorWaveForm(boolean on);
    boolean isColorWaveForm();
    void start();
    void stop();
    View getPreviewView();
    void setPreviewEventListner(PreviewEvent eventListner);
    int getViewWidth();
    int getViewHeight();
    int getPreviewWidth();
    int getPreviewHeight();
    void setRotation(int width, int height,int rotation);
    void setHistogramFeed(HistogramFeed feed);
    void clear();
    void setZebraHigh(float high);
    void setZebraLow(float low);
}
