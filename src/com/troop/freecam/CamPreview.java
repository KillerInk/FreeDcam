package com.troop.freecam;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.lge.real3d.Real3D;
import com.lge.real3d.Real3DInfo;
import com.troop.freecam.manager.Drawing.SizeAbleRectangle;

import java.util.List;

public class CamPreview extends SurfaceView implements SurfaceHolder.Callback  {

	SurfaceHolder mHolder;
    SurfaceHolder canvasHolder;
    private Real3D mReal3D;
    private CameraManager camMan;
    public SharedPreferences preferences;
    boolean is3d = false;
    Paint mPaint;
    public SizeAbleRectangle drawingRectHelper;


    private void init(Context context) {


        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mHolder = getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        //canvasHolder = getHolder();
        //canvasHolder.addCallback(this);
        mHolder.addCallback(this);
        // Initialize Real3D object
        mReal3D = new Real3D(mHolder);
        drawingRectHelper = new SizeAbleRectangle(this);
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
    public boolean onTouchEvent(MotionEvent event)
    {


        drawingRectHelper.OnTouch(event);
        return true;
    }



    public void SetCameraManager(CameraManager cameraManager)
    {
        this.camMan = cameraManager;
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        drawingRectHelper.Draw(canvas);
        //super.onDraw(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setWillNotDraw(false);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
