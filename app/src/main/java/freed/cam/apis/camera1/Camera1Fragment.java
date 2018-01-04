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

import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraToMainHandler;
import freed.cam.apis.basecamera.FocuspeakProcessor;
import freed.cam.apis.basecamera.MainToCameraHandler;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.camera1.cameraholder.CameraHolderLG;
import freed.cam.apis.camera1.cameraholder.CameraHolderLegacy;
import freed.cam.apis.camera1.cameraholder.CameraHolderMTK;
import freed.cam.apis.camera1.cameraholder.CameraHolderMotoX;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.renderscript.FocusPeakProcessorAp1;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 06.06.2015.
 */
public class Camera1Fragment extends CameraFragmentAbstract implements ModuleChangedEvent, SurfaceHolder.Callback
{
    protected ExtendedSurfaceView extendedSurfaceView;
    protected TextureViewRatio preview;
    private final String TAG = Camera1Fragment.class.getSimpleName();
    public FocusPeakProcessorAp1 focusPeakProcessorAp1;
    private boolean cameraRdy;
    private boolean cameraIsOpen = false;

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
        return inflater.inflate(layout.cameraholder1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        extendedSurfaceView = (ExtendedSurfaceView) view.findViewById(id.exSurface);
        preview = (TextureViewRatio) view.findViewById(id.textureView_preview);
        mainToCameraHandler.createCamera();

        Log.d(TAG, "Ctor done");
        extendedSurfaceView.getHolder().addCallback(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (PreviewSurfaceRdy && !cameraIsOpen)
            startCamera();
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
        stopCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d(TAG, "surface created");
        PreviewSurfaceRdy = true;
        if (!cameraIsOpen)
            startCamera();
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
    public void onCameraOpen(String message)
    {
        mainToCameraHandler.initCamera();
    }

    @Override
    public void onCameraClose(String message)
    {
        cameraRdy = false;
        super.onCameraClose(message);
    }

    @Override
    public void onPreviewOpen(String message) {
        super.onPreviewOpen(message);
        parametersHandler.setManualSettingsToParameters();
    }

    @Override
    public void onPreviewClose(String message) {
        super.onPreviewClose(message);
        cameraHolder.ResetPreviewCallback();
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

        @Override
        public void onStringValueChanged(String value) {
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
                if(value.equals(getResString(R.string.on_))) {
                    size = getOptimalPreviewSize(sizes, sizefromCam.width, sizefromCam.height, true);
                }
                else {
                    size = getOptimalPreviewSize(sizes, sizefromCam.width, sizefromCam.height, false);
                }
                Log.d(TAG, "set size to " + size.width + "x" + size.height);

                parametersHandler.get(SettingKeys.PreviewSize).SetValue(size.width + "x" + size.height, true);
                cameraToMainHandler.obtainMessage(CameraToMainHandler.MSG_SET_ASPECTRATIO, size).sendToTarget();

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

                Log.d(TAG, "set size to " + size.width + "x" + size.height);
                /*if (getAppSettingsManager().getApiString(AppSettingsManager.VIDEOPROFILE).contains("4k") &&parametersHandler.PreviewSize.GetValues().toString().contains("3840x"))
                {
                    parametersHandler.PreviewSize.SetValue(size.width + "x" + size.height, true);
                    cameraToMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (extendedSurfaceView != null)
                                extendedSurfaceView.setAspectRatio(3840, 2160);
                            if (focusPeakProcessorAp1 != null)
                                focusPeakProcessorAp1.SetAspectRatio(3840, 2160);
                        }
                    });

                }else {*/
                parametersHandler.get(SettingKeys.PreviewSize).SetValue(size.width + "x" + size.height, true);
                cameraToMainHandler.obtainMessage(CameraToMainHandler.MSG_SET_ASPECTRATIO, size).sendToTarget();
                //}


            }
        }

    };

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h,boolean FocusPeakClamp) {
        double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes)
        {
            if(!FocusPeakClamp) {
                if (size.width <= 2560 && size.height <= 1440 && size.width >= 640 && size.height >= 480) {
                    double ratio = (double) size.width / size.height;
                    if (ratio < targetRatio + ASPECT_TOLERANCE && ratio > targetRatio - ASPECT_TOLERANCE) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - h);
                        break;
                    }

                }
            }
            else {
                if (size.width <= 1280 && size.height <= 720 && size.width >= 640 && size.height >= 480) {
                    double ratio = (double) size.width / size.height;
                    if (ratio < targetRatio + ASPECT_TOLERANCE && ratio > targetRatio - ASPECT_TOLERANCE) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - h);
                        break;
                    }

                }
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes)
            {
                if(!FocusPeakClamp) {
                    if (size.width <= 2560 && size.height <= 1440 && size.width >= 640 && size.height >= 480) {
                        if (Math.abs(size.height - h) < minDiff) {
                            optimalSize = size;
                            minDiff = Math.abs(size.height - h);
                        }
                    }
                }
                else {
                    if (size.width <= 1280 && size.height <= 720 && size.width >= 640 && size.height >= 480) {
                        if (Math.abs(size.height - h) < minDiff) {
                            optimalSize = size;
                            minDiff = Math.abs(size.height - h);
                        }
                    }
                }
            }
        }
        Log.d(TAG, "Optimal preview size " + optimalSize.width + "x" + optimalSize.height);
        return optimalSize;
    }

    @Override
    public void onModuleChanged(String module)
    {
        onPreviewSizeShouldChange.onStringValueChanged(parametersHandler.get(SettingKeys.Focuspeak).GetStringValue());
    }

    @Override
    public int getMargineLeft() {
        return extendedSurfaceView.getLeft();
    }

    @Override
    public int getMargineRight() {
        return extendedSurfaceView.getRight();
    }

    @Override
    public int getMargineTop() {
        return extendedSurfaceView.getTop();
    }

    @Override
    public int getPreviewWidth() {
        return extendedSurfaceView.getWidth();
    }

    @Override
    public int getPreviewHeight() {
        return extendedSurfaceView.getHeight();
    }

    @Override
    public FocuspeakProcessor getFocusPeakProcessor() {
        return focusPeakProcessorAp1;
    }

    @Override
    public String getResString(int id) {
        return SettingsManager.getInstance().getResString(id);
    }

    @Override
    public SurfaceView getSurfaceView() {
        return extendedSurfaceView;
    }


    @Override
    public void handelMainMessage(Message msg) {

        switch (msg.what)
        {
            case CameraToMainHandler.MSG_SET_ASPECTRATIO:
                Size size = (Size)msg.obj;
                if (extendedSurfaceView != null)
                    extendedSurfaceView.setAspectRatio(size.width, size.height);
                if (focusPeakProcessorAp1 != null)
                    focusPeakProcessorAp1.SetAspectRatio(size.width, size.height);
                break;
            default:
                super.handelMainMessage(msg);
        }
    }

    @Override
    public void handelCameraMessage(Message message) {
        switch (message.what)
        {
            case MainToCameraHandler.MSG_START_CAMERA:
                if (!cameraIsOpen)
                    cameraIsOpen = cameraHolder.OpenCamera(SettingsManager.getInstance().GetCurrentCamera());
                Log.d(TAG, "startCamera");
                break;
            case MainToCameraHandler.MSG_STOP_CAMERA:
                Log.d(TAG, "Stop Camera");
                if (focusPeakProcessorAp1 != null)
                    focusPeakProcessorAp1.kill();
                cameraHolder.CloseCamera();
                cameraIsOpen = false;
                break;
            case MainToCameraHandler.MSG_RESTART_CAMERA:
                Log.d(TAG, "Stop Camera");
                if (focusPeakProcessorAp1 != null)
                    focusPeakProcessorAp1.kill();
                cameraHolder.CloseCamera();
                cameraIsOpen = false;
                if (!cameraIsOpen)
                    cameraIsOpen = cameraHolder.OpenCamera(SettingsManager.getInstance().GetCurrentCamera());
                Log.d(TAG, "startCamera");
                break;
            case MainToCameraHandler.MSG_START_PREVIEW:
                Log.d(TAG, "Start Preview");
                cameraHolder.StartPreview();
                break;
            case MainToCameraHandler.MSG_STOP_PREVIEW:
                Log.d(TAG, "Stop Preview");
                cameraHolder.StopPreview();
                break;
            case MainToCameraHandler.MSG_INIT_CAMERA:
                cameraRdy = true;
                ((ParametersHandler) parametersHandler).LoadParametersFromCamera();
                parametersHandler.get(SettingKeys.PictureSize).addEventListner(onPreviewSizeShouldChange);
                cameraHolder.SetSurface(extendedSurfaceView.getHolder());
                cameraHolder.StartPreview();
                this.onCameraOpenFinish("");
                break;
            case MainToCameraHandler.MSG_CREATE_CAMERA:
                parametersHandler = new ParametersHandler(Camera1Fragment.this);
                moduleHandler = new ModuleHandler(Camera1Fragment.this);
                moduleHandler.addListner(Camera1Fragment.this);

                Focus = new FocusHandler(Camera1Fragment.this);

                Log.d(TAG,"FrameWork:" + SettingsManager.getInstance().getFrameWork() + " openlegacy:" + SettingsManager.get(SettingKeys.openCamera1Legacy).get());

                if (SettingsManager.getInstance().getFrameWork() == SettingsManager.FRAMEWORK_LG) {
                    cameraHolder = new CameraHolderLG(Camera1Fragment.this, CameraHolder.Frameworks.LG);
                    Log.d(TAG, "create LG camera");
                }
                else if (SettingsManager.getInstance().getFrameWork() == SettingsManager.FRAMEWORK_MOTO_EXT) {
                    cameraHolder = new CameraHolderMotoX(Camera1Fragment.this, CameraHolder.Frameworks.MotoX);
                    Log.d(TAG, "create MotoExt camera");
                }
                else if (SettingsManager.getInstance().getFrameWork() == SettingsManager.FRAMEWORK_MTK) {
                    cameraHolder = new CameraHolderMTK(Camera1Fragment.this, CameraHolder.Frameworks.MTK);
                    Log.d(TAG, "create Mtk camera");
                }
                else if (SettingsManager.get(SettingKeys.openCamera1Legacy).get()) {
                    cameraHolder = new CameraHolderLegacy(Camera1Fragment.this, CameraHolder.Frameworks.Normal);
                    Log.d(TAG, "create Legacy camera");
                }
                else {
                    cameraHolder = new CameraHolder(Camera1Fragment.this, CameraHolder.Frameworks.Normal);
                    Log.d(TAG, "create Normal camera");
                }

                Log.d(TAG, "initModules");
                moduleHandler.initModules();

                Log.d(TAG, "Check Focuspeak");
                if (Build.VERSION.SDK_INT >= 18) {

                    focusPeakProcessorAp1 = new FocusPeakProcessorAp1(preview,Camera1Fragment.this, getContext(), renderScriptManager);
                    setCameraStateChangedListner(focusPeakProcessorAp1);
                }
                else
                    preview.setVisibility(View.GONE);
                break;
        }

    }

}
