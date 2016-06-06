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

package com.freedcam.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import com.troop.freedcam.R;

/**
 * Created by troop on 06.09.2015.
 */
public class FreeVerticalSeekbar extends View
{
    //the minmal value of the slider
    private int min = 0;
    //the max value of the slider
    private int max = 100;
    //the current value of the slider
    private int currentValue = 50;
    //Paint object for drawing
    private Paint paint;
    // size of one value in pixel
    private float pixelProValue;
    //current position in pixel of the current slider value
    private float currentValuePixelPos;
    //the sliderimage stored in memory
    private Drawable sliderImage;
    //area to draw the sliderImage
    private Rect drawPosition;
    //touch area is bigger then the drawPositonarea.
    private Rect touchArea;
    private boolean sliderMoving = false;

    private SeekBar.OnSeekBarChangeListener mListener;

    private int viewWidth =0;
    private int viewHeight = 0;

    public FreeVerticalSeekbar(Context context) {
        super(context);
        init(context, null);
    }

    public FreeVerticalSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FreeVerticalSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewWidth = w;
        this.viewHeight = h;
        pixelProValue = (viewHeight-viewWidth)  / max;
    }

    private void init(Context context, AttributeSet attrs)
    {
        Context context1 = context;
        paint = new Paint();
        paint.setAntiAlias(true);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if (sliderImage == null)
        {
            sliderImage = getResources().getDrawable(R.drawable.slider);// Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), picID), this.getHeight(), this.getHeight(), false);
            getPosToDraw();
        }
        if (sliderImage != null && drawPosition != null)
        {
            paint.setColor(Color.WHITE);
            canvas.drawLine((viewWidth / 2) - 1, 0, (viewWidth / 2) + 1, viewHeight, paint);
            paint.setColor(Color.BLACK);
            canvas.drawLine((viewWidth/2), 0, (viewWidth/2)+2, viewHeight, paint);
            sliderImage.setBounds(drawPosition.left, drawPosition.top, drawPosition.right, drawPosition.bottom);
            sliderImage.draw(canvas);
        }
    }

    private int getheight()
    {
        return getBottom() - getTop();
    }

    private Rect getPosToDraw()
    {
        if (max == 0)
            return null;

        currentValuePixelPos = currentValue * pixelProValue;
        int half = viewWidth / 2;
        Rect tmp = new Rect(half/2, (int)currentValuePixelPos, half/2+half, half + (int)currentValuePixelPos);
        drawPosition = tmp;
        touchArea = new Rect(0, (int)currentValuePixelPos - 10, viewWidth, viewWidth + (int)currentValuePixelPos +10);
        return tmp;
    }

    private int getValueFromDrawingPos(int posi)
    {
        int val;
        int i = (viewHeight- viewWidth/2)/max;
        if (i == 0)
            i=1;
        val = (posi)/i;

        return val;
    }

    private void setNewDrawingPos(int val)
    {
        requestLayout();

        int posval = getValueFromDrawingPos(val);
        if (posval > max || posval <0)
            return;
        if (posval != currentValue)
        {
            currentValue = posval;
            if (mListener != null)
                mListener.onProgressChanged(null,currentValue, true);
        }
        if (val >= 0 && val <= viewHeight-viewWidth/2)
        {

            currentValuePixelPos = val;
            int half = viewWidth/2;
            Rect tmp = new Rect(half/2,(int) currentValuePixelPos , half/2+half, half +(int)currentValuePixelPos);
            touchArea = new Rect(0, (int)currentValuePixelPos - 10, viewWidth, viewWidth + (int)currentValuePixelPos +10);
            drawPosition = tmp;


        }
    }

    private int startY;
    private int startX;
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean throwevent = false;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if (touchArea == null)
                    break;
                if (touchArea.contains((int)event.getX(), (int)event.getY()))
                {
                    startY = (int)event.getY();
                    startX = (int)event.getX();
                    throwevent = true;
                }
                else throwevent =false;

                break;
            case MotionEvent.ACTION_MOVE:
                if (!sliderMoving)
                {
                    int disx = getDistance(startX, (int)event.getX());
                    int disy = getDistance(startY, (int)event.getY());
                    if (disy > disx && disy > 40) {
                        sliderMoving = true;
                        if (mListener != null)
                            mListener.onStartTrackingTouch(null);
                    }
                }
                if (sliderMoving)
                {
                    setNewDrawingPos((int) event.getY());
                    invalidate();
                }
                throwevent =sliderMoving;

                break;
            case MotionEvent.ACTION_UP:
                if (sliderMoving)
                {
                    sliderMoving = false;
                    if (mListener != null)
                        mListener.onStopTrackingTouch(null);
                }
                /*else
                {
                    if (mListener != null)
                        mListener.onStartTrackingTouch(null);
                    setNewDrawingPos((int) event.getY());
                    invalidate();
                    if (mListener != null)
                        mListener.onStopTrackingTouch(null);
                }*/
                throwevent = false;
                break;
        }
        return throwevent;
    }

    private static int getDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        if (dis < 0)
            dis = dis *-1;
        return dis;
    }

    public void setProgress(int progress)
    {
        if (progress <= max && progress >= min)
        {
            currentValue = progress;

            getPosToDraw();
            this.post(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });

        }
    }

    public void setMax(int max)
    {
        this.max = max;
        if (currentValue > max)
            currentValue = max-1;

        getPosToDraw();
        invalidate();
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener mListener)
    {
        this.mListener = mListener;
    }

    public int getProgress(){ return currentValue;}

}
