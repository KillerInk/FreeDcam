package com.troop.freecamv2.ui.handler;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.troop.freecam.R;
import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.I_Focus;
import com.troop.freecamv2.ui.MainActivity_v2;
import com.troop.freecamv2.ui.TextureView.ExtendedSurfaceView;
import com.troop.freecamv2.ui.menu.TouchHandler;

/**
 * Created by troop on 02.09.2014.
 */
public class FocusImageHandler extends TouchHandler implements I_Focus
{
    private final MainActivity_v2 activity;
    private final CameraUiWrapper wrapper;
    ImageView imageView;
    final int crosshairShowTime = 5000;
    int disHeight;
    int disWidth;

    ExtendedSurfaceView surfaceView;

    public FocusImageHandler(MainActivity_v2 activity, CameraUiWrapper wrapper)
    {
        this.activity = activity;
        this.wrapper = wrapper;
        wrapper.Focus.focusEvent = this;
        imageView = (ImageView)activity.findViewById(R.id.imageView_Crosshair);
        imageView.setVisibility(View.GONE);
        surfaceView = (ExtendedSurfaceView)activity.findViewById(R.id.CameraPreview);


    }

    @Override
    public void FocusStarted(Rect rect)
    {
        disWidth = surfaceView.getWidth();
        disHeight = surfaceView.getHeight();
        handler.removeCallbacksAndMessages(null);
        int recthalf = imageView.getWidth()/2;
        int halfwidth = disWidth /2;
        int halfheight = disHeight /2;
        if (rect == null)
        {
            rect = new Rect(halfwidth - recthalf, halfheight -recthalf, halfwidth + recthalf, halfheight + recthalf);
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

    @Override
    public void FocusFinished(final boolean success)
    {
        imageView.post(new Runnable() {
            @Override
            public void run() {
                if (success)
                    imageView.setBackgroundResource(R.drawable.crosshair_success);
                else
                    imageView.setBackgroundResource(R.drawable.crosshair_failed);


                handler.postDelayed(hideCrosshair, crosshairShowTime);
            }
        });

    }


    private Handler handler = new Handler();
    Runnable hideCrosshair = new Runnable() {
        @Override
        public void run()
        {
            imageView.setBackgroundResource(R.drawable.crosshair_normal);
            imageView.setVisibility(View.GONE);
        }
    };

    @Override
    protected void OnClick(int x, int y)
    {
        disWidth = surfaceView.getWidth();
        disHeight = surfaceView.getHeight();
        int recthalf = imageView.getWidth()/2;
        Rect rect = new Rect(x - recthalf, y -recthalf, x +recthalf, y +recthalf);
        wrapper.Focus.StartTouchToFocus(rect, disWidth, disHeight);
    }
}
