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

package freed.cam.ui.themesample.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.R.anim;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.modes.OpCodeParameter;
import freed.cam.ui.themesample.AbstractFragment;
import freed.cam.ui.themesample.SettingsChildAbstract.CloseChildClick;
import freed.cam.ui.themesample.SettingsChildAbstract.SettingsChildClick;
import freed.cam.ui.themesample.cameraui.childs.UiSettingsChild;
import freed.cam.ui.themesample.settings.opcode.OpCodeFragment;
import freed.utils.Log;

/**
 * Created by troop on 14.06.2015.
 */
public class SettingsMenuFragment extends AbstractFragment implements CloseChildClick, SettingsChildClick
{
    private final String TAG = SettingsMenuFragment.class.getSimpleName();
    private LeftMenuFragment leftMenuFragment;
    private RightMenuFragment rightMenuFragment;


    private final int VALUE_MENU_CLOSED = 0;
    private final int VALUE_MENU_RIGHT_OPEN = 1;
    private final int VALUE_MENU_LEFT_OPEN = 2;
    private int value_menu_status = VALUE_MENU_CLOSED;

    private UiSettingsChild currentOpendItem;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);

        return inflater.inflate(layout.settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadLeftFragment();
        loadRightFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        setCameraToUi(cameraUiWrapper);
    }

    @Override
    public void setCameraToUi(CameraWrapperInterface wrapper)
    {
        super.setCameraToUi(wrapper);
        Log.d(TAG, "SetCameraUiWrapper");
        if (value_menu_status != VALUE_MENU_CLOSED)
            closeValueMenu();
        if (rightMenuFragment != null)
            rightMenuFragment.setCameraToUi(cameraUiWrapper);
        if (leftMenuFragment != null)
            leftMenuFragment.setCameraToUi(cameraUiWrapper);
        value_menu_status = VALUE_MENU_CLOSED;
    }

    private void loadLeftFragment()
    {
        if (leftMenuFragment == null) {
            leftMenuFragment = new LeftMenuFragment();
            leftMenuFragment.SetMenuItemClickListner(this);
        }
        try {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.left_to_right_enter, anim.left_to_right_exit);
            transaction.replace(id.left_holder, leftMenuFragment);
            transaction.commit();
        }catch (NullPointerException | IllegalStateException ex)
        {
            Log.WriteEx(ex);
        }
    }
    private void loadRightFragment()
    {
        if (rightMenuFragment == null)
        {
            rightMenuFragment = new RightMenuFragment();
            rightMenuFragment.SetMenuItemClickListner(this);
        }
        try {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.right_to_left_enter, anim.right_to_left_exit);
            transaction.replace(id.right_holder, rightMenuFragment);
            transaction.commit();
        }
        catch (NullPointerException | IllegalStateException ex)
        {
            Log.WriteEx(ex);
        }
    }


    @Override
    public void onCloseClicked(String value)
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
    public void onSettingsChildClick(UiSettingsChild item, boolean fromLeftFragment)
    {
        if (currentOpendItem == item)
        {
            closeValueMenu();
            return;
        }
        currentOpendItem = item;


        boolean loadopCodeFragment = item.GetParameter() instanceof OpCodeParameter;
        if (loadopCodeFragment){
            value_menu_status = VALUE_MENU_RIGHT_OPEN;
            OpCodeFragment opCodeFragment = new OpCodeFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.right_to_left_enter, anim.right_to_left_exit);
            transaction.replace(id.right_holder, opCodeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else {

            ValuesMenuFragment valuesMenuFragment = new ValuesMenuFragment();
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

            if (fromLeftFragment) {
                value_menu_status = VALUE_MENU_RIGHT_OPEN;
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setCustomAnimations(anim.left_to_right_enter, anim.left_to_right_exit);
                transaction.replace(id.right_holder, valuesMenuFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                value_menu_status = VALUE_MENU_LEFT_OPEN;
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.setCustomAnimations(anim.right_to_left_enter, anim.right_to_left_exit);
                transaction.replace(id.left_holder, valuesMenuFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }
}
