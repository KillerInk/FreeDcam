package com.troop.freedcam.ui.TextureView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.RelativeLayout;

import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.troop.freedcam.ui.AppSettingsManager;

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
        if (surfaceView != null)
        {
            surfaceView = null;
        }
        try {
            this.removeAllViews();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_SONY))
        {
            surfaceView = new SimpleStreamSurfaceView(context);
            this.addView(surfaceView);
        }
        else if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_1))
        {
            surfaceView = new ExtendedSurfaceView(context);
            this.addView(surfaceView);

        }
        else
        {
            textureView = new AutoFitTextureView(context);
            this.addView(textureView);
        }
    }

    public void SetAppSettingsAndTouch(AppSettingsManager appSettingsManager, View.OnTouchListener surfaceTouche)
    {
        if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_SONY))
        {
            SimpleStreamSurfaceView simplesurfaceView = (SimpleStreamSurfaceView)surfaceView;
            simplesurfaceView.setOnTouchListener(surfaceTouche);
        }
        else if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_1))
        {
            ExtendedSurfaceView extendedSurfaceView = (ExtendedSurfaceView)surfaceView;
            extendedSurfaceView.appSettingsManager = appSettingsManager;
            extendedSurfaceView.setOnTouchListener(surfaceTouche);
        }
        else
        {
            textureView.setOnTouchListener(surfaceTouche);

        }
    }

    public int getMargineLeft()
    {
        if (surfaceView != null)
            return surfaceView.getLeft();
        else if (textureView != null)
            return textureView.getLeft();
        else
            return 0;
    }


}
