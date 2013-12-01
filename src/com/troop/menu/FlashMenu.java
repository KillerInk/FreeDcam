package com.troop.menu;

import android.annotation.TargetApi;
import android.hardware.Camera;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.manager.ParametersManager;

import java.util.List;

/**
 * Created by troop on 27.08.13.
 */
public class FlashMenu extends BaseMenu
{

    String[] modes;
    CameraManager camMan;
    MainActivity activity;
    public  FlashMenu(CameraManager cameraManager, MainActivity activity)
    {
        super(cameraManager, activity);
        this.camMan = cameraManager;
        this.activity = activity;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
            modes = camMan.parametersManager.getParameters().get("flash-mode-values").split(",");

        PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
        for (int i = 0; i < modes.length; i++) {
            popupMenu.getMenu().add((CharSequence) modes[i]);
        }


        //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String tmp = item.toString();
                camMan.parametersManager.getParameters().setFlashMode(tmp);

                String camvalue = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_3D);
                if (camvalue.equals(ParametersManager.SwitchCamera_MODE_3D))
                    preferences.edit().putString(ParametersManager.Preferences_Flash3D, tmp).commit();
                if (camvalue.equals(ParametersManager.SwitchCamera_MODE_2D))
                    preferences.edit().putString(ParametersManager.Preferences_Flash2D, tmp).commit();
                //if (camvalue == CameraManager.SwitchCamera_MODE_Front)
                    //preferences.edit().putString(CameraManager.Preferences_ExposureFront, tmp).commit();
                //preferences.edit().putString("flash", tmp).commit();
                camMan.Restart(false);
                return true;
            }
        });

        popupMenu.show();

    }
}
