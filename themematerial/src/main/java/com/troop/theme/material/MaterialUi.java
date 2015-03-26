package com.troop.theme.material;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.troop.freedcam.themenubia.NubiaUi;
import com.troop.freedcam.themenubia.manual.NubiaManualMenuFragment;
import com.troop.freedcam.ui.menu.themes.classic.ClassicUi;
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
        rightview.setImageDrawable(null);
        rightview.setBackgroundColor(Color.argb(130, 50, 50, 50));
        leftview.setVisibility(View.VISIBLE);
        leftview.setImageDrawable(null);
        leftview.setBackgroundColor(Color.argb(130, 50, 50, 50));
        leftview.setAlpha(0.2f);
        OnPreviewSizeChanged(0,0);
    }
}
