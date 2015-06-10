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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera2.CameraUiWrapperApi2;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.FocusRect;
import com.troop.freedcam.i_camera.interfaces.I_Focus;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.AbstractFocusImageHandler;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.ImageViewTouchAreaHandler;
import com.troop.freedcam.ui.menu.themes.R;

/**
 * Created by troop on 02.09.2014.
 */
public class FocusImageHandler extends AbstractFocusImageHandler
{
    private AbstractCameraUiWrapper wrapper;
    ImageView focusImageView;
    final int crosshairShowTime = 5000;
    int disHeight;
    int disWidth;
    int recthalf;
    ImageView cancelFocus;
    ImageView meteringArea;
    ImageView awbArea;
    FocusRect meteringRect;
    FocusRect awbRect;
    static final int MAX_DURATION = 3500;


    public FocusImageHandler(View view, Fragment fragment, I_Activity activity)
    {
        super(view,fragment, activity);
        focusImageView = (ImageView)view.findViewById(R.id.imageView_Crosshair);
        recthalf = fragment.getResources().getDimensionPixelSize(R.dimen.crosshairwidth)/2;
        //focusImageView.setVisibility(View.GONE);
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
        meteringArea.setOnTouchListener(new ImageViewTouchAreaHandler(meteringArea, activity, meteringTouch, true));
        meteringArea.setVisibility(View.GONE);
        awbArea = (ImageView)view.findViewById(R.id.imageView_awbarea);
        awbArea.setOnTouchListener(new ImageViewTouchAreaHandler(awbArea, activity, awbTouch, true));
        awbArea.setVisibility(View.GONE);

    }

    public void SetCamerUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.wrapper = cameraUiWrapper;
        if(cameraUiWrapper instanceof CameraUiWrapper || cameraUiWrapper instanceof CameraUiWrapperApi2) {
            meteringRect = centerImageView(meteringArea);
            meteringArea.setVisibility(View.VISIBLE);
        }
        else
        {
            meteringArea.setVisibility(View.GONE);
        }
        if(cameraUiWrapper instanceof CameraUiWrapperApi2)
        {
            awbRect = centerImageView(awbArea);
            awbArea.setVisibility(View.VISIBLE);
        }
        else
            awbArea.setVisibility(View.GONE);
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
            RelativeLayout.LayoutParams mParams = (RelativeLayout.LayoutParams) focusImageView.getLayoutParams();
            mParams.leftMargin = rect.left;
            //mParams.rightMargin = x +half;
            mParams.topMargin = rect.top;

            focusImageView.setLayoutParams(mParams);
            focusImageView.setBackgroundResource(R.drawable.crosshair_normal);
            focusImageView.setVisibility(View.VISIBLE);

            RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

//Setup anim with desired properties
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE); //Repeat animation indefinitely
            anim.setDuration(5000); //Put desired duration per anim cycle here, in milliseconds

//Start animation
            focusImageView.startAnimation(anim);
//Later on, use view.setAnimation(null) to stop it.
            //focusImageView.getDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public void FocusFinished(final boolean success)
    {
        if (!(wrapper instanceof CameraUiWrapperSony)) {
            focusImageView.post(new Runnable() {
                @Override
                public void run() {
                    if (success)
                        focusImageView.setBackgroundResource(R.drawable.crosshair_success);
                    else
                        focusImageView.setBackgroundResource(R.drawable.crosshair_failed);

                    focusImageView.setAnimation(null);
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

    @Override
    public void TouchToFocusSupported(boolean isSupported)
    {
        if (!isSupported)
            focusImageView.setVisibility(View.GONE);
    }

    @Override
    public void AEMeteringSupported(boolean isSupported) {
        if (isSupported)
            meteringArea.setVisibility(View.VISIBLE);
        else
            meteringArea.setVisibility(View.GONE);
    }

    @Override
    public void AWBMeteringSupported(boolean isSupported) {
        if (isSupported)
            awbArea.setVisibility(View.VISIBLE);
        else
            awbArea.setVisibility(View.GONE);
    }


    /*private Handler handler = new Handler();
    Runnable hideCrosshair = new Runnable() {
        @Override
        public void run()
        {
            focusImageView.setBackgroundResource(R.drawable.crosshair_normal);
            focusImageView.setVisibility(View.GONE);
        }
    };*/

    ImageViewTouchAreaHandler.I_TouchListnerEvent meteringTouch = new ImageViewTouchAreaHandler.I_TouchListnerEvent() {
        @Override
        public void onAreaCHanged(FocusRect imageRect, int previewWidth, int previewHeight) {
            if (wrapper != null)
                wrapper.Focus.SetMeteringAreas(imageRect,previewWidth, previewHeight);
        }

        @Override
        public void OnAreaClick(int x, int y) {
            OnClick(x,y);
        }
    };

    ImageViewTouchAreaHandler.I_TouchListnerEvent awbTouch = new ImageViewTouchAreaHandler.I_TouchListnerEvent() {
        @Override
        public void onAreaCHanged(FocusRect imageRect, int previewWidth, int previewHeight) {
            if (wrapper != null)
                wrapper.Focus.SetAwbAreas(imageRect, previewWidth, previewHeight);
        }

        @Override
        public void OnAreaClick(int x, int y) {
            OnClick(x,y);
        }
    };

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


    private FocusRect centerImageView(ImageView imageview)
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
        imageview.setX(width/2 - recthalf);
        imageview.setY(height/2 - recthalf);

        return new FocusRect((int)imageview.getX() - recthalf, (int)imageview.getX() + recthalf, (int)imageview.getY() - recthalf, (int)imageview.getY() + recthalf);
    }



}
