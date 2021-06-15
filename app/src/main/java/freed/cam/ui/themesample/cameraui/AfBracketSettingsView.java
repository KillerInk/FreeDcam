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

package freed.cam.ui.themesample.cameraui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

/**
 * Created by troop on 09.09.2016.
 */
@AndroidEntryPoint
public class AfBracketSettingsView extends LinearLayout
{
    private TextView textView_max;
    private TextView textView_min;
    private CameraWrapperInterface cameraWrapperInterface;

    @Inject
    SettingsManager settingsManager;

    public AfBracketSettingsView(Context context) {
        super(context);
        init(context);
    }

    public AfBracketSettingsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AfBracketSettingsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.cameraui_afbracketsettings, this);
        Button button_setMax = findViewById(R.id.button_afbracket_fragment_setMax);
        button_setMax.setOnClickListener(onSetMaxClick);
        Button button_setMin = findViewById(R.id.button_afbracket_fragment_setMin);
        button_setMin.setOnClickListener(onSetMinClick);
        textView_max = findViewById(R.id.textView_afBracketFragment_maxvalue);
        textView_min = findViewById(R.id.textView_afBracketFragment_minvalue);
    }

    public void SetCameraWrapper(CameraWrapperInterface cameraWrapperInterface)
    {
        this.cameraWrapperInterface = cameraWrapperInterface;
    }

    View.OnClickListener onSetMaxClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int max = cameraWrapperInterface.getParameterHandler().get(SettingKeys.M_Focus).getIntValue();
            settingsManager.get(SettingKeys.AF_BRACKET_MAX).set(String.valueOf(max));
            textView_max.setText(cameraWrapperInterface.getParameterHandler().get(SettingKeys.M_Focus).getStringValue());
        }
    };

    View.OnClickListener onSetMinClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int min = cameraWrapperInterface.getParameterHandler().get(SettingKeys.M_Focus).getIntValue();
            settingsManager.get(SettingKeys.AF_BRACKET_MIN).set(String.valueOf(min));
            textView_min.setText(cameraWrapperInterface.getParameterHandler().get(SettingKeys.M_Focus).getStringValue());
        }
    };
}
