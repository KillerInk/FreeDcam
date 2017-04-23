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
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.FocuspeakProcessor;
import freed.cam.apis.camera2.modules.I_PreviewWrapper;
import freed.cam.apis.camera2.parameters.ParameterHandlerApi2;
import freed.cam.apis.camera2.renderscript.FocuspeakProcessorApi2;
import freed.utils.AppSettingsManager;
import freed.utils.Log;


/**
 * Created by troop on 06.06.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Fragment extends CameraFragmentAbstract implements TextureView.SurfaceTextureListener
{
    private AutoFitTextureView textureView;
    private final String TAG = Camera2Fragment.class.getSimpleName();
    private FocuspeakProcessorApi2 mProcessor;
    private boolean cameraIsOpen = false;



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

        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (cameraLock)
                {
                    parametersHandler = new ParameterHandlerApi2(Camera2Fragment.this);
                    moduleHandler = new ModuleHandlerApi2(Camera2Fragment.this);
                    Focus = new FocusHandler(Camera2Fragment.this);
                    cameraHolder = new CameraHolderApi2(Camera2Fragment.this);
                    ((CameraHolderApi2)cameraHolder).captureSessionHandler = new CaptureSessionHandler(Camera2Fragment.this, ((CameraHolderApi2)cameraHolder).cameraBackroundValuesChangedListner);
                    mProcessor = new FocuspeakProcessorApi2(renderScriptHandler);
                }
            }
        });

        Log.d(TAG, "Constructor done");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    @Override
    public void onResume() {
        super.onResume();
        if (textureView.isAttachedToWindow())
            startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        PreviewSurfaceRdy = false;
        stopPreview();
        stopCamera();
    }

    @Override
    public void startCamera()
    {
        if (mBackgroundHandler == null)
            return;
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (cameraLock) {
                if (!cameraIsOpen)
                    cameraIsOpen = cameraHolder.OpenCamera(getAppSettingsManager().GetCurrentCamera());
                }
            }
        });
    }

    @Override
    public void stopCamera()
    {
        if (mBackgroundHandler == null)
            return;
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (cameraLock) {
                Log.d(TAG, "Stop Camera");

                cameraHolder.CloseCamera();
                cameraIsOpen = false;
                }
            }
        });
    }

    @Override
    public void restartCamera() {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (cameraLock) {
                    Log.d(TAG, "Stop Camera");

                    cameraHolder.CloseCamera();
                    cameraIsOpen = false;
                    if (!cameraIsOpen)
                        cameraIsOpen = cameraHolder.OpenCamera(getAppSettingsManager().GetCurrentCamera());
                }
            }
        });
    }

    @Override
    public void startPreview() {
        if (mBackgroundHandler == null || !cameraIsOpen)
            return;
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (cameraLock){
                Log.d(TAG, "Start Preview");
                I_PreviewWrapper mi = ((I_PreviewWrapper) moduleHandler.getCurrentModule());
                if (mi != null) {
                    mi.startPreview();
                }}
            }
        });
    }

    @Override
    public void stopPreview()
    {
        if (mBackgroundHandler == null)
            return;
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (cameraLock) {
                    Log.d(TAG, "Stop Preview");
                    I_PreviewWrapper mi = ((I_PreviewWrapper) moduleHandler.getCurrentModule());
                    if (mi != null) {
                        mi.stopPreview();
                    }
                }
            }
        });

    }

    @Override
    public void onCameraOpen(final String message)
    {
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (cameraLock){
                    ((ParameterHandlerApi2)parametersHandler).Init();
                    ((CameraHolderApi2)cameraHolder).SetSurface(textureView);

                    Log.d(TAG, "Camera Opened and Preview Started");
                    Camera2Fragment.super.onCameraOpen(message);
                    moduleHandler.setModule(getAppSettingsManager().GetCurrentModule());
                    Camera2Fragment.this.onCameraOpenFinish("");
                }
            }
        });

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
        Log.d(TAG, "SurfaceTextureAvailable");
        if (!PreviewSurfaceRdy) {
            PreviewSurfaceRdy = true;
            if (!cameraIsOpen && isResumed())
                startCamera();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        Log.d(TAG, "Surface destroyed");
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
    public FocuspeakProcessor getFocusPeakProcessor() {
        return mProcessor;
    }

    @Override
    public String getResString(int id) {
        return getAppSettingsManager().getResString(id);
    }

    @Override
    public SurfaceView getSurfaceView() {
        return null;
    }

}
