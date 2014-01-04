package com.troop.menu;

import android.hardware.Camera;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.manager.ParametersManager;

import java.util.List;

/**
 * Created by troop on 07.09.13.
 */
public class PictureSizeMenu extends BaseMenu {
    public PictureSizeMenu(CameraManager camMan, MainActivity activity) {
        super(camMan, activity);
    }
    List<Camera.Size> sizes;
    @Override
    public void onClick(View v)
    {
        if(camMan.Running)
            sizes = camMan.parametersManager.getParameters().getSupportedPictureSizes();
        PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderPopup));
        //popupMenu.getMenuInflater().inflate(R.menu.menu_popup_flash, popupMenu.getMenu().);
        for (int i = 0; i < sizes.size(); i++) {
            popupMenu.getMenu().add((CharSequence) (sizes.get(i).width + "x" + sizes.get(i).height));
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String tmp = item.toString();
                camMan.Settings.PictureSize.Set(tmp);
                String[] widthHeight = tmp.split("x");
                int w = Integer.parseInt(widthHeight[0]);
                int h = Integer.parseInt(widthHeight[1]);

                camMan.parametersManager.getParameters().setPictureSize(w, h);

                camMan.Restart(false);

                return true;
            }
        });
        popupMenu.show();
    }
}
