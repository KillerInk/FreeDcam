package com.troop.freecam.menu.submenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.MenuItemControl;
import com.troop.freecam.controls.NumericUpDownControl;
import com.troop.freecam.interfaces.INumericUpDownValueCHanged;

/**
 * Created by troop on 20.01.14.
 */
public class HdrSubMenuControl extends BaseSubMenu
{
    NumericUpDownControl highExposure;
    NumericUpDownControl normalExposure;
    NumericUpDownControl lowExposure;
    MenuItemControl highIso;
    MenuItemControl normalIso;
    MenuItemControl lowIso;
    public HdrSubMenuControl(Context context) {
        super(context);
    }

    public HdrSubMenuControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.hdr_submenu, this);

    }

    public HdrSubMenuControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void Init(MainActivity activity, CameraManager cameraManager)
    {
        super.Init(activity, cameraManager);
        highExposure = (NumericUpDownControl)findViewById(R.id.numericUpDown_HighExposure);
        normalExposure = (NumericUpDownControl)findViewById(R.id.numericUpDown_NormalExposure);
        lowExposure = (NumericUpDownControl)findViewById(R.id.numericUpDown_LowExposure);
        highIso = (MenuItemControl)findViewById(R.id.IsoHigh);
        highIso.SetOnClickListner(popuplistnerhigh);
        normalIso = (MenuItemControl)findViewById(R.id.IsoNormal);
        normalIso.SetOnClickListner(popuplistnernormal);
        lowIso = (MenuItemControl)findViewById(R.id.IsoLow);
        lowIso.SetOnClickListner(popuplistnerlow);

    }

    public void UpdateUI()
    {
        highExposure.setMinMax(cameraManager.parametersManager.manualExposure.getMin(), cameraManager.parametersManager.manualExposure.getMax());
        highExposure.setCurrent(cameraManager.Settings.HDRSettings.getHighExposure());
        highExposure.setOnValueCHanged(new INumericUpDownValueCHanged() {
            @Override
            public void ValueHasCHanged(int value) {
                cameraManager.Settings.HDRSettings.setHighExposure(value);
            }
        });

        normalExposure.setMinMax(cameraManager.parametersManager.manualExposure.getMin(), cameraManager.parametersManager.manualExposure.getMax());
        normalExposure.setCurrent(cameraManager.Settings.HDRSettings.getNormalExposure());
        normalExposure.setOnValueCHanged(new INumericUpDownValueCHanged() {
            @Override
            public void ValueHasCHanged(int value) {
                cameraManager.Settings.HDRSettings.setNormalExposure(value);
            }
        });

        lowExposure.setMinMax(cameraManager.parametersManager.manualExposure.getMin(), cameraManager.parametersManager.manualExposure.getMax());
        lowExposure.setCurrent(cameraManager.Settings.HDRSettings.getLowExposure());
        lowExposure.setOnValueCHanged(new INumericUpDownValueCHanged() {
            @Override
            public void ValueHasCHanged(int value) {
                cameraManager.Settings.HDRSettings.setLowExposure(value);
            }
        });
        highIso.SetButtonText(cameraManager.Settings.HDRSettings.getHighIso());
        normalIso.SetButtonText(cameraManager.Settings.HDRSettings.getNormalIso());
        lowIso.SetButtonText(cameraManager.Settings.HDRSettings.getLowIso());
    }

    OnClickListener popuplistnerhigh = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            String[] isos = null;
            if(cameraManager.Running)
            {
                try
                {
                    isos = cameraManager.parametersManager.Iso.getValues();
                }
                catch (Exception ex)
                {

                }
            }
            if (isos != null && isos.length > 0)
            {
                PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderPopup));
                for (int i = 0; i < isos.length; i++) {
                    popupMenu.getMenu().add((CharSequence) isos[i]);
                }
                popupMenu.setOnMenuItemClickListener(popupitemclickhigh);
                popupMenu.show();
            }
        }
    };

    PopupMenu.OnMenuItemClickListener popupitemclickhigh = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            String tmp = item.toString();
            cameraManager.Settings.HDRSettings.setHighIso(tmp);
            highIso.SetButtonText(tmp);
            return true;
        }
    };

    OnClickListener popuplistnernormal = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            String[] isos = null;
            if(cameraManager.Running)
            {
                try
                {
                    isos = cameraManager.parametersManager.Iso.getValues();
                }
                catch (Exception ex)
                {

                }
            }
            if (isos != null && isos.length > 0)
            {
                PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderPopup));
                for (int i = 0; i < isos.length; i++) {
                    popupMenu.getMenu().add((CharSequence) isos[i]);
                }
                popupMenu.setOnMenuItemClickListener(popupitemclicknormal);
                popupMenu.show();
            }
        }
    };
    PopupMenu.OnMenuItemClickListener popupitemclicknormal = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            String tmp = item.toString();
            cameraManager.Settings.HDRSettings.setNormalIso(tmp);
            normalIso.SetButtonText(tmp);
            return true;
        }
    };

    OnClickListener popuplistnerlow = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            String[] isos = null;
            if(cameraManager.Running)
            {
                try
                {
                    isos = cameraManager.parametersManager.Iso.getValues();
                }
                catch (Exception ex)
                {

                }
            }
            if (isos != null && isos.length > 0)
            {
                PopupMenu popupMenu = new PopupMenu(activity, activity.findViewById(R.id.placeholderPopup));
                for (int i = 0; i < isos.length; i++) {
                    popupMenu.getMenu().add((CharSequence) isos[i]);
                }
                popupMenu.setOnMenuItemClickListener(popupitemclicklow);
                popupMenu.show();
            }
        }
    };
    PopupMenu.OnMenuItemClickListener popupitemclicklow = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item)
        {
            String tmp = item.toString();
            cameraManager.Settings.HDRSettings.setLowIso(tmp);
            lowIso.SetButtonText(tmp);
            return true;
        }
    };

}
