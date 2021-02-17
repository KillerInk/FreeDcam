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

package freed.cam.apis.camera1;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.camera1.cameraholder.CameraHolderLG;
import freed.cam.apis.camera1.cameraholder.CameraHolderLegacy;
import freed.cam.apis.camera1.cameraholder.CameraHolderMTK;
import freed.cam.apis.camera1.cameraholder.CameraHolderMotoX;
import freed.cam.apis.camera1.cameraholder.CameraHolderSony;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.events.CameraStateEvents;
import freed.cam.events.EventBusHelper;
import freed.cam.events.EventBusLifeCycle;
import freed.cam.events.ModuleHasChangedEvent;
import freed.cam.events.ValueChangedEvent;
import freed.cam.previewpostprocessing.Preview;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.renderscript.RenderScriptManager;
import freed.renderscript.RenderScriptProcessor;
import freed.renderscript.RenderScriptProcessorInterface;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.viewer.screenslide.views.MyHistogram;
import freed.views.AutoFitTextureView;

/**
 * Created by troop on 06.06.2015.
 */
public class Camera1Fragment extends CameraFragmentAbstract<ParametersHandler, CameraHolder> implements ModuleChangedEvent, Preview.PreviewEvent, EventBusLifeCycle
{

    //this gets called when the cameraholder has open the camera
    @Subscribe
    public void onCameraOpen(CameraStateEvents.CameraOpenEvent openEvent)
    {
        mainToCameraHandler.initCamera();
    }

    @Subscribe
    public void onCameraClose(CameraStateEvents.CameraCloseEvent cameraCloseEvent)
    {
        if (focusHandler != null)
            ((FocusHandler) focusHandler).stopListning();
        if (parametersHandler != null)
            parametersHandler.unregisterListners();
        getPreview().close();
    }

    @Subscribe
    public void onPreviewOpen(CameraStateEvents.PreviewOpenEvent previewOpenEvent) {

        parametersHandler.setManualSettingsToParameters();
    }

    @Subscribe
    public void onPreviewClose(CameraStateEvents.PreviewCloseEvent previewCloseEvent) {
        cameraHolder.resetPreviewCallback();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCameraChangedAspectRatio(CameraStateEvents.CameraChangedAspectRatioEvent event)
    {
        /*Size size = event.size;
        getPreview().setSize(size.width,size.height);*/
    }

    private final String TAG = Camera1Fragment.class.getSimpleName();
    private boolean cameraIsOpen = false;
    View textureView;
    MyHistogram histogram;

    public static Camera1Fragment getInstance()
    {
        Camera1Fragment fragment = new Camera1Fragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(layout.camerafragment, container, false);
        histogram = view.findViewById(id.hisotview);

        if (SettingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.RenderScript.name()))
            getPreview().initPreview(PreviewPostProcessingModes.RenderScript,getContext(),histogram);
        else
            getPreview().initPreview(PreviewPostProcessingModes.off,getContext(),histogram);
        textureView = getPreview().getPreviewView();
        FrameLayout frameLayout = view.findViewById(id.autofitview);
        frameLayout.addView(textureView);
        getPreview().setPreviewEventListner(this);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        if (mainToCameraHandler != null)
            mainToCameraHandler.createCamera();
        Log.d(TAG, "Ctor done");

    }

    @Override
    public void onResume() {
        super.onResume();
        startListning();
        if (PreviewSurfaceRdy && !cameraIsOpen)
            startCameraAsync();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(moduleHandler != null
                && moduleHandler.getCurrentModule() != null
                && moduleHandler.getCurrentModule().ModuleName() != null
                && moduleHandler.getCurrentModule().ModuleName().equals(FreedApplication.getStringFromRessources(R.string.module_video))
                && moduleHandler.getCurrentModule().IsWorking())
            moduleHandler.getCurrentModule().DoWork();
        stopCameraAsync();
        stopListning();
    }

