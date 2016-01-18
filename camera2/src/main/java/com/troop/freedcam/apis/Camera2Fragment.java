package com.troop.freedcam.apis;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.camera2.AutoFitTextureView;
import com.troop.freedcam.camera2.CameraUiWrapperApi2;
import com.troop.freedcam.camera2.R;
import com.troop.marshmallowpermission.MPermissions;

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
        this.cameraUiWrapper = new CameraUiWrapperApi2(view.getContext(),textureView,appSettingsManager);
        super.onCreateView(inflater,container,savedInstanceState);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DestroyCameraUiWrapper();
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

    @Override
    public SurfaceView getSurfaceView() {
        return null;
    }


}
