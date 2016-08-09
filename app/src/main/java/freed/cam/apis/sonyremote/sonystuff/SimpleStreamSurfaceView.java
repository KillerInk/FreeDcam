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
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView.StreamErrorListener.StreamErrorReason;
import freed.utils.FreeDPool;
import freed.utils.Logger;
import freed.utils.RenderScriptHandler;


/**
 * A SurfaceView based class to draw liveview frames serially.
 */
public class SimpleStreamSurfaceView extends SurfaceView implements SurfaceHolder.Callback, AbstractModeParameter.I_ModeParameterEvent {

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


    private Bitmap drawBitmap;
    private Bitmap stackBitmap;

    private int zoomPreviewMagineLeft;
    private int zoomPreviewMargineTop;
    private RenderScriptHandler renderScriptHandler;
    private ActivityInterface activityInterface;

    public int PreviewZOOMFactor = 1;

    public enum NightPreviewModes
    {
        on,
        off,
        grayscale,
        exposure,
        zoompreview,
    }

    public void SetRenderScriptHandlerAndInterface(RenderScriptHandler renderscripthandler, ActivityInterface activityInterface)
    {
        this.renderScriptHandler =renderscripthandler;
        this.activityInterface = activityInterface;
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR1)
    private void initRenderScript()
    {
        this.drawBitmap = Bitmap.createBitmap(this.mPreviousWidth, this.mPreviousHeight, Bitmap.Config.ARGB_8888);
        this.stackBitmap = Bitmap.createBitmap(this.mPreviousWidth, this.mPreviousHeight, Bitmap.Config.ARGB_8888);
        Type.Builder tbIn = new Type.Builder(renderScriptHandler.GetRS(), Element.RGBA_8888(renderScriptHandler.GetRS()));
        tbIn.setX(this.mPreviousWidth);
        tbIn.setY(this.mPreviousHeight);
        Type.Builder tbOut = new Type.Builder(renderScriptHandler.GetRS(), Element.RGBA_8888(renderScriptHandler.GetRS()));
        tbOut.setX(this.mPreviousWidth);
        tbOut.setY(this.mPreviousHeight);

        renderScriptHandler.SetAllocsTypeBuilder(tbIn,tbOut,Allocation.USAGE_SCRIPT,Allocation.USAGE_SCRIPT);
        renderScriptHandler.imagestack.set_gLastFrame(renderScriptHandler.GetOut());
        renderScriptHandler.imagestack.set_gCurrentFrame(renderScriptHandler.GetIn());
        renderScriptHandler.blurRS.setInput(renderScriptHandler.GetIn());
        renderScriptHandler.starfinderRS.set_gCurrentFrame(renderScriptHandler.GetIn());
        renderScriptHandler.focuspeak_argb.set_gCurrentFrame(renderScriptHandler.GetIn());
        renderScriptHandler.contrastRS.set_gCurrentFrame(renderScriptHandler.GetIn());
        renderScriptHandler.brightnessRS.set_gCurrentFrame(renderScriptHandler.GetIn());

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
            Logger.e(TAG, "start() streamUrl is null.");
            mWhileFetching = false;
            mErrorListener.onError(StreamErrorReason.OPEN_ERROR);
            return;
        }
        if (this.mWhileFetching) {
            Logger.d(SimpleStreamSurfaceView.TAG, "start() already starting.");
            return;
        }

        this.mWhileFetching = true;

        // A thread for retrieving liveview data from server.
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                Logger.d(SimpleStreamSurfaceView.TAG, "Starting retrieving streaming data from server.");
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
                    Logger.d(TAG, "IOException while fetching: " + e.getMessage());
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

        // A thread for drawing liveview frame fetched by above thread.
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                Logger.d(SimpleStreamSurfaceView.TAG, "Starting drawing stream frame.");
                Bitmap frameBitmap = null;

                BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
                factoryOptions.inSampleSize = 1;
                factoryOptions.inPreferQualityOverSpeed = true;
                factoryOptions.inDither = false;
                factoryOptions.inScaled = false;

                if (SimpleStreamSurfaceView.this.mInMutableAvailable) {
                    SimpleStreamSurfaceView.this.initInBitmap(factoryOptions);
                }

