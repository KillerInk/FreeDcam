package com.troop.freedcam.ui.handler;

import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.ExposureRect;
import com.troop.freedcam.i_camera.interfaces.I_Exposure;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.ui.menu.TouchHandler;

/**
 * Created by George on 1/19/2015.
 */
public class ExposureMeterHandler extends TouchHandler implements I_Exposure {
    private final MainActivity_v2 activity;
    private AbstractCameraUiWrapper wrapper;
    ImageView imageView;
    final int crosshairShowTime = 5000;
    int disHeight;
    int disWidth;
    int recthalf;

    SurfaceView surfaceView;

    public ExposureMeterHandler(MainActivity_v2 activity)
    {
        this.activity = activity;
        imageView = (ImageView)activity.findViewById(R.id.imageView_Crosshair);
        recthalf = activity.getResources().getDimensionPixelSize(R.dimen.crosshairwidth)/2;
        imageView.setVisibility(View.GONE);

    }

    public void SetCamerUIWrapper(AbstractCameraUiWrapper cameraUiWrapper, SurfaceView surfaceView)
    {
        this.surfaceView = surfaceView;
        this.wrapper = cameraUiWrapper;
        if (wrapper.ExposureM.exposureEvent != null)
            wrapper.ExposureM.exposureEvent = this;
    }

    @Override
    public void ExposureStarted(ExposureRect rect)
    {
        disWidth = surfaceView.getLayoutParams().width;
        disHeight = surfaceView.getLayoutParams().height;
        int margineleft = surfaceView.getLeft();
        handler.removeCallbacksAndMessages(null);
        int recthalf = imageView.getWidth()/2;
        int halfwidth = disWidth /2;
        int halfheight = disHeight /2;
        if (rect == null)
        {
            rect = new ExposureRect(halfwidth - recthalf, halfheight -recthalf, halfwidth + recthalf, halfheight + recthalf);
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

  /*  @Override
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

    }*/


    private Handler handler = new Handler();
    Runnable hideCrosshair = new Runnable() {
        @Override
        public void run()
        {
            imageView.setBackgroundResource(R.drawable.xcrosshair_normal);
            imageView.setVisibility(View.GONE);
        }
    };

    @Override
    protected void OnClick(int x, int y)
    {
        disWidth = surfaceView.getWidth();
        disHeight = surfaceView.getHeight();

        ExposureRect rect = new ExposureRect(x - recthalf, x + recthalf, y - recthalf, y + recthalf);
        if (wrapper.ExposureM != null)
            wrapper.ExposureM.StartTouchToFocus(rect, disWidth, disHeight);
    }
}
