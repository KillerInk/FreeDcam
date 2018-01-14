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

package freed.cam.apis.camera1.renderscript;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSInvalidStateException;
import android.renderscript.RSRuntimeException;
import android.renderscript.ScriptGroup;
import android.renderscript.Type.Builder;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView.SurfaceTextureListener;

import com.troop.freedcam.R;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import freed.cam.apis.basecamera.CameraStateEvents;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocuspeakProcessor;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.I_AspectRatio;
import freed.settings.SettingKeys;
import freed.utils.FreeDPool;
import freed.utils.Log;
import freed.utils.RenderScriptManager;


/**
 * Created by troop on 24.08.2015.
 */
@TargetApi(VERSION_CODES.KITKAT)
public class FocusPeakProcessorAp1 implements PreviewCallback, CameraStateEvents,ModuleChangedEvent, FocuspeakProcessor, SurfaceTextureListener {
    private final String TAG = FocusPeakProcessorAp1.class.getSimpleName();
    private final I_AspectRatio output;
    private final CameraWrapperInterface cameraUiWrapper;

    private int mHeight;
    private int mWidth;
    private Surface mSurface;

    private boolean doWork;

    private final RenderScriptManager renderScriptManager;
    private int expectedByteSize;
    private final BlockingQueue<byte[]> frameQueue = new ArrayBlockingQueue<>(CameraHolder.BUFFERCOUNT);
    private final Handler mainHandler;
    private final SetOutputAlphaRunner setOutputAlphaRunner;
    private final FocusPeakRunner focusPeakRunner;
    private Allocation tmprgballoc;

    public FocusPeakProcessorAp1(I_AspectRatio output, CameraWrapperInterface cameraUiWrapper, Context context, RenderScriptManager renderScriptManager)
    {
        Log.d(TAG, "Ctor");
        this.output = output;
        this.cameraUiWrapper = cameraUiWrapper;
        this.renderScriptManager = renderScriptManager;
        this.cameraUiWrapper.getModuleHandler().addListner(this);
        mainHandler = new Handler(Looper.getMainLooper());
        setOutputAlphaRunner = new SetOutputAlphaRunner(output);
        output.setSurfaceTextureListener(this);
        focusPeakRunner = new FocusPeakRunner(renderScriptManager,(CameraHolder) cameraUiWrapper.getCameraHolder(),frameQueue);
        clear_preview("Ctor");
    }

    @Override
    public boolean isEnabled() {
        return focusPeakRunner.isEnable();
    }

    @Override
    public void Enable(boolean enable)
    {
        Log.d(TAG, "Enable:" + enable);
        setEnable(enable);
    }

