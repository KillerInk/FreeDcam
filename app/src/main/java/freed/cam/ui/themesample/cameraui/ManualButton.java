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

import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;


import com.troop.freedcam.databinding.CamerauiManualbuttonBinding;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.ui.themenextgen.view.button.ManualButtonInterface;


/**
 * Created by troop on 08.12.2015.
 */
public class ManualButton extends LinearLayout implements ManualButtonInterface
{


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
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AbstractParameter p = (AbstractParameter) parameter;
        p.removeOnPropertyChangedCallback(selectedParameterObserver);
    }


    public void SetManualParameter(ParameterInterface parameter)
    {
        this.parameter = parameter;
        AbstractParameter p = (AbstractParameter) parameter;
        p.addOnPropertyChangedCallback(selectedParameterObserver);
        binding.setParameter((AbstractParameter) parameter);
        if (parameter != null) {
            parameterValues = parameter.getStringValues();
        }
    }

    Observable.OnPropertyChangedCallback selectedParameterObserver = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (propertyId == BR.stringValues)
                parameterValues =(((AbstractParameter)sender).getStringValues());
        }
    };

    @Override
    public String[] getStringValues()
    {
        if (parameterValues == null || parameterValues.length ==0)
            parameterValues = parameter.getStringValues();
        return parameterValues;
    }

    @Override
    public int getCurrentItem()
    {
        return parameter.getIntValue();
    }

    @Override
    public void setValueToParameters(final int value)
    {
        parameter.setIntValue(value, true);
    }

    @Override
    public void SetActive(boolean active) {
        if (active) {
            setBackgroundColor(backgroundColorActive);
        } else {
            setBackgroundColor(backgroundColor);
        }
    }

    @Override
    public AbstractParameter getParameter() {
        return (AbstractParameter) parameter;
    }

}
