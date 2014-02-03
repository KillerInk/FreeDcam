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
public class AFPriorityMenu extends BaseMenu  {

    public AFPriorityMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String [] modes;

    @Override
    public void onClick(View v)
    {




        PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderAutoMenu));

        if(camMan.Running && camMan.parametersManager.getSupportAfpPriority())
            modes = camMan.parametersManager.AfPriority.getValues();
        if (modes != null)
        {
            //PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
            for (int i = 0; i < modes.length; i++) {
                popupMenu.getMenu().add((CharSequence) modes[i]);
            }

            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_focus, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    camMan.parametersManager.AfPriority.Set(tmp, true);
                    camMan.Settings.afPriority.Set(tmp);
                    //camMan.autoFocusManager.StartFocus();
                    camMan.Restart(false);
                    return true;
                }
            });

            popupMenu.show();

        }

    }
}
