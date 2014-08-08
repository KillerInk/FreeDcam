package com.troop.freecam.menu.popupmenu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;


/**
 * Created by troop on 02.09.13.
 */
public class IsoMenu extends BaseMenu {
    public IsoMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String[] isos;

    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
        {
            try
            {
                isos = camMan.parametersManager.Iso.getValues();

            }
            catch (Exception ex)
            {

            }
        }
        if (isos != null && isos.length > 0)
        {
            PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderAutoMenu));
            for (int i = 0; i < isos.length; i++) {
                popupMenu.getMenu().add((CharSequence) isos[i]);
            }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {

                        String tmp = item.toString();
                        camMan.parametersManager.Iso.set(tmp, true);
                        camMan.Settings.IsoMode.Set(tmp);

                        return true;
                    }
                });


            popupMenu.show();
        }
    }
}
