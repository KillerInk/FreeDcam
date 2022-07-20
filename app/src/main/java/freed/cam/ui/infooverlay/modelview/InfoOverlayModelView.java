package freed.cam.ui.infooverlay.modelview;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

import com.troop.freedcam.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import freed.FreedApplication;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.ui.infooverlay.model.InfoOverlayModel;
import freed.cam.ui.themesample.cameraui.service.BatteryService;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.BackgroundHandlerThread;
import freed.utils.LocationManager;

@HiltViewModel
public class InfoOverlayModelView extends ViewModel implements LifecycleObserver {

    //this holds the format for video or picture
    private String format;
    //this holds the size for video/picture
    String size;

    String storageSpace;
    private DecimalFormat decimalFormat;

    private final String[] units = { "B", "KB", "MB", "GB", "TB" };
    private boolean started;
    private boolean isStopped;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private SettingsManager settingsManager;
    private final BatteryService batteryService;
    private CameraApiManager cameraApiManager;
    private final InfoOverlayModel infoOverlayModel;
    private LocationManager locationManager;
    private final BackgroundHandlerThread backgroundHandlerThread;

    @Inject
    public InfoOverlayModelView(@ApplicationContext Context context)
    {
        this.batteryService = new BatteryService(context);
        setCameraApiManager(cameraApiManager);
        infoOverlayModel = new InfoOverlayModel();
        backgroundHandlerThread = new BackgroundHandlerThread("InfoOverlay");
    }

    public void setCameraApiManager(CameraApiManager cameraApiManager)
    {
        this.cameraApiManager = cameraApiManager;
    }

    public void setSettingsManager(SettingsManager settingsManager)
    {
        this.settingsManager = settingsManager;
    }

    public InfoOverlayModel getInfoOverlayModel() {
        return infoOverlayModel;
    }

    public CameraApiManager getCameraApiManager() {
        return cameraApiManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    BatteryService.BatteryEvent batteryEvent = new BatteryService.BatteryEvent() {
        @Override
        public void onBatteryChanged(String batterylvl) {
            infoOverlayModel.setBatteryLvl(batterylvl);
        }
    };


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void start()
    {
        batteryService.setBatteryEventListner(batteryEvent);
        batteryService.startListen();
        backgroundHandlerThread.create();
        started = true;
        startLooperThread();
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void stop()
    {
        started = false;
        batteryService.stopListen();
        backgroundHandlerThread.destroy();
        batteryService.setBatteryEventListner(null);
    }

    private void startLooperThread()
    {
        if (started)
            backgroundHandlerThread.executeDelayed(runner, 1000);
    }

    private final Runnable runner = new Runnable() {
        @Override
        public void run()
        {
            infoOverlayModel.setTime(dateFormat.format(new Date()));
            if (cameraApiManager.getCamera() != null){
                try {
                    getFormat();
                }
                catch (NullPointerException ex)
                {
                    //Log.WriteEx(ex);
                }
                getStorageSpace();
            }
            else
            {
                format = "";
            }
            infoOverlayModel.setStorageSpace(storageSpace);
            infoOverlayModel.setSize(size);
            infoOverlayModel.setGps(getGps(locationManager));
            infoOverlayModel.setFormat(format);
            startLooperThread();
        }
    };

    private String getGps(LocationManager locationManager)
    {
        if (locationManager.getCurrentLocation() != null)
        {
            return FreedApplication.getStringFromRessources(R.string.font_gps)  +locationManager.getCurrentLocation().getAccuracy();
        }
        else return "";
    }

    private void getFormat()
    {
        if (cameraApiManager.getCamera().getModuleHandler().getCurrentModuleName().equals(FreedApplication.getStringFromRessources(R.string.module_video)))
        {
            ParameterInterface videoprofile = cameraApiManager.getCamera().getParameterHandler().get(SettingKeys.VIDEO_PROFILES);
            if (videoprofile != null)
                size = videoprofile.getStringValue();
            else
                size = "";
        }
        else
        {
            ParameterInterface pictureFormat = cameraApiManager.getCamera().getParameterHandler().get(SettingKeys.PICTURE_FORMAT);
            if (pictureFormat != null)
                format = pictureFormat.getStringValue();
            else
                format = "";

            ParameterInterface pictureSize = cameraApiManager.getCamera().getParameterHandler().get(SettingKeys.PICTURE_SIZE);
            if (pictureSize != null)
                size = pictureSize.getStringValue();
            else
                size = "";
        }
    }

    private void getStorageSpace()
    {
        try
        {
            //defcomg was here 24/01/2015
            if(!cameraApiManager.getCamera().getModuleHandler().getCurrentModuleName().equals(FreedApplication.getStringFromRessources(R.string.module_video)))
                storageSpace = Avail4PIC();
            else
                storageSpace = readableFileSize(SDspace());
        }
        catch (Exception ex)
        {
            storageSpace = "";
        }


    }

    private String readableFileSize(long size) {
        if( size < 524288000 ){ //at least leave 500MB so that OS can work properly.
            //set not enough storage recording state
            cameraApiManager.getCamera().getModuleHandler().SetIsLowStorage(true);
            if( !isStopped ) {
                //low storage reached; automatically stop the video.
                cameraApiManager.getCamera().getModuleHandler().startWork();
                isStopped = true;
            }
        }
        else{
            isStopped = false;
            cameraApiManager.getCamera().getModuleHandler().SetIsLowStorage(false);
        }

        if(size <= 0) return "0";
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return decimalFormat.format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private  String Avail4PIC()
    {
        // double calc;
        long done;
        done = (long) Calc();
        long a = SDspace() / done;
        return  a + " left";
    }
    private double Calc()
    {
        String[] res = settingsManager.get(SettingKeys.PICTURE_SIZE).get().split("x");

        if(settingsManager.get(SettingKeys.PICTURE_FORMAT).get().contains(FreedApplication.getStringFromRessources(R.string.bayer_)))
        {
            if (Build.MANUFACTURER.contains("HTC"))
                return Integer.parseInt(res[0]) * 2 *Integer.parseInt(res[1]) * 16 / 8;
            else
                return Integer.parseInt(res[0]) *Integer.parseInt(res[1]) * 10 / 8;
        }
        else
            return Integer.parseInt(res[0]) *Integer.parseInt(res[1]) * 8 / 8;
    }

    private long SDspace()
    {
        long bytesAvailable = 0;
        if (!settingsManager.GetWriteExternal()) {
            bytesAvailable = Environment.getExternalStorageDirectory().getUsableSpace();
        }
        else
        {
            StatFs stat = new StatFs(System.getenv("SECONDARY_STORAGE"));
            if(Build.VERSION.SDK_INT > 17)
                bytesAvailable = stat.getFreeBytes();
            else
            {
                bytesAvailable = stat.getAvailableBlocks() * stat.getBlockSize();
            }

        }
        return bytesAvailable;
    }
}
