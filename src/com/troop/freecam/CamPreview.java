package com.troop.freecam;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lge.real3d.Real3D;
import com.lge.real3d.Real3DInfo;

import java.util.List;

public class CamPreview extends SurfaceView  {

	SurfaceHolder mHolder;
    private Real3D mReal3D;
    private CameraManager camMan;
    SharedPreferences preferences;
    boolean is3d = false;


    private void init(Context context) {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mHolder = getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // Initialize Real3D object
        mReal3D = new Real3D(mHolder);
        // Set type to Side by Side.
        //mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_SS, Real3D.REAL3D_ORDER_LR));
        SwitchViewMode();

    }
	
	public CamPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
		// TODO Auto-generated constructor stub
	}

    public CamPreview(Context context, boolean is3d) {
        super(context);
        this.is3d = true;
        init(context);
        // TODO Auto-generated constructor stub
    }
	
	public CamPreview(Context context) {
		super(context);
		init(context);
		// TODO Auto-generated constructor stub
	}

    public  void SwitchViewMode()
    {

        if (preferences.getString(CameraManager.SwitchCamera, CameraManager.SwitchCamera_MODE_3D) == CameraManager.SwitchCamera_MODE_3D)
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
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            float x = event.getX();
            float y = event.getY();
            float touchMajor = event.getTouchMajor();
            float touchMinor = event.getTouchMinor();


            Rect touchRect = new Rect(
                    (int)(x - touchMajor/2),
                    (int)(y - touchMinor/2),
                    (int)(x + touchMajor/2),
                    (int)(y + touchMinor/2));


            camMan.SetTouchFocus(touchRect);
        }
        return true;
    }

    public void SetCameraManager(CameraManager cameraManager)
    {
        this.camMan = cameraManager;
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }
}
