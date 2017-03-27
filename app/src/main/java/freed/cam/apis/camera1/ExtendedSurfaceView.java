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

package freed.cam.apis.camera1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import freed.utils.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout.LayoutParams;

import com.lge.real3d.Real3D;
import com.lge.real3d.Real3DInfo;

import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.utils.AppSettingsManager;

/**
 * Created by troop on 21.08.2014.
 */
public class ExtendedSurfaceView extends SurfaceView
{
    private boolean hasReal3d;
    private final String TAG = ExtendedSurfaceView.class.getSimpleName();
    private Context context;

    private SharedPreferences preferences;

    private Real3D mReal3D;
    private boolean is3D;

    private int mRatioWidth;
    private int mRatioHeight;
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


        SurfaceHolder mHolder;
        if (VERSION.SDK_INT < 21)
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
                Log.d("Not", " 3D Device");
            }
        }
        else
        {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            mHolder = getHolder();
            LayoutParams params = new LayoutParams(960, 720);
            setLayoutParams(params);
        }
    }

    private void isReald3d()
    {
        try {
            Class c = Class.forName("com.lge.real3d.Real3D");
            String LGE_3D_DISPLAY = "lge.hardware.real3d.barrier.landscape";
            if(context.getPackageManager().hasSystemFeature(LGE_3D_DISPLAY))
                hasReal3d = true;
        } catch (ClassNotFoundException e) {
            hasReal3d = false;
        }

    }

    private void isopensense()
    {
        boolean hasOpenSense;
        try {
            Class c = Class.forName("com.htc.view.DisplaySetting");
            Log.d(TAG, "Found class com.htc.view.DisplaySetting");
            hasOpenSense = true;

        } catch (ClassNotFoundException e) {

            hasOpenSense = false;
            Log.d(TAG, "didnt find class com.htc.view.DisplaySetting, NO 3D!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

    }

    public  void SwitchViewMode()
    {
        if (hasReal3d)
        {
            if (preferences.getInt(AppSettingsManager.CURRENTCAMERA, 0) == 2)
            {
                is3D = true;
                mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_SS, Real3D.REAL3D_ORDER_LR));
                Log.d(this.TAG, "Set 3d");
            }
            else
            {
                is3D = false;
                mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_NONE, 0));
                Log.d(this.TAG, "Set 2d");
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
        Log.d(TAG, "new size: " + width + "x" + height);
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
