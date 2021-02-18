package freed.cam.previewpostprocessing;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import freed.viewer.screenslide.views.MyHistogram;

public class PreviewController implements PreviewControllerInterface
{
    private Preview preview;

    @Override
    public void initPreview(PreviewPostProcessingModes previewPostProcessingModes, Context context, MyHistogram histogram)
    {
        if (preview != null)
            preview.close();
        switch (previewPostProcessingModes)
        {
            case off:
                preview = new NormalPreview(context);
                break;
            case RenderScript:
                preview = new RenderScriptPreview(context,histogram);
                break;
            case OpenGL:
                preview = new OpenGLPreview(context,histogram);
                break;
        }
    }

    public Preview getPreview() {
        return preview;
    }

    @Override
    public void close() {
        if (preview != null)
            preview.close();
    }

    public SurfaceTexture getSurfaceTexture()
    {
        return preview.getSurfaceTexture();
    }

    public Surface getInputSurface()
    {
        return preview.getInputSurface();
    }

    @Override
    public void setOutputSurface(Surface surface) {
        preview.setOutputSurface(surface);
    }

    @Override
    public void setSize(int width, int height) {
        preview.setSize(width,height);
    }

    @Override
    public boolean isSucessfullLoaded() {
        return preview.isSucessfullLoaded();
    }

    @Override
    public void setBlue(boolean blue) {
        preview.setBlue(blue);
    }

    @Override
    public void setRed(boolean red) {
        preview.setRed(red);
    }

    @Override
    public void setGreen(boolean green) {
        preview.setGreen(green);
    }

    @Override
    public void setFocusPeak(boolean on) {
        preview.setFocusPeak(on);
    }

    @Override
    public boolean isFocusPeak() {
        return preview.isFocusPeak();
    }

    @Override
    public void setClipping(boolean on) {
        preview.setClipping(on);
    }

    @Override
    public boolean isClipping() {
        return preview.isClipping();
    }

    @Override
    public void setHistogram(boolean on) {
        preview.setHistogram(on);
    }

    @Override
    public boolean isHistogram() {
        return preview.isHistogram();
    }

    @Override
    public void start() {
        preview.start();
    }

    @Override
    public void stop() {
        preview.stop();
    }

    @Override
    public View getPreviewView() {
        return preview.getPreviewView();
    }

    @Override
    public void setPreviewEventListner(PreviewEvent eventListner) {
        preview.setPreviewEventListner(eventListner);
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

}
