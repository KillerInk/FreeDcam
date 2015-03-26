package com.troop.theme.minimal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.troop.freedcam.themenubia.NubiaUi;
import com.troop.freedcam.themenubia.manual.NubiaManualMenuFragment;
import com.troop.freedcam.ui.menu.themes.classic.ClassicUi;
import com.troop.theme.minimal.menu.MenuFragmentMinimal;
import com.troop.theme.minimal.shutter.ShutterItemFragmentMinimal;

/**
 * Created by troop on 26.03.2015.
 */
public class MinimalUi extends NubiaUi
{
    @Override
    protected void inflate(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.nubiaui, container, false);
        shutterItemsFragment = new ShutterItemFragmentMinimal();
        menuFragment = new MenuFragmentMinimal();
        manualMenuFragment = new NubiaManualMenuFragment();
        leftview = (ImageView)view.findViewById(R.id.imageViewLeft);
        rightview = (ImageView)view.findViewById(R.id.imageViewRight);
        rightview.setVisibility(View.VISIBLE);
        rightview.setImageDrawable(getResources().getDrawable(R.drawable.minimal_ui_right_bg));
        leftview.setVisibility(View.VISIBLE);
        leftview.setImageDrawable(getResources().getDrawable(R.drawable.minimal_ui_left_bg));
        leftview.setAlpha(0.2f);
        OnPreviewSizeChanged(0,0);
    }
}
