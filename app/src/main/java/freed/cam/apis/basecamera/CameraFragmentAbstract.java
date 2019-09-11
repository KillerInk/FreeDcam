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
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.renderscript.RenderScriptManager;
import freed.renderscript.RenderScriptProcessorInterface;
import freed.utils.Log;

/**
 * Created by troop on 06.06.2015.
 * That Fragment is used as base for all camera apis added.
 */
public abstract class CameraFragmentAbstract extends Fragment implements CameraInterface ,CameraWrapperInterface, CameraToMainHandler.MainMessageEvent {
    private final String TAG = CameraFragmentAbstract.class.getSimpleName();



    protected View view;
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

    /**
     * holds handler to invoke stuff in ui or camera thread
     */
    protected MainToCameraHandler mainToCameraHandler;
    protected CameraToMainHandler cameraToMainHandler;

    public static CameraFragmentAbstract getInstance()
    {
        return null;
    }

    public CameraFragmentAbstract()
    {
        cameraChangedListners = new ArrayList<>();
    }

    public void init(MainToCameraHandler mainToCameraHandler, CameraToMainHandler cameraToMainHandler)
    {
        Log.d(TAG, "init handler");
        this.mainToCameraHandler = mainToCameraHandler;
        this.cameraToMainHandler = cameraToMainHandler;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        Log.d(TAG, "onCreateView");
        cameraChangedListners.add(this);
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override
    public void onDestroyView()
    {
        Log.d(TAG, "onDestroyView");

        super.onDestroyView();

    }
    
    public void setRenderScriptManager(RenderScriptManager renderScriptManager)
    {
        this.renderScriptManager = renderScriptManager;
    }

    /**
     * adds a new listner for camera state changes
     * @param cameraChangedListner to add
     */
    public void setCameraEventListner(final CameraStateEvents cameraChangedListner)
    {
        cameraToMainHandler.setCameraStateChangedListner(cameraChangedListner);
    }

    @Override
    public void startCameraAsync() {
        if (mainToCameraHandler != null)
            mainToCameraHandler.startCamera();
        else
            Log.d(TAG, "MainToCameraHandler is null");
    }

    @Override
    public void stopCameraAsync() {
        if (mainToCameraHandler != null)
            mainToCameraHandler.stopCamera();
        else
            Log.d(TAG, "MainToCameraHandler is null");
    }

    @Override
    public void restartCameraAsync() {
        if (mainToCameraHandler != null)
            mainToCameraHandler.restartCamera();
        else
            Log.d(TAG, "MainToCameraHandler is null");
    }

    @Override
    public void startPreviewAsync() {
        if (mainToCameraHandler != null)
            mainToCameraHandler.startPreview();
        else
            Log.d(TAG, "MainToCameraHandler is null");
    }

    @Override
    public void stopPreviewAsync() {
        if (mainToCameraHandler != null)
            mainToCameraHandler.stopPreview();
        else
            Log.d(TAG, "MainToCameraHandler is null");
    }

    @Override
    public void fireCameraOpen()
    {
        if (cameraToMainHandler != null)
            cameraToMainHandler.onCameraOpen();
        else
            Log.d(TAG, "CameraHandler is null");
    }

    @Override
    public void fireCameraError(final String error) {
        if (cameraToMainHandler != null)
            cameraToMainHandler.onCameraError(error);
        else
            Log.d(TAG, "CameraHandler is null");
    }


    @Override
    public void fireCameraClose()
    {
        if (cameraToMainHandler != null)
            cameraToMainHandler.onCameraClose("");
        else
            Log.d(TAG, "CameraHandler is null");
    }

    @Override
    public void firePreviewOpen()
    {
        if (cameraToMainHandler != null)
            cameraToMainHandler.onPreviewOpen("");
        else
            Log.d(TAG, "CameraHandler is null");
    }

    @Override
    public void firePreviewClose() {
        if (cameraToMainHandler != null)
            cameraToMainHandler.onPreviewClose("");
        else
            Log.d(TAG, "CameraHandler is null");
    }

    @Override
    public void fireCameraOpenFinished()
    {
        if (cameraToMainHandler != null)
            cameraToMainHandler.onCameraOpenFinish();
        else
            Log.d(TAG, "CameraHandler is null");
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
    public RenderScriptProcessorInterface getFocusPeakProcessor() {
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
    public Looper getCameraHandlerLooper() {
        return mainToCameraHandler.getCameraLooper();
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
                    cameraChangedListner.onCameraOpen();
                break;
            case CameraToMainHandler.MSG_ON_CAMERA_ERROR:
                for (final CameraStateEvents cameraChangedListner : cameraChangedListners)
                    cameraChangedListner.onCameraError((String)msg.obj);
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
                    cameraChangedListner.onCameraOpenFinish();
                break;
        }
    }
}
