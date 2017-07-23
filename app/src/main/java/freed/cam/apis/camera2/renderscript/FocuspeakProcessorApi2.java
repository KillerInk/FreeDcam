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
import android.renderscript.Type;
import android.renderscript.Type.Builder;
import android.view.Surface;
import android.view.View;

import freed.cam.apis.basecamera.FocuspeakProcessor;
import freed.utils.Log;
import freed.utils.RenderScriptHandler;
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
    private final RenderScriptHandler renderScriptHandler;
    private final Object workLock = new Object();
    private final MyHistogram histogram;
    private final Allocation histodataR;
    private final Allocation histodataG;
    private final Allocation histodataB;
    private Allocation tmprgballoc;
    private final int emptydata[];
    ScriptGroup fpGpup;

    private ScriptGroup fpGroup;

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    public FocuspeakProcessorApi2(RenderScriptHandler renderScriptHandler, MyHistogram histogram)
    {
        Log.d(TAG, "Ctor");
        this.histogram = histogram;
        histogram.setVisibility(View.GONE);
        this.renderScriptHandler = renderScriptHandler;
        histodataR = Allocation.createSized(renderScriptHandler.GetRS(), Element.U32(renderScriptHandler.GetRS()), 256);
        histodataG = Allocation.createSized(renderScriptHandler.GetRS(), Element.U32(renderScriptHandler.GetRS()), 256);
        histodataB = Allocation.createSized(renderScriptHandler.GetRS(), Element.U32(renderScriptHandler.GetRS()), 256);
        emptydata = new int[256];
        HandlerThread mProcessingThread = new HandlerThread("ViewfinderProcessor");
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
        if (peak)
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
        Builder yuvTypeBuilder = new Builder(renderScriptHandler.GetRS(), Element.YUV(renderScriptHandler.GetRS()));
        yuvTypeBuilder.setX(width);
        yuvTypeBuilder.setY(height);
        yuvTypeBuilder.setYuvFormat(ImageFormat.YUV_420_888);

        Builder rgbTypeBuilder = new Builder(renderScriptHandler.GetRS(), Element.RGBA_8888(renderScriptHandler.GetRS()));
        rgbTypeBuilder.setX(width);
        rgbTypeBuilder.setY(height);
        tmprgballoc = Allocation.createTyped(renderScriptHandler.GetRS(), rgbTypeBuilder.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        renderScriptHandler.SetAllocsTypeBuilder(yuvTypeBuilder,rgbTypeBuilder, Allocation.USAGE_IO_INPUT | Allocation.USAGE_SCRIPT,  Allocation.USAGE_IO_OUTPUT | Allocation.USAGE_SCRIPT);
        renderScriptHandler.freedcamScript.set_gCurrentFrame(tmprgballoc);
        renderScriptHandler.yuvToRgbIntrinsic.setInput(renderScriptHandler.GetIn());
        renderScriptHandler.freedcamScript.bind_histodataR(histodataR);
        renderScriptHandler.freedcamScript.bind_histodataG(histodataG);
        renderScriptHandler.freedcamScript.bind_histodataB(histodataB);

        ScriptGroup.Builder builder = new ScriptGroup.Builder(renderScriptHandler.GetRS());
        builder.addKernel(renderScriptHandler.yuvToRgbIntrinsic.getKernelID());
        builder.addKernel(renderScriptHandler.freedcamScript.getKernelID_focuspeaksony());

        builder.addConnection(rgbTypeBuilder.create(), renderScriptHandler.yuvToRgbIntrinsic.getKernelID(), renderScriptHandler.freedcamScript.getFieldID_gCurrentFrame());

        fpGpup = builder.create();
        fpGpup.setInput(renderScriptHandler.yuvToRgbIntrinsic.getKernelID(), renderScriptHandler.GetIn());
        fpGpup.setOutput(renderScriptHandler.freedcamScript.getKernelID_focuspeaksony(),renderScriptHandler.GetOut());



        if (mProcessingTask != null) {

            synchronized (workLock) {
                mProcessingTask = null;
            }
        }
        mProcessingTask = new ProcessingTask();
    }

    public Surface getInputSurface() {
        return renderScriptHandler.GetInputAllocationSurface();
    }
    public void setOutputSurface(Surface output)
    {
        renderScriptHandler.SetSurfaceToOutputAllocation(output);
        Log.d(TAG,"setOutputSurface");
    }

    @Override
    public void kill()
    {
        if (mProcessingTask != null) {

            synchronized (workLock)
            {
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
                    renderScriptHandler.GetIn().ioReceive();
                }
                mCount++;
                framescount++;
                if (framescount % 10 ==0)
                    renderScriptHandler.freedcamScript.set_processhisto(true);
                else
                    renderScriptHandler.freedcamScript.set_processhisto(false);

                if (renderScriptHandler.GetOut() == null)
                    return;
                histodataR.copyFrom(emptydata);
                histodataG.copyFrom(emptydata);
                histodataB.copyFrom(emptydata);

                if (peak) {

                    // Run processing pass
                    /*renderScriptHandler.yuvToRgbIntrinsic.forEach(tmprgballoc);
                    renderScriptHandler.freedcamScript.forEach_focuspeaksony(renderScriptHandler.GetOut());*/
                    fpGpup.execute();
                    //renderScriptHandler.freedcamScript.forEach_focuspeakcam2(renderScriptHandler.GetOut());
                } else {
                    //renderScriptHandler.freedcamScript.forEach_nv21torgb(renderScriptHandler.GetOut());
                    renderScriptHandler.yuvToRgbIntrinsic.forEach(renderScriptHandler.GetOut());
                }
                if (framescount % 10 ==0) {
                    histodataR.copyTo(histogram.getRedHistogram());
                    histodataG.copyTo(histogram.getGreenHistogram());
                    histodataB.copyTo(histogram.getBlueHistogram());
                    histogram.redrawHistogram();
                    framescount = 0;
                }
                renderScriptHandler.GetOut().ioSend();
                working = false;
            }
        }
    }
}
