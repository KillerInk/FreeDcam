package troop.com.themesample.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by Ingo on 24.07.2015.
 */
public class VerticalSeekBar extends SeekBar
{
    private OnSeekBarChangeListener listner;
    boolean fromUser = false;
    int lastcurrent = 0;

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener mListener){
        this.listner =mListener;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        if (listner != null)
            listner.onProgressChanged(this, progress, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fromUser = true;
                if (listner != null)
                    listner.onStartTrackingTouch(this);
                break;
            case MotionEvent.ACTION_MOVE:
                int i=getMax() - (int) (getMax() * event.getY() / getHeight());
                /*if (event.getY() >= getItemPos() + itemlength()/2)
                    i++;
                if (event.getY() < getItemPos() + itemlength()/2)
                    i--;*/

                if (i != lastcurrent)
                {
                    if(i<0)
                        i=0;
                    setProgress(i);
                    listner.onProgressChanged(this, i, true);
                    lastcurrent = i;
                }

                //Log.i("Progress", getProgress() + "");
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;
            case MotionEvent.ACTION_UP:
                fromUser = false;
                if (listner != null)
                    listner.onStopTrackingTouch(this);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    private int itemlength()
    {
        return getHeight()/getMax();
    }

    private int getItemPos()
    {
        return itemlength() * getProgress();
    }
}
