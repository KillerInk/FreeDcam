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

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.ui.themesample.SettingsChildAbstract;
import freed.cam.ui.themesample.cameraui.childs.SimpleValueChild;

/**
 * Created by troop on 16.06.2015.
 */
public class HorizontalValuesFragment extends Fragment implements SettingsChildAbstract.CloseChildClick, ParameterEvents
{
    private LinearLayout valuesHolder;
    private String[] values;
    private SettingsChildAbstract.CloseChildClick rdytoclose;

    public void SetStringValues(String[] values, SettingsChildAbstract.CloseChildClick rdytoclose)
    {
        this.values = values;
        this.rdytoclose = rdytoclose;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,null);
        View view = inflater.inflate(layout.cameraui_horizontal_values_fragment, container, false);
        valuesHolder = view.findViewById(id.horizontal_values_holder);
        setValueToView();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

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
            SimpleValueChild child = new SimpleValueChild(getContext());
            child.SetString(s, this);
            valuesHolder.addView(child);
        }
    }

    private LinearLayout getNewLayout()
    {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        valuesHolder.addView(linearLayout);
        return linearLayout;
    }


    /*
    this gets attached to the Simplevalue childes and returns the value from the clicked SimpleValueChild
     */
    @Override
    public void onCloseClicked(String value)
    {
        if (rdytoclose != null)
            rdytoclose.onCloseClicked(value);
    }


    @Override
    public void onViewStateChanged(AbstractParameter.ViewState value) {

    }

    @Override
    public void onIntValueChanged(int current) {

    }

    @Override
    public void onValuesChanged(String[] values) {
        this.values = values;
        setValueToView();
    }

    @Override
    public void onStringValueChanged(String value) {

    }

}
