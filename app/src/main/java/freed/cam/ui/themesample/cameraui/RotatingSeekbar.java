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

package freed.cam.ui.themesample.cameraui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar.OnSeekBarChangeListener;

import freed.utils.Log;

/**
 * Created by troop on 07.12.2015.
 */
public class RotatingSeekbar extends View
{

    private class UiHandler extends Handler
    {
        private final int INVALIDATE = 0;
        private final int ONPROGRESSCHANGED = 1;
        private final int ONHANDELAUTOSCROLL = 2;


        public void setINVALIDATE()
        {
            this.obtainMessage(INVALIDATE).sendToTarget();
        }

        public void setONHANDELAUTOSCROLL()
        {
            this.obtainMessage(ONHANDELAUTOSCROLL).sendToTarget();
        }

        public void setONPROGRESSCHANGED(int value)
        {
            this.obtainMessage(ONPROGRESSCHANGED,value,0).sendToTarget();
        }

        public UiHandler()
        {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what)
            {
                case INVALIDATE:
                    RotatingSeekbar.this.invalidate();
                    break;
                case ONPROGRESSCHANGED:
                    if(mListener != null)
                        mListener.onProgressChanged(null, msg.arg1, true);
                    break;
                case ONHANDELAUTOSCROLL:
                    if (!autoscroll)
                        return;
                    int newpos = currentPosToDraw - distanceInPixelFromLastSwipe - scrollsubstract;
                    int positivepos = newpos *-1;
                    if (positivepos <= realMax && positivepos >= realMin)
                    {
                        log("scroll pos:" + newpos +" max:" + realMax + " min:" + realMin);
                        boolean rerun = false;
                        if (distanceInPixelFromLastSwipe < 0 && distanceInPixelFromLastSwipe + scrollsubstract < 0) {
                            distanceInPixelFromLastSwipe += scrollsubstract;
                            rerun = true;
                            currentPosToDraw -= distanceInPixelFromLastSwipe;
                            checkifCurrentValueHasChanged();
                        } else if (distanceInPixelFromLastSwipe > 0 && distanceInPixelFromLastSwipe - scrollsubstract > 0) {
                            distanceInPixelFromLastSwipe -= scrollsubstract;
                            rerun = true;
                            currentPosToDraw -= distanceInPixelFromLastSwipe;
                            checkifCurrentValueHasChanged();
                        }
                        else
                        {
                            checkifCurrentValueHasChanged();
                            distanceInPixelFromLastSwipe = 0;
                            setProgress(currentValue,true);
                            rerun = false;
                        }
                        if (rerun)
                            handleAutoScroll();
                    }
                    else
                    {
                        autoscroll = false;
                        distanceInPixelFromLastSwipe = 0;
                        if(positivepos > realMax)
                            setProgress(Values.length-1,true);
                        else if (positivepos < realMin)
                            setProgress(0,true);
                        else {
                            checkifCurrentValueHasChanged();
                            if (currentValue > Values.length -1)
                                currentValue = Values.length -1;
                            if (currentValue < 0)
                                currentValue = 0;
                            setProgress(currentValue,true);

                        }
                        //log("scroll pos:" + newpos + " max:" + realMax + " min:" + realMin);
                    }
                    handler.setINVALIDATE();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    }

    private String[] Values = "Auto,1/100000,1/6000,1/4000,1/2000,1/1000,1/500,1/250,1/125,1/60,1/30,1/15,1/8,1/4,1/2,2,4,8,15,30,60,180".split(",");
    private int currentValue = 3;
    private Paint paint;
    private int viewWidth;
    private int viewHeight;
    private int itemHeight;
    //height of one item in pixel
    private int allItemsHeight;
    private int realMin;
    private int realMax;
    private int currentPosToDraw;
    private OnSeekBarChangeListener mListener;
    private int textsize = 10;
    //holds the distance from the last swipe(how faster it was how bigger is the vale) and is used as base gravity for autoscroll how fast it moves
    private int distanceInPixelFromLastSwipe;
    private boolean autoscroll;
    private final int textColor = Color.WHITE;
    private final String TAG = RotatingSeekbar.class.getSimpleName();
    //this handels how much get added or substracted from @distanceInPixelFromLastSwipe when autoscroll = true
    private final int scrollsubstract = 1;
    private UiHandler handler;

