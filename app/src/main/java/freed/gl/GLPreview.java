package freed.gl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.HashMap;

import freed.gl.program.GLProgamInterface;
import freed.utils.Log;

public class GLPreview extends GLSurfaceView {
    private static final String TAG =  GLPreview.class.getSimpleName();
    MainRenderer mRenderer;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private PreviewModel previewModel;
    private boolean focuspeak_enabled = false;
    private boolean zebra_enabled= false;

    public enum PreviewProcessors
    {
        Normal,
        FocusPeak,
        Zebra,
        FocusPeak_Zebra,
    }

    public GLPreview(Context context) {
        super(context);
        init();
    }

    public GLPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        previewModel = new PreviewModel();
        mRenderer = new MainRenderer(this,previewModel);
        setEGLContextClientVersion(2);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void fireOnSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int w, int h)
    {
        if (surfaceTextureListener != null)
            surfaceTextureListener.onSurfaceTextureAvailable(surfaceTexture,w,h);
    }

    public void fireOnSurfaceTextureDestroyed(SurfaceTexture surfaceTexture)
    {
        if (surfaceTextureListener != null)
            surfaceTextureListener.onSurfaceTextureDestroyed(surfaceTexture);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        previewModel.setTextSize(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
        Log.d(TAG, "texSize :" + previewModel.getTextSize()[0] +"/"+ previewModel.getTextSize()[1]);

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
        previewModel.setTextSize(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());
        Log.d(TAG, "texSize :" + previewModel.getTextSize()[0] +"/"+ previewModel.getTextSize()[1]);
        if (surfaceTextureListener != null)
            surfaceTextureListener.onSurfaceTextureSizeChanged(mRenderer.getmSTexture(),w,h);
    }

    @Override
    public void onResume() {
        super.onResume();
        //mRenderer.onResume();
    }

    @Override
    public void onPause() {
        fireOnSurfaceTextureDestroyed(getSurfaceTexture());
        //mRenderer.onPause();
        super.onPause();
    }


    public SurfaceTexture getSurfaceTexture()
    {
        return mRenderer.getmSTexture();
    }


    public void setSurfaceTextureListener(TextureView.SurfaceTextureListener l) {
        this.surfaceTextureListener = l;
    }

    public void scale(int in_width, int in_height, int out_width, int out_height,boolean switchAspect)
    {
        //in w:1440 in h: 1080 out w: 2400 out h: 1080
        //in w:1440 in h: 1080 out w: 2560 out h: 1440
        Log.d(TAG, "in w: " + in_width + " in h: " + in_height + " out w: " + out_width + " out h: " + out_height);
        FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(in_width, in_height);


        if (!switchAspect) {

            if (in_height >= out_height) {
                layout.height = in_height;
                layout.width = in_width;
                layout.leftMargin = (out_width - in_width) / 2;
                layout.rightMargin = layout.leftMargin;
            }
            else {
                layout.height = Math.round((float) in_height * ((float) out_height / (float) in_height));
                layout.width = Math.round((float) in_width * ((float) out_height / (float) in_height));
                layout.leftMargin = (out_width - layout.width) / 2;
                layout.rightMargin = layout.leftMargin;
            }
        }
        else {
            int s = out_height - in_width;
            int ws = out_width +s;
            int hs = out_height - s;
            layout.height = ws;
            layout.width = hs;
            layout.leftMargin = (out_width - hs) / 2;
            layout.rightMargin = layout.leftMargin;
        }


        this.post(()-> this.setLayoutParams(layout));
       ;
    }

    private int getNewWidth(int input_height, float ratio)
    {
        return Math.round(input_height / ratio);
    }

    private int getNewHeight(int input_width, float ratio)
    {
        return Math.round(input_width * ratio);
    }

    private float getAspectRation(int w, int h)
    {
        return (float)w/(float)h;
    }

    public void setOrientation(int or)
    {
        previewModel.setOrientation(or);
    }

    public void setPreviewProcessors(PreviewProcessors processors)
    {
        mRenderer.setProgram(processors);
    }

    public void setFocusPeakColor(PreviewModel.Colors color)
    {
        previewModel.setPeak_color(color);
        requestRender();
    }

    public void setRed(boolean r)
    {
        previewModel.setRed(r);
        requestRender();
    }

    public void setGreen(boolean g)
    {
        previewModel.setGreen(g);
        requestRender();
    }

    public void setBlue(boolean b)
    {
        previewModel.setBlue(b);
        requestRender();
    }

    public void setFocuspeak_enabled(boolean focuspeak_enabled) {
        this.focuspeak_enabled = focuspeak_enabled;
        applyProgram();
    }

    public boolean isFocuspeak_enabled() {
        return focuspeak_enabled;
    }

    public void setZebra_enabled(boolean zebra_enabled) {
        this.zebra_enabled = zebra_enabled;
        applyProgram();
    }

    public boolean isZebra_enabled() {
        return zebra_enabled;
    }

    private void applyProgram()
    {
        if (zebra_enabled && focuspeak_enabled)
            setPreviewProcessors(PreviewProcessors.FocusPeak_Zebra);
        else if (zebra_enabled)
            setPreviewProcessors(PreviewProcessors.Zebra);
        else if (focuspeak_enabled)
        {
            setPreviewProcessors(PreviewProcessors.FocusPeak);
        }
        else
            setPreviewProcessors(PreviewProcessors.Normal);
    }
}
