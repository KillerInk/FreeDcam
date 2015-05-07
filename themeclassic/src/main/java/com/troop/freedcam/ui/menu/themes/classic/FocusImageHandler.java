package com.troop.freedcam.ui.menu.themes.classic;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera2.CameraUiWrapperApi2;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.interfaces.I_Focus;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.R;

/**
 * Created by troop on 02.09.2014.
 */
public class FocusImageHandler extends TouchHandler implements I_Focus
{
    private final I_Activity activity;
    private AbstractCameraUiWrapper wrapper;
    ImageView imageView;
    final int crosshairShowTime = 5000;
    int disHeight;
    int disWidth;
    int recthalf;
    ImageView cancelFocus;
    ImageView meteringArea;
    FocusRect meteringRect;
    View view;
    Fragment fragment;
    long start;
    long duration;
    static final int MAX_DURATION = 3500;


    public FocusImageHandler(View view, Fragment fragment, I_Activity activity)
    {
        this.activity = activity;
        this.view = view;
        this.fragment = fragment;
        imageView = (ImageView)view.findViewById(R.id.imageView_Crosshair);
        recthalf = fragment.getResources().getDimensionPixelSize(R.dimen.crosshairwidth)/2;
        //imageView.setVisibility(View.GONE);
        cancelFocus = (ImageView)view.findViewById(R.id.imageViewFocusClose);
        cancelFocus.setVisibility(View.GONE);
        cancelFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                wrapper.cameraHolder.CancelFocus();
                cancelFocus.setVisibility(View.GONE);
            }
        });

        meteringArea = (ImageView)view.findViewById(R.id.imageView_meteringarea);
        meteringArea.setOnTouchListener(new MeteringAreaTouch());
        meteringArea.setVisibility(View.GONE);

    }

    public void SetCamerUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.wrapper = cameraUiWrapper;
        if(cameraUiWrapper instanceof CameraUiWrapper || cameraUiWrapper instanceof CameraUiWrapperApi2) {
            centerMeteringArea();
            meteringArea.setVisibility(View.VISIBLE);
        }
        else
        {
            meteringArea.setVisibility(View.GONE);
        }
        if (wrapper.Focus != null)
            wrapper.Focus.focusEvent = this;
    }

    @Override
    public void FocusStarted(FocusRect rect)
    {
        if (!(wrapper instanceof CameraUiWrapperSony))
        {
            disWidth = activity.GetPreviewWidth();
            disHeight = activity.GetPreviewHeight();
            int margineleft = activity.GetPreviewLeftMargine();
            //handler.removeCallbacksAndMessages(null);

            if (rect == null)
            {
                int halfwidth = disWidth / 2;
                int halfheight = disHeight / 2;
                rect = new FocusRect(halfwidth - recthalf, halfheight - recthalf, halfwidth + recthalf, halfheight + recthalf);
            }
            RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            mParams.leftMargin = rect.left;
            //mParams.rightMargin = x +half;
            mParams.topMargin = rect.top;

            imageView.setLayoutParams(mParams);
            imageView.setBackgroundResource(R.drawable.crosshair_normal);
            imageView.setVisibility(View.VISIBLE);
            //imageView.getDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public void FocusFinished(final boolean success)
    {
        if (!(wrapper instanceof CameraUiWrapperSony)) {
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    if (success)
                        imageView.setBackgroundResource(R.drawable.crosshair_success);
                    else
                        imageView.setBackgroundResource(R.drawable.crosshair_failed);


                    //handler.postDelayed(hideCrosshair, crosshairShowTime);
                }
            });
        }

    }

    @Override
    public void FocusLocked(final boolean locked)
    {
        cancelFocus.post(new Runnable() {
            @Override
            public void run() {
                if (locked)
                    cancelFocus.setVisibility(View.VISIBLE);
                else
                    cancelFocus.setVisibility(View.GONE);
            }
        });

    }


    /*private Handler handler = new Handler();
    Runnable hideCrosshair = new Runnable() {
        @Override
        public void run()
        {
            imageView.setBackgroundResource(R.drawable.crosshair_normal);
            imageView.setVisibility(View.GONE);
        }
    };*/

    @Override
    protected void OnClick(int x, int y)
    {
        if (wrapper == null || wrapper.Focus == null)
            return;
        disWidth = activity.GetPreviewWidth();
        disHeight = activity.GetPreviewHeight();

        FocusRect rect = new FocusRect(x - recthalf, x + recthalf, y - recthalf, y + recthalf);
        if (wrapper.Focus != null)
            wrapper.Focus.StartTouchToFocus(rect, meteringRect, disWidth, disHeight);
    }

    private class MeteringAreaTouch implements View.OnTouchListener
    {
        float x, y, difx, dify;
        int distance = 10;
        boolean moving = false;
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN: {
                    x = event.getX();
                    y = event.getY();
                    startX = (int)event.getX() - (int)meteringArea.getX();
                    startY =(int) event.getY() - (int)meteringArea.getY();
                    start = System.currentTimeMillis();

                }
                break;
                case MotionEvent.ACTION_MOVE:
                {

                    difx = x - meteringArea.getX();
                    dify = y - meteringArea.getY();
                    int xd = getDistance(startX, (int)difx);
                    int yd = getDistance(startY, (int)dify);

                    if (event.getX() - difx > activity.GetPreviewLeftMargine() && event.getX() - difx + meteringArea.getWidth() < activity.GetPreviewLeftMargine() + activity.GetPreviewWidth())
                        meteringArea.setX(event.getX() - difx);
                    if (event.getY() - dify > activity.GetPreviewTopMargine() && event.getY() - dify + meteringArea.getHeight() < activity.GetPreviewTopMargine() + activity.GetPreviewHeight())
                        meteringArea.setY(event.getY() - dify);
                    if (xd >= distance || yd >= distance) {

                        moving = true;
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
                        meteringRect = new FocusRect((int) meteringArea.getX() - recthalf, (int) meteringArea.getX() + recthalf, (int) meteringArea.getY() - recthalf, (int) meteringArea.getY() + recthalf);
                        if (wrapper != null)
                            wrapper.Focus.SetMeteringAreas(meteringRect, activity.GetPreviewWidth(), activity.GetPreviewHeight());
                    }
                    else
                    {
                        OnClick((int)meteringArea.getX()+recthalf,(int)meteringArea.getY()+recthalf);



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

    private void centerMeteringArea()
    {
        int width = 0;
        int height = 0;

        if (Build.VERSION.SDK_INT >= 17)
        {
            WindowManager wm = (WindowManager)fragment.getActivity().getSystemService(Context.WINDOW_SERVICE);
            Point size =  new Point();
            wm.getDefaultDisplay().getRealSize(size);
            if (fragment.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                width = size.x;
                height = size.y;
            }
            else
            {
                height = size.x;
                width = size.y;
            }
        }
        else
        {
            DisplayMetrics metrics = fragment.getActivity().getResources().getDisplayMetrics();
            if (fragment.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                width = metrics.widthPixels;
                height = metrics.heightPixels;
            }
            else
            {
                width = metrics.heightPixels;
                height = metrics.widthPixels;
            }

        }
        meteringArea.setX(width/2 - recthalf);
        meteringArea.setY(height/2 - recthalf);

        meteringRect = new FocusRect((int)meteringArea.getX() - recthalf, (int)meteringArea.getX() + recthalf, (int)meteringArea.getY() - recthalf, (int)meteringArea.getY() + recthalf);
    }
}
