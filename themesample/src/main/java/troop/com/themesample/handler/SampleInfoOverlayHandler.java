package troop.com.themesample.handler;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.troop.freedcam.ui.AbstractInfoOverlayHandler;
import com.troop.freedcam.ui.AppSettingsManager;

import troop.com.themesample.R;

/**
 * Created by troop on 14.06.2015.
 */
public class SampleInfoOverlayHandler extends AbstractInfoOverlayHandler
{
    TextView tbattery;
    TextView tsize;
    TextView tformat;
    TextView tTime;
    TextView tStorage;
    public SampleInfoOverlayHandler(View view, AppSettingsManager appSettingsManager)
    {
        super(view.getContext(), appSettingsManager);
        tbattery = (TextView)view.findViewById(R.id.textView_battery);
        tsize = (TextView)view.findViewById(R.id.textView_size);
        tformat = (TextView)view.findViewById(R.id.textView_format);
        tTime = (TextView)view.findViewById(R.id.textView_time);
        tStorage = (TextView)view.findViewById(R.id.textView_storage);
    }

    @Override
    protected void UpdateViews() {
        tbattery.setText(batteryLevel);
        tsize.setText(size);
        tTime.setText(timeString);
        tformat.setText(format);
        tStorage.setText(storageSpace);
    }
}