    //holds the position when user touched down
    private int startY;
    private boolean sliderMoving;
    //paint object to draw the grandient to background
    private Paint grandientPaint;
    //draws from top to half view height transparent to black
    private LinearGradient topToHalfGradient;
    //draws from half view to bottom black to transparent
    private LinearGradient halfToBottmGradient;

    private final int VISIBLE_ITEMS_INVIEW = 16;

    public RotatingSeekbar(Context context) {
        super(context);
        init(context,null);
    }

    public RotatingSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public RotatingSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs)
    {
        handler = new UiHandler();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(textColor);
        paint.setStyle(Style.FILL);
        paint.setTextAlign(Align.RIGHT);
        textsize = (int) convertDpiToPixel(textsize);
        grandientPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);

        setProgress(currentValue, false);
    }

    private void log(String msg)
    {
        boolean debug = true;
        if (debug)
            Log.d(TAG, msg);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        topToHalfGradient = new LinearGradient(0, 0, 0, viewHeight/2, Color.TRANSPARENT, Color.BLACK, Shader.TileMode.MIRROR);
        halfToBottmGradient = new LinearGradient(0, viewHeight/2, 0, viewHeight, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.MIRROR);
        //calculates the item height depending on view height and itemscount that should be visible
        itemHeight = viewHeight / VISIBLE_ITEMS_INVIEW;
        //calc how big the view is when all items would be drawn
        allItemsHeight = itemHeight * Values.length + itemHeight;
        /*
         * calc the maximal minmal pos that could drawn,
         * we use as base the center of the view that why it can get < 0
         */
        realMin = -viewHeight /2 - itemHeight /2;
        //calc the maximal pos that could drawn
        realMax = allItemsHeight - viewHeight /2 - itemHeight *2;
        setProgress(currentValue, false);

        handler.setINVALIDATE();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //draw grandient background
        grandientPaint.setShader(topToHalfGradient);
        canvas.drawPaint(grandientPaint);
        grandientPaint.setShader(halfToBottmGradient);
        canvas.drawPaint(grandientPaint);
        paint.setStrokeWidth(10);
        paint.setColor(textColor);
        //draw outlines
        canvas.drawLine(viewWidth -convertDpiToPixel(30), 0, viewWidth,0,paint);
        canvas.drawLine(viewWidth -convertDpiToPixel(30), viewHeight, viewWidth,viewHeight,paint);
        canvas.drawLine(viewWidth, 0, viewWidth,viewHeight,paint);
        paint.setStrokeWidth(2);
        canvas.drawLine(0, convertDpiToPixel(30), 0,viewHeight - convertDpiToPixel(30),paint);
        paint.setStrokeWidth(10);
        paint.setColor(textColor);
        paint.setTextSize(textsize);
        for(int i = 0; i< Values.length; i++)
        {
            String val = Values[i];

            int dif = currentValue -i;
            if (dif < 0)
                dif *= -1;
            if (dif <= VISIBLE_ITEMS_INVIEW /2) {
                paint.setAlpha(switchalpha(dif));
                paint.setStrokeWidth(1);
                int xpos = i * itemHeight + textsize + currentPosToDraw + itemHeight / 2 - textsize / 2;
                canvas.drawLine(viewWidth - convertDpiToPixel(20), xpos - textsize / 2, viewWidth - 20, xpos - textsize / 2, paint);
                if (null != val)
                    canvas.drawText(val, viewWidth / 2 + convertDpiToPixel(10), xpos, paint);
            }
        }
        paint.setAlpha(255);
        paint.setStrokeWidth(10);
        canvas.drawLine(viewWidth - convertDpiToPixel(10), viewHeight / 2 + itemHeight / 2, viewWidth, viewHeight / 2 + itemHeight / 2, paint);
    }

    private int switchalpha(int pos)
    {
        switch (pos)
        {
            case 8:
                return 0;
            case 7: return 31;
            case 6: return 62;
            case 5: return 93;
            case 4: return 124;
            case 3: return 155;
            case 2: return 186;
            case 1: return 217;
            case 0: return 255;
            default:return 0;
        }
    }

    private float convertDpiToPixel(int dpi)
    {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpi, getResources().getDisplayMetrics());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean throwevent = false;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                startY = (int)event.getY();
                autoscroll = false;
                throwevent = true;
                break;
            case MotionEvent.ACTION_MOVE:
                int disy = 0;
                if (!sliderMoving)
                {
                    disy = getSignedDistance(startY, (int) event.getY());
                    if (disy > 20 || disy < -20) {
                        sliderMoving = true;
                        if (mListener != null)
                            mListener.onStartTrackingTouch(null);
                    }
                }
                if (sliderMoving)
                {
                    distanceInPixelFromLastSwipe = getSignedDistance(startY, (int) event.getY());
                    int newpos = currentPosToDraw - distanceInPixelFromLastSwipe;
                    int positivepos = newpos *-1;

                    if (positivepos < realMax && positivepos > realMin)
                    {
                        currentPosToDraw = newpos;
                        checkifCurrentValueHasChanged();
                        startY = (int) event.getY();
                    }
                }
                throwevent = sliderMoving;
                break;
            case MotionEvent.ACTION_UP:
                if (sliderMoving)
                {
                    sliderMoving = false;
                    if (mListener != null)
                        mListener.onStopTrackingTouch(null);
                    throwevent = false;
                    if (distanceInPixelFromLastSwipe > 0 && distanceInPixelFromLastSwipe > 10 || distanceInPixelFromLastSwipe < 0 && distanceInPixelFromLastSwipe <-10)
                    {
                        autoscroll = true;
                        handleAutoScroll();
                    }
                    else
                    {
                        setProgress(currentValue,true);
                    }
                }
                break;
        }
        handler.setINVALIDATE();
        return throwevent;
    }

    private void handleAutoScroll()
    {
        handler.setONHANDELAUTOSCROLL();
    }

    private void checkifCurrentValueHasChanged() {
        int item = (currentPosToDraw + realMin) / itemHeight;
        if (item < 0)
            item *= -1;
        if (item != currentValue)
        {
            Log.d("RotatingSeekbar", "currentpos" + currentPosToDraw + "item " + item);
            currentValue = item;
            if (currentValue >= Values.length)
                currentValue = Values.length-1;
            if (currentValue < 0)
                currentValue = 0;
            handler.setONPROGRESSCHANGED(currentValue);
        }
    }

    private int getSignedDistance(int start, int current)
    {
        return start -current;
    }

    public int getProgress()
    {
        return currentValue;
    }

    public void setProgress(int progress, boolean throwevent)
    {
        //int item = ((currentPosToDraw + realMin) /itemHeight) *1;
        currentValue = progress;
        Log.d("RotatingSeekbar", "setprogres" +progress);
        currentPosToDraw = (progress * itemHeight + itemHeight /2 + realMin) * -1;
        handler.setINVALIDATE();
        if (throwevent)
            handler.setONPROGRESSCHANGED(currentValue);

    }
    public String GetCurrentString()
    {
        return Values[currentValue];
    }

    public void SetStringValues(String[] ar)
    {
        Values = ar;
        itemHeight = viewHeight /16;
        allItemsHeight = itemHeight * Values.length + itemHeight;
        realMin = -viewHeight /2 - itemHeight /2;
        realMax = allItemsHeight - viewHeight /2;
        handler.setINVALIDATE();
    }
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener)
    {
        this.mListener = mListener;
    }

    public boolean IsAutoScrolling()
    {
        return autoscroll;
    }

    public boolean IsMoving()
    {
        return sliderMoving;
    }
}
