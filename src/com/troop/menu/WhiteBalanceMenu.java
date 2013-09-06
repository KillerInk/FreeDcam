package com.troop.menu;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;

import java.util.List;

/**
 * Created by troop on 29.08.13.
 */
public class WhiteBalanceMenu extends BaseMenu {

    static String TAG = "WhitebalanceMEnu";

    List<String> whitebalance;

    public WhiteBalanceMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
            whitebalance = camMan.parameters.getSupportedWhiteBalance();
        PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
        //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
        for (int i = 0; i < whitebalance.size(); i++) {
            popupMenu.getMenu().add((CharSequence) whitebalance.get(i));
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String tmp = item.toString();
                //camMan.parameters.setAutoWhiteBalanceLock(true);
                Log.d(TAG, "setWhiteBalance to " + tmp);
                String camvalue = preferences.getString(CameraManager.SwitchCamera, CameraManager.SwitchCamera_MODE_3D);
                if (camvalue == CameraManager.SwitchCamera_MODE_3D)
                    preferences.edit().putString(CameraManager.Preferences_WhiteBalance3D, tmp).commit();
                if (camvalue == CameraManager.SwitchCamera_MODE_2D)
                    preferences.edit().putString(CameraManager.Preferences_WhiteBalance2D, tmp).commit();
                if (camvalue == CameraManager.SwitchCamera_MODE_Front)
                    preferences.edit().putString(CameraManager.Preferences_WhiteBalanceFront, tmp).commit();
                //preferences.edit().putString("whitebalance", tmp).commit();
                camMan.parameters.setWhiteBalance(tmp);
                Log.d(TAG, "whitebalance is " + camMan.parameters.getWhiteBalance());
                camMan.Restart(false);

                return true;
            }
        });

        popupMenu.show();
    }
}
