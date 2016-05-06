package com.freedcam.ui.themesample.subfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.ui.AbstractFragment;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.I_Activity;
import com.freedcam.ui.themesample.views.menu.MenuItemTheme;
import com.freedcam.ui.themesample.views.uichilds.UiSettingsChild;
import com.freedcam.utils.Logger;
import com.troop.freedcam.R;

/**
 * Created by troop on 14.06.2015.
 */
public class SettingsMenuFragment extends AbstractFragment implements Interfaces.I_CloseNotice, Interfaces.I_MenuItemClick
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
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper)
    {
        super.SetCameraUIWrapper(wrapper);
        Logger.d(TAG, "SetCameraUiWrapper");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.settingsmenufragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        loadLeftFragment();
        loadRightFragment();
        Logger.d(TAG,"onviewCreated");
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
            transaction.setCustomAnimations(R.anim.empty, R.anim.empty);
            transaction.replace(R.id.left_holder, leftMenuFragment);
            transaction.commitAllowingStateLoss();
        }catch (NullPointerException | IllegalStateException ex)
        {}
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
            transaction.setCustomAnimations(R.anim.empty, R.anim.empty);
            transaction.replace(R.id.right_holder, rightMenuFragment);
            transaction.commitAllowingStateLoss();
        }
        catch (NullPointerException | IllegalStateException ex)
        {}
    }


    @Override
    public void onClose(String value)
    {
        currentOpendItem.SetValue(value);
        if (currentOpendItem instanceof MenuItemTheme)
            return;
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
            transaction.setCustomAnimations(R.anim.left_to_right_enter, R.anim.left_to_right_exit);
            transaction.replace(R.id.right_holder, valuesMenuFragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
        else
        {
            value_menu_status = VALUE_MENU_LEFT_OPEN;
            android.support.v4.app.FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
            transaction.replace(R.id.left_holder, valuesMenuFragment);
            transaction.addToBackStack(null);
            transaction.commitAllowingStateLoss();
        }
    }
}
