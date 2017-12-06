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

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.settings.AppSettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 08.12.2015.
 */
public class ManualButton extends LinearLayout implements ParameterEvents
{

    private class UiHandler extends Handler
    {
        private final int ONISSUPPRTEDCHANGED = 0;
        private final int ON_IS_SET_SUPPORTED_CHANGED = 1;
        private final int ON_STRING_VALUE_CHANGED = 2;
        private final int ON_INT_VALUE_CHANGED = 3;
        private final int ON_UPDATE_SETTING = 4;

        public UiHandler()
        {
            super(Looper.getMainLooper());
        }

        public void setON_IS_SUPPORTED_CHANGED(boolean val)
        {
            this.obtainMessage(ONISSUPPRTEDCHANGED,val).sendToTarget();
        }

        public void setON_IS_SET_SUPPORTED_CHANGED(boolean val)
        {
            this.obtainMessage(ON_IS_SET_SUPPORTED_CHANGED,val).sendToTarget();
        }

        public void setON_STRING_VALUE_CHANGED(String val)
        {
            this.obtainMessage(ON_STRING_VALUE_CHANGED,val).sendToTarget();
        }

        public void setON_INT_VALUE_CHANGED(int val)
        {
            this.obtainMessage(ON_STRING_VALUE_CHANGED,val).sendToTarget();
        }

        public void setON_UPDATE_SETTING(int val)
        {
            this.obtainMessage(ON_UPDATE_SETTING,val).sendToTarget();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case ONISSUPPRTEDCHANGED:
                    if ((boolean)msg.obj) {
                        setVisibility(View.VISIBLE);
                        animate().setListener(null).scaleX(1f).setDuration(300);
                    }
                    else
                    {
                        animate().setListener(hideListner).scaleX(0f).setDuration(300);
                    }
                    break;
                case ON_IS_SET_SUPPORTED_CHANGED:
                    if ((boolean)msg.obj) {
                        setEnabled(true);
                        imageView.getDrawable().setColorFilter(Color.TRANSPARENT, Mode.SRC_ATOP);
                    } else {
                        setEnabled(false);
                        imageView.getDrawable().setColorFilter(Color.GRAY, Mode.SRC_ATOP);
                    }
                    break;
                case ON_STRING_VALUE_CHANGED:
                    valueTextView.setText(String.valueOf(msg.obj));
                    break;
                case ON_INT_VALUE_CHANGED:
                    String txt = getStringValue((int)msg.obj);
                    if (txt != null && !TextUtils.isEmpty(txt) && !txt.equals("null"))
                        valueTextView.setText(txt);
                    else
                        valueTextView.setText((int)msg.obj);
                    //Log.d(TAG, "setTextValue:" + valueTextView.getText());
                    break;
                case ON_UPDATE_SETTING:
                    if (settingMode != null)
                        settingMode.set(String.valueOf((int)msg.obj));
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }

        }
    }

    private final String TAG = ManualButton.class.getSimpleName();
    private String[] parameterValues;
    private ParameterInterface parameter;
    private TextView valueTextView;
    private ImageView imageView;
    private final int backgroundColorActive = Color.parseColor("#46FFFFFF");
    private final int backgroundColor = Color.parseColor("#00000000");
    private int pos;
    protected ActivityInterface fragment_activityInterface;
    private AppSettingsManager.SettingMode settingMode;
    private UiHandler handler;

    public ManualButton(Context context, AppSettingsManager.SettingMode settingMode, ParameterInterface parameter, int drawableImg)
    {
        super(context);
        handler = new UiHandler();
        init(context);
        this.settingMode = settingMode;
        SetManualParameter(parameter);
        imageView.setImageDrawable(getResources().getDrawable(drawableImg));
    }

    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layout.cameraui_manualbutton, this);
        valueTextView = (TextView) findViewById(id.manualbutton_valuetext);
        valueTextView.setSelected(true);
        imageView = (ImageView) findViewById(id.imageView_ManualButton);
    }

    public void RemoveParameterListner( ParameterEvents t)
    {
        parameter.removeEventListner(t);
    }

    public void SetParameterListner( ParameterEvents t)
    {
        parameter.addEventListner(t);
    }

    public void SetManualParameter(@Nullable ParameterInterface parameter)
    {
        this.parameter = parameter;
        if (parameter != null) {
            parameter.addEventListner(this);
            if (parameter.IsSupported())
            {
                String txt = parameter.GetStringValue();
                if (valueTextView != null) {
                    if (txt != null && !TextUtils.isEmpty(txt))
                        valueTextView.setText(txt);
                    else
                        valueTextView.setText(parameter.GetValue() + "");
                }

                onIsSupportedChanged(parameter.IsVisible());
                onIsSetSupportedChanged(parameter.IsSetSupported());
                createStringParametersStrings(parameter);

            }
            else
                onIsSupportedChanged(false);
        }
        else
            onIsSupportedChanged(false);

    }

    private void createStringParametersStrings(ParameterInterface parameter) {
        parameterValues = parameter.getStringValues();
    }



    @Override
    public void onIsSupportedChanged(final boolean value) {
        handler.setON_IS_SUPPORTED_CHANGED(value);
    }

    private final AnimatorListener hideListner = new AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    public void onIsSetSupportedChanged(final boolean value) {
       handler.setON_IS_SET_SUPPORTED_CHANGED(value);
    }


    @Override
    public void onIntValueChanged(int current)
    {
        pos = current;
        Log.d(TAG, "onIntValueChanged current:"+current +" pos:" + pos);
        handler.setON_INT_VALUE_CHANGED(current);
    }

    @Override
    public void onValuesChanged(String[] values) {
        parameterValues = values;
    }

    @Override
    public void onStringValueChanged(final String value) {
        handler.setON_STRING_VALUE_CHANGED(value);
    }

    private String getStringValue(int pos)
    {
        if (parameterValues != null && parameterValues.length > 0)
        {
            if (pos >= parameterValues.length)
                return parameterValues[parameterValues.length-1];
            else if (pos < 0)
                return parameterValues[0];
            else
                return parameterValues[pos];
        }

        return null;
    }

    public String[] getStringValues()
    {
        if (parameterValues == null)
            createStringParametersStrings(parameter);
        return parameterValues;
    }

    public int getCurrentItem()
    {
        return parameter.GetValue();
    }

    public void setValueToParameters(final int value)
    {
        parameter.SetValue(value);
        handler.setON_UPDATE_SETTING(value);
    }

    public void SetActive(boolean active) {
        if (active) {
            setBackgroundColor(backgroundColorActive);
        } else {
            setBackgroundColor(backgroundColor);
        }
    }

}
