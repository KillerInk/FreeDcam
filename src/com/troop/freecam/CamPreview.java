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

public class CamPreview extends SurfaceView  {

	public SurfaceHolder mHolder;
    SurfaceHolder canvasHolder;
    private Real3D mReal3D;
    private CameraManager camMan;
    public SharedPreferences preferences;
    boolean is3d = false;

    public int canvasWidth;
    public int canvasHeight;


    private void init(Context context) {

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mHolder = getHolder();
        mReal3D = new Real3D(mHolder);
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
            mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_SS, Real3D.REAL3D_ORDER_LR));
        }
        else
        {
            mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_NONE, 0));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //drawingRectHelper.OnTouch(event);
        return true;
    }

    public void SetCameraManager(CameraManager cameraManager)
    {
        this.camMan = cameraManager;
    }

}
