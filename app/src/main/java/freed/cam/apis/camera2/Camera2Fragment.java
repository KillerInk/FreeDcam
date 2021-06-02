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

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.ActivityInterface;
import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraThreadHandler;
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
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@AndroidEntryPoint
public class Camera2Fragment extends CameraFragmentAbstract<Camera2> implements Preview.PreviewEvent, EventBusLifeCycle
{

    private View textureView;
    private MyHistogram histogram;
    private final String TAG = Camera2Fragment.class.getSimpleName();
    private boolean cameraIsOpen = false;
    public CaptureSessionHandler captureSessionHandler;
    public CameraValuesChangedCaptureCallback cameraBackroundValuesChangedListner;
    private Surface surface;

    @Inject
    SettingsManager settingsManager;

    public static Camera2Fragment getInstance()
    {
        Camera2Fragment fragment = new Camera2Fragment();
        return fragment;
    }

    public Camera2Fragment()
    {}



    public String CameraApiName() {
        return SettingsManager.API_2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(layout.camerafragment, container, false);
        this.histogram = view.findViewById(id.hisotview);
        HistogramController histogramController = new HistogramController(histogram);
        if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.RenderScript.name()))
            preview.initPreview(PreviewPostProcessingModes.RenderScript,getContext(),histogramController);
        else if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.OpenGL.name()))
            preview.initPreview(PreviewPostProcessingModes.OpenGL,getContext(),histogramController);
        else
            preview.initPreview(PreviewPostProcessingModes.off,getContext(),histogramController);
        textureView = preview.getPreviewView();
        FrameLayout frameLayout = view.findViewById(id.autofitview);
        frameLayout.addView(textureView);
        camera = new Camera2();
        CameraThreadHandler.setCameraInterface(camera);

        preview.setPreviewEventListner(this);

        Log.d(TAG, "Constructor done");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CameraThreadHandler.createCameraAsync();
        Log.d(TAG,"Create Camera");
    }

    @Override
    public void startListning() {
        EventBusHelper.register(this);
    }

    @Override
    public void stopListning() {
        EventBusHelper.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        startListning();
        Log.d(TAG, "onResume");
        if (textureView.isAttachedToWindow() && PreviewSurfaceRdy && preview.getSurfaceTexture() != null)
            CameraThreadHandler.startCameraAsync();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        CameraThreadHandler.stopPreviewAsync();
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

    @Subscribe
    public void onCameraOpen(CameraStateEvents.CameraOpenEvent event)
    {
        Log.d(TAG, "onCameraOpen, initCamera");
        CameraThreadHandler.initCameraAsync();
    }

    @Subscribe
    public void onCameraClose(CameraStateEvents.CameraCloseEvent event)
    {
        try {
            Log.d(TAG, "onCameraClose");
            cameraIsOpen = false;
            preview.close();
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    @Subscribe
    public void onPreviewOpen(CameraStateEvents.PreviewOpenEvent message) {
        Log.d(TAG, "onPreviewOpen");
    }

    @Subscribe
    public void onPreviewClose(CameraStateEvents.PreviewCloseEvent message) {
        Log.d(TAG, "onPreviewClose");
    }


    @Override
    public void onPreviewAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "SurfaceTextureAvailable");
        if (!PreviewSurfaceRdy) {
            PreviewSurfaceRdy = true;
            if (!cameraIsOpen && isResumed()) {
                Log.d(TAG, "surface already ready start camera");
                Camera2Fragment.this.surface = new Surface(surface);
                CameraThreadHandler.startCameraAsync();
            }
            else if (cameraIsOpen)
            {
                Log.d(TAG, "Surface now ready camera already open");
                CameraThreadHandler.startPreviewAsync();
                //moduleHandler.setModule(SettingsManager.getInstance().GetCurrentModule());
            }
        }
    }

    @Override
    public void onPreviewSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged WxH " + width+"x"+height);
    }

    @Override
    public boolean onPreviewDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "Surface destroyed");
        PreviewSurfaceRdy = false;
        Camera2Fragment.this.surface = null;
        return false;
    }

    @Override
    public void onPreviewUpdated(SurfaceTexture surface) {

    }



}
