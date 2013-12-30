package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.manager.ParametersManager;
import com.troop.freecam.utils.DeviceUtils;

/**
 * Created by George on 12/7/13.
 */
public class ZslMenu extends BaseMenu  {
    public ZslMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String[] zslv;

    @Override
    public void onClick(View v)
    {

        if(camMan.Running)
        {
            try
            {
                zslv = camMan.parametersManager.ZSLModes.getValues();
            }
            catch (Exception ex)
            {

            }
        }
        if (zslv != null && !zslv.equals(""))
        {
            PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < zslv.length; i++) {
                popupMenu.getMenu().add((CharSequence) zslv[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    camMan.parametersManager.ZSLModes.setValue(tmp);
                    preferences.edit().putString(ParametersManager.Preferences_ZSL, tmp).commit();
                    //preferences.edit().putString("color", tmp).commit();
                    camMan.Restart(false);

                    return true;
                }
            });

            popupMenu.show();
        }
    }
}

