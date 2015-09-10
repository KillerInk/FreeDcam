package troop.com.imageconverter;

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


import android.util.Size;
import android.view.Surface;
/**
 * Renderscript-based Focus peaking viewfinder
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class ViewfinderProcessor {
    int mCount;
    long mLastTime;
    float mFps;
    private Allocation mInputAllocation;
    private Allocation mOutputAllocation;
    private HandlerThread mProcessingThread;
    private Handler mProcessingHandler;
    private ScriptC_focus_peak mScriptFocusPeak;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    public ProcessingTask mProcessingTask;
    public boolean peak = false;

    RenderScript rs;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public ViewfinderProcessor(RenderScript rs)
    {
        this.rs = rs;
        mProcessingThread = new HandlerThread("ViewfinderProcessor");
        mProcessingThread.start();
        mProcessingHandler = new Handler(mProcessingThread.getLooper());
        mScriptFocusPeak = new ScriptC_focus_peak(rs);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    }


    public void Reset(int width,int height)
    {
        Type.Builder yuvTypeBuilder = new Type.Builder(rs, Element.YUV(rs));
        yuvTypeBuilder.setX(width);
        yuvTypeBuilder.setY(height);
        yuvTypeBuilder.setYuvFormat(ImageFormat.NV21);
        mInputAllocation = Allocation.createTyped(rs, yuvTypeBuilder.create(),
                Allocation.USAGE_IO_INPUT | Allocation.USAGE_SCRIPT);
        Type.Builder rgbTypeBuilder = new Type.Builder(rs, Element.RGBA_8888(rs));
        rgbTypeBuilder.setX(width);
        rgbTypeBuilder.setY(height);
        mOutputAllocation = Allocation.createTyped(rs, rgbTypeBuilder.create(),
                Allocation.USAGE_IO_OUTPUT | Allocation.USAGE_SCRIPT);


        mProcessingTask = new ProcessingTask(mInputAllocation);
    }

    public Surface getInputSurface() {
        return mInputAllocation.getSurface();
    }
    public void setOutputSurface(Surface output)
    {

        mOutputAllocation.setSurface(output);
    }

    public void kill()
    {
        if (mInputAllocation == null)
            return;
        mInputAllocation.setOnBufferAvailableListener(null);
        if (mProcessingTask == null)
            return;

        while (mProcessingTask.working) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mOutputAllocation.setSurface(null);
        mProcessingTask = null;
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
            if (mOutputAllocation == null)
                return;
            if (peak) {
                mScriptFocusPeak.set_gCurrentFrame(mInputAllocation);
                // Run processing pass
                mScriptFocusPeak.forEach_peak(mOutputAllocation);
            }
            else
            {
                yuvToRgbIntrinsic.setInput(mInputAllocation);
                yuvToRgbIntrinsic.forEach(mOutputAllocation);
            }
            mOutputAllocation.ioSend();
            working = false;
        }
    }
}
