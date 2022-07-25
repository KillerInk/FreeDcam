package freed.cam.ui.themenextgen.layoutconfig;

import android.graphics.Color;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import freed.FreedApplication;
import freed.settings.SettingKeys;

public class ManualGroupConfig {

    public List<ManualItemConfig> getManualGroup()
    {
        List<ManualItemConfig> group = new ArrayList<>();
        group.add(new ManualItemConfig<String>(SettingKeys.M_ZOOM, FreedApplication.getStringFromRessources(R.string.font_zoom_plus) ));
        group.add(new ManualItemConfig<String>(SettingKeys.M_FOCUS, FreedApplication.getStringFromRessources(R.string.font_manual_focus) ));
        group.add(new ManualItemConfig<String>(SettingKeys.M_MANUAL_ISO, FreedApplication.getStringFromRessources(R.string.font_iso) ));
        group.add(new ManualItemConfig<String>(SettingKeys.M_EXPOSURE_TIME, FreedApplication.getStringFromRessources(R.string.font_exposuretime) ));
        group.add(new ManualItemConfig<String>(SettingKeys.M_EXPOSURE_COMPENSATION, FreedApplication.getStringFromRessources(R.string.font_ev) ));
        group.add(new ManualItemConfig<String>(SettingKeys.M_APERTURE, FreedApplication.getStringFromRessources(R.string.font_aperture) ));
        group.add(new ManualItemConfig<String>(SettingKeys.M_WHITEBALANCE, FreedApplication.getStringFromRessources(R.string.font_wb) ));
        group.add(new ManualItemConfig<String>(SettingKeys.M_BURST, FreedApplication.getStringFromRessources(R.string.font_burst) ));
        group.add(new ManualItemConfig<String>(SettingKeys.M_CONTRAST, FreedApplication.getStringFromRessources(R.string.font_contrast) ));
        group.add(new ManualItemConfig<String>(SettingKeys.M_Brightness, FreedApplication.getStringFromRessources(R.string.font_brightness) ));
        group.add(new ManualItemConfig<String>(SettingKeys.M_SATURATION, FreedApplication.getStringFromRessources(R.string.font_saturation) ));
        group.add(new ManualItemConfig<String>(SettingKeys.M_SHARPNESS, FreedApplication.getStringFromRessources(R.string.font_sharpness) ));
        group.add(new ManualItemConfig<String>(SettingKeys.M_ZEBRA_HIGH, FreedApplication.getStringFromRessources(R.string.font_clipping), Color.RED));
        group.add(new ManualItemConfig<String>(SettingKeys.M_ZEBRA_LOW, FreedApplication.getStringFromRessources(R.string.font_clipping),Color.BLUE));

        return group;
    }
}
