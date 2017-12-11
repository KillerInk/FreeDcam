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

package freed.cam.ui.themesample.handler;


import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.troop.freedcam.R;

import freed.ActivityAbstract;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.settings.Settings;
import freed.cam.apis.camera1.Camera1Fragment;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.sonyremote.SonyCameraRemoteFragment;
import freed.cam.ui.themesample.cameraui.FocusSelector;
import freed.cam.ui.themesample.handler.ImageViewTouchAreaHandler.I_TouchListnerEvent;


/**
 * Created by troop on 02.09.2014.
 */
public class FocusImageHandler extends AbstractFocusImageHandler
{
    private CameraWrapperInterface wrapper;
    private final FocusSelector focusImageView;
    private int disHeight;
    private int disWidth;
    private final int recthalf;
    private final ImageView cancelFocus;
    private final ImageView meteringArea;
    private boolean touchToFocusIsSupported = false;
    private boolean meteringIsSupported = false;


    public FocusImageHandler(View view, ActivityAbstract fragment)
    {
        super(fragment);
        focusImageView = (FocusSelector) view.findViewById(R.id.imageView_Crosshair);

        cancelFocus = (ImageView)view.findViewById(R.id.imageViewFocusClose);
        meteringArea = (ImageView)view.findViewById(R.id.imageView_meteringarea);
        recthalf = fragment.getResources().getDimensionPixelSize(R.dimen.cameraui_focusselector_width)/2;

        cancelFocus.setVisibility(View.GONE);
        cancelFocus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                wrapper.getCameraHolder().CancelFocus();
                cancelFocus.setVisibility(View.GONE);
            }
        });


        meteringArea.setVisibility(View.GONE);
        if (wrapper != null)
            meteringArea.setOnTouchListener(new ImageViewTouchAreaHandler(meteringArea, wrapper, meteringTouch));
    }

    public void SetCamerUIWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        wrapper = cameraUiWrapper;
        if(cameraUiWrapper instanceof Camera1Fragment || cameraUiWrapper instanceof Camera2Fragment) {
            centerImageView(meteringArea);
            meteringArea.setOnTouchListener(new ImageViewTouchAreaHandler(meteringArea, wrapper, meteringTouch));
            if (wrapper.isAeMeteringSupported())
            {
                meteringArea.setVisibility(View.VISIBLE);
                meteringIsSupported = true;
            }
            else {
                meteringArea.setVisibility(View.GONE);
                meteringIsSupported = false;

            }

        }
        else
        {
            meteringArea.setVisibility(View.GONE);
            meteringIsSupported = false;
        }
        if (wrapper.getFocusHandler() != null)
            wrapper.getFocusHandler().focusEvent = this;
        focusImageView.setVisibility(View.GONE);
    }

    @Override
    public void FocusStarted(int x, int y)
    {
        if (!(wrapper instanceof SonyCameraRemoteFragment))
        {
            disWidth = wrapper.getPreviewWidth();
            disHeight = wrapper.getPreviewHeight();

            /*if (rect == null)
            {
                int halfwidth = disWidth / 2;
                int halfheight = disHeight / 2;
                rect = new FocusRect(halfwidth - recthalf, halfheight - recthalf, halfwidth + recthalf, halfheight + recthalf,halfwidth,halfheight);
            }*/
            final LayoutParams mParams = (LayoutParams) focusImageView.getLayoutParams();
            mParams.leftMargin = x;
            mParams.topMargin = y;

            focusImageView.post(new Runnable() {
                @Override
                public void run() {
                    focusImageView.setLayoutParams(mParams);
                    //focusImageView.setBackgroundResource(R.drawable.crosshair_circle_normal);
                    focusImageView.setFocusCheck(false);
                    focusImageView.setVisibility(View.VISIBLE);
                    Animation anim = AnimationUtils.loadAnimation(focusImageView.getContext(), R.anim.scale_focusimage);
                    focusImageView.startAnimation(anim);
                }
            });

        }
    }

    @Override
    public void FocusFinished(final boolean success)
    {
        if (!(wrapper instanceof SonyCameraRemoteFragment)) {
            focusImageView.post(new Runnable() {
                @Override
                public void run() {
                    focusImageView.setFocusCheck(success);
                    focusImageView.getFocus(wrapper.getParameterHandler().getFocusDistances());
                    /*if (success)
                        focusImageView.setBackgroundResource(R.drawable.crosshair_circle_success);
                    else
                        focusImageView.setBackgroundResource(R.drawable.crosshair_circle_failed);*/

                    focusImageView.setAnimation(null);
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
        touchToFocusIsSupported = isSupported;
        if (!isSupported)
            focusImageView.setVisibility(View.GONE);
    }

    @Override
    public void AEMeteringSupported(final boolean isSupported)
    {
        meteringIsSupported = isSupported;
        meteringArea.post(new Runnable() {
            @Override
            public void run() {
                if (isSupported)
                    meteringArea.setVisibility(View.VISIBLE);
                else
                    meteringArea.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (wrapper instanceof SonyCameraRemoteFragment)
            wrapper.getFocusHandler().SetMotionEvent(event);
        return false;
    }

    private final I_TouchListnerEvent meteringTouch = new I_TouchListnerEvent() {
        @Override
        public void onAreaCHanged(int x, int y, int previewWidth, int previewHeight) {
            if (wrapper != null)
                wrapper.getFocusHandler().SetMeteringAreas(x,y,previewWidth, previewHeight);
        }

        @Override
        public void OnAreaClick(int x, int y) {
            OnClick(x,y);
        }

        @Override
        public void OnAreaLongClick(int x, int y) {
            
        }

        /**
         * Disable the viewpager touch while moving to avoid that settings or screenslide get open
         * @param moving when true disable viewpager from CameraUI
         *               else allow swiping
         */
        @Override
        public void IsMoving(boolean moving)
        {
            //disable exposure lock that metering can get applied
            ParameterInterface expolock = wrapper.getParameterHandler().get(Settings.ExposureLock);
            if (moving && expolock != null && expolock.IsSupported() && expolock.GetStringValue().equals("true"))
            {
                expolock.SetValue("false",true);
            }
            //enable/disable viewpager touch
            fragment.DisablePagerTouch(moving);
        }
    };

    /*
    This listen to clicks that happen inside awb or exposuremetering
    and translate that postion into preview coordinates for touchtofocus
     */
    public void OnClick(int x, int y)
    {
        int width = wrapper.getPreviewWidth() + recthalf;
        if (wrapper == null || wrapper.getFocusHandler() == null || !touchToFocusIsSupported
                || x < wrapper.getMargineLeft() || x > width) {
            focusImageView.setVisibility(View.GONE);
            return;
        }
        disWidth = wrapper.getPreviewWidth();
        disHeight = wrapper.getPreviewHeight();

        /*int marginLeft = wrapper.getMargineLeft();
        int marginRight = wrapper.getMargineRight();
        if (x > marginLeft && x < disWidth + marginLeft) {
            if (x < marginLeft + recthalf)
                x = marginLeft + recthalf;
            if (x > marginRight - recthalf)
                x = marginRight - recthalf;
            if (y < recthalf)
                y = recthalf;
            if (y > disHeight - recthalf)
                y = disHeight - recthalf;


        }*/

        if (wrapper.getFocusHandler() != null)
            wrapper.getFocusHandler().StartTouchToFocus(x,y, disWidth, disHeight);



    }


    /*
    Centers the attached Imageview
     */
    private Rect centerImageView(ImageView imageview)
    {
        int width = 0;
        int height = 0;

        if(fragment == null)
            return null;
        if (VERSION.SDK_INT >= 17)
        {
            WindowManager wm = (WindowManager) fragment.getSystemService(Context.WINDOW_SERVICE);
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
            DisplayMetrics metrics = fragment.getResources().getDisplayMetrics();
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

        return new Rect((int)imageview.getX() - recthalf, (int)imageview.getX() + recthalf, (int)imageview.getY() - recthalf, (int)imageview.getY() + recthalf);
    }
}