                while (SimpleStreamSurfaceView.this.mWhileFetching)
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
                        Logger.e(SimpleStreamSurfaceView.TAG, "Drawer thread is Interrupted.");
                        break;
                    }
                    frameBitmap = BitmapFactory.decodeByteArray(dataExtractor.jpegData, 0, dataExtractor.jpegData.length, factoryOptions);

                    SimpleStreamSurfaceView.this.drawFrame(frameBitmap, frameExtractor);
                }

                if (frameBitmap != null) {
                    frameBitmap.recycle();
                }
                SimpleStreamSurfaceView.this.mWhileFetching = false;
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

    /**
     * Draw frame bitmap onto a canvas.
     *
     * @param frame
     */
    private void drawFrame(Bitmap frame,DataExtractor frameExtractor)
    {
        try {
            if (frame.getWidth() != this.mPreviousWidth || frame.getHeight() != this.mPreviousHeight) {
                this.onDetectedFrameSizeChanged(frame.getWidth(), frame.getHeight());
                return;
            }

            //canvas.drawColor(Color.BLACK);
            int frameWidth = frame.getWidth();
            int frameHeight = frame.getHeight();
            Rect src = new Rect(0, 0, frameWidth, frameHeight);
            src = drawZoomPreview(frame, frameWidth, frameHeight, src);


            float by = Math.min((float) this.getWidth() / frameWidth, (float) this.getHeight() / frameHeight);
            int offsetX = (this.getWidth() - (int) (frameWidth * by)) / 2;
            int offsetY = (this.getHeight() - (int) (frameHeight * by)) / 2;
            Rect dst = new Rect(offsetX, offsetY, this.getWidth() - offsetX, this.getHeight() - offsetY);
            if (renderScriptHandler.isSucessfullLoaded()) {
                if (nightmode == NightPreviewModes.on) {
                    if (!drawNightPreview(frame, frameExtractor, src, dst))
                        return;
                } else if (nightmode == NightPreviewModes.grayscale) {
                    drawGrayScale(frame);
                } else if (nightmode == NightPreviewModes.exposure) {
                    if (!drawExposureStack(frame))
                        return;
                }
                if (focuspeak) {
                    if (nightmode != NightPreviewModes.off || this.PreviewZOOMFactor > 1)
                        renderScriptHandler.GetIn().copyFrom(this.drawBitmap);
                    else
                        renderScriptHandler.GetIn().copyFrom(frame);
                    renderScriptHandler.focuspeak_argb.forEach_peak(renderScriptHandler.GetOut());
                    renderScriptHandler.GetOut().copyTo(drawBitmap);

                }
            }
            Canvas canvas = getHolder().lockCanvas();
            if (canvas == null) {
                return;
            }
            if ((nightmode != NightPreviewModes.off || this.focuspeak) && renderScriptHandler.isSucessfullLoaded())
                canvas.drawBitmap(this.drawBitmap, src, dst, this.mFramePaint);
            else
                canvas.drawBitmap(frame, src, dst, this.mFramePaint);
            if (frameExtractor != null)
                this.drawFrameInformation(frameExtractor, canvas, dst);

            this.getHolder().unlockCanvasAndPost(canvas);
        }
        catch(IllegalStateException ex)
        {Logger.exception(ex);}
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR2)
    private void drawGrayScale(Bitmap frame) {
        renderScriptHandler.GetIn().copyFrom(frame);
        renderScriptHandler.blurRS.setRadius(1.5f);
        renderScriptHandler.blurRS.forEach(renderScriptHandler.GetOut());
        renderScriptHandler.GetIn().copyFrom(renderScriptHandler.GetOut());
        renderScriptHandler.starfinderRS.forEach_processBrightness(renderScriptHandler.GetOut());
        renderScriptHandler.GetOut().copyTo(drawBitmap);
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR2)
    private Rect drawZoomPreview(Bitmap frame, int frameWidth, int frameHeight, Rect src) {
        if (renderScriptHandler.isSucessfullLoaded() && this.PreviewZOOMFactor > 1)
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
                Logger.d(SimpleStreamSurfaceView.TAG, "zoommargineLeft = " + this.zoomPreviewMagineLeft);
                this.zoomPreviewMagineLeft +=dif;
                Logger.d(SimpleStreamSurfaceView.TAG, "zoommargineLeft = " + this.zoomPreviewMagineLeft);
                Log.d(SimpleStreamSurfaceView.TAG, "frameleft < 0");
            }
            if (frameright > frameWidth)
            {
                int dif = frameright - frameWidth;
                frameright -=dif;
                frameleft -=dif;
                Logger.d(SimpleStreamSurfaceView.TAG, "zoommargineLeft = " + this.zoomPreviewMagineLeft);
                this.zoomPreviewMagineLeft -=dif;
                Logger.d(SimpleStreamSurfaceView.TAG, "zoommargineLeft = " + this.zoomPreviewMagineLeft);
                Log.d(SimpleStreamSurfaceView.TAG, "frameright > w");
            }
            if (frametop < 0)
            {
                int dif = frametop * -1;
                frametop +=dif;
                framebottom +=dif;
                Logger.d(SimpleStreamSurfaceView.TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                this.zoomPreviewMargineTop +=dif;
                Logger.d(SimpleStreamSurfaceView.TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                Log.d(SimpleStreamSurfaceView.TAG, "framebottom < 0");
            }
            if (framebottom > frameHeight)
            {
                int dif = framebottom -frameHeight;
                framebottom -=dif;
                frametop -= dif;
                Logger.d(SimpleStreamSurfaceView.TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                this.zoomPreviewMargineTop -=dif;
                Logger.d(SimpleStreamSurfaceView.TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                Log.d(SimpleStreamSurfaceView.TAG, "framebottom > h");
            }

            src = new Rect(frameleft,frametop,frameright,framebottom);
            //Logger.d(TAG, src.flattenToString());
            renderScriptHandler.GetIn().copyFrom(frame);
            renderScriptHandler.blurRS.setRadius(0.3f);
            renderScriptHandler.blurRS.forEach(renderScriptHandler.GetOut());
            renderScriptHandler.GetOut().copyTo(this.drawBitmap);
        }
        return src;
    }

    private boolean drawExposureStack(Bitmap frame) {
        renderScriptHandler.GetIn().copyFrom(frame);
        boolean draw = false;
        if (currentImageStackCount > 6)
        {
            renderScriptHandler.GetOut().copyFrom(frame);
            currentImageStackCount = 0;
            Log.d(TAG,"Stackcount reset");
        }
        currentImageStackCount++;
        renderScriptHandler.imagestack.forEach_stackimage_exposure(renderScriptHandler.GetOut());
        renderScriptHandler.GetOut().copyTo(drawBitmap);
        if (currentImageStackCount == 6)
            draw = true;
        return draw;
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN_MR2)
    private boolean drawNightPreview(Bitmap frame, DataExtractor frameExtractor, Rect src, Rect dst) {
        renderScriptHandler.GetIn().copyFrom(frame);
        renderScriptHandler.blurRS.setRadius(1.5f);
        renderScriptHandler.blurRS.forEach(renderScriptHandler.GetOut());
        renderScriptHandler.GetIn().copyFrom(renderScriptHandler.GetOut());
        renderScriptHandler.GetOut().copyFrom(drawBitmap);
        renderScriptHandler.imagestack.forEach_stackimage_avarage(renderScriptHandler.GetOut());
        renderScriptHandler.GetOut().copyTo(this.drawBitmap);

        if (this.currentImageStackCount < 3)
            this.currentImageStackCount++;
        else
            this.currentImageStackCount = 0;
        if (this.currentImageStackCount < 3)
            return false;
        else
        {
            renderScriptHandler.GetIn().copyFrom(this.drawBitmap);
            renderScriptHandler.brightnessRS.set_brightness(100 / 255.0f);
            renderScriptHandler.brightnessRS.forEach_processBrightness(renderScriptHandler.GetOut());
            renderScriptHandler.GetOut().copyTo(this.drawBitmap);
            renderScriptHandler.GetIn().copyFrom(this.drawBitmap);
            renderScriptHandler.contrastRS.invoke_setBright(200f);
            renderScriptHandler.contrastRS.forEach_processContrast(renderScriptHandler.GetOut());
            renderScriptHandler.GetOut().copyTo(this.drawBitmap);
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
                dst = new Rect(left, top, right, bottom);
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
        Logger.d(SimpleStreamSurfaceView.TAG, "Change of aspect ratio detected");
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

    @Override
    public void onParameterValueChanged(String val) {

    }

    @Override
    public void onParameterIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onParameterIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onParameterValuesChanged(String[] values) {

    }

    @Override
    public void onVisibilityChanged(boolean visible) {

    }

    public interface StreamErrorListener {

        enum StreamErrorReason {
            IO_EXCEPTION,
            OPEN_ERROR,
        }

        void onError(StreamErrorReason reason);
    }

    private int startX;
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
                    zoomPreviewMagineLeft -= (startX - currentX)/ PreviewZOOMFactor;
                }
                else
                    zoomPreviewMagineLeft += (currentX - startX)/ PreviewZOOMFactor;
                if (startY < currentY)
                    zoomPreviewMargineTop -= (startY - currentY)/ PreviewZOOMFactor;
                else
                    zoomPreviewMargineTop += (currentY - startY)/ PreviewZOOMFactor;
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
    }
}
