package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;

/**
 * Created by George on 12/6/13.
 */
public class PreviewFormatMenu extends BaseMenu  {
    public PreviewFormatMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String[] pref;

    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
        {
            try
            {
                pref = camMan.parametersManager.PreviewFormat.getValues();
            }
            catch (Exception ex)
            {

            }
        }
        if (pref != null && !pref.equals(""))
        {
            PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderPopup));
            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < pref.length; i++) {
                popupMenu.getMenu().add((CharSequence) pref[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    camMan.parametersManager.PreviewFormat.Set(tmp);
                    camMan.Settings.PreviewFormat.Set(tmp);
                    camMan.Restart(false);

                    return true;
                }
            });

            popupMenu.show();
        }

    }
}
