package com.troop.freedcam.ui;


import android.content.Context;
import android.content.Intent;
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
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.troop.freedcam.R;
import com.troop.freedcam.apis.AbstractCameraFragment;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.guide.GuideHandler;
import com.troop.freedcam.ui.handler.ApiHandler;
import com.troop.freedcam.ui.handler.HardwareKeyHandler;
import com.troop.freedcam.ui.handler.HelpOverlayHandler;
import com.troop.freedcam.ui.menu.themes.classic.MessageHandler;
import com.troop.freedcam.ui.handler.ThemeHandler;
import com.troop.freedcam.ui.handler.TimerHandler;
import com.troop.freedcam.ui.menu.I_orientation;
import com.troop.freedcam.ui.menu.OrientationHandler;
import com.troop.freedcam.utils.SensorsUtil;
import com.troop.freedcam.utils.StringUtils;

import troop.com.imageviewer.ImageViewerActivity;
import troop.com.imageviewer.ImageViewerFragment;

/**
 * Created by troop on 18.08.2014.
 */
public class MainActivity_v2 extends FragmentActivity implements I_orientation, I_error, I_CameraChangedListner, I_Activity, I_ModuleEvent, AbstractCameraFragment.CamerUiWrapperRdy
{
    protected ViewGroup appViewGroup;
    protected boolean helpOverlayOpen = false;
    boolean histogramFragmentOpen = false;
    static OrientationHandler orientationHandler;
    int flags;
    static protected HelpOverlayHandler helpOverlayHandler;
    protected GuideHandler guideHandler;
    private static String TAG = StringUtils.TAG + MainActivity_v2.class.getSimpleName();
    private static String TAGLIFE = StringUtils.TAG + "LifeCycle";
    static AppSettingsManager appSettingsManager;
    static HardwareKeyHandler hardwareKeyHandler;
    MainActivity_v2 activity;
    static ApiHandler apiHandler;
    static TimerHandler timerHandler;


    public ThemeHandler themeHandler;
    public SensorsUtil sensorsUtil;
    static HistogramFragment histogramFragment;



