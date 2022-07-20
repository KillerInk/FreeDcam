package freed.cam.previewpostprocessing;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.View;

import freed.cam.event.BaseEventHandler;
import freed.cam.event.MyEvent;
import freed.cam.histogram.HistogramController;
import freed.cam.histogram.HistogramFeed;
import freed.utils.Log;

public class PreviewController implements PreviewControllerInterface
{
    public interface PreviewPostProcessingChangedEvent extends MyEvent {
        void onPreviewPostProcessingChanged();
    }
    public static class PreviewPostProcessingChangedEventHandler extends BaseEventHandler<PreviewPostProcessingChangedEvent>
    {
        public void fireOnPreviewPostProcessingChanged()
        {
            for (PreviewPostProcessingChangedEvent event : eventListners)
                event.onPreviewPostProcessingChanged();
        }
    }

    private static final String TAG = PreviewController.class.getSimpleName();
    private Preview preview;
    PreviewEvent eventListner;
    public PreviewPostProcessingChangedEventHandler previewPostProcessingChangedEventHandler;

    boolean blue = false;
    boolean red = false;
    boolean green = false;
    boolean focuspeak = false;
    boolean clipping = false;
    boolean showhistogram = false;
    boolean color_waveform = true;
    float zebrahigh = 0.001f;
    float zebralow = 0.01f;
    HistogramFeed feed;

    public PreviewController()
    {
        previewPostProcessingChangedEventHandler = new PreviewPostProcessingChangedEventHandler();
    }

    @Override
    public void initPreview(PreviewPostProcessingModes previewPostProcessingModes, Context context, HistogramController histogram)
    {
        Log.d(TAG, "init preview " +previewPostProcessingModes.name());
        switch (previewPostProcessingModes)
        {
            case off:
                preview = new NormalPreview(context);
                break;
            case OpenGL:
                preview = new OpenGLPreview(context,histogram);
                break;
        }
        preview.setPreviewEventListner(eventListner);
        preview.setBlue(blue);
        preview.setGreen(green);
        preview.setRed(red);
        preview.setClipping(clipping);
        preview.setFocusPeak(focuspeak);
        preview.setHistogram(showhistogram);
        preview.setZebraHigh(zebrahigh);
        preview.setZebraLow(zebralow);
        preview.setHistogramFeed(feed);
    }

    @Override
    public void setHistogramFeed(HistogramFeed feed) {
        Log.d(TAG,"setHistogramFeed");
        this.feed = feed;
        if (preview != null)
            this.preview.setHistogramFeed(feed);
    }

    @Override
    public void clear() {
        preview.clear();
    }

    public Preview getPreview() {
        return preview;
    }


    public SurfaceTexture getSurfaceTexture()
    {
        return preview.getSurfaceTexture();
    }

    @Override
    public void setSize(int width, int height) {
        preview.setSize(width,height);
    }

    @Override
    public void setBlue(boolean blue) {
        this.blue = blue;
        if (preview != null)
            preview.setBlue(blue);
    }

    @Override
    public void setRed(boolean red) {
        this.red = red;
        if (preview != null)
            preview.setRed(red);
    }

    @Override
    public void setGreen(boolean green) {
        this.green = green;
        if (preview != null)
            preview.setGreen(green);
    }


    @Override
    public void setFocusPeak(boolean on) {
        this.focuspeak = on;
        preview.setFocusPeak(on);
    }

    @Override
    public boolean isFocusPeak() {
        if (preview == null)
            return false;
        return preview.isFocusPeak();
    }

    @Override
    public void setClipping(boolean on) {
        this.clipping = on;
        if (preview != null)
            preview.setClipping(on);
    }

    @Override
    public boolean isClipping() {
        if (preview == null)
            return false;
        return preview.isClipping();
    }

    @Override
    public void setHistogram(boolean on) {
        this.showhistogram = on;
        if (preview != null)
            preview.setHistogram(on);
    }

    @Override
    public boolean isHistogram() {
        if (preview == null)
            return false;
        return preview.isHistogram();
    }

    @Override
    public void setColorWaveForm(boolean on) {
        if (preview == null)
            return;
        preview.setColorWaveForm(on);
    }

    @Override
    public boolean isColorWaveForm() {
        if (preview == null)
            return false;
        return preview.isColorWaveForm();
    }

    @Override
    public void start() {
        Log.d(TAG,"start");
        preview.start();
    }

    @Override
    public void stop() {
        Log.d(TAG,"stop");
        preview.stop();
    }

    @Override
    public View getPreviewView() {
        return preview.getPreviewView();
    }

    @Override
    public void setPreviewEventListner(PreviewEvent eventListner) {
        this.eventListner = eventListner;
        if (preview != null)
            preview.setPreviewEventListner(eventListner);
    }

    @Override
    public int getViewWidth() {
        return preview.getViewWidth();
    }

    @Override
    public int getViewHeight() {
        return preview.getViewHeight();
    }

    @Override
    public int getPreviewWidth() {
        return preview.getPreviewWidth();
    }

    @Override
    public int getPreviewHeight() {
        return preview.getPreviewHeight();
    }

    @Override
    public void setRotation(int width, int height, int rotation) {
        preview.setRotation(width,height,rotation);
    }

    @Override
    public int getMargineLeft() {
        if (preview.getPreviewView() == null)
            return 0;
        return preview.getPreviewView().getLeft();
    }

    @Override
    public int getMargineRight() {
        return preview.getPreviewView().getRight();
    }

    @Override
    public int getMargineTop() {
        return preview.getPreviewView().getTop();
    }

    public void changePreviewPostProcessing()
    {

        previewPostProcessingChangedEventHandler.fireOnPreviewPostProcessingChanged();
    }

    @Override
    public void setZebraHigh(float high) {
        zebrahigh = high;
        if (preview!=null)
            preview.setZebraHigh(high);
    }

    @Override
    public void setZebraLow(float low) {
        zebralow = low;
        if (preview != null)
            preview.setZebraLow(low);
    }

}
