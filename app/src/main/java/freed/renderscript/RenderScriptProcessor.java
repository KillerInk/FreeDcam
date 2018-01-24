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

package freed.renderscript;

import android.annotation.TargetApi;
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

import freed.utils.Log;
import freed.viewer.screenslide.MyHistogram;

/**
 * Renderscript-based Focus peaking viewfinder
 */
@TargetApi(VERSION_CODES.KITKAT)
public class RenderScriptProcessor implements RenderScriptProcessorInterface
{
    private final String TAG = RenderScriptProcessor.class.getSimpleName();
    private int mCount;
    long mLastTime;
    private float mFps;
    private final Handler mProcessingHandler;
    private ProcessingTask mProcessingTask;
    private boolean peak;
    private boolean processHistogram;
    private boolean processClipping;
    private final RenderScriptManager renderScriptManager;
    private final Object workLock = new Object();
    private final MyHistogram histogram;
    private final Allocation histodataR;

    private ScriptGroup allScriptGroup;
    private ScriptGroup histoGroup;
    private ScriptGroup peakGroup;
    private ScriptGroup clippingGroup;
    private ScriptGroup histoPeakGroup;
    private ScriptGroup clippingPeakGroup;
    private ScriptGroup clippingHistoGroup;
    private boolean blue;
    private boolean green;
    private boolean red;
    private int imageformat;
    int width = 0;
    int height =0;

    private final static int HISTOGRAM_UPDATE_RATE = 10;

    public RenderScriptProcessor(RenderScriptManager renderScriptManager, final MyHistogram histogram, int imageformat)
    {
        Log.d(TAG, "Ctor");
        this.imageformat = imageformat;
        this.histogram = histogram;
        if (histogram != null) {
            histogram.post(new Runnable() {
                @Override
                public void run() {
                    histogram.setVisibility(View.GONE);
                }
            });
        }

        this.renderScriptManager = renderScriptManager;
        histodataR = Allocation.createSized(renderScriptManager.GetRS(), Element.U32(renderScriptManager.GetRS()), 256);
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
        if (histogram == null)
            return;
        processHistogram = enable;
        if (processHistogram)
            histogram.setVisibility(View.VISIBLE);
        else
            histogram.setVisibility(View.GONE);
    }

    @Override
    public void setClippingEnable(boolean enable) {
        processClipping = enable;
    }

    @Override
    public void setBlue(boolean blue) {
        this.blue = blue;
    }

    @Override
    public void setRed(boolean red) {
        this.red = red;
    }

    @Override
    public void setGreen(boolean green) {
        this.green = green;
    }

