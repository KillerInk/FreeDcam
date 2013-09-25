package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;

/**
 * Created by troop on 08.09.13.
 */
public class IppMenu extends  BaseMenu
{
    public IppMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String[] ipp;
    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
            ipp = camMan.parameters.get("ipp-values").split(",");
        PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
        //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
        for (int i = 0; i < ipp.length; i++) {
            popupMenu.getMenu().add((CharSequence) ipp[i]);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String tmp = item.toString();
                camMan.parameters.set("ipp", tmp);
                String camvalue = preferences.getString(CameraManager.SwitchCamera, CameraManager.SwitchCamera_MODE_3D);
                if (camvalue.equals(CameraManager.SwitchCamera_MODE_3D))
                    preferences.edit().putString(CameraManager.Preferences_IPP3D, tmp).commit();
                if (camvalue.equals(CameraManager.SwitchCamera_MODE_2D))
                    preferences.edit().putString(CameraManager.Preferences_IPP2D, tmp).commit();
                if (camvalue.equals(CameraManager.SwitchCamera_MODE_Front))
                    preferences.edit().putString(CameraManager.Preferences_IPPFront, tmp).commit();
                //preferences.edit().putString("color", tmp).commit();
                camMan.Restart(false);

                return true;
            }
        });

        popupMenu.show();
    }
}
