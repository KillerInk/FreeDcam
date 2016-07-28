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
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.Type.Builder;
import android.view.Surface;
import android.view.TextureView.SurfaceTextureListener;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperEvent;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocuspeakProcessor;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.I_AspectRatio;
import freed.utils.FreeDPool;
import freed.utils.Logger;
import freed.utils.RenderScriptHandler;


/**
 * Created by troop on 24.08.2015.
 */
@TargetApi(VERSION_CODES.KITKAT)
public class FocusPeakProcessorAp1 implements PreviewCallback, CameraWrapperEvent,ModuleChangedEvent, FocuspeakProcessor
{
    private final String TAG = FocusPeakProcessorAp1.class.getSimpleName();
    private final I_AspectRatio output;
    private final CameraWrapperInterface cameraUiWrapper;

    private int mHeight;
    private int mWidth;
    private Surface mSurface;

    private boolean enable;
    private boolean doWork;
    private boolean isWorking;
    private final Context context;
    private final RenderScriptHandler renderScriptHandler;
    private int expectedByteSize;
    private final BlockingQueue<byte[]> frameQueue = new ArrayBlockingQueue<>(2);

    public FocusPeakProcessorAp1(I_AspectRatio output, CameraWrapperInterface cameraUiWrapper, Context context, RenderScriptHandler renderScriptHandler)
    {
        Logger.d(TAG, "Ctor");
        this.output = output;
        this.cameraUiWrapper = cameraUiWrapper;
        this.context = context;
        this.renderScriptHandler = renderScriptHandler;
        this.cameraUiWrapper.GetModuleHandler().addListner(this);
        output.setSurfaceTextureListener(previewSurfaceListner);
        clear_preview("Ctor");
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }

    @Override
    public void Enable(boolean enable)
    {
        Logger.d(TAG, "Enable:" + enable);
        this.enable = enable;
        setEnable(this.enable);
    }

    private void setEnable(boolean enabled)
    {
        Logger.d(TAG, "setEnable" + enabled);
        if (enabled)
        {
            Size size = new Size(cameraUiWrapper.GetParameterHandler().PreviewSize.GetValue());
            reset(size.width, size.height);
            startPeak();
            Logger.d(TAG, "Set PreviewCallback");
            Logger.d(TAG, "enable focuspeak");
            show_preview();
        }
        else
        {
            Logger.d(TAG, "stop focuspeak");
            cameraUiWrapper.GetCameraHolder().ResetPreviewCallback();
            clear_preview("setEnable");

        }
        if(cameraUiWrapper.GetParameterHandler().Focuspeak != null && cameraUiWrapper.GetParameterHandler().Focuspeak.IsSupported())
            cameraUiWrapper.GetParameterHandler().Focuspeak.BackgroundValueHasChanged(enabled +"");
    }

    private void clear_preview(String from)
    {
        if (!doWork || !enable) {
            output.setAlpha(0);
            Logger.d(TAG, "Preview cleared from:" + from);
        }
    }
    private void show_preview()
    {
        if (doWork && enable) {
            output.setAlpha(1);
            Logger.d(TAG, "Preview show from:" + "setEnable");
        }
    }

