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

package com.freedcam.apis.basecamera;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.SurfaceView;
import android.view.View;

import com.freedcam.apis.basecamera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.interfaces.I_Module;
import com.freedcam.apis.basecamera.interfaces.I_error;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.parameters.AbstractParameterHandler;
import com.freedcam.apis.basecamera.parameters.modes.LocationParameter;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedcam.utils.RenderScriptHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by troop on 06.06.2015.
 * That Fragment is used as base for all camera apis added.
 */
public abstract class AbstractCameraFragment extends Fragment implements I_CameraUiWrapper, I_CameraChangedListner, I_error
{
    private final String TAG = AbstractCameraFragment.class.getSimpleName();

    protected View view;
    //the event listner when the camerauiwrapper is rdy to get attached to ui
    protected AbstractCameraFragment.CamerUiWrapperRdy onrdy;
    //holds the appsettings
    protected RenderScriptHandler renderScriptHandler;

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


    public AbstractCameraFragment()
    {
        cameraChangedListners = new CopyOnWriteArrayList<>();
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public void SetRenderScriptHandler(RenderScriptHandler renderScriptHandler)
    {
        this.renderScriptHandler = renderScriptHandler;
    }

    public void SetAppSettingsManager(AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;
    }

    /**
     *
     * @return the current instance of the cameruiwrapper
     */
    public I_CameraUiWrapper GetCameraUiWrapper()
    {
        return this;
    }

    /**
     *
     * @param rdy the listner that gets thrown when the cameraUIwrapper
     *            has loaded all stuff and is rdy to get attached to ui.
     */
    public void Init(AbstractCameraFragment.CamerUiWrapperRdy rdy)
    {
        onrdy = rdy;
    }


    /**
     * shutdown the current camera instance
     */
    public void DestroyCameraUiWrapper()
    {
            Logger.d(TAG, "Destroying Wrapper");
            parametersHandler.CLEAR();
            moduleHandler.moduleEventHandler.CLEAR();
            moduleHandler.CLEARWORKERLISTNER();
            StopPreview();
            StopCamera();
            Logger.d(TAG, "destroyed cameraWrapper");

    }

    /**
     * inteface for event listning when the camerauiwrapper is rdy
     */
    public interface CamerUiWrapperRdy
    {
        void onCameraUiWrapperRdy(I_CameraUiWrapper cameraUiWrapper);
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
        ((LocationParameter)parametersHandler.locationParameter).stopLocationListining();
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



    @Override
    public AppSettingsManager GetAppSettingsManager() {
        return appSettingsManager;
    }

}
