package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;

import java.util.List;

/**
 * Created by troop on 29.08.13.
 */
public class SceneMenu extends BaseMenu
{
    String[] sceneModes;
    public SceneMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
            sceneModes = camMan.parametersManager.SceneMode.getValues();
        if (sceneModes != null)
        {
            PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderAutoMenu));
            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < sceneModes.length; i++) {
                popupMenu.getMenu().add((CharSequence) sceneModes[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    camMan.Settings.SceneMode.Set(tmp);
                    camMan.parametersManager.SceneMode.set(tmp);
                    return true;
                }
            });

            popupMenu.show();
        }

    }
}
