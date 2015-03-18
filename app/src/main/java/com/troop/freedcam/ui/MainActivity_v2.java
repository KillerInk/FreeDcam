package com.troop.freedcam.ui;


import android.annotation.TargetApi;
import android.content.Context;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;
import com.troop.freedcam.ui.TextureView.PreviewHandler;
import com.troop.freedcam.ui.handler.ApiHandler;
import com.troop.freedcam.ui.handler.ExposureLockHandler;
import com.troop.freedcam.ui.handler.FocusImageHandler;
import com.troop.freedcam.ui.handler.GuideHandler;
import com.troop.freedcam.ui.handler.HardwareKeyHandler;
import com.troop.freedcam.ui.handler.HelpOverlayHandler;
import com.troop.freedcam.ui.handler.InfoOverlayHandler;
import com.troop.freedcam.ui.handler.MessageHandler;
import com.troop.freedcam.ui.handler.ShutterHandler;
import com.troop.freedcam.ui.handler.ThemeHandler;
import com.troop.freedcam.ui.handler.ThumbnailHandler;
import com.troop.freedcam.ui.handler.TimerHandler;
import com.troop.freedcam.ui.handler.WorkHandler;
import com.troop.freedcam.ui.menu.I_orientation;
import com.troop.freedcam.ui.menu.I_swipe;
import com.troop.freedcam.ui.menu.ManualMenuHandler;
import com.troop.freedcam.ui.menu.fragments.ManualMenuFragment;
import com.troop.freedcam.ui.menu.fragments.MenuFragment;
import com.troop.freedcam.ui.menu.OrientationHandler;
import com.troop.freedcam.ui.menu.SwipeMenuListner;
import com.troop.freedcam.ui.menu.fragments.ShutterItemsFragments;
import com.troop.freedcam.ui.switches.CameraSwitchHandler;
import com.troop.freedcam.ui.switches.FlashSwitchHandler;
import com.troop.freedcam.ui.switches.ModuleSwitchHandler;
import com.troop.freedcam.ui.switches.NightModeSwitchHandler;
import com.troop.freedcam.utils.BitmapUtil;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 18.08.2014.
 */
public class MainActivity_v2 extends FragmentActivity implements I_swipe, I_orientation, I_error, I_CameraChangedListner
{
    protected ViewGroup appViewGroup;
    //public LinearLayout settingsLayout;
    boolean settingsLayloutOpen = false;
    MenuFragment menuFragment;
    ManualMenuFragment manualMenuFragment;
    public ShutterItemsFragments shutterItemsFragment;


    boolean manualMenuOpen = false;
    protected boolean helpOverlayOpen = false;

    SwipeMenuListner swipeMenuListner;
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

    FocusImageHandler focusImageHandler;

    MainActivity_v2 activity;
    ApiHandler apiHandler;
    TimerHandler timerHandler;
    PreviewHandler previewHandler;

    //OrientationHandler orientationHandler;
    //HelpOverlayHandler helpOverlayHandler;

    InfoOverlayHandler infoOverlayHandler;
    MessageHandler messageHandler;
    public ThemeHandler themeHandler;


    //bitmaps
    Bitmap AmbientCoverSML;
    Bitmap TMPBMP;
    Bitmap AmbientCover;
    /////////////////
    int a,b = 0;


    WorkHandler workHandler;

    boolean initDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        if (appSettingsManager.getShowHelpOverlay() == false)
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

    private void LoadBitmaps()
    {


       final LinearLayout Def = (LinearLayout)findViewById(R.id.ShutterFragmentDefault);

       final LinearLayout NUB = (LinearLayout)findViewById(R.id.ShutterFragmentDefault);





    }