    @Override
    public void SetAspectRatio(int w, int h) {

    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    @Override
    public void Reset(int width, int height)
    {
        this.width = width;
        this.height = height;
        Log.d(TAG,"Reset:"+width +"x"+height);
        //create the yuv input allocation type
        Builder yuvTypeBuilder = new Builder(renderScriptManager.GetRS(), Element.YUV(renderScriptManager.GetRS()));
        yuvTypeBuilder.setX(width);
        yuvTypeBuilder.setY(height);
        yuvTypeBuilder.setYuvFormat(imageformat);

        //create the argb output allocation type
        Builder rgbTypeBuilder = new Builder(renderScriptManager.GetRS(), Element.RGBA_8888(renderScriptManager.GetRS()));
        rgbTypeBuilder.setX(width);
        rgbTypeBuilder.setY(height);

        //create the input and out allocations
        renderScriptManager.SetAllocsTypeBuilder(yuvTypeBuilder,rgbTypeBuilder, Allocation.USAGE_IO_INPUT | Allocation.USAGE_SCRIPT,  Allocation.USAGE_IO_OUTPUT | Allocation.USAGE_SCRIPT);

        //scriptintrinsic need to  set the input else it returns no input set ex
        //that seems not the case for own ScriptC with a own input. so rgb_focuspeak dont need to set the input
        renderScriptManager.yuvToRgbIntrinsic.setInput(renderScriptManager.GetIn());
        renderScriptManager.rgb_histogram.bind_histodataR(histodataR);
        renderScriptManager.rgb_histogram.set_takeOnlyPixel(width*height/8);

        createScriptGroups(rgbTypeBuilder);

        if (mProcessingTask != null) {

            synchronized (workLock) {
                mProcessingTask = null;
            }
        }
        mProcessingTask = new ProcessingTask();
    }

    private void createScriptGroups(Builder rgbTypeBuilder) {
        createAllScriptsGroup(rgbTypeBuilder);
        createHistogramGroup(rgbTypeBuilder);
        createFocusPeakGroup(rgbTypeBuilder);
        createClippingGroup(rgbTypeBuilder);
        createHistogramFocusPeakGroup(rgbTypeBuilder);
        createClippingFocusPeakGroup(rgbTypeBuilder);
        createHistogramClippingGroup(rgbTypeBuilder);
    }

    private void createHistogramFocusPeakGroup(Builder rgbTypeBuilder) {
        //create script group that peak and process histogram.
        ScriptGroup.Builder peakHistoBuilder = new ScriptGroup.Builder(renderScriptManager.GetRS());
        // add kernels involved with this group
        peakHistoBuilder.addKernel(renderScriptManager.yuvToRgbIntrinsic.getKernelID());
        peakHistoBuilder.addKernel(renderScriptManager.rgb_histogram.getKernelID_processHistogram());
        peakHistoBuilder.addKernel(renderScriptManager.rgb_focuspeak.getKernelID_focuspeak());

        //create the bridge between the kernels
        peakHistoBuilder.addConnection(rgbTypeBuilder.create(), renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.rgb_histogram.getKernelID_processHistogram());
        peakHistoBuilder.addConnection(rgbTypeBuilder.create(), renderScriptManager.rgb_histogram.getKernelID_processHistogram(), renderScriptManager.rgb_focuspeak.getFieldID_input());

        //create the group and apply the input/ouput to kernels.
        histoPeakGroup = peakHistoBuilder.create();
        histoPeakGroup.setInput(renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.GetIn());
        histoPeakGroup.setOutput(renderScriptManager.rgb_focuspeak.getKernelID_focuspeak(), renderScriptManager.GetOut());
    }

    private void createHistogramClippingGroup(Builder rgbTypeBuilder) {
        //create script group that peak and process histogram.
        ScriptGroup.Builder peakHistoBuilder = new ScriptGroup.Builder(renderScriptManager.GetRS());
        // add kernels involved with this group
        peakHistoBuilder.addKernel(renderScriptManager.yuvToRgbIntrinsic.getKernelID());
        peakHistoBuilder.addKernel(renderScriptManager.rgb_histogram.getKernelID_processHistogram());
        peakHistoBuilder.addKernel(renderScriptManager.rgb_clipping.getKernelID_processClipping());

        //create the bridge between the kernels
        peakHistoBuilder.addConnection(rgbTypeBuilder.create(), renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.rgb_histogram.getKernelID_processHistogram());
        peakHistoBuilder.addConnection(rgbTypeBuilder.create(), renderScriptManager.rgb_histogram.getKernelID_processHistogram(), renderScriptManager.rgb_clipping.getKernelID_processClipping());

        //create the group and apply the input/ouput to kernels.
        clippingHistoGroup = peakHistoBuilder.create();
        clippingHistoGroup.setInput(renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.GetIn());
        clippingHistoGroup.setOutput(renderScriptManager.rgb_clipping.getKernelID_processClipping(), renderScriptManager.GetOut());
    }

    private void createClippingFocusPeakGroup(Builder rgbTypeBuilder) {
        //create script group that peak and process histogram.
        ScriptGroup.Builder peakHistoBuilder = new ScriptGroup.Builder(renderScriptManager.GetRS());
        // add kernels involved with this group
        peakHistoBuilder.addKernel(renderScriptManager.yuvToRgbIntrinsic.getKernelID());
        peakHistoBuilder.addKernel(renderScriptManager.rgb_clipping.getKernelID_processClipping());
        peakHistoBuilder.addKernel(renderScriptManager.rgb_focuspeak.getKernelID_focuspeak());

        //create the bridge between the kernels
        peakHistoBuilder.addConnection(rgbTypeBuilder.create(), renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.rgb_focuspeak.getFieldID_input());
        peakHistoBuilder.addConnection(rgbTypeBuilder.create(), renderScriptManager.rgb_focuspeak.getKernelID_focuspeak(), renderScriptManager.rgb_clipping.getKernelID_processClipping());

        //create the group and apply the input/ouput to kernels.
        clippingPeakGroup = peakHistoBuilder.create();
        clippingPeakGroup.setInput(renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.GetIn());
        clippingPeakGroup.setOutput(renderScriptManager.rgb_clipping.getKernelID_processClipping(), renderScriptManager.GetOut());
    }

    private void createClippingGroup(Builder rgbTypeBuilder) {
        //create scriptgroup that only clip
        ScriptGroup.Builder clipbuilder = new ScriptGroup.Builder(renderScriptManager.GetRS());
        clipbuilder.addKernel(renderScriptManager.yuvToRgbIntrinsic.getKernelID());
        clipbuilder.addKernel(renderScriptManager.rgb_clipping.getKernelID_processClipping());

        clipbuilder.addConnection(rgbTypeBuilder.create(), renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.rgb_clipping.getKernelID_processClipping());
        clippingGroup = clipbuilder.create();
        clippingGroup.setInput(renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.GetIn());
        clippingGroup.setOutput(renderScriptManager.rgb_clipping.getKernelID_processClipping(), renderScriptManager.GetOut());
    }

    private void createFocusPeakGroup(Builder rgbTypeBuilder) {
        //create scriptgroup that only peak
        ScriptGroup.Builder peakbuilder = new ScriptGroup.Builder(renderScriptManager.GetRS());
        peakbuilder.addKernel(renderScriptManager.yuvToRgbIntrinsic.getKernelID());
        peakbuilder.addKernel(renderScriptManager.rgb_focuspeak.getKernelID_focuspeak());

        peakbuilder.addConnection(rgbTypeBuilder.create(), renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.rgb_focuspeak.getFieldID_input());
        peakGroup = peakbuilder.create();
        peakGroup.setInput(renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.GetIn());
        peakGroup.setOutput(renderScriptManager.rgb_focuspeak.getKernelID_focuspeak(), renderScriptManager.GetOut());
    }

    private void createHistogramGroup(Builder rgbTypeBuilder) {
        //create script group that process histogram
        ScriptGroup.Builder histobuilder = new ScriptGroup.Builder(renderScriptManager.GetRS());
        histobuilder.addKernel(renderScriptManager.yuvToRgbIntrinsic.getKernelID());
        histobuilder.addKernel(renderScriptManager.rgb_histogram.getKernelID_processHistogram());

        histobuilder.addConnection(rgbTypeBuilder.create(), renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.rgb_histogram.getKernelID_processHistogram());

        histoGroup = histobuilder.create();
        histoGroup.setInput(renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.GetIn());
        histoGroup.setOutput(renderScriptManager.rgb_histogram.getKernelID_processHistogram(), renderScriptManager.GetOut());
    }

    private void createAllScriptsGroup(Builder rgbTypeBuilder) {
        //create script group that peak and process histogram.
        ScriptGroup.Builder builder = new ScriptGroup.Builder(renderScriptManager.GetRS());
        // add kernels involved with this group
        builder.addKernel(renderScriptManager.yuvToRgbIntrinsic.getKernelID());
        builder.addKernel(renderScriptManager.rgb_histogram.getKernelID_processHistogram());
        builder.addKernel(renderScriptManager.rgb_focuspeak.getKernelID_focuspeak());
        builder.addKernel(renderScriptManager.rgb_clipping.getKernelID_processClipping());

        //create the bridge between the kernels
        builder.addConnection(rgbTypeBuilder.create(), renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.rgb_histogram.getKernelID_processHistogram());
        builder.addConnection(rgbTypeBuilder.create(), renderScriptManager.rgb_histogram.getKernelID_processHistogram(), renderScriptManager.rgb_focuspeak.getFieldID_input());
        builder.addConnection(rgbTypeBuilder.create(), renderScriptManager.rgb_focuspeak.getKernelID_focuspeak(), renderScriptManager.rgb_clipping.getKernelID_processClipping());

        //create the group and apply the input/ouput to kernels.
        allScriptGroup = builder.create();
        allScriptGroup.setInput(renderScriptManager.yuvToRgbIntrinsic.getKernelID(), renderScriptManager.GetIn());
        allScriptGroup.setOutput(renderScriptManager.rgb_clipping.getKernelID_processClipping(), renderScriptManager.GetOut());
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
        width = 0;
        height =0;
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
                //apply focuspeak color
                renderScriptManager.rgb_focuspeak.set_red(red);
                renderScriptManager.rgb_focuspeak.set_green(green);
                renderScriptManager.rgb_focuspeak.set_blue(blue);

                if (peak && processHistogram && processClipping)
                {
                    //draw histogram only each HISTOGRAM_UPDATE_RATE frames
                    //it takes to much time to get it foreach frame and cause a lag
                    if (framescount % HISTOGRAM_UPDATE_RATE == 0) {

                        renderScriptManager.rgb_histogram.invoke_clear();
                        allScriptGroup.execute();
                        histodataR.copyTo(histogram.getRedHistogram());
                        histodataR.copyTo(histogram.getGreenHistogram());
                        histodataR.copyTo(histogram.getBlueHistogram());
                        histogram.redrawHistogram();
                        framescount = 0;
                    }
                    else { // if no histogram process process only focuspeak
                        clippingPeakGroup.execute();
                    }
                }
                //focuspeak and histogram is active
                else if (peak && processHistogram)
                {
                    //draw histogram only each HISTOGRAM_UPDATE_RATE frames
                    //it takes to much time to get it foreach frame and cause a lag
                    if (framescount % HISTOGRAM_UPDATE_RATE == 0) {

                        renderScriptManager.rgb_histogram.invoke_clear();
                        histoPeakGroup.execute();
                        histodataR.copyTo(histogram.getRedHistogram());
                        histodataR.copyTo(histogram.getGreenHistogram());
                        histodataR.copyTo(histogram.getBlueHistogram());
                        histogram.redrawHistogram();
                        framescount = 0;
                    }
                    else { // if no histogram process process only focuspeak
                        peakGroup.execute();
                    }
                }
                else if (processClipping && processHistogram)
                {
                    //draw histogram only each HISTOGRAM_UPDATE_RATE frames
                    //it takes to much time to get it foreach frame and cause a lag
                    if (framescount % HISTOGRAM_UPDATE_RATE == 0) {

                        renderScriptManager.rgb_histogram.invoke_clear();
                        clippingHistoGroup.execute();
                        histodataR.copyTo(histogram.getRedHistogram());
                        histodataR.copyTo(histogram.getGreenHistogram());
                        histodataR.copyTo(histogram.getBlueHistogram());
                        histogram.redrawHistogram();
                        framescount = 0;
                    }
                    else { // if no histogram process process only focuspeak
                        clippingGroup.execute();
                    }
                }
                else if(peak && processClipping)
                {
                    clippingPeakGroup.execute();
                }
                else if (peak) // process only focuspeak
                {
                    peakGroup.execute();
                }
                else if (processClipping)
                    clippingGroup.execute();
                else if (processHistogram) // process only histogram
                {
                    if (framescount % HISTOGRAM_UPDATE_RATE == 0) {
                        renderScriptManager.rgb_histogram.invoke_clear();
                        histoGroup.execute();
                        histodataR.copyTo(histogram.getRedHistogram());
                        histodataR.copyTo(histogram.getGreenHistogram());
                        histodataR.copyTo(histogram.getBlueHistogram());
                        histogram.redrawHistogram();
                        framescount = 0;
                    }
                    else {
                        renderScriptManager.yuvToRgbIntrinsic.forEach(renderScriptManager.GetOut());
                    }
                }
                else
                {
                    renderScriptManager.yuvToRgbIntrinsic.forEach(renderScriptManager.GetOut());
                }
                renderScriptManager.GetOut().ioSend();
                working = false;
            }
        }
    }
}
