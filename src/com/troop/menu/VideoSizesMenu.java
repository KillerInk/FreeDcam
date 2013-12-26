package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.manager.ParametersManager;

/**
 * Created by troop on 26.12.13.
 */
public class VideoSizesMenu extends BaseMenu
{

    public VideoSizesMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    @Override
    public void onClick(View v)
    {
        PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
        for (int i = 0; i < camMan.parametersManager.videoModes.GetStringValues().length; i++)
        {
            //if (sizes.get(i).height != 576)
            popupMenu.getMenu().add((CharSequence) camMan.parametersManager.videoModes.GetStringValues()[i]);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String tmp = item.toString();
                preferences.edit().putString("videosizes", tmp).commit();
                camMan.parametersManager.videoModes.SetProfile(tmp);

                camMan.Restart(false);

                return true;
            }
        });
        popupMenu.show();
    }
}
