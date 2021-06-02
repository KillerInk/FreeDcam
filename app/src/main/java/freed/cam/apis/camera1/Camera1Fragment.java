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

import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.ActivityInterface;
import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.events.CameraStateEvents;
import freed.cam.events.EventBusHelper;
import freed.cam.events.EventBusLifeCycle;
import freed.cam.histogram.HistogramController;
import freed.cam.previewpostprocessing.Preview;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.viewer.screenslide.views.MyHistogram;

/**
 * Created by troop on 06.06.2015.
 */
@AndroidEntryPoint
public class Camera1Fragment extends CameraFragmentAbstract<Camera1> implements ModuleChangedEvent, Preview.PreviewEvent, EventBusLifeCycle
{

    //this gets called when the cameraholder has open the camera
    @Subscribe
    public void onCameraOpen(CameraStateEvents.CameraOpenEvent openEvent)
    {
        CameraThreadHandler.initCameraAsync();
    }

    @Subscribe
    public void onCameraClose(CameraStateEvents.CameraCloseEvent cameraCloseEvent)
    {
        if (camera != null) {
            if (camera.getFocusHandler() != null)
                ((FocusHandler) camera.getFocusHandler()).stopListning();
            if (camera.getParameterHandler() != null)
                camera.getParameterHandler().unregisterListners();
        }
        preview.close();
    }

    @Subscribe
    public void onPreviewOpen(CameraStateEvents.PreviewOpenEvent previewOpenEvent) {

        camera.getParameterHandler().setManualSettingsToParameters();
    }

    @Subscribe
    public void onPreviewClose(CameraStateEvents.PreviewCloseEvent previewCloseEvent) {
        camera.getCameraHolder().resetPreviewCallback();
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
    @Inject
    SettingsManager settingsManager;

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
        HistogramController histogramController = new HistogramController(histogram);
        if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.RenderScript.name()))
            preview.initPreview(PreviewPostProcessingModes.RenderScript, getContext(), histogramController);
        else if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.OpenGL.name()))
            preview.initPreview(PreviewPostProcessingModes.OpenGL, getContext(), histogramController);
        else
            preview.initPreview(PreviewPostProcessingModes.off,getContext(),histogramController);
        textureView = preview.getPreviewView();
        FrameLayout frameLayout = view.findViewById(id.autofitview);
        frameLayout.addView(textureView);
        preview.setPreviewEventListner(this);
        camera = new Camera1();
        camera.init();
        CameraThreadHandler.setCameraInterface(camera);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        CameraThreadHandler.createCameraAsync();
        Log.d(TAG, "Ctor done");

    }

    @Override
    public void onResume() {
        super.onResume();
        startListning();
        if (PreviewSurfaceRdy && !cameraIsOpen)
            CameraThreadHandler.startCameraAsync();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(camera.getModuleHandler() != null
                && camera.getModuleHandler().getCurrentModule() != null
                && camera.getModuleHandler().getCurrentModule().ModuleName() != null
                && camera.getModuleHandler().getCurrentModule().ModuleName().equals(FreedApplication.getStringFromRessources(R.string.module_video))
                && camera.getModuleHandler().getCurrentModule().IsWorking())
            camera.getModuleHandler().getCurrentModule().DoWork();
        CameraThreadHandler.stopCameraAsync();
        stopListning();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FrameLayout frameLayout = view.findViewById(id.autofitview);
        frameLayout.removeAllViews();
        textureView = null;
    }


    @Override
    public void startListning() {
        EventBusHelper.register(this);
        if (camera.getFocusHandler() != null)
            ((FocusHandler) camera.getFocusHandler()).startListning();
    }

    @Override
    public void stopListning() {
        EventBusHelper.unregister(this);
        if (camera.getFocusHandler() != null)
            ((FocusHandler) camera.getFocusHandler()).stopListning();
    }

    @Override
    public void onPreviewAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "surface created");
        PreviewSurfaceRdy = true;
        if (!cameraIsOpen)
            CameraThreadHandler.startCameraAsync();
        else
            CameraThreadHandler.initCameraAsync();
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

}
