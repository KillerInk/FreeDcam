package freed.cam.ui.handler;

import android.view.View;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;

import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.modules.I_RecorderStateChanged;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;

/**
 * Created by troop on 26.11.2014.
 */
public class TimerHandler implements ModuleChangedEvent, I_RecorderStateChanged
{
    private final TextView timerText;

    private final ActivityFreeDcamMain activityFreeDcamMain;
    private final MyTimer timer;

    public TimerHandler(ActivityFreeDcamMain activityFreeDcamMain)
    {
        this.activityFreeDcamMain = activityFreeDcamMain;
        timerText = (TextView) activityFreeDcamMain.findViewById(id.textView_RecCounter);
        timer = new MyTimer(timerText);
        timerText.setVisibility(View.GONE);
    }

    @Override
    public void onModuleChanged(final String module)
    {
        activityFreeDcamMain.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (module.equals(activityFreeDcamMain.getAppSettings().getResString(R.string.module_video)))
                    timerText.setVisibility(View.VISIBLE);
                else
                    timerText.setVisibility(View.GONE);
            }
        });
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
