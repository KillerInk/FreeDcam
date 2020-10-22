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

package com.troop.freedcam.camera.camera1;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.R.id;
import com.troop.freedcam.camera.R.layout;
import com.troop.freedcam.camera.basecamera.CameraFragmentAbstract;
import com.troop.freedcam.camera.basecamera.modules.ModuleChangedEvent;
import com.troop.freedcam.camera.camera1.cameraholder.CameraHolderLG;
import com.troop.freedcam.camera.camera1.cameraholder.CameraHolderLegacy;
import com.troop.freedcam.camera.camera1.cameraholder.CameraHolderMTK;
import com.troop.freedcam.camera.camera1.cameraholder.CameraHolderMotoX;
import com.troop.freedcam.camera.camera1.cameraholder.CameraHolderSony;
import com.troop.freedcam.camera.camera1.parameters.ParametersHandler;
import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.EventBusLifeCycle;
import com.troop.freedcam.eventbus.events.CameraStateEvents;
import com.troop.freedcam.eventbus.events.ModuleHasChangedEvent;
import com.troop.freedcam.eventbus.events.ValueChangedEvent;
import com.troop.freedcam.processor.RenderScriptManager;
import com.troop.freedcam.processor.RenderScriptProcessor;
import com.troop.freedcam.processor.RenderScriptProcessorInterface;
import com.troop.freedcam.settings.Frameworks;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.Log;
import com.troop.freedcam.utils.Size;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by troop on 06.06.2015.
 */
public class Camera1Fragment extends CameraFragmentAbstract<ParametersHandler, CameraHolder> implements ModuleChangedEvent, TextureView.SurfaceTextureListener, EventBusLifeCycle
{

    private final String TAG = Camera1Fragment.class.getSimpleName();
    public RenderScriptProcessor focusPeakProcessorAp1;
    private boolean cameraIsOpen = false;

