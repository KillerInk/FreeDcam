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
public class PictureFormatMenu extends BaseMenu  {
    public PictureFormatMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String[] picf;

    @Override
    public void onClick(View v)
    {

        if(camMan.Running)
        {
            try
            {
                //TODO get the values from the camera parameters
                String Values = "JPEG,WEBMP,PNG,RAW";
                picf = Values.split(",");


            }
            catch (Exception ex)
            {

            }
        }
        if (picf != null && !picf.equals(""))
        {
            PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < picf.length; i++) {
                popupMenu.getMenu().add((CharSequence) picf[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    //camMan.parametersManager.getParameters().set("iso", tmp);
                    activity.buttonPictureFormat.setText(tmp);

                    if (camMan.parametersManager.is3DMode())
                        preferences.edit().putString(ParametersManager.Preferences_PictureFormat, tmp).commit();
                    if (camMan.parametersManager.is2DMode())
                        preferences.edit().putString(ParametersManager.Preferences_PictureFormat, tmp).commit();
                    if (camMan.parametersManager.isFrontMode())
                        preferences.edit().putString(ParametersManager.Preferences_PictureFormat, tmp).commit();
                    //preferences.edit().putString("color", tmp).commit();
                    camMan.Restart(false);

                    return true;
                }
            });

            popupMenu.show();
        }

    }
}
