package com.troop.freedcam.ui.TextureView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.ExtendedSurfaceView;
import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;
import com.troop.freedcam.utils.BitmapUtil;

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
    ImageView leftview;
    ImageView rightview;

    Bitmap AmbientCoverSML;
    Bitmap TMPBMP;
    public Bitmap AmbientCover;
    public MainActivity_v2 activity;

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
        leftview = (ImageView)activity.findViewById(R.id.imageViewLeft);
        rightview = (ImageView)activity.findViewById(R.id.imageViewRight);
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

    public int getMargineRight()
    {
        if (surfaceView != null)
            return surfaceView.getRight();
        else if (textureView != null)
            return textureView.getRight();
        else
            return 0;
    }

    public  void SwitchTheme()
    {
        String theme = appSettingsManager.GetTheme();

        LinearLayout.LayoutParams Rparams = new LinearLayout.LayoutParams(getWidth()-getMargineRight(), getHeight());
        rightview.setLayoutParams(Rparams);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getMargineLeft(), getWidth());
        leftview.setLayoutParams(Rparams);

        if (!theme.equals("Ambient"))
        {
            if (Build.VERSION.SDK_INT < 16)
                PreviewHandler.this.setBackgroundDrawable(null);
            else
                PreviewHandler.this.setBackground(null);
        }


        switch (theme) {
            case "Minimal":

                rightview.setVisibility(View.VISIBLE);

                rightview.post(new Runnable() {
                    @Override
                    public void run() {
                        rightview.setImageDrawable(getResources().getDrawable(R.drawable.minimal_ui_right_bg));
                        leftview.setVisibility(View.VISIBLE);
                        leftview.setImageDrawable(getResources().getDrawable(R.drawable.minimal_ui_left_bg));
                    }
                });

                System.out.println("Snoop" + " " + theme);
                break;
            case "Nubia":
                rightview.setVisibility(View.VISIBLE);
                rightview.setImageDrawable(getResources().getDrawable(R.drawable.nubia_ui_right_bg));
                leftview.setVisibility(View.VISIBLE);
                leftview.setImageDrawable(getResources().getDrawable(R.drawable.nubia_ui_left_bg));
                System.out.println("Snoop" + " " + theme);
                break;
            case "Material":
                rightview.setVisibility(View.VISIBLE);
                rightview.setImageDrawable(null);
                rightview.setBackgroundColor(Color.argb(130, 50, 50, 50));
                leftview.setVisibility(View.VISIBLE);
                leftview.setImageDrawable(null);
                leftview.setBackgroundColor(Color.argb(130, 50, 50, 50));
                System.out.println("Snoop" + " " + theme);
                break;
            case "Ambient":
                rightview.setVisibility(View.GONE);
                leftview.setVisibility(GONE);
                if (getWidth() == 0 || getHeight() == 0)
                    return;

                TMPBMP = BitmapUtil.RotateBitmap(BitmapUtil.getWallpaperBitmap(context), -90f, getWidth(), getHeight());
                BitmapUtil.initBlur(context, TMPBMP);
                AmbientCoverSML = TMPBMP;
                BitmapUtil.doGausianBlur(AmbientCoverSML, TMPBMP, 16f);
                AmbientCover = BitmapUtil.ScaleUP(AmbientCoverSML,getWidth(),getHeight());
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT < 16)
                            PreviewHandler.this.setBackgroundDrawable(new BitmapDrawable(AmbientCover));
                        else
                            PreviewHandler.this.setBackground(new BitmapDrawable(getResources(), AmbientCover));

                    }
                });

                break;
            default:
                rightview.setVisibility(View.INVISIBLE);

                break;
        }
        System.out.println("Snoop" +" "+theme);


    }

    public void setLalphas()
    {
        String theme = appSettingsManager.GetTheme();
        String module = appSettingsManager.getString(AppSettingsManager.SETTING_CURRENTMODULE);

        switch (theme) {
            case "Minimal":
                if (module.equals("module_video")||module.equals("module_hdr")) {
                    leftview.setAlpha(0.2f);
                } else {
                    leftview.setAlpha(0.8f);
                }


                break;
            case "Nubia":
                if (module.equals("module_video")||module.equals("module_hdr")) {
                    leftview.setAlpha(0.2f);
                } else {
                    leftview.setAlpha(0.8f);
                }

                break;
            case "Material":

                break;
            case "Ambient":
                if (module.equals("module_video")||module.equals("module_hdr")) {
                    leftview.setAlpha(0.2f);
                } else {
                    leftview.setAlpha(1.0f);
                }

                break;
        }
    }

    public void setRalphas()
    {
        String theme = appSettingsManager.GetTheme();

        String module = appSettingsManager.getString(AppSettingsManager.SETTING_CURRENTMODULE);

        switch (theme) {
            case "Minimal":
                if (module.equals("module_video")||module.equals("module_hdr")) {
                    rightview.setAlpha(0.2f);
                } else {
                    rightview.setAlpha(0.5f);
                }
                break;
            case "Nubia":
                if (module.equals("module_video")||module.equals("module_hdr")) {
                    rightview.setAlpha(0.2f);
                } else {
                    rightview.setAlpha(0.3f);
                }

                break;
            case "Material":


                break;
            case "Ambient":
                if (module.equals("module_video")||module.equals("module_hdr")) {
                    rightview.setAlpha(0.2f);
                } else {
                    rightview.setAlpha(1.0f);
                }

                break;
        }

    }


}
