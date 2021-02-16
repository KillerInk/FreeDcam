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
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.camera2.modules.I_PreviewWrapper;
import freed.cam.apis.camera2.parameters.ParameterHandlerApi2;
import freed.cam.events.CameraStateEvents;
import freed.cam.events.EventBusHelper;
import freed.cam.events.EventBusLifeCycle;
import freed.cam.previewpostprocessing.Preview;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.renderscript.RenderScriptManager;
import freed.renderscript.RenderScriptProcessor;
import freed.renderscript.RenderScriptProcessorInterface;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.viewer.screenslide.views.MyHistogram;
import freed.views.AutoFitTextureView;


/**
 * Created by troop on 06.06.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Fragment extends CameraFragmentAbstract<ParameterHandlerApi2, CameraHolderApi2> implements Preview.PreviewEvent, CameraValuesChangedCaptureCallback.WaitForFirstFrameCallback, EventBusLifeCycle
{
    //limits the preview to use maximal that size for preview
    //when set to high it its possbile to get a laggy preview with active focuspeak
    public static int MAX_PREVIEW_WIDTH = 1920;
    public static int MAX_PREVIEW_HEIGHT = 1080;

    private View textureView;
    private MyHistogram histogram;
    private final String TAG = Camera2Fragment.class.getSimpleName();
    private boolean cameraIsOpen = false;
    public CaptureSessionHandler captureSessionHandler;
    public CameraValuesChangedCaptureCallback cameraBackroundValuesChangedListner;
    private Surface surface;

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
        if (SettingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.RenderScript.name()))
            getPreview().initPreview(PreviewPostProcessingModes.RenderScript,getContext(),histogram);
        else
            getPreview().initPreview(PreviewPostProcessingModes.off,getContext(),histogram);
        textureView = getPreview().getPreviewView();
        FrameLayout frameLayout = view.findViewById(id.autofitview);
        frameLayout.addView(textureView);

        getPreview().setPreviewEventListner(this);

        Log.d(TAG, "Constructor done");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mainToCameraHandler == null)
            throw new NullPointerException("main to camera handler is null");
        mainToCameraHandler.createCamera();
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
        if (textureView.isAttachedToWindow() && PreviewSurfaceRdy && getPreview().getSurfaceTexture() != null)
            startCameraAsync();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        stopPreviewAsync();
        stopCameraAsync();
        stopListning();
    }

    @Subscribe
    public void onCameraOpen(CameraStateEvents.CameraOpenEvent event)
    {
        Log.d(TAG, "onCameraOpen, initCamera");
        mainToCameraHandler.initCamera();
    }

    @Subscribe
    public void onCameraClose(CameraStateEvents.CameraCloseEvent event)
    {
        try {
            Log.d(TAG, "onCameraClose");
            cameraIsOpen = false;
            getPreview().close();
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
                startCameraAsync();
            }
            else if (cameraIsOpen)
            {
                Log.d(TAG, "Surface now ready camera already open");
                startPreviewAsync();
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


/*    public TextureView getTexturView()
    {
        return getPreview().getSurfaceTexture();
    }*/

    public Surface getPreviewSurface()
    {
        return surface;
    }

    @Override
    public void onFirstFrame() {
        Log.d(TAG,"onFirstFrame");
        //workaround, that seem to kill front camera when switching picformat
        if (!SettingsManager.getInstance().getIsFrontCamera())
            parametersHandler.setManualSettingsToParameters();
    }

    public Size getSizeForPreviewDependingOnImageSize(int imageformat, int mImageWidth, int mImageHeight)
    {
        List<Size> sizes = new ArrayList<>();
        Size[] choices = ((CameraHolderApi2)cameraHolder).map.getOutputSizes(imageformat);
        Point displaysize = captureSessionHandler.getDisplaySize();
        double ratio = (double)mImageWidth/mImageHeight;
        for (Size s : choices)
        {
            if (s.getWidth() <= MAX_PREVIEW_WIDTH && s.getHeight() <= MAX_PREVIEW_HEIGHT && ratioMatch((double)s.getWidth()/s.getHeight(),ratio))
                sizes.add(s);
        }
        if (sizes.size() > 0) {
            return Collections.max(sizes, new CameraHolderApi2.CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable previewSize size");
            Size s = choices[0];
            if (s.getWidth() > displaysize.x && s.getHeight() > displaysize.y)
                return new Size(displaysize.x, displaysize.y);
            return choices[0];
        }
    }

    private boolean ratioMatch(double preview, double image)
    {
        double rangelimter = 0.1;

        if (preview+rangelimter >= image && preview-rangelimter <= image)
            return true;
        else
            return false;
    }

    @Override
    public void createCamera() {
        Log.d(TAG, "createCamera");
        parametersHandler = new ParameterHandlerApi2(Camera2Fragment.this);
        moduleHandler = new ModuleHandlerApi2(Camera2Fragment.this);
        focusHandler = new FocusHandler(Camera2Fragment.this);

        cameraHolder = new CameraHolderApi2(Camera2Fragment.this);
        cameraBackroundValuesChangedListner = new CameraValuesChangedCaptureCallback(this);
        cameraBackroundValuesChangedListner.setWaitForFirstFrameCallback(this);
        captureSessionHandler = new CaptureSessionHandler(this, cameraBackroundValuesChangedListner);
    }

    @Override
    public void initCamera() {
        Log.d(TAG,"initCamera");
        captureSessionHandler.CreatePreviewRequestBuilder();
        ((FocusHandler) focusHandler).startListning();
        parametersHandler.Init();
        //cameraHolder.SetSurface(getPreview().getSurfaceTexture());
        Log.d(TAG, "initCamera Camera Opened and Preview Started");

        CameraStateEvents.fireCameraOpenFinishEvent();
        moduleHandler.setModule(SettingsManager.getInstance().GetCurrentModule());
        //parametersHandler.SetAppSettingsToParameters();
    }

    @Override
    public void startCamera() {
        if (!cameraIsOpen && cameraHolder != null) {
            Log.d(TAG, "Start Camera");
            cameraIsOpen = cameraHolder.OpenCamera(SettingsManager.getInstance().getCameraIds()[SettingsManager.getInstance().GetCurrentCamera()]);
        } else
            Log.d(TAG, "Camera is already open");
    }

    @Override
    public void stopCamera() {
        try {
            Log.d(TAG, "Stop Camera");
            if (cameraHolder != null)
                cameraHolder.CloseCamera();
            cameraIsOpen = false;
            if (focusHandler != null)
                ((FocusHandler) focusHandler).stopListning();
            if (parametersHandler != null)
                parametersHandler.unregisterListners();
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    @Override
    public void restartCamera() {
        Log.d(TAG, "Restart Camera");
        stopCamera();
        startCamera();
    }

    @Override
    public void startPreview() {
        Log.d(TAG, "Start Preview");
        I_PreviewWrapper mi = ((I_PreviewWrapper) moduleHandler.getCurrentModule());
        if (mi != null) {
            mi.startPreview();
        }
    }

    @Override
    public void stopPreview() {
        Log.d(TAG, "Stop Preview");
        if (moduleHandler == null)
            return;
        I_PreviewWrapper mi = ((I_PreviewWrapper) moduleHandler.getCurrentModule());
        if (mi != null) {
            mi.stopPreview();
        }
    }


}
