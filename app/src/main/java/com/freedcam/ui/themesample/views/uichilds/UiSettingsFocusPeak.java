package com.freedcam.ui.themesample.views.uichilds;

import android.content.Context;
import android.util.AttributeSet;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.ui.themesample.subfragments.Interfaces;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

/**
 * Created by troop on 09.09.2015.
 */
public class UiSettingsFocusPeak extends UiSettingsChild implements Interfaces.I_MenuItemClick {
    public UiSettingsFocusPeak(Context context) {
        super(context);
    }

    public UiSettingsFocusPeak(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UiSettingsFocusPeak(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void SetMenuItemListner(Interfaces.I_MenuItemClick menuItemClick) {
        super.SetMenuItemListner(this,false);
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {

        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);

        ModuleChanged(cameraUiWrapper.moduleHandler.GetCurrentModuleName());

    }

    @Override
    public void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment)
    {
        if (parameter == null)
            return;
        if (parameter.GetValue().equals(StringUtils.ON)) {
            try {
                parameter.SetValue(StringUtils.OFF, false);
            }
            catch (Exception ex)
            {
                Logger.d("Freedcam", ex.getMessage());
            }
        }
        else
            parameter.SetValue(StringUtils.ON,false);
    }

    @Override
    public void ModuleChanged(String module)
    {
        if ((module.equals(KEYS.MODULE_PICTURE) || module.equals(KEYS.MODULE_HDR)|| module.equals(KEYS.MODULE_INTERVAL)) && parameter != null && parameter.IsSupported())
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);
    }
}