    @Override
    public void createCamera() {
        Log.d(TAG,"FrameWork:" + SettingsManager.getInstance().getFrameWork() + " openlegacy:" + SettingsManager.get(SettingKeys.openCamera1Legacy).get());

        if (SettingsManager.getInstance().getFrameWork() == Frameworks.LG) {
            cameraHolder = new CameraHolderLG(Camera1Fragment.this, Frameworks.LG);
            Log.d(TAG, "create LG camera");
        }
        else if (SettingsManager.getInstance().getFrameWork() == Frameworks.Moto_Ext) {
            cameraHolder = new CameraHolderMotoX(Camera1Fragment.this, Frameworks.Moto_Ext);
            Log.d(TAG, "create MotoExt camera");
        }
        else if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK) {
            cameraHolder = new CameraHolderMTK(Camera1Fragment.this, Frameworks.MTK);
            Log.d(TAG, "create Mtk camera");
        }
        else if (SettingsManager.getInstance().getFrameWork() == Frameworks.SonyCameraExtension)
        {
            cameraHolder = new CameraHolderSony(Camera1Fragment.this, Frameworks.SonyCameraExtension);
        }
        else if (SettingsManager.get(SettingKeys.openCamera1Legacy).get()) {
            cameraHolder = new CameraHolderLegacy(Camera1Fragment.this, Frameworks.Default);
            Log.d(TAG, "create Legacy camera");
        }
        else {
            cameraHolder = new CameraHolder(Camera1Fragment.this, Frameworks.Default);
            Log.d(TAG, "create Normal camera");
        }
        moduleHandler = new ModuleHandler(Camera1Fragment.this);

        parametersHandler = new ParametersHandler(Camera1Fragment.this);

        //moduleHandler.addListner(Camera1Fragment.this);
        focusHandler = new FocusHandler(Camera1Fragment.this);

        Log.d(TAG, "initModules");
        moduleHandler.initModules();
        Log.d(TAG, "Check Focuspeak");
    }

    @Override
    public void initCamera() {
        ((FocusHandler) focusHandler).startListning();
        parametersHandler.LoadParametersFromCamera();
        CameraStateEvents.fireCameraOpenFinishEvent();
    }

    @Override
    public void startCamera() {
        EventBusHelper.register(this);
        if (!cameraIsOpen)
            cameraIsOpen = cameraHolder.OpenCamera(SettingsManager.getInstance().getCameraIds()[SettingsManager.getInstance().GetCurrentCamera()]);
        Log.d(TAG, "startCamera");
    }

    @Override
    public void stopCamera() {
        EventBusHelper.unregister(this);
        Log.d(TAG, "Stop Camera");
        getPreview().close();
        if (cameraHolder != null)
            cameraHolder.CloseCamera();
        cameraIsOpen = false;
    }

    @Override
    public void restartCamera() {
        Log.d(TAG, "Stop Camera");
        getPreview().close();
        cameraHolder.CloseCamera();
        cameraIsOpen = false;
        if (!cameraIsOpen)
            cameraIsOpen = cameraHolder.OpenCamera(SettingsManager.getInstance().getCameraIds()[SettingsManager.getInstance().GetCurrentCamera()]);
        Log.d(TAG, "startCamera");
    }

    @Override
    public void startPreview() {
        Log.d(TAG, "Start Preview");
        cameraHolder.StartPreview();
    }

    @Override
    public void stopPreview() {
        try {
            Log.d(TAG, "Stop Preview");
            if (cameraHolder != null)
                cameraHolder.StopPreview();
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }


    @Override
    public void startListning() {
        EventBusHelper.register(this);
        if (focusHandler != null)
            ((FocusHandler) focusHandler).startListning();
    }

    @Override
    public void stopListning() {
        EventBusHelper.unregister(this);
        if (focusHandler != null)
            ((FocusHandler) focusHandler).stopListning();
    }

    @Override
    public void onPreviewAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "surface created");
        PreviewSurfaceRdy = true;
        if (!cameraIsOpen)
            startCameraAsync();
        else
            mainToCameraHandler.initCamera();
    }

    @Override
    public void onPreviewSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onPreviewDestroyed(SurfaceTexture surface) {
        PreviewSurfaceRdy =false;
        return false;
    }

    @Override
    public void onPreviewUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onModuleChanged(String module)
    {
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

}
