package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.manager.ParametersManager;
import com.troop.freecam.utils.DeviceUtils;


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
                if(DeviceUtils.isOmap())
                    isos = camMan.parametersManager.getParameters().get("iso-mode-values").split(",");
                if(DeviceUtils.isQualcomm())
                    isos = camMan.parametersManager.getParameters().get("iso-values").split(",");

            }
            catch (Exception ex)
            {

            }
        }
        if (isos != null && !isos.equals(""))
        {
            PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < isos.length; i++) {
                popupMenu.getMenu().add((CharSequence) isos[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    camMan.parametersManager.getParameters().set("iso", tmp);
                    if (camMan.parametersManager.is3DMode())
                        preferences.edit().putString(ParametersManager.Preferences_Iso3D, tmp).commit();
                    if (camMan.parametersManager.is2DMode())
                        preferences.edit().putString(ParametersManager.Preferences_Iso2D, tmp).commit();
                    if (camMan.parametersManager.isFrontMode())
                        preferences.edit().putString(ParametersManager.Preferences_IsoFront, tmp).commit();
                    //preferences.edit().putString("color", tmp).commit();
                    camMan.Restart(false);

                    return true;
                }
            });

            popupMenu.show();
        }
    }
}
