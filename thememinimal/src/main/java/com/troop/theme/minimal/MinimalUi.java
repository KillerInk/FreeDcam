package com.troop.theme.minimal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.troop.freedcam.themenubia.NubiaUi;
import com.troop.freedcam.themenubia.manual.NubiaManualMenuFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.theme.minimal.menu.MenuFragmentMinimal;
import com.troop.theme.minimal.shutter.ShutterItemFragmentMinimal;

/**
 * Created by troop on 26.03.2015.
 */
public class MinimalUi extends NubiaUi
{
    public MinimalUi() {
    }

    @Override
    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity iActivity) {
        super.SetStuff(appSettingsManager, iActivity);
        shutterItemsFragment = new ShutterItemFragmentMinimal();
        menuFragment = new MenuFragmentMinimal(appSettingsManager, i_activity);
        manualMenuFragment = new NubiaManualMenuFragment();
    }

    @Override
    protected void inflate(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.nubiaui, container, false);

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
