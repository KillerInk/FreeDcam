package com.troop.theme.material;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.troop.freedcam.themenubia.manual.NubiaManualMenuFragment;
import com.troop.freedcam.ui.menu.themes.classic.ClassicUi;
import com.troop.theme.material.menu.MenuFragmentMaterial;
import com.troop.theme.material.shutter.ShutterItemFragmentMaterial;

/**
 * Created by troop on 26.03.2015.
 */
public class MaterialUi extends ClassicUi
{
    @Override
    protected void inflate(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.nubiaui, container, false);
        shutterItemsFragment = new ShutterItemFragmentMaterial();
        menuFragment = new MenuFragmentMaterial();
        manualMenuFragment = new NubiaManualMenuFragment();
    }
}
