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
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.troop.freedcam.databinding.CamerauiManualbuttonBinding;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterInterface;


/**
 * Created by troop on 08.12.2015.
 */
public class ManualButton extends LinearLayout
{
    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onViewStateChanged(ValueChangedEvent<AbstractParameter.ViewState> viewStateValueChangedEvent)
    {
        if (viewStateValueChangedEvent.type != AbstractParameter.ViewState.class)
            return;
        if (viewStateValueChangedEvent.key == parameter.getKey())
        {
            AbstractParameter.ViewState state = viewStateValueChangedEvent.newValue;
            applyViewState(state);
        }
    }*/

    /*private void applyViewState(AbstractParameter.ViewState state) {
        Log.d(TAG, "applyViewState for " +parameter.getKey().toString() + " " + state.toString() );
        switch (state)
        {
            case Enabled:
                ManualButton.this.setVisibility(VISIBLE);
                ManualButton.this.setEnabled(true);
                if (imageView != null)
                    imageView.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
                break;
            case Disabled:
                ManualButton.this.setVisibility(VISIBLE);
                ManualButton.this.setEnabled(false);
                if (imageView != null)
                    imageView.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
                break;
            case Visible:
                ManualButton.this.setVisibility(View.VISIBLE);
                ManualButton.this.setEnabled(true);
                if (imageView != null)
                    imageView.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
                break;
            case Hidden:
                ManualButton.this.setVisibility(View.GONE);
                break;
        }
    }*/

    /*@Subscribe(threadMode = ThreadMode.MAIN)
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
    }*/

    /*@Subscribe(threadMode = ThreadMode.MAIN)
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
    }*/

    private final String TAG = ManualButton.class.getSimpleName();
    private String[] parameterValues;
    protected ParameterInterface parameter;
    private final int backgroundColorActive = Color.parseColor("#46FFFFFF");
    private final int backgroundColor = Color.parseColor("#00000000");
    private int pos;
    protected CamerauiManualbuttonBinding binding;

    public ManualButton(Context context, ParameterInterface parameter, int drawableImg)
    {
        super(context);
        init(context);
        SetManualParameter(parameter);
        binding.imageViewManualButton.setBackgroundResource(drawableImg);
    }

    private void init(Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = CamerauiManualbuttonBinding.inflate(inflater,this,true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //EventBusHelper.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //EventBusHelper.unregister(this);
    }


    public void SetManualParameter(ParameterInterface parameter)
    {
        this.parameter = parameter;
        binding.setParameter((AbstractParameter) parameter);
        if (parameter != null) {
            parameterValues = parameter.getStringValues();
        }
    }

    /*private String getStringValue(int pos)
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
    }*/

    public String[] getStringValues()
    {
        if (parameterValues == null || parameterValues.length ==0)
            parameterValues = parameter.getStringValues();
        return parameterValues;
    }

    public int getCurrentItem()
    {
        return parameter.getIntValue();
    }

    public void setValueToParameters(final int value)
    {
        parameter.setIntValue(value, true);

    }

    public void SetActive(boolean active) {
        if (active) {
            setBackgroundColor(backgroundColorActive);
        } else {
            setBackgroundColor(backgroundColor);
        }
    }

}
