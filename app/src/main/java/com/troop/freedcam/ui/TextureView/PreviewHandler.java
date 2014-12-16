package com.troop.freedcam.ui.TextureView;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.troop.freedcam.camera.modules.I_ModuleEvent;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.modes.PreviewSizeParameter;
import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.List;

/**
 * Created by troop on 21.11.2014.
 */
public class PreviewHandler extends RelativeLayout
{
    public TextureView textureView;
    public SurfaceView surfaceView;
    public
    Context context;
    public com.troop.freedcam.ui.AppSettingsManager appSettingsManager;

    public PreviewHandler(Context context) {
        super(context);
        init(context);
    }



    public PreviewHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PreviewHandler(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;

    }

    public void Init()
    {
        this.removeAllViews();
        if (appSettingsManager.getSonyCam())
        {
            surfaceView = new SimpleStreamSurfaceView(context);
            this.addView(surfaceView);
        }
        else /*if (Build.VERSION.SDK_INT < 21)*/
        {
            surfaceView = new ExtendedSurfaceView(context);
            this.addView(surfaceView);

        }
        /*else
        {
            textureView = new AutoFitTextureView(context);
            this.addView(textureView);
        }*/
    }

    public void SetAppSettingsAndTouch(AppSettingsManager appSettingsManager, View.OnTouchListener surfaceTouche)
    {
        if (appSettingsManager.getSonyCam())
        {
            SimpleStreamSurfaceView simplesurfaceView = (SimpleStreamSurfaceView)surfaceView;
            simplesurfaceView.setOnTouchListener(surfaceTouche);
        }
        /*else if (Build.VERSION.SDK_INT < 21)
        {
            textureView.setOnTouchListener(surfaceTouche);
        }*/
        else
        {
            ExtendedSurfaceView extendedSurfaceView = (ExtendedSurfaceView)surfaceView;
            extendedSurfaceView.appSettingsManager = appSettingsManager;
            extendedSurfaceView.setOnTouchListener(surfaceTouche);
        }
    }


}
