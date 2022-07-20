package freed.cam.ui.themenextgen.adapter;

import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import freed.cam.ui.themenextgen.fragment.NextGenCameraSettingFragment;
import freed.cam.ui.themenextgen.fragment.NextGenCameraUiFragment;
import freed.viewer.screenslide.views.ScreenSlideFragment;

public class NextGenCameraUiSlidePagerAdapter extends FragmentStatePagerAdapter
{
    private final NextGenCameraSettingFragment settingsMenuFragment = new NextGenCameraSettingFragment();
    private final ScreenSlideFragment screenSlideFragment = new ScreenSlideFragment();
    private final NextGenCameraUiFragment cameraUiFragment = new NextGenCameraUiFragment();

    ScreenSlideFragment.ButtonClick click;

    public NextGenCameraUiSlidePagerAdapter(FragmentManager fm, ScreenSlideFragment.ButtonClick click) {
        super(fm);
        this.click = click;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return settingsMenuFragment;
        }
        else if (position == 2) {
            if (screenSlideFragment != null) {
                screenSlideFragment.setOnBackClickListner(click);
            }
            return screenSlideFragment;
        }
        else {
            return cameraUiFragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public Parcelable saveState() {
        return null;
    }
}