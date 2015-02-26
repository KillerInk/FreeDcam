package com.troop.freedcam.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.troop.androiddng.MainActivity;
import com.troop.freedcam.R;
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
import com.troop.freedcam.ui.handler.ThumbnailHandler;
import com.troop.freedcam.ui.handler.TimerHandler;
import com.troop.freedcam.ui.handler.WorkHandler;
import com.troop.freedcam.ui.menu.I_orientation;
import com.troop.freedcam.ui.menu.I_swipe;
import com.troop.freedcam.ui.menu.ManualMenuHandler;
import com.troop.freedcam.ui.menu.MenuHandler;
import com.troop.freedcam.ui.menu.OrientationHandler;
import com.troop.freedcam.ui.menu.SwipeMenuListner;
import com.troop.freedcam.ui.switches.CameraSwitchHandler;
import com.troop.freedcam.ui.switches.FlashSwitchHandler;
import com.troop.freedcam.ui.switches.ModuleSwitchHandler;
import com.troop.freedcam.ui.switches.NightModeSwitchHandler;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 18.08.2014.
 */
public class MainActivity_v2 extends Activity implements I_swipe, I_orientation, I_error, I_CameraChangedListner
{
    protected ViewGroup appViewGroup;
    public LinearLayout settingsLayout;
    public LinearLayout settingsLayoutHolder;
    boolean settingsLayloutOpen = false;
    public LinearLayout manualSettingsLayout;
    public LinearLayout seekbarLayout;
    LinearLayout manualMenuHolder;


    boolean manualMenuOpen = false;
    protected boolean helpOverlayOpen = false;

    SwipeMenuListner swipeMenuListner;
    OrientationHandler orientationHandler;
    int flags;
    int flags2;

    protected HelpOverlayHandler helpOverlayHandler;
    protected GuideHandler guideHandler;
    int helplayoutrot;

    private final int animationtime = 300;



    private static String TAG = StringUtils.TAG + MainActivity_v2.class.getSimpleName();
    private static String TAGLIFE = StringUtils.TAG + "LifeCycle";
    //ExtendedSurfaceView cameraPreview;
    AbstractCameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;
    MenuHandler menuHandler;
    public ShutterHandler shutterHandler;
    CameraSwitchHandler cameraSwitchHandler;
    ModuleSwitchHandler moduleSwitchHandler;
    FlashSwitchHandler flashSwitchHandler;
    ThumbnailHandler thumbnailHandler;
    HardwareKeyHandler hardwareKeyHandler;
    public ManualMenuHandler manualMenuHandler;
    FocusImageHandler focusImageHandler;
    TextView exitButton;
    MainActivity_v2 activity;
    ApiHandler apiHandler;
    TimerHandler timerHandler;
    PreviewHandler previewHandler;
    ExposureLockHandler exposureLockHandler;
    //OrientationHandler orientationHandler;
    //HelpOverlayHandler helpOverlayHandler;
    NightModeSwitchHandler nightModeSwitchHandler;
    InfoOverlayHandler infoOverlayHandler;
    MessageHandler messageHandler;



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



        manualMenuHolder = (LinearLayout)findViewById(R.id.manualMenuHolder);
        settingsLayout = (LinearLayout)findViewById(R.id.v2_settings_menu);
        settingsLayoutHolder = (LinearLayout)findViewById(R.id.settings_menuHolder);
        settingsLayout.removeView(settingsLayoutHolder);
        settingsLayloutOpen = false;


        //settingsLayout.setAlpha(0f);
        //settingsLayout.setVisibility(View.GONE);
        manualSettingsLayout = (LinearLayout)findViewById(R.id.v2_manual_menu);
        //manualSettingsLayout.setAlpha(0f);
        //manualSettingsLayout.setVisibility(View.GONE);
        seekbarLayout = (LinearLayout)findViewById(R.id.v2_seekbar_layout);
        manualMenuHolder.removeView(manualSettingsLayout);
        manualMenuHolder.removeView(seekbarLayout);



        swipeMenuListner = new SwipeMenuListner(this);
        orientationHandler = new OrientationHandler(this, this);

        HIDENAVBAR();

        this.activity =this;
        appSettingsManager = new AppSettingsManager(PreferenceManager.getDefaultSharedPreferences(this), this);

        previewHandler = (PreviewHandler) findViewById(R.id.CameraPreview);
        previewHandler.appSettingsManager = appSettingsManager;

        activity = this;
        timerHandler = new TimerHandler(this);

        //initUI
        menuHandler = new MenuHandler(this, appSettingsManager);
        thumbnailHandler = new ThumbnailHandler(this);
        apiHandler = new ApiHandler();
        workHandler = new WorkHandler(this);
        cameraSwitchHandler = new CameraSwitchHandler(this, appSettingsManager);
        shutterHandler = new ShutterHandler(this);
        moduleSwitchHandler = new ModuleSwitchHandler(this, appSettingsManager);
        flashSwitchHandler = new FlashSwitchHandler(this, appSettingsManager);
        nightModeSwitchHandler = new NightModeSwitchHandler(this, appSettingsManager);
        hardwareKeyHandler = new HardwareKeyHandler(this, appSettingsManager);
        manualMenuHandler = new ManualMenuHandler(this, appSettingsManager);
        focusImageHandler = new FocusImageHandler(this);
        exposureLockHandler = new ExposureLockHandler(this, appSettingsManager);

