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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.troop.freedcam.R;

import freed.ActivityAbstract;
import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.apis.camera1.Camera1;
import freed.cam.apis.camera2.Camera2;
import freed.cam.previewpostprocessing.PreviewController;
import freed.cam.ui.themesample.PagingViewTouchState;
import freed.cam.ui.themesample.cameraui.FocusSelector;
import freed.cam.ui.themesample.handler.ImageViewTouchAreaHandler.I_TouchListnerEvent;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.DisplayUtil;
import freed.utils.Log;


/**
 * Created by troop on 02.09.2014.
 */
public class FocusImageHandler extends AbstractFocusImageHandler
{
    private static final String TAG =  FocusImageHandler.class.getSimpleName();
    private CameraWrapperInterface wrapper;
    private final FocusSelector focusImageView;
    private int disHeight;
    private int disWidth;
    private final int recthalf;
    private final ImageView cancelFocus;
    private final ImageView meteringArea;
    private boolean touchToFocusIsSupported = false;
    private boolean meteringIsSupported = false;
    private boolean waitForFocusEnd = false;

    private SettingsManager settingsManager;
    private PreviewController previewController;
    private PagingViewTouchState pagingViewTouchState;

    public FocusImageHandler(View view, ActivityAbstract fragment, PagingViewTouchState pagingViewTouchState)
    {
        super(fragment);
        settingsManager = FreedApplication.settingsManager();
        previewController = ActivityFreeDcamMain.previewController();
        focusImageView = view.findViewById(R.id.imageView_Crosshair);

        cancelFocus = view.findViewById(R.id.imageViewFocusClose);
        meteringArea = view.findViewById(R.id.imageView_meteringarea);
        recthalf = fragment.getResources().getDimensionPixelSize(R.dimen.cameraui_focusselector_width)/2;

        cancelFocus.setVisibility(View.GONE);
        cancelFocus.setOnClickListener(v -> {
            wrapper.getCameraHolder().CancelFocus();
            cancelFocus.setVisibility(View.GONE);
        });


        meteringArea.setVisibility(View.GONE);
        if (wrapper != null)
            meteringArea.setOnTouchListener(new ImageViewTouchAreaHandler(meteringArea, wrapper, meteringTouch,previewController));
    }

    public void SetCamerUIWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        wrapper = cameraUiWrapper;
        if(cameraUiWrapper instanceof Camera1 || cameraUiWrapper instanceof Camera2) {
            centerImageView(meteringArea);
            meteringArea.setOnTouchListener(new ImageViewTouchAreaHandler(meteringArea, wrapper, meteringTouch,previewController));
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
        if (wrapper.getFocusHandler() != null) {
            wrapper.getFocusHandler().focusEvent = this;
            TouchToFocusSupported(wrapper.getFocusHandler().isTouchSupported());
        }
        focusImageView.setVisibility(View.GONE);
    }

    @Override
    public void FocusStarted(int x, int y)
    {
        waitForFocusEnd = true;

        Log.d(TAG,"FocusStarted");
        disWidth = previewController.getViewWidth();
        disHeight = previewController.getViewHeight();

        /*if (rect == null)
        {
            int halfwidth = disWidth / 2;
            int halfheight = disHeight / 2;
            rect = new FocusRect(halfwidth - recthalf, halfheight - recthalf, halfwidth + recthalf, halfheight + recthalf,halfwidth,halfheight);
        }*/
        final LayoutParams mParams = (LayoutParams) focusImageView.getLayoutParams();
        mParams.leftMargin = x +getLeftMargin();
        mParams.topMargin = y+ getTopMargin();

        focusImageView.post(() -> {
            focusImageView.setLayoutParams(mParams);
            //focusImageView.setBackgroundResource(R.drawable.crosshair_circle_normal);
            focusImageView.setFocusCheck(false);
            focusImageView.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(focusImageView.getContext(), R.anim.scale_focusimage);
            focusImageView.startAnimation(anim);
        });

    }

