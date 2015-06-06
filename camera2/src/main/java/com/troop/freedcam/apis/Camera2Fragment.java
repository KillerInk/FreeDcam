package com.troop.freedcam.apis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.camera2.AutoFitTextureView;
import com.troop.freedcam.camera2.CameraUiWrapperApi2;
import com.troop.freedcam.camera2.R;
import com.troop.freedcam.ui.I_PreviewSizeEvent;

/**
 * Created by troop on 06.06.2015.
 */
public class Camera2Fragment extends AbstractCameraFragment
{
    AutoFitTextureView textureView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cameraholder, container, false);
        textureView = (AutoFitTextureView) view.findViewById(R.id.view);
        textureView.SetOnPreviewSizeCHangedListner(i_previewSizeEvent);
        this.cameraUiWrapper = new CameraUiWrapperApi2(view.getContext(),textureView,appSettingsManager);
        return view;
    }

    @Override
    public int getMargineLeft() {
        return textureView.getLeft();
    }

    @Override
    public int getMargineRight() {
        return textureView.getRight();
    }

    @Override
    public int getMargineTop() {
        return textureView.getTop();
    }

    @Override
    public int getPreviewWidth() {
        return textureView.getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return textureView.getHeight();
    }

}
