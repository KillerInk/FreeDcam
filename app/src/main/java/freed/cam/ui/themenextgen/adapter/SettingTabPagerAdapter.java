package freed.cam.ui.themenextgen.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import freed.cam.ui.themenextgen.fragment.NextGenSettingDummyFragment;

import freed.cam.ui.themenextgen.objects.SettingGroupConfig;

public class SettingTabPagerAdapter extends FragmentStatePagerAdapter {

    private NextGenSettingDummyFragment videoFragment;
    private NextGenSettingDummyFragment pictureFragment;
    private NextGenSettingDummyFragment rawFragment;
    private NextGenSettingDummyFragment cameraFragment;
    private NextGenSettingDummyFragment intervalFragment;
    private NextGenSettingDummyFragment previewFragment;
    private NextGenSettingDummyFragment etcFragment;

    public SettingTabPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        SettingGroupConfig groupConfig = new SettingGroupConfig();
        rawFragment = NextGenSettingDummyFragment.getInstance(groupConfig.getRawGroup());
        videoFragment = NextGenSettingDummyFragment.getInstance(groupConfig.getVideoGroup());
        pictureFragment = NextGenSettingDummyFragment.getInstance(groupConfig.getPictureGroup());
        intervalFragment = NextGenSettingDummyFragment.getInstance(groupConfig.getIntervalGroup());
        etcFragment = NextGenSettingDummyFragment.getInstance(groupConfig.getEtcGroup());
        previewFragment = NextGenSettingDummyFragment.getInstance(groupConfig.getPreviewGroup());
        cameraFragment = NextGenSettingDummyFragment.getInstance(groupConfig.getCameraGroup());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 1:
                return pictureFragment;
            case 2:
                return rawFragment;
            case 3:
                return cameraFragment;
            case 4:
                return intervalFragment;
            case 5:
                return previewFragment;
            case 6:
                return etcFragment;
        }
        return videoFragment;
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 1:
                return "Picture";
            case 2:
                return "Raw";
            case 3:
                return "Camera";
            case 4:
                return "Interval";
            case 5:
                return "Preview";
            case 6:
                return "Etc";
        }
        return "Video";
    }
}
