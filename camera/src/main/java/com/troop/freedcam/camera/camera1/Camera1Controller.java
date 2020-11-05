package com.troop.freedcam.camera.camera1;

import android.graphics.ImageFormat;
import android.os.Build;
import android.view.Surface;

import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.basecamera.AbstractCameraController;
import com.troop.freedcam.camera.camera1.cameraholder.CameraHolderLG;
import com.troop.freedcam.camera.camera1.cameraholder.CameraHolderLegacy;
import com.troop.freedcam.camera.camera1.cameraholder.CameraHolderMTK;
import com.troop.freedcam.camera.camera1.cameraholder.CameraHolderMotoX;
import com.troop.freedcam.camera.camera1.cameraholder.CameraHolderSony;
import com.troop.freedcam.camera.camera1.parameters.ParametersHandler;
import com.troop.freedcam.eventbus.EventBusHelper;
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

public class Camera1Controller extends AbstractCameraController<ParametersHandler,CameraHolder,FocusHandler,ModuleHandler> {

    private final String TAG = Camera1Controller.class.getSimpleName();

    private RenderScriptProcessor focusPeakProcessorAp1;
    private boolean cameraIsOpen = false;

    @Subscribe
    public void onModuleHasChangedEvent(ModuleHasChangedEvent event)
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

    @Subscribe
    public void onPictureSizeChanged(ValueChangedEvent<String> valueChangedEvent)
    {
        if (valueChangedEvent.key == SettingKeys.PictureSize)
        {
            mainToCameraHandler.removeCallbacks(createPreviewRunner);
            mainToCameraHandler.post(createPreviewRunner);
        }
    }

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
        if(focusPeakProcessorAp1 != null)
            focusPeakProcessorAp1.kill();
    }

    @Subscribe
    public void onPreviewOpen(CameraStateEvents.PreviewOpenEvent previewOpenEvent) {

        parametersHandler.setManualSettingsToParameters();
    }

    @Subscribe
    public void onPreviewClose(CameraStateEvents.PreviewCloseEvent previewCloseEvent) {
        cameraHolder.resetPreviewCallback();
    }



    @Override
    public void createCamera() {
        Log.d(TAG,"FrameWork:" + SettingsManager.getInstance().getFrameWork() + " openlegacy:" + SettingsManager.get(SettingKeys.openCamera1Legacy).get());

        if (SettingsManager.getInstance().getFrameWork() == Frameworks.LG) {
            cameraHolder = new CameraHolderLG(this, Frameworks.LG);
            Log.d(TAG, "create LG camera");
        }
        else if (SettingsManager.getInstance().getFrameWork() == Frameworks.Moto_Ext) {
            cameraHolder = new CameraHolderMotoX(this, Frameworks.Moto_Ext);
            Log.d(TAG, "create MotoExt camera");
        }
        else if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK) {
            cameraHolder = new CameraHolderMTK(this, Frameworks.MTK);
            Log.d(TAG, "create Mtk camera");
        }
        else if (SettingsManager.getInstance().getFrameWork() == Frameworks.SonyCameraExtension)
        {
            cameraHolder = new CameraHolderSony(this, Frameworks.SonyCameraExtension);
        }
        else if (SettingsManager.get(SettingKeys.openCamera1Legacy).get()) {
            cameraHolder = new CameraHolderLegacy(this, Frameworks.Default);
            Log.d(TAG, "create Legacy camera");
        }
        else {
            cameraHolder = new CameraHolder(this, Frameworks.Default);
            Log.d(TAG, "create Normal camera");
        }
        moduleHandler = new ModuleHandler(this);
        if (RenderScriptManager.isSupported() && ((CameraHolder)cameraHolder).canSetSurfaceDirect()) {
            focusPeakProcessorAp1 = new RenderScriptProcessor(renderScriptManager, ImageFormat.NV21);
        }
        parametersHandler = new ParametersHandler(this);

        //moduleHandler.addListner(Camera1Fragment.this);
        focusHandler = new FocusHandler(this);

        Log.d(TAG, "initModules");
        moduleHandler.initModules();
        Log.d(TAG, "Check Focuspeak");
    }

    @Override
    public void initCamera() {
        focusHandler.startListning();
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


    private Runnable createPreviewRunner =new Runnable()
    {
        @Override
        public void run() {
            Log.d(TAG, "createPreviewRunner.run()");
            if (textureHolder == null) {
                Log.d(TAG, "FAILED TO GET SURFACE FROM TEXTUREVIEW ################");
                return;
            }
            if(moduleHandler.getCurrentModuleName().equals(ContextApplication.getStringFromRessources(R.string.module_picture))
                    || moduleHandler.getCurrentModuleName().equals(ContextApplication.getStringFromRessources(R.string.module_hdr))
                    || moduleHandler.getCurrentModuleName().equals(ContextApplication.getStringFromRessources(R.string.module_interval)))
            {
                Size sizefromCam = new Size(parametersHandler.get(SettingKeys.PictureSize).GetStringValue());
                List<Size> sizes = new ArrayList<>();
                String[] stringsSizes = parametersHandler.get(SettingKeys.PreviewSize).getStringValues();
                final Size size;
                for (String s : stringsSizes) {
                    sizes.add(new Size(s));
                }
                size = getOptimalPreviewSize(sizes, sizefromCam.getWidth(), sizefromCam.getHeight(), true);

                Log.d(TAG, "set size to " + size.getWidth() + "x" + size.getHeight());
                if (focusPeakProcessorAp1 != null && SettingsManager.getGlobal(SettingKeys.EnableRenderScript).get()) {
                    if(size == null || textureHolder.getSurfaceTexture() == null ||
                            (size.getHeight() == focusPeakProcessorAp1.getHeight() && size.getWidth() == focusPeakProcessorAp1.getWidth()))
                        return;
                    cameraHolder.StopPreview();
                    focusPeakProcessorAp1.kill();
                    cameraHolder.setSurface((Surface) null);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        textureHolder.getSurfaceTexture().setDefaultBufferSize(size.getWidth(), size.getHeight());
                    }

                    parametersHandler.get(SettingKeys.PreviewSize).SetValue(size.getWidth() + "x" + size.getHeight(), true);
                    Surface surface = new Surface(textureHolder.getSurfaceTexture());
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
                        Surface surface = new Surface(textureHolder.getSurfaceTexture());
                        cameraHolder.setSurface(surface);
                    }
                    else
                        ((CameraHolder)cameraHolder).setTextureView(textureHolder.getSurfaceTexture());

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

                if(size == null || textureHolder.getSurfaceTexture() == null)
                    return;
                cameraHolder.StopPreview();
                if (focusPeakProcessorAp1 != null) {
                    focusPeakProcessorAp1.kill();
                }

                if (((CameraHolder)cameraHolder).canSetSurfaceDirect()) {
                    cameraHolder.setSurface((Surface)null);
                    Surface surface = new Surface(textureHolder.getSurfaceTexture());
                    cameraHolder.setSurface(surface);
                }
                else
                    ((CameraHolder)cameraHolder).setTextureView(textureHolder.getSurfaceTexture());

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
    public RenderScriptProcessorInterface getFocusPeakProcessor() {
        return focusPeakProcessorAp1;
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
}
