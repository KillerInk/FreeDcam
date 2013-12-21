package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.manager.ParametersManager;

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
                pref = camMan.parametersManager.getParameters().get("preview-format-values").split(",");
            }
            catch (Exception ex)
            {

            }
        }
        if (pref != null && !pref.equals(""))
        {
            PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < pref.length; i++) {
                popupMenu.getMenu().add((CharSequence) pref[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    camMan.parametersManager.getParameters().set("preview-format", tmp);

                    if (camMan.parametersManager.is3DMode())
                        preferences.edit().putString(ParametersManager.Preferences_PreviewFormat, tmp).commit();
                    if (camMan.parametersManager.is2DMode())
                        preferences.edit().putString(ParametersManager.Preferences_PreviewFormat, tmp).commit();
                    if (camMan.parametersManager.isFrontMode())
                        preferences.edit().putString(ParametersManager.Preferences_PreviewFormat, tmp).commit();
                    camMan.Restart(false);

                    return true;
                }
            });

            popupMenu.show();
        }

    }
}
