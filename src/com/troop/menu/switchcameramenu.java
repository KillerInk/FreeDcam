package com.troop.menu;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;

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
        PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
        //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);

        popupMenu.getMenu().add((CharSequence) CameraManager.SwitchCamera_MODE_3D);
        popupMenu.getMenu().add((CharSequence) CameraManager.SwitchCamera_MODE_2D);
        popupMenu.getMenu().add((CharSequence) CameraManager.SwitchCamera_MODE_Front);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String tmp = item.toString();
                //camMan.parameters.setAutoWhiteBalanceLock(true);

                preferences.edit().putString(CameraManager.SwitchCamera, tmp).commit();

                camMan.Stop();
                activity.mPreview.SwitchViewMode();
                activity.drawSurface.SwitchViewMode();

                camMan.Start();
                camMan.Restart(true);
                activity.drawSurface.drawingRectHelper.Draw();

                //camMan.Restart(false);

                return true;
            }
        });

        popupMenu.show();
    }
}
