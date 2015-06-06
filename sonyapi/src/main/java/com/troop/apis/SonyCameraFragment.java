package com.troop.apis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.apis.AbstractCameraFragment;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.sonyapi.R;
import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;

/**
 * Created by troop on 06.06.2015.
 */
public class SonyCameraFragment extends AbstractCameraFragment
{
    SimpleStreamSurfaceView surfaceView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cameraholder, container, false);
        surfaceView = (SimpleStreamSurfaceView) view.findViewById(R.id.view);
        surfaceView.SetOnPreviewSizeCHangedListner(i_previewSizeEvent);
        this.cameraUiWrapper = new CameraUiWrapperSony(surfaceView, appSettingsManager);
        return view;
    }

    @Override
    public int getMargineLeft() {
        return surfaceView.getLeft();
    }

    @Override
    public int getMargineRight() {
        return surfaceView.getRight();
    }

    @Override
    public int getMargineTop() {
        return surfaceView.getTop();
    }

    @Override
    public int getPreviewWidth() {
        return surfaceView.getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return surfaceView.getHeight();
    }
}
