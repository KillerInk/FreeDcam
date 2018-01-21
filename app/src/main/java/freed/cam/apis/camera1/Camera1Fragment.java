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
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraToMainHandler;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.camera1.cameraholder.CameraHolderLG;
import freed.cam.apis.camera1.cameraholder.CameraHolderLegacy;
import freed.cam.apis.camera1.cameraholder.CameraHolderMTK;
import freed.cam.apis.camera1.cameraholder.CameraHolderMotoX;
import freed.cam.apis.camera1.cameraholder.CameraHolderSony;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera2.AutoFitTextureView;
import freed.renderscript.RenderScriptManager;
import freed.renderscript.RenderScriptProcessor;
import freed.renderscript.RenderScriptProcessorInterface;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.viewer.screenslide.MyHistogram;

/**
 * Created by troop on 06.06.2015.
 */
public class Camera1Fragment extends CameraFragmentAbstract implements ModuleChangedEvent, SurfaceHolder.Callback, TextureView.SurfaceTextureListener
{
    /*protected ExtendedSurfaceView extendedSurfaceView;
    protected TextureViewRatio preview;*/
    private final String TAG = Camera1Fragment.class.getSimpleName();
    public RenderScriptProcessor focusPeakProcessorAp1;
    private boolean cameraRdy;
    private boolean cameraIsOpen = false;
    AutoFitTextureView textureView;
    MyHistogram histogram;

    public static Camera1Fragment getInstance(HandlerThread mBackgroundThread, Object cameraLock)
    {
        Camera1Fragment fragment = new Camera1Fragment();
        fragment.init(mBackgroundThread, cameraLock);
        return fragment;
    }


    @Override
    public String CameraApiName() {
        return SettingsManager.API_1;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(layout.camerafragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        textureView = view.findViewById(id.autofitview);
        histogram = view.findViewById(id.hisotview);
        /*extendedSurfaceView = (ExtendedSurfaceView) view.findViewById(id.exSurface);
        preview = (TextureViewRatio) view.findViewById(id.textureView_preview);*/
        mainToCameraHandler.createCamera();

        Log.d(TAG, "Ctor done");
        textureView.setSurfaceTextureListener(this);
        /*extendedSurfaceView.getHolder().addCallback(this);*/

    }

    @Override
    public void onResume() {
        super.onResume();
        if (PreviewSurfaceRdy && !cameraIsOpen)
            startCameraAsync();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(moduleHandler != null
                && moduleHandler.getCurrentModule() != null
                && moduleHandler.getCurrentModule().ModuleName() != null
                && moduleHandler.getCurrentModule().ModuleName().equals(getResString(R.string.module_video))
                && moduleHandler.getCurrentModule().IsWorking())
            moduleHandler.getCurrentModule().DoWork();
        stopCameraAsync();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d(TAG, "surface created");
        PreviewSurfaceRdy = true;
        if (!cameraIsOpen)
            startCameraAsync();
        else
            mainToCameraHandler.initCamera();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        PreviewSurfaceRdy =false;
    }


    //this gets called when the cameraholder has open the camera
    @Override
    public void onCameraOpen()
    {
        mainToCameraHandler.initCamera();
    }

    @Override
    public void onCameraOpenFinish() {

    }

    @Override
    public void onCameraClose(String message)
    {
        cameraRdy = false;
        if(focusPeakProcessorAp1 != null)
            focusPeakProcessorAp1.kill();
    }

    @Override
    public void onPreviewOpen(String msg) {

        parametersHandler.setManualSettingsToParameters();
    }

    @Override
    public void onPreviewClose(String message) {
        cameraHolder.ResetPreviewCallback();
    }

    @Override
    public void onCameraError(String error) {

    }


    ParameterEvents onPreviewSizeShouldChange = new ParameterEvents() {

        @Override
        public void onIsSupportedChanged(boolean value) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean value) {

        }

        @Override
        public void onIntValueChanged(int current) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }

