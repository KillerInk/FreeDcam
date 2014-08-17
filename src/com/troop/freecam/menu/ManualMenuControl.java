package com.troop.freecam.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.old.CameraManager;
import com.troop.freecam.controls.ToggleControl;
import com.troop.freecam.enums.E_ManualSeekbar;
import com.troop.freecam.menu.seekbar.SeekbarListHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 17.01.14.
 */
public class ManualMenuControl extends LinearLayout
{
    CameraManager camMan;
    MainActivity activity;

    ToggleControl toggleExposure;
    ToggleControl toggleSharpness;
    ToggleControl toggleFocus;
    ToggleControl toggleShutter;
    ToggleControl toggleContrast;
    ToggleControl toggleBrightness;
    ToggleControl toggleSaturation;
    ToggleControl toggleConvergence;
    ToggleControl toggleZoom;

    private List<ToggleControl> toggleControls;
    SeekbarListHandler seekbarListHandler;

    public ManualMenuControl(Context context) {
        super(context);
    }

    public ManualMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ManualMenuControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void SetStuff(CameraManager cameraManager, MainActivity activity, SeekbarListHandler seekbarListHandler)
    {
        this.camMan = cameraManager;
        this.activity = activity;
        this.seekbarListHandler = seekbarListHandler;
        init();
    }

    private void init()
    {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.manual_menu_control, this);

        toggleControls = new ArrayList<ToggleControl>();

        toggleExposure = (ToggleControl)findViewById(R.id.toggleExposure);
        toggleExposure.SetSeekbarValue(E_ManualSeekbar.Exposure);
        toggleControls.add(toggleExposure);
        toggleExposure.setOnClickListener(onCheckBoxClick);

        toggleSharpness = (ToggleControl) findViewById(R.id.toggleSharpness);
        toggleSharpness.SetSeekbarValue(E_ManualSeekbar.Sharpness);
        toggleControls.add(toggleSharpness);
        toggleSharpness.setOnClickListener(onCheckBoxClick);

        toggleFocus = (ToggleControl)findViewById(R.id.toggleFocus);
        toggleFocus.SetSeekbarValue(E_ManualSeekbar.Focus);
        toggleControls.add(toggleFocus);
        toggleFocus.setOnClickListener(onCheckBoxClick);

        toggleShutter = (ToggleControl)findViewById(R.id.toggleShutter);
        toggleShutter.SetSeekbarValue(E_ManualSeekbar.Shutter);
        toggleControls.add(toggleShutter);
        toggleShutter.setOnClickListener(onCheckBoxClick);

        toggleContrast = (ToggleControl)findViewById(R.id.toggleContrast);
        toggleContrast.SetSeekbarValue(E_ManualSeekbar.Contrast);
        toggleControls.add(toggleContrast);
        toggleContrast.setOnClickListener(onCheckBoxClick);

        toggleBrightness = (ToggleControl)findViewById(R.id.toggleBrightness);
        toggleBrightness.SetSeekbarValue(E_ManualSeekbar.Brightness);
        toggleControls.add(toggleBrightness);
        toggleBrightness.setOnClickListener(onCheckBoxClick);

        toggleSaturation = (ToggleControl) findViewById(R.id.toggleSaturation);
        toggleSaturation.SetSeekbarValue(E_ManualSeekbar.Saturation);
        toggleControls.add(toggleSaturation);
        toggleSaturation.setOnClickListener(onCheckBoxClick);

        toggleConvergence = (ToggleControl)findViewById(R.id.toggleConvergence);
        toggleConvergence.SetSeekbarValue(E_ManualSeekbar.Convergence);
        toggleControls.add(toggleConvergence);
        toggleConvergence.setOnClickListener(onCheckBoxClick);

        toggleZoom = (ToggleControl)findViewById(R.id.toggleZoom);
        toggleZoom.SetSeekbarValue(E_ManualSeekbar.Zoom);
        toggleControls.add(toggleZoom);
        toggleZoom.setOnClickListener(onCheckBoxClick);
    }

    OnClickListener onCheckBoxClick = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            //ToogleControl->LinearLayout->ToggleButton v=ToggleButton
            ToggleControl box = (ToggleControl) v.getParent().getParent();
            for (int i = 0; i < toggleControls.size(); i++)
            {
                if (toggleControls.get(i) != box)
                    toggleControls.get(i).setChecked(false);
                else
                {
                    seekbarListHandler.setVisibility(toggleControls.get(i).GetSeekBarValue(), toggleControls.get(i).isChecked() );
                }

            }
        }
    };

    public void UpdateUI(boolean restarted)
    {
        if (restarted)
        {
            if (!camMan.parametersManager.getSupportBrightness())
                toggleBrightness.setVisibility(View.GONE);
            else
            {
                toggleBrightness.setVisibility(View.VISIBLE);
            }
            if (!camMan.parametersManager.getSupportSharpness())
                toggleSharpness.setVisibility(View.GONE);
            else
            {
                toggleSharpness.setVisibility(View.VISIBLE);
            }
            if (!camMan.parametersManager.getSupportSaturation())
                toggleSaturation.setVisibility(View.GONE);
            else
            {
                toggleSaturation.setVisibility(View.VISIBLE);
            }
            if (!camMan.parametersManager.getSupportContrast())
                toggleContrast.setVisibility(GONE);
            else
            {
                toggleContrast.setVisibility(VISIBLE);
            }
            if (camMan.parametersManager.getSupportManualConvergence())
            {
                toggleConvergence.setVisibility(VISIBLE);
            }
            else
            {
                toggleConvergence.setVisibility(GONE);
            }
            if (!camMan.parametersManager.getSupportManualFocus())
                toggleFocus.setVisibility(View.GONE);
            else
            {
                toggleFocus.setVisibility(View.VISIBLE);
            }
            if (!camMan.parametersManager.getSupportManualShutter())
                toggleShutter.setVisibility(View.GONE);
            else
            {
                toggleShutter.setVisibility(View.VISIBLE);
            }
            if (camMan.parametersManager.getParameters().isZoomSupported())
                toggleZoom.setVisibility(VISIBLE);
            else
                toggleZoom.setVisibility(GONE);

        }
    }
}
