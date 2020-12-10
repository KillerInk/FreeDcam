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
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import freed.ActivityAbstract;
import freed.cam.apis.CameraFragmentManager;
import freed.cam.events.CameraStateEvents;
import freed.cam.events.DisableViewPagerTouchEvent;
import freed.cam.events.EventBusHelper;
import freed.cam.events.EventBusLifeCycle;
import freed.cam.events.SwichCameraFragmentEvent;
import freed.cam.events.UpdateScreenSlide;
import freed.cam.ui.CameraUiSlidePagerAdapter;
import freed.cam.ui.SecureCamera;
import freed.cam.ui.themesample.PagingView;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.file.FileListController;
import freed.image.ImageManager;
import freed.image.ImageTask;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.LocationManager;
import freed.utils.Log;
import freed.utils.OrientationEvent;
import freed.utils.OrientationManager;
import freed.utils.PermissionManager;
import freed.viewer.helper.BitmapHelper;
import freed.viewer.screenslide.modelview.ScreenSlideFragmentModelView;
import freed.viewer.screenslide.views.ScreenSlideFragment;

/**
 * Created by troop on 18.08.2014.
 */
public class ActivityFreeDcamMain extends ActivityAbstract
        implements OrientationEvent,
            SecureCamera.SecureCameraActivity, EventBusLifeCycle, FileListController.NotifyFilesChanged
{

    @Override
    public void startListning() {
        EventBusHelper.register(this);
        EventBusHelper.register(fileListController);
    }

    @Override
    public void stopListning() {
        EventBusHelper.unregister(this);
        EventBusHelper.unregister(fileListController);
    }

    @Override
    public void onFilesChanged() {
        if (uiViewPagerAdapter != null)
            uiViewPagerAdapter.updateScreenSlideFile(fileListController.getFiles());
    }

    @Override
    public void onFileDeleted(int id) {

    }

    private class LoadFreeDcamDcimDirsFilesRunner extends ImageTask
    {
        @Override
        public boolean process() {
            fileListController.LoadFreeDcamDCIMDirsFiles();
            return false;
        }
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateScreenSlide(UpdateScreenSlide updateScreenSlide)
    {
       onFilesChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCameraOpenFinishEvent(CameraStateEvents.CameraOpenFinishEvent cameraOpenFinishEvent)
    {
        //in case the featuredetector runned bevor, uiViewPagerAdapter is null.
        //thats the correct behavior because we dont want that the helpview overlay the featuredetector on first start
        if (uiViewPagerAdapter == null)
            initScreenSlide();
        //note the ui that cameraFragment is loaded
        uiViewPagerAdapter.setCameraFragment(cameraFragmentManager.getCameraFragment());

        SetNightOverlay();
        if (!FileListController.needStorageAccessFrameWork) {
            if (getPermissionManager().isPermissionGranted(PermissionManager.Permissions.SdCard) && (fileListController.getFiles() == null || fileListController.getFiles().size() == 0))
                ImageManager.putImageLoadTask(new LoadFreeDcamDcimDirsFilesRunner());
        }
        else
        {
            if (fileListController.getFiles() == null || fileListController.getFiles().size() == 0)
                ImageManager.putImageLoadTask(new LoadFreeDcamDcimDirsFilesRunner());
        }
    }

    @Subscribe
    public void onSwitchCameraApiEvent(SwichCameraFragmentEvent event)
    {
        Log.d(TAG, "onSwitchCameraApiEvent");
        unloadCameraFragment();
        loadCameraFragment();
    }

    @Subscribe
    public void onDisableViewPagerTouch(DisableViewPagerTouchEvent event)
    {
        uiViewPager.EnableScroll(!event.disableIt);
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
    private UserMessageHandler userMessageHandler;
    private ScreenSlideFragmentModelView screenSlideFragmentModelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        Log.d(TAG,"onCreate: ");
        locationManager = new LocationManager(this,getLifecycle());
        userMessageHandler = new UserMessageHandler();
        userMessageHandler.setContext(getApplication());
        userMessageHandler.startListning();
        mSecureCamera.onCreate();
        cameraFragmentManager = new CameraFragmentManager(getSupportFragmentManager(), id.cameraFragmentHolder, getApplicationContext(),this);
        fileListController = new FileListController(getApplicationContext());
        fileListController.setNotifyFilesChanged(this);
        startListning();
        //listen to phone orientation changes
        orientationManager = new OrientationManager(this, this);
        bitmapHelper = new BitmapHelper(getApplicationContext(),getResources().getDimensionPixelSize(R.dimen.image_thumbnails_size));
        screenSlideFragmentModelView = new ScreenSlideFragmentModelView();
        screenSlideFragmentModelView.setFileListController(fileListController);
        screenSlideFragmentModelView.setBitmapHelper(bitmapHelper);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestory");
        stopListning();
        cameraFragmentManager.destroy();
        userMessageHandler.stopListning();
        userMessageHandler.setContext(null);

    }

    public UserMessageHandler getUserMessageHandler()
    {
        return userMessageHandler;
    }

    @Override
    protected void setContentToView() {
        setContentView(layout.freedcam_main_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!FileListController.needStorageAccessFrameWork){
            if (getPermissionManager().isPermissionGranted(PermissionManager.Permissions.SdCard_Camera)) {
                if (mSecureCamera !=  null)
                    mSecureCamera.onResume();
            }
            else
                getPermissionManager().requestPermission(PermissionManager.Permissions.SdCard_Camera);
        }
        else
        {
            if (getPermissionManager().isPermissionGranted(PermissionManager.Permissions.Camera)) {
                if (mSecureCamera !=  null)
                    mSecureCamera.onResume();
            }
            else
                getPermissionManager().requestPermission(PermissionManager.Permissions.Camera);
        }
    }

    @Override
    public void onResumeTasks() {
        Log.d(TAG, "onResumeTasks() ");
        activityIsResumed = true;
        if (!SettingsManager.getInstance().isInit())
            SettingsManager.getInstance().init();

        cameraFragmentManager.onResume();
        if (!SettingsManager.getInstance().appVersionHasChanged() && uiViewPagerAdapter == null)
            initScreenSlide();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause() ");
        // forward to secure camera to handle resume bug
        if (mSecureCamera != null)
            mSecureCamera.onPause();
    }

    @Override
    public void onPauseTasks() {
        cameraFragmentManager.onPause();
        SettingsManager.getInstance().save();
        Log.d(TAG, "onPauseTasks() ");
        if(orientationManager != null)
            orientationManager.Stop();
        activityIsResumed = false;
    }

    private void initScreenSlide() {
        uiViewPagerAdapter = new CameraUiSlidePagerAdapter(getSupportFragmentManager(),onThumbBackClick,screenSlideFragmentModelView);
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

        if (cameraFragmentManager != null)
            cameraFragmentManager.unloadCameraFragment();
        if (uiViewPagerAdapter != null)
            uiViewPager.post(()->{uiViewPagerAdapter.setCameraFragment(null);});

        Log.d(TAG, "destroyed cameraWrapper");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (activityIsResumed) {
            Log.d(TAG, "KeyCode Pressed:" + keyCode);
            int appSettingsKeyShutter = 0;

            try {
                String es = cameraFragmentManager.getCameraFragment().getParameterHandler().get(SettingKeys.EXTERNAL_SHUTTER).GetStringValue();
                if(es == null)
                    super.onKeyDown(keyCode,event);
                if (es.equals("Vol+"))
                    appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_UP;
                else if (es.equals("Vol-"))
                    appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_DOWN;
                else if (es.equals("Hook")
                        || es.isEmpty())
                    appSettingsKeyShutter = KeyEvent.KEYCODE_HEADSETHOOK;
            }
            catch (NullPointerException ex)
            {
                Log.WriteEx(ex);
            }

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
    public void closeActivity()
    {
        moveTaskToBack(true);
    }

    @Override
    public LocationManager getLocationManager() {
        return locationManager;
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
    public void SetNightOverlay() {
        if (nightoverlay == null)
            nightoverlay = findViewById(id.nightoverlay);
        Log.d(TAG, "NightOverlay:" + SettingsManager.get(SettingKeys.NightOverlay).get());
        if (SettingsManager.getGlobal(SettingKeys.NightOverlay).get())
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
