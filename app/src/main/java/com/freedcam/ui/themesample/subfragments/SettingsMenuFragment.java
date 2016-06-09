/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.ui.themesample.subfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.ui.AbstractFragment;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.themesample.subfragments.Interfaces.I_CloseNotice;
import com.freedcam.ui.themesample.subfragments.Interfaces.I_MenuItemClick;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChild;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.troop.freedcam.R.anim;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

/**
 * Created by troop on 14.06.2015.
 */
public class SettingsMenuFragment extends AbstractFragment implements I_CloseNotice, I_MenuItemClick
{
    private final String TAG = SettingsMenuFragment.class.getSimpleName();
    private LeftMenuFragment leftMenuFragment;
    private RightMenuFragment rightMenuFragment;
    private ValuesMenuFragment valuesMenuFragment;


    private final int VALUE_MENU_CLOSED = 0;
    private final int VALUE_MENU_RIGHT_OPEN = 1;
    private final int VALUE_MENU_LEFT_OPEN = 2;
    private int value_menu_status = VALUE_MENU_CLOSED;

    private UiSettingsChild currentOpendItem;

    public static SettingsMenuFragment GetInstance(I_Activity i_activity, AppSettingsManager appSettingsManager)
    {
        SettingsMenuFragment settingsMenuFragment = new SettingsMenuFragment();
        settingsMenuFragment.i_activity = i_activity;
        settingsMenuFragment.appSettingsManager = appSettingsManager;
        return settingsMenuFragment;
    }

    @Override
    public void SetCameraUIWrapper(I_CameraUiWrapper wrapper)
    {
        super.SetCameraUIWrapper(wrapper);
        Logger.d(TAG, "SetCameraUiWrapper");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(layout.settingsmenufragment, container, false);
        loadLeftFragment();
        loadRightFragment();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        setCameraUiWrapperToUi();
    }

    @Override
    protected void setCameraUiWrapperToUi()
    {
        Logger.d(TAG, "set CameraWrapper");
        if (value_menu_status != VALUE_MENU_CLOSED)
            closeValueMenu();
        if (rightMenuFragment != null)
            rightMenuFragment.SetCameraUIWrapper(cameraUiWrapper);
        if (leftMenuFragment != null)
            leftMenuFragment.SetCameraUIWrapper(cameraUiWrapper);
        value_menu_status = VALUE_MENU_CLOSED;
    }

    private void loadLeftFragment()
    {
        if (leftMenuFragment == null) {
            leftMenuFragment = LeftMenuFragment.GetInstance(i_activity,appSettingsManager);
            leftMenuFragment.SetMenuItemClickListner(this);
        }
        try {
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.empty, anim.empty);
            transaction.replace(id.left_holder, leftMenuFragment);
            transaction.commitAllowingStateLoss();
        }catch (NullPointerException | IllegalStateException ex)
        {
            Logger.exception(ex);
        }
    }
    private void loadRightFragment()
    {
        if (rightMenuFragment == null)
        {
            rightMenuFragment = RightMenuFragment.GetInstance(i_activity,appSettingsManager);
            rightMenuFragment.SetMenuItemClickListner(this);
        }
        try {
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.empty, anim.empty);
            transaction.replace(id.right_holder, rightMenuFragment);
            transaction.commitAllowingStateLoss();
        }
        catch (NullPointerException | IllegalStateException ex)
        {
            Logger.exception(ex);
        }
    }


    @Override
    public void onClose(String value)
    {
        currentOpendItem.SetValue(value);
        closeValueMenu();
    }

    private void closeValueMenu()
    {
        if (value_menu_status == VALUE_MENU_LEFT_OPEN) {
            loadLeftFragment();
        } else if (value_menu_status == VALUE_MENU_RIGHT_OPEN) {
            loadRightFragment();
        }
        currentOpendItem = null;
        value_menu_status = VALUE_MENU_CLOSED;
    }

    @Override
    public void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment)
    {
        if (currentOpendItem == item)
        {
            closeValueMenu();
            return;
        }
        currentOpendItem = item;

        valuesMenuFragment = new ValuesMenuFragment();
        if (item.GetValues() == null) {
            item.onIsSupportedChanged(false);
            value_menu_status = VALUE_MENU_CLOSED;
            if (fromLeftFragment)
                loadRightFragment();
            else
                loadLeftFragment();
            return;
        }
        valuesMenuFragment.SetMenuItem(item.GetValues(), this);

        if (fromLeftFragment)
        {
            value_menu_status = VALUE_MENU_RIGHT_OPEN;
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.left_to_right_enter, anim.left_to_right_exit);
            transaction.replace(id.right_holder, valuesMenuFragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
        else
        {
            value_menu_status = VALUE_MENU_LEFT_OPEN;
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.right_to_left_enter, anim.right_to_left_exit);
            transaction.replace(id.left_holder, valuesMenuFragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }
}
