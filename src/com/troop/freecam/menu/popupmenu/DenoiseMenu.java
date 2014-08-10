package com.troop.freecam.menu.popupmenu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.manager.camera_parameters.ParametersManager;

/**
 * Created by George on 12/7/13.
 */
public class DenoiseMenu extends BaseMenu {

    public DenoiseMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String[] noise;

    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
        {
            try
            {
                noise = camMan.parametersManager.Denoise.getDenoiseValues();

            }
            catch (Exception ex)
            {

            }
        }
        if (noise != null && !noise.equals(""))
        {
            PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderPopup));
            //popupMenu.getMenuInflater().inflate(R.popupmenu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < noise.length; i++) {
                popupMenu.getMenu().add((CharSequence) noise[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    camMan.parametersManager.Denoise.Set(tmp);

                    //activity.button_denoise.SetValue(tmp);
                    preferences.edit().putString(ParametersManager.Preferences_Denoise, tmp).commit();
                    return true;
                }
            });

            popupMenu.show();
        }
    }
}
