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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import freed.ActivityInterface;
import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.AbstractFocusHandler;
import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraHolderInterface;
import freed.cam.apis.basecamera.FocuspeakProcessor;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.camera1.cameraholder.CameraHolderLG;
import freed.cam.apis.camera1.cameraholder.CameraHolderLegacy;
import freed.cam.apis.camera1.cameraholder.CameraHolderMTK;
import freed.cam.apis.camera1.cameraholder.CameraHolderMotoX;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.renderscript.FocusPeakProcessorAp1;
import freed.utils.AppSettingsManager;
import freed.utils.DeviceUtils;
import freed.utils.RenderScriptHandler;

/**
 * Created by troop on 06.06.2015.
 */
public class Camera1Fragment extends CameraFragmentAbstract implements ModuleChangedEvent, SurfaceHolder.Callback
{
    protected ExtendedSurfaceView extendedSurfaceView;
    protected TextureViewRatio preview;
    private final String TAG = Camera1Fragment.class.getSimpleName();
    public FocusPeakProcessorAp1 focusPeakProcessorAp1;
    boolean cameraRdy;

    @Override
    public String CameraApiName() {
        return AppSettingsManager.API_1;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(layout.cameraholder1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        extendedSurfaceView = (ExtendedSurfaceView) view.findViewById(id.exSurface);
        preview = (TextureViewRatio) view.findViewById(id.textureView_preview);

        parametersHandler = new ParametersHandler(this);
        this.extendedSurfaceView.ParametersHandler = parametersHandler;
        moduleHandler = new ModuleHandler(this);
        moduleHandler.addListner(this);

        Focus = new FocusHandler(this);

        if (hasLGFramework())
            cameraHolder = new CameraHolderLG(this, CameraHolder.Frameworks.LG);
        else if (isMotoExt())
            cameraHolder = new CameraHolderMotoX(this, CameraHolder.Frameworks.MotoX);
        else if (isMTKDevice())
            cameraHolder = new CameraHolderMTK(this, CameraHolder.Frameworks.MTK);
        else if (appSettingsManager.IsCamera2FullSupported().equals(KEYS.TRUE) && appSettingsManager.getDevice() != DeviceUtils.Devices.OnePlusOne)
            cameraHolder = new CameraHolderLegacy(this, CameraHolder.Frameworks.Normal);
        else
            cameraHolder = new CameraHolder(this, CameraHolder.Frameworks.Normal);
        moduleHandler.initModules();

        if (Build.VERSION.SDK_INT >= 18) {
            focusPeakProcessorAp1 = new FocusPeakProcessorAp1(preview,this, getContext(), renderScriptHandler);
            SetCameraStateChangedListner(focusPeakProcessorAp1);
        }
        else
            preview.setVisibility(View.GONE);
        Log.d(TAG, "Ctor done");

        extendedSurfaceView.getHolder().addCallback(this);
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onPause() {
        super.onPause();
        try {
            if(moduleHandler.GetCurrentModule().ModuleName().equals(KEYS.MODULE_VIDEO) && moduleHandler.GetCurrentModule().IsWorking())
                moduleHandler.GetCurrentModule().DoWork();
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
    }

    private boolean hasLGFramework()
    {
        try {
            Class c = Class.forName("com.lge.hardware.LGCamera");
            Log.d(TAG, "Has Lg Framework");
            c = Class.forName("com.lge.media.CamcorderProfileEx");
            Log.d(TAG, "Has Lg Framework");
            return true;

        } catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e) {

            Log.d(TAG, "No LG Framework");
            return false;
        }
    }

    private boolean isMotoExt()
    {
        try {
            Class c = Class.forName("com.motorola.android.camera.CameraMotExt");
            Log.d(TAG, "Has Moto Framework");
            c = Class.forName("com.motorola.android.media.MediaRecorderExt");
            Log.d(TAG, "Has Moto Framework");
            return true;

        } catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e) {
            Log.d(TAG, "No Moto Framework");
            return false;
        }

    }

    private boolean isMTKDevice()
    {
        try
        {
            Class camera = Class.forName("android.hardware.Camera");
            Method[] meths = camera.getMethods();
            Method app = null;
            for (Method m : meths)
            {
                if (m.getName().equals("setProperty"))
                    app = m;
            }
            if (app != null) {
                Log.d(TAG,"MTK Framework found");
                return true;
            }
            Log.d(TAG, "MTK Framework not found");
            return false;
        }
        catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e)
        {
            e.printStackTrace();
            Log.d(TAG, "MTK Framework not found");
            return false;
        }
    }

    @Override
    public void StartCamera() {
        cameraHolder.OpenCamera(appSettingsManager.GetCurrentCamera());
        Log.d(TAG, "opencamera");
    }

    @Override
    public void StopCamera() {
        Log.d(TAG, "Stop Camera");
        focusPeakProcessorAp1.kill();
        cameraHolder.CloseCamera();
    }

