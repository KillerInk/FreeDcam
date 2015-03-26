package com.troop.freedcam.themenubia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.troop.freedcam.themenubia.manual.NubiaManualMenuFragment;
import com.troop.freedcam.themenubia.menu.MenuFragmentNubia;
import com.troop.freedcam.themenubia.shutter.ShutterItemFragmentNubia;
import com.troop.freedcam.ui.menu.themes.classic.ClassicUi;

/**
 * Created by troop on 26.03.2015.
 */
public class NubiaUi extends ClassicUi
{
    protected ImageView leftview;
    protected ImageView rightview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        leftview = (ImageView)view.findViewById(R.id.imageViewLeft);
        rightview = (ImageView)view.findViewById(R.id.imageViewRight);
        rightview.setVisibility(View.VISIBLE);
        rightview.setImageDrawable(getResources().getDrawable(R.drawable.nubia_ui_right_bg));
        leftview.setVisibility(View.VISIBLE);
        leftview.setImageDrawable(getResources().getDrawable(R.drawable.nubia_ui_left_bg));
        LinearLayout.LayoutParams Rparams = new LinearLayout.LayoutParams(i_activity.GetPreviewWidth()-i_activity.GetPreviewRightMargine(), i_activity.GetPreviewHeight());
        rightview.setLayoutParams(Rparams);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(i_activity.GetPreviewLeftMargine(),i_activity.GetPreviewHeight());
        leftview.setLayoutParams(Rparams);
        return view;
    }

    @Override
    protected void inflate(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.nubiaui, container, false);
        shutterItemsFragment = new ShutterItemFragmentNubia();
        menuFragment = new MenuFragmentNubia();
        manualMenuFragment = new NubiaManualMenuFragment();

    }
}
