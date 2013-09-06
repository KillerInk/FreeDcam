package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.R;

import java.util.List;

/**
 * Created by troop on 31.08.13.
 */
public class ColorMenu extends BaseMenu {

    List<String> colors;
    public ColorMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
            colors = camMan.parameters.getSupportedColorEffects();
        PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
        //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
        for (int i = 0; i < colors.size(); i++) {
            popupMenu.getMenu().add((CharSequence) colors.get(i));
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String tmp = item.toString();
                camMan.parameters.setColorEffect(tmp);
                String camvalue = preferences.getString(CameraManager.SwitchCamera, CameraManager.SwitchCamera_MODE_3D);
                if (camvalue == CameraManager.SwitchCamera_MODE_3D)
                {
                    preferences.edit().putString(CameraManager.Preferences_Color3D, tmp).commit();
                }
                if (camvalue == CameraManager.SwitchCamera_MODE_2D)
                    preferences.edit().putString(CameraManager.Preferences_Color2D, tmp).commit();
                if (camvalue == CameraManager.SwitchCamera_MODE_Front)
                    preferences.edit().putString(CameraManager.Preferences_ColorFront, tmp).commit();
                //preferences.edit().putString("color", tmp).commit();
                camMan.Restart(false);

                return true;
            }
        });

        popupMenu.show();
    }
}
