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

package freed.cam.ui.themesample.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import freed.cam.ui.themesample.SettingsChildAbstract.CloseChildClick;

/**
 * Created by troop on 15.06.2015.
 */
public class ValuesMenuFragment extends Fragment implements ListView.OnItemClickListener
{
    private String[] item;
    private ListView listView;
    private CloseChildClick _closeChildClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(layout.settings_menuvalues_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(id.values_fragment_listview);

        if(item == null)
            return;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                layout.settings_menuvalues_listviewlayout, id.listviewlayout_textview, item);
        //attach adapter to the listview and fill
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    public void SetMenuItem(String[] item, CloseChildClick _closeChildClick)
    {
        this.item = item;
        this._closeChildClick = _closeChildClick;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        listView.setOnItemClickListener(null);
        String value = (String) listView.getItemAtPosition(position);
        _closeChildClick.onCloseClicked(value);
    }
}
