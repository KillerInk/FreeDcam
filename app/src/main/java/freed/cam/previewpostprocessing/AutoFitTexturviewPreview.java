package freed.cam.previewpostprocessing;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;

import freed.utils.Log;
import freed.views.AutoFitTextureView;

public abstract class AutoFitTexturviewPreview implements Preview, TextureView.SurfaceTextureListener {
    private final String TAG = AutoFitTexturviewPreview.class.getSimpleName();
    protected AutoFitTextureView autoFitTextureView;
    private PreviewEvent previewEventListner;

    public AutoFitTexturviewPreview(Context context)
    {
        autoFitTextureView = new AutoFitTextureView(context);
    }

    @Override
    public SurfaceTexture getSurfaceTexture() {
        return autoFitTextureView.getSurfaceTexture();
    }

    @Override
    public View getPreviewView() {
        return autoFitTextureView;
    }

    @Override
    public void setSize(int width, int height) {
        autoFitTextureView.setAspectRatio(width,height);
    }

    @Override
    public void setPreviewEventListner(PreviewEvent eventListner) {
        previewEventListner = eventListner;
        if (eventListner != null)
            autoFitTextureView.setSurfaceTextureListener(this);
        else
            autoFitTextureView.setSurfaceTextureListener(null);
    }

    @Override
    public int getPreviewWidth() {
        return autoFitTextureView.getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return autoFitTextureView.getHeight();
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable");
        if (previewEventListner != null)
            previewEventListner.onPreviewAvailable(surface,width,height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged");
        if (previewEventListner != null)
            previewEventListner.onPreviewSizeChanged(surface,width,height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureDestroyed");
        if (previewEventListner != null)
            previewEventListner.onPreviewDestroyed(surface);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        //Log.d(TAG, "onSurfaceTextureUpdated");
       /* if (previewEventListner != null)
            previewEventListner.onPreviewUpdated(surface);*/
    }

    protected AutoFitTextureView getAutoFitTextureView()
    {
        return autoFitTextureView;
    }
}
