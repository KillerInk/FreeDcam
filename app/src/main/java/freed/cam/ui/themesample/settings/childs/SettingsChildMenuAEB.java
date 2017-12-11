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

package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.Settings;
import freed.settings.SettingsManager;

/**
 * Created by Ar4eR on 05.02.16.
 */
public class SettingsChildMenuAEB extends LinearLayout {
    private EditText editText;

    private int min = -12;
    private int max = 12;
    private final int step = 1;
    private int current;
    private CameraWrapperInterface cameraUiWrapper;
    private String settingsname;

    public SettingsChildMenuAEB(Context context) {
        super(context);
        init(context);
    }

    public SettingsChildMenuAEB(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    private void init(Context context)
    {
        Context context1 = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layout.settings_expandable_childs_number, this);
        Button plus = (Button) findViewById(id.button_plus);
        Button minus = (Button) findViewById(id.button_minus);
        editText = (EditText) findViewById(id.editText_number);
        /*this.plus.setClickable(true);
        this.minus.setClickable(true);
        this.plus.setEnabled(true);
        this.minus.setEnabled(true);*/

        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (current - step >= min)
                    current -= step;
                setCurrent(current);
            }
        });
        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current + step <= max)
                    current += step;
                setCurrent(current);

            }
        });

    }

    public void SetCameraUIWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        if (cameraUiWrapper == this.cameraUiWrapper)
            return;
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper !=  null && cameraUiWrapper.getParameterHandler() != null && cameraUiWrapper.getParameterHandler().get(Settings.M_ExposureCompensation) != null)
        {
            String[] v = cameraUiWrapper.getParameterHandler().get(Settings.M_ExposureCompensation).getStringValues();
            int le = v.length;
            min = -(le/2);
            max = le/2;
            setCurrent(current);
        }
        //else
            //this.setVisibility(GONE);

    }

    private void setCurrent(int current) {
        String tempcurrent = String.valueOf(current);
        SettingsManager.getInstance().setApiString(settingsname, tempcurrent);
    }

    public void SetStuff(String settingvalue) {

        settingsname = settingvalue;
        String exp="";
        if (SettingsManager.getInstance() != null)
        exp = SettingsManager.getInstance().getApiString(settingsname);
        if (exp == null || TextUtils.isEmpty(exp)) {
            exp = "0";
            current = Integer.parseInt(exp);
            setCurrent(current);
        }
        editText.setText(exp);
        current = Integer.parseInt(exp);
    }
}
