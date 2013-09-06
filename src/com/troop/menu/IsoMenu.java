package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;


/**
 * Created by troop on 02.09.13.
 */
public class IsoMenu extends BaseMenu {
    public IsoMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String[] isos;
    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
            isos = camMan.parameters.get("iso-mode-values").split(",");
        PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
        //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
        for (int i = 0; i < isos.length; i++) {
            popupMenu.getMenu().add((CharSequence) isos[i]);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String tmp = item.toString();
                camMan.parameters.set("iso", tmp);
                String camvalue = preferences.getString(CameraManager.SwitchCamera, CameraManager.SwitchCamera_MODE_3D);
                if (camvalue == CameraManager.SwitchCamera_MODE_3D)
                    preferences.edit().putString(CameraManager.Preferences_Iso3D, tmp).commit();
                if (camvalue == CameraManager.SwitchCamera_MODE_2D)
                    preferences.edit().putString(CameraManager.Preferences_Iso2D, tmp).commit();
                if (camvalue == CameraManager.SwitchCamera_MODE_Front)
                    preferences.edit().putString(CameraManager.Preferences_IsoFront, tmp).commit();
                //preferences.edit().putString("color", tmp).commit();
                camMan.Restart(false);

                return true;
            }
        });

        popupMenu.show();
    }
}
