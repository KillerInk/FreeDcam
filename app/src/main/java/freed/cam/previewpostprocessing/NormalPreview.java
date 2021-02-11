package freed.cam.previewpostprocessing;

import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;


import freed.views.AutoFitTextureView;

public class NormalPreview implements Preview {

    private AutoFitTextureView autoFitTextureView;

    @Override
    public void setTextureView(TextureView autofitTextureView) {
        this.autoFitTextureView = (AutoFitTextureView)autofitTextureView;
    }

    @Override
    public void close() {

    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return autoFitTextureView.getSurfaceTexture();
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
        autoFitTextureView.setAspectRatio(width,height);
    }

    @Override
    public boolean isSucessfullLoaded() {
        return false;
    }

    @Override
    public void setBlue(boolean blue) {

    }

    @Override
    public void setRed(boolean red) {

    }

    @Override
    public void setGreen(boolean green) {

    }

    @Override
    public void setFocusPeak(boolean on) {

    }

    @Override
    public boolean isFocusPeak() {
        return false;
    }

    @Override
    public void setClipping(boolean on) {

    }

    @Override
    public boolean isClipping() {
        return false;
    }

    @Override
    public void setHistogram(boolean on) {

    }

    @Override
    public boolean isHistogram() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

}
