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

package freed.cam.apis.sonyremote.sonystuff;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.Type;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.sonyremote.parameters.JoyPad;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView.StreamErrorListener.StreamErrorReason;
import freed.utils.FreeDPool;
import freed.utils.Log;
import freed.utils.RenderScriptManager;


/**
 * A SurfaceView based class to draw liveview frames serially.
 */
public class SimpleStreamSurfaceView extends SurfaceView implements SurfaceHolder.Callback, ParameterEvents, JoyPad.NavigationClick {

    private static final String TAG = SimpleStreamSurfaceView.class.getSimpleName();

    private boolean mWhileFetching;
    private final BlockingQueue<DataExtractor> mJpegQueue = new ArrayBlockingQueue<>(2);
    private final BlockingQueue<DataExtractor> frameQueue = new ArrayBlockingQueue<>(2);
    private final boolean mInMutableAvailable = Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    private int mPreviousWidth;
    private int mPreviousHeight;
    private final Paint mFramePaint;
    private  Paint paint;
    private StreamErrorListener mErrorListener;
    public boolean focuspeak;
    public NightPreviewModes nightmode = NightPreviewModes.off;
    private int currentImageStackCount;
    private Allocation mAllocationIn;
    private boolean scalePreview =false;
    private final int SCALEFACTOR = 2;


    private Bitmap drawBitmap;

    private int zoomPreviewMagineLeft;
    private int zoomPreviewMargineTop;
    private RenderScriptManager renderScriptManager;
    private ActivityInterface activityInterface;

    //Tells the drawing Thread that it can draw
    private boolean DODRAW = false;
    //state if the drawing thread draws;
    private boolean IS_DRAWING = false;


    public int PreviewZOOMFactor = 1;

    private final float[] SHARPMATRIX = {-0f, -1f, -0f,
                                         -1f,  5f, -1f,
                                         -0f, -1f, -0f };

    @Override
    public void onMove(int x, int y) {

        zoomPreviewMagineLeft += (x);
        zoomPreviewMargineTop += (y);
    }

    @Override
    public void onDown() {
        activityInterface.DisablePagerTouch(true);
    }

    @Override
    public void onUp() {
        activityInterface.DisablePagerTouch(false);
    }

