package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.manager.ParametersManager;

import java.util.List;

/**
 * Created by troop on 31.08.13.
 */
public class ColorMenu extends BaseMenu {

    List<String> colors;
    public ColorMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
            colors = camMan.parametersManager.getParameters().getSupportedColorEffects();
        if (colors != null)
        {
            PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderAutoMenu));
            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < colors.size(); i++) {
                popupMenu.getMenu().add((CharSequence) colors.get(i));
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    camMan.parametersManager.getParameters().setColorEffect(tmp);
                    camMan.Settings.ColorMode.Set(tmp);
                    camMan.Restart(false);
                    return true;
                }
            });

            popupMenu.show();
        }
    }
}
