package com.troop.freedcam.cameraui.fragment;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.troop.freedcam.camera.basecamera.AbstractCameraController;
import com.troop.freedcam.cameraui.R;
import com.troop.freedcam.cameraui.databinding.CamerafragmentBinding;
import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.models.TextureHolder;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.Log;

public  class BasicCameraFragment<C extends AbstractCameraController> extends Fragment implements TextureView.SurfaceTextureListener {

    private static final String TAG = "BasicCameraFragment";
    protected CamerafragmentBinding camerafragmentBinding;
    protected C cameraController;
    protected boolean previewSurfaceRdy;
    protected boolean cameraIsOpen;

    public void setCameraController(C cameraController)
    {
        this.cameraController = cameraController;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        camerafragmentBinding = DataBindingUtil.inflate(inflater, R.layout.camerafragment, container, false);
        camerafragmentBinding.autoFitTextureViewCameraPreview.setSurfaceTextureListener(this);
        return camerafragmentBinding.getRoot();
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "surface created");
        previewSurfaceRdy = true;
        cameraController.setTextureHolder(new TextureHolder(camerafragmentBinding.autoFitTextureViewCameraPreview));
        if (!cameraIsOpen)
            cameraController.startCameraAsync();
        else {
            cameraController.initCamera();
            cameraController.startPreviewAsync();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        previewSurfaceRdy =false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onResume() {
        super.onResume();
        onResumeFragment();
    }

    protected void onResumeFragment()
    {
        EventBusHelper.register(camerafragmentBinding.autoFitTextureViewCameraPreview);
        if (previewSurfaceRdy && !cameraIsOpen)
            cameraController.startCameraAsync();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBusHelper.unregister(camerafragmentBinding.autoFitTextureViewCameraPreview);
        onPauseFragment();
    }

    protected void onPauseFragment()
    {
        if(cameraController != null
                && cameraController.moduleHandler != null
                && cameraController.moduleHandler.getCurrentModule() != null
                && cameraController.moduleHandler.getCurrentModule().ModuleName() != null
                && cameraController.moduleHandler.getCurrentModule().ModuleName().equals(ContextApplication.getStringFromRessources(R.string.module_video))
                && cameraController.moduleHandler.getCurrentModule().IsWorking())
            cameraController.moduleHandler.getCurrentModule().DoWork();
        if (cameraController != null)
            cameraController.stopCameraAsync();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraController = null;
    }
}
