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

package com.freedcam.apis.sonyremote.camera;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.camera.interfaces.I_Module;
import com.freedcam.apis.sonyremote.camera.modules.ModuleHandlerSony;
import com.freedcam.apis.sonyremote.camera.parameters.ParameterHandler;
import com.freedcam.apis.sonyremote.camera.sonystuff.ServerDevice;
import com.freedcam.apis.sonyremote.camera.sonystuff.SimpleStreamSurfaceView;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FreeDPool;

/**
 * Created by troop on 11.12.2014.
 */
public class CameraUiWrapper extends AbstractCameraUiWrapper implements SurfaceHolder.Callback
{
    protected SimpleStreamSurfaceView surfaceView;

    public ServerDevice serverDevice;
    public CameraHolder cameraHolder;


    @Override
    public String CameraApiName() {
        return AppSettingsManager.API_SONY;
    }

    public CameraUiWrapper(SurfaceView preview, Context context, AppSettingsManager appSettingsManager) {
        super(appSettingsManager);
        this.surfaceView = (SimpleStreamSurfaceView)preview;
        this.surfaceView.getHolder().addCallback(this);
        this.cameraHolder = new CameraHolder(preview.getContext(), surfaceView, this, uiHandler,appSettingsManager);
        parametersHandler = new ParameterHandler(this, surfaceView, context,appSettingsManager);
        cameraHolder.ParameterHandler = (ParameterHandler) parametersHandler;

        moduleHandler = new ModuleHandlerSony(cameraHolder,context,appSettingsManager);
        this.Focus = new FocusHandler(this);
        super.cameraHolder = cameraHolder;
        cameraHolder.focusHandler =(FocusHandler) Focus;
        cameraHolder.moduleHandlerSony = (ModuleHandlerSony)moduleHandler;
    }


    @Override
    public void StartCamera()
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                cameraHolder.OpenCamera(serverDevice);
                onCameraOpen("");
            }
        });
    }

    @Override
    public void StopCamera() {
        cameraHolder.CloseCamera();
    }


    @Override
    public void onCameraOpen(String message) {
        super.onCameraOpen(message);
    }

    @Override
    public void onCameraError(String error) {
        cameraHolder.isPreviewRunning = false;
        PreviewSurfaceRdy = false;
        surfaceView.stop();
        super.onCameraError(error);
    }

    @Override
    public void onCameraStatusChanged(String status) {
        super.onCameraStatusChanged(status);
    }

    @Override
    public void onModuleChanged(I_Module module) {
        super.onModuleChanged(module);
    }

    @Override
    public void DoWork() {
        moduleHandler.DoWork();
    }

    @Override
    public void onCameraClose(String message) {

    }

    @Override
    public void onPreviewOpen(String message) {

    }

    @Override
    public void onPreviewClose(String message) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        StopPreview();
        StopCamera();
    }

    @Override
    public void OnError(String error) {
        super.onCameraError(error);
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

    @Override
    public SurfaceView getSurfaceView() {
        return surfaceView;
    }
}
