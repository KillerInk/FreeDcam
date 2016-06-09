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

package com.freedcam;


import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.ViewGroup;

import com.AbstractFragmentActivity;
import com.freedcam.apis.ApiHandler;
import com.freedcam.apis.ApiHandler.ApiEvent;
import com.freedcam.apis.basecamera.AbstractCameraFragment;
import com.freedcam.apis.basecamera.AbstractCameraFragment.CamerUiWrapperRdy;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.ui.handler.HardwareKeyHandler;
import com.freedcam.ui.handler.I_orientation;
import com.freedcam.ui.handler.OrientationHandler;
import com.freedcam.ui.handler.TimerHandler;
import com.freedcam.ui.themesample.SampleThemeFragment;
import com.freedcam.utils.Logger;
import com.freedcam.utils.RenderScriptHandler;
import com.freedcam.utils.StringUtils;
import com.troop.freedcam.R.anim;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Created by troop on 18.08.2014.
 */
public class MainActivity extends AbstractFragmentActivity implements I_orientation, CamerUiWrapperRdy, ApiEvent
{
    private final String TAG =MainActivity.class.getSimpleName();
    private final String TAGLIFE = "LifeCycle";

    private ViewGroup appViewGroup;
    //listen to orientation changes
    private OrientationHandler orientationHandler;
    //listen to hardwarekeys
    private HardwareKeyHandler hardwareKeyHandler;
    //handels the api fragments
    private ApiHandler apiHandler;
    private TimerHandler timerHandler;
    //handel the themes and create the ui fragment
    //holds the current api fragment
    private AbstractCameraFragment cameraFragment;
    //hold the state if logging to file is true when folder /sdcard/DCIM/DEBUG/ is created
    private boolean savelogtofile = false;
    //holds the default UncaughtExecptionHandler from activity wich get replaced with own to have a change to save
    //fc to file and pass it back when done and let app crash as it should
    private UncaughtExceptionHandler defaultEXhandler;
    private SampleThemeFragment sampleThemeFragment;
    private RenderScriptHandler renderScriptHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(layout.freedcam_main_activity);

        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT)
            renderScriptHandler = new RenderScriptHandler(getApplicationContext());

        //load the cameraui
        sampleThemeFragment = new SampleThemeFragment();
        sampleThemeFragment.SetAppSettingsManagerAndBitmapHelper(appSettingsManager, bitmapHelper);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.left_to_right_enter, anim.left_to_right_exit);
        transaction.add(id.themeFragmentholder, sampleThemeFragment, "CameraFragment");
        transaction.commitAllowingStateLoss();

        if (VERSION.SDK_INT >= VERSION_CODES.M)
        {
            if (checkSelfPermission(permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED || checkSelfPermission(permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{
                        permission.CAMERA,
                        permission.READ_EXTERNAL_STORAGE,
                        permission.WRITE_EXTERNAL_STORAGE,
                        permission.RECORD_AUDIO,
                        permission.ACCESS_COARSE_LOCATION,
                        permission.ACCESS_FINE_LOCATION,
                        permission.ACCESS_WIFI_STATE,
                        permission.CHANGE_WIFI_STATE,}, 1);
            }
            else
                createHandlers();
        }
        else
            createHandlers();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (savelogtofile) {
            Logger.StopLogging();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED
                && grantResults[4] == PackageManager.PERMISSION_GRANTED
                && grantResults[5] == PackageManager.PERMISSION_GRANTED
                && grantResults[6] == PackageManager.PERMISSION_GRANTED
                && grantResults[7] == PackageManager.PERMISSION_GRANTED)
        {
            createHandlers();
        }
        else
            finish();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getMuliplier() {
        return super.getMuliplier();
    }

    private void checkSaveLogToFile()
    {
        File debugfile = new File(StringUtils.GetInternalSDCARD() + StringUtils.freedcamFolder +"DEBUG");
        if (debugfile.exists()) {
            savelogtofile = true;
            Logger.StartLogging();
        }
    }

    private void createHandlers()
    {
        Logger.d(TAG, "createHandlers()");
        //Get default handler for uncaught exceptions. to let fc app as it should
        defaultEXhandler = Thread.getDefaultUncaughtExceptionHandler();
        //set up own ex handler to have a change to catch the fc bevor app dies
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread,final Throwable e)
            {
                //yeahaw app crash print ex to logger
                if (thread != Looper.getMainLooper().getThread())
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            Logger.LogUncaughtEX(e);
                        }
                    });
                else
                    Logger.LogUncaughtEX(e);

                //set back default exhandler and let app die
                defaultEXhandler.uncaughtException(thread,e);
            }
        });

        checkSaveLogToFile();
        orientationHandler = new OrientationHandler(this, this);
        timerHandler = new TimerHandler(this);
        //setup apihandler and register listner for apiDetectionDone
        apiHandler = new ApiHandler(getApplicationContext(),this,appSettingsManager,renderScriptHandler);
        //check if camera is camera2 full device
        apiHandler.CheckApi();
        hardwareKeyHandler = new HardwareKeyHandler(this,appSettingsManager);
    }

    /**
     * gets called from ApiHandler when apidetection has finished
     * thats loads the CameraFragment on appstart
     */
    @Override
    public void apiDetectionDone()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadCameraFragment();

            }
        });

    }

    private void loadCameraFragment()
    {
        Logger.d(TAG, "loading cameraWrapper");
        unloadCameraFragment();
        cameraFragment = apiHandler.getCameraFragment();
        cameraFragment.Init(this);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(anim.left_to_right_enter, anim.left_to_right_exit);
        transaction.add(id.cameraFragmentHolder, cameraFragment, "CameraFragment");
        transaction.commitAllowingStateLoss();
        Logger.d(TAG, "loaded cameraWrapper");
        orientationHandler.Start();
    }

    private void unloadCameraFragment()
    {
        Logger.d(TAG, "destroying cameraWrapper");
        if(orientationHandler != null)
            orientationHandler.Stop();

        if (cameraFragment != null)
        {
            //kill the cam bevor the fragment gets removed to make sure when
            // new camerafragment gets created and its texture view is created the cam get started
            //when its done in textureviews destory method its already to late and we get a security ex lack of privilege
            if (cameraFragment.GetCameraUiWrapper() != null)
                cameraFragment.GetCameraUiWrapper().StopCamera();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(anim.right_to_left_enter, anim.right_to_left_exit);
            transaction.remove(cameraFragment);
            transaction.commitAllowingStateLoss();
            cameraFragment = null;
        }
        Logger.d(TAG, "destroyed cameraWrapper");
    }

    /**
     * gets thrown when the cameraui cameraUiWrapper is created sucessfull and all items are up like modulehandler
     * and rdy to register listners
     * @param cameraUiWrapper the cameraWrapper to register the listners
     */
    @Override
    public void onCameraUiWrapperRdy(I_CameraUiWrapper cameraUiWrapper)
    {
        cameraUiWrapper.GetModuleHandler().SetWorkListner(orientationHandler);
        sampleThemeFragment.SetCameraUIWrapper(cameraUiWrapper);
        hardwareKeyHandler.SetCameraUIWrapper(cameraUiWrapper);
        Logger.d(TAG, "add events");
        cameraUiWrapper.GetModuleHandler().moduleEventHandler.AddRecoderChangedListner(timerHandler);
        cameraUiWrapper.GetModuleHandler().moduleEventHandler.addListner(timerHandler);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        boolean haskey = hardwareKeyHandler.OnKeyUp(keyCode, event);
        if (!haskey)
            haskey = super.onKeyUp(keyCode, event);
        return haskey;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        hardwareKeyHandler.OnKeyDown(keyCode, event);
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event)
    {
        boolean haskey = hardwareKeyHandler.OnKeyLongPress(keyCode, event);
        if (!haskey)
            haskey = super.onKeyLongPress(keyCode, event);
        return haskey;
    }

    private int currentorientation = 0;

    @Override
    public void OrientationChanged(int orientation)
    {
        if (orientation != currentorientation)
        {
            currentorientation = orientation;
            if (cameraFragment.GetCameraUiWrapper() != null && cameraFragment.GetCameraUiWrapper().GetCameraHolder() != null && cameraFragment.GetCameraUiWrapper().GetParameterHandler() != null)
                cameraFragment.GetCameraUiWrapper().GetParameterHandler().SetPictureOrientation(orientation);
        }
    }

    @Override
    public void SwitchCameraAPI(String value)
    {
        loadCameraFragment();
    }

    @Override
    public void SetTheme(String Theme)
    {

    }



    @Override
    public void closeActivity()
    {
        moveTaskToBack(true);
    }

}
