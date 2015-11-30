package troop.com.themesample.views.uichilds;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.utils.StringUtils;

import troop.com.themesample.subfragments.Interfaces;

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

    @Override
    public void SetMenuItemListner(Interfaces.I_MenuItemClick menuItemClick) {
        super.SetMenuItemListner(this);
    }

    public void SetCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {

        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);


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
                Log.d("Freedcam", ex.getMessage());
            }
        }
        else
            parameter.SetValue(StringUtils.ON,false);
    }

    @Override
    public String ModuleChanged(String module) {
        if (module.equals(AbstractModuleHandler.MODULE_PICTURE) || module.equals(AbstractModuleHandler.MODULE_HDR))
            this.setVisibility(VISIBLE);
        else
            this.setVisibility(GONE);
        return null;
    }
}
