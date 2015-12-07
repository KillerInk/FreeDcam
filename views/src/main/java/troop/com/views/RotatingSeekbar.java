package troop.com.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

/**
 * Created by troop on 07.12.2015.
 */
public class RotatingSeekbar extends View
{
    private String[] Values = "Auto,1/6000,1/4000,1/2000,1/1000,1/500,1/250,1/125,1/60,1/30,1/15,1/8,1/4,1/2,2,4,8,15,30,60,180".split(",");
    private int currentValue = 3;
    private Paint paint;
    private int viewWidth =0;
    private int viewHeight = 0;
    private int itemHeight = 0;
    private int allItemsHeight = 0;
    private int realMin = 0;
    private int realMax = 0;
    private int currentPosToDraw = 0;
    private Context context;
    private SeekBar.OnSeekBarChangeListener mListener;
    private int textsize = 40;
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
        this.context = context;
        paint = new Paint();
        paint.setAntiAlias(true);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        setProgress(currentValue);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewWidth = w;
        this.viewHeight = h;
        this.itemHeight = viewHeight /8;
        this.allItemsHeight = itemHeight * Values.length + itemHeight;
        realMin = -viewHeight/2 -itemHeight/2;
        realMax = allItemsHeight - viewHeight/2 - itemHeight*2;
        setProgress(currentValue);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        paint.setColor(Color.BLUE);
        paint.setTextSize(textsize);
        for(int i = 0; i< Values.length; i++)
        {
            canvas.drawLine(5,i*itemHeight+currentPosToDraw, viewWidth -5, i*itemHeight +currentPosToDraw, paint);
            int pos = i*itemHeight+ textsize +currentPosToDraw + (itemHeight/2 - textsize/2);
            canvas.drawText(Values[i], 0, pos, paint);
        }
        canvas.drawLine(viewWidth - 60,viewHeight/2 + itemHeight/2, viewWidth, viewHeight/2 + itemHeight/2,paint);

    }

    //holds the position when user touched down
    private int startY;
    private boolean sliderMoving = false;
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        boolean throwevent = false;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                startY = (int)event.getY();
                throwevent = true;
                break;
            case MotionEvent.ACTION_MOVE:
                int disy = 0;
                if (!sliderMoving)
                {
                    disy = getSignedDistance(startY, (int) event.getY());
                    if (disy > 40 || disy < -40) {
                        sliderMoving = true;
                        if (mListener != null)
                            mListener.onStartTrackingTouch(null);
                    }
                }
                if (sliderMoving)
                {
                    int newpos = currentPosToDraw - getSignedDistance(startY, (int) event.getY());
                    int positivepos = newpos *-1;

                    if (positivepos < realMax && positivepos > realMin)
                    {
                        currentPosToDraw = newpos;
                        int item = ((currentPosToDraw + realMin) /itemHeight);
                        if (item < 0)
                            item *= -1;
                        if (item != currentValue)
                        {
                            Log.d("RotatingSeekbar", "currentpos" + currentPosToDraw + "item " + item);
                            currentValue = item;
                            if (mListener != null)
                                mListener.onProgressChanged(null, currentValue, true);
                        }
                        startY = (int) event.getY();

                    }
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
                    throwevent = false;
                    setProgress(currentValue);
                }
                break;
        }
        return throwevent;
    }

    private int getSignedDistance(int start, int current)
    {
        return start -current;
    }

    public int getProgress()
    {
        return currentValue;
    }

    public void setProgress(int progress)
    {
        //int item = ((currentPosToDraw + realMin) /itemHeight) *1;
        currentValue = progress;
        Log.d("RotatingSeekbar", "setprogres" +progress);
        currentPosToDraw = ((progress *itemHeight + itemHeight/2 ) + realMin) * -1;
        invalidate();
    }
    public String GetCurrentString()
    {
        return Values[currentValue];
    }

    public void SetStringValues(String[] ar)
    {
        this.Values = ar;
        allItemsHeight = itemHeight * Values.length;
    }
    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener mListener)
    {
        this.mListener = mListener;
    }
}
