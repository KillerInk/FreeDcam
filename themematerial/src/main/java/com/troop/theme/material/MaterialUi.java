package com.troop.theme.material;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.troop.freedcam.themenubia.NubiaUi;
import com.troop.freedcam.themenubia.manual.NubiaManualMenuFragment;
import com.troop.theme.material.menu.MenuFragmentMaterial;
import com.troop.theme.material.shutter.ShutterItemFragmentMaterial;

/**
 * Created by troop on 26.03.2015.
 */
public class MaterialUi extends NubiaUi
{
    @Override
    protected void inflate(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.nubiaui, container, false);
        shutterItemsFragment = new ShutterItemFragmentMaterial();
        menuFragment = new MenuFragmentMaterial();
        manualMenuFragment = new NubiaManualMenuFragment();
        leftview = (ImageView)view.findViewById(R.id.imageViewLeft);
        rightview = (ImageView)view.findViewById(R.id.imageViewRight);

        rightview.setVisibility(View.VISIBLE);
        leftview.setVisibility(View.VISIBLE);

        i_activity.GetSurfaceView().post(new Runnable() {
            @Override
            public void run() {
                rightview.setImageBitmap(colorBitmap(130,50,50,50));
                leftview.setImageBitmap(colorBitmap(130,50,50,50));
            }
        });
        OnPreviewSizeChanged(0, 0);
    }

    private Bitmap colorBitmap(int a,int r,int g,int b )
    {
        int[] size = i_activity.GetScreenSize();

        int width = (size[0] -  i_activity.GetSurfaceView().getWidth())/2;

        System.out.println("Skreen Width"+width);

        Bitmap img = Bitmap.createBitmap(width,size[1], Bitmap.Config.ARGB_4444);
        img.eraseColor(Color.argb(a,r,g,b));

        return img;
    }
}
