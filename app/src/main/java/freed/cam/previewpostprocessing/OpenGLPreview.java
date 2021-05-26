package freed.cam.previewpostprocessing;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.FreedApplication;
import freed.cam.histogram.HistogramController;
import freed.cam.histogram.HistogramFeed;
import freed.gl.GLPreview;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.DisplayUtil;
import freed.utils.Log;

public class OpenGLPreview implements Preview, TextureView.SurfaceTextureListener
{

    private static final String TAG = OpenGLPreview.class.getSimpleName();
    private GLPreview glPreview;
    private PreviewEvent previewEventListner;
    private HistogramController histogramController;
    private HistogramFeed feed;
    SettingsManager settingsManager;

    public OpenGLPreview(Context context, HistogramController myHistogram)
    {
        glPreview = new GLPreview(context);
        glPreview.setSurfaceTextureListener(this);
        this.histogramController = myHistogram;
        settingsManager = FreedApplication.settingsManager();
    }

    @Override
    public void close() {
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return glPreview.getSurfaceTexture();
    }

    @Override
    public Surface getInputSurface() {
        return null;
    }

    @Override
    public void setOutputSurface(Surface surface) {

    }

    @Override
    public void setSize(int width, int height) {
        Point disp =DisplayUtil.getDisplaySize();
        Log.d(TAG, "setSize width :" + width + " height:"+height+ " switch aspectRatio:" + settingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).get());
        glPreview.scale(width,height,disp.x,disp.y, settingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).get());
    }

    @Override
    public boolean isSucessfullLoaded() {
        return true;
    }

    @Override
    public void setBlue(boolean blue) {
        glPreview.setBlue(blue);
    }

    @Override
    public void setRed(boolean red) {
        glPreview.setRed(red);
    }

    @Override
    public void setGreen(boolean green) {
        glPreview.setGreen(green);
    }

    @Override
    public void setFocusPeak(boolean on) {
        glPreview.setFocuspeak_enabled(on);
    }

    @Override
    public boolean isFocusPeak() {
        return glPreview.isFocuspeak_enabled();
    }

    @Override
    public void setClipping(boolean on) {
        glPreview.setZebra_enabled(on);
    }

    @Override
    public boolean isClipping() {
        return glPreview.isZebra_enabled();
    }

    private boolean histogram = false;
    @Override
    public void setHistogram(boolean on) {
        if (on)
            histogramController.setFeedToRegister(feed);
        else
            histogramController.setFeedToRegister(null);
        histogram = on;
        histogramController.enable(on);

    }

    @Override
    public boolean isHistogram() {
        return histogram;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public View getPreviewView() {
        return glPreview;
    }

    @Override
    public void setPreviewEventListner(PreviewEvent eventListner) {
        this.previewEventListner = eventListner;
    }

    @Override
    public int getPreviewWidth() {
        return glPreview.getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return glPreview.getHeight();
    }

    @Override
    public void setRotation(int width, int height, int rotation) {
        glPreview.setOrientation(rotation);
    }

    @Override
    public void setHistogramFeed(HistogramFeed feed) {
        this.feed = feed;
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        if (previewEventListner != null)
            previewEventListner.onPreviewAvailable(surface,width,height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        if (previewEventListner != null)
            previewEventListner.onPreviewSizeChanged(surface,width,height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        if (previewEventListner != null)
            previewEventListner.onPreviewDestroyed(surface);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        if (previewEventListner != null)
            previewEventListner.onPreviewUpdated(surface);
    }

    @Override
    public void clear() {
        glPreview = null;
    }
}
