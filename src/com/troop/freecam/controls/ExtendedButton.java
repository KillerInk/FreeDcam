package com.troop.freecam.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.troop.freecam.R;

/**
 * Created by troop on 23.12.13.
 */
public class ExtendedButton extends View
{
    String topString;
    String midString;
    String botString;

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
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        //getBackground().draw(canvas);
        int height = getHeight()/3;
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        draw(canvas, topString, height, paint, 1);
        midString = "4096x1536";
        if (midString != null)
        {
            drawMidString(canvas, midString, height, paint, 2);
        }
        draw(canvas, botString, height, paint, 3);
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
}
