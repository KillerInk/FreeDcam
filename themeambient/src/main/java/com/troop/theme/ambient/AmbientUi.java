package com.troop.theme.ambient;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.troop.freedcam.themenubia.NubiaUi;
import com.troop.freedcam.themenubia.manual.NubiaManualMenuFragment;
import com.troop.theme.ambient.menu.MenuFragmentAmbient;
import com.troop.theme.ambient.shutter.ShutterItemFragmentAmbient;

/**
 * Created by troop on 26.03.2015.
 */
public class AmbientUi extends NubiaUi
{

    Bitmap AmbientCoverSML;
    Bitmap TMPBMP;
    public Bitmap AmbientCover;
    @Override
    protected void inflate(LayoutInflater inflater, ViewGroup container)
    {
        view = inflater.inflate(R.layout.nubiaui, container, false);
        shutterItemsFragment = new ShutterItemFragmentAmbient();
        menuFragment = new MenuFragmentAmbient(this);

        manualMenuFragment = new NubiaManualMenuFragment();
       final int[] size = i_activity.GetScreenSize();
        TMPBMP = BitmapUtil.RotateBitmap(BitmapUtil.getWallpaperBitmap(view.getContext()), -90f, size[0], size[1]);
        BitmapUtil.initBlur(view.getContext(), TMPBMP);
        AmbientCoverSML = TMPBMP;
        BitmapUtil.doGausianBlur(AmbientCoverSML, TMPBMP, 16f);
        AmbientCover = BitmapUtil.ScaleUP(AmbientCoverSML,size[0], size[1]);
        leftview = (ImageView)view.findViewById(R.id.imageViewLeft);
        rightview = (ImageView)view.findViewById(R.id.imageViewRight);
        rightview.setVisibility(View.VISIBLE);
        leftview.setVisibility(View.VISIBLE);
        /*rightview.post(new Runnable() {
            @Override
            public void run() {
                OnPreviewSizeChanged(0,0);
            }
        });*/


    }

    boolean loadingPics = false;
    @Override
    public void OnPreviewSizeChanged(int w, int h)
    {
        super.OnPreviewSizeChanged(w,h);
        if (i_activity.GetPreviewLeftMargine() > 0 && !loadingPics)
        {
            leftview.post(new Runnable() {
                @Override
                public void run()
                {
                    loadingPics = true;
                    int[] size = i_activity.GetScreenSize();
                    leftview.setImageBitmap(Bitmap.createBitmap(AmbientCover, 0, 0, i_activity.GetPreviewLeftMargine(), i_activity.GetPreviewHeight()));
                    rightview.setImageBitmap(Bitmap.createBitmap(AmbientCover, i_activity.GetPreviewRightMargine(), 0, size[0] - i_activity.GetPreviewRightMargine(), size[1]));
                    loadingPics = false;
                }
            });

        }

    }

    private void hide() {
        leftview.setVisibility(View.GONE);
        rightview.setVisibility(View.GONE);
    }
    private void show() {
        leftview.setVisibility(View.GONE);
        rightview.setVisibility(View.GONE);
    }
}