    private void setEnable(boolean enabled)
    {
        Log.d(TAG, "setEnable" + enabled);
        focusPeakRunner.setEnable(enabled);
        if (enabled)
        {
            Size size = new Size(cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewSize).GetStringValue());
            reset(size.width, size.height);
            startPeak();
            Log.d(TAG, "Set PreviewCallback");
            Log.d(TAG, "enable focuspeak");
            show_preview();
        }
        else
        {
            Log.d(TAG, "stop focuspeak");
            cameraUiWrapper.getCameraHolder().ResetPreviewCallback();
            clear_preview("setEnable");

        }
        if(cameraUiWrapper.getParameterHandler().get(SettingKeys.Focuspeak) != null && cameraUiWrapper.getParameterHandler().get(SettingKeys.Focuspeak).IsSupported())
            cameraUiWrapper.getParameterHandler().get(SettingKeys.Focuspeak).fireStringValueChanged(enabled +"");
    }

    private void clear_preview(String from)
    {
        if (!doWork || !focusPeakRunner.isEnable()) {
            setOutputAlphaRunner.setAlpha(0);
            mainHandler.post(setOutputAlphaRunner);
            Log.d(TAG, "Preview cleared from:" + from);
        }
    }
    private void show_preview()
    {
        if (doWork && focusPeakRunner.isEnable()) {
            setOutputAlphaRunner.setAlpha(1);
            mainHandler.post(setOutputAlphaRunner);
            Log.d(TAG, "Preview show from:" + "setEnable");
        }
    }

    private void startPeak()
    {
        focusPeakRunner.setEnable(true);
        FreeDPool.Execute(focusPeakRunner);
    }

    private void reset(int width, int height)
    {
        try {
            mHeight = height;
            mWidth = width;
            expectedByteSize = mHeight * mWidth *
                    ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;

            Log.d(TAG, "reset allocs to :" + width + "x" + height);
            try {
                cameraUiWrapper.getCameraHolder().ResetPreviewCallback();
            } catch (NullPointerException ex)
            {
                Log.WriteEx(ex);
            }

            Builder tbIn = new Builder(renderScriptManager.GetRS(), Element.U8(renderScriptManager.GetRS()));
            tbIn.setX(mWidth);
            tbIn.setY(mHeight);
            tbIn.setYuvFormat(ImageFormat.NV21);
            if (renderScriptManager.GetOut()!= null) {
                renderScriptManager.GetOut().setSurface(null);
            }


            Builder tbOut = new Builder(renderScriptManager.GetRS(), Element.RGBA_8888(renderScriptManager.GetRS()));
            tbOut.setX(mWidth);
            tbOut.setY(mHeight);
            renderScriptManager.SetAllocsTypeBuilder(tbIn,tbOut,Allocation.USAGE_SCRIPT, Allocation.USAGE_SCRIPT | Allocation.USAGE_IO_OUTPUT);

            tmprgballoc = Allocation.createTyped(renderScriptManager.GetRS(), tbOut.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            renderScriptManager.yuvToRgbIntrinsic.setInput(renderScriptManager.GetIn());

            if (mSurface != null)
                renderScriptManager.GetOut().setSurface(mSurface);
            else
                Log.d(TAG, "surfaceNull");
            renderScriptManager.freedcamScript.set_gCurrentFrame(tmprgballoc);

            ScriptGroup.Builder builder = new ScriptGroup.Builder(renderScriptManager.GetRS());
            builder.addKernel(renderScriptManager.yuvToRgbIntrinsic.getKernelID());
            builder.addKernel(renderScriptManager.freedcamScript.getKernelID_focuspeaksony());

            builder.addConnection(tbOut.create(), renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.freedcamScript.getFieldID_gCurrentFrame());

            ScriptGroup fpGpup = builder.create();
            fpGpup.setInput(renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.GetIn());
            fpGpup.setOutput(renderScriptManager.freedcamScript.getKernelID_focuspeaksony(), renderScriptManager.GetOut());
            focusPeakRunner.setScriptGroup(fpGpup);

            Log.d(TAG, "script done enabled: " + focusPeakRunner.isEnable());
            ((CameraHolder) cameraUiWrapper.getCameraHolder()).SetPreviewCallback(this);
        }
        catch (RSRuntimeException ex)
        {
            onCameraError("RenderScript Failed");
            clear_preview("reset()");
        }
    }
    @Override
    public void SetAspectRatio(int w, int h)
    {
        Log.d(TAG, "SetAspectRatio enable: " + focusPeakRunner.isEnable());
        output.setAspectRatio(w, h);
        if (focusPeakRunner.isEnable())
            reset(w,h);
    }

    @Override
    public void Reset(int width, int height) {

    }

    @Override
    public Surface getInputSurface() {
        return null;
    }

    @Override
    public void setOutputSurface(Surface output) {

    }

    @Override
    public void kill() {

    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera)
    {
        if (data == null)
            return;
        else if (!focusPeakRunner.isEnable())
        {
            Log.d(TAG, "onPreviewFrame enabled:" + focusPeakRunner.isEnable());
            camera.addCallbackBuffer(data);
            cameraUiWrapper.getCameraHolder().ResetPreviewCallback();
            clear_preview("onPreviewFrame();");
            return;
        }
        else if (!doWork) {
            camera.addCallbackBuffer(data);
            return;
        }
        else if (expectedByteSize != data.length) {
            camera.addCallbackBuffer(data);
            Log.d(TAG, "frame size does not match rendersize");
            Camera.Size s = camera.getParameters().getPreviewSize();
            reset(s.width, s.height);
            return;
        }
        //if limit is reached pass one frame back to the camera that i can get resused
        if (frameQueue.size() == CameraHolder.BUFFERCOUNT)
        {
            Log.d(TAG, "frameQueue full send one back to cam");
            try {
                camera.addCallbackBuffer(frameQueue.take());
            } catch (InterruptedException e) {
                Log.WriteEx(e);
            }
        }
        //store new frame, dont pass it back to the camera else it get cleaned
        frameQueue.add(data);

    }

    @Override
    public void onCameraOpen(String message)
    {

    }

    @Override
    public void onCameraOpenFinish(String message) {

    }

    @Override
    public void onCameraClose(String message) {

    }

    @Override
    public void onPreviewOpen(String message)
    {
        Log.d(TAG, "onPreviewOpen enable:" + focusPeakRunner.isEnable());
        clear_preview("onPreviewOpen");
        setDoWork(true);
        setEnable(focusPeakRunner.isEnable());
    }

    @Override
    public void onPreviewClose(String message)
    {
        setDoWork(false);
    }

    @Override
    public void onCameraError(String error) {
    }

    @Override
    public void onCameraStatusChanged(String status) {
    }

    @Override
    public void onModuleChanged(String module)
    {
        Log.d(TAG, "onModuleChanged(String):" + module + " enabled:" + focusPeakRunner.isEnable());
        if (module.equals(cameraUiWrapper.getResString(R.string.module_picture))
                ||module.equals(cameraUiWrapper.getResString(R.string.module_hdr))
                ||module.equals(cameraUiWrapper.getResString(R.string.module_interval)))
        {
            setDoWork(true);
            setEnable(focusPeakRunner.isEnable());

        }
        else {
            setDoWork(false);
            if (focusPeakRunner.isEnable())
                setEnable(false);
        }
    }

    private void setDoWork(boolean work) {
        doWork = work;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mWidth = width;
        mHeight = height;
        Log.d(TAG, "SurfaceSizeAvail");
        mSurface = new Surface(surface);
        clear_preview("onSurfaceTextureAvailable");
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "SurfaceSizeChanged");
        mSurface = new Surface(surface);
        clear_preview("onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "SurfaceDestroyed");
        clear_preview("onSurfaceTextureDestroyed");
        mSurface = null;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

}
