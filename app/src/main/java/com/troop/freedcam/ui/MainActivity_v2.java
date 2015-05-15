package com.troop.freedcam.ui;


import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.ExtendedSurfaceView;
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
import com.troop.freedcam.ui.handler.InfoOverlayHandler;
import com.troop.freedcam.ui.handler.MessageHandler;
import com.troop.freedcam.ui.handler.PreviewHandler;
import com.troop.freedcam.ui.handler.ThemeHandler;
import com.troop.freedcam.ui.handler.ThumbnailHandler;
import com.troop.freedcam.ui.handler.TimerHandler;
import com.troop.freedcam.ui.handler.WorkHandler;
import com.troop.freedcam.ui.menu.I_orientation;
import com.troop.freedcam.ui.menu.OrientationHandler;
import com.troop.freedcam.utils.SensorsUtil;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 18.08.2014.
 */
public class MainActivity_v2 extends FragmentActivity implements I_orientation, I_error, I_CameraChangedListner, I_Activity, I_ModuleEvent
{
    protected ViewGroup appViewGroup;
    //public LinearLayout settingsLayout;

    protected boolean helpOverlayOpen = false;
    boolean histogramFragmentOpen = false;


    OrientationHandler orientationHandler;
    int flags;
    int flags2;

    protected HelpOverlayHandler helpOverlayHandler;
    protected GuideHandler guideHandler;

    private static String TAG = StringUtils.TAG + MainActivity_v2.class.getSimpleName();
    private static String TAGLIFE = StringUtils.TAG + "LifeCycle";
    //ExtendedSurfaceView cameraPreview;
    AbstractCameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;


    ThumbnailHandler thumbnailHandler;
    HardwareKeyHandler hardwareKeyHandler;



    MainActivity_v2 activity;
    ApiHandler apiHandler;
    TimerHandler timerHandler;
    public PreviewHandler previewHandler;

    //OrientationHandler orientationHandler;
    //HelpOverlayHandler helpOverlayHandler;

    InfoOverlayHandler infoOverlayHandler;
    MessageHandler messageHandler;
    public ThemeHandler themeHandler;
    public SensorsUtil sensorsUtil;

    HistogramFragment histogramFragment;
    LinearLayout ll;

    //bitmaps

    /////////////////
    int a,b = 0;


    WorkHandler workHandler;

