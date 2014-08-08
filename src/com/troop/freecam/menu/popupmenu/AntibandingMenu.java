package com.troop.freecam.menu.popupmenu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 27.01.14.
 */
public class AntibandingMenu extends BaseMenu {
    public AntibandingMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    @Override
    public void onClick(View v)
    {
        String[] modes = null;
        PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderPopup));

        if(camMan.Running && camMan.parametersManager.getSupportAntibanding())
            modes = camMan.parametersManager.Antibanding.GetValues();
        if (modes != null)
        {
            //PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
            for (int i = 0; i < modes.length; i++) {
                popupMenu.getMenu().add((CharSequence) modes[i]);
            }

            //popupMenu.getMenuInflater().inflate(R.popupmenu.menu_popup_focus, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    camMan.parametersManager.Antibanding.Set(tmp);
                    camMan.Settings.Antibanding.Set(tmp);
                    //camMan.autoFocusManager.StartFocus();
                    //camMan.ReloadCameraParameters(false);
                    return true;
                }
            });

            popupMenu.show();

        }
    }
}
