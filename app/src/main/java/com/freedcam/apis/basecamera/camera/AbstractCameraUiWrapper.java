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

package com.freedcam.apis.basecamera.camera;

import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceView;

import com.freedcam.apis.basecamera.camera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.camera.interfaces.I_Module;
import com.freedcam.apis.basecamera.camera.interfaces.I_error;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.AppSettingsManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractCameraUiWrapper implements I_CameraUiWrapper, I_CameraChangedListner, I_error
{
    private final String TAG = AbstractCameraUiWrapper.class.getSimpleName();
    /**
     * Holds all modules avail for the api, and handel it
     */
    public AbstractModuleHandler moduleHandler;
    /**
     * parameters for avail for the cameraHolder
     */
    public AbstractParameterHandler parametersHandler;
    /**
     * holds the current camera
     */
    public AbstractCameraHolder cameraHolder;
    /**
     * handels focus releated stuff for the current camera
     */
    public AbstractFocusHandler Focus;

    protected boolean PreviewSurfaceRdy = false;

    /**
     * holds the listners that get informed when the camera state change
     */
    private List<I_CameraChangedListner> cameraChangedListners;

    /**
     * holds handler to invoke stuff in ui thread
     */
    protected Handler uiHandler;
    /**
     * holds the appsettings for the current camera
     */
    public AppSettingsManager appSettingsManager;


    public abstract String CameraApiName();

    protected AbstractCameraUiWrapper(AppSettingsManager appSettingsManager)
    {   cameraChangedListners = new CopyOnWriteArrayList<>();
        uiHandler = new Handler(Looper.getMainLooper());
        this.appSettingsManager = appSettingsManager;
    }


    /**
     * adds a new listner for camera state changes
     * @param cameraChangedListner to add
     */
    public void SetCameraChangedListner(I_CameraChangedListner cameraChangedListner)
    {
        cameraChangedListners.add(cameraChangedListner);
    }

    @Override
    public void StartCamera()
    {
    }

    @Override
    public void StopCamera()
    {
    }

    @Override
    public void StopPreview()
    {
    }


    @Override
    public void StartPreview()
    {
    }

    /**
     * Starts a new work with the current active module
     * the module must handle the workstate on its own if it gets hit twice while work is already in progress
     */
    @Override
    public void DoWork()
    {
        moduleHandler.DoWork();
    }


    @Override
    public void onCameraOpen(final String message)
    {
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpen(message);
                }
            });


    }

    @Override
    public void onCameraError(final String error) {
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraError(error);
                }
            });
    }

    @Override
    public void onCameraStatusChanged(final String status)
    {
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraStatusChanged(status);
                }
            });


    }

    @Override
    public void onCameraClose(final String message)
    {
        parametersHandler.locationParameter.stopLocationListining();
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraClose(message);
                }
            });


    }

    @Override
    public void onPreviewOpen(final String message)
    {
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onPreviewOpen(message);
                }
            });
    }

    @Override
    public void onPreviewClose(final String message) {
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onPreviewClose(message);
                }
            });
    }

    @Override
    public void onModuleChanged(final I_Module module) {
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onModuleChanged(module);
                }
            });

    }

    @Override
    public void onCameraOpenFinish(final String message)
    {
        for (final I_CameraChangedListner cameraChangedListner : cameraChangedListners )
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpenFinish(message);
                }
            });

    }

    public abstract int getMargineLeft();
    public abstract int getMargineRight();
    public abstract int getMargineTop();
    public abstract int getPreviewWidth();
    public abstract int getPreviewHeight();
    public abstract SurfaceView getSurfaceView();
}
