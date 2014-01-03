package com.troop.freecam.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.troop.freecam.R;

/**
 * Created by troop on 23.12.13.
 */
public class ExtendedButton extends View
{
    String topString;
    String midString;
    String botString;
    boolean drawLong = false;
    Paint paint;

    public ExtendedButton(Context context) {
        super(context);
    }

    public ExtendedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public ExtendedButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, context);
    }

    private void init(AttributeSet attrs, Context context)
    {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExtendedButton,
                0, 0);
        topString = a.getString(R.styleable.ExtendedButton_StringTop);
        botString = a.getString(R.styleable.ExtendedButton_StringBot);
        drawLong = a.getBoolean(R.styleable.ExtendedButton_drawLong, false);
        a.recycle();
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(getHeight() - 4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        //getBackground().draw(canvas);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        if (!drawLong)
        {
            int height = getHeight()/3;

            if (topString != null)
                draw(canvas, topString, height, paint, 1);
            if (midString != null)
            {
                drawMidString(canvas, midString, height, paint, 2);
            }
            if (botString != null)
                draw(canvas, botString, height, paint, 3);
        }
        else
        {
            int height = getHeight() - 4;
            String draw = topString + " " + botString + ": ";
            paint.setTextSize(height);
            requestLayout();
            int length = (int) paint.measureText(draw);
            canvas.drawText(draw, 2, height, paint);
            paint.setColor(Color.RED);
            if (midString != null)
            {
                canvas.drawText(midString, 2 + length, height,paint);
            }

        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

// Set the dimension to the smaller of the 2 measures
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;

// Create the new specs
        int widthSpec = MeasureSpec.makeMeasureSpec(measureWidth(widthMeasureSpec), widthMode);
        //int heightSpec = MeasureSpec.makeMeasureSpec(size, heightMode);
        super.onMeasure(widthSpec, heightMeasureSpec);
        //int width = MeasureSpec.makeMeasureSpec((int)(), MeasureSpec.EXACTLY);
        //setMeasuredDimension(measureWidth(widthMeasureSpec), heightMeasureSpec);
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            result = (int) paint.measureText(getStringFromAll()) + getPaddingLeft()
                    + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    private int getMatchingTextSize(Paint paint, int height, String _string)
    {
        paint.setTextSize(height);
        int count = height;
        int length = (int)paint.measureText(_string);
        if (length + 8 > getWidth())
        {
            int dif = length + 8 - getWidth();
            for (int i = 0; i < dif; i++)
            {
                count--;
                paint.setTextSize(count);
                length = (int)paint.measureText(_string);
                if (length + 8 <= getWidth())
                    break;
            }
        }
        return length;
    }

    private void draw(Canvas canvas,String string, int height, Paint paint, int count)
    {
        int length = getMatchingTextSize(paint, height, string);
        int dif = getWidth() - length;
        paint.setColor(Color.BLACK);
        canvas.drawText(string, dif/2 + 2, (height - 1 *count)* count, paint);
        paint.setColor(Color.WHITE);
        canvas.drawText(string, dif/2, (height - 1* count) * count, paint);
    }

    private void drawMidString(Canvas canvas,String string, int height, Paint paint, int count)
    {
        int length = getMatchingTextSize(paint, height, string);
        int dif = getWidth() - length;
        paint.setColor(Color.BLACK);
        canvas.drawText(string, dif/2 + 2, (height - 1 *count)* count, paint);
        paint.setColor(Color.RED);
        canvas.drawText(string, dif/2, (height - 1* count) * count, paint);
    }

    public void SetValue(String value)
    {
        midString = value;

        invalidate();
    }


    private String getStringFromAll()
    {
        String draw = topString + " " + botString + ": ";
        if (midString != null)
        {
            draw += midString;
        }
        return draw;
    }
}
