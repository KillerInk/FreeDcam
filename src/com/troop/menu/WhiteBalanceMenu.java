package com.troop.menu;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 29.08.13.
 */
public class WhiteBalanceMenu extends BaseMenu {

    static String TAG = "WhitebalanceMEnu";

    String[] whitebalance;

    public WhiteBalanceMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
            whitebalance = camMan.parametersManager.WhiteBalance.getValues();
        if (whitebalance != null)
        {
            PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderAutoMenu));
            //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < whitebalance.length; i++) {
                popupMenu.getMenu().add((CharSequence) whitebalance[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    Log.d(TAG, "setWhiteBalance to " + tmp);
                    camMan.Settings.WhiteBalanceMode.Set(tmp);
                    camMan.parametersManager.getParameters().setWhiteBalance(tmp);
                    Log.d(TAG, "whitebalance is " + camMan.parametersManager.getParameters().getWhiteBalance());
                    camMan.ReloadCameraParameters(false);
                    return true;
                }
            });

            popupMenu.show();
        }
    }
}
