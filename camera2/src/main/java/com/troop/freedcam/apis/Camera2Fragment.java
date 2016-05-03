package com.troop.freedcam.apis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.camera2.AutoFitTextureView;
import com.troop.freedcam.camera2.CameraUiWrapperApi2;
import com.troop.freedcam.camera2.R;

/**
 * Created by troop on 06.06.2015.
 */
public class Camera2Fragment extends AbstractCameraFragment
{
    AutoFitTextureView textureView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cameraholder2, container, false);
        textureView = (AutoFitTextureView) view.findViewById(R.id.autofitview);

        super.onCreateView(inflater,container,savedInstanceState);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.cameraUiWrapper = new CameraUiWrapperApi2(view.getContext(),textureView);
        if (onrdy != null)
            onrdy.onCameraUiWrapperRdy(cameraUiWrapper);
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraUiWrapper.StopPreview();
        cameraUiWrapper.StopCamera();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }




}
