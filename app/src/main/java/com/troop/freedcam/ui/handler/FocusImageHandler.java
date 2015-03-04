package com.troop.freedcam.ui.handler;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.interfaces.I_Focus;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.ui.TextureView.PreviewHandler;
import com.troop.freedcam.ui.menu.TouchHandler;

/**
 * Created by troop on 02.09.2014.
 */
public class FocusImageHandler extends TouchHandler implements I_Focus
{
    private final MainActivity_v2 activity;
    private AbstractCameraUiWrapper wrapper;
    ImageView imageView;
    final int crosshairShowTime = 5000;
    int disHeight;
    int disWidth;
    int recthalf;
    ImageView cancelFocus;
    ImageView meteringArea;
    FocusRect meteringRect;

    PreviewHandler surfaceView;

    public FocusImageHandler(MainActivity_v2 activity)
    {
        this.activity = activity;
        imageView = (ImageView)activity.findViewById(R.id.imageView_Crosshair);
        recthalf = activity.getResources().getDimensionPixelSize(R.dimen.crosshairwidth)/2;
        //imageView.setVisibility(View.GONE);
        cancelFocus = (ImageView)activity.findViewById(R.id.imageViewFocusClose);
        cancelFocus.setVisibility(View.GONE);
        cancelFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                wrapper.cameraHolder.CancelFocus();
                cancelFocus.setVisibility(View.GONE);
            }
        });

        meteringArea = (ImageView)activity.findViewById(R.id.imageView_meteringarea);
        meteringArea.setOnTouchListener(new MeteringAreaTouch());
        meteringArea.setVisibility(View.GONE);

    }

    public void SetCamerUIWrapper(AbstractCameraUiWrapper cameraUiWrapper, PreviewHandler surfaceView)
    {
        this.surfaceView = surfaceView;
        this.wrapper = cameraUiWrapper;
        if(cameraUiWrapper instanceof CameraUiWrapper) {
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
        if (!(wrapper instanceof CameraUiWrapperSony) && surfaceView != null)
        {
            disWidth = surfaceView.getLayoutParams().width;
            disHeight = surfaceView.getLayoutParams().height;
            int margineleft = surfaceView.getLeft();
            //handler.removeCallbacksAndMessages(null);
            int recthalf = imageView.getWidth() / 2;
            int halfwidth = disWidth / 2;
            int halfheight = disHeight / 2;
            if (rect == null) {
                rect = new FocusRect(halfwidth - recthalf, halfheight - recthalf, halfwidth + recthalf, halfheight + recthalf);
            }
            RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            mParams.leftMargin = rect.left + margineleft;
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
        disWidth = surfaceView.getWidth();
        disHeight = surfaceView.getHeight();

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

                }
                break;
                case MotionEvent.ACTION_MOVE:
                {

                    difx = x - meteringArea.getX();
                    dify = y - meteringArea.getY();
                    int xd = getDistance(startX, (int)difx);
                    int yd = getDistance(startY, (int)dify);

                    if (event.getX() - difx > surfaceView.getLeft() && event.getX() - difx + meteringArea.getWidth() < surfaceView.getLeft() + surfaceView.getWidth())
                        meteringArea.setX(event.getX() - difx);
                    if (event.getY() - dify > surfaceView.getTop() && event.getY() - dify + meteringArea.getHeight() < surfaceView.getTop() + surfaceView.getHeight())
                        meteringArea.setY(event.getY() - dify);
                    if (xd >= distance || yd >= distance) {

                        moving = true;
                    }
                }
                break;
                case MotionEvent.ACTION_UP:
                {
                    if (moving)
                    {
                        moving = false;
                        x = 0;
                        y = 0;
                        difx = 0;
                        dify = 0;
                        meteringRect = new FocusRect((int) meteringArea.getX() - recthalf, (int) meteringArea.getX() + recthalf, (int) meteringArea.getY() - recthalf, (int) meteringArea.getY() + recthalf);
                        if (wrapper != null)
                            wrapper.Focus.SetMeteringAreas(meteringRect, surfaceView.getWidth(), surfaceView.getHeight());
                    }
                    else
                    {
                        OnClick((int)meteringArea.getX()-recthalf,(int)meteringArea.getY()+recthalf);
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
            WindowManager wm = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
            Point size =  new Point();
            wm.getDefaultDisplay().getRealSize(size);
            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
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
            DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
            if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
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
