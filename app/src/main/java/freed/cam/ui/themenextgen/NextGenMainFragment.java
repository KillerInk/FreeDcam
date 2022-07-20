package freed.cam.ui.themenextgen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.troop.freedcam.R;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.Size;
import freed.cam.event.camera.CameraHolderEvent;
import freed.cam.histogram.HistogramController;
import freed.cam.previewpostprocessing.PreviewController;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.cam.ui.themenextgen.adapter.NextGenCameraUiSlidePagerAdapter;
import freed.file.FileListController;
import freed.image.ImageManager;
import freed.image.ImageTask;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.PermissionManager;
import freed.viewer.screenslide.views.ScreenSlideFragment;
import freed.views.pagingview.PagingView;

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
    @Inject
    HistogramController histogramController;
    private PagingView uiViewPager;
    private NextGenCameraUiSlidePagerAdapter uiViewPagerAdapter;
    private LinearLayout nightoverlay;
    private View view;
    private FrameLayout cameraPreview;

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
        cameraPreview = view.findViewById(R.id.nextgen_camera_preview);
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
        Log.d(TAG, "NightOverlay:" + settingsManager.getGlobal(SettingKeys.NIGHT_OVERLAY).get());
        if (settingsManager.getGlobal(SettingKeys.NIGHT_OVERLAY).get())
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
        cameraPreview.removeAllViews();
        if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get() == null)
            previewController.initPreview(PreviewPostProcessingModes.off,getContext(),histogramController);
        else if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.OpenGL.name()))
            previewController.initPreview(PreviewPostProcessingModes.OpenGL, getContext(), histogramController);
        else
            previewController.initPreview(PreviewPostProcessingModes.off,getContext(),histogramController);
        cameraPreview.addView(previewController.getPreviewView());
    }
}
