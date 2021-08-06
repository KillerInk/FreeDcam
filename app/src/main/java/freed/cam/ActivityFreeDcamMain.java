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

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.ActivityAbstract;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.Size;
import freed.cam.event.camera.CameraHolderEvent;
import freed.cam.histogram.HistogramController;
import freed.cam.previewpostprocessing.PreviewController;
import freed.cam.ui.CameraUiSlidePagerAdapter;
import freed.cam.ui.KeyPressedController;
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
import freed.utils.OrientationManager;
import freed.utils.PermissionManager;
import freed.utils.SoundPlayer;
import freed.viewer.screenslide.views.ScreenSlideFragment;
import hilt.CameraApiManagerEntryPoint;
import hilt.HistogramControllerEntryPoint;
import hilt.LocationManagerEntryPoint;
import hilt.OrientationMangerEntryPoint;
import hilt.PreviewControllerEntryPoint;
import hilt.SoundPlayerEntryPoint;
import hilt.UserMessageHandlerEntryPoint;

/**
 * Created by troop on 18.08.2014.
 */
@AndroidEntryPoint
public class ActivityFreeDcamMain extends ActivityAbstract
        implements
            SecureCamera.SecureCameraActivity, CameraHolderEvent
{

    /*
        provide hilt instance to non ui classes
     */
    public static LocationManager locationManager()
    {
        return getEntryPointFromActivity(LocationManagerEntryPoint.class).locationManager();
    }
    /*
        provide hilt instance to non ui classes
     */
    public static OrientationManager orientationManager()
    {
        return getEntryPointFromActivity(OrientationMangerEntryPoint.class).orientationManager();
    }

    public static PreviewController previewController()
    {
        return getEntryPointFromActivity(PreviewControllerEntryPoint.class).previewController();
    }

    public static CameraApiManager cameraApiManager()
    {
        return getEntryPointFromActivity(CameraApiManagerEntryPoint.class).cameraApiManager();
    }

    public static UserMessageHandler userMessageHandler()
    {
        return getEntryPointFromActivity(UserMessageHandlerEntryPoint.class).userMessageHandler();
    }

    public static SoundPlayer soundPlayer()
    {
        return getEntryPointFromActivity(SoundPlayerEntryPoint.class).soundPlayer();
    }

    public static HistogramController histogramController()
    {
        return getEntryPointFromActivity(HistogramControllerEntryPoint.class).histogramcontroller();
    }

    @Override
    public void onCameraOpen() {

    }

    @Override
    public void onCameraOpenFinished() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //in case the featuredetector runned bevor, uiViewPagerAdapter is null.
                //thats the correct behavior because we dont want that the helpview overlay the featuredetector on first start
                if (uiViewPagerAdapter == null)
                    initScreenSlide();
                SetNightOverlay();
                if (!FileListController.needStorageAccessFrameWork) {
                    if (permissionManager.isPermissionGranted(PermissionManager.Permissions.SdCard) && (fileListController.getFiles() == null || fileListController.getFiles().size() == 0))
                        imageManager.putImageLoadTask(new LoadFreeDcamDcimDirsFilesRunner());
                }
                else
                {
                    if (fileListController.getFiles() == null || fileListController.getFiles().size() == 0)
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


    private class LoadFreeDcamDcimDirsFilesRunner extends ImageTask
    {
        @Override
        public boolean process() {
            fileListController.LoadFreeDcamDCIMDirsFiles();
            return false;
        }
    }

    private final String TAG =ActivityFreeDcamMain.class.getSimpleName();
    //listen to orientation changes
    @Inject
    OrientationManager orientationManager;
    private PagingView uiViewPager;
    private CameraUiSlidePagerAdapter uiViewPagerAdapter;
    private boolean activityIsResumed= false;
    private SecureCamera mSecureCamera = new SecureCamera(this);
    private LinearLayout nightoverlay;
    @Inject
    public CameraApiManager cameraApiManager;
    @Inject
    public SettingsManager settingsManager;
    @Inject
    FileListController fileListController;
    @Inject PermissionManager permissionManager;
    @Inject LocationManager locationManager;
    @Inject KeyPressedController keyPressedController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        Log.d(TAG,"onCreate: ");
        getLifecycle().addObserver(locationManager);
        mSecureCamera.onCreate();
        cameraApiManager.init();
        previewController().init(getSupportFragmentManager(), id.cameraFragmentHolder);
        cameraApiManager.addEventListner(this);
        //listen to phone orientation changes
        getLifecycle().addObserver(orientationManager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        cameraApiManager.destroy();
        getLifecycle().removeObserver(locationManager);
        getLifecycle().removeObserver(orientationManager);
    }

    @Override
    protected void setContentToView() {
        setContentView(layout.freedcam_main_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!FileListController.needStorageAccessFrameWork){
            if (permissionManager.isPermissionGranted(PermissionManager.Permissions.SdCard_Camera)) {
                if (mSecureCamera !=  null)
                    mSecureCamera.onResume();
            }
            else
                permissionManager.requestPermission(PermissionManager.Permissions.SdCard_Camera);
        }
        else
        {
            if (permissionManager.isPermissionGranted(PermissionManager.Permissions.Camera)) {
                if (mSecureCamera !=  null)
                    mSecureCamera.onResume();
            }
            else
                permissionManager.requestPermission(PermissionManager.Permissions.Camera);
        }
    }

    @Override
    public void onResumeTasks() {
        Log.d(TAG, "onResumeTasks() ");
        activityIsResumed = true;
        if (!settingsManager.isInit())
            settingsManager.init();

        cameraApiManager.onResume();
        if (!settingsManager.appVersionHasChanged() && uiViewPagerAdapter == null)
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
        cameraApiManager.onPause();
        settingsManager.save();
        Log.d(TAG, "onPauseTasks() ");
        if(orientationManager != null)
            orientationManager.Stop();
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


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyPressedController.onKeyDown(keyCode,event))
            return true;
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyPressedController.onKeyUp(keyCode,event))
            return true;
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
        {
            closeActivity();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        keyPressedController.onKeyMultiple(keyCode,repeatCount,event);
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if(keyPressedController.onKeyLongPressed(keyCode,event))
            return true;
        return super.onKeyLongPress(keyCode, event);
    }

    public void closeActivity()
    {
        moveTaskToBack(true);
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
            nightoverlay = findViewById(id.nightoverlay);
        Log.d(TAG, "NightOverlay:" + settingsManager.getGlobal(SettingKeys.NightOverlay).get());
        if (settingsManager.getGlobal(SettingKeys.NightOverlay).get())
            nightoverlay.setVisibility(View.VISIBLE);
        else
            nightoverlay.setVisibility(View.GONE);
    }

}
