package com.troop.freecam.menu.popupmenu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.old.CameraManager;

/**
 * Created by George on 12/6/13.
 */
public class MeteringMenu extends BaseMenu   {

    public MeteringMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String [] modes;

    @Override
    public void onClick(View v)
    {

        PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderAutoMenu));

        if(camMan.Running && camMan.parametersManager.getSupportAutoExposure())

            modes = camMan.parametersManager.getParameters().get("auto-exposure-values").split(",");

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
                    camMan.parametersManager.getParameters().set("auto-exposure", tmp);
                    camMan.Settings.MeteringMode.Set(tmp);
                    //camMan.autoFocusManager.StartFocus();
                    camMan.ReloadCameraParameters(false);
                    return true;
                }
            });

            popupMenu.show();
        }
    }

}
