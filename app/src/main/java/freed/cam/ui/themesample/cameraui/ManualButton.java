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
import android.graphics.PorterDuff;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.events.EventBusHelper;
import freed.cam.events.ValueChangedEvent;
import freed.utils.Log;


/**
 * Created by troop on 08.12.2015.
 */
public class ManualButton extends LinearLayout
{
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewStateChanged(ValueChangedEvent<AbstractParameter.ViewState> viewStateValueChangedEvent)
    {
        if (viewStateValueChangedEvent.type != AbstractParameter.ViewState.class)
            return;
        if (viewStateValueChangedEvent.key == parameter.getKey())
        {
            AbstractParameter.ViewState state = viewStateValueChangedEvent.newValue;
            switch (state)
            {
                case Enabled:
                    if (ManualButton.this.getVisibility() == View.GONE)
                        ManualButton.this.setVisibility(VISIBLE);
                    ManualButton.this.setEnabled(true);
                    if (imageView != null)
                        imageView.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
                    break;
                case Disabled:
                    if (ManualButton.this.getVisibility() == View.GONE)
                        ManualButton.this.setVisibility(VISIBLE);
                    ManualButton.this.setEnabled(false);
                    if (imageView != null)
                        imageView.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                    break;
                case Visible:
                    ManualButton.this.setVisibility(View.VISIBLE);
                    ManualButton.this.setEnabled(true);
                    animate().setListener(null).scaleY(1f).setDuration(300);
                    break;
                case Hidden:
                    animate().setListener(hideListner).scaleY(0f).setDuration(300);
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIntValueChanged(ValueChangedEvent<Integer> current)
    {
        if (current.type != Integer.class)
            return;
        if (current.key == parameter.getKey()) {
            pos = current.newValue;
            Log.d(TAG, "onIntValueChanged current:" + current + " pos:" + pos);
            String txt = getStringValue(current.newValue);
            if (txt != null && !TextUtils.isEmpty(txt) && !txt.equals("null"))
                valueTextView.setText(txt);
            else
                valueTextView.setText(String.valueOf(current.newValue));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onValuesChanged(ValueChangedEvent<String[]> values) {
        if (values.type != String[].class)
            return;
        if (values.key == parameter.getKey()) {
            parameterValues = values.newValue;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStringValueChanged(ValueChangedEvent<String> value) {
        if (value.type != String.class)
            return;
        if (value.key == parameter.getKey())
            valueTextView.setText(value.newValue);
    }

    private final String TAG = ManualButton.class.getSimpleName();
    private String[] parameterValues;
    protected ParameterInterface parameter;
    protected TextView valueTextView;
    private ImageView imageView;
    private final int backgroundColorActive = Color.parseColor("#46FFFFFF");
    private final int backgroundColor = Color.parseColor("#00000000");
    private int pos;
    protected ActivityInterface fragment_activityInterface;

    public ManualButton(Context context, ParameterInterface parameter, int drawableImg)
    {
        super(context);
        init(context);
        SetManualParameter(parameter);
        imageView.setImageDrawable(getResources().getDrawable(drawableImg));
    }

    private void init(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layout.cameraui_manualbutton, this);
        valueTextView = findViewById(id.manualbutton_valuetext);
        valueTextView.setSelected(true);
        imageView = findViewById(id.imageView_ManualButton);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBusHelper.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBusHelper.unregister(this);
    }


    public void SetManualParameter(@Nullable ParameterInterface parameter)
    {
        this.parameter = parameter;
        if (parameter != null) {

            String txt = parameter.GetStringValue();
            if (valueTextView != null) {
                if (txt != null && !TextUtils.isEmpty(txt))
                    valueTextView.setText(txt);
                else
                    valueTextView.setText(parameter.GetValue() + "");
            }
            createStringParametersStrings(parameter);

        }
        //onViewStateChanged(parameter.getViewState());

    }

    private void createStringParametersStrings(ParameterInterface parameter) {
        parameterValues = parameter.getStringValues();
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
        parameter.SetValue(value, true);

    }

    public void SetActive(boolean active) {
        if (active) {
            setBackgroundColor(backgroundColorActive);
        } else {
            setBackgroundColor(backgroundColor);
        }
    }

}
