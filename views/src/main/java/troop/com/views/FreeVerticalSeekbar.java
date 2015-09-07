package troop.com.views;

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

/**
 * Created by troop on 06.09.2015.
 */
public class FreeVerticalSeekbar extends View
{
    //the activity context
    private Context context;
    //the minmal value of the slider
    private int min;
    //the max value of the slider
    private int max;
    //the current value of the slider
    private int currentValue;
    //Paint object for drawing
    private Paint paint;
    // size of one value in pixel
    int pixelProValue;
    //current position in pixel of the current slider value
    private int currentValuePixelPos;
    //the sliderimage stored in memory
    private Drawable sliderImage;
    //area to draw the sliderImage
    private Rect drawPosition;
    boolean sliderMoving = false;

    SeekBar.OnSeekBarChangeListener mListener;



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

    private void init(Context context, AttributeSet attrs)
    {
        this.context = context;
        min = 0;
        max = 100;
        currentValue = 50;
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
        if (sliderImage != null)
        {
            paint.setColor(Color.WHITE);
            canvas.drawLine((getWidth() / 4) - 1, getTop(), (getWidth() / 4) + 1, getBottom(), paint);
            paint.setColor(Color.BLACK);
            canvas.drawLine((getWidth()/4), getTop(), (getWidth()/4)+2, getBottom(), paint);
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
        pixelProValue = (getheight()-getWidth())  / max;
        currentValuePixelPos = currentValue * pixelProValue;
        int half = getWidth() / 2;
        Rect tmp = new Rect(0, currentValuePixelPos, half, half + currentValuePixelPos);
        drawPosition = tmp;
        return tmp;
    }

    private int getValueFromDrawingPos(int posi)
    {
        int val;
        int i = (getheight()- getWidth()/2)/max;
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
        int r = val;
        if (r >= 0 && r <= getheight()-getWidth()/2)
        {

            currentValuePixelPos = r;
            int half = getWidth()/2;
            Rect tmp = new Rect(0, currentValuePixelPos , half, half +currentValuePixelPos);

            drawPosition = tmp;


        }
    }


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
                    if (mListener != null)
                        mListener.onStartTrackingTouch(null);
                }
                throwevent = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (sliderMoving)
                {
                    setNewDrawingPos((int) event.getY());

                    invalidate();
                    throwevent = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (sliderMoving) {
                    sliderMoving = false;
                    if (mListener != null)
                        mListener.onStopTrackingTouch(null);
                }
                throwevent = false;
                break;
        }
        return throwevent;
    }

    public void setProgress(int progress)
    {
        if (progress <= max && progress >= min)
        {
            currentValue = progress;

            getPosToDraw();
            invalidate();
        }
    }

    public void setMax(int max)
    {
        this.max = max;
        if (currentValue > max)
            currentValue = max;

        getPosToDraw();
        invalidate();
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener mListener)
    {
        this.mListener = mListener;
    }
}
