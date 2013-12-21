package com.troop.menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.manager.ParametersManager;

/**
 * Created by troop on 03.09.13.
 */
public class ExposureMenu extends BaseMenu {
    public ExposureMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String[] exposures;


    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
        {
            try {

                if(CameraManager.isOmap())
                exposures = camMan.parametersManager.getParameters().get("exposure-mode-values").split(",");
                if(CameraManager.isQualcomm() || CameraManager.isExynos() || CameraManager.isTegra())
                    exposures = null;
            }
            catch (Exception ex)
            {}
        }

        if (exposures != null)
        {
            PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < exposures.length; i++) {
                popupMenu.getMenu().add((CharSequence) exposures[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    if (camMan.parametersManager.is3DMode())
                    {
                        preferences.edit().putString(ParametersManager.Preferences_Exposure3D, tmp).commit();
                    }
                    if (camMan.parametersManager.is2DMode())
                        preferences.edit().putString(ParametersManager.Preferences_Exposure2D, tmp).commit();
                    if (camMan.parametersManager.isFrontMode())
                        preferences.edit().putString(ParametersManager.Preferences_ExposureFront, tmp).commit();
                    camMan.parametersManager.getParameters().set("exposure", tmp);
                    //if (tmp.equals("manual"))
                    //activity.exposureSeekbar.setVisibility(View.VISIBLE);
                    //else
                    //activity.exposureSeekbar.setVisibility(View.INVISIBLE);
                    //preferences.edit().putString("color", tmp).commit();
                    camMan.Restart(false);

                    return true;
                }
            });
            popupMenu.show();
        }

    }
}
