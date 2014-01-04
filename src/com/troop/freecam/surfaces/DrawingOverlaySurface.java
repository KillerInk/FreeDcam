package com.troop.freecam.surfaces;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.lge.real3d.Real3D;
import com.lge.real3d.Real3DInfo;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.SizeAbleRectangle;
import com.troop.freecam.manager.SettingsManager;

/**
 * Created by troop on 25.09.13.
 */
public class DrawingOverlaySurface extends BasePreview implements SurfaceHolder.Callback
{

    public SurfaceHolder mHolder;
    private Real3D mReal3D;
    public SharedPreferences preferences;
    Context context;
    public SizeAbleRectangle drawingRectHelper;
    public boolean RDY = false;
    private CameraManager camMan;

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

    public DrawingOverlaySurface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    private void init()
    {
        this.isInEditMode();
        isReald3d();
        this.setZOrderOnTop(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mHolder = getHolder();
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        mHolder.addCallback(this);
        drawingRectHelper = new SizeAbleRectangle(this, camMan);
        // Initialize Real3D object
        if (hasReal3d)
        {
            mReal3D = new Real3D(mHolder);
            mReal3D.setMinimumNegative(-1);
            mReal3D.setMaximumPositive(1);

            SwitchViewMode();
        }
    }

    public  void SwitchViewMode()
    {

        if (hasReal3d)
        {
            if (preferences.getString(SettingsManager.SwitchCamera, SettingsManager.SwitchCamera_MODE_Front).equals(SettingsManager.SwitchCamera_MODE_3D))
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

    public void SetCameraManager(CameraManager cameraManager)
    {
        this.camMan = cameraManager;
        drawingRectHelper.cameraManager = cameraManager;
    }
}
