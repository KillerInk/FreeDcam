package freed.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.troop.freedcam.R;

import dagger.hilt.processor.internal.definecomponent.codegen._dagger_hilt_android_components_ActivityRetainedComponent;


public class StrokedTextView extends androidx.appcompat.widget.AppCompatTextView {

    private int textColor = Color.WHITE;
    private int strokeColor = Color.BLACK;
    private boolean internalInvalidate = false;

    private int strokeSize =1;
    public StrokedTextView(Context context) {
        super(context);
        setTypeface(ResourcesCompat.getFont(context,R.font.freedcam));
    }

    public StrokedTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTypeface(ResourcesCompat.getFont(context,R.font.freedcam));
        setAttrs(context,attrs);
    }

    public StrokedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(ResourcesCompat.getFont(context,R.font.freedcam));
        setAttrs(context,attrs);
    }

    private void setAttrs(Context context, AttributeSet attrs)
    {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.StrokedTextView,
                0, 0
        );
        //try to set the attributs
        try
        {
            strokeColor = a.getColor(R.styleable.StrokedTextView_setStrokeColor,Color.BLACK);
            strokeSize = a.getInt(R.styleable.StrokedTextView_setStrokeSize,1);
        }
        finally {
            a.recycle();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        internalInvalidate = true;
        Paint paint = getPaint();

        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setColor(strokeColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeSize);
        setTextColor(strokeColor);
        super.onDraw(canvas);

        setTextColor(textColor);
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);
        internalInvalidate = false;
    }


    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
    }

    public void setStrokeSize(int strokeSize) {
        this.strokeSize = strokeSize;
        invalidate();
    }


    @Override
    public void setTextColor(int color)
    {
        if (!internalInvalidate) {
            this.textColor = color;
        }
        super.setTextColor(color);
    }


    @Override
    public void invalidate() {
        if (!internalInvalidate)
            super.invalidate();
    }
}
