package com.freedcam.ui.handler;

import com.freedcam.MainActivity;
import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.ui.AbstractFragment;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.themesample.SampleThemeFragment;
import com.troop.freedcam.R;

/**
 * Created by Ingo on 14.03.2015.
 */
public class ThemeHandler
{
    private MainActivity activity_v2;
    private AbstractFragment uiFragment;
    private AppSettingsManager appSettingsManager;

    public ThemeHandler(MainActivity activity_v2,AppSettingsManager appSettingsManager)
    {
        this.activity_v2 = activity_v2;
        this.appSettingsManager = appSettingsManager;
    }


    /**
     * @return the currently loaded ui theme fragment
     */
    public AbstractFragment getCurrenttheme()
    {
        return  uiFragment;
    }

    /**
     * loads the theme depending on the cameraparameters
     * @param cameraUiWrapper
     * @return
     */
    public AbstractFragment GetThemeFragment(AbstractCameraUiWrapper cameraUiWrapper)
    {
        String theme = appSettingsManager.GetTheme();
        //kill old removed themes if they are left and used from a older version in appSettingManager and set it to default Sample
        if(theme == null || theme.equals("Ambient") || theme.equals("Material")|| theme.equals("Minimal") || theme.equals("Nubia") || theme.equals("Classic") || theme.equals("")) {
            theme = "Sample";
            appSettingsManager.SetTheme("Sample");
        }
        //load sampleFragment
        if (theme.equals("Sample"))
        {
            SampleThemeFragment sampleThemeFragment = SampleThemeFragment.GetInstance(activity_v2,appSettingsManager,cameraUiWrapper);
            uiFragment = sampleThemeFragment;
        }
        inflateFragment(uiFragment);
        return uiFragment;
    }

    private void inflateFragment(AbstractFragment fragment)
    {
        android.support.v4.app.FragmentTransaction transaction = activity_v2.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.left_to_right_enter, R.anim.left_to_right_exit);
        transaction.replace(R.id.themeFragmentholder, fragment, "Main");
        transaction.commitAllowingStateLoss();
    }
}
