package com.troop.freedcam.ui;

/**
 * Created by troop on 09.06.2015.
 */

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.FocusRect;

/**
 * This class handles touch events that happens to the attached imageview and moves them
 * and return the values when its moving or a click happen
 */
public class ImageViewTouchAreaHandler implements View.OnTouchListener
{
    AbstractCameraUiWrapper cameraUiWrapper;
    I_TouchListnerEvent touchListnerEvent;
    ImageView imageView;
    float x, y, difx, dify;
    private Handler longClickHandler;

    public interface I_TouchListnerEvent
    {
        void onAreaCHanged(FocusRect imageRect, int previewWidth, int previewHeight);
        void OnAreaClick(int x, int y);
        void OnAreaLongClick(int x, int y);
        void IsMoving(boolean moving);
    }

    /**
     *
     * @param imageView the view that should get moved
     */
    public ImageViewTouchAreaHandler(ImageView imageView,AbstractCameraUiWrapper cameraUiWrapper, I_TouchListnerEvent touchListnerEvent, boolean allowDrag)
    {
        this.imageView = imageView;
        this.cameraUiWrapper = cameraUiWrapper;
        this.recthalf = imageView.getWidth()/2;
        this.touchListnerEvent = touchListnerEvent;
        this.allowDrag = allowDrag;
        longClickHandler = new Handler();
    }


    /**
     * if set to true the imageview is dragable
     */
    boolean allowDrag = false;
    /**
     * distance in pixel? to move bevor it gets detected as move
     */
    final int distance = 20;
    /**
     * the start values that gets set on action down
     */
    int startX, startY;
    /**
     * holdes the time when last action down happend
     */
    long start;
    static final int MAX_DURATION = 1000;
    /*
    the area where the imageview is on screen
     */
    FocusRect imageRect;
    /**
     * true if a move was detected
     */
    boolean moving = false;
    /**
     * half size from the imageview to calculate the center postion
     */
    int recthalf;

    private int getDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        if (dis < 0)
            dis = dis *-1;
        return dis;
    }

    private boolean longClickHappen = false;

    @Override
    public boolean onTouch(View v,final MotionEvent event)
    {
        boolean ret = true;
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                x = event.getX();
                y = event.getY();
                startX = (int)event.getX() - (int)imageView.getX();
                startY =(int) event.getY() - (int)imageView.getY();
                start = System.currentTimeMillis();
                longClickHandler.postDelayed(longClickRunnable,MAX_DURATION);
                longClickHappen = false;
            }
            break;
            case MotionEvent.ACTION_MOVE:
            {

                difx = x - imageView.getX();
                dify = y - imageView.getY();
                int xd = getDistance(startX, (int)difx);
                int yd = getDistance(startY, (int)dify);

                if (allowDrag) {
                    if (event.getX() - difx > cameraUiWrapper.getMargineLeft() && event.getX() - difx + imageView.getWidth() < cameraUiWrapper.getMargineLeft() + cameraUiWrapper.getPreviewWidth())
                        imageView.setX(event.getX() - difx);
                    if (event.getY() - dify > cameraUiWrapper.getMargineTop() && event.getY() - dify + imageView.getHeight() < cameraUiWrapper.getMargineTop() + cameraUiWrapper.getPreviewHeight())
                        imageView.setY(event.getY() - dify);
                    if ((xd >= distance || yd >= distance) && !moving) {
                        moving = true;
                        longClickHandler.removeCallbacks(longClickRunnable);
                        ret = false;
                        if (touchListnerEvent != null)
                            touchListnerEvent.IsMoving(true);
                    }
                }

            }
            break;
            case MotionEvent.ACTION_UP:
            {


                if (moving)
                {
                    longClickHandler.removeCallbacks(longClickRunnable);
                    moving = false;
                    x = 0;
                    y = 0;
                    difx = 0;
                    dify = 0;
                    recthalf = (int)imageView.getWidth()/2;
                    imageRect = new FocusRect((int) imageView.getX() - recthalf, (int) imageView.getX() + recthalf, (int) imageView.getY() - recthalf, (int) imageView.getY() + recthalf,(int)imageView.getX(),(int)imageView.getY());
                    if (touchListnerEvent != null) {
                        touchListnerEvent.onAreaCHanged(imageRect, cameraUiWrapper.getPreviewWidth(), cameraUiWrapper.getPreviewHeight());
                            touchListnerEvent.IsMoving(false);
                    }
                }
                else
                {
                    if (!longClickHappen)
                    {
                        longClickHandler.removeCallbacks(longClickRunnable);
                        touchListnerEvent.OnAreaClick(((int) imageView.getX() + (int) event.getX()), ((int) imageView.getY() + (int) event.getY()));
                    }
                }
                ret = false;

            }
            break;
        }
        return ret;
    }

    Runnable longClickRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            longClickHappen = true;
            touchListnerEvent.OnAreaLongClick((int) imageView.getX(), (int) imageView.getY());
        }
    };
}