    @Override
    public void onIsSupportedChanged(boolean value) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean value) {

    }

    @Override
    public void onIntValueChanged(int current) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onStringValueChanged(String value) {

    }

    public enum NightPreviewModes
    {
        on,
        off,
        grayscale,
        exposure,
        zoompreview,
    }

    public void SetRenderScriptHandlerAndInterface(RenderScriptManager renderscripthandler, ActivityInterface activityInterface)
    {
        this.renderScriptManager =renderscripthandler;
        this.activityInterface = activityInterface;
    }

    /**
     * Constructor
     *
     * @param context
     */
    public SimpleStreamSurfaceView(Context context) {
        super(context);
        this.getHolder().addCallback(this);
        this.mFramePaint = new Paint();
        this.mFramePaint.setDither(true);
        this.initPaint(context);
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     */
    public SimpleStreamSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.getHolder().addCallback(this);
        this.mFramePaint = new Paint();
        this.mFramePaint.setDither(true);
        this.initPaint(context);
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SimpleStreamSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.getHolder().addCallback(this);
        this.mFramePaint = new Paint();
        this.mFramePaint.setDither(true);
        this.initPaint(context);
    }

    public void ScalePreview(boolean enable)
    {
        DODRAW = false;
        while (IS_DRAWING)
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.WriteEx(e);
            }
        }
        scalePreview = enable;
        initRenderScript();
        startDrawingThread();
    }

    public boolean isScalePreview()
    {
        return scalePreview;
    }

    private void initPaint(Context context)
    {
        this.paint = new Paint();
        this.paint.setColor(Color.WHITE);
        this.paint.setStrokeWidth(5);
        this.paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // do nothing.
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // do nothing.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mWhileFetching = false;
    }

    /**
     * Start retrieving and drawing liveview frame data by new threads.
     *
     * @return true if the starting is completed successfully, false otherwise.

     */
    public void start(final String streamUrl, StreamErrorListener listener) {
        mErrorListener = listener;

        if (streamUrl == null) {
            Log.e(TAG, "start() streamUrl is null.");
            mWhileFetching = false;
            mErrorListener.onError(StreamErrorReason.OPEN_ERROR);
            return;
        }
        if (this.mWhileFetching) {
            Log.d(SimpleStreamSurfaceView.TAG, "start() already starting.");
            return;
        }

        this.mWhileFetching = true;

        // A thread for retrieving liveview data from server.
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                Log.d(SimpleStreamSurfaceView.TAG, "Starting retrieving streaming data from server.");
                SimpleLiveviewSlicer slicer = null;

                try {

                    // Create Slicer to open the stream and parse it.
                    slicer = new SimpleLiveviewSlicer();
                    slicer.open(streamUrl);

                    while (SimpleStreamSurfaceView.this.mWhileFetching)
                    {
                        fetchPayLoad(slicer);
                    }
                } catch (IOException e) {
                    Log.d(TAG, "IOException while fetching: " + e.getMessage());
                    mErrorListener.onError(StreamErrorReason.IO_EXCEPTION);
                } finally {
                    if (slicer != null) {
                        slicer.close();
                    }


                    SimpleStreamSurfaceView.this.mJpegQueue.clear();
                    SimpleStreamSurfaceView.this.frameQueue.clear();
                    SimpleStreamSurfaceView.this.mWhileFetching = false;
                }
            }
        });
        startDrawingThread();


    }

    private void startDrawingThread() {
        DODRAW = true;
        // A thread for drawing liveview frame fetched by above thread.
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                IS_DRAWING = true;
                Log.d(SimpleStreamSurfaceView.TAG, "Starting drawing stream frame.");
                Bitmap frameBitmap = null;

                BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
                factoryOptions.inSampleSize = 1;
                factoryOptions.inPreferQualityOverSpeed = true;
                factoryOptions.inDither = false;
                factoryOptions.inScaled = false;

                if (SimpleStreamSurfaceView.this.mInMutableAvailable) {
                    SimpleStreamSurfaceView.this.initInBitmap(factoryOptions);
                }

                while (SimpleStreamSurfaceView.this.mWhileFetching && DODRAW)
                {
                    DataExtractor dataExtractor = null;
                    DataExtractor frameExtractor =null;
                    try {
                        dataExtractor = SimpleStreamSurfaceView.this.mJpegQueue.take();
                        if (!SimpleStreamSurfaceView.this.frameQueue.isEmpty())
                            frameExtractor = SimpleStreamSurfaceView.this.frameQueue.take();


                    } catch (IllegalArgumentException e) {
                        if (SimpleStreamSurfaceView.this.mInMutableAvailable) {
                            SimpleStreamSurfaceView.this.clearInBitmap(factoryOptions);
                        }
                        continue;
                    } catch (InterruptedException e) {
                        Log.e(SimpleStreamSurfaceView.TAG, "Drawer thread is Interrupted.");
                        break;
                    }
                    frameBitmap = BitmapFactory.decodeByteArray(dataExtractor.jpegData, 0, dataExtractor.jpegData.length, factoryOptions);

                    SimpleStreamSurfaceView.this.drawFrame(frameBitmap, frameExtractor);
                }

                if (frameBitmap != null) {
                    frameBitmap.recycle();
                }
                //SimpleStreamSurfaceView.this.mWhileFetching = false;
                IS_DRAWING = false;
            }
        });
    }

    private void fetchPayLoad(SimpleLiveviewSlicer slicer) throws IOException {
        DataExtractor payload = slicer.nextDataExtractor();
        if (payload.commonHeader == null) { // never occurs
            return;
        }
        if (payload.commonHeader.PayloadType == 1)
        {
            if (this.mJpegQueue.size() == 2) {
                this.mJpegQueue.remove();
            }
            this.mJpegQueue.add(payload);
        }
        if (payload.commonHeader.PayloadType == 2) {
            if (this.frameQueue.size() == 2) {
                this.frameQueue.remove();
            }
            this.frameQueue.add(payload);
        }
    }


    private int convert(int length, int val)
    {
        double pro = (double)val /(double)10000 * 100;
        double newret = (double)length /100 * pro;
        return (int)newret;
    }

    /**
     * Request to stop retrieving and drawing liveview data.
     */
    public void stop() {
        Log.d(TAG, "stop");
        this.mWhileFetching = false;

    }

    /**
     * Check to see whether start() is already called.
     *
     * @return true if start() is already called, false otherwise.
     */
    public boolean isStarted() {
        return this.mWhileFetching;
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    private void initInBitmap(BitmapFactory.Options options) {
        options.inBitmap = null;
        options.inMutable = true;
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    private void clearInBitmap(BitmapFactory.Options options) {
        if (options.inBitmap != null) {
            options.inBitmap.recycle();
            options.inBitmap = null;
        }
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    private void setInBitmap(BitmapFactory.Options options, Bitmap bitmap) {
        options.inBitmap = bitmap;
    }


    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    private void initRenderScript()
    {
        Type.Builder tbIn = new Type.Builder(renderScriptManager.GetRS(), Element.RGBA_8888(renderScriptManager.GetRS()));
        tbIn.setX(this.mPreviousWidth);
        tbIn.setY(this.mPreviousHeight);
        Type.Builder tbOut = new Type.Builder(renderScriptManager.GetRS(), Element.RGBA_8888(renderScriptManager.GetRS()));

        if (scalePreview) {
            mAllocationIn = Allocation.createTyped(renderScriptManager.GetRS(), tbIn.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            this.drawBitmap = Bitmap.createBitmap(this.mPreviousWidth * SCALEFACTOR, this.mPreviousHeight * SCALEFACTOR, Bitmap.Config.ARGB_8888);
            tbIn = new Type.Builder(renderScriptManager.GetRS(), Element.RGBA_8888(renderScriptManager.GetRS()));
            tbIn.setX(this.mPreviousWidth * SCALEFACTOR);
            tbIn.setY(this.mPreviousHeight * SCALEFACTOR);
            tbOut.setX(this.mPreviousWidth * SCALEFACTOR);
            tbOut.setY(this.mPreviousHeight * SCALEFACTOR);
            renderScriptManager.SetAllocsTypeBuilder(tbIn,tbOut,Allocation.USAGE_SCRIPT,Allocation.USAGE_SCRIPT);
            renderScriptManager.freedcamScript.set_Height(mPreviousHeight*SCALEFACTOR);
            renderScriptManager.freedcamScript.set_Width(mPreviousWidth*SCALEFACTOR);
            renderScriptManager.freedcamScript.set_gCurrentFrame(mAllocationIn);
            renderScriptManager.freedcamScript.set_gLastFrame(renderScriptManager.GetIn());
            renderScriptManager.convolve3x3.setInput(renderScriptManager.GetIn());
            renderScriptManager.convolve3x3.setCoefficients(SHARPMATRIX);
        }
        else
        {
            tbOut.setX(this.mPreviousWidth);
            tbOut.setY(this.mPreviousHeight);
            this.drawBitmap = Bitmap.createBitmap(this.mPreviousWidth, this.mPreviousHeight, Bitmap.Config.ARGB_8888);
            renderScriptManager.SetAllocsTypeBuilder(tbIn,tbOut,Allocation.USAGE_SCRIPT,Allocation.USAGE_SCRIPT);
            renderScriptManager.freedcamScript.set_gLastFrame(renderScriptManager.GetOut());
            renderScriptManager.freedcamScript.set_gCurrentFrame(renderScriptManager.GetIn());
        }
        renderScriptManager.blurRS.setInput(renderScriptManager.GetIn());






    }

    /**
     * Draw frame bitmap onto a canvas.
     *
     * @param frame
     */
    @TargetApi(VERSION_CODES.JELLY_BEAN_MR2)
    private void drawFrame(Bitmap frame, DataExtractor frameExtractor)
    {
        try {
            if (frame.getWidth() != this.mPreviousWidth || frame.getHeight() != this.mPreviousHeight) {
                this.onDetectedFrameSizeChanged(frame.getWidth(), frame.getHeight());
                return;
            }
            Canvas canvas = null;
            Rect dst = null;
            //canvas.drawColor(Color.BLACK);
            int frameWidth = frame.getWidth();
            int frameHeight = frame.getHeight();
            int fragmentwidth = this.getWidth();
            int fragmentheight = this.getHeight();

            if (renderScriptManager.isSucessfullLoaded())
            {

                if (scalePreview) {
                    renderScriptManager.freedcamScript.set_gCurrentFrame(mAllocationIn);
                    mAllocationIn.copyFrom(frame);
                    renderScriptManager.freedcamScript.forEach_clear(renderScriptManager.GetIn());
                    renderScriptManager.freedcamScript.forEach_fillPixelInterpolate(mAllocationIn);
                    renderScriptManager.blurRS.setRadius(1.5f);
                    renderScriptManager.blurRS.forEach(renderScriptManager.GetOut());
                    renderScriptManager.GetIn().copyFrom(renderScriptManager.GetOut());
                    renderScriptManager.convolve3x3.forEach(renderScriptManager.GetOut());
                    renderScriptManager.GetIn().copyFrom(renderScriptManager.GetOut());
                    frameWidth = frame.getWidth() * SCALEFACTOR;
                    frameHeight = frame.getHeight() * SCALEFACTOR;
                }
                else
                {

                    renderScriptManager.GetIn().copyFrom(frame);
                    renderScriptManager.GetOut().copyFrom(renderScriptManager.GetIn());
                }

                renderScriptManager.freedcamScript.set_gCurrentFrame(renderScriptManager.GetIn());

                Rect src = new Rect(0, 0, frameWidth, frameHeight);
                src = drawZoomPreview(frameWidth, frameHeight, src);


                float by = Math.min((float) fragmentwidth / frameWidth, (float) fragmentheight / frameHeight);
                int offsetX = (fragmentwidth - (int) (frameWidth * by)) / 2;
                int offsetY = (fragmentheight - (int) (frameHeight * by)) / 2;
                dst = new Rect(offsetX, offsetY, fragmentwidth - offsetX, fragmentheight - offsetY);

                if (nightmode == NightPreviewModes.on) {
                    if (!drawNightPreview())
                        return;
                } else if (nightmode == NightPreviewModes.grayscale) {
                    drawGrayScale();
                } else if (nightmode == NightPreviewModes.exposure) {
                    if (!drawExposureStack())
                        return;
                }
                if (focuspeak) {
                    renderScriptManager.freedcamScript.forEach_focuspeaksony(renderScriptManager.GetOut());

                }

                canvas = getHolder().lockCanvas();
                if (canvas == null) {
                    return;
                }
                renderScriptManager.GetOut().copyTo(drawBitmap);
                canvas.drawBitmap(this.drawBitmap, src, dst, this.mFramePaint);
            }
            else
            {
                Rect src = new Rect(0, 0, frameWidth, frameHeight);
                float by = Math.min((float) fragmentwidth / frameWidth, (float) fragmentheight / frameHeight);
                int offsetX = (fragmentwidth - (int) (frameWidth * by)) / 2;
                int offsetY = (fragmentheight - (int) (frameHeight * by)) / 2;
                dst = new Rect(offsetX, offsetY, fragmentwidth - offsetX, fragmentheight - offsetY);


                canvas.drawBitmap(frame, src, dst, this.mFramePaint);
            }
            if (canvas == null) {
                return;
            }
            if (frameExtractor != null)
                this.drawFrameInformation(frameExtractor, canvas, dst);

            this.getHolder().unlockCanvasAndPost(canvas);
        }
        catch(IllegalStateException ex)
        {Log.WriteEx(ex);}
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR2)
    private void drawGrayScale() {
        renderScriptManager.blurRS.setRadius(1.5f);
        renderScriptManager.blurRS.forEach(renderScriptManager.GetOut());
        renderScriptManager.GetIn().copyFrom(renderScriptManager.GetOut());
        renderScriptManager.freedcamScript.forEach_grayscale(renderScriptManager.GetOut());
        renderScriptManager.GetIn().copyFrom(renderScriptManager.GetOut());
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR2)
    private Rect drawZoomPreview(int frameWidth, int frameHeight, Rect src) {
        if (renderScriptManager.isSucessfullLoaded() && this.PreviewZOOMFactor > 1)
        {
            int zoomedWidth = frameWidth / this.PreviewZOOMFactor;
            int zoomedHeight = frameHeight/ this.PreviewZOOMFactor;
            int halfFrameWidth = frameWidth/2;
            int halfFrameHeight = frameHeight/2;
            int frameleft = halfFrameWidth -zoomedWidth + this.zoomPreviewMagineLeft;
            int frameright = halfFrameWidth +zoomedWidth + this.zoomPreviewMagineLeft;
            int frametop = halfFrameHeight -zoomedHeight + this.zoomPreviewMargineTop;
            int framebottom = halfFrameHeight +zoomedHeight + this.zoomPreviewMargineTop;

            if (frameleft < 0)
            {
                int dif = frameleft * -1;
                frameleft +=dif;
                frameright +=dif;
                Log.d(SimpleStreamSurfaceView.TAG, "zoommargineLeft = " + this.zoomPreviewMagineLeft);
                this.zoomPreviewMagineLeft +=dif;
                Log.d(SimpleStreamSurfaceView.TAG, "zoommargineLeft = " + this.zoomPreviewMagineLeft);
                Log.d(SimpleStreamSurfaceView.TAG, "frameleft < 0");
            }
            if (frameright > frameWidth)
            {
                int dif = frameright - frameWidth;
                frameright -=dif;
                frameleft -=dif;
                Log.d(SimpleStreamSurfaceView.TAG, "zoommargineLeft = " + this.zoomPreviewMagineLeft);
                this.zoomPreviewMagineLeft -=dif;
                Log.d(SimpleStreamSurfaceView.TAG, "zoommargineLeft = " + this.zoomPreviewMagineLeft);
                Log.d(SimpleStreamSurfaceView.TAG, "frameright > w");
            }
            if (frametop < 0)
            {
                int dif = frametop * -1;
                frametop +=dif;
                framebottom +=dif;
                Log.d(SimpleStreamSurfaceView.TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                this.zoomPreviewMargineTop +=dif;
                Log.d(SimpleStreamSurfaceView.TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                Log.d(SimpleStreamSurfaceView.TAG, "framebottom < 0");
            }
            if (framebottom > frameHeight)
            {
                int dif = framebottom -frameHeight;
                framebottom -=dif;
                frametop -= dif;
                Log.d(SimpleStreamSurfaceView.TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                this.zoomPreviewMargineTop -=dif;
                Log.d(SimpleStreamSurfaceView.TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                Log.d(SimpleStreamSurfaceView.TAG, "framebottom > h");
            }

            src = new Rect(frameleft,frametop,frameright,framebottom);
            renderScriptManager.blurRS.setRadius(0.3f);
            renderScriptManager.blurRS.forEach(renderScriptManager.GetOut());
            renderScriptManager.GetIn().copyFrom(renderScriptManager.GetOut());
        }
        return src;
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR2)
    private boolean drawExposureStack() {
        boolean draw = false;
        if (currentImageStackCount > 6)
        {
            renderScriptManager.GetOut().copyFrom(renderScriptManager.GetIn());
            currentImageStackCount = 0;
            Log.d(TAG,"Stackcount reset");
        }
        currentImageStackCount++;
        renderScriptManager.freedcamScript.forEach_stackimage_exposure(renderScriptManager.GetOut());
        renderScriptManager.GetOut().copyTo(drawBitmap);
        if (currentImageStackCount == 6)
            draw = true;
        return draw;
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR2)
    private boolean drawNightPreview() {
        renderScriptManager.blurRS.setRadius(1.5f);
        renderScriptManager.blurRS.forEach(renderScriptManager.GetOut());
        renderScriptManager.GetIn().copyFrom(renderScriptManager.GetOut());
        renderScriptManager.GetOut().copyFrom(drawBitmap);
        renderScriptManager.freedcamScript.forEach_stackimage_avarage(renderScriptManager.GetOut());
        renderScriptManager.GetOut().copyTo(this.drawBitmap);

        if (this.currentImageStackCount < 3)
            this.currentImageStackCount++;
        else
            this.currentImageStackCount = 0;
        if (this.currentImageStackCount < 3)
            return false;
        else
        {
            renderScriptManager.GetIn().copyFrom(this.drawBitmap);
            renderScriptManager.freedcamScript.set_brightness(100 / 255.0f);
            renderScriptManager.freedcamScript.forEach_processBrightness(renderScriptManager.GetOut());
            renderScriptManager.GetOut().copyTo(this.drawBitmap);
            renderScriptManager.GetIn().copyFrom(this.drawBitmap);
            renderScriptManager.freedcamScript.invoke_setContrast(200f);
            renderScriptManager.freedcamScript.forEach_processContrast(renderScriptManager.GetOut());
            renderScriptManager.GetOut().copyTo(this.drawBitmap);
            return true;
        }
    }

    private void drawFrameInformation(DataExtractor dataExtractor, Canvas canvas, Rect dst)
    {
        if (dataExtractor.frameInfoList == null)
            return;
        for (int i=0; i< dataExtractor.frameInfoList.size(); i++)
        {
            DataExtractor.FrameInfo frameInfo =  dataExtractor.frameInfoList.get(i);
            int w = this.getWidth();
            int h = this.getHeight();
            int top = this.convert(h, frameInfo.Top);
            int left = this.convert(w, frameInfo.Left);
            int right = this.convert(w,frameInfo.Right);
            int bottom = this.convert(h,frameInfo.Bottom);
            if (frameInfo.Category == 0x01)
            {
                //dst = new Rect(left, top, right, bottom);
                //Rect src = new Rect(0, 0, crosshairs[0].getWidth(), crosshairs[0].getHeight());
                if (frameInfo.Status == 0x01)
                    this.paint.setColor(Color.BLUE);
                //canvas.drawBitmap(crosshairs[0], src, dst, mFramePaint);
                if (frameInfo.Status == 0x00)
                    this.paint.setColor(Color.RED);
                //canvas.drawBitmap(crosshairs[1], src, dst, mFramePaint);
                if (frameInfo.Status == 0x04)
                    this.paint.setColor(Color.GREEN);
                //canvas.drawBitmap(crosshairs[2], src, dst, mFramePaint);
            }
            else if (frameInfo.Category == 0x05 ||frameInfo.Category == 0x04)
            {
                this.paint.setColor(Color.BLUE);

            }
            canvas.drawRect(left, top, right, bottom, this.paint);

        }
    }

    /**
     * Called when the width or height of liveview frame image is changed.
     *
     * @param width
     * @param height
     */
    private void onDetectedFrameSizeChanged(int width, int height) {
        Log.d(SimpleStreamSurfaceView.TAG, "Change of aspect ratio detected");
        this.mPreviousWidth = width;
        this.mPreviousHeight = height;
        this.initRenderScript();
        this.drawBlackFrame();
        this.drawBlackFrame();
        this.drawBlackFrame(); // delete triple buffers

    }


    /**
     * Draw black screen.
     */
    private void drawBlackFrame() {
        Canvas canvas = this.getHolder().lockCanvas();
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(new Rect(0, 0, this.getWidth(), this.getHeight()), paint);
        this.getHolder().unlockCanvasAndPost(canvas);
    }

    public interface StreamErrorListener {

        enum StreamErrorReason {
            IO_EXCEPTION,
            OPEN_ERROR,
        }

        void onError(StreamErrorReason reason);
    }

    /*private int startX;
    private int startY;
    private int currentX;
    private int currentY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (PreviewZOOMFactor > 1)
                    activityInterface.DisablePagerTouch(true);
                //action down resets all already set values and get the new one from the event
                startX = (int) event.getX();
                startY = (int) event.getY();
                //reset swipeDetected to false
                break;
            case MotionEvent.ACTION_MOVE:
                //in case action down never happend


                if (startX == 0 && startY == 0) {
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    //reset swipeDetected to false
                }
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                if (startX < currentX)
                {
                    zoomPreviewMagineLeft += (startX - currentX)/ PreviewZOOMFactor;
                }
                else
                    zoomPreviewMagineLeft -= (currentX - startX)/ PreviewZOOMFactor;
                if (startY < currentY)
                    zoomPreviewMargineTop += (startY - currentY)/ PreviewZOOMFactor;
                else
                    zoomPreviewMargineTop -= (currentY - startY)/ PreviewZOOMFactor;
                startX = currentX;
                startY = currentY;
                break;
            case MotionEvent.ACTION_UP:
                activityInterface.DisablePagerTouch(false);
                startY = 0;
                startX = 0;
                break;
        }
        return  super.onTouchEvent(event);
    }*/
}
