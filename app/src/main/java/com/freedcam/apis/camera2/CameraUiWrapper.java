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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build.VERSION_CODES;
import android.view.SurfaceView;
import android.view.TextureView.SurfaceTextureListener;

import com.freedcam.apis.basecamera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.interfaces.I_error;
import com.freedcam.apis.camera2.modules.ModuleHandlerApi2;
import com.freedcam.apis.camera2.parameters.ParameterHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedcam.utils.RenderScriptHandler;


/**
 * Created by troop on 07.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class CameraUiWrapper extends AbstractCameraUiWrapper implements SurfaceTextureListener

{
    public CameraHolder cameraHolder;
    private Context context;
    private AutoFitTextureView preview;
    protected I_error errorHandler;

    private static String TAG = CameraUiWrapper.class.getSimpleName();

    @Override
    public String CameraApiName() {
        return AppSettingsManager.API_2;
    }

    public CameraUiWrapper(Context context, AutoFitTextureView preview, AppSettingsManager appSettingsManager, RenderScriptHandler renderScriptHandler)
    {
        super(appSettingsManager);
        this.preview = preview;
        this.preview.setSurfaceTextureListener(this);
        this.context = context;
        errorHandler = this;
        cameraHolder = new CameraHolder(context, this,appSettingsManager,renderScriptHandler);
        super.cameraHolder = cameraHolder;
        parametersHandler = new ParameterHandler(this,context,appSettingsManager);
        cameraHolder.SetParameterHandler(parametersHandler);
        moduleHandler = new ModuleHandlerApi2(cameraHolder,context,appSettingsManager,renderScriptHandler);
        Focus = new FocusHandler(this);
        cameraHolder.Focus = Focus;
        Logger.d(TAG, "Constructor done");
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
    public void onCameraOpen(String message)
    {
        cameraHolder.SetSurface(preview);

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
        return preview.getLeft();
    }

    @Override
    public int getMargineRight() {
        return preview.getRight();
    }

    @Override
    public int getMargineTop() {
        return preview.getTop();
    }

    @Override
    public int getPreviewWidth() {
        return preview.getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return preview.getHeight();
    }

    @Override
    public SurfaceView getSurfaceView() {
        return null;
    }

}
