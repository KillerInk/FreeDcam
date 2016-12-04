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


import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.troop.freedcam.R.anim;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import freed.ActivityAbstract;
import freed.cam.apis.ApiHandler;
import freed.cam.apis.ApiHandler.ApiEvent;
import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraFragmentAbstract;
import freed.cam.apis.basecamera.CameraStateEvents;
import freed.cam.apis.basecamera.modules.I_WorkEvent;
import freed.cam.apis.basecamera.modules.ModuleInterface;
import freed.cam.apis.sonyremote.SonyCameraFragment;
import freed.cam.ui.SecureCamera;
import freed.cam.ui.handler.I_orientation;
import freed.cam.ui.handler.OrientationHandler;
import freed.cam.ui.handler.TimerHandler;
import freed.cam.ui.themesample.PagingView;
import freed.cam.ui.themesample.cameraui.CameraUiFragment;
import freed.cam.ui.themesample.settings.SettingsMenuFragment;
import freed.utils.AppSettingsManager;
import freed.utils.LocationHandler;
import freed.utils.RenderScriptHandler;
import freed.utils.StringUtils;
import freed.viewer.holder.FileHolder;
import freed.viewer.screenslide.ScreenSlideFragment;

/**
 * Created by troop on 18.08.2014.
 */
public class ActivityFreeDcamMain extends ActivityAbstract
        implements I_orientation, ApiEvent,
            SecureCamera.SecureCameraActivity, CameraStateEvents
{
    private final String TAG =ActivityFreeDcamMain.class.getSimpleName();
    //listen to orientation changes
    private OrientationHandler orientationHandler;
    //handels/load the api camerafragments
    private ApiHandler apiHandler;
    private TimerHandler timerHandler;
    //holds the current api camerafragment
    private CameraFragmentAbstract cameraFragment;
    //hold the state if logging to file is true when folder /sdcard/DCIM/DEBUG/ is created
    private boolean savelogtofile;
    //holds the default UncaughtExecptionHandler from activity wich get replaced with own to have a change to save
    //fc to file and pass it back when done and let app crash as it should
    private UncaughtExceptionHandler defaultEXhandler;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.freedcam_main_activity);

        mSecureCamera.onCreate();

        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT)
            renderScriptHandler = new RenderScriptHandler(getApplicationContext());
        bitmapHelper.SetWorkDoneListner(cacheImageRdy);
        locationHandler = new LocationHandler(this);

        //load the camera ui
        mPager = (PagingView)findViewById(id.viewPager_fragmentHolder);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        nightoverlay = (LinearLayout) findViewById(id.nightoverlay);
        createHandlers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        // forward to secure camera to handle resume bug
        mSecureCamera.onResume();
    }

    @Override
    public void onResumeTasks() {
        Log.d(TAG, "onResumeTasks()");
        if (!hasCameraPermission()) {
            return;
        }
        apiHandler.CheckApi();
        activityIsResumed = true;
        if (screenSlideFragment != null)
            screenSlideFragment.NotifyDATAhasChanged();
        if (getAppSettings().getString(AppSettingsManager.SETTING_LOCATION).equals(KEYS.ON) && hasLocationPermission())
            locationHandler.startLocationListing();
        SetNightOverlay();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause()");
        // forward to secure camera to handle resume bug
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

    //that finaly create all stuff needed
    private void createHandlers() {
        if (hasExternalSDPermission()) {
            Log.d(TAG, "createHandlers()");
            LoadFreeDcamDCIMDirsFiles();

        }
        //listen to phone orientation changes
        orientationHandler = new OrientationHandler(this, this);
        orientationHandler.Start();
        //used for videorecording timer
        //TODO move that into camerauifragment
        timerHandler = new TimerHandler(this);

        initApiHandler();
    }

    //setup apihandler and register listner for apiDetectionDone
    //api handler itself checks first if its a camera2 full device
    //and if yes loads camera2fragment else load camera1fragment
    private void initApiHandler() {
        apiHandler = new ApiHandler(getApplicationContext(), this, getAppSettings(), renderScriptHandler);
        //check if camera is camera2 full device
    }

    /**
     * gets called from ApiHandler when apidetection has finished
     * thats loads the CameraFragment
     */
    @Override
    public void apiDetectionDone()
    {
        mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);
        loadCameraFragment();
        orientationHandler.Start();
    }

    /*
    load the camerafragment to ui
     */
    private void loadCameraFragment() {
        Log.d(TAG, "loading cameraWrapper");

        if (cameraFragment == null) {
            //get new cameraFragment
            cameraFragment = apiHandler.getCameraFragment();
            cameraFragment.SetCameraStateChangedListner(this);
            //load the cameraFragment to ui
            //that starts the camera represent by that fragment when the surface/textureviews
            //are created and calls then onCameraUiWrapperRdy(I_CameraUiWrapper cameraUiWrapper)
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.left_to_right_enter, anim.left_to_right_exit);
            transaction.replace(id.cameraFragmentHolder, cameraFragment, "CameraFragment");
            transaction.commit();
            Log.d(TAG, "loaded cameraWrapper");
        }
        else { //resuse fragments
            cameraFragment.StartCamera();

        }
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
            if (cameraFragment.GetCameraUiWrapper() != null)
                cameraFragment.GetCameraUiWrapper().StopCamera();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.right_to_left_enter, anim.right_to_left_exit);
            transaction.remove(cameraFragment);
            transaction.commit();
            cameraFragment = null;
        }
        Log.d(TAG, "destroyed cameraWrapper");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (activityIsResumed) {
            Log.d(TAG, "KeyCode Pressed:" + keyCode);
            int appSettingsKeyShutter = 0;

            if (getAppSettings().getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(StringUtils.VoLP))
                appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_UP;
            else if (getAppSettings().getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(StringUtils.VoLM))
                appSettingsKeyShutter = KeyEvent.KEYCODE_VOLUME_DOWN;
            else if (getAppSettings().getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(StringUtils.Hook) || getAppSettings().getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(""))
                appSettingsKeyShutter = KeyEvent.KEYCODE_HEADSETHOOK;

            if (keyCode == KeyEvent.KEYCODE_3D_MODE
                    || keyCode == KeyEvent.KEYCODE_POWER
                    || keyCode == appSettingsKeyShutter
                    || keyCode == KeyEvent.KEYCODE_UNKNOWN
                    || keyCode == KeyEvent.KEYCODE_CAMERA) {
                cameraFragment.GetModuleHandler().DoWork();
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
        super.LoadFreeDcamDCIMDirsFiles();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(screenSlideFragment != null && activityIsResumed)
                    screenSlideFragment.NotifyDATAhasChanged();
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
    public void LoadFolder(FileHolder fileHolder, FormatTypes types) {
        super.LoadFolder(fileHolder, types);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (screenSlideFragment != null && activityIsResumed)
                    screenSlideFragment.NotifyDATAhasChanged();
            }
        });
    }


    private final ScreenSlideFragment.I_ThumbClick onThumbClick = new ScreenSlideFragment.I_ThumbClick() {
        @Override
        public void onThumbClick(int position,View view)
        {
            if (mPager != null)
                mPager.setCurrentItem(2);
        }
    };

    private final ScreenSlideFragment.I_ThumbClick onThumbBackClick = new ScreenSlideFragment.I_ThumbClick() {
        @Override
        public void onThumbClick(int position,View view)
        {
            if (mPager != null)
                mPager.setCurrentItem(1);
        }

    };


    @Override
    public void WorkHasFinished(final FileHolder fileHolder) {
        Log.d(TAG, "newImageRecieved:" + fileHolder.getFile().getAbsolutePath());
        /*final Bitmap b = getBitmapHelper().getBitmap(fileHolder, true);
        if (b == null) {
            return;
        }
        else {*/
        Log.d(TAG,"WorkHasFinished:"+ fileHolder.getFile().getName());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AddFile(fileHolder);
                    if (screenSlideFragment != null && activityIsResumed)
                        screenSlideFragment.NotifyDATAhasChanged();
                }
            });
        //}
    }

    private I_WorkEvent cacheImageRdy = new I_WorkEvent() {
        @Override
        public void WorkHasFinished(final FileHolder fileHolder) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"Cached Image Rdy:"+ fileHolder.getFile().getName());
                    if (screenSlideFragment != null && activityIsResumed)
                        screenSlideFragment.NotifyDATAhasChanged();
                }
            });
        }
    };

    @Override
    public void onCameraOpen(String message) {

    }

    @Override
    public void onCameraOpenFinish(String message) {
        //note the ui that cameraFragment is loaded
        if (cameraUiFragment != null) {
            cameraUiFragment.SetCameraUIWrapper(cameraFragment);
        }
        if (settingsMenuFragment != null)
            settingsMenuFragment.SetCameraUIWrapper(cameraFragment);
        Log.d(TAG, "add events");
        //register timer to to moduleevent handler that it get shown/hidden when its video or not
        //and start/stop working when recording starts/stops
        cameraFragment.GetModuleHandler().AddRecoderChangedListner(timerHandler);
        cameraFragment.GetModuleHandler().addListner(timerHandler);
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
                if (settingsMenuFragment == null) {
                    settingsMenuFragment = new SettingsMenuFragment();
                    settingsMenuFragment.SetCameraUIWrapper(cameraFragment);
                }
                return settingsMenuFragment;
            }
            else if (position == 2) {
                if (screenSlideFragment == null) {
                    screenSlideFragment = new ScreenSlideFragment();
                    screenSlideFragment.SetOnThumbClick(onThumbBackClick);
                }
                return screenSlideFragment;
            }
            else {
                if (cameraUiFragment == null)
                    cameraUiFragment = CameraUiFragment.GetInstance(onThumbClick,cameraFragment);
                return cameraUiFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

    @Override
    protected void cameraPermsissionGranted(boolean granted) {
        Log.d(TAG, "cameraPermission Granted:" + granted);
        if (granted) {
            initApiHandler();
        }
        else {
            finish();
        }
    }

    @Override
    protected void externalSDPermissionGranted(boolean granted) {
        Log.d(TAG, "externalSdPermission Granted:" + granted);
        if (granted) {
            LoadFreeDcamDCIMDirsFiles();
        }
        else {
            finish();
        }
    }

    @Override
    protected void locationPermissionGranted(boolean granted) {
        if (granted) {
            if (!(cameraFragment instanceof SonyCameraFragment))
                locationHandler.startLocationListing();
            else
                ((SonyCameraFragment)cameraFragment).StartLookUp();
        }
    }

    @Override
    public void SetNightOverlay() {
        if (getAppSettings().getBoolean(AppSettingsManager.SETTINGS_NIGHTOVERLAY, false))
            nightoverlay.setVisibility(View.VISIBLE);
        else
            nightoverlay.setVisibility(View.GONE);
    }
}
