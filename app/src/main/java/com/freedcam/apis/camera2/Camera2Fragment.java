/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.camera2;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.MainActivity;
import com.freedcam.apis.basecamera.AbstractCameraFragment;
import com.freedcam.apis.basecamera.AbstractFocusHandler;
import com.freedcam.apis.basecamera.interfaces.I_CameraHolder;
import com.freedcam.apis.basecamera.interfaces.I_error;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.parameters.AbstractParameterHandler;
import com.freedcam.apis.camera2.modules.ModuleHandlerApi2;
import com.freedcam.apis.camera2.parameters.ParameterHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;


/**
 * Created by troop on 06.06.2015.
 */
public class Camera2Fragment extends AbstractCameraFragment implements TextureView.SurfaceTextureListener
{
    public CameraHolder cameraHolder;
    private AutoFitTextureView textureView;
    protected I_error errorHandler;
    private final String TAG = Camera2Fragment.class.getSimpleName();

    public String CameraApiName() {
        return AppSettingsManager.API_2;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(layout.cameraholder2, container, false);
        textureView = (AutoFitTextureView) view.findViewById(id.autofitview);
        this.textureView.setSurfaceTextureListener(this);
        errorHandler = this;
        cameraHolder = new CameraHolder(getContext(), this,appSettingsManager,renderScriptHandler);
        super.cameraHolder = cameraHolder;
        parametersHandler = new ParameterHandler(this,getContext(),appSettingsManager);
        cameraHolder.SetParameterHandler(parametersHandler);
        moduleHandler = new ModuleHandlerApi2(cameraHolder,getContext(),appSettingsManager,renderScriptHandler);
        Focus = new FocusHandler(this);
        cameraHolder.Focus = Focus;
        Logger.d(TAG, "Constructor done");
        ((MainActivity)getActivity()).onCameraUiWrapperRdy(this);

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

    @Override
    public void StartCamera() {
        cameraHolder.OpenCamera(appSettingsManager.GetCurrentCamera());
        Logger.d(TAG, "opencamera");
    }

    @Override
    public void StopCamera() {
        Logger.d(TAG, "Stop Camera");
        cameraHolder.CloseCamera();
    }

    @Override
    public void StartPreview() {
        Logger.d(TAG, "Stop Preview");
        cameraHolder.StartPreview();
    }

    @Override
    public void StopPreview()
    {
        Logger.d(TAG, "Stop Preview");
        cameraHolder.StopPreview();
    }

    @Override
    public I_CameraHolder GetCameraHolder() {
        return cameraHolder;
    }

    @Override
    public AbstractParameterHandler GetParameterHandler() {
        return parametersHandler;
    }

    @Override
    public AbstractModuleHandler GetModuleHandler() {
        return moduleHandler;
    }

    @Override
    public void onCameraOpen(String message)
    {
        cameraHolder.SetSurface(textureView);

        Logger.d(TAG, "Camera Opened and Preview Started");
        super.onCameraOpen(message);
        moduleHandler.SetModule(appSettingsManager.GetCurrentModule());
    }

    @Override
    public void onCameraClose(String message)
    {
        super.onCameraClose(message);
    }

    @Override
    public void onPreviewOpen(String message) {

    }

    @Override
    public void onPreviewClose(String message) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
    {
        Logger.d(TAG, "SurfaceTextureAvailable");
        if (!PreviewSurfaceRdy) {
            PreviewSurfaceRdy = true;
            StartCamera();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        Logger.d(TAG, "Surface destroyed");
        PreviewSurfaceRdy = false;
        StopPreview();
        StopCamera();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void OnError(String error) {
        onCameraError(error);
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
    public boolean isAeMeteringSupported() {
        return Focus.isAeMeteringSupported();
    }

    @Override
    public SurfaceView getSurfaceView() {
        return null;
    }

    @Override
    public AbstractFocusHandler getFocusHandler() {
        return Focus;
    }


}
