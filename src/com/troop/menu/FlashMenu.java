package com.troop.menu;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 27.08.13.
 */
public class FlashMenu extends BaseMenu
{

    String[] modes;
    CameraManager camMan;
    MainActivity activity;


    public  FlashMenu(CameraManager cameraManager, MainActivity activity)
    {
        super(cameraManager, activity);
        this.camMan = cameraManager;
        this.activity = activity;
    }

    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
            modes = camMan.parametersManager.getParameters().get("flash-mode-values").split(",");
        if (modes != null)
        {
            PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderPopup));
            for (int i = 0; i < modes.length; i++) {
                popupMenu.getMenu().add((CharSequence) modes[i]);
            }


            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    if(activity.recordVideo == false)
                    {
                        camMan.parametersManager.getParameters().setFlashMode(tmp);
                        camMan.Settings.FlashMode.Set(tmp);
                    }
                    if(activity.recordVideo == true)
                    {
                        if (!tmp.equals("off"))
                            tmp = "torch";
                        camMan.parametersManager.getParameters().setFlashMode(tmp);
                        camMan.Settings.FlashMode.Set(tmp);
                    }
                    camMan.Restart(false);
                    return true;
                }
            });

            popupMenu.show();
        }

    }
}
