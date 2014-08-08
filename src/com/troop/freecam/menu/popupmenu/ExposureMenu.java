package com.troop.freecam.menu.popupmenu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;

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
        {
            try {

                exposures = camMan.parametersManager.ExposureMode.getExposureValues();
            }
            catch (Exception ex)
            {}
        }

        if (exposures != null)
        {
            PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderAutoMenu));
            //popupMenu.getMenuInflater().inflate(R.popupmenu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < exposures.length; i++) {
                popupMenu.getMenu().add((CharSequence) exposures[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    camMan.Settings.ExposureMode.Set(tmp);
                    camMan.parametersManager.ExposureMode.set(tmp);
                    return true;
                }
            });
            popupMenu.show();
        }

    }
}
