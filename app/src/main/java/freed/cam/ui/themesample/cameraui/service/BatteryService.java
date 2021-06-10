package freed.cam.ui.themesample.cameraui.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import freed.utils.Log;

public class BatteryService {

    private Context context;
    private String batteryLevel;
    private BatteryEvent batteryEventListner;

    public BatteryService(Context context)
    {
        this.context = context;
    }

    public void setBatteryEventListner(BatteryEvent batteryEventListner)
    {
        this.batteryEventListner = batteryEventListner;
    }

    public String getBatteryLevel()
    {
        return batteryLevel;
    }

    public void startListen()
    {
        context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void stopListen()
    {
        try {
            context.unregisterReceiver(receiver);
        }
        catch (IllegalArgumentException ex) {
            Log.WriteEx(ex);
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)+"%";
            if (batteryEventListner != null)
                batteryEventListner.onBatteryChanged(batteryLevel);
        }
    };

    public interface BatteryEvent
    {
        void onBatteryChanged(String batterylvl);
    }
}
