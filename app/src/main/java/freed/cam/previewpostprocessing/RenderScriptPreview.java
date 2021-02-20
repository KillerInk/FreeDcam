package freed.cam.previewpostprocessing;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.renderscript.RenderScript;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;

import freed.cam.apis.camera2.modules.AbstractModuleApi2;
import freed.cam.events.EventBusHelper;
import freed.cam.events.SwichCameraFragmentEvent;
import freed.cam.histogram.HistogramController;
import freed.cam.histogram.HistogramFeed;
import freed.renderscript.RenderScriptManager;
import freed.renderscript.RenderScriptProcessor;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.MatrixUtil;
import freed.viewer.screenslide.views.MyHistogram;
import freed.views.AutoFitTextureView;

public class RenderScriptPreview extends AutoFitTexturviewPreview {

    private RenderScriptManager renderScriptManager;
    private RenderScriptProcessor mProcessor;
    private Surface outputsurface;
    private boolean renderScriptError5 = false;
    private HistogramController histogramController;

    //use to workaround the problem with activated renderscript when switching back from a non renderscript session
    protected class MyRSErrorHandler extends RenderScript.RSErrorHandler
    {
        @Override
        public void run() {
            super.run();
            Log.e(MyRSErrorHandler.class.getSimpleName(), mErrorNum +":"+ mErrorMessage);
            if (mErrorNum == 5) // Error:5 setting IO output buffer usage.
            {
                renderScriptError5 = true;
                if (renderScriptError5)
                {
                    renderScriptError5 = false;
                    //clear the error else it trigger over and over....
                    mErrorNum = 0;
                    mErrorMessage = null;
                    //Restart the module
                    EventBusHelper.post(new SwichCameraFragmentEvent());
                }
            }
        }
    }

    public RenderScriptPreview(Context context, HistogramController histogram)
    {
        super(context);
        this.histogramController = histogram;
        if (RenderScriptManager.isSupported())
            renderScriptManager = new RenderScriptManager(context);
        if (SettingsManager.getInstance().getCamApi().equals(SettingsManager.API_2))
            mProcessor = new RenderScriptProcessor(renderScriptManager, ImageFormat.YUV_420_888);
        else if (SettingsManager.getInstance().getCamApi().equals(SettingsManager.API_1))
            mProcessor = new RenderScriptProcessor(renderScriptManager, ImageFormat.NV21);

    }

    public RenderScriptManager getRenderScriptManager() {
        return renderScriptManager;
    }


    @Override
    public void close() {
        mProcessor.kill();
    }

    @Override
    public Surface getInputSurface() {
        return mProcessor.getInputSurface();
    }

    @Override
    public void setOutputSurface(Surface surface) {
        this.outputsurface = surface;
    }

    @Override
    public void setSize(int width, int height) {
        mProcessor.SetAspectRatio(width,height);
        mProcessor.Reset(width, height,outputsurface);
    }

    @Override
    public boolean isSucessfullLoaded() {
        return renderScriptManager.isSucessfullLoaded();
    }

    @Override
    public void setBlue(boolean blue) {
        mProcessor.setBlue(blue);
    }

    @Override
    public void setRed(boolean red) {
        mProcessor.setRed(red);
    }

    @Override
    public void setGreen(boolean green) {
        mProcessor.setGreen(green);
    }

    @Override
    public void setFocusPeak(boolean on) {
        mProcessor.setFocusPeakEnable(on);
    }

    @Override
    public boolean isFocusPeak() {
        return mProcessor.isEnabled();
    }

    private boolean isclipping = false;
    @Override
    public void setClipping(boolean on) {
        isclipping = on;
        mProcessor.setClippingEnable(on);
    }

    @Override
    public boolean isClipping() {
        return isclipping;
    }

    private boolean ishistogram = false;
    @Override
    public void setHistogram(boolean on) {
        ishistogram = on;
        if (on)
            histogramController.setFeedToRegister(mProcessor);
        else
            histogramController.setFeedToRegister(null);
        mProcessor.setHistogramEnable(on);
        histogramController.enable(on);

    }

    @Override
    public boolean isHistogram() {
        return ishistogram;
    }

    @Override
    public void start() {
        mProcessor.setRenderScriptErrorListner(new MyRSErrorHandler());
        mProcessor.start();
    }

    @Override
    public void stop() {
        mProcessor.kill();
        mProcessor.setRenderScriptErrorListner(null);
    }

    @Override
    public void setRotation(int width, int height, int rotation) {
        float dispWidth = 0;
        float dispHeight = 0;
        dispWidth = getPreviewWidth();
        dispHeight = getPreviewHeight();
        Matrix matrix = MatrixUtil.getTransFormMatrix(width,height,(int)dispWidth,(int)dispHeight,rotation,true);
        getAutoFitTextureView().setTransform(matrix);
    }

    @Override
    public void setHistogramFeed(HistogramFeed feed) {

    }

}
