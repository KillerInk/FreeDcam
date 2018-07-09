package freed.cam.apis.sonyremote.parameters;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterEvents;

/**
 * Created by troop on 12.12.2016.
 */

public class JoyPad extends View implements ParameterEvents
{
    private Paint backgroundDrawer;
    private Paint joypadDrawer;
    private int joypad_posX;
    private int joypad_posY;

    private NavigationClick navigationClickListner;


    @Override
    public void onViewStateChanged(AbstractParameter.ViewState value) {

    }

    @Override
    public void onIntValueChanged(int current) {
        if (current > 1)
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);
    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onStringValueChanged(String value) {

    }

    public interface NavigationClick
    {
        void onMove(int x,int y);
        void onDown();
        void onUp();
    }

    public JoyPad(Context context) {
        super(context);
        init();
    }

    public JoyPad(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init()
    {
        this.setClickable(true);
        this.setFocusable(true);
        this.setEnabled(true);
        this.setActivated(true);
        this.bringToFront();
        this.setFocusableInTouchMode(true);
        backgroundDrawer = new Paint();
        backgroundDrawer.setAntiAlias(true);
        backgroundDrawer.setStyle(Paint.Style.FILL);
        backgroundDrawer.setColor(Color.WHITE);

        joypadDrawer = new Paint();
        joypadDrawer.setAntiAlias(true);
        joypadDrawer.setStyle(Paint.Style.FILL);
        joypadDrawer.setColor(Color.BLACK);

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth()/2,getHeight()/2, getWidth()/2,backgroundDrawer);
        if (joypad_posX == 0 && joypad_posY == 0)
        {
            joypad_posX = getWidth()/2;
            joypad_posY = getHeight()/2;
        }
        canvas.drawCircle(joypad_posX, joypad_posY, getWidth()/4, joypadDrawer);
    }

    public void setNavigationClickListner(NavigationClick navigationClickListner)
    {
        this.navigationClickListner =navigationClickListner;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                joypad_posX = getWidth()/2;
                joypad_posY = getHeight()/2;
                if (navigationClickListner != null)
                    navigationClickListner.onDown();
                break;
            case MotionEvent.ACTION_MOVE:
                joypad_posX = (int)event.getX();
                if (joypad_posX < getWidth()/4)
                {
                    joypad_posX = getWidth()/4;
                }
                else if (joypad_posX > getWidth()- (getWidth()/4))
                {
                    joypad_posX = getWidth()-(getWidth()/4);
                }
                joypad_posY = (int)event.getY();
                if (joypad_posY < getHeight()/4)
                {
                    joypad_posY = getHeight()/4;
                }
                else if (joypad_posY > getHeight()- (getHeight()/4))
                {
                    joypad_posY = getHeight()-(getHeight()/4);
                }
                if (navigationClickListner != null)
                {
                    int x = 0,y = 0;
                    if (joypad_posX > getWidth()/2)
                        x = 1;
                    else if (joypad_posX < getWidth()/2)
                        x = -1;
                    if (joypad_posY > getHeight()/2)
                        y = 1;
                    else if (joypad_posX < getHeight()/2)
                        y = -1;
                    navigationClickListner.onMove(x,y);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                joypad_posX = getWidth()/2;
                joypad_posY = getHeight()/2;
                invalidate();
                if (navigationClickListner != null)
                    navigationClickListner.onUp();
                break;
        }
        return true;
    }
}
