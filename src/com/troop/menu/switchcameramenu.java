package com.troop.menu;

import android.hardware.Camera;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.manager.SettingsManager;
import com.troop.freecam.utils.DeviceUtils;

/**
 * Created by troop on 05.09.13.
 */
public class switchcameramenu extends  BaseMenu
{
    public switchcameramenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    @Override
    public void onClick(View v)
    {
        PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderPopup));
        //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
        if (Camera.getNumberOfCameras() == 3 || DeviceUtils.isEvo3d())
        {
            set3DMenu(popupMenu);
        }
        else if (Camera.getNumberOfCameras() == 2)
        {
            popupMenu.getMenu().add((CharSequence) SettingsManager.Preferences.MODE_2D);
            popupMenu.getMenu().add((CharSequence) SettingsManager.Preferences.MODE_Front);
        }
        else if (Camera.getNumberOfCameras() == 1)
        {
            popupMenu.getMenu().add((CharSequence) SettingsManager.Preferences.MODE_Front);
        }


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String tmp = item.toString();
                //camMan.parameters.setAutoWhiteBalanceLock(true);

                camMan.Settings.Cameras.SetCamera(tmp);

                camMan.Stop();
                activity.mPreview.SwitchViewMode();
                activity.drawSurface.SwitchViewMode();

                camMan.Start();
                camMan.Restart(true);
                activity.drawSurface.drawingRectHelper.Draw();
                activity.SwitchCropButton();

                //camMan.Restart(false);

                return true;
            }
        });

        popupMenu.show();
    }

    private void set3DMenu(PopupMenu popupMenu) {
        popupMenu.getMenu().add((CharSequence) SettingsManager.Preferences.MODE_3D);
        popupMenu.getMenu().add((CharSequence) SettingsManager.Preferences.MODE_2D);
        popupMenu.getMenu().add((CharSequence) SettingsManager.Preferences.MODE_Front);
    }
}
