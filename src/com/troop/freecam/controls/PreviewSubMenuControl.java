package com.troop.freecam.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.menu.PreviewFormatMenu;
import com.troop.menu.PreviewSizeMenu;

/**
 * Created by troop on 13.01.14.
 */
public class PreviewSubMenuControl extends LinearLayout
{
    MainActivity activity;
    CameraManager cameraManager;

    MenuItemControl switchPreviewSize;
    MenuItemControl switchPreviewFormat;
    Button ShowHideSettings;
    LinearLayout controlsHolder;


    public PreviewSubMenuControl(Context context) {
        super(context);
    }

    public PreviewSubMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.preview_submenu, this);
    }

    public PreviewSubMenuControl(Context context, AttributeSet attrs, int defStyle) {
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

        switchPreviewSize = (MenuItemControl)findViewById(R.id.switch_previewsize_control);
        switchPreviewSize.SetOnClickListner(new PreviewSizeMenu(cameraManager, activity));

        //switchPreviewFormat = new MenuItemFragment(camMan, activity, "Preview Format", "", new PreviewFormatMenu(camMan, activity));
        switchPreviewFormat = (MenuItemControl)findViewById(R.id.switch_previewformat_control);
        switchPreviewFormat.SetOnClickListner(new PreviewFormatMenu(cameraManager, activity));
    }

    public void UpdateUI()
    {
        switchPreviewFormat.SetButtonText(cameraManager.parametersManager.PreviewFormat.Get());

        switchPreviewSize.SetButtonText(cameraManager.parametersManager.getParameters().getPreviewSize().width + "x" + cameraManager.parametersManager.getParameters().getPreviewSize().height);
    }

    public void Visible(boolean visible)
    {

    }
}
