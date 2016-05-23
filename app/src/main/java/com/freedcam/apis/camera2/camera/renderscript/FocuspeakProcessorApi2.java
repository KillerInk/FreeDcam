package com.freedcam.apis.camera2.camera.renderscript;

    /*
     * Copyright (C) 2015 The Android Open Source Project
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.view.Surface;

import com.freedcam.utils.Logger;
import com.freedcam.utils.RenderScriptHandler;

/**
 * Renderscript-based Focus peaking viewfinder
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class FocuspeakProcessorApi2
{
    private final String TAG = FocuspeakProcessorApi2.class.getSimpleName();
    private int mCount;
    long mLastTime;
    private float mFps;
    private HandlerThread mProcessingThread;
    private Handler mProcessingHandler;
    private ScriptC_focus_peak mScriptFocusPeak;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private ProcessingTask mProcessingTask;
    public boolean peak = false;
    private RenderScriptHandler renderScriptHandler;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public FocuspeakProcessorApi2(RenderScriptHandler renderScriptHandler)
    {
        Logger.d(TAG, "Ctor");
        this.renderScriptHandler = renderScriptHandler;
        mProcessingThread = new HandlerThread("ViewfinderProcessor");
        mProcessingThread.start();
        mProcessingHandler = new Handler(mProcessingThread.getLooper());
        mScriptFocusPeak = new ScriptC_focus_peak(renderScriptHandler.GetRenderScript());
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(renderScriptHandler.GetRenderScript(), Element.U8_4(renderScriptHandler.GetRenderScript()));
    }

    public void setRenderScriptErrorListner(RenderScript.RSErrorHandler errorListner)
    {
        renderScriptHandler.GetRenderScript().setErrorHandler(errorListner);
    }

    public void Reset(int width,int height)
    {
        Logger.d(TAG,"Reset:"+width +"x"+height);
        Type.Builder yuvTypeBuilder = new Type.Builder(renderScriptHandler.GetRenderScript(), Element.YUV(renderScriptHandler.GetRenderScript()));
        yuvTypeBuilder.setX(width);
        yuvTypeBuilder.setY(height);
        yuvTypeBuilder.setYuvFormat(ImageFormat.YUV_420_888);

        Type.Builder rgbTypeBuilder = new Type.Builder(renderScriptHandler.GetRenderScript(), Element.RGBA_8888(renderScriptHandler.GetRenderScript()));
        rgbTypeBuilder.setX(width);
        rgbTypeBuilder.setY(height);
        renderScriptHandler.SetAllocsTypeBuilder(yuvTypeBuilder,rgbTypeBuilder);
        mScriptFocusPeak.set_gCurrentFrame(renderScriptHandler.GetInputAllocation());
        yuvToRgbIntrinsic.setInput(renderScriptHandler.GetInputAllocation());

        if (mProcessingTask != null) {

            while (mProcessingTask.working) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Logger.exception(e);
                }
            }
            mProcessingTask = null;
        }
        mProcessingTask = new ProcessingTask(renderScriptHandler.GetInputAllocation());
    }

    public Surface getInputSurface() {
        return renderScriptHandler.GetInputAllocationSurface();
    }
    public void setOutputSurface(Surface output)
    {
        renderScriptHandler.SetSurfaceToOutputAllocation(output);
        Logger.d(TAG,"setOutputSurface");
    }

    public void kill()
    {
        if (mProcessingTask != null) {

            while (mProcessingTask.working) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Logger.exception(e);
                }
            }
            mProcessingTask = null;
            renderScriptHandler.SetSurfaceToOutputAllocation(null);
        }
        Logger.d(TAG,"kill()");
    }

    public float getmFps() {
        return mFps;
    }
    /**
     * Class to process buffer from camera and output to buffer to screen
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    class ProcessingTask implements Runnable, Allocation.OnBufferAvailableListener {
        private int mPendingFrames = 0;
        private Allocation mInputAllocation;
        private boolean working = false;
        public ProcessingTask(Allocation input) {
            mInputAllocation = input;
            mInputAllocation.setOnBufferAvailableListener(this);
        }
        @Override
        public void onBufferAvailable(Allocation a) {
            synchronized (this) {
                mPendingFrames++;
                mProcessingHandler.post(this);
            }
        }
        @Override
        public void run()
        {
            working = true;
            // Find out how many frames have arrived
            int pendingFrames;
            synchronized (this) {
                pendingFrames = mPendingFrames;
                mPendingFrames = 0;
                // Discard extra messages in case processing is slower than frame rate
                mProcessingHandler.removeCallbacks(this);
            }
            // Get to newest input
            for (int i = 0; i < pendingFrames; i++)
            {
                mInputAllocation.ioReceive();
            }
            mCount++;
            if (renderScriptHandler.GetOutputAllocation() == null)
                return;
            if (peak) {

                // Run processing pass
                mScriptFocusPeak.forEach_peak(renderScriptHandler.GetOutputAllocation());
            }
            else
            {

                yuvToRgbIntrinsic.forEach(renderScriptHandler.GetOutputAllocation());
            }
            renderScriptHandler.GetOutputAllocation().ioSend();
            working = false;
        }
    }
}
