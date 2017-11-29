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

import freed.settings.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 29.08.2015.
 */
public class SettingsChildMenuTimeLapseFrames extends LinearLayout
{
    private final String TAG = SettingsChildMenuTimeLapseFrames.class.getSimpleName();
    private EditText editText;

    private final float min = 0.01f;
    private final float max = 30;
    private float current;
    private final float mover = (float)1/60;
    private final float bigmover = 1;
    private String settingsname;

    public SettingsChildMenuTimeLapseFrames(Context context) {
        super(context);
        init(context);
    }

    public SettingsChildMenuTimeLapseFrames(Context context, AttributeSet attrs)
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
        plus.setClickable(true);
        minus.setClickable(true);

        //this.setClickable(false);

        minus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (current - bigmover >= 1)
                    current -= bigmover;
                else if (current - mover > min)
                    current -= mover;
                setCurrent(current);
            }
        });
        plus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current +mover > 1 && current + bigmover <= max)
                    current += bigmover;
                else if (current + mover <= 1) {
                    current += mover;
                }
                setCurrent(current);

            }
        });
        settingsname = AppSettingsManager.TIMELAPSEFRAME;
        String fps = AppSettingsManager.getInstance().getApiString(settingsname);
        if (fps == null || TextUtils.isEmpty(fps))
            fps = "30";

        Log.d(TAG, "set to " + fps);
        current = Float.parseFloat(fps);
        if (current >= 1)
            editText.setText(current + " fps");
        else
            editText.setText(current * 60 + " fpm");
    }

    private void setCurrent(float current)
    {
        String form = String.format("%.4f", current).replace(",", ".");
        try {

            current = Float.parseFloat(form);
        }
        catch (Exception ex)
        {
            Log.WriteEx(ex);
        }
        AppSettingsManager.getInstance().setApiString(settingsname, current + "");
        if (current >= 1)
            editText.setText(current + " fps");
        else
            editText.setText(current * 60 + " fpm");
    }

}
