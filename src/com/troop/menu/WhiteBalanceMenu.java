package com.troop.menu;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.manager.ParametersManager;

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
            whitebalance = camMan.parametersManager.getParameters().getSupportedWhiteBalance();
        if (whitebalance != null)
        {
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
                    if (camMan.parametersManager.is3DMode())
                        preferences.edit().putString(ParametersManager.Preferences_WhiteBalance3D, tmp).commit();
                    if (camMan.parametersManager.is2DMode())
                        preferences.edit().putString(ParametersManager.Preferences_WhiteBalance2D, tmp).commit();
                    if (camMan.parametersManager.isFrontMode())
                        preferences.edit().putString(ParametersManager.Preferences_WhiteBalanceFront, tmp).commit();
                    camMan.parametersManager.getParameters().setWhiteBalance(tmp);
                    Log.d(TAG, "whitebalance is " + camMan.parametersManager.getParameters().getWhiteBalance());
                    camMan.Restart(false);

                    return true;
                }
            });

            popupMenu.show();
        }
    }
}
