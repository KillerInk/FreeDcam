package com.troop.freedcamv2.ui.TextureView;

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

import com.troop.freedcamv2.camera.modules.I_ModuleEvent;
import com.troop.freedcamv2.camera.modules.ModuleHandler;
import com.troop.freedcamv2.camera.parameters.CamParametersHandler;
import com.troop.freedcamv2.camera.parameters.I_ParametersLoaded;
import com.troop.freedcamv2.ui.AppSettingsManager;

import java.util.List;

/**
 * Created by troop on 21.08.2014.
 */
public class ExtendedSurfaceView extends SurfaceView implements I_PreviewSizeEvent, I_ParametersLoaded, I_ModuleEvent
{
    boolean hasReal3d = false;
    boolean hasOpenSense = false;
    final String TAG = "freedcam.ExtendedTextureView";
    Context context;

    public SurfaceHolder mHolder;
    SharedPreferences preferences;

    Real3D mReal3D;

    public com.troop.freedcamv2.ui.AppSettingsManager appSettingsManager;
    public CamParametersHandler ParametersHandler;
    String currentModule;

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
            if (preferences.getInt(com.troop.freedcamv2.ui.AppSettingsManager.SETTING_CURRENTCAMERA, 0) == 2)
                mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_SS, Real3D.REAL3D_ORDER_LR));
            else
                mReal3D.setReal3DInfo(new Real3DInfo(true, Real3D.REAL3D_TYPE_NONE, 0));
        }
    }

    @Override
    public void OnPreviewSizeChanged(int w, int h)
    {
        if (currentModule.equals(""))
            currentModule = appSettingsManager.GetCurrentModule();
        if (currentModule.equals(ModuleHandler.MODULE_PICTURE)) {
            /*double pictureRatio = getRatio(w, h);
            Log.d(TAG, "New Picture size is set: Width: " + w + "Height : " + h + "Ratio:" + pictureRatio);
            String[] previewSizes = ParametersHandler.PreviewSize.GetValues();
            boolean foundmatchingPreview = false;
            for (int i = 0; i < previewSizes.length; i++) {
                String[] split = previewSizes[i].split("x");
                int pw = Integer.parseInt(split[0]);
                int ph = Integer.parseInt(split[1]);
                double previewRatio = getRatio(pw, ph);
                if (previewRatio == pictureRatio) {

                    Log.d(TAG, "Found matching preview size and set it to:" + previewSizes[i] + " Ratio:" + previewRatio);
                    ParametersHandler.PreviewSize.SetValue(previewSizes[i], true);
                    setPreviewToDisplay(pw, ph);
                    String msg = "PreviewSize:" + previewSizes[i] + " Ratio:" + previewRatio + "\nPictureSize:" + w + "x" + h + " Ratio:" + pictureRatio;
                    ParametersHandler.cameraHolder.errorHandler.OnError(msg);
                    foundmatchingPreview = true;
                    break;
                }
            }
            if (foundmatchingPreview == false) {
                Log.d(TAG, "Found no matching preview size, raw capture will fail");
                String msg = "Found no matching preview size, raw capture will fail";
                ParametersHandler.cameraHolder.errorHandler.OnError(msg);
            }
            int width = 0;
            int height = 0;
            if (Build.VERSION.SDK_INT >= 17)
            {
                WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
                Point size =  new Point();
                wm.getDefaultDisplay().getRealSize(size);
                width = size.x;
                height = size.y;
            }
            else
            {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                width = metrics.widthPixels;
                height = metrics.heightPixels;

            }*/

            Camera.Size size = getOptimalPreviewSize(ParametersHandler.PreviewSize.GetSizes(),w, h );
            ParametersHandler.PreviewSize.SetValue(size.width+"x"+size.height, true);
            setPreviewToDisplay(size.width, size.height);

        }
        else
        {
            ParametersHandler.PreviewSize.SetValue(w+"x"+h, true);
            setPreviewToDisplay(w, h);
        }

        //[1.00 = square] [1.25 = 5:4] [1.33 = 4:3] [1.50 = 3:2] [1.60 = 16:10] [1.67 = 5:3] [1.71 = 128:75] [1.78 = 16:9] [1.85] [2.33 = 21:9 (1792x768)] [2.35 = Cinamascope] [2.37 = "21:9" (2560x1080)] [2.39 = Panavision]


    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.2;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void ParametersLoaded()
    {
        String previewsize = "";
        if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_PICTURE)
                || appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_HDR) )
            previewsize = ParametersHandler.PictureSize.GetValue();
        if (appSettingsManager.GetCurrentModule().equals(ModuleHandler.MODULE_LONGEXPO))
            previewsize = ParametersHandler.PreviewSize.GetValue();
        setPreviewSize(previewsize);
    }

    public void setPreviewSize(String previewsize) {
        String[] split = previewsize.split("x");
        int w = Integer.parseInt(split[0]);
        int h = Integer.parseInt(split[1]);
        OnPreviewSizeChanged(w, h);
    }

    private double getRatio(int w, int h)
    {
        double newratio = (double)w/(double)h;
        newratio = Math.round(newratio*100.0)/100.0;
        return newratio;
    }

    private void setPreviewToDisplay(int w, int h)
    {
        double newratio = getRatio(w, h);
        int width = 0;
        int height = 0;

        if (Build.VERSION.SDK_INT >= 17)
        {
            WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            Point size =  new Point();
            wm.getDefaultDisplay().getRealSize(size);
            width = size.x;
            height = size.y;
        }
        else
        {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            width = metrics.widthPixels;
            height = metrics.heightPixels;

        }
        double displayratio = getRatio(width, height);

        if (newratio == displayratio)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.rightMargin = 0;
            layoutParams.leftMargin = 0;
            this.setLayoutParams(layoutParams);
        }
        else if (newratio == 1.33)
        {
            int tmo = (int)((double)width / displayratio * newratio);
            int newwidthdiff = width - tmo;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.rightMargin = newwidthdiff/2;
            layoutParams.leftMargin = newwidthdiff/2;
            this.setLayoutParams(layoutParams);
        }
        else
        {
            int tmo = (int)((double)width / displayratio * newratio);
            int newwidthdiff = width - tmo;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.rightMargin = newwidthdiff;
            this.setLayoutParams(layoutParams);
        }
    }


    @Override
    public String ModuleChanged(String module)
    {
        this.currentModule = module;
        if(module.equals(ModuleHandler.MODULE_PICTURE))
            setPreviewSize(ParametersHandler.PictureSize.GetValue());
        if (module.equals(ModuleHandler.MODULE_LONGEXPO))
            setPreviewSize(ParametersHandler.PreviewSize.GetValue());
        return null;
    }
}
