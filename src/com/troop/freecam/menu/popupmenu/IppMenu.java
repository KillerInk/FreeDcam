package com.troop.freecam.menu.popupmenu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.old.CameraManager;

/**
 * Created by troop on 08.09.13.
 */
public class IppMenu extends  BaseMenu
{
    public IppMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String[] ipp;


    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
            try{

                ipp = camMan.parametersManager.ImagePostProcessing.getValues();
            }
            catch (NullPointerException ex)
            {

            }

        if (ipp != null)
        {
            PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderPopup));
            //popupMenu.getMenuInflater().inflate(R.popupmenu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < ipp.length; i++) {
                popupMenu.getMenu().add((CharSequence) ipp[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    camMan.parametersManager.ImagePostProcessing.Set(tmp, true);
                    camMan.Settings.ImagePostProcessing.Set(tmp);
                    return true;
                }
            });

            popupMenu.show();
        }

    }

}
