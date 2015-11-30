package com.troop.freedcam.ui.handler;

import android.widget.LinearLayout;

import com.troop.freedcam.MainActivity;
import com.troop.freedcam.R;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;

import troop.com.themesample.SampleThemeFragment;

/**
 * Created by Ingo on 14.03.2015.
 */
public class ThemeHandler implements I_ModuleEvent
{
    AppSettingsManager appSettingsManager;
    static MainActivity activity_v2;
    LinearLayout uiLayout;
    AbstractFragment uiFragment;
    AbstractCameraUiWrapper cameraUiWrapper;

    public ThemeHandler(MainActivity activity_v2, AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;
        this.activity_v2 = activity_v2;
        uiLayout = (LinearLayout) activity_v2.findViewById(R.id.themeFragmentholder);

    }

    public AbstractFragment getCurrenttheme()
    {
        return  uiFragment;
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        /*if (uiFragment != null)
            uiFragment.SetCameraUIWrapper(cameraUiWrapper);*/
    }

    public AbstractFragment GetThemeFragment(boolean infalte)
    {
        String theme = appSettingsManager.GetTheme();
        if(theme.equals("Ambient") || theme.equals("Material")|| theme.equals("Minimal") || theme.equals("Nubia") || theme.equals("Classic")) {
            theme = "Sample";
            appSettingsManager.SetTheme("Sample");
        }
        if (infalte)
            DestroyUI();
        /*if (theme.equals("Classic"))
        {
            ClassicUi CuiFragment = new ClassicUi();
            CuiFragment.SetStuff(appSettingsManager, activity_v2);

            CuiFragment.SetCameraUIWrapper(cameraUiWrapper);
            uiFragment = CuiFragment;
        }*/
        if (theme.equals("Sample"))
        {
            SampleThemeFragment sampleThemeFragment = new SampleThemeFragment();
            sampleThemeFragment.SetStuff(appSettingsManager, activity_v2);
            sampleThemeFragment.SetCameraUIWrapper(cameraUiWrapper);
            uiFragment = sampleThemeFragment;
        }
        if (infalte)
            inflateFragment(uiFragment);
        return uiFragment;
    }

    public void DestroyUI() {
        if (uiFragment != null)
        {
            android.support.v4.app.FragmentTransaction transaction = activity_v2.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
            transaction.remove(uiFragment);
            transaction.commitAllowingStateLoss();
            uiFragment.onDestroyView();

            uiFragment = null;
        }
    }

    private void inflateFragment(AbstractFragment fragment)
    {
        android.support.v4.app.FragmentTransaction transaction = activity_v2.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
        transaction.add(R.id.themeFragmentholder, fragment, "Main");
        transaction.commitAllowingStateLoss();
    }

    public void SetTheme(String theme)
    {
        GetThemeFragment(true);

    }

    @Override
    public String ModuleChanged(String module) {

        return null;
    }




}
