package freed.cam.apis.sonyremote;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.Type;
import android.view.Surface;
import android.view.TextureView;

import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import freed.ActivityInterface;
import freed.FreedApplication;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.sonyremote.sonystuff.DataExtractor;
import freed.cam.apis.sonyremote.sonystuff.SimpleLiveviewSlicer;
import freed.cam.events.DisableViewPagerTouchEvent;
import freed.cam.events.EventBusHelper;
import freed.renderscript.RenderScriptManager;
import freed.renderscript.RenderScriptProcessorInterface;
import freed.utils.FreeDPool;
import freed.utils.Log;

public class PreviewStreamDrawer implements ParameterEvents, RenderScriptProcessorInterface {
    private final String TAG = PreviewStreamDrawer.class.getSimpleName();

    private TextureView textureView;

    private boolean mWhileFetching;
    private final BlockingQueue<DataExtractor> mJpegQueue = new ArrayBlockingQueue<>(2);
    private final BlockingQueue<DataExtractor> frameQueue = new ArrayBlockingQueue<>(2);
    private final boolean mInMutableAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
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
    private boolean blue = true;
    private boolean green = true;
    private boolean red = true;

    private boolean useRenderScript = false;

    @Subscribe
    public void onJoypadTouchUp(boolean up)
    {
        if (up)
            EventBusHelper.post(new DisableViewPagerTouchEvent(false));
        else
            EventBusHelper.post(new DisableViewPagerTouchEvent(true));
    }

    @Subscribe
    public void onJoypadMove(int x, int y)
    {
        zoomPreviewMagineLeft += (x);
        zoomPreviewMargineTop += (y);
    }

    public PreviewStreamDrawer(TextureView textureView,RenderScriptManager renderScriptManager)
    {
        this.textureView = textureView;
        this.mFramePaint = new Paint();
        this.mFramePaint.setDither(true);
        this.initPaint(FreedApplication.getContext());
        this.renderScriptManager = renderScriptManager;
    }

    @Override
    public void onViewStateChanged(AbstractParameter.ViewState value) {

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
            mErrorListener.onError(StreamErrorListener.StreamErrorReason.OPEN_ERROR);
            return;
        }
        if (this.mWhileFetching) {
            Log.d(TAG, "start() already starting.");
            return;
        }

        this.mWhileFetching = true;

