package freed.cam.ui.handler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.TextView;

import com.troop.freedcam.R.id;

import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.modules.I_RecorderStateChanged;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;

/**
 * Created by troop on 26.11.2014.
 */
public class TimerHandler implements I_RecorderStateChanged
{
    private final TextView timerText;

    private final ActivityFreeDcamMain activityFreeDcamMain;
    private final MyTimer timer;

    public TimerHandler(ActivityFreeDcamMain activityFreeDcamMain)
    {
        this.activityFreeDcamMain = activityFreeDcamMain;
        timerText = (TextView) activityFreeDcamMain.findViewById(id.textView_RecCounter);
        timer = new MyTimer(timerText);
        activityFreeDcamMain.getContext().registerReceiver(new ModuleChangedReciever(), new IntentFilter("troop.com.freedcam.MODULE_CHANGED"));
        timerText.setVisibility(View.GONE);
    }

    private class ModuleChangedReciever extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String module = intent.getStringExtra("INTENT_EXTRA_MODULENAME");
            if (module.equals(KEYS.MODULE_VIDEO))
                timerText.setVisibility(View.VISIBLE);
            else
                timerText.setVisibility(View.GONE);
        }
    }


    @Override
    public void RecordingStateChanged(int status)
    {
        switch (status) {
            case I_RecorderStateChanged.STATUS_RECORDING_STOP:
                timer.Stop();
                break;
            case I_RecorderStateChanged.STATUS_RECORDING_START :
                timer.Start();
                break;
            default:
                timer.Stop();
                break;
        }

    }
}
