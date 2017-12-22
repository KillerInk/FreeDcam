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
import freed.utils.RenderScriptManager;

/**
 * Created by troop on 06.06.2015.
 * That Fragment is used as base for all camera apis added.
 */
public abstract class CameraFragmentAbstract extends Fragment implements CameraWrapperInterface, MainToCameraHandler.CameraMessageEvent, CameraToMainHandler.MainMessageEvent {
    private final String TAG = CameraFragmentAbstract.class.getSimpleName();



    protected View view;
    //holds the appsettings
    protected RenderScriptManager renderScriptManager;

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



    public abstract String CameraApiName();

    protected Object cameraLock;
    /**
     * holds handler to invoke stuff in ui thread
     */
    protected CameraToMainHandler cameraToMainHandler;
    protected MainToCameraHandler mainToCameraHandler;
    protected HandlerThread handlerThread;

    public static CameraFragmentAbstract getInstance(HandlerThread handlerThread, Object cameraLock)
    {
        return null;
    }

    public CameraFragmentAbstract()
    {
        cameraChangedListners = new ArrayList<>();
        cameraToMainHandler = new CameraToMainHandler(this    );
    }

    protected void init(HandlerThread mBackgroundThread, Object cameraLock)
    {
        this.handlerThread = mBackgroundThread;
        this.mainToCameraHandler = new MainToCameraHandler(handlerThread.getLooper(),this);
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
    
    public void SetRenderScriptHandler(RenderScriptManager renderScriptManager)
    {
        this.renderScriptManager = renderScriptManager;
    }

    /**
     * adds a new listner for camera state changes
     * @param cameraChangedListner to add
     */
    public void setCameraStateChangedListner(final CameraStateEvents cameraChangedListner)
    {
        cameraToMainHandler.setCameraStateChangedListner(cameraChangedListner);
    }

    @Override
    public void startCamera() {
        mainToCameraHandler.startCamera();
    }

    @Override
    public void stopCamera() {
        mainToCameraHandler.stopCamera();
    }

    @Override
    public void restartCamera() {
        mainToCameraHandler.restartCamera();
    }

    @Override
    public void startPreview() {
        mainToCameraHandler.startPreview();
    }

    @Override
    public void stopPreview() {
        mainToCameraHandler.stopPreview();
    }

    @Override
    public void onCameraOpen(final String message)
    {
        cameraToMainHandler.onCameraOpen(message);
    }

    @Override
    public void onCameraError(final String error) {
        cameraToMainHandler.onCameraError(error);
    }

    @Override
    public void onCameraStatusChanged(final String status)
    {
        cameraToMainHandler.onCameraStatusChanged(status);
    }

    @Override
    public void onCameraClose(final String message)
    {
        cameraToMainHandler.onCameraClose(message);
    }

    @Override
    public void onPreviewOpen(final String message)
    {
        cameraToMainHandler.onPreviewOpen(message);
    }

    @Override
    public void onPreviewClose(final String message) {
        cameraToMainHandler.onPreviewClose(message);
    }

    @Override
    public void onCameraOpenFinish(final String message)
    {
        cameraToMainHandler.onCameraOpenFinish(message);
    }

    public abstract int getMargineLeft();
    public abstract int getMargineRight();
    public abstract int getMargineTop();
    public abstract int getPreviewWidth();
    public abstract int getPreviewHeight();
    public abstract SurfaceView getSurfaceView();

    @Override
    public RenderScriptManager getRenderScriptManager() {
        return renderScriptManager;
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

    @Override
    public void handelMainMessage(Message msg)
    {
        switch (msg.what) {
            case CameraToMainHandler.MSG_SET_CAMERASTAUSLISTNER:
                cameraChangedListners.add((CameraStateEvents)msg.obj);
                break;
            case CameraToMainHandler.MSG_ON_CAMERA_OPEN:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onCameraOpen((String)msg.obj);
                break;
            case CameraToMainHandler.MSG_ON_CAMERA_ERROR:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onCameraError((String)msg.obj);
                break;
            case CameraToMainHandler.MSG_ON_CAMERA_STATUS_CHANGED:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onCameraStatusChanged((String)msg.obj);
                break;
            case CameraToMainHandler.MSG_ON_CAMERA_CLOSE:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onCameraClose((String)msg.obj);
                break;
            case CameraToMainHandler.MSG_ON_PREVIEW_OPEN:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onPreviewOpen((String)msg.obj);
                break;
            case CameraToMainHandler.MSG_ON_PREVIEW_CLOSE:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onPreviewClose((String)msg.obj);
                break;
            case CameraToMainHandler.MSG_ON_CAMERA_OPEN_FINISHED:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onCameraOpenFinish((String)msg.obj);
                break;
        }
    }
}
