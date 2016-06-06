package com.freedcam.apis.camera2.apis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.apis.basecamera.apis.AbstractCameraFragment;
import com.freedcam.apis.camera2.camera.AutoFitTextureView;
import com.freedcam.apis.camera2.camera.CameraUiWrapperApi2;
import com.troop.freedcam.R;


/**
 * Created by troop on 06.06.2015.
 */
public class Camera2Fragment extends AbstractCameraFragment
{
    private AutoFitTextureView textureView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.cameraholder2, container, false);
        textureView = (AutoFitTextureView) view.findViewById(R.id.autofitview);
        this.cameraUiWrapper = new CameraUiWrapperApi2(view.getContext(),textureView,appSettingsManager, renderScriptHandler);
        if (onrdy != null)
            onrdy.onCameraUiWrapperRdy(cameraUiWrapper);
        super.onCreateView(inflater,container,savedInstanceState);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }




}
