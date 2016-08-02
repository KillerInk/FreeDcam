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

package freed.cam.apis.camera2;

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.ActivityInterface;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.AbstractFocusHandler;
import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraHolderInterface;
import freed.cam.apis.basecamera.FocuspeakProcessor;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.camera2.modules.I_PreviewWrapper;
import freed.cam.apis.camera2.parameters.ParameterHandler;
import freed.cam.apis.camera2.renderscript.FocuspeakProcessorApi2;
import freed.utils.AppSettingsManager;
import freed.utils.Logger;
import freed.utils.RenderScriptHandler;


/**
 * Created by troop on 06.06.2015.
 */
public class Camera2Fragment extends CameraFragmentAbstract implements TextureView.SurfaceTextureListener
{
    public CameraHolderApi2 cameraHolder;
    private AutoFitTextureView textureView;
    private final String TAG = Camera2Fragment.class.getSimpleName();
    private FocuspeakProcessorApi2 mProcessor;

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
        super.cameraHolder = cameraHolder;
        parametersHandler = new ParameterHandler(this);
        moduleHandler = new ModuleHandlerApi2(this, renderScriptHandler);
        Focus = new FocusHandler(this);
        cameraHolder = new CameraHolderApi2(this);
        mProcessor = new FocuspeakProcessorApi2(renderScriptHandler);
        Logger.d(TAG, "Constructor done");
        ((ActivityFreeDcamMain) getActivity()).onCameraUiWrapperRdy(this);

        return view;
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
        I_PreviewWrapper mi = ((I_PreviewWrapper) moduleHandler.GetCurrentModule());
        if (mi != null) {
            mi.startPreview();
        }
    }

    @Override
    public void StopPreview()
    {
        Logger.d(TAG, "Stop Preview");
        I_PreviewWrapper mi = ((I_PreviewWrapper) moduleHandler.GetCurrentModule());
        if (mi != null) {
            mi.stopPreview();
        }
    }

    @Override
    public CameraHolderInterface GetCameraHolder() {
        return cameraHolder;
    }

    @Override
    public AbstractParameterHandler GetParameterHandler() {
        return parametersHandler;
    }

    @Override
    public ModuleHandlerAbstract GetModuleHandler() {
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
        mProcessor.kill();
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
    public FocuspeakProcessor getFocusPeakProcessor() {
        return mProcessor;
    }

    @Override
    public RenderScriptHandler getRenderScriptHandler() {
        return renderScriptHandler;
    }

    @Override
    public ActivityInterface getActivityInterface() {
        return (ActivityInterface) getActivity();
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
