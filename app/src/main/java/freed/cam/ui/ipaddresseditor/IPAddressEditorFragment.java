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

package freed.cam.ui.ipaddresseditor;

import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

import java.util.HashMap;

import freed.ActivityAbstract;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.SettingMode;
import freed.utils.LocationManager;
import freed.viewer.holder.FileHolder;

/**
 * Created by troop on 15.02.2016.
 */
public class IPAddressEditorFragment extends Fragment {
    final String TAG = IPAddressEditorFragment.class.getSimpleName();

    private EditText editText_ipaddress;
    private EditText editText_ipaddress_port;
    private EditText editText_cropsize;

    private String mIPAddress = "192.168.43.86";//"192.168.43.83";
    private int mPort = 1234;
    private int mCropsize = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(layout.ip_address_editor_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editText_ipaddress = view.findViewById(id.editText_IPAddress);
        editText_ipaddress_port = view.findViewById(id.editText_IPAddress_port);
        editText_cropsize = view.findViewById(id.editText_Cropsize);

        Button button_save = view.findViewById(id.button_Save_profile);
        button_save.setOnClickListener(onSavebuttonClick);
        if (!SettingsManager.getInstance().isInit()){
            SettingsManager.getInstance().init(PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext()), getResources());
        }

        // get previously saved IPPort settings and set it to the GUI
        setGUI();
    }

    private void setGUI() {
        String ip_port = SettingsManager.get(SettingKeys.IP_PORT).get();
        String splitIP_Port[] = ip_port.split(":");
        String cropsize = SettingsManager.get(SettingKeys.mCropsize).get();

        try {
            mIPAddress = splitIP_Port[0];
            mPort = Integer.parseInt(splitIP_Port[1]);
            mCropsize = Integer.parseInt(cropsize);
        }
        catch(Exception e){
            mIPAddress = "192.168.2.100";
            mPort = 5555;
            mCropsize = 100;
            Toast.makeText(getContext(),"Switching back to previous settings", Toast.LENGTH_SHORT).show();
        }

        // Set the text/numbers
        editText_ipaddress.setText(mIPAddress);
        editText_ipaddress_port.setText(String.valueOf(mPort));
        editText_cropsize.setText(String.valueOf(mCropsize));

        SettingsManager.get(SettingKeys.IP_PORT).set(mIPAddress+":" + mPort);
        SettingsManager.get(SettingKeys.mCropsize).set(String.valueOf(mCropsize));
    }

    private void setIPAddressPort(String myIPAddress, int myPort, int myCropsize) {
        mIPAddress = myIPAddress;
        mPort = myPort;
        mCropsize = myCropsize;
        SettingsManager.get(SettingKeys.IP_PORT).set(myIPAddress+":" + myPort);
        SettingsManager.get(SettingKeys.mCropsize).set(String.valueOf(mCropsize));
    }

    private final OnClickListener onSavebuttonClick = new OnClickListener() {
        @Override
        public void onClick(View v)
        {
            mIPAddress = String.valueOf(editText_ipaddress.getText());
            mPort = Integer.parseInt(String.valueOf(editText_ipaddress_port.getText()));
            mCropsize = Integer.parseInt(String.valueOf(editText_cropsize.getText()));
            setIPAddressPort(mIPAddress, mPort, mCropsize);
            Toast.makeText(getContext(),"IP Address Set", Toast.LENGTH_SHORT).show();
        }
    };
}
