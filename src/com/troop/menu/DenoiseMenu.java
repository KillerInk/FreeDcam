package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.manager.ParametersManager;

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
                if(CameraManager.isOmap())
                {

                    noise = camMan.parametersManager.getParameters().get("vnf-supported").split(",");
                    String[] tmp = new String[2];
                    tmp[0] = noise[0];
                    tmp[1] = "false";
                    noise = tmp;
                }
                if(CameraManager.isQualcomm())
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
                    if (CameraManager.isOmap())
                    {
                        camMan.parametersManager.getParameters().set("vnf", tmp);
                    }
                    if(CameraManager.isQualcomm())
                        camMan.parametersManager.getParameters().set("denoise", tmp);
                    activity.button_denoise.setText(tmp);


                    String camvalue = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_3D);
                    if (camvalue.equals(ParametersManager.SwitchCamera_MODE_3D))
                        preferences.edit().putString(ParametersManager.Preferences_Denoise, tmp).commit();
                    if (camvalue.equals(ParametersManager.SwitchCamera_MODE_2D))
                        preferences.edit().putString(ParametersManager.Preferences_Denoise, tmp).commit();
                    if (camvalue.equals(ParametersManager.SwitchCamera_MODE_Front))
                        preferences.edit().putString(ParametersManager.Preferences_Denoise, tmp).commit();
                    //preferences.edit().putString("color", tmp).commit();
                    camMan.Restart(false);

                    return true;
                }
            });

            popupMenu.show();
        }
    }
}