    private void startPeak()
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                isWorking = true;
                byte[] tmp;
                while (enable)
                {
                    try {
                        //take one stored frame for processing
                        tmp = frameQueue.take();
                        renderScriptHandler.GetIn().copyFrom(tmp);

                        renderScriptHandler.ScriptFocusPeakApi1.forEach_peak(renderScriptHandler.GetOut());
                        renderScriptHandler.GetOut().ioSend();
                        //pass frame back to camera that it get reused
                        ((CameraHolder) cameraUiWrapper.GetCameraHolder()).GetCamera().addCallbackBuffer(tmp);
                    } catch (InterruptedException | NullPointerException e) {
                        e.printStackTrace();
                    }
                    finally
                    {
                        //clear frames and pass them back the camera
                        while (frameQueue.size()>0)
                            try {
                                ((CameraHolder) cameraUiWrapper.GetCameraHolder()).GetCamera().addCallbackBuffer(frameQueue.take());
                            } catch (InterruptedException | NullPointerException e)
                            {
                                e.printStackTrace();
                            }
                        frameQueue.clear();
                    }
                }
                isWorking = false;
            }
        });
    }

    public boolean isEnable() { return enable;}

    private void reset(int width, int height)
    {
        try {
            mHeight = height;
            mWidth = width;
            expectedByteSize = mHeight * mWidth *
                    ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;

            Logger.d(TAG, "reset allocs to :" + width + "x" + height);
            try {
                cameraUiWrapper.GetCameraHolder().ResetPreviewCallback();
            } catch (NullPointerException ex)
            {
                Logger.exception(ex);
            }

            Builder tbIn = new Builder(renderScriptHandler.GetRS(), Element.U8(renderScriptHandler.GetRS()));
            tbIn.setX(mWidth);
            tbIn.setY(mHeight);
            tbIn.setYuvFormat(ImageFormat.NV21);
            if (renderScriptHandler.GetOut()!= null)
                renderScriptHandler.GetOut().setSurface(null);


            Builder tbOut = new Builder(renderScriptHandler.GetRS(), Element.RGBA_8888(renderScriptHandler.GetRS()));
            tbOut.setX(mWidth);
            tbOut.setY(mHeight);
            renderScriptHandler.SetAllocsTypeBuilder(tbIn,tbOut,Allocation.USAGE_SCRIPT, Allocation.USAGE_SCRIPT | Allocation.USAGE_IO_OUTPUT);

            if (mSurface != null)
                renderScriptHandler.GetOut().setSurface(mSurface);
            else
                Logger.d(TAG, "surfaceNull");
            renderScriptHandler.ScriptFocusPeakApi1.set_gCurrentFrame(renderScriptHandler.GetIn());
            Logger.d(TAG, "script done enabled: " + enable);
            ((CameraHolder) cameraUiWrapper.GetCameraHolder()).SetPreviewCallback(this);
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
        Logger.d(TAG, "SetAspectRatio enable: " + enable);
        output.setAspectRatio(w, h);
        if (enable)
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
        else if (!enable)
        {
            Logger.d(TAG, "onPreviewFrame enabled:" + enable);
            camera.addCallbackBuffer(data);
            cameraUiWrapper.GetCameraHolder().ResetPreviewCallback();
            clear_preview("onPreviewFrame();");
            return;
        }
        else if (!doWork) {
            camera.addCallbackBuffer(data);
            return;
        }
        else if (expectedByteSize != data.length) {
            Logger.d(TAG, "frame size does not match rendersize");
            Camera.Size s = camera.getParameters().getPreviewSize();
            reset(s.width, s.height);
            return;
        }
        //if limit is reached pass one frame back to the camera that i can get resused
        if (frameQueue.size() == 2)
        {
            try {
                camera.addCallbackBuffer(frameQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
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
        Logger.d(TAG, "onPreviewOpen enable:" + enable);
        clear_preview("onPreviewOpen");
        //setEnable(enable);
    }

    @Override
    public void onPreviewClose(String message)
    {
    }

    @Override
    public void onCameraError(String error) {
    }

    @Override
    public void onCameraStatusChanged(String status) {
    }

    @Override
    public void onModuleChanged(ModuleInterface module) {
    }

    @Override
    public void onModuleChanged(String module)
    {
        Logger.d(TAG, "onModuleChanged(String):" + module + " enabled:" + enable);
        if (module.equals(KEYS.MODULE_PICTURE)
                ||module.equals(KEYS.MODULE_HDR)
                ||module.equals(KEYS.MODULE_INTERVAL))
        {
            setDoWork(true);
            setEnable(enable);

        }
        else {
            setDoWork(false);
            setEnable(enable);
        }
    }

    private void setDoWork(boolean work) {
        doWork = work;}

    private final SurfaceTextureListener previewSurfaceListner = new SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
        {
            mWidth = width;
            mHeight = height;
            Logger.d(TAG, "SurfaceSizeAvail");
            mSurface = new Surface(surface);
            if (renderScriptHandler.GetOut() != null && renderScriptHandler.GetOut().getUsage() == Allocation.USAGE_IO_OUTPUT)
                renderScriptHandler.GetOut().setSurface(mSurface);
            else {
                Logger.d(TAG, "Allocout null or not USAGE_IO_OUTPUT");
                Size size = new Size(cameraUiWrapper.GetParameterHandler().PreviewSize.GetValue());
                reset(size.width, size.height);
            }
            clear_preview("onSurfaceTextureAvailable");
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Logger.d(TAG, "SurfaceSizeChanged");
            mSurface = new Surface(surface);
            if (renderScriptHandler.GetOut()  != null)
                renderScriptHandler.GetOut().setSurface(mSurface);
            else {
                Logger.d(TAG, "Allocout null");

            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Logger.d(TAG, "SurfaceDestroyed");
            clear_preview("onSurfaceTextureDestroyed");
            mSurface = null;


            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };
}
