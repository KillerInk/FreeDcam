package com.troop.menu;

import android.hardware.Camera;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.manager.ParametersManager;

/**
 * Created by troop on 03.09.13.
 */
public class ExposureMenu extends BaseMenu {
    public ExposureMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String[] exposures;

    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
            exposures = camMan.parametersManager.getParameters().get("exposure-mode-values").split(",");
        if (exposures != null)
        {
            PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < exposures.length; i++) {
                popupMenu.getMenu().add((CharSequence) exposures[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();

                    String camvalue = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_3D);
                    if (camvalue.equals(ParametersManager.SwitchCamera_MODE_3D))
                    {
                        preferences.edit().putString(ParametersManager.Preferences_Exposure3D, tmp).commit();
                    }
                    if (camvalue.equals(ParametersManager.SwitchCamera_MODE_2D))
                        preferences.edit().putString(ParametersManager.Preferences_Exposure2D, tmp).commit();
                    if (camvalue.equals(ParametersManager.SwitchCamera_MODE_Front))
                        preferences.edit().putString(ParametersManager.Preferences_ExposureFront, tmp).commit();
                    camMan.parametersManager.getParameters().set("exposure", tmp);
                    //if (tmp.equals("manual"))
                    //activity.exposureSeekbar.setVisibility(View.VISIBLE);
                    //else
                    //activity.exposureSeekbar.setVisibility(View.INVISIBLE);
                    //preferences.edit().putString("color", tmp).commit();
                    camMan.Restart(false);

                    return true;
                }
            });
            popupMenu.show();
        }

    }
}
