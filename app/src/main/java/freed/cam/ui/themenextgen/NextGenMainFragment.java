package freed.cam.ui.themenextgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.troop.freedcam.R;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.PreviewFragment;
import freed.cam.apis.basecamera.Size;
import freed.cam.event.camera.CameraHolderEvent;
import freed.cam.previewpostprocessing.PreviewController;
import freed.cam.ui.themenextgen.adapter.NextGenCameraUiSlidePagerAdapter;
import freed.views.pagingview.PagingView;
import freed.file.FileListController;
import freed.image.ImageManager;
import freed.image.ImageTask;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.PermissionManager;
import freed.viewer.screenslide.views.ScreenSlideFragment;

@AndroidEntryPoint
public class NextGenMainFragment extends Fragment implements CameraHolderEvent, PreviewController.PreviewPostProcessingChangedEvent
{
    private static final String TAG = NextGenMainFragment.class.getSimpleName();
    @Inject
    PermissionManager permissionManager;
    @Inject
    ImageManager imageManager;
    @Inject
    SettingsManager settingsManager;
    @Inject
    FileListController fileListController;
    @Inject
    CameraApiManager cameraApiManager;
    @Inject
    PreviewController previewController;
    private PagingView uiViewPager;
    private NextGenCameraUiSlidePagerAdapter uiViewPagerAdapter;
    private LinearLayout nightoverlay;
    private View view;

    private PreviewFragment previewFragment;

    @Override
    public void onCameraOpen() {

    }

    @Override
    public void onCameraOpenFinished() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //in case the featuredetector runned bevor, uiViewPagerAdapter is null.
                //thats the correct behavior because we dont want that the helpview overlay the featuredetector on first start
                if (uiViewPagerAdapter == null)
                    initScreenSlide();
                SetNightOverlay();
                if (!FileListController.needStorageAccessFrameWork) {
                    if (permissionManager.isPermissionGranted(PermissionManager.Permissions.SdCard))
                        imageManager.putImageLoadTask(new LoadFreeDcamDcimDirsFilesRunner());
                }
                else
                {
                    imageManager.putImageLoadTask(new LoadFreeDcamDcimDirsFilesRunner());
                }
            }
        });
    }

    @Override
    public void onCameraClose() {

    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraChangedAspectRatioEvent(Size size) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nextgen_main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        cameraApiManager.addEventListner(this);
        previewController.previewPostProcessingChangedEventHandler.setEventListner(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        previewController.previewPostProcessingChangedEventHandler.removeEventListner(this);
        cameraApiManager.removeEventListner(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        cameraApiManager.onResume();
        if (!settingsManager.appVersionHasChanged() && uiViewPagerAdapter == null)
            initScreenSlide();
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraApiManager.onPause();
    }

    private void initScreenSlide() {

        uiViewPagerAdapter = new NextGenCameraUiSlidePagerAdapter(getParentFragmentManager(),onThumbBackClick);
        if (uiViewPager == null)
            uiViewPager = view.findViewById(R.id.viewPager_fragmentHolder);
        uiViewPager.setOffscreenPageLimit(2);
        uiViewPager.setAdapter(uiViewPagerAdapter);
        uiViewPager.setCurrentItem(1);
    }

    //get called when the back button from screenslidefragment gets clicked
    private final ScreenSlideFragment.ButtonClick onThumbBackClick = new ScreenSlideFragment.ButtonClick() {
        @Override
        public void onButtonClick(int position, View view)
        {
            //show cameraui
            if (uiViewPager != null)
                uiViewPager.setCurrentItem(1);
        }

    };

    public void SetNightOverlay() {
        if (nightoverlay == null)
            nightoverlay = view.findViewById(R.id.nightoverlay);
        Log.d(TAG, "NightOverlay:" + settingsManager.getGlobal(SettingKeys.NightOverlay).get());
        if (settingsManager.getGlobal(SettingKeys.NightOverlay).get())
            nightoverlay.setVisibility(View.VISIBLE);
        else
            nightoverlay.setVisibility(View.GONE);
    }

    @Override
    public void onPreviewPostProcessingChanged() {
        changePreviewPostProcessing();
    }

    private class LoadFreeDcamDcimDirsFilesRunner extends ImageTask
    {
        @Override
        public boolean process() {
            fileListController.LoadFreeDcamDCIMDirsFiles();
            return false;
        }
    }

    private void changePreviewPostProcessing()
    {
        Log.d(TAG,"changePreviewPostProcessing");
        if (previewFragment != null) {
            Log.d(TAG, "unload old Preview");
            FragmentTransaction transaction  = getParentFragmentManager().beginTransaction();
            transaction.remove(previewFragment);
            transaction.commit();
            previewFragment = null;
        }
        Log.d(TAG, "load new Preview");
        previewFragment = new PreviewFragment();
        FragmentTransaction transaction  = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.cameraFragmentHolder, previewFragment);
        transaction.commit();
    }
}
