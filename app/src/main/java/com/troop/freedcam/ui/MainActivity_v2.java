package com.troop.freedcam.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.troop.freedcam.ui.handler.HardwareKeyHandler;
import com.troop.freedcam.ui.handler.HelpOverlayHandler;
import com.troop.freedcam.ui.handler.GuideHandler;
import com.troop.freedcam.ui.handler.ShutterHandler;
import com.troop.freedcam.ui.handler.ThumbnailHandler;
import com.troop.freedcam.ui.handler.TimerHandler;
import com.troop.freedcam.ui.handler.WorkHandler;
import com.troop.freedcam.ui.menu.ManualMenuHandler;
import com.troop.freedcam.ui.menu.MenuHandler;
import com.troop.freedcam.ui.switches.CameraSwitchHandler;
import com.troop.freedcam.ui.switches.FlashSwitchHandler;
import com.troop.freedcam.ui.switches.ModuleSwitchHandler;
import com.troop.freedcam.ui.switches.NightModeSwitchHandler;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 18.08.2014.
 */
public class MainActivity_v2 extends MenuVisibilityActivity implements I_error, I_CameraChangedListner
{

    private static String TAG = StringUtils.TAG + MainActivity_v2.class.getSimpleName();
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
    private BroadcastReceiver rec;

    TextView Storage;
    TextView BattL;
    TextView Restext;
    TextView FormatTextL;
    WorkHandler workHandler;

    boolean initDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        hardwareKeyHandler = new HardwareKeyHandler(this);
        manualMenuHandler = new ManualMenuHandler(this, appSettingsManager);
        focusImageHandler = new FocusImageHandler(this);
        exposureLockHandler = new ExposureLockHandler(this, appSettingsManager);

        loadCameraUiWrapper();

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

        Storage = (TextView)findViewById(R.id.txtViewRemainingStorage);
        BattL = (TextView)findViewById(R.id.txtViewBattLevel);

        Restext = (TextView)findViewById(R.id.textViewRes);

        FormatTextL = (TextView)findViewById(R.id.textViewFormat);

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

        rec = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
                int level = -1;
                if(currentLevel >= 0 && scale > 0)
                {
                    level = (currentLevel * 100) / scale;
                }
                BattL.setText(level+"%");
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(rec, batteryLevelFilter);


        Thread t = new Thread()
        {
            @Override
            public  void run() {
                try {
                    while (!isInterrupted()) {


                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                trySet();
                                Restext.setText(appSettingsManager.getString(AppSettingsManager.SETTING_PICTURESIZE));
                                FormatTextL.setText(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT));

                            }
                        });
                    }
                }
                catch (InterruptedException e)
                {

                }
            }
        };
        t.start();

    }

    //defcomg was here this should go into some handler class that handles module change
    public void trySet()
    {
        try {
            Storage.setText(Avail4PIC());
        }
        catch (Exception ex)
        {
            Storage.setText("error");
        }
    }



    private  String Avail4PIC()
    {

        // double calc;
        long done;







        done = (long) Calc();
        long a = SDspace() / done;

        return String.valueOf(a);

    }
    private double Calc()
    {
        double calc;
        String res [] = appSettingsManager.getString(AppSettingsManager.SETTING_PICTURESIZE).split("x");

        if(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals("jpeg"))
            return calc = Integer.parseInt(res[0]) *Integer.parseInt(res[1]) *1.2;
        if(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals("raw"))
            return calc = Integer.parseInt(res[0]) *Integer.parseInt(res[1]) *1.26;
        if(appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals("dng"))
            return calc = Integer.parseInt(res[0]) * 2 *Integer.parseInt(res[1]) *1.2;

        return 1;
    }

    private static long SDspace()
    {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        stat.restat(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
        return bytesAvailable;
    }

    //End defcomg


    private void loadCameraUiWrapper()
    {

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
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        initDone = true;
    }

    @Override
    protected void onDestroy()
    {
        /*Log.d(TAG, "ActivityDestroy, StopCamera");
        cameraUiWrapper.StopCamera();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
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
        return super.onTouchEvent(event);
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
    {   super.OrientationChanged(orientation);

        //cameraUiWrapper.camParametersHandler.SetPictureOrientation(orientation);
        return orientation;
    }

    @Override
    public void OnError(String error)
    {
        Toast toast = Toast.makeText(this, error, Toast.LENGTH_LONG);
        toast.show();
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
                Toast.makeText(this, "Searching RemoteDevice", Toast.LENGTH_SHORT);
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
            Toast.makeText(this, "Found RemoteDevice", Toast.LENGTH_SHORT);
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
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            appSettingsManager.setCamApi(AppSettingsManager.API_1);
            loadCameraUiWrapper();
        }



    }

    @Override
    public void onCameraStatusChanged(String status) {

    }

    @Override
    public void onModuleChanged(I_Module module) {

    }
}