    static AbstractCameraFragment cameraFragment;
    static ImageViewerFragment imageViewerFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(null);
        flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appViewGroup = (ViewGroup) inflater.inflate(R.layout.main_v2, null);
        setContentView(R.layout.main_v2);

    }



    private void createUI() {


        orientationHandler = new OrientationHandler(this, this);

        this.activity =this;
        appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this), this);
        themeHandler = new ThemeHandler(this, appSettingsManager);
        sensorsUtil = new SensorsUtil();
        timerHandler = new TimerHandler(this);

        //initUI

        apiHandler = new ApiHandler();

        hardwareKeyHandler = new HardwareKeyHandler(this, appSettingsManager);


        helpOverlayHandler = (HelpOverlayHandler)findViewById(R.id.helpoverlay);
        helpOverlayHandler.appSettingsManager = appSettingsManager;

        guideHandler = new GuideHandler();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.guideHolder, guideHandler, "Guide");
        transaction.commit();
        timerHandler = new TimerHandler(this);


    }

    boolean loadingWrapper = false;
    private void loadCameraUiWrapper()
    {
        if(!loadingWrapper)
        {
            Log.d(TAG, "loading cameraWrapper");
            loadingWrapper = true;
            destroyCameraUiWrapper();
            cameraFragment = apiHandler.getCameraFragment(appSettingsManager);
            cameraFragment.Init(appSettingsManager, this);
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
            transaction.add(R.id.cameraFragmentHolder, cameraFragment, "CameraFragment");
            transaction.commit();

            loadingWrapper = false;
            Log.d(TAG, "loaded cameraWrapper");


        }
    }


    @Override
    public void onCameraUiWrapperRdy()
    {

        cameraFragment.GetCameraUiWrapper().SetCameraChangedListner(this);

        cameraFragment.GetCameraUiWrapper().moduleHandler.SetWorkListner(orientationHandler);
        initCameraUIStuff(cameraFragment.GetCameraUiWrapper());
        //orientationHandler = new OrientationHandler(this, cameraUiWrapper);
        Log.d(TAG, "add events");
        cameraFragment.GetCameraUiWrapper().moduleHandler.moduleEventHandler.AddRecoderChangedListner(timerHandler);
        cameraFragment.GetCameraUiWrapper().moduleHandler.moduleEventHandler.addListner(timerHandler);
        cameraFragment.GetCameraUiWrapper().moduleHandler.moduleEventHandler.addListner(themeHandler);
        cameraFragment.GetCameraUiWrapper().moduleHandler.moduleEventHandler.addListner(this);
    }

    private void destroyCameraUiWrapper()
    {
        themeHandler.SetCameraUIWrapper(null);
        if (histogramFragment != null)
        {
            histogramFragment.stopLsn();
            histogramFragment.SetCameraUIWrapper(null);
        }
        if (cameraFragment != null) {
            cameraFragment.DestroyCameraUiWrapper();
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
            transaction.remove(cameraFragment);
            transaction.commit();
            cameraFragment = null;
        }
    }

    private void initCameraUIStuff(AbstractCameraUiWrapper cameraUiWrapper)
    {

        themeHandler.SetCameraUIWrapper(cameraUiWrapper);
        themeHandler.GetThemeFragment(true);

        hardwareKeyHandler.SetCameraUIWrapper(cameraUiWrapper);

        guideHandler.setCameraUiWrapper(cameraUiWrapper, this);
        guideHandler.SetViewG(appSettingsManager.getString(AppSettingsManager.SETTING_GUIDE));



        if (histogramFragment != null && histogramFragment.isAdded())
        {
            histogramFragment.SetCameraUIWrapper(cameraUiWrapper);
            cameraUiWrapper.moduleHandler.SetWorkListner(histogramFragment);
        }
    }

    @Override
    public void MenuActive(boolean status)
    {

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
            //final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0);

        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HIDENAVBAR();
                createUI();
                if (!appSettingsManager.getShowHelpOverlay()) {

                    helpOverlayOpen = false;
                    helpOverlayHandler.setVisibility(View.GONE);
                } else {
                    helpOverlayOpen = true;
                }
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (cameraFragment == null)
                    loadCameraUiWrapper();
                orientationHandler.Start();
            }
        });


        Log.d(TAGLIFE, "Activity onResume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();


        orientationHandler.Stop();
        destroyCameraUiWrapper();
        Log.d(TAGLIFE, "Activity onPause");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        Log.d(TAGLIFE, "Focus has changed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + hasFocus);
        if (hasFocus)
            HIDENAVBAR();
        //super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return  super.onTouchEvent(event);
    }


    @Override
    public int OrientationChanged(int orientation)
    {
        if (cameraFragment.GetCameraUiWrapper() != null && cameraFragment.GetCameraUiWrapper().cameraHolder != null && cameraFragment.GetCameraUiWrapper().camParametersHandler != null)
            cameraFragment.GetCameraUiWrapper().camParametersHandler.SetPictureOrientation(orientation);
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
        themeHandler.SetTheme(Theme);
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
    public void SetPreviewSizeChangedListner(I_PreviewSizeEvent event) {
        cameraFragment.setOnPreviewSizeChangedListner(event);
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
    public void ShowHistogram(boolean enable)
    {
        if (enable && !histogramFragmentOpen)
        {
            if(histogramFragment == null)
                histogramFragment = new HistogramFragment();
            histogramFragment.SetAppSettings(appSettingsManager, this);
            if (!histogramFragment.isAdded()) {
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
                transaction.add(R.id.histogramHolder, histogramFragment, "Histogramm");
                transaction.commit();
                histogramFragmentOpen = true;
            }
        }
        else if (!enable && histogramFragmentOpen)
        {
            histogramFragmentOpen = false;
            histogramFragment.stopLsn();
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
            transaction.remove(histogramFragment);
            transaction.commit();
        }
        if (enable && histogramFragmentOpen)
        {
            if (cameraFragment.GetCameraUiWrapper() != null)
            {
                histogramFragment.SetCameraUIWrapper(cameraFragment.GetCameraUiWrapper());
                cameraFragment.GetCameraUiWrapper().moduleHandler.SetWorkListner(histogramFragment);
                if (cameraFragment.GetCameraUiWrapper().cameraHolder.isPreviewRunning)
                    histogramFragment.strtLsn();
            }
        }
    }

    @Override
    public Context GetActivityContext() {
        return getApplicationContext();
    }

    @Override
    public void loadImageViewerFragment()
    {
        /*themeHandler.DestroyUI();

        imageViewerFragment = new ImageViewerFragment();
        imageViewerFragment.SetIActivity(this);
        android.support.v4.app.FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
        transaction.replace(R.id.MainLayout, imageViewerFragment);
        transaction.commit();*/
        Intent intent = new Intent(this, ImageViewerActivity.class);
        startActivity(intent);
    }

    @Override
    public void loadCameraUiFragment()
    {
        android.support.v4.app.FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit);
        transaction.replace(R.id.MainLayout,themeHandler.GetThemeFragment(false));
        transaction.commit();

    }

    @Override
    public void closeActivity() {
        this.finish();
    }

    @Override
    public void onCameraOpen(String message)
    {

        //else
            //progress = ProgressDialog.show(this,"", "Loading", true);
    }

    @Override
    public void onCameraOpenFinish(String message)
    {

    }

    @Override
    public void onCameraClose(String message) {

    }

    @Override
    public void onPreviewOpen(String message)
    {
        if(appSettingsManager.getString(AppSettingsManager.SETTING_HISTOGRAM).equals(StringUtils.ON) )
            ShowHistogram(true);
    }

    @Override
    public void onPreviewClose(String message) {
        ShowHistogram(false);
    }

    @Override
    public void onCameraError(String error)
    {
        if (cameraFragment.GetCameraUiWrapper() instanceof CameraUiWrapperSony)
        {

            appSettingsManager.setCamApi(AppSettingsManager.API_1);
            loadCameraUiWrapper();
        }
    }

    @Override
    public void onCameraStatusChanged(String status) {

    }

    @Override
    public void onModuleChanged(I_Module module)
    {


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        Log.d(TAG, "conf changed");
        int or =  newConfig.orientation;
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public String ModuleChanged(String module)
    {
        if ((module.equals(ModuleHandler.MODULE_PICTURE) || module.equals(ModuleHandler.MODULE_HDR)) && appSettingsManager.getString(AppSettingsManager.SETTING_HISTOGRAM).equals("true"))
            ShowHistogram(true);
        else if (!module.equals(""))
            ShowHistogram(false);
        return null;
    }

}