    boolean initDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appViewGroup = (ViewGroup) inflater.inflate(R.layout.main_v2, null);
        setContentView(R.layout.main_v2);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);



        HIDENAVBAR();

        createUI();

        if (!appSettingsManager.getShowHelpOverlay())
        {

            helpOverlayOpen = false;
            helpOverlayHandler.setVisibility(View.GONE);
        }
        else
        {
            helpOverlayOpen = true;
        }

        //loadCameraUiWrapper();
    }



    private void createUI() {
        ll = (LinearLayout)findViewById(R.id.infoOverLay);

        orientationHandler = new OrientationHandler(this, this);

        this.activity =this;
        appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this), this);
        themeHandler = new ThemeHandler(this, appSettingsManager);
        sensorsUtil = new SensorsUtil();

        previewHandler = (PreviewHandler) findViewById(R.id.CameraPreview);
        previewHandler.activity = this;
        previewHandler.appSettingsManager = appSettingsManager;
        timerHandler = new TimerHandler(this);

        //initUI

        thumbnailHandler = new ThumbnailHandler(this);
        apiHandler = new ApiHandler();
        workHandler = new WorkHandler(this);

        hardwareKeyHandler = new HardwareKeyHandler(this, appSettingsManager);




        infoOverlayHandler= new InfoOverlayHandler(MainActivity_v2.this, appSettingsManager);
        messageHandler = new MessageHandler(this);



        helpOverlayHandler = (HelpOverlayHandler)findViewById(R.id.helpoverlay);
        helpOverlayHandler.appSettingsManager = appSettingsManager;

        guideHandler = new GuideHandler();
        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.guideHolder, guideHandler, "Guide");
        transaction.commit();

        //if (appSettingsManager.getString(AppSettingsManager.SETTING_HISTOGRAM).equals("true"))
        //    ShowHistogram(true);

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
            previewHandler.Init();
            previewHandler.SetAppSettingsAndTouch(appSettingsManager);

            Log.d(TAG, "create cameraWrapper");
            cameraUiWrapper = apiHandler.getCameraUiWrapper(this, previewHandler, appSettingsManager, this, cameraUiWrapper);
            cameraUiWrapper.SetCameraChangedListner(this);
            cameraUiWrapper.moduleHandler.SetWorkListner(workHandler);
            cameraUiWrapper.moduleHandler.SetWorkListner(orientationHandler);
            Log.d(TAG, "created cameraWrapper");


            Log.d(TAG, "InitUiStuff");
            initCameraUIStuff(cameraUiWrapper);


            //orientationHandler = new OrientationHandler(this, cameraUiWrapper);
            Log.d(TAG, "add events");
            cameraUiWrapper.moduleHandler.moduleEventHandler.AddWorkFinishedListner(thumbnailHandler);
            if (previewHandler.surfaceView != null && previewHandler.surfaceView instanceof ExtendedSurfaceView && appSettingsManager.getCamApi().equals(AppSettingsManager.API_1)) {
                ExtendedSurfaceView extendedSurfaceView = (ExtendedSurfaceView) previewHandler.surfaceView;
                cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(extendedSurfaceView);
                cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(extendedSurfaceView);
            }


            cameraUiWrapper.moduleHandler.moduleEventHandler.AddRecoderChangedListner(timerHandler);
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(timerHandler);
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(themeHandler);
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
            loadingWrapper = false;
            Log.d(TAG, "loaded cameraWrapper");
        }
        //cameraUiWrapper.StartCamera();
    }

    private void destroyCameraUiWrapper()
    {
        themeHandler.SetCameraUIWrapper(null);
        if (histogramFragment != null)
        {
            histogramFragment.stopLsn();
            histogramFragment.SetCameraUIWrapper(null);
        }
        if (cameraUiWrapper != null)
        {
            Log.d(TAG, "Destroying Wrapper");
            cameraUiWrapper.camParametersHandler.ParametersEventHandler.CLEAR();
            cameraUiWrapper.camParametersHandler.ParametersEventHandler = null;
            cameraUiWrapper.moduleHandler.moduleEventHandler.CLEAR();
            cameraUiWrapper.moduleHandler.moduleEventHandler = null;
            cameraUiWrapper.moduleHandler.SetWorkListner(null);
            cameraUiWrapper.StopPreview();
            cameraUiWrapper.StopCamera();


            cameraUiWrapper = null;
            Log.d(TAG, "destroyed cameraWrapper");

        }
    }


    private void initCameraUIStuff(AbstractCameraUiWrapper cameraUiWrapper)
    {

        themeHandler.SetCameraUIWrapper(cameraUiWrapper);
        themeHandler.GetThemeFragment();


        hardwareKeyHandler.SetCameraUIWrapper(cameraUiWrapper);



        guideHandler.setCameraUiWrapper(cameraUiWrapper, this);
        guideHandler.SetViewG(appSettingsManager.getString(AppSettingsManager.SETTING_GUIDE));
        infoOverlayHandler.setCameraUIWrapper(cameraUiWrapper);
        workHandler.HideSpinner();
        if (histogramFragment != null && histogramFragment.isAdded())
        {
            histogramFragment.SetCameraUIWrapper(cameraUiWrapper);
            cameraUiWrapper.moduleHandler.SetWorkListner(histogramFragment);
        }
    }



    /*private void CoverCamUI(Boolean shown)
    {
        ImageView tmp = (ImageView)findViewById(R.id.SettingsCover);

        if(shown)
        {
            LockWidgets(false);
            String theme = appSettingsManager.GetTheme();
            tmp.setVisibility(View.VISIBLE);

            switch (theme) {
                case "Minimal":
                        tmp.setImageDrawable(null);
                        //tmp.setImageBitmap(null);
                        tmp.setBackgroundColor(Color.argb(200,20,20,20));
                    break;
                case "Classic":
                    tmp.setImageDrawable(null);
                    tmp.setBackgroundColor(Color.TRANSPARENT);
                case "Nubia":
                    tmp.setImageDrawable(null);
                    tmp.setBackgroundColor(Color.argb(200,90,90,90));
                    break;
                case "Material":
                    tmp.setImageDrawable(null);
                    tmp.setBackgroundColor(Color.argb(230,50,50,50));


                    break;
                case "Ambient":
                    tmp.setBackgroundColor(Color.TRANSPARENT);
                    tmp.setImageBitmap(previewHandler.AmbientCover);


                    break;
            }

        }
        else
        {
            tmp.setVisibility(View.GONE);
            LockWidgets(true);
        }
    }*/
    @Override
    public void MenuActive(boolean status)
    {
       /* ImageView shutter_key = (ImageView)findViewById(R.id.SettingsCover);
        ImageView cam_switch = (ImageView)findViewById(R.id.SettingsCover);
        ImageView modes_switch = (ImageView)findViewById(R.id.SettingsCover);
        ImageView flash_switch = (ImageView)findViewById(R.id.SettingsCover);
        ImageView night_switch = (ImageView)findViewById(R.id.SettingsCover);
        ImageView exposure_lock = (ImageView)findViewById(R.id.SettingsCover);
        ImageView exit_key = (ImageView)findViewById(R.id.SettingsCover);

        shutter_key.setEnabled(status);
        cam_switch.setEnabled(status);
        modes_switch.setEnabled(status);
        flash_switch.setEnabled(status);
        night_switch.setEnabled(status);
        exposure_lock.setEnabled(status);
        exit_key.setEnabled(status);*/


        if (status)
        {

            ll.setVisibility(View.INVISIBLE);
        }
        else
        {
            ll.setVisibility(View.VISIBLE);
        }
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
        if(cameraUiWrapper == null)
            loadCameraUiWrapper();
        orientationHandler.Start();
        infoOverlayHandler.StartUpdating();


        //sensorsUtil.start();


        Log.d(TAGLIFE, "Activity onResume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        messageHandler.close();
        infoOverlayHandler.StopUpdating();
        orientationHandler.Stop();

      //  sensorsUtil.stop();
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
        if (cameraUiWrapper != null && cameraUiWrapper.cameraHolder != null && cameraUiWrapper.camParametersHandler != null)
            cameraUiWrapper.camParametersHandler.SetPictureOrientation(orientation);
        return orientation;
    }

    @Override
    public void OnError(final String error)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageHandler.ShowMessage(error);
            }
        });

    }

    public void ActivateSonyApi(String value)
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
        return previewHandler.surfaceView;
    }

    @Override
    public int GetPreviewWidth() {
        return previewHandler.getPreviewWidth();
    }

    @Override
    public int GetPreviewHeight() {
        return previewHandler.getPreviewHeight();
    }

    @Override
    public int GetPreviewLeftMargine() {
        return previewHandler.getMargineLeft();
    }

    @Override
    public int GetPreviewRightMargine() {
        return previewHandler.getMargineRight();
    }

    @Override
    public int GetPreviewTopMargine() {
        return previewHandler.getMargineTop();
    }

    @Override
    public void SetPreviewSizeChangedListner(I_PreviewSizeEvent event) {
        previewHandler.setPreviewSizeEventListner(event);
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
            transaction.remove(histogramFragment);
            transaction.commit();
        }
        if (enable && histogramFragmentOpen)
        {
            if (cameraUiWrapper != null)
            {
                histogramFragment.SetCameraUIWrapper(cameraUiWrapper);
                cameraUiWrapper.moduleHandler.SetWorkListner(histogramFragment);
                if (cameraUiWrapper.cameraHolder.isPreviewRunning)
                    histogramFragment.strtLsn();
            }
        }


    }

    @Override
    public Context GetActivityContext() {
        return getApplicationContext();
    }

    @Override
    public void onCameraOpen(String message)
    {
        try {
            if (cameraUiWrapper instanceof CameraUiWrapperSony)
            {
                messageHandler.ShowMessage("Searching RemoteDevice");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        //else
            //progress = ProgressDialog.show(this,"", "Loading", true);
    }

    @Override
    public void onCameraOpenFinish(String message)
    {
        if (cameraUiWrapper instanceof CameraUiWrapperSony)
        {
            messageHandler.ShowMessage("Found RemoteDevice");
        }
    }

    @Override
    public void onCameraClose(String message) {

    }

    @Override
    public void onPreviewOpen(String message) {

    }

    @Override
    public void onPreviewClose(String message) {

    }

    @Override
    public void onCameraError(String error)
    {
        if (cameraUiWrapper instanceof CameraUiWrapperSony)
        {

            appSettingsManager.setCamApi(AppSettingsManager.API_1);
            loadCameraUiWrapper();
        }
        messageHandler.ShowMessage(error);
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
