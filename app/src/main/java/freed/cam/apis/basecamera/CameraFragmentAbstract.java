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

package freed.cam.apis.basecamera;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.SurfaceView;
import android.view.View;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.utils.AppSettingsManager;
import freed.utils.RenderScriptHandler;

/**
 * Created by troop on 06.06.2015.
 * That Fragment is used as base for all camera apis added.
 */
public abstract class CameraFragmentAbstract extends Fragment implements CameraWrapperInterface {
    private final String TAG = CameraFragmentAbstract.class.getSimpleName();

    protected View view;
    //the event listner when the camerauiwrapper is rdy to get attached to ui
    protected CameraFragmentAbstract.CamerUiWrapperRdy onrdy;
    //holds the appsettings
    protected RenderScriptHandler renderScriptHandler;

    public ModuleHandlerAbstract moduleHandler;
    /**
     * parameters for avail for the cameraHolder
     */
    public AbstractParameterHandler parametersHandler;
    /**
     * holds the current camera
     */
    public CameraHolderAbstract cameraHolder;
    /**
     * handels focus releated stuff for the current camera
     */
    public AbstractFocusHandler Focus;

    protected boolean PreviewSurfaceRdy;

    /**
     * holds the listners that get informed when the camera state change
     */
    private final List<CameraWrapperEvent> cameraChangedListners;

    /**
     * holds handler to invoke stuff in ui thread
     */
    protected Handler uiHandler;
    /**
     * holds the appsettings for the current camera
     */
    public AppSettingsManager appSettingsManager;


    public abstract String CameraApiName();


    public CameraFragmentAbstract()
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
    public CameraWrapperInterface GetCameraUiWrapper()
    {
        return this;
    }

    /**
     *
     * @param rdy the listner that gets thrown when the cameraUIwrapper
     *            has loaded all stuff and is rdy to get attached to ui.
     */
    public void Init(CameraFragmentAbstract.CamerUiWrapperRdy rdy)
    {
        onrdy = rdy;
    }


    /**
     * inteface for event listning when the camerauiwrapper is rdy
     */
    public interface CamerUiWrapperRdy
    {
        void onCameraUiWrapperRdy(CameraWrapperInterface cameraUiWrapper);
    }


    /**
     * adds a new listner for camera state changes
     * @param cameraChangedListner to add
     */
    public void SetCameraChangedListner(CameraWrapperEvent cameraChangedListner)
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
        for (final CameraWrapperEvent cameraChangedListner : cameraChangedListners)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpen(message);
                }
            });


    }

    @Override
    public void onCameraError(final String error) {
        for (final CameraWrapperEvent cameraChangedListner : cameraChangedListners)
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
        for (final CameraWrapperEvent cameraChangedListner : cameraChangedListners)
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
        for (final CameraWrapperEvent cameraChangedListner : cameraChangedListners)
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
        for (final CameraWrapperEvent cameraChangedListner : cameraChangedListners)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onPreviewOpen(message);
                }
            });
    }

    @Override
    public void onPreviewClose(final String message) {
        for (final CameraWrapperEvent cameraChangedListner : cameraChangedListners)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onPreviewClose(message);
                }
            });
    }

    @Override
    public void onModuleChanged(final ModuleInterface module) {
        for (final CameraWrapperEvent cameraChangedListner : cameraChangedListners)
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
        for (final CameraWrapperEvent cameraChangedListner : cameraChangedListners)
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
