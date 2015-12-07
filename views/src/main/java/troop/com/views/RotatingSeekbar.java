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
    private String[] Values = {"1","2","3","4","5","6","7","8","9","10" };
    private int currentValue = 0;
    private Paint paint;
    private int viewWidth =0;
    private int viewHeight = 0;
    private int itemHeight = 0;
    private int allItemsHeight = 0;
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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewWidth = w;
        this.viewHeight = h;
        this.itemHeight = viewHeight /7;
        this.allItemsHeight = itemHeight * Values.length;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        paint.setColor(Color.BLUE);
        paint.setTextSize(textsize);
        int maxvisible = currentPosToDraw + viewHeight;
        int minvisible = currentPosToDraw;
        for(int i = 0; i< Values.length; i++)
        {
                int pos = i*itemHeight+ textsize +currentPosToDraw;
                canvas.drawText(Values[i], 0, pos, paint);
        }

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
                    int max = viewHeight/2 + itemHeight *2;
                    int min =- viewHeight/2;
                    if (positivepos < max && positivepos > min)
                    {
                        currentPosToDraw = newpos;
                        startY = (int) event.getY();
                        Log.d("RotatingSeekbar", "currentpos" + currentPosToDraw);
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
                }
                break;
        }
        return throwevent;
    }

    private int getSignedDistance(int start, int current)
    {
        return start -current;
    }

    public int GetCurrentPosition()
    {
        return currentValue;
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
