package com.troop.freedcam.themenubia.menu;

import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.menu.themes.classic.menu.MenuFragment;
import com.troop.freedcam.ui.menu.themes.classic.menu.MenuHandler;

/**
 * Created by troop on 23.03.2015.
 */
public class NubiaMenuHandler extends MenuHandler {
    public NubiaMenuHandler(MenuFragment context, AppSettingsManager appSettingsManager, I_Activity activity) {
        super(context, appSettingsManager, activity);
    }

    @Override
    public void loadMenuCreator(MenuFragment context, AppSettingsManager appSettingsManager, I_Activity activity) {
        menuCreator = new NubiaMenuCreator(context,appSettingsManager,activity);
    }
}
