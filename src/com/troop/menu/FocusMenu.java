package com.troop.menu;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 27.08.13.
 */
public class FocusMenu extends BaseMenu {


    String [] modes;

    public FocusMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    @Override
    public void onClick(View v)
    {



        PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderPopup));

        if(camMan.Running)
            modes = camMan.parametersManager.getParameters().get("focus-mode-values").split(",");
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
                    camMan.Settings.FocusMode.Set(tmp);
                    camMan.parametersManager.setFocusMode(tmp);

                    return true;
                }
            });

            popupMenu.show();
        }
    }
}
