package troop.com.themesample.handler;

import android.view.View;
import android.widget.TextView;

import com.troop.freedcam.ui.AbstractInfoOverlayHandler;

import troop.com.themesample.R;

/**
 * Created by troop on 14.06.2015.
 */
public class SampleInfoOverlayHandler extends AbstractInfoOverlayHandler
{
    private TextView tbattery;
    private TextView tsize;
    private TextView tformat;
    private TextView tTime;
    private TextView tStorage;
    private TextView tdngsupported;
    private TextView tbuidlmodel;
    TextView tappversion;
    public SampleInfoOverlayHandler(View view)
    {
        super(view.getContext());
        tbattery = (TextView)view.findViewById(R.id.textView_battery);
        tsize = (TextView)view.findViewById(R.id.textView_size);
        tformat = (TextView)view.findViewById(R.id.textView_format);
        tTime = (TextView)view.findViewById(R.id.textView_time);
        tStorage = (TextView)view.findViewById(R.id.textView_storage);
        tdngsupported=(TextView)view.findViewById(R.id.textView_dngsupported);
        tdngsupported.setVisibility(View.GONE);
        /* if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_1))
        {
            tdngsupported.setText("DNG:" + DeviceUtils.isCamera1DNGSupportedDevice());
            if (DeviceUtils.isCamera1DNGSupportedDevice())
                tdngsupported.setTextColor(Color.GREEN);
            else
                tdngsupported.setTextColor(Color.RED);
        }*/
        //else tdngsupported.setVisibility(View.GONE);
        tbuidlmodel = (TextView)view.findViewById(R.id.textView_buildmodel);
        tbuidlmodel.setVisibility(View.GONE);
        /*tbuidlmodel.setText(Build.MODEL);
        tappversion = (TextView)view.findViewById(R.id.textView_appversion);
        try {
            tappversion.setText(appSettingsManager.context.getPackageManager()
                    .getPackageInfo(appSettingsManager.context.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Logger.exception(e);
        }*/
    }

    @Override
    protected void UpdateViews() {
        tbattery.setText(batteryLevel);
        tsize.setText(size);
        tTime.setText(timeString);
        tformat.setText(format);
        tStorage.setText(storageSpace);
        /*if (1 > 2)
        {
            if (((CameraUiWrapperApi2) cameraUiWrapper).cameraHolder == null || ((CameraUiWrapperApi2) cameraUiWrapper).cameraHolder.characteristics == null)
                return;
            int devlvl = ((CameraUiWrapperApi2) cameraUiWrapper).cameraHolder.characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            if(devlvl == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                tdngsupported.setText("LEGACY");
            else if(devlvl == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED)
                tdngsupported.setText("LIMITED");
            else if(devlvl == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL)
                tdngsupported.setText("FULL");
            if (tdngsupported.getVisibility() == View.GONE)
                tdngsupported.setVisibility(View.VISIBLE);
        }*/
    }
}
