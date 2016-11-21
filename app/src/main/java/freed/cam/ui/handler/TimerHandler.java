package freed.cam.ui.handler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;

import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.modules.CaptureStates;

/**
 * Created by troop on 26.11.2014.
 */
public class TimerHandler
{
    private TextView timerText;
    private MyTimer timer;

    public TimerHandler(ActivityFreeDcamMain activityFreeDcamMain)
    {
        timerText = (TextView) activityFreeDcamMain.findViewById(id.textView_RecCounter);
        timer = new MyTimer(timerText);
        activityFreeDcamMain.getContext().registerReceiver(new ModuleChangedReciever(), new IntentFilter(activityFreeDcamMain.getResources().getString(R.string.INTENT_MODULECHANGED)));
        activityFreeDcamMain.getContext().registerReceiver(new CaptureStateChangedReceiver(), new IntentFilter(activityFreeDcamMain.getResources().getString(R.string.INTENT_CAPTURESTATE)));
        timerText.setVisibility(View.GONE);
    }

    private class ModuleChangedReciever extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(timerText == null)
                return;
            String module = intent.getStringExtra(timerText.getResources().getString(R.string.INTENT_EXTRA_MODULECHANGED));
            if (module.equals(KEYS.MODULE_VIDEO))
                timerText.setVisibility(View.VISIBLE);
            else
                timerText.setVisibility(View.GONE);
        }
    }

    private class CaptureStateChangedReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (timerText == null)
                return;
            int status = intent.getIntExtra(timerText.getContext().getString(R.string.INTENT_EXTRA_CAPTURESTATE),2);
            switch (status) {
                case CaptureStates.RECORDING_STOP:
                    timer.Stop();
                    timerText.setVisibility(View.GONE);
                    break;
                case CaptureStates.RECORDING_START:
                    timerText.setVisibility(View.VISIBLE);
                    timer.Start();
                    break;
                default:
                    timer.Stop();
                    timerText.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