        // A thread for retrieving liveview data from server.
        FreeDPool.Execute(() -> {
            Log.d(TAG, "Starting retrieving streaming data from server.");
            SimpleLiveviewSlicer slicer = null;

            try {

                // Create Slicer to open the stream and parse it.
                slicer = new SimpleLiveviewSlicer();
                slicer.open(streamUrl);

                while (mWhileFetching)
                {
                    fetchPayLoad(slicer);
                }
            } catch (IOException e) {
                Log.d(TAG, "IOException while fetching: " + e.getMessage());
                mErrorListener.onError(StreamErrorListener.StreamErrorReason.IO_EXCEPTION);
            } finally {
                if (slicer != null) {
                    slicer.close();
                }


                mJpegQueue.clear();
                frameQueue.clear();
                mWhileFetching = false;
            }
        });
        startDrawingThread();


    }

    private void startDrawingThread() {
        DODRAW = true;
        // A thread for drawing liveview frame fetched by above thread.
        FreeDPool.Execute(() -> {
            IS_DRAWING = true;
            Log.d(TAG, "Starting drawing stream frame.");
            Bitmap frameBitmap = null;

            BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
            factoryOptions.inSampleSize = 1;
            factoryOptions.inPreferQualityOverSpeed = true;
            factoryOptions.inDither = false;
            factoryOptions.inScaled = false;

            if (mInMutableAvailable) {
                initInBitmap(factoryOptions);
            }

            while (mWhileFetching && DODRAW)
            {
                DataExtractor dataExtractor = null;
                DataExtractor frameExtractor =null;
                try {
                    dataExtractor = mJpegQueue.take();
                    if (!frameQueue.isEmpty())
                        frameExtractor = frameQueue.take();


                } catch (IllegalArgumentException e) {
                    if (mInMutableAvailable) {
                        clearInBitmap(factoryOptions);
                    }
                    continue;
                } catch (InterruptedException e) {
                    Log.e(TAG, "Drawer thread is Interrupted.");
                    break;
                }
                frameBitmap = BitmapFactory.decodeByteArray(dataExtractor.jpegData, 0, dataExtractor.jpegData.length, factoryOptions);

                drawFrame(frameBitmap, frameExtractor);
            }

            if (frameBitmap != null) {
                frameBitmap.recycle();
            }
            //mWhileFetching = false;
            IS_DRAWING = false;
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initInBitmap(BitmapFactory.Options options) {
        options.inBitmap = null;
        options.inMutable = true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void clearInBitmap(BitmapFactory.Options options) {
        if (options.inBitmap != null) {
            options.inBitmap.recycle();
            options.inBitmap = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setInBitmap(BitmapFactory.Options options, Bitmap bitmap) {
        options.inBitmap = bitmap;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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
            renderScriptManager.rgb_focuspeak.set_input(mAllocationIn);
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
            renderScriptManager.rgb_focuspeak.set_input(renderScriptManager.GetIn());
        }
        renderScriptManager.blurRS.setInput(renderScriptManager.GetIn());






    }

    /**
     * Draw frame bitmap onto a canvas.
     *
     * @param frame
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
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
            int fragmentwidth = textureView.getWidth();
            int fragmentheight = textureView.getHeight();

            if (renderScriptManager.isSucessfullLoaded() && useRenderScript)
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
                    renderScriptManager.rgb_focuspeak.set_blue(blue);
                    renderScriptManager.rgb_focuspeak.set_red(red);
                    renderScriptManager.rgb_focuspeak.set_green(green);
                    renderScriptManager.rgb_focuspeak.forEach_focuspeak(renderScriptManager.GetOut());
                }

                canvas = textureView.lockCanvas();
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

                canvas = textureView.lockCanvas();
                canvas.drawBitmap(frame, src, dst, this.mFramePaint);
            }
            if (canvas == null) {
                return;
            }
            if (frameExtractor != null)
                this.drawFrameInformation(frameExtractor, canvas, dst);

            textureView.unlockCanvasAndPost(canvas);
        }
        catch(IllegalStateException ex)
        {Log.WriteEx(ex);}
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void drawGrayScale() {
        renderScriptManager.blurRS.setRadius(1.5f);
        renderScriptManager.blurRS.forEach(renderScriptManager.GetOut());
        renderScriptManager.GetIn().copyFrom(renderScriptManager.GetOut());
        renderScriptManager.freedcamScript.forEach_grayscale(renderScriptManager.GetOut());
        renderScriptManager.GetIn().copyFrom(renderScriptManager.GetOut());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
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
                Log.d(TAG, "zoommargineLeft = " + this.zoomPreviewMagineLeft);
                this.zoomPreviewMagineLeft +=dif;
                Log.d(TAG, "zoommargineLeft = " + this.zoomPreviewMagineLeft);
                Log.d(TAG, "frameleft < 0");
            }
            if (frameright > frameWidth)
            {
                int dif = frameright - frameWidth;
                frameright -=dif;
                frameleft -=dif;
                Log.d(TAG, "zoommargineLeft = " + this.zoomPreviewMagineLeft);
                this.zoomPreviewMagineLeft -=dif;
                Log.d(TAG, "zoommargineLeft = " + this.zoomPreviewMagineLeft);
                Log.d(TAG, "frameright > w");
            }
            if (frametop < 0)
            {
                int dif = frametop * -1;
                frametop +=dif;
                framebottom +=dif;
                Log.d(TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                this.zoomPreviewMargineTop +=dif;
                Log.d(TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                Log.d(TAG, "framebottom < 0");
            }
            if (framebottom > frameHeight)
            {
                int dif = framebottom -frameHeight;
                framebottom -=dif;
                frametop -= dif;
                Log.d(TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                this.zoomPreviewMargineTop -=dif;
                Log.d(TAG, "zoomPreviewMargineTop = " + this.zoomPreviewMargineTop);
                Log.d(TAG, "framebottom > h");
            }

            src = new Rect(frameleft,frametop,frameright,framebottom);
            renderScriptManager.blurRS.setRadius(0.3f);
            renderScriptManager.blurRS.forEach(renderScriptManager.GetOut());
            renderScriptManager.GetIn().copyFrom(renderScriptManager.GetOut());
        }
        return src;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
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
            int w = textureView.getWidth();
            int h = textureView.getHeight();
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
        Log.d(TAG, "Change of aspect ratio detected");
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
        Canvas canvas = textureView.lockCanvas();
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawRect(new Rect(0, 0, textureView.getWidth(), textureView.getHeight()), paint);
        textureView.unlockCanvasAndPost(canvas);
    }

    public interface StreamErrorListener {

        enum StreamErrorReason {
            IO_EXCEPTION,
            OPEN_ERROR,
        }

        void onError(StreamErrorListener.StreamErrorReason reason);
    }


    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setFocusPeakEnable(boolean enable) {
        useRenderScript = enable;
    }

    @Override
    public void setHistogramEnable(boolean enable) {

    }

    @Override
    public void setClippingEnable(boolean enable) {

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

    @Override
    public void Reset(int width, int height, Surface surface) {

    }

    @Override
    public Surface getInputSurface() {
        return null;
    }

    @Override
    public void start() {

    }


    @Override
    public void kill() {

    }
}
