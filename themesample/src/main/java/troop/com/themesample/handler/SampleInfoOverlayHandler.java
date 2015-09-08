package troop.com.themesample.handler;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.troop.freedcam.ui.AbstractInfoOverlayHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

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
    TextView tdngsupported;
    TextView tbuidlmodel;
    TextView tappversion;
    public SampleInfoOverlayHandler(View view, AppSettingsManager appSettingsManager)
    {
        super(view.getContext(), appSettingsManager);
        tbattery = (TextView)view.findViewById(R.id.textView_battery);
        tsize = (TextView)view.findViewById(R.id.textView_size);
        tformat = (TextView)view.findViewById(R.id.textView_format);
        tTime = (TextView)view.findViewById(R.id.textView_time);
        tStorage = (TextView)view.findViewById(R.id.textView_storage);
        tdngsupported=(TextView)view.findViewById(R.id.textView_dngsupported);
        if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_1))
        {
            tdngsupported.setText("DNG:" + DeviceUtils.isCamera1DNGSupportedDevice());
            if (DeviceUtils.isCamera1DNGSupportedDevice())
                tdngsupported.setTextColor(Color.GREEN);
            else
                tdngsupported.setTextColor(Color.RED);
        }
        else tdngsupported.setVisibility(View.GONE);
        tbuidlmodel = (TextView)view.findViewById(R.id.textView_buildmodel);
        tbuidlmodel.setText(Build.MODEL);
        tappversion = (TextView)view.findViewById(R.id.textView_appversion);
        try {
            tappversion.setText(appSettingsManager.context.getPackageManager()
                    .getPackageInfo(appSettingsManager.context.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
