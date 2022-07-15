package freed.cam.ui.themenextgen.layoutconfig;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.List;

import freed.FreedApplication;
import freed.settings.SettingKeys;

public class ManualGroupConfig {

    public List<ManualItemConfig> getManualGroup()
    {
        List<ManualItemConfig> group = new ArrayList<>();
        group.add(new ManualItemConfig(SettingKeys.M_Aperture, FreedApplication.getStringFromRessources(R.string.font_aperture) ));
        group.add(new ManualItemConfig(SettingKeys.M_ManualIso, FreedApplication.getStringFromRessources(R.string.font_iso) ));
        group.add(new ManualItemConfig(SettingKeys.M_ExposureTime, FreedApplication.getStringFromRessources(R.string.font_exposuretime) ));
        group.add(new ManualItemConfig(SettingKeys.M_ExposureCompensation, FreedApplication.getStringFromRessources(R.string.font_ev) ));
        group.add(new ManualItemConfig(SettingKeys.M_Focus, FreedApplication.getStringFromRessources(R.string.font_manual_focus) ));
        group.add(new ManualItemConfig(SettingKeys.M_Whitebalance, FreedApplication.getStringFromRessources(R.string.font_wb) ));
        group.add(new ManualItemConfig(SettingKeys.M_Burst, FreedApplication.getStringFromRessources(R.string.font_burst) ));

        return group;
    }
}
