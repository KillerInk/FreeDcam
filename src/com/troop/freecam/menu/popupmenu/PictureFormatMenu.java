package com.troop.freecam.menu.popupmenu;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.manager.parameters.ParametersManager;
import com.troop.freecam.utils.DeviceUtils;

/**
 * Created by George on 12/6/13.
 */
public class PictureFormatMenu extends BaseMenu
{
    public PictureFormatMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }

    String[] picf;
    String xxx;

    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
        {
            try
            {
                //String Values = "jpeg,raw,bayer-mipi-10bggr,bayer-mipi-10rggb,bayer-mipi-10grgb,bayer-mipi-10gbrg,bayer-qcom-10bggr,bayer-qcom-10rggb,bayer-qcom-10grgb,bayer-qcom-10gbrg";
                picf = camMan.parametersManager.getPictureFormatValues();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        if (picf != null && !picf.equals(""))
        {
            PopupMenu popupMenu = new PopupMenu(activity, super.GetPlaceHolder());
            //popupMenu.getMenuInflater().inflate(R.popupmenu.menu_popup_flash, popupMenu.getMenu().);
            for (int i = 0; i < picf.length; i++) {
                popupMenu.getMenu().add((CharSequence) picf[i]);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String tmp = item.toString();
                    try
                    {
                        camMan.parametersManager.setPictureFormat(tmp);
                        camMan.Settings.pictureFormat.Set(tmp);
                        camMan.ReloadCameraParameters(false);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    //camMan.parametersManager.GetCamP(tmp);
                    

                    Log.d("CurrentP", tmp);
                    xxx = tmp;


                    return true;
                }
            });

            popupMenu.show();
        }

    }

    public String PictureString()
    {
        return xxx;
    }


}
