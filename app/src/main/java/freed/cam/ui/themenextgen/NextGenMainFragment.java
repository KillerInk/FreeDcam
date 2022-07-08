package freed.cam.ui.themenextgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.troop.freedcam.R;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import freed.cam.histogram.HistogramController;
import freed.cam.previewpostprocessing.PreviewController;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.cam.ui.themenextgen.adapter.SettingTabPagerAdapter;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

@AndroidEntryPoint
public class NextGenMainFragment extends Fragment implements PreviewController.PreviewPostProcessingChangedEvent
{
    private boolean settingsOpen = false;
    private TextView settingButton;
    ViewPager settings_holder;
    SettingTabPagerAdapter settingTabPagerAdapter;
    TabLayout tabLayout;
    private FrameLayout camera_preview;
    private FrameLayout settings_viewpagerHolder;

    @Inject
    PreviewController previewController;
    @Inject
    SettingsManager settingsManager;
    @Inject
    HistogramController histogramController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nextgen_cameraui, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settingButton = view.findViewById(R.id.textView_settings);
        settings_holder = view.findViewById(R.id.setting_fragment_holder);
        settings_holder.setOffscreenPageLimit(3);
        tabLayout = view.findViewById(R.id.tab_layout);
        settingTabPagerAdapter = new SettingTabPagerAdapter(getParentFragmentManager());
        settings_holder.setAdapter(settingTabPagerAdapter);
        tabLayout.setupWithViewPager(settings_holder);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!settingsOpen) {
                    settings_viewpagerHolder.setVisibility(View.VISIBLE);
                    settingsOpen = true;
                }
                else {
                    settings_viewpagerHolder.setVisibility(View.GONE);
                    settingsOpen = false;
                }
            }
        });

        camera_preview = view.findViewById(R.id.nextgen_camera_preview);
        settings_viewpagerHolder = view.findViewById(R.id.setting_holder);


        previewController.previewPostProcessingChangedEventHandler.setEventListner(this);
        settings_viewpagerHolder.setVisibility(View.GONE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        previewController.previewPostProcessingChangedEventHandler.removeEventListner(this);
    }

    @Override
    public void onPreviewPostProcessingChanged() {
        camera_preview.removeAllViews();
        if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get() == null)
            previewController.initPreview(PreviewPostProcessingModes.off,getContext(),histogramController);
        else if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.OpenGL.name()))
            previewController.initPreview(PreviewPostProcessingModes.OpenGL, getContext(), histogramController);
        else
            previewController.initPreview(PreviewPostProcessingModes.off,getContext(),histogramController);
        camera_preview.addView(previewController.getPreviewView());

    }
}
