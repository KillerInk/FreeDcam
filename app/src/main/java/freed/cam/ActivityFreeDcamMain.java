/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import freed.ActivityAbstract;
import freed.cam.apis.CameraFragmentManager;
import freed.cam.apis.basecamera.CameraStateEvents;
import freed.cam.events.EventBusHelper;
import freed.cam.ui.CameraUiSlidePagerAdapter;
import freed.cam.ui.SecureCamera;
import freed.cam.ui.themesample.PagingView;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.image.ImageManager;
import freed.image.ImageTask;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.LocationManager;
import freed.utils.Log;
import freed.utils.MediaScannerManager;
import freed.utils.OrientationEvent;
import freed.utils.OrientationManager;
import freed.utils.StorageFileManager;
import freed.viewer.helper.BitmapHelper;
import freed.viewer.holder.FileHolder;
import freed.viewer.screenslide.ScreenSlideFragment;

/**
 * Created by troop on 18.08.2014.
 */
public class ActivityFreeDcamMain extends ActivityAbstract
        implements OrientationEvent,
            SecureCamera.SecureCameraActivity, CameraStateEvents
{

    private class LoadFreeDcamDcimDirsFilesRunner extends ImageTask
    {
        @Override
        public boolean process() {
            LoadFreeDcamDCIMDirsFiles();
            return false;
        }
    }


    private class UpdateScreenSlide
    {
        public UpdateScreenSlide()
        {};
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateScreenSlide(UpdateScreenSlide updateScreenSlide)
    {
        if (files == null)
            return;
        synchronized (files) {
            if (uiViewPagerAdapter != null)
                uiViewPagerAdapter.updateScreenSlideFile(files);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addFromEventFile(FileHolder fileHolder)
    {
        AddFile(fileHolder);
        if (uiViewPagerAdapter != null)
            uiViewPagerAdapter.updateScreenSlideFile(files);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void addFromEventFiles(FileHolder[] fileHolder)
    {
        AddFiles(fileHolder);
        if (uiViewPagerAdapter != null)
            uiViewPagerAdapter.updateScreenSlideFile(files);
    }

    private final String TAG =ActivityFreeDcamMain.class.getSimpleName();
    //listen to orientation changes
    private OrientationManager orientationManager;

    private PagingView uiViewPager;
    private CameraUiSlidePagerAdapter uiViewPagerAdapter;
    private LocationManager locationManager;

    private boolean activityIsResumed= false;
    private int currentorientation = 0;

    private SecureCamera mSecureCamera = new SecureCamera(this);

    private LinearLayout nightoverlay;

    private CameraFragmentManager cameraFragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserMessageHandler.setContext(getContext());
        mSecureCamera.onCreate();
        cameraFragmentManager = new CameraFragmentManager(getSupportFragmentManager(), id.cameraFragmentHolder, getApplicationContext(), this);
        storageHandler = new StorageFileManager();
        EventBusHelper.register(this);
        //listen to phone orientation changes
        orientationManager = new OrientationManager(this, this);
        bitmapHelper = new BitmapHelper(getApplicationContext(),getResources().getDimensionPixelSize(R.dimen.image_thumbnails_size),this);
        locationManager = new LocationManager(this);
    }


    @Override
    protected void onDestroy() {
        EventBusHelper.unregister(this);
        cameraFragmentManager.destroy();
        UserMessageHandler.setContext(null);
        super.onDestroy();
    }

    @Override
    protected void setContentToView() {
        setContentView(layout.freedcam_main_activity);
    }

    //get called when we have sd and camerapermission
    @Override
    protected void initOnCreate() {
        super.initOnCreate();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        // forward to secure camera to handle resume bug
        if (mSecureCamera !=  null)
            mSecureCamera.onResume();
    }

    @Override
    public void onResumeTasks() {
        Log.d(TAG, "onResumeTasks()");
        activityIsResumed = true;
        if (!SettingsManager.getInstance().isInit() || cameraFragmentManager == null)
            return;
        //check if we have the permissions. its needed because onResume gets called while we ask in ActivityAbstract.onCreate().
        getPermissionManager().hasCameraAndSdPermission(granted -> {
            if (granted && SettingsManager.getInstance().isInit()) {
                if (SettingsManager.getInstance().appVersionHasChanged())
                    cameraFragmentManager.switchCameraFragment();
                else {
                    if (uiViewPagerAdapter == null)
                        initScreenSlide();
                    loadCameraFragment();
                }
            }
        });

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause()");
        // forward to secure camera to handle resume bug
        if (mSecureCamera != null)
            mSecureCamera.onPause();
    }

    @Override
    public void onPauseTasks() {
        Log.d(TAG, "onPauseTasks()");
        if(orientationManager != null)
            orientationManager.Stop();
        if (locationManager != null)
            locationManager.stopLocationListining();
        activityIsResumed = false;
    }

    private void initScreenSlide() {
        uiViewPagerAdapter = new CameraUiSlidePagerAdapter(getSupportFragmentManager(),onThumbBackClick);
        if (uiViewPager == null)
            uiViewPager = findViewById(id.viewPager_fragmentHolder);
        uiViewPager.setOffscreenPageLimit(2);
        uiViewPager.setAdapter(uiViewPagerAdapter);
        uiViewPager.setCurrentItem(1);
    }

    /*
    load the camerafragment to ui
     */
    private void loadCameraFragment() {
        Log.d(TAG, "loading cameraWrapper");
        if(orientationManager == null)
            return;
        orientationManager.Start();

        cameraFragmentManager.switchCameraFragment();
    }

    /**
     * Unload the current active camerafragment
     */
    private void unloadCameraFragment() {
        Log.d(TAG, "destroying cameraWrapper");
        if(orientationManager != null)
            orientationManager.Stop();

        cameraFragmentManager.unloadCameraFragment();
        if (uiViewPagerAdapter != null)
            uiViewPagerAdapter.setCameraFragment(null);
        Log.d(TAG, "destroyed cameraWrapper");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (activityIsResumed) {
            Log.d(TAG, "KeyCode Pressed:" + keyCode);
            int appSettingsKeyShutter = 0;


            String es = SettingsManager.get(SettingKeys.EXTERNAL_SHUTTER).get();

            if (es.equals("Vol+"))
                appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_UP;
            else if (es.equals("Vol-"))
                appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_DOWN;
            else if (es.equals("Hook")
                    || es.isEmpty())
                appSettingsKeyShutter = KeyEvent.KEYCODE_HEADSETHOOK;

            if (keyCode == KeyEvent.KEYCODE_3D_MODE
                    || keyCode == KeyEvent.KEYCODE_POWER
                    || keyCode == appSettingsKeyShutter
                    || keyCode == KeyEvent.KEYCODE_UNKNOWN
                    || keyCode == KeyEvent.KEYCODE_CAMERA) {
                cameraFragmentManager.getCameraFragment().getModuleHandler().startWork();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
            {
                closeActivity();
                return true;
            }
        }
        return super.onKeyDown(keyCode,event);
    }

    /**
     * Set the orientaion to the current camerafragment
     * @param orientation the new phone orientation
     */
    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation != currentorientation)
        {
            Log.d(TAG,"orientation changed to :" +orientation);
            currentorientation = orientation;
        }
    }

    @Override
    public int getOrientation() {
        return currentorientation;
    }

    @Override
    public void SwitchCameraAPI(String value)
    {
        //if a camera fragment exists stop and destroy it
        unloadCameraFragment();
        loadCameraFragment();
    }

    @Override
    public void closeActivity()
    {
        moveTaskToBack(true);
    }

    /**
     * Loads all files stored in DCIM/FreeDcam from internal and external SD
     * and notfiy the stored screenslide fragment in sampletheme that
     * files got changed
     */
    @Override
    public void LoadFreeDcamDCIMDirsFiles() {
        Log.d(TAG, "LoadFreeDcamDCIMDirsFiles()");
        super.LoadFreeDcamDCIMDirsFiles();
        EventBusHelper.post(new UpdateScreenSlide());
    }

    @Override
    public void DisablePagerTouch(boolean disable) {
        uiViewPager.EnableScroll(!disable);
    }

    @Override
    public LocationManager getLocationManager() {
        return locationManager;
    }


    /**
     * Loads the files stored from that folder
     * and notfiy the stored screenslide fragment in sampletheme that
     * files got changed
     * @param fileHolder the folder to lookup
     * @param types the file format to load
     */
    @Override
    public void LoadFolder(final FileHolder fileHolder, FormatTypes types) {
        super.LoadFolder(fileHolder, types);
        EventBusHelper.post(new UpdateScreenSlide());
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


    @Override
    public void WorkHasFinished(final FileHolder fileHolder) {

        ScanFile(fileHolder.getFile());
        EventBusHelper.post(fileHolder);
        Log.d(TAG, "newImageRecieved:" + fileHolder.getFile().getAbsolutePath());
    }

    @Override
    public void WorkHasFinished(final FileHolder[] fileHolder) {
        MediaScannerManager.ScanMedia(getContext(),fileHolder);
        EventBusHelper.post(fileHolder);
    }

    @Override
    public void onCameraOpen() {

    }

    @Override
    public void onCameraOpenFinish() {
        //in case the featuredetector runned bevor, uiViewPagerAdapter is null.
        //thats the correct behavior because we dont want that the helpview overlay the featuredetector on first start
        if (uiViewPagerAdapter == null)
            initScreenSlide();
        //note the ui that cameraFragment is loaded
        uiViewPagerAdapter.setCameraFragment(cameraFragmentManager.getCameraFragment());
        if (SettingsManager.getInstance().getApiString(SettingsManager.SETTING_LOCATION).equals(SettingsManager.getInstance().getResString(R.string.on_))
                && getPermissionManager().hasLocationPermission())
            locationManager.startLocationListing();

        SetNightOverlay();
        if(getPermissionManager().hasExternalSDPermission(null) && (files == null || files.size() == 0))
            ImageManager.putImageLoadTask(new LoadFreeDcamDcimDirsFilesRunner());

    }

    @Override
    public void onCameraClose(String message)
    {

    }

    @Override
    public void onPreviewOpen(String message) {

    }

    @Override
    public void onPreviewClose(String message) {

    }

    @Override
    public void onCameraError(String error) {
    }


    @Override
    public void SetNightOverlay() {
        if (nightoverlay == null)
            nightoverlay = findViewById(id.nightoverlay);
        if (SettingsManager.get(SettingKeys.NightOverlay).get())
            nightoverlay.setVisibility(View.VISIBLE);
        else
            nightoverlay.setVisibility(View.GONE);
    }

    @Override
    public void runFeatureDetector() {
        unloadCameraFragment();
        boolean legacy = SettingsManager.get(SettingKeys.openCamera1Legacy).get();
        boolean showHelpOverlay = SettingsManager.getInstance().getShowHelpOverlay();
        SettingsManager.getInstance().RESET();
        SettingsManager.get(SettingKeys.openCamera1Legacy).set(legacy);
        SettingsManager.getInstance().setshowHelpOverlay(showHelpOverlay);
        cameraFragmentManager.switchCameraFragment();
    }
}
