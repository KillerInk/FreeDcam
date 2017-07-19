package freed.cam.ui.handler;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.R.id;

import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.modules.I_RecorderStateChanged;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.utils.AppSettingsManager;

/**
 * Created by troop on 26.11.2014.
 */
public class TimerHandler implements ModuleChangedEvent, I_RecorderStateChanged
{
    private final TextView timerText;

    private final AppSettingsManager appSettingsManager;
    private final MyTimer timer;
    private final View view;

    public TimerHandler(TextView view, AppSettingsManager appSettingsManager)
    {
        this.view = view;
        this.appSettingsManager = appSettingsManager;
        timerText = view;
        timer = new MyTimer(timerText);
        timerText.setVisibility(View.GONE);
    }

    @Override
    public void onModuleChanged(final String module)
    {
        timerText.post(new Runnable() {
            @Override
            public void run() {
                if (module.equals(appSettingsManager.getResString(R.string.module_video)))
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
