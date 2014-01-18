package com.troop.freecam.surfaces;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.RelativeLayout;

import com.lge.real3d.Real3D;
import com.lge.real3d.Real3DInfo;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.manager.SettingsManager;

public class CamPreview extends BasePreview {

	public SurfaceHolder mHolder;
    SurfaceHolder canvasHolder;

    Real3D mReal3D;
    private CameraManager camMan;
    public SharedPreferences preferences;
    boolean is3d = false;


    public int canvasWidth;
    public int canvasHeight;



    private void init(Context context)
    {
        try {



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
        if (hasReal3d)
        {
            //dont get the preferences from the SettingManager, its not init at this time
            if (preferences.getString(SettingsManager.Preferences.SwitchCamera, SettingsManager.Preferences.MODE_Front).equals(SettingsManager.Preferences.MODE_3D))
            {
                if(preferences.getBoolean("upsidedown", false) == false)
                    mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_SS, Real3D.REAL3D_ORDER_LR));
                else
                    mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_SS, Real3D.REAL3D_ORDER_RL));

            }
            else
            {
                mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_NONE, 0));
            }
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
        camMan.parametersManager.setPreviewSizeCHanged = this;
    }

    //NOTE this is called each time when the preview size changes
    @Override
    public void onPreviewsizeHasChanged(int w, int h) {
        super.onPreviewsizeHasChanged(w, h);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        double ratio = (double)w/h;
        double displayratio = metrics.widthPixels/metrics.heightPixels;
        //if rato 16/9:10
        if (ratio == 1.7777777777777777 || ratio == 1.6666666666666667)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.leftMargin = 0;
            this.setLayoutParams(layoutParams);
        }
        else
        {
            //TODO calculate 4:3 from screen size
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(metrics.widthPixels - 100, metrics.heightPixels);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.leftMargin = 50;
            this.setLayoutParams(layoutParams);
        }


        //1920x1080 = 1280x720 = 1.7777777777777777
        //960x720 = 640x480 = 1.3333333333333333
        //800x480 = 1.6666666666666667;
        //720x576 = 1.25
        //720x480 = 1.5

    }
}
