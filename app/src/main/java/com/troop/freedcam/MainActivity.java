package com.troop.freedcam;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.troop.freedcam.apis.AbstractCameraFragment;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.manager.FileLogger;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;
import com.troop.freedcam.ui.handler.ApiHandler;
import com.troop.freedcam.ui.handler.ApiHandler.ApiEvent;
import com.troop.freedcam.ui.handler.HardwareKeyHandler;
import com.troop.freedcam.ui.handler.ThemeHandler;
import com.troop.freedcam.ui.handler.TimerHandler;
import com.troop.freedcam.ui.menu.I_orientation;
import com.troop.freedcam.ui.menu.OrientationHandler;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;

import troop.com.imageviewer.ScreenSlideFragment;

/**
 * Created by troop on 18.08.2014.
 */
public class MainActivity extends FragmentActivity implements I_orientation, I_error, I_CameraChangedListner, I_Activity, I_ModuleEvent, AbstractCameraFragment.CamerUiWrapperRdy, ApiEvent
{
    protected ViewGroup appViewGroup;
    static OrientationHandler orientationHandler;
    int flags;
    private static String TAG = StringUtils.TAG + MainActivity.class.getSimpleName();
    private static String TAGLIFE = StringUtils.TAG + "LifeCycle";
    static AppSettingsManager appSettingsManager;
    static HardwareKeyHandler hardwareKeyHandler;
    MainActivity activity;
    static ApiHandler apiHandler;
    static TimerHandler timerHandler;
    public ThemeHandler themeHandler;
    static AbstractCameraFragment cameraFragment;
    ScreenSlideFragment imageViewerFragment;
    private boolean debuglogging = false;
    FileLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);
        Log.d(TAGLIFE,"onCreate");
        DeviceUtils.SETCONTEXT(getApplicationContext());

        orientationHandler = new OrientationHandler(this, this);
        checkStartLogging();
        flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appViewGroup = (ViewGroup) inflater.inflate(R.layout.main_v2, null);
        setContentView(R.layout.main_v2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkMarshmallowPermissions();
        }
        else
            createHandlers();
    }


    private void checkMarshmallowPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE
                    },
                    1);
        }
        else
            createHandlers();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED
                && grantResults[2] == PackageManager.PERMISSION_GRANTED
                && grantResults[3] == PackageManager.PERMISSION_GRANTED
                && grantResults[4] == PackageManager.PERMISSION_GRANTED
                && grantResults[5] == PackageManager.PERMISSION_GRANTED
                && grantResults[6] == PackageManager.PERMISSION_GRANTED)
        {
            createHandlers();
        }
        else
            this.finish();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        HIDENAVBAR();
        Log.d(TAGLIFE, "Activity onResume");
    }
    @Override
    protected void onPause()
    {
        super.onPause();

        Log.d(TAGLIFE, "Activity onPause");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAGLIFE, "Focus has changed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + hasFocus);
        if (hasFocus)
            HIDENAVBAR();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private void checkStartLogging()
    {
        File debugfile = new File(StringUtils.GetInternalSDCARD() + StringUtils.freedcamFolder +"DEBUG");
        if (debugfile.exists()) {
            debuglogging = true;
            logger = new FileLogger();
            logger.StartLogging();
        }
    }

    private void createHandlers() {

        this.activity =this;
        appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this), this);
        themeHandler = new ThemeHandler(this, appSettingsManager);
        timerHandler = new TimerHandler(this);
        apiHandler = new ApiHandler(appSettingsManager, this);
        apiHandler.CheckApi();
        hardwareKeyHandler = new HardwareKeyHandler(this, appSettingsManager);
        if (cameraFragment != null)
            themeHandler.GetThemeFragment(true, cameraFragment.GetCameraUiWrapper());
        else
            themeHandler.GetThemeFragment(true, null);
    }

    /**
     * gets called from ApiHandler when apidetection has finished
     * thats loads the CameraFragment on appstart
     */
    @Override
    public void apiDetectionDone()
    {
        loadCameraUiWrapper();
        orientationHandler.Start();
    }

    private void loadCameraUiWrapper()
    {
            Log.d(TAG, "loading cameraWrapper");
            destroyCameraUiWrapper();
            cameraFragment = apiHandler.getCameraFragment(appSettingsManager);
            cameraFragment.Init(appSettingsManager, this);
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.left_to_right_enter, R.anim.left_to_right_exit);
            transaction.add(R.id.cameraFragmentHolder, cameraFragment, "CameraFragment");
            transaction.commitAllowingStateLoss();
            Log.d(TAG, "loaded cameraWrapper");

    }

    private void destroyCameraUiWrapper()
    {
        //themeHandler.SetCameraUIWrapper(null);
        if (cameraFragment != null) {
            cameraFragment.DestroyCameraUiWrapper();
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
            transaction.remove(cameraFragment);
            transaction.commitAllowingStateLoss();
            cameraFragment = null;
        }
        orientationHandler.Stop();
    }

    //gets thrown when the cameraui wrapper is created sucessfull and all items are up like modulehandler
    @Override
    public void onCameraUiWrapperRdy(AbstractCameraUiWrapper cameraUiWrapper)
    {
        cameraUiWrapper.SetCameraChangedListner(this);
        cameraUiWrapper.moduleHandler.SetWorkListner(orientationHandler);
        initCameraUIStuff(cameraUiWrapper);
        //orientationHandler = new OrientationHandler(this, cameraUiWrapper);
        Log.d(TAG, "add events");
        cameraUiWrapper.moduleHandler.moduleEventHandler.AddRecoderChangedListner(timerHandler);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(timerHandler);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(themeHandler);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
    }



    private void initCameraUIStuff(AbstractCameraUiWrapper cameraUiWrapper)
    {
        themeHandler.getCurrenttheme().SetCameraUIWrapper(cameraUiWrapper);
        hardwareKeyHandler.SetCameraUIWrapper(cameraUiWrapper);

    }

    public void HIDENAVBAR()
    {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else
        {
            //HIDE nav and action bar
            final View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(flags);
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if (visibility > 0) {
                        if (Build.VERSION.SDK_INT >= 16)
                            getWindow().getDecorView().setSystemUiVisibility(flags);
                    }
                }
            });
        }
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
    public int OrientationChanged(int orientation)
    {
        if (orientation != currentorientation)
        {
            currentorientation = orientation;
            if (cameraFragment.GetCameraUiWrapper() != null && cameraFragment.GetCameraUiWrapper().cameraHolder != null && cameraFragment.GetCameraUiWrapper().camParametersHandler != null)
                cameraFragment.GetCameraUiWrapper().camParametersHandler.SetPictureOrientation(orientation);
            if (orientation == 0 || orientation == 180) {
                LinearLayout uiholder = (LinearLayout) findViewById(R.id.themeFragmentholder);
                uiholder.setRotation(orientation);
                uiholder.requestLayout();
            }
        }
        return orientation;
    }

    @Override
    public void OnError(final String error)
    {
    }

    public void SwitchCameraAPI(String value)
    {
        loadCameraUiWrapper();
    }

    @Override
    public void SetTheme(String Theme)
    {
        themeHandler.GetThemeFragment(true, cameraFragment.GetCameraUiWrapper());
    }


    @Override
    public SurfaceView GetSurfaceView() {
        return cameraFragment.getSurfaceView();
    }

    @Override
    public int GetPreviewWidth()
    {
        try
        {
            return cameraFragment.getPreviewWidth();
        }
        catch (NullPointerException ex)
        {
            return GetScreenSize()[0];
        }
    }

    @Override
    public int GetPreviewHeight()
    {
        try{
            return cameraFragment.getPreviewHeight();
        }
        catch (NullPointerException ex)
        {
            return GetScreenSize()[1];
        }
    }

    @Override
    public int GetPreviewLeftMargine()
    {
        try{
            return cameraFragment.getMargineLeft();
        }
        catch (NullPointerException ex)
        {
            return 0;
        }
    }

    @Override
    public int GetPreviewRightMargine()
    {
        try{
            return cameraFragment.getMargineRight();
        }
        catch (NullPointerException ex)
        {
            return 0;
        }
    }

    @Override
    public int GetPreviewTopMargine() {

        try{
            return cameraFragment.getMargineTop();
        }
        catch (NullPointerException ex)
        {
            return 0;
        }
    }

    @Override
    public int[] GetScreenSize() {
        int width = 0;
        int height = 0;

        if (Build.VERSION.SDK_INT >= 17)
        {
            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            Point size =  new Point();
            wm.getDefaultDisplay().getRealSize(size);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                width = size.x;
                height = size.y;
            }
            else
            {
                height = size.x;
                width = size.y;
            }
        }
        else
        {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            {
                width = metrics.widthPixels;
                height = metrics.heightPixels;
            }
            else
            {
                width = metrics.heightPixels;
                height = metrics.widthPixels;
            }
        }
        return new int[]{width,height};
    }

    @Override
    public void
    ShowHistogram(boolean enable) {}

    @Override
    public void loadImageViewerFragment(File file)
    {
        try {
            imageViewerFragment = new ScreenSlideFragment();
            imageViewerFragment.Set_I_Activity(this);
            android.support.v4.app.FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
            transaction.replace(R.id.themeFragmentholder, imageViewerFragment);
            transaction.commitAllowingStateLoss();
        }
        catch (Exception ex)
        {
            Log.d("Freedcam",ex.getMessage());
        }
    }

    @Override
    public void loadCameraUiFragment()
    {
        themeHandler.GetThemeFragment(true, cameraFragment.GetCameraUiWrapper());
    }

    @Override
    public void closeActivity()
    {
        this.finish();
        if (logger != null)
            logger.StopLogging();
    }

    @Override
    public void onCameraOpen(String message) {}

    @Override
    public void onCameraOpenFinish(String message) {}

    @Override
    public void onCameraClose(String message) {}

    @Override
    public void onPreviewOpen(String message) {}

    @Override
    public void onPreviewClose(String message) {}

    @Override
    public void onCameraError(String error) {}

    @Override
    public void onCameraStatusChanged(String status) {}

    @Override
    public void onModuleChanged(I_Module module) {}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        Log.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public String ModuleChanged(String module)
    {
        return null;
    }

}
