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
import android.os.HandlerThread;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.camera2.modules.I_PreviewWrapper;
import freed.cam.apis.camera2.parameters.ParameterHandlerApi2;
import freed.renderscript.RenderScriptProcessor;
import freed.renderscript.RenderScriptProcessorInterface;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.viewer.screenslide.MyHistogram;


/**
 * Created by troop on 06.06.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Fragment extends CameraFragmentAbstract implements TextureView.SurfaceTextureListener, CameraValuesChangedCaptureCallback.WaitForFirstFrameCallback
{
    //limits the preview to use maximal that size for preview
    //when set to high it its possbile to get a laggy preview with active focuspeak
    public static int MAX_PREVIEW_WIDTH = 1920;
    public static int MAX_PREVIEW_HEIGHT = 1080;

    private AutoFitTextureView textureView;
    private MyHistogram histogram;
    private final String TAG = Camera2Fragment.class.getSimpleName();
    private RenderScriptProcessor mProcessor;
    private boolean cameraIsOpen = false;
    public CaptureSessionHandler captureSessionHandler;
    public CameraValuesChangedCaptureCallback cameraBackroundValuesChangedListner;

    public static Camera2Fragment getInstance(HandlerThread mBackgroundThread, Object cameraLock)
    {
        Camera2Fragment fragment = new Camera2Fragment();
        fragment.init(mBackgroundThread, cameraLock);
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
        textureView = (AutoFitTextureView) view.findViewById(id.autofitview);
        this.textureView.setSurfaceTextureListener(this);
        this.histogram = (MyHistogram)view.findViewById(id.hisotview);

        mainToCameraHandler.createCamera();
        Log.d(TAG,"Create Camera");


        Log.d(TAG, "Constructor done");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (textureView.isAttachedToWindow() && PreviewSurfaceRdy)
            startCameraAsync();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        stopPreviewAsync();
        stopCameraAsync();
    }

    @Override
    public void onCameraOpen()
    {
        Log.d(TAG, "onCameraOpen, initCamera");
        mainToCameraHandler.initCamera();
    }

    @Override
    public void onCameraOpenFinish() {

    }

    @Override
    public void onCameraClose(String message)
    {
        Log.d(TAG, "onCameraClose");
        cameraIsOpen = false;
        mProcessor.kill();
    }

    @Override
    public void onPreviewOpen(String message) {
        Log.d(TAG, "onPreviewOpen");
    }

    @Override
    public void onPreviewClose(String message) {
        Log.d(TAG, "onPreviewClose");
    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
    {
        Log.d(TAG, "SurfaceTextureAvailable");
        if (!PreviewSurfaceRdy) {
            PreviewSurfaceRdy = true;
            if (!cameraIsOpen && isResumed())
                startCameraAsync();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        Log.d(TAG, "Surface destroyed");
        PreviewSurfaceRdy = false;
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
    public RenderScriptProcessorInterface getFocusPeakProcessor() {
        return mProcessor;
    }

    @Override
    public String getResString(int id) {
        return SettingsManager.getInstance().getResString(id);
    }

    @Override
    public SurfaceView getSurfaceView() {
        return null;
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
        mProcessor = new RenderScriptProcessor(renderScriptManager,histogram, ImageFormat.YUV_420_888);
        parametersHandler = new ParameterHandlerApi2(Camera2Fragment.this);
        moduleHandler = new ModuleHandlerApi2(Camera2Fragment.this);
        Focus = new FocusHandler(Camera2Fragment.this);
        cameraHolder = new CameraHolderApi2(Camera2Fragment.this);
        cameraBackroundValuesChangedListner = new CameraValuesChangedCaptureCallback(this);
        cameraBackroundValuesChangedListner.setWaitForFirstFrameCallback(this);
        captureSessionHandler = new CaptureSessionHandler(Camera2Fragment.this, cameraBackroundValuesChangedListner);
    }

    @Override
    public void initCamera() {
        Log.d(TAG,"Init Camera");
        captureSessionHandler.CreatePreviewRequestBuilder();
        ((ParameterHandlerApi2)parametersHandler).Init();
        ((CameraHolderApi2)cameraHolder).SetSurface(textureView);
        Log.d(TAG, "Camera Opened and Preview Started");
        //Camera2Fragment.this.fireCameraOpen();
        moduleHandler.setModule(SettingsManager.getInstance().GetCurrentModule());
        Camera2Fragment.this.fireCameraOpenFinished();
    }

    @Override
    public void startCamera() {
        if (!cameraIsOpen) {
            Log.d(TAG, "Start Camera");
            cameraIsOpen = cameraHolder.OpenCamera(SettingsManager.getInstance().GetCurrentCamera());
        } else
            Log.d(TAG, "Camera is already open");
    }

    @Override
    public void stopCamera() {
        Log.d(TAG, "Stop Camera");
        captureSessionHandler.Clear();
        cameraHolder.CloseCamera();
        cameraIsOpen = false;
    }

    @Override
    public void restartCamera() {
        Log.d(TAG, "Restart Camera");
        cameraHolder.CloseCamera();
        cameraIsOpen = false;
        if (!cameraIsOpen)
            cameraIsOpen = cameraHolder.OpenCamera(SettingsManager.getInstance().GetCurrentCamera());
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
        I_PreviewWrapper mi = ((I_PreviewWrapper) moduleHandler.getCurrentModule());
        if (mi != null) {
            mi.stopPreview();
        }
    }
}
