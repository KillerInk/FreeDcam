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

package freed.cam.apis.camera2.renderscript;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.HandlerThread;
import android.renderscript.Allocation;
import android.renderscript.Allocation.OnBufferAvailableListener;
import android.renderscript.Element;
import android.renderscript.RenderScript.RSErrorHandler;
import android.renderscript.ScriptGroup;
import android.renderscript.Type.Builder;
import android.view.Surface;
import android.view.View;

import freed.cam.apis.basecamera.FocuspeakProcessor;
import freed.utils.Log;
import freed.utils.RenderScriptManager;
import freed.viewer.screenslide.MyHistogram;

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
    private final Handler mProcessingHandler;
    private ProcessingTask mProcessingTask;
    private boolean peak;
    private boolean processHistogram;
    private final RenderScriptManager renderScriptManager;
    private final Object workLock = new Object();
    private final MyHistogram histogram;
    private final Allocation histodataR;
    private final Allocation histodataG;
    private final Allocation histodataB;
    private Allocation tmprgballoc;
    private final int emptydata[];

    private ScriptGroup scriptGroup;
    private ScriptGroup histoGroup;
    private ScriptGroup peakGroup;

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    public FocuspeakProcessorApi2(RenderScriptManager renderScriptManager, final MyHistogram histogram)
    {
        Log.d(TAG, "Ctor");
        this.histogram = histogram;
        histogram.post(new Runnable() {
            @Override
            public void run() {
                histogram.setVisibility(View.GONE);
            }
        });

        this.renderScriptManager = renderScriptManager;
        histodataR = Allocation.createSized(renderScriptManager.GetRS(), Element.U32(renderScriptManager.GetRS()), 256);
        histodataG = Allocation.createSized(renderScriptManager.GetRS(), Element.U32(renderScriptManager.GetRS()), 256);
        histodataB = Allocation.createSized(renderScriptManager.GetRS(), Element.U32(renderScriptManager.GetRS()), 256);
        emptydata = new int[256];
        HandlerThread mProcessingThread = new HandlerThread("ViewfinderProcessor");
        mProcessingThread.start();
        mProcessingHandler = new Handler(mProcessingThread.getLooper());

    }

    public void setRenderScriptErrorListner(RSErrorHandler errorListner)
    {
        renderScriptManager.GetRS().setErrorHandler(errorListner);
    }

    @Override
    public boolean isEnabled() {
        return peak;
    }

    @Override
    public void setFocusPeakEnable(boolean enable) {
        peak = enable;

    }

    @Override
    public void setHistogramEnable(boolean enable) {
        processHistogram = enable;
        if (processHistogram)
            histogram.setVisibility(View.VISIBLE);
        else
            histogram.setVisibility(View.GONE);
    }

    @Override
    public void SetAspectRatio(int w, int h) {

    }

    @Override
    public void Reset(int width, int height)
    {
        Log.d(TAG,"Reset:"+width +"x"+height);
        Builder yuvTypeBuilder = new Builder(renderScriptManager.GetRS(), Element.YUV(renderScriptManager.GetRS()));
        yuvTypeBuilder.setX(width);
        yuvTypeBuilder.setY(height);
        yuvTypeBuilder.setYuvFormat(ImageFormat.YUV_420_888);

        Builder rgbTypeBuilder = new Builder(renderScriptManager.GetRS(), Element.RGBA_8888(renderScriptManager.GetRS()));
        rgbTypeBuilder.setX(width);
        rgbTypeBuilder.setY(height);
        tmprgballoc = Allocation.createTyped(renderScriptManager.GetRS(), rgbTypeBuilder.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        renderScriptManager.SetAllocsTypeBuilder(yuvTypeBuilder,rgbTypeBuilder, Allocation.USAGE_IO_INPUT | Allocation.USAGE_SCRIPT,  Allocation.USAGE_IO_OUTPUT | Allocation.USAGE_SCRIPT);

        renderScriptManager.yuvToRgbIntrinsic.setInput(renderScriptManager.GetIn());
        renderScriptManager.rgb_focuspeak.set_input(tmprgballoc);
        renderScriptManager.rgb_histogram.bind_histodataR(histodataR);
        renderScriptManager.rgb_histogram.bind_histodataG(histodataG);
        renderScriptManager.rgb_histogram.bind_histodataB(histodataB);

        ScriptGroup.Builder builder = new ScriptGroup.Builder(renderScriptManager.GetRS());
        builder.addKernel(renderScriptManager.yuvToRgbIntrinsic.getKernelID());
        builder.addKernel(renderScriptManager.rgb_histogram.getKernelID_processHistogram());
        builder.addKernel(renderScriptManager.rgb_focuspeak.getKernelID_focuspeak());

        builder.addConnection(rgbTypeBuilder.create(), renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.rgb_histogram.getKernelID_processHistogram());
        builder.addConnection(rgbTypeBuilder.create(), renderScriptManager.rgb_histogram.getKernelID_processHistogram(), renderScriptManager.rgb_focuspeak.getFieldID_input());

        scriptGroup = builder.create();
        scriptGroup.setInput(renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.GetIn());
        scriptGroup.setOutput(renderScriptManager.rgb_focuspeak.getKernelID_focuspeak(), renderScriptManager.GetOut());

        ScriptGroup.Builder histobuilder = new ScriptGroup.Builder(renderScriptManager.GetRS());
        histobuilder.addKernel(renderScriptManager.yuvToRgbIntrinsic.getKernelID());
        histobuilder.addKernel(renderScriptManager.rgb_histogram.getKernelID_processHistogram());

        histobuilder.addConnection(rgbTypeBuilder.create(), renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.rgb_histogram.getKernelID_processHistogram());
        histoGroup = builder.create();
        histoGroup.setInput(renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.GetIn());
        //histoGroup.setOutput(renderScriptManager.rgb_histogram.getKernelID_processHistogram(), renderScriptManager.GetOut());


        ScriptGroup.Builder peakbuilder = new ScriptGroup.Builder(renderScriptManager.GetRS());
        peakbuilder.addKernel(renderScriptManager.yuvToRgbIntrinsic.getKernelID());
        peakbuilder.addKernel(renderScriptManager.rgb_focuspeak.getKernelID_focuspeak());

        peakbuilder.addConnection(rgbTypeBuilder.create(), renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.rgb_focuspeak.getFieldID_input());
        peakGroup = builder.create();
        peakGroup.setInput(renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.GetIn());
        peakGroup.setOutput(renderScriptManager.rgb_focuspeak.getKernelID_focuspeak(), renderScriptManager.GetOut());



        if (mProcessingTask != null) {

            synchronized (workLock) {
                mProcessingTask = null;
            }
        }
        mProcessingTask = new ProcessingTask();
    }

    public Surface getInputSurface() {
        return renderScriptManager.GetInputAllocationSurface();
    }
    public void setOutputSurface(Surface output)
    {
        renderScriptManager.SetSurfaceToOutputAllocation(output);
        Log.d(TAG,"setOutputSurface");
    }

    @Override
    public void kill()
    {
        if (mProcessingTask != null) {

            synchronized (workLock)
            {
                mProcessingTask = null;
                if (renderScriptManager.GetIn() != null) {
                    renderScriptManager.GetIn().setOnBufferAvailableListener(null);
                }
                if (renderScriptManager.GetOut() != null)
                {
                    renderScriptManager.GetOut().setSurface(null);
                    //mOutputAllocation = null;
                }
            }
        }
        Log.d(TAG,"kill()");
    }

    public float getmFps() {
        return mFps;
    }
    /**
     * Class to process buffer from camera and output to buffer to screen
     */
    @TargetApi(VERSION_CODES.KITKAT)
    class ProcessingTask implements Runnable, OnBufferAvailableListener {
        private int mPendingFrames;
        private boolean working;
        private int framescount = 0;
        public ProcessingTask() {
            renderScriptManager.GetIn().setOnBufferAvailableListener(this);
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
            synchronized (workLock) {
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
                for (int i = 0; i < pendingFrames; i++) {
                    renderScriptManager.GetIn().ioReceive();
                }
                mCount++;
                framescount++;

                if (peak && processHistogram)
                {
                    if (framescount % 10 ==0) {
                        histodataR.copyFrom(emptydata);
                        histodataG.copyFrom(emptydata);
                        histodataB.copyFrom(emptydata);
                        scriptGroup.execute();

                        histodataR.copyTo(histogram.getRedHistogram());
                        histodataG.copyTo(histogram.getGreenHistogram());
                        histodataB.copyTo(histogram.getBlueHistogram());
                        histogram.redrawHistogram();
                        framescount = 0;
                    }
                    else
                        peakGroup.execute();
                }
                else if (peak)
                {
                    peakGroup.execute();
                }
                else if (processHistogram)
                {
                    if (framescount % 10 ==0) {
                        histodataR.copyFrom(emptydata);
                        histodataG.copyFrom(emptydata);
                        histodataB.copyFrom(emptydata);
                        scriptGroup.execute();

                        histodataR.copyTo(histogram.getRedHistogram());
                        histodataG.copyTo(histogram.getGreenHistogram());
                        histodataB.copyTo(histogram.getBlueHistogram());
                        histogram.redrawHistogram();
                        framescount = 0;
                    }
                    else
                        renderScriptManager.yuvToRgbIntrinsic.forEach(renderScriptManager.GetOut());
                }
                else if (!processHistogram && !peak)
                {
                    renderScriptManager.yuvToRgbIntrinsic.forEach(renderScriptManager.GetOut());
                }

                renderScriptManager.GetOut().ioSend();
                working = false;
            }
        }
    }
}
