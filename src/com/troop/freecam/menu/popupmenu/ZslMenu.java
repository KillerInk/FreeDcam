package com.troop.freecam.menu.popupmenu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;

/**
 * Created by George on 12/7/13.
 */
public class ZslMenu extends BaseMenu  {
    public ZslMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String[] zslv;

    @Override
    public void onClick(View v)
    {

        if(camMan.Running)
        {
            try
            {
                zslv = camMan.parametersManager.ZSLModes.getValues();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        if (zslv != null && !zslv.equals(""))
        {
            PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderPopup));
            for (int i = 0; i < zslv.length; i++)
            {
                popupMenu.getMenu().add((CharSequence) zslv[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    camMan.parametersManager.ZSLModes.setValue(tmp, true);
                    camMan.Settings.ZeroShutterLag.Set(tmp);
                    return true;
                }
            });

            popupMenu.show();
        }
    }
}

