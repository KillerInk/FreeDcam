package com.troop.theme.minimal;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.troop.freedcam.themenubia.manual.NubiaManualMenuFragment;
import com.troop.freedcam.ui.menu.themes.classic.ClassicUi;
import com.troop.theme.minimal.menu.MenuFragmentMinimal;
import com.troop.theme.minimal.shutter.ShutterItemFragmentMinimal;

/**
 * Created by troop on 26.03.2015.
 */
public class MinimalUi extends ClassicUi
{
    @Override
    protected void inflate(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.nubiaui, container, false);
        shutterItemsFragment = new ShutterItemFragmentMinimal();
        menuFragment = new MenuFragmentMinimal();
        manualMenuFragment = new NubiaManualMenuFragment();
    }
}
