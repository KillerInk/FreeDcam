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
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.imageconverter.ScriptC_brightness;
import com.imageconverter.ScriptC_contrast;
import com.imageconverter.ScriptC_focuspeak_argb;
import com.imageconverter.ScriptC_imagestack;
import com.imageconverter.ScriptC_starfinder;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView.StreamErrorListener.StreamErrorReason;
import freed.utils.FreeDPool;
import freed.utils.Logger;



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

    private RenderScript mRS;
    private Allocation mInputAllocation;
    private Allocation mInputAllocation2;
    private Allocation mOutputAllocation;
    private ScriptC_focuspeak_argb focuspeak_argb;
    private ScriptC_imagestack imagestack_argb;
    private ScriptC_brightness brightnessRS;
    private ScriptC_contrast contrastRS;
    private ScriptC_starfinder starfinderRS;
    private ScriptIntrinsicBlur blurRS;
    private Bitmap drawBitmap;
    private Bitmap stackBitmap;

    private int zoomPreviewMagineLeft;
    private int zoomPreviewMargineTop;

    public int PreviewZOOMFactor = 1;

    public enum NightPreviewModes
    {
        on,
        off,
        grayscale,
        zoompreview,
    }

    private void initRenderScript()
    {
        this.drawBitmap = Bitmap.createBitmap(this.mPreviousWidth, this.mPreviousHeight, Bitmap.Config.ARGB_8888);
        this.stackBitmap = Bitmap.createBitmap(this.mPreviousWidth, this.mPreviousHeight, Bitmap.Config.ARGB_8888);
        Type.Builder tbIn = new Type.Builder(this.mRS, Element.RGBA_8888(this.mRS));
        tbIn.setX(this.mPreviousWidth);
        tbIn.setY(this.mPreviousHeight);
        Type.Builder tbIn2 = new Type.Builder(this.mRS, Element.RGBA_8888(this.mRS));
        tbIn2.setX(this.mPreviousWidth);
        tbIn2.setY(this.mPreviousHeight);

        Type.Builder tbOut = new Type.Builder(this.mRS, Element.RGBA_8888(this.mRS));
        tbOut.setX(this.mPreviousWidth);
        tbOut.setY(this.mPreviousHeight);

        this.mInputAllocation = Allocation.createTyped(this.mRS, tbIn.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        this.mInputAllocation2 = Allocation.createTyped(this.mRS, tbIn.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        this.mOutputAllocation = Allocation.createTyped(this.mRS, tbOut.create(), Allocation.MipmapControl.MIPMAP_NONE,  Allocation.USAGE_SCRIPT);
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
        this.initBitmaps(context);
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
        this.initBitmaps(context);
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
        this.initBitmaps(context);
    }

    private void initBitmaps(Context context)
    {
        this.paint = new Paint();
        this.paint.setColor(Color.WHITE);
        this.paint.setStrokeWidth(5);
        this.paint.setStyle(Paint.Style.STROKE);
        if (Build.VERSION.SDK_INT >= 18) {
            this.mRS = RenderScript.create(context);
            this.focuspeak_argb = new ScriptC_focuspeak_argb(this.mRS);
            this.imagestack_argb = new ScriptC_imagestack(this.mRS);
            this.brightnessRS = new ScriptC_brightness(this.mRS);
            this.contrastRS = new ScriptC_contrast(this.mRS);
            this.blurRS = ScriptIntrinsicBlur.create(this.mRS, Element.U8_4(this.mRS));
            this.starfinderRS = new ScriptC_starfinder(this.mRS);
        }

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

                    SimpleStreamSurfaceView.this.drawFrame(frameBitmap, dataExtractor, frameExtractor);
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
    @TargetApi(VERSION_CODES.JELLY_BEAN_MR2)
    private void drawFrame(Bitmap frame, DataExtractor dataExtractor, DataExtractor frameExtractor)
    {
        try {
            if (frame.getWidth() != this.mPreviousWidth || frame.getHeight() != this.mPreviousHeight) {
                this.onDetectedFrameSizeChanged(frame.getWidth(), frame.getHeight());
                return;
            }

            //canvas.drawColor(Color.BLACK);
            int w = frame.getWidth();
            int h = frame.getHeight();
            Rect src = new Rect(0, 0, w, h);
            if (this.PreviewZOOMFactor > 1)
            {
                int w4 = w / this.PreviewZOOMFactor;
                int h4 = h/ this.PreviewZOOMFactor;
                int wCenter = w/2;
                int hCenter = h/2;
                int frameleft = wCenter -w4 + this.zoomPreviewMagineLeft;
                int frameright = wCenter +w4 + this.zoomPreviewMagineLeft;
                int frametop = hCenter -h4 + this.zoomPreviewMargineTop;
                int framebottom = hCenter +h4 + this.zoomPreviewMargineTop;

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
                if (frameright > w)
                {
                    int dif = frameright - w;
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
                if (framebottom > h)
                {
                    int dif = framebottom -h;
                    framebottom -=dif;
                    frametop -= dif;
                    Logger.d(SimpleStreamSurfaceView.TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                    this.zoomPreviewMargineTop -=dif;
                    Logger.d(SimpleStreamSurfaceView.TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                    Log.d(SimpleStreamSurfaceView.TAG, "framebottom > h");
                }

                src = new Rect(frameleft,frametop,frameright,framebottom);
                //Logger.d(TAG, src.flattenToString());
                this.mInputAllocation.copyFrom(frame);
                this.blurRS.setInput(this.mInputAllocation);
                this.blurRS.setRadius(0.3f);
                this.blurRS.forEach(this.mOutputAllocation);
                this.mOutputAllocation.copyTo(this.drawBitmap);
            }


            float by = Math.min((float) this.getWidth() / w, (float) this.getHeight() / h);
            int offsetX = (this.getWidth() - (int) (w * by)) / 2;
            int offsetY = (this.getHeight() - (int) (h * by)) / 2;
            Rect dst = new Rect(offsetX, offsetY, this.getWidth() - offsetX, this.getHeight() - offsetY);
            if (nightmode == NightPreviewModes.on)
            {
                if(!drawNightPreview(frame, frameExtractor, src, dst))
                    return;
            }
            else if (nightmode == NightPreviewModes.grayscale)
            {
                this.mInputAllocation.copyFrom(frame);
                this.blurRS.setInput(this.mInputAllocation);
                this.blurRS.setRadius(1.5f);
                this.blurRS.forEach(this.mOutputAllocation);
                this.mInputAllocation.copyFrom(this.mOutputAllocation);
                this.starfinderRS.set_gCurrentFrame(this.mInputAllocation);
                this.starfinderRS.forEach_processBrightness(this.mOutputAllocation);
                mOutputAllocation.copyTo(drawBitmap);

            }
            if (focuspeak) {
                if (nightmode != NightPreviewModes.off || this.PreviewZOOMFactor > 1)
                    this.mInputAllocation.copyFrom(this.drawBitmap);
                else
                    this.mInputAllocation.copyFrom(frame);
                this.focuspeak_argb.set_gCurrentFrame(this.mInputAllocation);
                this.focuspeak_argb.forEach_peak(this.mOutputAllocation);
                mOutputAllocation.copyTo(drawBitmap);

            }
            Canvas canvas = getHolder().lockCanvas();
            if (canvas == null) {
                return;
            }
            if (nightmode != NightPreviewModes.off || this.focuspeak)
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
    private boolean drawNightPreview(Bitmap frame, DataExtractor frameExtractor, Rect src, Rect dst) {
        this.mInputAllocation.copyFrom(frame);
        this.blurRS.setInput(this.mInputAllocation);
        this.blurRS.setRadius(1.5f);
        this.blurRS.forEach(this.mOutputAllocation);
        this.mInputAllocation.copyFrom(this.mOutputAllocation);
        if (this.currentImageStackCount == 0)
            this.mInputAllocation2.copyFrom(frame);
        else
            this.mInputAllocation2.copyFrom(this.drawBitmap);
        this.imagestack_argb.set_gCurrentFrame(this.mInputAllocation);
        this.imagestack_argb.set_gLastFrame(this.mInputAllocation2);
        this.imagestack_argb.forEach_stackimage_avarage(this.mOutputAllocation);
        this.mOutputAllocation.copyTo(this.drawBitmap);

        if (this.currentImageStackCount < 3)
            this.currentImageStackCount++;
        else
            this.currentImageStackCount = 0;
        if (this.currentImageStackCount < 3)
            return false;
        else
        {
            this.mInputAllocation.copyFrom(this.drawBitmap);
            this.brightnessRS.set_gCurrentFrame(this.mInputAllocation);
            this.brightnessRS.set_brightness(100 / 255.0f);
            this.brightnessRS.forEach_processBrightness(this.mOutputAllocation);
            this.mOutputAllocation.copyTo(this.drawBitmap);
            this.mInputAllocation.copyFrom(this.drawBitmap);
            this.contrastRS.set_gCurrentFrame(this.mInputAllocation);
            this.contrastRS.invoke_setBright(200f);
            this.contrastRS.forEach_processContrast(this.mOutputAllocation);
            this.mOutputAllocation.copyTo(this.drawBitmap);
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
                if (startX > currentX)
                {
                    zoomPreviewMagineLeft -= (startX - currentX)/ PreviewZOOMFactor;
                }
                else
                    zoomPreviewMagineLeft += (currentX - startX)/ PreviewZOOMFactor;
                if (startY > currentY)
                    zoomPreviewMargineTop -= (startY - currentY)/ PreviewZOOMFactor;
                else
                    zoomPreviewMargineTop += (currentY - startY)/ PreviewZOOMFactor;
                startX = currentX;
                startY = currentY;
                //detect swipeDetected. if swipeDetected detected return false else true
                break;
            case MotionEvent.ACTION_UP:
                startY = 0;
                startX = 0;
                break;
        }
        return  super.onTouchEvent(event);
    }
}
