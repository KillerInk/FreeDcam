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

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.utils.AppSettingsManager;
import freed.utils.Log;
import freed.utils.RenderScriptHandler;

/**
 * Created by troop on 06.06.2015.
 * That Fragment is used as base for all camera apis added.
 */
public abstract class CameraFragmentAbstract extends Fragment implements CameraWrapperInterface {
    private final String TAG = CameraFragmentAbstract.class.getSimpleName();

    protected View view;
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
    private final List<CameraStateEvents> cameraChangedListners;

    /**
     * holds handler to invoke stuff in ui thread
     */
    protected Handler uiHandler;
    /**
     * holds the appsettings for the current camera
     */
    private AppSettingsManager appSettingsManager;


    public abstract String CameraApiName();

    protected Object cameraLock = new Object();
    protected HandlerThread mBackgroundThread;
    protected Handler mBackgroundHandler;


    public CameraFragmentAbstract()
    {
        cameraChangedListners = new ArrayList<>();
        uiHandler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        startBackgroundThread();
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override
    public void onDestroyView()
    {
        if (moduleHandler != null) {
            moduleHandler.CLEAR();
            moduleHandler.CLEARWORKERLISTNER();
        }
        stopBackgroundThread();
        super.onDestroyView();

    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        synchronized (cameraLock) {
            mBackgroundThread = new HandlerThread("CameraBackground");
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread()
    {
        synchronized (cameraLock) {
            Log.d(TAG, "stopBackgroundThread");
            if (mBackgroundThread == null)
                return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundThread.quitSafely();
            } else
                mBackgroundThread.quit();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                Log.WriteEx(e);
            }
        }
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
     * adds a new listner for camera state changes
     * @param cameraChangedListner to add
     */
    public void setCameraStateChangedListner(final CameraStateEvents cameraChangedListner)
    {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                cameraChangedListners.add(cameraChangedListner);
            }
        });
    }

    @Override
    public void startCamera()
    {
    }

    @Override
    public void stopCamera()
    {
    }

    @Override
    public void stopPreview()
    {
    }


    @Override
    public void startPreview()
    {
    }

    /**
     * Starts a new work with the current active module
     * the module must handle the workstate on its own if it gets hit twice while work is already in progress
     */
    @Override
    public void startWork()
    {
        moduleHandler.startWork();
    }


    @Override
    public void onCameraOpen(final String message)
    {
        for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpen(message);
                }
            });


    }

    @Override
    public void onCameraError(final String error) {
        for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
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
        for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
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
        for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
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
        for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onPreviewOpen(message);
                }
            });
    }

    @Override
    public void onPreviewClose(final String message) {
        for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onPreviewClose(message);
                }
            });
    }

    @Override
    public void onCameraOpenFinish(final String message)
    {
        for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
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
    public AppSettingsManager getAppSettingsManager() {
        return appSettingsManager;
    }

    @Override
    public RenderScriptHandler getRenderScriptHandler() {
        return renderScriptHandler;
    }

    @Override
    public ActivityInterface getActivityInterface() {
        return (ActivityInterface)getActivity();
    }

    @Override
    public boolean isAeMeteringSupported() {
        return Focus.isAeMeteringSupported();
    }

    @Override
    public FocuspeakProcessor getFocusPeakProcessor() {
        return null;
    }

    @Override
    public AbstractFocusHandler getFocusHandler() {
        return Focus;
    }

    @Override
    public CameraHolderInterface getCameraHolder() {
        return cameraHolder;
    }

    @Override
    public AbstractParameterHandler getParameterHandler() {
        return parametersHandler;
    }

    @Override
    public ModuleHandlerAbstract getModuleHandler() {
        return moduleHandler;
    }
}
