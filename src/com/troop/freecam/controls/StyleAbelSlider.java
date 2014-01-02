package com.troop.freecam.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.troop.freecam.HDR.BitmapHandler;
import com.troop.freecam.R;
import com.troop.freecam.manager.interfaces.IStyleAbleSliderValueHasChanged;

/**
 * Created by troop on 22.12.13.
 */
public class StyleAbelSlider extends View
{
    //the activity
    Context context;
    //the sliderimage stored in memory
    Drawable sliderImage;
    //the minmal value of the slider
    int min;
    //the max value of the slider
    int max;
    //the current value of the slider
    int current;
    int sliderWidth;
    int sliderHeight;
    Rect drawPosition;
    boolean horizontal;

    int picID;


    public IStyleAbleSliderValueHasChanged valueHasChanged;

    public StyleAbelSlider(Context context) {
        super(context);
        //init(context);
    }

    public StyleAbelSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public StyleAbelSlider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        this.context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StyleAbelSlider,
                0, 0);

        horizontal = a.getBoolean(R.styleable.StyleAbelSlider_horizontal, false);
        picID = a.getResourceId(R.styleable.StyleAbelSlider_SliderImage, R.drawable.icon_shutter_thanos_blast);
        a.recycle();
        min = 0;
        max = 100;
        current = 50;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (sliderImage == null)
        {
            sliderImage = getResources().getDrawable(picID);// Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), picID), this.getHeight(), this.getHeight(), false);
            getPosToDraw();
        }
        if (sliderImage != null)
        {
            sliderImage.setBounds(drawPosition.left, drawPosition.top, drawPosition.right, drawPosition.bottom);
            sliderImage.draw(canvas);
        }
            //canvas.drawBitmap(sliderImage, getPosToDraw().left, getPosToDraw().top, new Paint());
    }

    private Rect getPosToDraw()
    {
        Rect ret;
        if (horizontal)
        {
            int pos = getWidth() / max * current;
            int half = getHeight() / 2;
            ret = new Rect(pos - half, 0, getHeight() + pos - half, getHeight());
        }
        else
        {
            int pos = getHeight() /max *current;
            int half = getWidth() / 2;
            ret = new Rect(0, pos - half , getWidth(), getWidth() + pos - half);
        }
        drawPosition = ret;
        return ret;
    }

    private int getValueFromDrawingPos(int posi)
    {
        int val;
        if (horizontal)
        {
            int i = getWidth()/max;
            val = posi/i;
        }
        else
        {
            int i = getHeight()/max;
            val = posi/i;
        }
        return val;
    }

    public void SetCurrentValue(int pos)
    {
        if (pos <= max && pos >= min)
        {
            current = pos;
            getPosToDraw();
            invalidate();
        }
    }

    public int getCurrent()
    {
        return current;
    }

    public void SetMaxValue(int max)
    {
        this.max = max;
        if (current > max)
            current = max;
        getPosToDraw();
        invalidate();
    }

    private void setNewDrawingPos(int val)
    {
        if (horizontal)
        {
            int half = getHeight() / 2;
            Rect tmp = new Rect(val - half, 0 , getHeight() + val - half, getHeight());
            if (tmp.left >= 0 && tmp.right <= getWidth())
                drawPosition = tmp;
        }
        else
        {
            int half = getWidth() / 2;
            Rect tmp = new Rect(0, val - half , getWidth(), getWidth() + val - half);
            if (tmp.top >= 0 && tmp.bottom <= getHeight())
                drawPosition = tmp;
        }
        if (current != getValueFromDrawingPos(val))
        {
            current = getValueFromDrawingPos(val);
            throwvalueHasChanged(current);
        }
    }

    boolean sliderMoving = false;
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean throwevent = false;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if (drawPosition.contains((int)event.getX(), (int)event.getY()))
                {
                    sliderMoving = true;
                }
                throwevent = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (sliderMoving)
                {
                    if (horizontal)
                    {
                        setNewDrawingPos((int) event.getX());
                    }
                    else
                    {
                        setNewDrawingPos((int) event.getY());
                    }
                    invalidate();
                    throwevent = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (sliderMoving)
                    sliderMoving = false;
                throwevent = false;
                break;
        }
        return throwevent;
    }

    private void throwvalueHasChanged(int value)
    {
        if (valueHasChanged != null)
            valueHasChanged.ValueHasChanged(value);
    }
}
