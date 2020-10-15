/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.ui;

import android.content.res.Resources;
import android.os.Handler;
import android.view.MotionEvent;

import com.troop.freedcam.utils.Log;

/**
 * Created by troop on 02.09.2014.
 */
public class TouchHandler
{
    private final String TAG = TouchHandler.class.getSimpleName();

    private void L(String log)
    {
        boolean DEBUG = false;
        if (DEBUG)
            Log.d(TAG, log);
    }

    private int startX;
    private int startY;
    private int currentX;
    private int currentY;
    private boolean swipeDetected;
    private boolean newActionBlocked;
    private final int blockTime = 500;
    private final Handler handler;


    TouchHandler()
    {
        handler = new Handler();
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        boolean fireagain = true;

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                //action down resets all already set values and get the new one from the event
                startX = (int) event.getX();
                startY = (int) event.getY();
                //reset swipeDetected to false
                swipeDetected = false;
                L("ACTION_DOWN currentX:" + currentX + " X:" + startX);
                break;
            case MotionEvent.ACTION_MOVE:
                //in case action down never happend
                if (startX == 0 && startY == 0)
                {
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    //reset swipeDetected to false
                    swipeDetected = false;
                }
                currentX = (int) event.getX();
                currentY = (int) event.getY();
                //detect swipeDetected. if swipeDetected detected return false else true
                fireagain = detectSwipeDirection();
                L("ACTION_MOVE Swipedetected:"+ swipeDetected);
                break;
            case MotionEvent.ACTION_UP:
                L("ACTION_UP Swipedetected:"+ swipeDetected);
                //in case no swipeDetected happen swipeDetected is false and it was a click
                if (!swipeDetected && !newActionBlocked)
                {
                    L("On Click happen");
                    OnClick((int) event.getX(), (int) event.getY());
                    newActionBlocked = true;
                    handler.postDelayed(resetActionBlock, blockTime);
                }
                swipeDetected = false;
                fireagain = false;
                break;
        }
        OnMotionEvent(event);

        return fireagain;
    }

    private boolean detectSwipeDirection()
    {
        //if last swipeDetected is less then 500 ms it blocked

        if (swipeDetected || newActionBlocked)
            return false;
        float x = getDistance(startX, currentX);
        float y = getDistance(startY, currentY);
        //if we have a swipeDetected
        int distance = 90;
        if (x >= distance || y >= distance)
        {
            //its a swipeDetected
            swipeDetected = true;
            Log.d("TouchHAndler", "currentX:" + currentX + " X:" + startX);
            if (x >= y)
            {
                if (currentX > startX)
                    doLeftToRightSwipe();
                else
                    doRightToLeftSwipe();
            }
            else{
                if (currentY > startY)
                    doTopToBottomSwipe();
                else
                    doBottomToTopSwipe();
            }
            newActionBlocked = true;
            handler.postDelayed(resetActionBlock, blockTime);
            return false;
        }
        return false;
    }

    private final Runnable resetActionBlock = new Runnable() {
        @Override
        public void run() {
            newActionBlocked = false;
        }
    };

    void doLeftToRightSwipe()
    {
    }

    void doRightToLeftSwipe()
    {
    }

    void doTopToBottomSwipe()
    {
    }

    void doBottomToTopSwipe()
    {
    }

    void OnClick(int x, int y)
    {

    }

    void OnMotionEvent(MotionEvent event)
    {

    }

    private static float getDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        if (dis < 0)
            dis = dis *-1;
        float density = Resources.getSystem().getDisplayMetrics().density;
        return dis / density;
    }

    public static float getNegDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        float density = Resources.getSystem().getDisplayMetrics().density;
        return dis / density;
    }


}
