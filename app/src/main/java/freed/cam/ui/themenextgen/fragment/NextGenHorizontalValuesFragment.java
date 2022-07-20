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

package freed.cam.ui.themenextgen.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.cam.ui.themenextgen.view.NextGenSimpleValueChild;
import freed.cam.ui.themesample.SettingsChildAbstract;


/**
 * Created by troop on 16.06.2015.
 */
public class NextGenHorizontalValuesFragment extends Fragment
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
        View view = inflater.inflate(layout.nextgen_cameraui_horizontal_values_fragment, container, false);
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
            NextGenSimpleValueChild child = new NextGenSimpleValueChild(getContext());
            child.SetString(s);
            child.setOnClickListener(onChildClick);
            valuesHolder.addView(child);
        }
    }



    private View.OnClickListener onChildClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (rdytoclose != null)
                rdytoclose.onCloseClicked(((NextGenSimpleValueChild)v).getText());
        }
    };
}
