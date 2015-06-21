package com.troop.freedcam.ui.menu.themes.classic;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.R;

import troop.com.themesample.handler.FocusImageHandler;

/**
 * Created by troop on 21.06.2015.
 */
public class ClassicFocusImageHandler extends FocusImageHandler {
    public ClassicFocusImageHandler(View view, Fragment fragment, I_Activity activity) {
        super(view, fragment, activity);
    }

    @Override
    protected void init(View view) {
        focusImageView = (ImageView)view.findViewById(com.troop.freedcam.ui.menu.themes.R.id.imageView_Crosshair);
        cancelFocus = (ImageView)view.findViewById(com.troop.freedcam.ui.menu.themes.R.id.imageViewFocusClose);
        meteringArea = (ImageView)view.findViewById(com.troop.freedcam.ui.menu.themes.R.id.imageView_meteringarea);
        awbArea = (ImageView)view.findViewById(com.troop.freedcam.ui.menu.themes.R.id.imageView_awbarea);
    }
}
