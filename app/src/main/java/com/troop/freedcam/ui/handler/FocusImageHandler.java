package com.troop.freedcam.ui.handler;

import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.interfaces.I_Focus;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.MainActivity_v2;
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

    SurfaceView surfaceView;

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

    }

    public void SetCamerUIWrapper(AbstractCameraUiWrapper cameraUiWrapper, SurfaceView surfaceView)
    {
        this.surfaceView = surfaceView;
        this.wrapper = cameraUiWrapper;
        if (wrapper.Focus != null)
            wrapper.Focus.focusEvent = this;
    }

    @Override
    public void FocusStarted(FocusRect rect)
    {
        if (!(wrapper instanceof CameraUiWrapperSony))
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
            wrapper.Focus.StartTouchToFocus(rect, disWidth, disHeight);
    }
}
