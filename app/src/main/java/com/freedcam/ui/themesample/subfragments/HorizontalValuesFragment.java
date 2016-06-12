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

package com.freedcam.ui.themesample.subfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.freedcam.apis.basecamera.interfaces.ModeParameterInterface;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter.I_ModeParameterEvent;
import com.freedcam.ui.themesample.subfragments.Interfaces.I_CloseNotice;
import com.freedcam.ui.themesample.views.uichilds.SimpleValueChild;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

/**
 * Created by troop on 16.06.2015.
 */
public class HorizontalValuesFragment extends Fragment implements I_CloseNotice, I_ModeParameterEvent
{
    private View view;
    private LinearLayout valuesHolder;
    private String[] values;
    private I_CloseNotice rdytoclose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,null);
        view = inflater.inflate(layout.horizontal_values_fragment, container, false);
        valuesHolder = (LinearLayout) view.findViewById(id.horizontal_values_holder);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        setValueToView();
    }

    public void Clear()
    {
        if (valuesHolder != null)
            valuesHolder.removeAllViews();
    }

    private void setValueToView() {
        if (values == null)
            return;
        for (String s : values)
        {
            SimpleValueChild child = new SimpleValueChild(view.getContext());
            child.SetString(s, this);
            valuesHolder.addView(child);
        }
    }

    private LinearLayout getNewLayout()
    {
        LinearLayout linearLayout = new LinearLayout(view.getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        valuesHolder.addView(linearLayout);
        return linearLayout;
    }


    public void SetStringValues(String[] values, I_CloseNotice rdytoclose)
    {
        this.values = values;
        this.rdytoclose = rdytoclose;
    }

    public void ListenToParameter(ModeParameterInterface parameter)
    {
        parameter.addEventListner(this);
    }

    /*
    this gets attached to the Simplevalue childes and returns the value from the clicked SimpleValueChild
     */
    @Override
    public void onClose(String value)
    {
        if (rdytoclose != null)
            rdytoclose.onClose(value);
    }

    @Override
    public void onValueChanged(String val) {

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values)
    {
        this.values = values;
        setValueToView();
    }

    @Override
    public void onVisibilityChanged(boolean visible) {

    }
}
