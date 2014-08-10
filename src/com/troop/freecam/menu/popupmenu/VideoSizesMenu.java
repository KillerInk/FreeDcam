package com.troop.freecam.menu.popupmenu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.manager.camera_parameters.BaseParametersManager;

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
        PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderPopup));
        for (int i = 0; i < camMan.parametersManager.VideoProfileClass.getProfiles().length; i++)
        {
            //if (sizes.get(i).height != 576)
            popupMenu.getMenu().add((CharSequence) camMan.parametersManager.VideoProfileClass.getProfiles()[i]);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String tmp = item.toString();
                camMan.parametersManager.VideoProfileClass.SetProfile(tmp);
                camMan.parametersManager.UpdateGui(false, BaseParametersManager.enumParameters.All);

                return true;
            }
        });
        popupMenu.show();
    }
}
