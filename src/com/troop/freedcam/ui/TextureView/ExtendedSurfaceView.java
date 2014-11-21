package com.troop.freedcam.ui.TextureView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.lge.real3d.Real3D;
import com.lge.real3d.Real3DInfo;

import com.troop.freedcam.camera.modules.I_ModuleEvent;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;

import java.util.List;

/**
 * Created by troop on 21.08.2014.
 */
public class ExtendedSurfaceView extends SurfaceView implements I_ParametersLoaded
{
    boolean hasReal3d = false;
    boolean hasOpenSense = false;
    final String TAG = "freedcam.ExtendedTextureView";
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
            if (preferences.getInt(com.troop.freedcam.ui.AppSettingsManager.SETTING_CURRENTCAMERA, 0) == 2)
                mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_SS, Real3D.REAL3D_ORDER_LR));
            else
                mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_NONE, 0));
        }
    }





    @Override
    public void ParametersLoaded()
    {
        /*String previewsize = "";
        if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_PICTURE)
                || appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_HDR) )
            previewsize = ParametersHandler.PictureSize.GetValue();
        if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_LONGEXPO))
            previewsize = ParametersHandler.PreviewSize.GetValue();
        setPreviewSize(previewsize);*/
    }





}
