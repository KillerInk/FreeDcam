/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.ui.themesample.handler;

import android.view.View;
import android.widget.TextView;

import com.troop.freedcam.R.id;

/**
 * Created by troop on 14.06.2015.
 */
public class SampleInfoOverlayHandler extends AbstractInfoOverlayHandler
{
    private final TextView tbattery;
    private final TextView tsize;
    private final TextView tTime;
    private final TextView tStorage;
    TextView tappversion;
    public SampleInfoOverlayHandler(View view)
    {
        super(view.getContext());
        tbattery = (TextView)view.findViewById(id.textView_battery);
        tsize = (TextView)view.findViewById(id.textView_size);
        TextView tformat = (TextView) view.findViewById(id.textView_format);
        tTime = (TextView)view.findViewById(id.textView_time);
        tStorage = (TextView)view.findViewById(id.textView_storage);
        TextView tdngsupported = (TextView) view.findViewById(id.textView_dngsupported);
        tdngsupported.setVisibility(View.GONE);
        /* if (AppSettingsManager.getInstance().getCamApi().equals(AppSettingsManager.API_1))
        {
            tdngsupported.setText("DNG:" + DeviceUtils.isCamera1DNGSupportedDevice());
            if (DeviceUtils.isCamera1DNGSupportedDevice())
                tdngsupported.setTextColor(Color.GREEN);
            else
                tdngsupported.setTextColor(Color.RED);
        }*/
        //else tdngsupported.setVisibility(View.GONE);
        TextView tbuidlmodel = (TextView) view.findViewById(id.textView_buildmodel);
        tbuidlmodel.setVisibility(View.GONE);
        /*tbuidlmodel.setText(Build.MODEL);
        tappversion = (TextView)view.findViewById(R.id.textView_appversion);
        try {
            tappversion.setText(AppSettingsManager.getInstance().context.getPackageManager()
                    .getPackageInfo(AppSettingsManager.getInstance().context.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.exception(e);
        }*/
    }

    @Override
    protected void UpdateViews() {
        tbattery.setText(batteryLevel);
        tsize.setText(size);
        tTime.setText(timeString);
        //tformat.setText(format);
        tStorage.setText(storageSpace);
        /*if (1 > 2)
        {
            if (((CameraUiWrapper) cameraUiWrapper).cameraHolder == null || ((CameraUiWrapper) cameraUiWrapper).cameraHolder.characteristics == null)
                return;
            int devlvl = ((CameraUiWrapper) cameraUiWrapper).cameraHolder.characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
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