        infoOverlayHandler= new InfoOverlayHandler(MainActivity_v2.this, appSettingsManager);
        messageHandler = new MessageHandler(this);



        exitButton = (TextView)findViewById(R.id.textView_Exit);

        if( ViewConfiguration.get(this).hasPermanentMenuKey())
        {
            exitButton.setVisibility(View.GONE);
        }
        else
        {
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {


                    activity.finish();

                }
            });
        }
        helpOverlayHandler = (HelpOverlayHandler)findViewById(R.id.helpoverlay);
        helpOverlayHandler.appSettingsManager = appSettingsManager;

        guideHandler = (GuideHandler)findViewById(R.id.GuideView);



        timerHandler = new TimerHandler(this);



        if (appSettingsManager.getShowHelpOverlay() == false)
        {
            RelativeLayout view = (RelativeLayout) helpOverlayHandler.getParent();
            view.removeView(helpOverlayHandler);
            helpOverlayOpen = false;
            helpOverlayHandler.setVisibility(View.GONE);
        }
        else
        {
            helpOverlayOpen = true;
        }

        loadCameraUiWrapper();
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
        menuHandler.SetCameraUiWrapper(cameraUiWrapper, previewHandler.surfaceView);
        cameraSwitchHandler.SetCameraUiWrapper(cameraUiWrapper, previewHandler.surfaceView);
        shutterHandler.SetCameraUIWrapper(cameraUiWrapper);
        moduleSwitchHandler.SetCameraUIWrapper(cameraUiWrapper);
        flashSwitchHandler.SetCameraUIWrapper(cameraUiWrapper);
        try {

            nightModeSwitchHandler.SetCameraUIWrapper(cameraUiWrapper);
        }
        catch (Exception ex)
        {

        }
        hardwareKeyHandler.SetCameraUIWrapper(cameraUiWrapper);
        manualMenuHandler.SetCameraUIWrapper(cameraUiWrapper);
        focusImageHandler.SetCamerUIWrapper(cameraUiWrapper, previewHandler.surfaceView);
        exposureLockHandler.SetCameraUIWrapper(cameraUiWrapper);
        guideHandler.setCameraUiWrapper(cameraUiWrapper);
        infoOverlayHandler.setCameraUIWrapper(cameraUiWrapper);
        workHandler.HideSpinner();
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
                public void onSystemUiVisibilityChange(int visibility)
                {
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
        if(cameraUiWrapper == null)
            loadCameraUiWrapper();
        orientationHandler.Start();
        infoOverlayHandler.StartUpdating();
        super.onResume();

        Log.d(TAGLIFE, "Activity onResume");
    }

    @Override
    protected void onPause()
    {
        messageHandler.close();
        infoOverlayHandler.StopUpdating();
        orientationHandler.Stop();
        destroyCameraUiWrapper();
        super.onPause();

        Log.d(TAGLIFE, "Activity onPause");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        Log.d(TAGLIFE,"Focus has changed!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" + hasFocus);
        if (hasFocus)
            HIDENAVBAR();
        //super.onWindowFocusChanged(hasFocus);
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
            activity.onTouchEvent(event);
            return focusImageHandler.onTouchEvent(event);
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
            if (!settingsLayloutOpen) {
                settingsLayout.addView(settingsLayoutHolder);
                settingsLayloutOpen = true;
            }
        }
        else
        {
            if (settingsLayloutOpen) {
                settingsLayout.removeView(settingsLayoutHolder);
                settingsLayloutOpen = false;
            }
        }
    }

    @Override
    public void doVerticalSwipe()
    {
        if (swipeMenuListner.startY  - swipeMenuListner.currentY < 0)
        {
            if (!manualMenuOpen) {
                manualMenuHolder.addView(manualSettingsLayout);
                manualMenuHolder.addView(seekbarLayout);
                manualMenuOpen = true;
            }
        }
        else
        {
            if (manualMenuOpen)
            {
                manualMenuHolder.removeView(manualSettingsLayout);
                manualMenuHolder.removeView(seekbarLayout);
                manualMenuOpen = false;
            }
        }
    }

    private void rotateViews(int orientation)
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
    }

    private void rotateSettingsMenu(int orientation)
    {
        settingsLayoutHolder.animate().rotation(orientation).setDuration(animationtime).start();

        int h = settingsLayout.getLayoutParams().height;
        int w = settingsLayout.getLayoutParams().width;
        ViewGroup.LayoutParams params = settingsLayout.getLayoutParams();
        params.height = w;
        params.width = h;

        settingsLayout.setLayoutParams(params);
    }
}
