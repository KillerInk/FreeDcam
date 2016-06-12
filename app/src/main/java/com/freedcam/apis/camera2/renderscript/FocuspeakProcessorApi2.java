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

package com.freedcam.apis.camera2.renderscript;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.HandlerThread;
import android.renderscript.Allocation;
import android.renderscript.Allocation.OnBufferAvailableListener;
import android.renderscript.Element;
import android.renderscript.RenderScript.RSErrorHandler;
import android.renderscript.Type.Builder;
import android.view.Surface;

import com.freedcam.apis.basecamera.interfaces.FocuspeakProcessor;
import com.freedcam.utils.Logger;
import com.freedcam.utils.RenderScriptHandler;

/**
 * Renderscript-based Focus peaking viewfinder
 */
@TargetApi(VERSION_CODES.KITKAT)
public class FocuspeakProcessorApi2 implements FocuspeakProcessor
{
    private final String TAG = FocuspeakProcessorApi2.class.getSimpleName();
    private int mCount;
    long mLastTime;
    private float mFps;
    private HandlerThread mProcessingThread;
    private Handler mProcessingHandler;
    private ProcessingTask mProcessingTask;
    private boolean peak = false;
    private RenderScriptHandler renderScriptHandler;

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    public FocuspeakProcessorApi2(RenderScriptHandler renderScriptHandler)
    {
        Logger.d(TAG, "Ctor");
        this.renderScriptHandler = renderScriptHandler;
        mProcessingThread = new HandlerThread("ViewfinderProcessor");
        mProcessingThread.start();
        mProcessingHandler = new Handler(mProcessingThread.getLooper());

    }

    public void setRenderScriptErrorListner(RSErrorHandler errorListner)
    {
        renderScriptHandler.GetRS().setErrorHandler(errorListner);
    }

    @Override
    public boolean isEnabled() {
        return peak;
    }

    @Override
    public void Enable(boolean enable) {
        peak = enable;
    }

    @Override
    public void SetAspectRatio(int w, int h) {

    }

    @Override
    public void Reset(int width, int height)
    {
        Logger.d(TAG,"Reset:"+width +"x"+height);
        Builder yuvTypeBuilder = new Builder(renderScriptHandler.GetRS(), Element.YUV(renderScriptHandler.GetRS()));
        yuvTypeBuilder.setX(width);
        yuvTypeBuilder.setY(height);
        yuvTypeBuilder.setYuvFormat(ImageFormat.YUV_420_888);

        Builder rgbTypeBuilder = new Builder(renderScriptHandler.GetRS(), Element.RGBA_8888(renderScriptHandler.GetRS()));
        rgbTypeBuilder.setX(width);
        rgbTypeBuilder.setY(height);
        renderScriptHandler.SetAllocsTypeBuilder(yuvTypeBuilder,rgbTypeBuilder, Allocation.USAGE_IO_INPUT | Allocation.USAGE_SCRIPT,  Allocation.USAGE_IO_OUTPUT | Allocation.USAGE_SCRIPT);
        renderScriptHandler.ScriptFocusPeakApi2.set_gCurrentFrame(renderScriptHandler.GetIn());
        renderScriptHandler.yuvToRgbIntrinsic.setInput(renderScriptHandler.GetIn());

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
        mProcessingTask = new ProcessingTask();
    }

    public Surface getInputSurface() {
        return renderScriptHandler.GetInputAllocationSurface();
    }
    public void setOutputSurface(Surface output)
    {
        renderScriptHandler.SetSurfaceToOutputAllocation(output);
        Logger.d(TAG,"setOutputSurface");
    }

    @Override
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
            if (renderScriptHandler.GetIn() != null) {
                renderScriptHandler.GetIn().setOnBufferAvailableListener(null);
            }
            if (renderScriptHandler.GetOut() != null)
            {
                renderScriptHandler.GetOut().setSurface(null);
                //mOutputAllocation = null;
            }
        }
        Logger.d(TAG,"kill()");
    }

    public float getmFps() {
        return mFps;
    }
    /**
     * Class to process buffer from camera and output to buffer to screen
     */
    @TargetApi(VERSION_CODES.KITKAT)
    class ProcessingTask implements Runnable, OnBufferAvailableListener {
        private int mPendingFrames = 0;
        private boolean working = false;
        public ProcessingTask() {
            renderScriptHandler.GetIn().setOnBufferAvailableListener(this);
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
                renderScriptHandler.GetIn().ioReceive();
            }
            mCount++;
            if (renderScriptHandler.GetOut() == null)
                return;
            if (peak) {

                // Run processing pass
                renderScriptHandler.ScriptFocusPeakApi2.forEach_peak(renderScriptHandler.GetOut());
            }
            else
            {

                renderScriptHandler.yuvToRgbIntrinsic.forEach(renderScriptHandler.GetOut());
            }
            renderScriptHandler.GetOut().ioSend();
            working = false;
        }
    }
}