    @Override
    public void FocusFinished(final boolean success)
    {
        if (waitForFocusEnd) {
            waitForFocusEnd = false;
                focusImageView.post(() -> {
                    focusImageView.setFocusCheck(success);
                    focusImageView.getFocus(wrapper.getParameterHandler().getFocusDistances());
                    Log.d(TAG,"Focus success:" + success + " TouchtoCapture:" + settingsManager.getGlobal(SettingKeys.TouchToCapture).get());
                    if (success && settingsManager.getGlobal(SettingKeys.TouchToCapture).get() && !wrapper.getModuleHandler().getCurrentModule().ModuleName().equals(FreedApplication.getStringFromRessources(R.string.module_video))) {
                        Log.d(TAG,"start capture");
                        wrapper.getModuleHandler().startWork();
                    }


                    focusImageView.setAnimation(null);
                });
            }

    }

    @Override
    public void FocusLocked(final boolean locked)
    {
        cancelFocus.post(() -> {
            if (locked)
                cancelFocus.setVisibility(View.VISIBLE);
            else
                cancelFocus.setVisibility(View.GONE);
        });

    }

    @Override
    public void TouchToFocusSupported(boolean isSupported)
    {
        touchToFocusIsSupported = isSupported;
        if (!isSupported)
            focusImageView.post(()->focusImageView.setVisibility(View.GONE));
    }

    @Override
    public void AEMeteringSupported(final boolean isSupported)
    {
        meteringIsSupported = isSupported;
        meteringArea.post(() -> {
            if (isSupported)
                meteringArea.setVisibility(View.VISIBLE);
            else
                meteringArea.setVisibility(View.GONE);
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        /*if (wrapper instanceof SonyCameraRemoteFragment)
            wrapper.getFocusHandler().SetMotionEvent(event);*/
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
            ParameterInterface expolock = wrapper.getParameterHandler().get(SettingKeys.ExposureLock);
            if (moving && expolock != null && expolock.getViewState() == AbstractParameter.ViewState.Visible && expolock.getStringValue().equals("true"))
            {
                expolock.setStringValue("false",true);
            }
            //enable/disable viewpager touch
            pagingViewTouchState.setTouchEnable(!moving);
        }
    };

    /*
    This listen to clicks that happen inside awb or exposuremetering
    and translate that postion into preview coordinates for touchtofocus
     */
    public void OnClick(int x, int y)
    {
        if (!touchToFocusIsSupported) {
            focusImageView.setVisibility(View.GONE);
            return;
        }
        Log.d(TAG, "view width/height:" + previewController.getViewWidth() + "/" + previewController.getViewHeight());
        Log.d(TAG, "preview view width/height:" + previewController.getPreviewWidth() + "/" + previewController.getPreviewHeight());
        Log.d(TAG, "Margin left top" + getLeftMargin() + "/" + getTopMargin());
        Log.d(TAG, "touch x y " + x + "/" + y);
        float vw = previewController.getViewWidth();
        float vh = previewController.getViewHeight();
        float x_nonMargin = x -getLeftMargin();
        float y_nonMargin = y - getTopMargin();
        float x_pos = 1/ vw * x_nonMargin;
        float y_pos = 1/ vh * y_nonMargin;
        Log.d(TAG, "normalized pos  x/y " + x_pos + "/" + y_pos);
        x -= (recthalf +getLeftMargin());
        y -= (recthalf +getTopMargin());
        if(x_pos >=0 && x_pos <= 1 && y_pos >=0 && y_pos <= 1)
        {
            if (wrapper.getFocusHandler() != null)
                wrapper.getFocusHandler().StartTouchToFocus(x,y,previewController.getViewWidth(),previewController.getViewHeight(), x_pos, y_pos);
        }
    }

    private int getLeftMargin()
    {
        return previewController.getViewWidth()/2 - previewController.getPreviewWidth()/2;
    }

    private int getTopMargin()
    {
        int mtop = previewController.getViewHeight()/2 - previewController.getPreviewHeight()/2;
        if (mtop > 0)
            return mtop;
        else return 0;
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
        Point displaySize = DisplayUtil.getDisplaySize();
        if (fragment.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = displaySize.x;
            height = displaySize.y;
        }
        else
        {
            height = displaySize.x;
            width = displaySize.y;
        }
        imageview.setX(width/2 - recthalf);
        imageview.setY(height/2 - recthalf);

        return new Rect((int)imageview.getX() - recthalf, (int)imageview.getX() + recthalf, (int)imageview.getY() - recthalf, (int)imageview.getY() + recthalf);
    }
}
