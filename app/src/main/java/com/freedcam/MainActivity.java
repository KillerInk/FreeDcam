package com.freedcam;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.freedcam.apis.apis.AbstractCameraFragment;
import com.freedcam.apis.i_camera.AbstractCameraUiWrapper;
import com.freedcam.apis.i_camera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.i_camera.interfaces.I_Module;
import com.freedcam.apis.i_camera.interfaces.I_error;
import com.freedcam.apis.i_camera.modules.I_ModuleEvent;
import com.freedcam.apis.ApiHandler;
import com.freedcam.ui.handler.HardwareKeyHandler;
import com.freedcam.ui.handler.I_orientation;
import com.freedcam.ui.handler.OrientationHandler;
import com.freedcam.ui.handler.ThemeHandler;
import com.freedcam.ui.handler.TimerHandler;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.freedviewer.AbstractFragmentActivity;
import com.troop.freedcam.R;

import java.io.File;

/**
 * Created by troop on 18.08.2014.
 */
public class MainActivity extends AbstractFragmentActivity implements I_orientation, I_error, I_CameraChangedListner, I_ModuleEvent, AbstractCameraFragment.CamerUiWrapperRdy, ApiHandler.ApiEvent
{
    private ViewGroup appViewGroup;
    private OrientationHandler orientationHandler;
    private final String TAG = StringUtils.TAG + MainActivity.class.getSimpleName();
    private final String TAGLIFE = StringUtils.TAG + "LifeCycle";
    private HardwareKeyHandler hardwareKeyHandler;
    private ApiHandler apiHandler;
    private TimerHandler timerHandler;
    private ThemeHandler themeHandler;
    private AbstractCameraFragment cameraFragment;
    private boolean debugLoggerging = false;
    private Thread.UncaughtExceptionHandler defaultEXhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appViewGroup = (ViewGroup) inflater.inflate(R.layout.main_v2, null);
        setContentView(R.layout.main_v2);

        // Setup handler for uncaught exceptions.
        defaultEXhandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                Logger.exception(e);
                defaultEXhandler.uncaughtException(thread,e);
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Logger.d(TAGLIFE, "Activity onResume");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkMarshmallowPermissions();
        }
        else {
            createHandlers();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        destroyCameraUiWrapper();
        Logger.d(TAGLIFE, "Activity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (debugLoggerging) {
            Logger.StopLogging();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkMarshmallowPermissions() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_WIFI_STATE,
                    },
                    1);
        }
        else
            createHandlers();
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
            this.finish();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getMuliplier() {
        return super.getMuliplier();
    }

    private void checkStartLoggerging()
    {
        File debugfile = new File(StringUtils.GetInternalSDCARD() + StringUtils.freedcamFolder +"DEBUG");
        if (debugfile.exists()) {
            debugLoggerging = true;
            Logger.StartLogging();
        }
    }

    private void createHandlers() {

        checkStartLoggerging();
        orientationHandler = new OrientationHandler(this, this);
        themeHandler = new ThemeHandler(this);
        timerHandler = new TimerHandler(this);
        apiHandler = new ApiHandler(this);
        apiHandler.CheckApi();
        hardwareKeyHandler = new HardwareKeyHandler(this);
        if (cameraFragment != null)
            themeHandler.GetThemeFragment(cameraFragment.GetCameraUiWrapper());
        else
            themeHandler.GetThemeFragment(null);
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
                loadCameraUiWrapper();
                orientationHandler.Start();
            }
        });

    }

    private void loadCameraUiWrapper()
    {
            Logger.d(TAG, "loading cameraWrapper");
            destroyCameraUiWrapper();
            cameraFragment = apiHandler.getCameraFragment();
            cameraFragment.Init(this);
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.left_to_right_enter, R.anim.left_to_right_exit);
            transaction.add(R.id.cameraFragmentHolder, cameraFragment, "CameraFragment");
            transaction.commitAllowingStateLoss();
            Logger.d(TAG, "loaded cameraWrapper");

    }

    private void destroyCameraUiWrapper()
    {
        //themeHandler.SetCameraUIWrapper(null);
        Logger.d(TAG, "destroying cameraWrapper");
        if(orientationHandler != null)
            orientationHandler.Stop();

        if (cameraFragment != null) {
            //cameraFragment.DestroyCameraUiWrapper();
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
            transaction.remove(cameraFragment);
            transaction.commitAllowingStateLoss();
            cameraFragment = null;
        }
        Logger.d(TAG, "destroyed cameraWrapper");

    }

    //gets thrown when the cameraui wrapper is created sucessfull and all items are up like modulehandler
    @Override
    public void onCameraUiWrapperRdy(AbstractCameraUiWrapper cameraUiWrapper)
    {
        cameraUiWrapper.SetCameraChangedListner(this);
        cameraUiWrapper.moduleHandler.SetWorkListner(orientationHandler);
        initCameraUIStuff(cameraUiWrapper);
        //orientationHandler = new OrientationHandler(this, cameraUiWrapper);
        Logger.d(TAG, "add events");
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
            /*if (orientation == 0 || orientation == 180) {
                LinearLayout uiholder = (LinearLayout) findViewById(R.id.themeFragmentholder);
                uiholder.setRotation(orientation);
                uiholder.requestLayout();
            }*/
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
        themeHandler.GetThemeFragment(cameraFragment.GetCameraUiWrapper());
    }

    @Override
    public int[] GetScreenSize() {
        int width = 0;
        int height = 0;

        if (Build.VERSION.SDK_INT >= 17) {
            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            Point size =  new Point();
            wm.getDefaultDisplay().getRealSize(size);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                width = size.x;
                height = size.y;
            }
            else {
                height = size.x;
                width = size.y;
            }
        }
        else
        {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                width = metrics.widthPixels;
                height = metrics.heightPixels;
            }
            else {
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
    }

    @Override
    public void loadCameraUiFragment()
    {
        themeHandler.GetThemeFragment(cameraFragment.GetCameraUiWrapper());
    }

    @Override
    public void closeActivity()
    {
        this.finish();

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

        Logger.d(TAG, "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public String ModuleChanged(String module)
    {
        return null;
    }

    @Override
    public void ChooseSDCard(I_OnActivityResultCallback callback)
    {
        super.ChooseSDCard(callback);
    }

}
