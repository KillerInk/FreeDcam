package com.troop.freedcam.ui;

/**
 * Created by troop on 09.06.2015.
 */

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.troop.freedcam.i_camera.FocusRect;

/**
 * This class handles touch events that happens to the attached imageview and moves them
 * and return the values when its moving or a click happen
 */
public class ImageViewTouchAreaHandler implements View.OnTouchListener
{

    public interface I_TouchListnerEvent
    {
        void onAreaCHanged(FocusRect imageRect, int previewWidth, int previewHeight);
        void OnAreaClick(int x, int y);
    }

    /**
     *
     * @param imageView the view that should get moved
     */
    public ImageViewTouchAreaHandler(ImageView imageView, I_Activity i_activity, I_TouchListnerEvent touchListnerEvent)
    {
        this.imageView = imageView;
        this.recthalf = imageView.getWidth()/2;
        this.i_activity = i_activity;
        this.touchListnerEvent = touchListnerEvent;
        this.allowDrag = true;
    }
    private I_TouchListnerEvent touchListnerEvent;
    private I_Activity i_activity;
    private ImageView imageView;
    private float x;
    private float y;
    private float difx;
    private float dify;

    /**
     * if set to true the imageview is dragable
     */
    private boolean allowDrag = false;
    /**
     * distance in pixel? to move bevor it gets detected as move
     */
    private int distance = 10;
    /**
     * the start values that gets set on action down
     */
    private int startX;
    private int startY;
    /**
     * holdes the time when last action down happend
     */
    private long start;
    private long duration;
    private static final int MAX_DURATION = 3500;
    /*
    the area where the imageview is on screen
     */
    private FocusRect imageRect;
    /**
     * true if a move was detected
     */
    private boolean moving = false;
    /**
     * half size from the imageview to calculate the center postion
     */
    private int recthalf;

    private int getDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        if (dis < 0)
            dis = dis *-1;
        return dis;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                x = event.getX();
                y = event.getY();
                startX = (int)event.getX() - (int)imageView.getX();
                startY =(int) event.getY() - (int)imageView.getY();
                start = System.currentTimeMillis();

            }
            break;
            case MotionEvent.ACTION_MOVE:
            {

                difx = x - imageView.getX();
                dify = y - imageView.getY();
                int xd = getDistance(startX, (int)difx);
                int yd = getDistance(startY, (int)dify);

                if (allowDrag) {
                    if (event.getX() - difx > i_activity.GetPreviewLeftMargine() && event.getX() - difx + imageView.getWidth() < i_activity.GetPreviewLeftMargine() + i_activity.GetPreviewWidth())
                        imageView.setX(event.getX() - difx);
                    if (event.getY() - dify > i_activity.GetPreviewTopMargine() && event.getY() - dify + imageView.getHeight() < i_activity.GetPreviewTopMargine() + i_activity.GetPreviewHeight())
                        imageView.setY(event.getY() - dify);
                    if (xd >= distance || yd >= distance) {

                        moving = true;
                    }
                }
            }
            break;
            case MotionEvent.ACTION_UP:
            {
                long time = System.currentTimeMillis() - start;
                duration = duration+time;

                if (moving)
                {
                    moving = false;
                    x = 0;
                    y = 0;
                    difx = 0;
                    dify = 0;
                    recthalf = (int)imageView.getWidth()/2;
                    imageRect = new FocusRect((int) imageView.getX() - recthalf, (int) imageView.getX() + recthalf, (int) imageView.getY() - recthalf, (int) imageView.getY() + recthalf);
                    if (touchListnerEvent != null)
                        touchListnerEvent.onAreaCHanged(imageRect, i_activity.GetPreviewWidth(), i_activity.GetPreviewHeight());
                }
                else
                {
                    touchListnerEvent.OnAreaClick(((int)imageView.getX()+ (int)event.getX()),((int)imageView.getY() + (int)event.getY()));
                }

                if (duration >= MAX_DURATION) {
                    System.out.println("Long Press Time: " + duration);
                    //George Was Here On a tuesday lol
                    System.out.println("Insert AE Code here: ");

                }
            }
        }
        return true;
    }

}
