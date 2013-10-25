package com.troop.freecam.manager.Drawing;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.lge.real3d.Real3D;
import com.lge.real3d.Real3DInfo;
import com.troop.freecam.CameraManager;

/**
 * Created by troop on 25.09.13.
 */
public class DrawingOverlaySurface extends SurfaceView implements SurfaceHolder.Callback
{

    public SurfaceHolder mHolder;
    private Real3D mReal3D;
    public SharedPreferences preferences;
    Context context;
    public SizeAbleRectangle drawingRectHelper;
    public boolean RDY = false;

    long lastclick;

    public DrawingOverlaySurface(Context context)
    {
        super(context);
        this.context = context;
        init();
    }

    public DrawingOverlaySurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init()
    {
        this.setZOrderOnTop(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        mHolder.addCallback(this);
        // Initialize Real3D object
        mReal3D = new Real3D(mHolder);
        mReal3D.setMinimumNegative(-1);
        mReal3D.setMaximumPositive(1);
        drawingRectHelper = new SizeAbleRectangle(this);
        SwitchViewMode();
    }

    public  void SwitchViewMode()
    {

        if (preferences.getString(CameraManager.SwitchCamera, CameraManager.SwitchCamera_MODE_3D).equals(CameraManager.SwitchCamera_MODE_3D))
        {
            //mReal3D.setViewMode(1);
            mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_SS, Real3D.REAL3D_ORDER_LR));
        }
        else
        {
            //mReal3D = null;
            //mReal3D.setViewMode(2);
            mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_NONE, 0));
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        RDY = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        RDY = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        RDY = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        drawingRectHelper.OnTouch(event);

        return true;
    }
}
