package com.troop.freecamv2.ui.TextureView;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.RelativeLayout;

import com.lge.real3d.Real3D;
import com.lge.real3d.Real3DInfo;
import com.troop.freecam.manager.AppSettingsManager;

/**
 * Created by troop on 21.08.2014.
 */
public class ExtendedSurfaceView extends SurfaceView implements I_PreviewSizeEvent
{
    boolean hasReal3d = false;
    boolean hasOpenSense = false;
    final String TAG = "freecam.ExtendedTextureView";
    Context context;

    public SurfaceHolder mHolder;
    SharedPreferences preferences;

    Real3D mReal3D;

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
        try
        {
            isopensense();
            isReald3d();
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
            mHolder = getHolder();
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            if (hasReal3d)
            {
                mReal3D = new Real3D(mHolder);
                mReal3D.setMinimumNegative(-1);
                SwitchViewMode();
            }
        }
        catch (NoSuchMethodError noSuchMethodError)
        {
            Log.d("Not", " 3D Device");
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
            if (preferences.getInt(com.troop.freecamv2.ui.AppSettingsManager.SETTING_CURRENTCAMERA, 0) == 2)
                mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_SS, Real3D.REAL3D_ORDER_LR));
            else
                mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_NONE, 0));
        }
    }

    @Override
    public void OnPreviewSizeChanged(int w, int h)
    {
        //[1.00 = square] [1.25 = 5:4] [1.33 = 4:3] [1.50 = 3:2] [1.60 = 16:10] [1.67 = 5:3] [1.71 = 128:75] [1.78 = 16:9] [1.85] [2.33 = 21:9 (1792x768)] [2.35 = Cinamascope] [2.37 = "21:9" (2560x1080)] [2.39 = Panavision]
        double newratio = (double)w/(double)h;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        double displayratio = (double)metrics.widthPixels/(double)metrics.heightPixels;
        if (newratio == displayratio)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.rightMargin = 0;
            this.setLayoutParams(layoutParams);
        }
        else
        {
            int tmo = (int)((double)metrics.widthPixels / displayratio * newratio);
            int newwidthdiff = metrics.widthPixels - tmo;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.rightMargin = newwidthdiff;
            this.setLayoutParams(layoutParams);
        }

    }
}
