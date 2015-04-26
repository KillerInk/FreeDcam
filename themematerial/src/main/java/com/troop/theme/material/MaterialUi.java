package com.troop.theme.material;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.troop.freedcam.themenubia.NubiaUi;
import com.troop.freedcam.themenubia.manual.NubiaManualMenuFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.theme.material.menu.MenuFragmentMaterial;
import com.troop.theme.material.shutter.ShutterItemFragmentMaterial;

/**
 * Created by troop on 26.03.2015.
 */
public class MaterialUi extends NubiaUi
{
    public MaterialUi(AppSettingsManager appSettingsManager, I_Activity iActivity) {
        super(appSettingsManager, iActivity);
        shutterItemsFragment = new ShutterItemFragmentMaterial();
        menuFragment = new MenuFragmentMaterial(appSettingsManager, i_activity);
        manualMenuFragment = new NubiaManualMenuFragment();
    }

    @Override
    protected void inflate(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.nubiaui, container, false);

        leftview = (ImageView)view.findViewById(R.id.imageViewLeft);
        rightview = (ImageView)view.findViewById(R.id.imageViewRight);

        rightview.setVisibility(View.VISIBLE);
        leftview.setVisibility(View.VISIBLE);

        rightview.post(new Runnable() {
            @Override
            public void run() {
                try {
                    rightview.setImageBitmap(colorBitmap(130,50,50,50));
                    leftview.setImageBitmap(colorBitmap(130,50,50,50));
                }
                catch (IllegalArgumentException ex)
                {
                    ex.printStackTrace();
                }

            }
        });
        OnPreviewSizeChanged(0, 0);
    }

    private Bitmap colorBitmap(int a,int r,int g,int b )
    {
        int[] size = i_activity.GetScreenSize();

        int width = (size[0] -  i_activity.GetPreviewWidth())/2;

        System.out.println("Skreen Width"+width);

        Bitmap img = Bitmap.createBitmap(width,size[1], Bitmap.Config.ARGB_4444);
        img.eraseColor(Color.argb(a,r,g,b));

        return img;
    }
}
