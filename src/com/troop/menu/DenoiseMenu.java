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
                if(DeviceUtils.isOmap())
                {
                    noise = camMan.parametersManager.getParameters().get("vnf-supported").split(",");
                    if (noise.length == 1)
                    {
                        String[] tmp = new String[2];
                        tmp[0] = noise[0];
                        tmp[1] = "false";
                        noise = tmp;
                    }
                }
                if(DeviceUtils.isQualcomm())
                    noise = camMan.parametersManager.getParameters().get("denoise-values").split(",");

            }
            catch (Exception ex)
            {

            }
        }
        if (noise != null && !noise.equals(""))
        {
            PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < noise.length; i++) {
                popupMenu.getMenu().add((CharSequence) noise[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    if (DeviceUtils.isOmap())
                    {
                        camMan.parametersManager.getParameters().set("vnf", tmp);
                    }
                    if(DeviceUtils.isQualcomm())
                        camMan.parametersManager.getParameters().set("denoise", tmp);
                    activity.button_denoise.setText(tmp);
                    preferences.edit().putString(ParametersManager.Preferences_Denoise, tmp).commit();
                    camMan.Restart(false);
                    return true;
                }
            });

            popupMenu.show();
        }
    }
}