    private void createUI() {
        swipeMenuListner = new SwipeMenuListner(this);
        orientationHandler = new OrientationHandler(this, this);

        this.activity =this;
        appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this), this);
        themeHandler = new ThemeHandler(this, appSettingsManager);

        previewHandler = (PreviewHandler) findViewById(R.id.CameraPreview);
        previewHandler.appSettingsManager = appSettingsManager;
        timerHandler = new TimerHandler(this);

        //initUI

        thumbnailHandler = new ThumbnailHandler(this);
        apiHandler = new ApiHandler();
        workHandler = new WorkHandler(this);

        hardwareKeyHandler = new HardwareKeyHandler(this, appSettingsManager);

        focusImageHandler = new FocusImageHandler(this);


        infoOverlayHandler= new InfoOverlayHandler(MainActivity_v2.this, appSettingsManager);
        messageHandler = new MessageHandler(this);



        helpOverlayHandler = (HelpOverlayHandler)findViewById(R.id.helpoverlay);
        helpOverlayHandler.appSettingsManager = appSettingsManager;

        guideHandler = (GuideHandler)findViewById(R.id.GuideView);

        timerHandler = new TimerHandler(this);

        themeHandler.GetThemeFragment();
        shutterItemsFragment.SetAppSettings(appSettingsManager);
    }

    private void loadCameraUiWrapper()
    {

        destroyCameraUiWrapper();
        previewHandler.Init();
        previewHandler.SetAppSettingsAndTouch(appSettingsManager, surfaceTouche);

        cameraUiWrapper = apiHandler.getCameraUiWrapper(this,previewHandler, appSettingsManager, this, cameraUiWrapper);
        cameraUiWrapper.SetCameraChangedListner(this);
        cameraUiWrapper.moduleHandler.SetWorkListner(workHandler);

        initCameraUIStuff(cameraUiWrapper);


        //orientationHandler = new OrientationHandler(this, cameraUiWrapper);
        cameraUiWrapper.moduleHandler.moduleEventHandler.AddWorkFinishedListner(thumbnailHandler);
        if (previewHandler.surfaceView != null && previewHandler.surfaceView instanceof ExtendedSurfaceView && appSettingsManager.getCamApi().equals(AppSettingsManager.API_1)) {
            ExtendedSurfaceView extendedSurfaceView = (ExtendedSurfaceView)previewHandler.surfaceView;
            cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(extendedSurfaceView);
            cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(extendedSurfaceView);
        }


        cameraUiWrapper.moduleHandler.moduleEventHandler.AddRecoderChangedListner(timerHandler);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(timerHandler);
        //cameraUiWrapper.StartCamera();
    }

    private void destroyCameraUiWrapper() {
        if (cameraUiWrapper != null)
        {
            Log.d(TAG, "loading new cameraUIWrapper, Destroying Old");
            cameraUiWrapper.camParametersHandler.ParametersEventHandler.CLEAR();
            cameraUiWrapper.camParametersHandler.ParametersEventHandler = null;
            cameraUiWrapper.moduleHandler.moduleEventHandler.CLEAR();
            cameraUiWrapper.moduleHandler.moduleEventHandler = null;
            cameraUiWrapper.moduleHandler.SetWorkListner(null);
            cameraUiWrapper.StopPreview();
            cameraUiWrapper.StopCamera();


            cameraUiWrapper = null;

        }
    }


    private void initCameraUIStuff(AbstractCameraUiWrapper cameraUiWrapper)
    {
        inflateShutterItemFragment();

        if (menuFragment != null && menuFragment.isAdded())
            menuFragment.SetCameraUIWrapper(cameraUiWrapper, previewHandler.surfaceView);
        hardwareKeyHandler.SetCameraUIWrapper(cameraUiWrapper, shutterItemsFragment.shutterHandler);
        if (manualMenuFragment != null && manualMenuFragment.isAdded())
            manualMenuFragment.SetCameraUIWrapper(cameraUiWrapper, appSettingsManager);
        focusImageHandler.SetCamerUIWrapper(cameraUiWrapper, previewHandler);

        guideHandler.setCameraUiWrapper(cameraUiWrapper);
        infoOverlayHandler.setCameraUIWrapper(cameraUiWrapper);
        workHandler.HideSpinner();


        rightFragHandler();
        leftFragHandler();
        setRalphas();
        setLalphas();

    }

    public void setLalphas()
    {
        String theme = appSettingsManager.getString(AppSettingsManager.SETTING_Theme);
        final ImageView tmp = (ImageView)findViewById(R.id.imageViewLeft);
        if (theme.equals("Minimal"))
        {
            if(!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_PICTURE))
            {
                tmp.setAlpha(0.2f);
            }
            else
            {
                tmp.setAlpha(0.8f);
            }


        }
        else if(theme.equals("Nubia"))
        {
            if(!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_PICTURE))
            {
                tmp.setAlpha(0.2f);
            }
            else
            {
                tmp.setAlpha(0.8f);
            }

        }
        else if(theme.equals("Material"))
        {

        }
        else if(theme.equals("Ambient"))
        {
            if(!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_PICTURE))
            {
                tmp.setAlpha(0.2f);
            }
            else
            {
                tmp.setAlpha(1.0f);
            }

        }
    }

    public void setRalphas()
    {
        String theme = appSettingsManager.getString(AppSettingsManager.SETTING_Theme);
        final ImageView tmp = (ImageView)findViewById(R.id.imageViewRight);

        if (theme.equals("Minimal"))
        {
            if(!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_PICTURE))
            {
                tmp.setAlpha(0.2f);
            }
            else
            {
                tmp.setAlpha(0.5f);
            }
        }
        else if(theme.equals("Nubia"))
        {
            if(!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_PICTURE))
            {
                tmp.setAlpha(0.2f);
            }
            else
            {
                tmp.setAlpha(0.3f);
            }

        }
        else if(theme.equals("Material"))
        {


        }
        else if(theme.equals("Ambient"))
        {
            if(!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_PICTURE))
            {
                tmp.setAlpha(0.2f);
            }
            else
            {
                tmp.setAlpha(1.0f);
            }

        }

    }

    public void leftFragHandler()
    {
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        String theme = appSettingsManager.getString(AppSettingsManager.SETTING_Theme);
        final ImageView tmp = (ImageView)findViewById(R.id.imageViewLeft);


        if (theme.equals("Minimal"))
        {

            tmp.setVisibility(View.VISIBLE);

            switch (size.x)
            {
                case 1920:
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(242,1080);
                    tmp.setLayoutParams(params);
                    break;
                case 2560:
                    LinearLayout.LayoutParams paramsx = new LinearLayout.LayoutParams(322,1440);
                    tmp.setLayoutParams(paramsx);
                    break;
                case 1280:
                    LinearLayout.LayoutParams paramsz = new LinearLayout.LayoutParams(162,720);
                    tmp.setLayoutParams(paramsz);
                    break;
            }
            tmp.setImageDrawable(getResources().getDrawable(R.drawable.minimal_ui_left_bg));

            System.out.println("Snoop" +" "+theme);
        }
        else if (theme.equals("Nubia"))
        {

            tmp.setVisibility(View.VISIBLE);

            switch (size.x)
            {
                case 1920:
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(240,1080);
                    tmp.setLayoutParams(params);
                    break;
                case 2560:
                    LinearLayout.LayoutParams paramsx = new LinearLayout.LayoutParams(320,1440);
                    tmp.setLayoutParams(paramsx);
                    break;
                case 1280:
                    LinearLayout.LayoutParams paramsz = new LinearLayout.LayoutParams(160,720);
                    tmp.setLayoutParams(paramsz);
                    break;
            }
            tmp.setImageDrawable(getResources().getDrawable(R.drawable.nubia_ui_left_bg));


            System.out.println("Snoop" +" "+theme);
        }
        else if (theme.equals("Material"))
        {

            tmp.setVisibility(View.VISIBLE);

            switch (size.x)
            {
                case 1920:
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(240,1080);
                    tmp.setLayoutParams(params);
                    break;
                case 2560:
                    LinearLayout.LayoutParams paramsx = new LinearLayout.LayoutParams(320,1440);
                    tmp.setLayoutParams(paramsx);
                    break;
                case 1280:
                    LinearLayout.LayoutParams paramsz = new LinearLayout.LayoutParams(160,720);
                    tmp.setLayoutParams(paramsz);
                    break;
            }
            tmp.setImageDrawable(getResources().getDrawable(R.drawable.nubia_ui_right_bg));

            System.out.println("Snoop" +" "+theme);
        }
        else if (theme.equals("Ambient"))

        {
            tmp.setVisibility(View.VISIBLE);

           // TMPBMP = BitmapUtil.RotateBitmap(BitmapUtil.getWallpaperBitmap(this), -90f, size.x, size.y);

         //   BitmapUtil.initBlur(this,TMPBMP);

        //    AmbientCoverSML = TMPBMP;

         //   BitmapUtil.doGausianBlur(AmbientCoverSML, TMPBMP, 16f);

        //    AmbientCover = BitmapUtil.ScaleUP(AmbientCoverSML,size.x,size.y);

            switch (size.x)
            {
                case 1920:
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(240,1080);
                    tmp.setLayoutParams(params);
                    a=240;
                    b=1080;
                    break;
                case 2560:
                    LinearLayout.LayoutParams paramsx = new LinearLayout.LayoutParams(320,1440);
                    tmp.setLayoutParams(paramsx);
                    a=240;
                    b=1080;
                    break;
                case 1280:
                    LinearLayout.LayoutParams paramsz = new LinearLayout.LayoutParams(160,720);
                    tmp.setLayoutParams(paramsz);
                    a=240;
                    b=1080;
                    break;
            }

            tmp.post(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("Freed ImageView"+AmbientR.getMeasuredWidth() +" "+ AmbientR.getMeasuredHeight());
                    int[] sizess  = {a,b,size.x,size.y};

                    tmp.setImageBitmap(BitmapUtil.CropBitmap(AmbientCover,sizess,true));
                }
            });

        }
        else
        {
            tmp.setVisibility(View.INVISIBLE);

        }
        System.out.println("Snoop" +" "+theme);
    }

    public void rightFragHandler()
    {
        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        String theme = appSettingsManager.getString(AppSettingsManager.SETTING_Theme);
        final ImageView tmp = (ImageView)findViewById(R.id.imageViewRight);


        if (theme.equals("Minimal"))
        {

            tmp.setVisibility(View.VISIBLE);

            switch (size.x)
            {
                case 1920:
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(242,1080);
                    tmp.setLayoutParams(params);
                    break;
                case 2560:
                    LinearLayout.LayoutParams paramsx = new LinearLayout.LayoutParams(322,1440);
                    tmp.setLayoutParams(paramsx);
                    break;
                case 1280:
                    LinearLayout.LayoutParams paramsz = new LinearLayout.LayoutParams(162,720);
                    tmp.setLayoutParams(paramsz);
                    break;
            }
            tmp.setImageDrawable(getResources().getDrawable(R.drawable.minimal_ui_right_bg));

            System.out.println("Snoop" +" "+theme);
        }
        else if (theme.equals("Nubia"))
        {

            tmp.setVisibility(View.VISIBLE);

            switch (size.x)
            {
                case 1920:
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(240,1080);
                    tmp.setLayoutParams(params);
                    break;
                case 2560:
                    LinearLayout.LayoutParams paramsx = new LinearLayout.LayoutParams(320,1440);
                    tmp.setLayoutParams(paramsx);
                    break;
                case 1280:
                    LinearLayout.LayoutParams paramsz = new LinearLayout.LayoutParams(160,720);
                    tmp.setLayoutParams(paramsz);
                    break;
            }
            tmp.setImageDrawable(getResources().getDrawable(R.drawable.nubia_ui_right_bg));

            System.out.println("Snoop" +" "+theme);
        }
        else if (theme.equals("Material"))
        {

            tmp.setVisibility(View.VISIBLE);

            switch (size.x)
            {
                case 1920:
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(240,1080);
                    tmp.setLayoutParams(params);
                    break;
                case 2560:
                    LinearLayout.LayoutParams paramsx = new LinearLayout.LayoutParams(320,1440);
                    tmp.setLayoutParams(paramsx);
                    break;
                case 1280:
                    LinearLayout.LayoutParams paramsz = new LinearLayout.LayoutParams(160,720);
                    tmp.setLayoutParams(paramsz);
                    break;
            }
            tmp.setImageDrawable(getResources().getDrawable(R.drawable.nubia_ui_right_bg));

            System.out.println("Snoop" +" "+theme);
        }
        else if (theme.equals("Ambient"))

        {
            tmp.setVisibility(View.VISIBLE);

            TMPBMP = BitmapUtil.RotateBitmap(BitmapUtil.getWallpaperBitmap(this), -90f, size.x, size.y);

            BitmapUtil.initBlur(this,TMPBMP);

            AmbientCoverSML = TMPBMP;

            BitmapUtil.doGausianBlur(AmbientCoverSML, TMPBMP, 16f);

            AmbientCover = BitmapUtil.ScaleUP(AmbientCoverSML,size.x,size.y);

            tmp.post(new Runnable() {
                @Override
                public void run() {
                    //System.out.println("Freed ImageView"+AmbientR.getMeasuredWidth() +" "+ AmbientR.getMeasuredHeight());
                    int[] sizess  = {242,1080,size.x,size.y};

                    tmp.setImageBitmap(BitmapUtil.CropBitmap(AmbientCover,sizess,false));
                }
            });

        }
        else
        {
            tmp.setVisibility(View.INVISIBLE);

        }
        System.out.println("Snoop" +" "+theme);


    }

    public void inflateShutterItemFragment()
    {
        shutterItemsFragment.SetAppSettings(appSettingsManager);
        shutterItemsFragment.SetCameraUIWrapper(cameraUiWrapper, previewHandler.surfaceView);

        if (!shutterItemsFragment.isAdded())
        {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.layout__cameraControls, shutterItemsFragment, "Controls");
            transaction.commit();
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


        Log.d(TAGLIFE, "Activity onResume");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        messageHandler.close();
        infoOverlayHandler.StopUpdating();
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
        return  swipeMenuListner.onTouchEvent(event);
    }

    View.OnTouchListener surfaceTouche = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {

            if (focusImageHandler != null)
            {
                activity.onTouchEvent(event);
                return focusImageHandler.onTouchEvent(event);
            }
            else
                return activity.onTouchEvent(event);
        }
    };

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
            public void run()
            {
                messageHandler.ShowMessage(error);
            }
        });

    }

    public void ActivateSonyApi(String value)
    {
        loadCameraUiWrapper();
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
    public void onModuleChanged(I_Module module) {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        Log.d(TAG, "conf changed");
        int or =  newConfig.orientation;
        super.onConfigurationChanged(newConfig);
    }



    @Override
    public void doHorizontalSwipe()
    {
        if (swipeMenuListner.startX - swipeMenuListner.currentX < 0)
        {
            if (!settingsLayloutOpen)
            {
                if (menuFragment == null)
                {
                    menuFragment = new MenuFragment();
                }
                menuFragment.SetAppSettings(appSettingsManager);
                menuFragment.SetCameraUIWrapper(cameraUiWrapper, previewHandler.surfaceView);
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
                transaction.add(R.id.v2_settings_menu, menuFragment, "Menu");
                transaction.commit();




                settingsLayloutOpen = true;
            }
        }
        else
        {
            if (settingsLayloutOpen)
            {

                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.remove(menuFragment);
                fragmentTransaction.commit();
                settingsLayloutOpen = false;
            }
        }
    }

    @Override
    public void doVerticalSwipe()
    {
        if (swipeMenuListner.startY  - swipeMenuListner.currentY < 0)
        {
            if (!manualMenuOpen)
            {
                if (manualMenuFragment == null)
                    manualMenuFragment = new ManualMenuFragment();
                manualMenuFragment.SetCameraUIWrapper(cameraUiWrapper, appSettingsManager);
                android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.manualMenuHolder, manualMenuFragment, "ManualMenu");
                transaction.commit();

                manualMenuOpen = true;
            }
        }
        else
        {
            if (manualMenuOpen)
            {
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.remove(manualMenuFragment);
                fragmentTransaction.commit();
                manualMenuOpen = false;
            }
        }
    }

    /*private void rotateViews(int orientation)
    {
        TextView textView = (TextView)seekbarLayout.findViewById(R.id.textView_seekbar);
        textView.setRotation(orientation);
        if (helpOverlayOpen)
        {
            helpOverlayHandler.animate().rotation(orientation).setDuration(animationtime).start();
        }


        rotateSettingsMenu(orientation);
        for (int i = 0; i < manualSettingsLayout.getChildCount(); i++)
        {
            View view =  manualSettingsLayout.getChildAt(i);
            view.animate().rotation(orientation).setDuration(animationtime).start();
        }
    }*/

    /*private void rotateSettingsMenu(int orientation)
    {
        settingsLayoutHolder.animate().rotation(orientation).setDuration(animationtime).start();

        int h = settingsLayout.getLayoutParams().height;
        int w = settingsLayout.getLayoutParams().width;
        ViewGroup.LayoutParams params = settingsLayout.getLayoutParams();
        params.height = w;
        params.width = h;

        settingsLayout.setLayoutParams(params);
    }*/
}
