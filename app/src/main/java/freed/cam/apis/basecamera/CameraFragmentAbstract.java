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

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
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
import freed.utils.RenderScriptHandler;

/**
 * Created by troop on 06.06.2015.
 * That Fragment is used as base for all camera apis added.
 */
public abstract class CameraFragmentAbstract extends Fragment implements CameraWrapperInterface {
    private final String TAG = CameraFragmentAbstract.class.getSimpleName();

    private final int MSG_ON_CAMERA_OPEN = 0;
    private final int MSG_ON_CAMERA_ERROR = 1;
    private final int MSG_ON_CAMERA_STATUS_CHANGED = 2;
    private final int MSG_ON_CAMERA_CLOSE = 3;
    private final int MSG_ON_PREVIEW_OPEN= 4;
    private final int MSG_ON_PREVIEW_CLOSE= 5;
    private final int MSG_ON_CAMERA_OPEN_FINISHED= 6;
    private final int MSG_SET_CAMERASTAUSLISTNER = 7;

    public final int MSG_START_CAMERA = 10;
    public final int MSG_STOP_CAMERA = 11;
    public final int MSG_RESTART_CAMERA = 12;
    public final int MSG_START_PREVIEW = 13;
    public final int MSG_STOP_PREVIEW = 14;
    public final int MSG_INIT_CAMERA = 15;
    public final int MSG_CREATE_CAMERA = 16;
    public final int MSG_SET_ASPECTRATIO = 1337;

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

    protected Object cameraLock;
    protected Handler mBackgroundHandler;
    private HandlerThread handlerThread;


    public CameraFragmentAbstract(HandlerThread handlerThread, Object cameraLock)
    {
        cameraChangedListners = new ArrayList<>();
        uiHandler = new UiHandler(Looper.getMainLooper());
        this.handlerThread = handlerThread;
        this.mBackgroundHandler = new BackgroundHandler(handlerThread.getLooper());
        this.cameraLock = cameraLock;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override
    public void onDestroyView()
    {
        if (moduleHandler != null) {
            moduleHandler.CLEAR();
            moduleHandler.CLEARWORKERLISTNER();
        }
        super.onDestroyView();

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
        uiHandler.sendMessage(uiHandler.obtainMessage(MSG_SET_CAMERASTAUSLISTNER,cameraChangedListner));
    }

    @Override
    public void startCamera() {
        mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(MSG_START_CAMERA));
    }

    @Override
    public void stopCamera() {
        mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(MSG_STOP_CAMERA));
    }

    @Override
    public void restartCamera() {
        mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(MSG_RESTART_CAMERA));
    }

    @Override
    public void startPreview() {
        mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(MSG_START_PREVIEW));
    }

    @Override
    public void stopPreview() {
        mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(MSG_STOP_PREVIEW));
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
        uiHandler.sendMessage(uiHandler.obtainMessage(MSG_ON_CAMERA_OPEN,message));
    }

    @Override
    public void onCameraError(final String error) {
        uiHandler.sendMessage(uiHandler.obtainMessage(MSG_ON_CAMERA_ERROR,error));
    }

    @Override
    public void onCameraStatusChanged(final String status)
    {
        uiHandler.sendMessage(uiHandler.obtainMessage(MSG_ON_CAMERA_STATUS_CHANGED, status));
    }

    @Override
    public void onCameraClose(final String message)
    {
        uiHandler.sendMessage(uiHandler.obtainMessage(MSG_ON_CAMERA_CLOSE, message));
    }

    @Override
    public void onPreviewOpen(final String message)
    {
        uiHandler.sendMessage(uiHandler.obtainMessage(MSG_ON_PREVIEW_OPEN, message));
    }

    @Override
    public void onPreviewClose(final String message) {
        uiHandler.sendMessage(uiHandler.obtainMessage(MSG_ON_PREVIEW_CLOSE, message));
    }

    @Override
    public void onCameraOpenFinish(final String message)
    {
        uiHandler.sendMessage(uiHandler.obtainMessage(MSG_ON_CAMERA_OPEN_FINISHED, message));
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

    @Override
    public HandlerThread getCameraHandlerThread() {
        return handlerThread;
    }

    private class UiHandler extends Handler
    {
        public UiHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what)
            {
                case MSG_SET_CAMERASTAUSLISTNER:
                    cameraChangedListners.add((CameraStateEvents)msg.obj);
                    break;
                default:
                    CameraFragmentAbstract.this.handleUiMessage(msg);
            }

        }
    }

    protected void handleUiMessage(Message msg)
    {
        switch (msg.what) {
            case MSG_ON_CAMERA_OPEN:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onCameraOpen((String)msg.obj);
                break;
            case MSG_ON_CAMERA_ERROR:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onCameraError((String)msg.obj);
                break;
            case MSG_ON_CAMERA_STATUS_CHANGED:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onCameraStatusChanged((String)msg.obj);
                break;
            case MSG_ON_CAMERA_CLOSE:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onCameraClose((String)msg.obj);
                break;
            case MSG_ON_PREVIEW_OPEN:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onPreviewOpen((String)msg.obj);
                break;
            case MSG_ON_PREVIEW_CLOSE:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onPreviewClose((String)msg.obj);
                break;
            case MSG_ON_CAMERA_OPEN_FINISHED:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onCameraOpenFinish((String)msg.obj);
                break;
        }
    }

    private class BackgroundHandler extends Handler
    {
        public BackgroundHandler(Looper looper)
        {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CameraFragmentAbstract.this.handleBackgroundMessage(msg);
        }
    }

    protected void handleBackgroundMessage(Message message)
    {

    }


}
