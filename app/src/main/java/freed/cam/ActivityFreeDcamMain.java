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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.ActivityAbstract;
import freed.cam.apis.CameraFragmentManager;
import freed.cam.apis.basecamera.CameraStateEvents;
import freed.cam.ui.CameraUiSlidePagerAdapter;
import freed.cam.ui.SecureCamera;
import freed.cam.ui.handler.I_orientation;
import freed.cam.ui.handler.OrientationHandler;
import freed.cam.ui.themesample.PagingView;
import freed.image.ImageManager;
import freed.image.ImageTask;
import freed.settings.AppSettingsManager;
import freed.utils.LocationHandler;
import freed.utils.Log;
import freed.utils.PermissionHandler;
import freed.utils.StorageFileHandler;
import freed.utils.StringUtils;
import freed.viewer.helper.BitmapHelper;
import freed.viewer.holder.FileHolder;
import freed.viewer.screenslide.ScreenSlideFragment;

/**
 * Created by troop on 18.08.2014.
 */
public class ActivityFreeDcamMain extends ActivityAbstract
        implements I_orientation,
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




    private class UpdateScreenSlideHandler extends Handler
    {
        private final int MSG_UPDATE_SCREENSLIDE = 0;
        private final int MSG_ADDFILES = 1;
        private final int MSG_ADDFILE = 2;

        public void update()
        {
            this.obtainMessage(MSG_UPDATE_SCREENSLIDE).sendToTarget();
        }

        public void addFiles(FileHolder[] fileHolders)
        {
            this.obtainMessage(MSG_ADDFILES,fileHolders).sendToTarget();
        }

        public void addFile(FileHolder fileHolder)
        {
            this.obtainMessage(MSG_ADDFILE,fileHolder).sendToTarget();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_SCREENSLIDE:
                    synchronized (files) {
                        if (uiViewPagerAdapter != null)
                            uiViewPagerAdapter.updateScreenSlideFile(files);
                    }
                    break;
                case MSG_ADDFILE:
                    AddFile((FileHolder)msg.obj);
                    if (uiViewPagerAdapter != null)
                        uiViewPagerAdapter.updateScreenSlideFile(files);
                    break;
                case  MSG_ADDFILES:
                    AddFiles((FileHolder[])msg.obj);
                    if (uiViewPagerAdapter != null)
                        uiViewPagerAdapter.updateScreenSlideFile(files);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }




    private final String TAG =ActivityFreeDcamMain.class.getSimpleName();
    //listen to orientation changes
    private OrientationHandler orientationHandler;

    private PagingView uiViewPager;
    private CameraUiSlidePagerAdapter uiViewPagerAdapter;
    private LocationHandler locationHandler;

    private boolean activityIsResumed= false;
    private int currentorientation = 0;

    private SecureCamera mSecureCamera = new SecureCamera(this);

    private LinearLayout nightoverlay;



    private UpdateScreenSlideHandler updateScreenSlideHandler;

    private CameraFragmentManager cameraFragmentManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSecureCamera.onCreate();
    }


    @Override
    protected void onDestroy() {
        cameraFragmentManager.destroy();
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
        cameraFragmentManager = new CameraFragmentManager(getSupportFragmentManager(), id.cameraFragmentHolder, getApplicationContext(), this);
        storageHandler = new StorageFileHandler();
        updateScreenSlideHandler = new UpdateScreenSlideHandler();
        //listen to phone orientation changes
        orientationHandler = new OrientationHandler(this, this);
        bitmapHelper = new BitmapHelper(getApplicationContext(),getResources().getDimensionPixelSize(R.dimen.image_thumbnails_size),this);

        locationHandler = new LocationHandler(this);
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
        if (!AppSettingsManager.getInstance().isInit() || cameraFragmentManager == null)
            return;
        cameraFragmentManager.resume();
        //check if we have the permissions. its needed because onResume gets called while we ask in ActivityAbstract.onCreate().
        getPermissionHandler().hasCameraAndSdPermission(new PermissionHandler.PermissionCallback() {
            @Override
            public void permissionGranted(boolean granted) {
                if (granted) {
                    if (AppSettingsManager.getInstance().appVersionHasChanged())
                        cameraFragmentManager.switchCameraFragment();
                    else {
                        if (uiViewPagerAdapter == null)
                            initScreenSlide();
                        loadCameraFragment();
                    }
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
        if(orientationHandler != null)
            orientationHandler.Stop();
        if (locationHandler != null)
            locationHandler.stopLocationListining();
        activityIsResumed = false;
    }

    private void initScreenSlide() {
        uiViewPagerAdapter = new CameraUiSlidePagerAdapter(getSupportFragmentManager(),onThumbBackClick);
        if (uiViewPager == null)
            uiViewPager = (PagingView)findViewById(id.viewPager_fragmentHolder);
        uiViewPager.setOffscreenPageLimit(2);
        uiViewPager.setAdapter(uiViewPagerAdapter);
        uiViewPager.setCurrentItem(1);
    }

    /*
    load the camerafragment to ui
     */
    private void loadCameraFragment() {
        Log.d(TAG, "loading cameraWrapper");
        if(orientationHandler == null)
            return;
        orientationHandler.Start();

        cameraFragmentManager.switchCameraFragment();
    }

    /**
     * Unload the current active camerafragment
     */
    private void unloadCameraFragment() {
        Log.d(TAG, "destroying cameraWrapper");
        if(orientationHandler != null)
            orientationHandler.Stop();

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

            if (AppSettingsManager.getInstance().getApiString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(StringUtils.VoLP))
                appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_UP;
            else if (AppSettingsManager.getInstance().getApiString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(StringUtils.VoLM))
                appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_DOWN;
            else if (AppSettingsManager.getInstance().getApiString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(StringUtils.Hook)
                    || TextUtils.isEmpty(AppSettingsManager.getInstance().getApiString(AppSettingsManager.SETTING_EXTERNALSHUTTER)))
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
    public void OrientationChanged(int orientation) {
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
        updateScreenSlideHandler.update();
    }

    @Override
    public void DisablePagerTouch(boolean disable) {
        uiViewPager.EnableScroll(!disable);
    }

    @Override
    public LocationHandler getLocationHandler() {
        return locationHandler;
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
        updateScreenSlideHandler.update();
    }

    //get called when the back button from screenslidefragment gets clicked
    private final ScreenSlideFragment.I_ThumbClick onThumbBackClick = new ScreenSlideFragment.I_ThumbClick() {
        @Override
        public void onThumbClick(int position,View view)
        {
            //show cameraui
            if (uiViewPager != null)
                uiViewPager.setCurrentItem(1);
        }

    };


    @Override
    public void WorkHasFinished(final FileHolder fileHolder) {
        updateScreenSlideHandler.addFile(fileHolder);
        Log.d(TAG, "newImageRecieved:" + fileHolder.getFile().getAbsolutePath());
    }

    @Override
    public void WorkHasFinished(final FileHolder[] fileHolder) {
        updateScreenSlideHandler.addFiles(fileHolder);
    }

    @Override
    public void onCameraOpen(String message) {

    }

    @Override
    public void onCameraOpenFinish(String message) {
        //in case the featuredetector runned bevor, uiViewPagerAdapter is null.
        //thats the correct behavior because we dont want that the helpview overlay the featuredetector on first start
        if (uiViewPagerAdapter == null)
            initScreenSlide();
        //note the ui that cameraFragment is loaded
        uiViewPagerAdapter.setCameraFragment(cameraFragmentManager.getCameraFragment());
        if (AppSettingsManager.getInstance().getApiString(AppSettingsManager.SETTING_LOCATION).equals(AppSettingsManager.getInstance().getResString(R.string.on_))
                && getPermissionHandler().hasLocationPermission(null))
            locationHandler.startLocationListing();

        SetNightOverlay();
        if(getPermissionHandler().hasExternalSDPermission(null))
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
    public void onCameraStatusChanged(String status) {

    }


    @Override
    public void SetNightOverlay() {
        if (nightoverlay == null)
            nightoverlay = (LinearLayout) findViewById(id.nightoverlay);
        if (AppSettingsManager.getInstance().getBoolean(AppSettingsManager.SETTINGS_NIGHTOVERLAY, false))
            nightoverlay.setVisibility(View.VISIBLE);
        else
            nightoverlay.setVisibility(View.GONE);
    }

    @Override
    public void runFeatureDetector() {
        unloadCameraFragment();
        cameraFragmentManager.switchCameraFragment();
    }
}
