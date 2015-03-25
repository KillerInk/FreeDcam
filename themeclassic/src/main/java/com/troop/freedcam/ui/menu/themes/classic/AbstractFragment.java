package com.troop.freedcam.ui.menu.themes.classic;

import android.support.v4.app.Fragment;

import com.troop.freedcam.ui.menu.themes.classic.shutter.ShutterHandler;

/**
 * Created by troop on 25.03.2015.
 */
public class AbstractFragment extends Fragment
{
    public void inflateShutterItemFragment(){}
    public void inflateMenuFragment(){};
    public void inflateManualMenuFragment(){}
    public void deflateMenuFragment(){}
    public void deflateManualMenuFragment(){}
    public ShutterHandler getShutterHandler(){return null;}
}
