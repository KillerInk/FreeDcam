package com.troop.freecam.menu.submenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 13.01.14.
 */
public class BaseSubMenu extends LinearLayout
{
    MainActivity activity;
    CameraManager cameraManager;
    Button ShowHideSettings;
    LinearLayout controlsHolder;

    public BaseSubMenu(Context context) {
        super(context);
    }

    public BaseSubMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseSubMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void Init(MainActivity activity, CameraManager cameraManager)
    {
        this.activity = activity;
        this.cameraManager = cameraManager;

        controlsHolder = (LinearLayout)findViewById(R.id.Layout_Preview_subMenu);
        controlsHolder.setVisibility(GONE);

        ShowHideSettings = (Button)findViewById(R.id.button_ShowHide);
        ShowHideSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (controlsHolder.getVisibility() == VISIBLE)
                    controlsHolder.setVisibility(GONE);
                else
                    controlsHolder.setVisibility(VISIBLE);
            }
        });
    }
}
