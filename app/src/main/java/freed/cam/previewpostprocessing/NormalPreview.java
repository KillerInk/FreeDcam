package freed.cam.previewpostprocessing;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.view.Surface;

import freed.cam.histogram.HistogramFeed;
import freed.utils.DisplayUtil;
import freed.utils.MatrixUtil;

public class NormalPreview extends AutoFitTexturviewPreview {

    Point displaySize;

    public NormalPreview(Context context) {
        super(context);
        displaySize = DisplayUtil.getDisplaySize();
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
    public void setColorWaveForm(boolean on) {

    }

    @Override
    public boolean isColorWaveForm() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void setRotation(int width, int height, int rotation) {
        float dispWidth = 0;
        float dispHeight = 0;
        if (displaySize.x > displaySize.y) {
            dispWidth = displaySize.x;
            dispHeight = displaySize.y;
        }
        else
        {
            dispWidth = displaySize.y;
            dispHeight = displaySize.x;
        }
        Matrix matrix = MatrixUtil.getTransFormMatrix(width,height,(int)dispWidth,(int)dispHeight,rotation,false);
        getAutoFitTextureView().setTransform(matrix);
    }

    @Override
    public void setHistogramFeed(HistogramFeed feed) {

    }

    @Override
    public void setZebraHigh(float high) {

    }

    @Override
    public void setZebraLow(float low) {

    }


}
