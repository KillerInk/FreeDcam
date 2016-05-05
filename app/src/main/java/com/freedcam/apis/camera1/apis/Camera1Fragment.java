package com.freedcam.apis.camera1.apis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.apis.basecamera.apis.AbstractCameraFragment;
import com.freedcam.apis.camera1.camera.CameraUiWrapper;
import com.freedcam.apis.camera1.camera.ExtendedSurfaceView;
import com.freedcam.apis.camera1.camera.TextureViewRatio;
import com.troop.freedcam.R;

/**
 * Created by troop on 06.06.2015.
 */
public class Camera1Fragment extends AbstractCameraFragment
{
    private ExtendedSurfaceView extendedSurfaceView;
    private TextureViewRatio preview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.cameraholder1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        extendedSurfaceView = (ExtendedSurfaceView) view.findViewById(R.id.exSurface);
        preview = (TextureViewRatio) view.findViewById(R.id.textureView_preview);

        //call super at end because its throws on camerardy event
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.cameraUiWrapper = new CameraUiWrapper(extendedSurfaceView, preview, getContext(),appSettingsManager);
        if (onrdy != null)
            onrdy.onCameraUiWrapperRdy(cameraUiWrapper);
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraUiWrapper.StopPreview();
        cameraUiWrapper.StopCamera();
    }
}
