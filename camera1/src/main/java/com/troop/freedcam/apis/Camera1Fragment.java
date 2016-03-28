package com.troop.freedcam.apis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.ExtendedSurfaceView;
import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.TextureViewRatio;

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
        this.cameraUiWrapper = new CameraUiWrapper(extendedSurfaceView, preview);
        //call super at end because its throws on camerardy event
        super.onViewCreated(view, savedInstanceState);
    }





}
