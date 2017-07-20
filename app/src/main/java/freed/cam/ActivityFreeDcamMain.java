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


import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.troop.freedcam.BuildConfig;
import com.troop.freedcam.R;
import com.troop.freedcam.R.anim;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.ActivityAbstract;
import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraStateEvents;
import freed.cam.apis.camera1.Camera1Fragment;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.sonyremote.SonyCameraRemoteFragment;
import freed.cam.featuredetector.CameraFeatureDetectorFragment;
import freed.cam.featuredetector.CameraFeatureDetectorFragment.FeatureDetectorEvents;
import freed.cam.ui.SecureCamera;
import freed.cam.ui.handler.I_orientation;
import freed.cam.ui.handler.OrientationHandler;
import freed.cam.ui.handler.TimerHandler;
import freed.cam.ui.themesample.PagingView;
import freed.cam.ui.themesample.cameraui.CameraUiFragment;
import freed.cam.ui.themesample.settings.SettingsMenuFragment;
import freed.utils.AppSettingsManager;
import freed.utils.LocationHandler;
import freed.utils.Log;
import freed.utils.PermissionHandler;
import freed.utils.RenderScriptHandler;
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
    private final String TAG =ActivityFreeDcamMain.class.getSimpleName();
    //listen to orientation changes
    private OrientationHandler orientationHandler;

    //holds the current api camerafragment
    private CameraFragmentAbstract cameraFragment;
    //private SampleThemeFragment sampleThemeFragment;
    private RenderScriptHandler renderScriptHandler;

    private PagingView mPager;
    private PagerAdapter mPagerAdapter;
    private CameraUiFragment cameraUiFragment;
    private SettingsMenuFragment settingsMenuFragment;
    private ScreenSlideFragment screenSlideFragment;
    private LocationHandler locationHandler;

    private boolean activityIsResumed= false;
    private int currentorientation = 0;

    private SecureCamera mSecureCamera = new SecureCamera(this);

    private LinearLayout nightoverlay;
    private CameraFeatureDetectorFragment fd;

    protected Object cameraLock = new Object();
    protected HandlerThread mBackgroundThread;
    protected Handler mBackgroundHandler;

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        synchronized (cameraLock) {
            mBackgroundThread = new HandlerThread("CameraBackground");
            mBackgroundThread.start();
            mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        }
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread()
    {
        synchronized (cameraLock) {
            Log.d(TAG, "stopBackgroundThread");
            if (mBackgroundThread == null)
                return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mBackgroundThread.quitSafely();
            } else
                mBackgroundThread.quit();
            try {
                mBackgroundThread.join();
                mBackgroundThread = null;
                mBackgroundHandler = null;
            } catch (InterruptedException e) {
                Log.WriteEx(e);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSecureCamera.onCreate();
        startBackgroundThread();
    }


    @Override
    protected void onDestroy() {
        stopBackgroundThread();
        super.onDestroy();
    }

    @Override
    protected void setContentToView() {
        setContentView(layout.freedcam_main_activity);
    }

    @Override
    protected void initOnCreate() {
        super.initOnCreate();

        bitmapHelper =new BitmapHelper(getApplicationContext(),getResources().getDimensionPixelSize(R.dimen.image_thumbnails_size),this);
        storageHandler = new StorageFileHandler(this);


        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT)
            renderScriptHandler = new RenderScriptHandler(getApplicationContext());
        locationHandler = new LocationHandler(this);
        mPager = (PagingView)findViewById(id.viewPager_fragmentHolder);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);

        nightoverlay = (LinearLayout) findViewById(id.nightoverlay);

        //listen to phone orientation changes
        orientationHandler = new OrientationHandler(this, this);
        orientationHandler.Start();

    }

    private PermissionHandler.PermissionCallback onLocationPermission = new PermissionHandler.PermissionCallback() {
        @Override
        public void permissionGranted(boolean granted) {
            Log.d(TAG, "locationPermission Granted:" + granted);
            if (granted) {
                locationHandler.startLocationListing();
            }
        }
    };


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
        if (getAppSettings() == null)
            return;
        if (getPermissionHandler().hasExternalSDPermission(null)) {
            if (getPermissionHandler().hasCameraPermission(null)) {
                if ((!getAppSettings().areFeaturesDetected() || BuildConfig.VERSION_CODE != getAppSettings().getAppVersion()) && fd == null) {
                    loadFeatureDetector();
                } else if (fd == null)
                    loadcam();
            }
        }
    }

    private void loadFeatureDetector() {
        Log.d(TAG, "Start FeatureDetector");
        getAppSettings().RESET();
        fd = new CameraFeatureDetectorFragment();
        fd.setAppSettingsManagerAndListner(getAppSettings(), fdevent);
        replaceCameraFragment(fd, "FeatureDetector");
    }

    private void loadcam()
    {
        if (getAppSettings() == null)
            return;
        loadCameraFragment();

        if (getAppSettings().getApiString(AppSettingsManager.SETTING_LOCATION).equals(getAppSettings().getResString(R.string.on_)))
            getPermissionHandler().hasLocationPermission(onLocationPermission);
        SetNightOverlay();
        if(getPermissionHandler().hasExternalSDPermission(null))
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LoadFreeDcamDCIMDirsFiles();
                }
            }).start();
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
        unloadCameraFragment();
        if(orientationHandler != null)
            orientationHandler.Stop();
        if (locationHandler != null)
            locationHandler.stopLocationListining();
        activityIsResumed = false;
    }

    private FeatureDetectorEvents fdevent = new FeatureDetectorEvents()
    {
        @Override
        public void featuredetectorDone() {
            Log.d(TAG,"FD done, load cameraFragment");
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.right_to_left_enter, anim.right_to_left_exit);
            transaction.remove(fd);
            transaction.commit();
            fd = null;
            loadCameraFragment();
        }
    };

    /*
    load the camerafragment to ui
     */
    private void loadCameraFragment() {
        Log.d(TAG, "loading cameraWrapper");
        if(orientationHandler == null)
            return;
        orientationHandler.Start();

        if (cameraFragment == null) {
            //get new cameraFragment
            if (getAppSettings().getCamApi().equals(AppSettingsManager.API_SONY))
            {
                cameraFragment = new SonyCameraRemoteFragment();
                cameraFragment.SetRenderScriptHandler(renderScriptHandler);

            }
            //create Camera2Fragment
            else if (getAppSettings().getCamApi().equals(AppSettingsManager.API_2))
            {
                cameraFragment = new Camera2Fragment();
                cameraFragment.SetRenderScriptHandler(renderScriptHandler);
            }
            else //default is Camera1Fragment is supported by all devices
            {
                cameraFragment = new Camera1Fragment();
                cameraFragment.SetRenderScriptHandler(renderScriptHandler);
            }
            cameraFragment.setHandler(mBackgroundHandler,cameraLock);
            cameraFragment.SetAppSettingsManager(getAppSettings());

            cameraFragment.setCameraStateChangedListner(this);
            //load the cameraFragment to ui
            replaceCameraFragment(cameraFragment,"CameraFragment");
            Log.d(TAG, "loaded cameraWrapper");
        }
        else { //resuse fragments
            cameraFragment.startCamera();

        }
    }

    private void replaceCameraFragment(Fragment fragment, String id)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.left_to_right_enter, anim.left_to_right_exit);
        transaction.replace(R.id.cameraFragmentHolder, fragment, id);
        transaction.commit();
    }

    /**
     * Unload the current active camerafragment
     */
    private void unloadCameraFragment() {
        Log.d(TAG, "destroying cameraWrapper");
        if(orientationHandler != null)
            orientationHandler.Stop();

        if (cameraFragment != null) {
            //kill the cam befor the fragment gets removed to make sure when
            //new cameraFragment gets created and its texture view is created the cam get started
            //when its done in textureview/surfaceview destroy method its already to late and we get a security ex lack of privilege
            cameraFragment.stopCamera();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.right_to_left_enter, anim.right_to_left_exit);
            transaction.remove(cameraFragment);
            transaction.commit();
            cameraFragment = null;
            if (cameraUiFragment != null) {
                cameraUiFragment.setCameraToUi(null);
            }
            if (settingsMenuFragment != null)
                settingsMenuFragment.setCameraToUi(null);
        }
        Log.d(TAG, "destroyed cameraWrapper");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (activityIsResumed) {
            Log.d(TAG, "KeyCode Pressed:" + keyCode);
            int appSettingsKeyShutter = 0;

            if (getAppSettings().getApiString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(StringUtils.VoLP))
                appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_UP;
            else if (getAppSettings().getApiString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(StringUtils.VoLM))
                appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_DOWN;
            else if (getAppSettings().getApiString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(StringUtils.Hook) || getAppSettings().getApiString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(""))
                appSettingsKeyShutter = KeyEvent.KEYCODE_HEADSETHOOK;

            if (keyCode == KeyEvent.KEYCODE_3D_MODE
                    || keyCode == KeyEvent.KEYCODE_POWER
                    || keyCode == appSettingsKeyShutter
                    || keyCode == KeyEvent.KEYCODE_UNKNOWN
                    || keyCode == KeyEvent.KEYCODE_CAMERA) {
                cameraFragment.getModuleHandler().startWork();
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
        finish();//moveTaskToBack(true);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                synchronized (files) {
                    if (screenSlideFragment != null)
                        screenSlideFragment.NotifyDATAhasChanged(files);
                }
            }
        });
    }

    @Override
    public void DisablePagerTouch(boolean disable) {
        mPager.EnableScroll(!disable);
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (screenSlideFragment != null)
                    screenSlideFragment.NotifyDATAhasChanged(files);
            }
        });
    }

    //get called when the back button from screenslidefragment gets clicked
    private final ScreenSlideFragment.I_ThumbClick onThumbBackClick = new ScreenSlideFragment.I_ThumbClick() {
        @Override
        public void onThumbClick(int position,View view)
        {
            //show cameraui
            if (mPager != null)
                mPager.setCurrentItem(1);
        }

    };


    @Override
    public void WorkHasFinished(final FileHolder fileHolder) {
        Log.d(TAG, "newImageRecieved:" + fileHolder.getFile().getAbsolutePath());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AddFile(fileHolder);
                if (screenSlideFragment != null)
                    screenSlideFragment.NotifyDATAhasChanged(files);
            }
        });
    }

    @Override
    public void WorkHasFinished(final FileHolder[] fileHolder) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AddFiles(fileHolder);
                if (screenSlideFragment != null)
                    screenSlideFragment.NotifyDATAhasChanged(files);
            }
        });
    }

    @Override
    public void onCameraOpen(String message) {

    }

    @Override
    public void onCameraOpenFinish(String message) {
        //note the ui that cameraFragment is loaded
        if (cameraUiFragment != null) {
            cameraUiFragment.setCameraToUi(cameraFragment);
        }
        if (settingsMenuFragment != null)
            settingsMenuFragment.setCameraToUi(cameraFragment);
        Log.d(TAG, "add events");

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


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (settingsMenuFragment == null)
                    settingsMenuFragment = new SettingsMenuFragment();
                if (settingsMenuFragment != null) {
                    settingsMenuFragment.setCameraToUi(cameraFragment);
                }
                return settingsMenuFragment;
            }
            else if (position == 2) {
                if (screenSlideFragment == null)
                    screenSlideFragment = new ScreenSlideFragment();
                if (screenSlideFragment != null) {

                    screenSlideFragment.SetOnThumbClick(onThumbBackClick);
                }
                return screenSlideFragment;
            }
            else {
                if (cameraUiFragment == null) {
                    cameraUiFragment = new CameraUiFragment();
                }
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

    @Override
    public void SetNightOverlay() {
        if (getAppSettings().getBoolean(AppSettingsManager.SETTINGS_NIGHTOVERLAY, false))
            nightoverlay.setVisibility(View.VISIBLE);
        else
            nightoverlay.setVisibility(View.GONE);
    }

    @Override
    public void runFeatureDetector() {
        unloadCameraFragment();
        loadFeatureDetector();
    }
}