    @Override
    public void StartPreview() {
        cameraHolder.StartPreview();
    }

    @Override
    public void StopPreview() {
        Log.d(TAG, "Stop Preview");
        cameraHolder.StopPreview();
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
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d(TAG, "surface created");
        PreviewSurfaceRdy = true;
        StartCamera();
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
        cameraRdy = true;
        super.onCameraOpen(message);
        ((ParametersHandler) parametersHandler).LoadParametersFromCamera();
        parametersHandler.PictureSize.addEventListner(onPreviewSizeShouldChange);
        cameraHolder.SetSurface(extendedSurfaceView.getHolder());
        cameraHolder.StartPreview();
        this.onCameraOpenFinish("");
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

    }

    @Override
    public void onPreviewClose(String message) {
        super.onPreviewClose(message);
        cameraHolder.ResetPreviewCallback();
    }


    AbstractModeParameter.I_ModeParameterEvent onPreviewSizeShouldChange = new AbstractModeParameter.I_ModeParameterEvent() {

        @Override
        public void onParameterValueChanged(String val)
        {
            if(moduleHandler.GetCurrentModuleName().equals(KEYS.MODULE_PICTURE)
                    || moduleHandler.GetCurrentModuleName().equals(KEYS.MODULE_HDR)
                    || moduleHandler.GetCurrentModuleName().equals(KEYS.MODULE_INTERVAL)
                    || moduleHandler.GetCurrentModuleName().equals(KEYS.MODULE_STACKING))
            {
                Size sizefromCam = new Size(parametersHandler.PictureSize.GetValue());
                List<Size> sizes = new ArrayList<>();
                String[] stringsSizes = parametersHandler.PreviewSize.GetValues();
                final Size size;
                for (String s : stringsSizes) {
                    sizes.add(new Size(s));
                }
                if(val.equals(KEYS.ON)) {
                     size = getOptimalPreviewSize(sizes, sizefromCam.width, sizefromCam.height, true);
                }
                else {
                      size = getOptimalPreviewSize(sizes, sizefromCam.width, sizefromCam.height, false);
                }
                Log.d(TAG, "set size to " + size.width + "x" + size.height);

                parametersHandler.PreviewSize.SetValue(size.width + "x" + size.height, true);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (extendedSurfaceView != null)
                            extendedSurfaceView.setAspectRatio(size.width, size.height);
                        if (focusPeakProcessorAp1 != null)
                            focusPeakProcessorAp1.SetAspectRatio(size.width,size.height);
                    }
                });

            }
            else if (moduleHandler.GetCurrentModuleName().equals(KEYS.MODULE_VIDEO))
            {
                Size sizefromCam = new Size("1920x1080");

                List<Size> sizes = new ArrayList<>();
                String[] stringsSizes = parametersHandler.PreviewSize.GetValues();
                for (String s : stringsSizes) {
                    sizes.add(new Size(s));
                }
                final Size size = getOptimalPreviewSize(sizes, sizefromCam.width, sizefromCam.height,false);

                Log.d(TAG, "set size to " + size.width + "x" + size.height);
                if (appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE).contains("4k") &&parametersHandler.PreviewSize.GetValues().toString().contains("3840x"))
                {
                    parametersHandler.PreviewSize.SetValue(size.width + "x" + size.height, true);
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (extendedSurfaceView != null)
                                extendedSurfaceView.setAspectRatio(3840, 2160);
                            if (focusPeakProcessorAp1 != null)
                                focusPeakProcessorAp1.SetAspectRatio(3840, 2160);
                        }
                    });

                }else {
                    parametersHandler.PreviewSize.SetValue(size.width + "x" + size.height, true);
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (extendedSurfaceView != null)
                                extendedSurfaceView.setAspectRatio(size.width, size.height);
                            if (focusPeakProcessorAp1 != null)
                                focusPeakProcessorAp1.SetAspectRatio(size.width, size.height);
                        }
                    });
                }

            }
        }

        @Override
        public void onParameterIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onParameterIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onParameterValuesChanged(String[] values) {

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
        onPreviewSizeShouldChange.onParameterValueChanged(parametersHandler.Focuspeak.GetValue());
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
    public boolean isAeMeteringSupported() {
        return Focus.isAeMeteringSupported();
    }

    @Override
    public FocuspeakProcessor getFocusPeakProcessor() {
        return focusPeakProcessorAp1;
    }

    @Override
    public RenderScriptHandler getRenderScriptHandler() {
        return renderScriptHandler;
    }

    @Override
    public ActivityInterface getActivityInterface() {
        return (ActivityInterface)getActivity();
    }

    @Override
    public SurfaceView getSurfaceView() {
        return extendedSurfaceView;
    }

    @Override
    public AbstractFocusHandler getFocusHandler() {
        return Focus;
    }

}