        @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        @Override
        public void onStringValueChanged(String value) {
            mainToCameraHandler.removeCallbacks(createPreviewRunner);
            mainToCameraHandler.post(createPreviewRunner);
        }

    };

    private Runnable createPreviewRunner =new Runnable()
    {
        @Override
        public void run() {
            if(moduleHandler.getCurrentModuleName().equals(getResString(R.string.module_picture))
                    || moduleHandler.getCurrentModuleName().equals(getResString(R.string.module_hdr))
                    || moduleHandler.getCurrentModuleName().equals(getResString(R.string.module_interval)))
            {
                Size sizefromCam = new Size(parametersHandler.get(SettingKeys.PictureSize).GetStringValue());
                List<Size> sizes = new ArrayList<>();
                String[] stringsSizes = parametersHandler.get(SettingKeys.PreviewSize).getStringValues();
                final Size size;
                for (String s : stringsSizes) {
                    sizes.add(new Size(s));
                }
                size = getOptimalPreviewSize(sizes, sizefromCam.width, sizefromCam.height, true);

                Log.d(TAG, "set size to " + size.width + "x" + size.height);
                if (focusPeakProcessorAp1 != null && SettingsManager.get(SettingKeys.EnableRenderScript).get()) {
                    if(size == null || textureView.getSurfaceTexture() == null ||
                            (size.height == focusPeakProcessorAp1.getHeight() && size.width == focusPeakProcessorAp1.getWidth()))
                        return;
                    cameraHolder.StopPreview();
                    focusPeakProcessorAp1.kill();
                    cameraHolder.SetSurface((Surface) null);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        textureView.getSurfaceTexture().setDefaultBufferSize(size.width, size.height);
                    }

                    parametersHandler.get(SettingKeys.PreviewSize).SetValue(size.width + "x" + size.height, true);
                    focusPeakProcessorAp1.Reset(size.width, size.height);
                    cameraToMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            focusPeakProcessorAp1.setHistogramEnable(false);
                        }
                    });
                    
                    Surface surface = new Surface(textureView.getSurfaceTexture());
                    focusPeakProcessorAp1.setOutputSurface(surface);
                    parametersHandler.get(SettingKeys.PreviewSize).SetValue(size.width + "x" + size.height, true);
                    cameraHolder.SetSurface(focusPeakProcessorAp1.getInputSurface());
                    cameraToMainHandler.obtainMessage(CameraToMainHandler.MSG_SET_ASPECTRATIO, size).sendToTarget();
                    cameraHolder.StartPreview();
                }
                else
                {
                    cameraHolder.StopPreview();

                    cameraHolder.SetSurface((Surface)null);
                    //textureView.getSurfaceTexture().setDefaultBufferSize(size.width,size.height);
                    Surface surface = new Surface(textureView.getSurfaceTexture());
                    cameraHolder.SetSurface(surface);

                    Log.d(TAG, "set size to " + size.width + "x" + size.height);
                    parametersHandler.get(SettingKeys.PreviewSize).SetValue(size.width + "x" + size.height, true);
                    cameraToMainHandler.obtainMessage(CameraToMainHandler.MSG_SET_ASPECTRATIO, size).sendToTarget();
                    cameraHolder.StartPreview();
                }

            }
            else if (moduleHandler.getCurrentModuleName().equals(getResString(R.string.module_video)))
            {
                Size sizefromCam = new Size("1920x1080");

                List<Size> sizes = new ArrayList<>();
                String[] stringsSizes = parametersHandler.get(SettingKeys.PreviewSize).getStringValues();
                for (String s : stringsSizes) {
                    sizes.add(new Size(s));
                }
                final Size size = getOptimalPreviewSize(sizes, sizefromCam.width, sizefromCam.height,false);

                if(size == null || textureView.getSurfaceTexture() == null)
                    return;
                cameraHolder.StopPreview();
                if (focusPeakProcessorAp1 != null) {
                    focusPeakProcessorAp1.kill();
                    focusPeakProcessorAp1.setOutputSurface(null);
                }

                cameraHolder.SetSurface((Surface)null);
                //textureView.getSurfaceTexture().setDefaultBufferSize(size.width,size.height);
                Surface surface = new Surface(textureView.getSurfaceTexture());
                cameraHolder.SetSurface(surface);

                Log.d(TAG, "set size to " + size.width + "x" + size.height);
                parametersHandler.get(SettingKeys.PreviewSize).SetValue(size.width + "x" + size.height, true);
                cameraToMainHandler.obtainMessage(CameraToMainHandler.MSG_SET_ASPECTRATIO, size).sendToTarget();
                cameraHolder.StartPreview();
            }
        }
    };

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h,boolean FocusPeakClamp) {
        double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;
        Size optimalSize = null;
        List<Size> aspectRatioMatches = new ArrayList<>();
        double ratio;
        for (Size s : sizes)
        {
            ratio = (double) s.width / s.height;
            if (ratio < targetRatio + ASPECT_TOLERANCE && ratio > targetRatio - ASPECT_TOLERANCE) {
                if (s.width <= 2560 && s.height <= 1440 && s.width >= 640 && s.height >= 480)
                    aspectRatioMatches.add(s);
            }
        }

        if (aspectRatioMatches.size() > 0)
        {
            return Collections.max(aspectRatioMatches, new SizeCompare());
        }
        else
            return Collections.max(sizes,new SizeCompare());
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
        if (RenderScriptManager.isSupported() && ((CameraHolder)cameraHolder).canSetSurfaceDirect()) {
            focusPeakProcessorAp1 = new RenderScriptProcessor(renderScriptManager, histogram, ImageFormat.NV21);
        }
        parametersHandler = new ParametersHandler(Camera1Fragment.this);
        moduleHandler.addListner(Camera1Fragment.this);
        Focus = new FocusHandler(Camera1Fragment.this);
        Log.d(TAG, "initModules");
        moduleHandler.initModules();
        Log.d(TAG, "Check Focuspeak");
    }

    @Override
    public void initCamera() {
        cameraRdy = true;
        ((ParametersHandler) parametersHandler).LoadParametersFromCamera();

        parametersHandler.get(SettingKeys.PictureSize).addEventListner(onPreviewSizeShouldChange);

        fireCameraOpenFinished();
    }

    @Override
    public void startCamera() {
        if (!cameraIsOpen)
            cameraIsOpen = cameraHolder.OpenCamera(SettingsManager.getInstance().GetCurrentCamera());
        Log.d(TAG, "startCamera");
    }

    @Override
    public void stopCamera() {
        Log.d(TAG, "Stop Camera");
        if (focusPeakProcessorAp1 != null)
            focusPeakProcessorAp1.kill();
        cameraHolder.CloseCamera();
        cameraIsOpen = false;
    }

    @Override
    public void restartCamera() {
        Log.d(TAG, "Stop Camera");
        if (focusPeakProcessorAp1 != null)
            focusPeakProcessorAp1.kill();
        cameraHolder.CloseCamera();
        cameraIsOpen = false;
        if (!cameraIsOpen)
            cameraIsOpen = cameraHolder.OpenCamera(SettingsManager.getInstance().GetCurrentCamera());
        Log.d(TAG, "startCamera");
    }

    @Override
    public void startPreview() {
        Log.d(TAG, "Start Preview");
        cameraHolder.StartPreview();
    }

    @Override
    public void stopPreview() {
        Log.d(TAG, "Stop Preview");
        cameraHolder.StopPreview();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "surface created");
        PreviewSurfaceRdy = true;
        if (!cameraIsOpen)
            startCameraAsync();
        else
            mainToCameraHandler.initCamera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        PreviewSurfaceRdy =false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private class SizeCompare implements Comparator<Size>
    {
        @Override
        public int compare(Size o1, Size o2) {
            int calc = -1;
            if (o1.width > o2.width)
                calc++;
            if (o1.height > o2.height)
                calc++;
            return calc;
        }
    }

    @Override
    public void onModuleChanged(String module)
    {
        onPreviewSizeShouldChange.onStringValueChanged(parametersHandler.get(SettingKeys.Focuspeak).GetStringValue());
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
        return focusPeakProcessorAp1;
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
    public void handelMainMessage(Message msg) {

        switch (msg.what)
        {
            case CameraToMainHandler.MSG_SET_ASPECTRATIO:
                Size size = (Size)msg.obj;
                if (textureView != null)
                    textureView.setAspectRatio(size.width, size.height);
                if (focusPeakProcessorAp1 != null)
                    focusPeakProcessorAp1.SetAspectRatio(size.width, size.height);
                break;
            default:
                super.handelMainMessage(msg);
        }
    }
}
