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

import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.utils.Log;

/**
 * Created by GeorgeKiarie on 10/4/2015.
 */
public class SettingsChildMenuInterval extends SettingsChildMenu
{
    private final String TAG = SettingsChildMenuInterval.class.getSimpleName();
    public SettingsChildMenuInterval(Context context, AbstractParameter parameter, int headerid, int descriptionid) {
        super(context, parameter, headerid, descriptionid);
    }

    @Override
    public void SetParameter(AbstractParameter parameter) {
        super.SetParameter(parameter);
        parameter.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.stringValue)
                    Log.d(TAG,"BR.stringValue changed");
            }
        });
    }

    @Override
    public String[] GetValues() {
       return parameter.getStringValues();
    }

    @Override
    public void SetValue(String value)
    {
        //onStringValueChanged(value);
        parameter.setStringValue(value,true);
    }
}