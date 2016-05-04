package com.freedcam.apis.camera1.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.freedcam.apis.i_camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.lge.real3d.Real3D;
import com.lge.real3d.Real3DInfo;

/**
 * Created by troop on 21.08.2014.
 */
public class ExtendedSurfaceView extends SurfaceView
{
    private boolean hasReal3d = false;
    private boolean hasOpenSense = false;
    private static String TAG = ExtendedSurfaceView.class.getSimpleName();
    private Context context;

    private SurfaceHolder mHolder;
    private SharedPreferences preferences;

    private Real3D mReal3D;
    private boolean is3D = false;

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    public AbstractParameterHandler ParametersHandler;

    public ExtendedSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public ExtendedSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExtendedSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;


        if (Build.VERSION.SDK_INT < 21)
        {

            try {
                isopensense();
                isReald3d();

                preferences = PreferenceManager.getDefaultSharedPreferences(context);
                mHolder = getHolder();
                mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


                if (hasReal3d) {
                    mReal3D = new Real3D(mHolder);
                    mReal3D.setMinimumNegative(-1);
                    SwitchViewMode();
                }
            } catch (NoSuchMethodError noSuchMethodError) {
                Logger.d("Not", " 3D Device");
            }
        }
        else
        {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            mHolder = getHolder();
            android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(960, 720);
            this.setLayoutParams(params);
        }
    }

    private void isReald3d()
    {
        try {
            Class c = Class.forName("com.lge.real3d.Real3D");
            final String LGE_3D_DISPLAY = "lge.hardware.real3d.barrier.landscape";
            if(context.getPackageManager().hasSystemFeature(LGE_3D_DISPLAY))
                hasReal3d = true;
        } catch (ClassNotFoundException e) {
            hasReal3d = false;
        }

    }

    private void isopensense()
    {
        try {
            Class c = Class.forName("com.htc.view.DisplaySetting");
            Logger.d(TAG, "Found class com.htc.view.DisplaySetting");
            hasOpenSense = true;

        } catch (ClassNotFoundException e) {

            hasOpenSense = false;
            Logger.d(TAG, "didnt find class com.htc.view.DisplaySetting, NO 3D!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

    }

    public  void SwitchViewMode()
    {
        if (hasReal3d)
        {
            if (preferences.getInt(AppSettingsManager.SETTING_CURRENTCAMERA, 0) == 2)
            {
                is3D = true;
                mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_SS, Real3D.REAL3D_ORDER_LR));
                Logger.d(TAG, "Set 3d");
            }
            else
            {
                is3D = false;
                mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_NONE, 0));
                Logger.d(TAG, "Set 2d");
            }
        }
    }

    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        if (hasReal3d && is3D)
        {
            ParametersHandler.PreviewSize.SetValue(800 + "x" + 480, true);
            mRatioWidth = 800;
            mRatioHeight = 480;
        }
        else {
            mRatioWidth = width;
            mRatioHeight = height;
        }
        Logger.d(TAG, "new size: " + width + "x" + height);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height);
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
            } else {
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
            }
        }
    }
}