    public static Camera1Fragment getInstance()
    {
        Camera1Fragment fragment = new Camera1Fragment();
        return fragment;
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
        //histogram = view.findViewById(id.hisotview);
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
                && moduleHandler.getCurrentModule().ModuleName().equals(ContextApplication.getStringFromRessources(R.string.module_video))
                && moduleHandler.getCurrentModule().IsWorking())
            moduleHandler.getCurrentModule().DoWork();
        stopCameraAsync();
        stopListning();
    }

    private Size getSizefromString(String s)
    {
        String split[] = s.split("x");
        return new Size(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    private Runnable createPreviewRunner =new Runnable()
    {
        @Override
        public void run() {
            Log.d(TAG, "createPreviewRunner.run()");
            if (!PreviewSurfaceRdy) {
                Log.d(TAG, "FAILED TO GET SURFACE FROM TEXTUREVIEW ################");
                return;
            }
            if(moduleHandler.getCurrentModuleName().equals(ContextApplication.getStringFromRessources(R.string.module_picture))
                    || moduleHandler.getCurrentModuleName().equals(ContextApplication.getStringFromRessources(R.string.module_hdr))
                    || moduleHandler.getCurrentModuleName().equals(ContextApplication.getStringFromRessources(R.string.module_interval)))
            {
                Size sizefromCam = getSizefromString(parametersHandler.get(SettingKeys.PictureSize).GetStringValue());
                List<Size> sizes = new ArrayList<>();
                String[] stringsSizes = parametersHandler.get(SettingKeys.PreviewSize).getStringValues();
                final Size size;
                for (String s : stringsSizes) {
                    sizes.add(getSizefromString(s));
                }
                size = getOptimalPreviewSize(sizes, sizefromCam.getWidth(), sizefromCam.getHeight(), true);

                Log.d(TAG, "set size to " + size.getWidth() + "x" + size.getHeight());
                if (focusPeakProcessorAp1 != null && SettingsManager.getGlobal(SettingKeys.EnableRenderScript).get()) {
                    if(size == null || textureView.getSurfaceTexture() == null ||
                            (size.getHeight() == focusPeakProcessorAp1.getHeight() && size.getWidth() == focusPeakProcessorAp1.getWidth()))
                        return;
                    cameraHolder.StopPreview();
                    focusPeakProcessorAp1.kill();
                    cameraHolder.setSurface((Surface) null);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        textureView.getSurfaceTexture().setDefaultBufferSize(size.getWidth(), size.getHeight());
                    }

                    parametersHandler.get(SettingKeys.PreviewSize).SetValue(size.getWidth() + "x" + size.getHeight(), true);
                    Surface surface = new Surface(textureView.getSurfaceTexture());
                    focusPeakProcessorAp1.Reset(size.getWidth(), size.getHeight(),surface);
                    cameraToMainHandler.post(() -> focusPeakProcessorAp1.setHistogramEnable(false));

                    parametersHandler.get(SettingKeys.PreviewSize).SetValue(size.getWidth() + "x" + size.getHeight(), true);
                    cameraHolder.setSurface(focusPeakProcessorAp1.getInputSurface());
                    CameraStateEvents.fireCameraAspectRatioChangedEvent(size);
                    focusPeakProcessorAp1.start();
                    cameraHolder.StartPreview();
                }
                else
                {
                    cameraHolder.StopPreview();
                    if (((CameraHolder)cameraHolder).canSetSurfaceDirect()) {
                        cameraHolder.setSurface((Surface)null);
                        Surface surface = new Surface(textureView.getSurfaceTexture());
                        cameraHolder.setSurface(surface);
                    }
                    else
                        ((CameraHolder)cameraHolder).setTextureView(textureView);

                    Log.d(TAG, "set size to " + size.getWidth() + "x" + size.getHeight());
                    parametersHandler.get(SettingKeys.PreviewSize).SetValue(size.getWidth() + "x" + size.getHeight(), true);
                    CameraStateEvents.fireCameraAspectRatioChangedEvent(size);
                    cameraHolder.StartPreview();
                }

            }
            else if (moduleHandler.getCurrentModuleName().equals(ContextApplication.getStringFromRessources(R.string.module_video)))
            {
                Size sizefromCam = new Size(1920,1080);

                List<Size> sizes = new ArrayList<>();
                String[] stringsSizes = parametersHandler.get(SettingKeys.PreviewSize).getStringValues();
                for (String s : stringsSizes) {
                    String split[] = s.split("x");
                    sizes.add(new Size(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
                }
                final Size size = getOptimalPreviewSize(sizes, sizefromCam.getWidth(), sizefromCam.getHeight(),false);

                if(size == null || textureView.getSurfaceTexture() == null)
                    return;
                cameraHolder.StopPreview();
                if (focusPeakProcessorAp1 != null) {
                    focusPeakProcessorAp1.kill();
                }
                
                if (((CameraHolder)cameraHolder).canSetSurfaceDirect()) {
                    cameraHolder.setSurface((Surface)null);
                    Surface surface = new Surface(textureView.getSurfaceTexture());
                    cameraHolder.setSurface(surface);
                }
                else
                    ((CameraHolder)cameraHolder).setTextureView(textureView);

                Log.d(TAG, "set size to " + size.getWidth() + "x" + size.getHeight());
                parametersHandler.get(SettingKeys.PreviewSize).SetValue(size.getWidth() + "x" + size.getHeight(), true);
                CameraStateEvents.fireCameraAspectRatioChangedEvent(size);
                cameraHolder.StartPreview();
            }
            Log.d(TAG, "createPreviewRunner.run() done");
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
            ratio = (double) s.getWidth() / s.getHeight();
            if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE) {
                if (s.getWidth() <= 2560 && s.getHeight() <= 1440 && s.getWidth() >= 800 && s.getHeight() >= 600)
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
            focusPeakProcessorAp1 = new RenderScriptProcessor(renderScriptManager,ImageFormat.NV21);
        }
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
        ((ParametersHandler) parametersHandler).LoadParametersFromCamera();
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
        if (focusPeakProcessorAp1 != null)
            focusPeakProcessorAp1.kill();
        if (cameraHolder != null)
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

    private class SizeCompare implements Comparator<Size>
    {
        @Override
        public int compare(Size o1, Size o2) {
            int calc = -1;
            if (o1.getWidth() > o2.getWidth())
                calc++;
            if (o1.getHeight() > o2.getHeight())
                calc++;
            return calc;
        }
    }



    @Override
    public void onModuleChanged(String module)
    {
        if (parametersHandler.get(SettingKeys.Focuspeak) == null)
            return;
        try {
            mainToCameraHandler.removeCallbacks(createPreviewRunner);
            mainToCameraHandler.post(createPreviewRunner);
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }

    }

    @Override
    public RenderScriptProcessorInterface getFocusPeakProcessor() {
        return focusPeakProcessorAp1;
    }





}
