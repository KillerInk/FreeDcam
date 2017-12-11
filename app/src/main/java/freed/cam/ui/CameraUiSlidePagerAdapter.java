package freed.cam.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.ui.themesample.cameraui.CameraUiFragment;
import freed.cam.ui.themesample.settings.SettingsMenuFragment;
import freed.viewer.holder.FileHolder;
import freed.viewer.screenslide.ScreenSlideFragment;

public class CameraUiSlidePagerAdapter extends FragmentStatePagerAdapter
{
    private SettingsMenuFragment settingsMenuFragment = new SettingsMenuFragment();
    private ScreenSlideFragment screenSlideFragment = new ScreenSlideFragment();
    private CameraUiFragment cameraUiFragment = new CameraUiFragment();

    private CameraFragmentAbstract cameraFragment;
    ScreenSlideFragment.ButtonClick click;

    public CameraUiSlidePagerAdapter(FragmentManager fm, ScreenSlideFragment.ButtonClick click) {
        super(fm);
        this.click = click;
    }

    public void setCameraFragment(CameraFragmentAbstract cameraFragment)
    {
        this.cameraFragment = cameraFragment;
        settingsMenuFragment.setCameraToUi(cameraFragment);
        cameraUiFragment.setCameraToUi(cameraFragment);
    }

    public void updateScreenSlideFile(List<FileHolder> files)
    {
        screenSlideFragment.NotifyDATAhasChanged(files);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (settingsMenuFragment != null) {
                settingsMenuFragment.setCameraToUi(cameraFragment);
            }
            return settingsMenuFragment;
        }
        else if (position == 2) {
            if (screenSlideFragment != null) {

                screenSlideFragment.setOnBackClickListner(click);
            }
            return screenSlideFragment;
        }
        else {
            if (cameraUiFragment != null)
                cameraUiFragment.setCameraToUi(cameraFragment);
            return cameraUiFragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

}